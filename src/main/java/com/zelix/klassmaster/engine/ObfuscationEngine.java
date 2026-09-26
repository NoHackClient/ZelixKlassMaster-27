package com.zelix.klassmaster.engine;

import com.zelix.klassmaster.archive.ArchiveZipFile;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.changelog.ChangeLogWriter;
import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassMemberRef;
import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ModuleInfoClass;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ResolvedMemberRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRef;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchy;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassPathResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassLoader;
import com.zelix.klassmaster.classfile.hierarchy.CommonSuperTypeResolver;
import com.zelix.klassmaster.classfile.hierarchy.InheritedMemberAnalyzer;
import com.zelix.klassmaster.classfile.hierarchy.MethodOverrideAnalyzer;
import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;
import com.zelix.klassmaster.classfile.insn.LocalVariableIndex;
import com.zelix.klassmaster.classfile.insn.MethodBytecode;
import com.zelix.klassmaster.classfile.insn.MethodFlowAnalyzer;
import com.zelix.klassmaster.config.ChangeLogInputFile;
import com.zelix.klassmaster.config.GroupingsSpec;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.config.MixedCaseNamesMode;
import com.zelix.klassmaster.exceptions.AssertionFailedException;
import com.zelix.klassmaster.exceptions.MethodAnalysisException;
import com.zelix.klassmaster.exceptions.MissingClassException;
import com.zelix.klassmaster.exceptions.StackAnalysisException;
import com.zelix.klassmaster.exceptions.ZkmClassNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.license.LicenseCheckBase;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.obfuscator.constants.IntegerConstantEncryptor;
import com.zelix.klassmaster.obfuscator.constants.IntegerEncryptionExclusions;
import com.zelix.klassmaster.obfuscator.constants.LongConstantEncryptor;
import com.zelix.klassmaster.obfuscator.constants.LongEncryptionExclusionHandler;
import com.zelix.klassmaster.obfuscator.exceptions.ExceptionObfuscationExclusions;
import com.zelix.klassmaster.obfuscator.exclude.AbstractExclusionSpec;
import com.zelix.klassmaster.obfuscator.exclude.ExistingSerializedClassesHandler;
import com.zelix.klassmaster.obfuscator.exclude.FixedClassesExclusionSet;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.obfuscator.flow.FlowFieldPair;
import com.zelix.klassmaster.obfuscator.flow.FlowObfuscationExclusions;
import com.zelix.klassmaster.obfuscator.flow.FlowObfuscationGroup;
import com.zelix.klassmaster.obfuscator.flow.FlowObfuscationManager;
import com.zelix.klassmaster.obfuscator.flow.OpaquePredicateField;
import com.zelix.klassmaster.obfuscator.flow.StaticInitCalleeAnalyzer;
import com.zelix.klassmaster.obfuscator.parameters.ChangedMethodDescriptor;
import com.zelix.klassmaster.obfuscator.parameters.LookupClassOption;
import com.zelix.klassmaster.obfuscator.parameters.MethodKeyInjector;
import com.zelix.klassmaster.obfuscator.parameters.MethodParamChangeNode;
import com.zelix.klassmaster.obfuscator.parameters.MethodParameterChangeSet;
import com.zelix.klassmaster.obfuscator.parameters.MethodParameterChanger;
import com.zelix.klassmaster.obfuscator.parameters.MethodParameterExclusions;
import com.zelix.klassmaster.obfuscator.parameters.MethodParameterObfuscator;
import com.zelix.klassmaster.obfuscator.parameters.ParameterListGenerator;
import com.zelix.klassmaster.obfuscator.references.LookupClassFactory;
import com.zelix.klassmaster.obfuscator.references.ReferenceObfuscationExclusions;
import com.zelix.klassmaster.obfuscator.references.ReferenceObfuscator;
import com.zelix.klassmaster.obfuscator.reflection.AutoReflectionHandler;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionAccessMatcher;
import com.zelix.klassmaster.obfuscator.rename.ClassRenameClashChecker;
import com.zelix.klassmaster.obfuscator.rename.DefaultFieldNameGenerator;
import com.zelix.klassmaster.obfuscator.rename.FieldRenamer;
import com.zelix.klassmaster.obfuscator.rename.FileClassNameGenerator;
import com.zelix.klassmaster.obfuscator.rename.MethodNameAssigner;
import com.zelix.klassmaster.obfuscator.rename.MethodRenamer;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.obfuscator.rename.SharedSequenceClassNamer;
import com.zelix.klassmaster.obfuscator.string.StringEncryptionExclusionSpec;
import com.zelix.klassmaster.obfuscator.string.StringEncryptor;
import com.zelix.klassmaster.obfuscator.trim.ClassMemberSets;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.CountingBag;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.JavaRuntimeVersion;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MultiMapTable;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.NestedMultimapView;
import com.zelix.klassmaster.util.ObjectTriple;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.PairMultiMap;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;
import com.zelix.klassmaster.util.ReadOnlyMultiMapView;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.SetValuedMap;
import com.zelix.klassmaster.util.SyncIndexedSet;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.TwoKeySetMultiMap;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ObfuscationEngine extends ObfuscatorEngineBase {
    public void addChangeLogLineNumberMappings(NestedMultiMap nestedMultiMap, ChangeLogMapping changeLogMapping1) throws IOException {
        Iterator iterator = changeLogMapping1.getNewClassNames().iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            if (!ClassHierarchyNode.isProgramClassName(string)) {
                String string1 = changeLogMapping1.getOriginalClassName(string);
                if (string1 == null) {
                    string1 = string;
                }

                ListMultimap listMultimap = changeLogMapping1.getLineNumberMappings(string1);
                if (listMultimap != null) {
                    nestedMultiMap.putMultimap(string, listMultimap);
                }
            }
        }
    }

    public static List readNameListFile(String string, String string1, ScriptEnvironment scriptEnvironment1) throws ZkmException {
        String string2 = string;
        File file1 = scriptEnvironment1.getDefaultDirectory();
        string2 = ZkmFileUtils.resolveDotRelativePath(ZkmFileUtils.normalizeSeparators(string2), file1);
        File file2;
        if (ZkmFileUtils.isRelativePath(string2)) {
            file2 = new File(file1, string2);
        } else {
            file2 = new File(string2);
        }

        if (file2.exists()) {
            if (!file2.isDirectory()) {
                SyncIndexedSet syncIndexedSet = new SyncIndexedSet();
                BufferedReader bufferedReader = null;

                try {
                    ObservableHolder observableHolder = new ObservableHolder();
                    bufferedReader = ZkmFileUtils.createBomAwareReader(new FileInputStream(file2), "UTF-8", observableHolder);

                    label211:
                    while (true) {
                        String string4;
                        boolean bl;
                        while (true) {
                            if ((string4 = bufferedReader.readLine()) == null) {
                                return new ArrayList(syncIndexedSet);
                            }

                            string4 = string4.trim();
                            if (string4.length() > 0) {
                                if (string4.length() >= 2) {
                                    if (string4.charAt(0) == '/' || string4.charAt(1) == '/') {
                                        continue;
                                    }

                                    bl = HiddenOptionFlags.ALLOW_HASH_NAMES_IN_NAME_FILE;
                                    break;
                                }

                                bl = HiddenOptionFlags.ALLOW_HASH_NAMES_IN_NAME_FILE;
                                break;
                            }
                        }

                        if (bl || string4.charAt(0) != '#') {
                            String string3 = string4;
                            if (!HiddenOptionFlags.DONT_UNESCAPE_NAME_FILE) {
                                string4 = ZkmUtils.unescapeJavaString(string4);
                                bl = HiddenOptionFlags.VALIDATE_NAME_FILE_IDENTIFIERS;
                            } else {
                                bl = HiddenOptionFlags.VALIDATE_NAME_FILE_IDENTIFIERS;
                            }

                            if (bl) {
                                if (!Character.isJavaIdentifierStart(string4.charAt(0))) {
                                    scriptEnvironment1.logWarning(
                                            "Line '"
                                                    + string3
                                                    + "' in file '"
                                                    + file2.getAbsolutePath()
                                                    + "' specified by '"
                                                    + string1
                                                    + "' parameter is not a valid Java identifier and will not be used. (A)"
                                    );
                                    continue;
                                }

                                for (int i = 1; i < string4.length(); i++) {
                                    if (!Character.isJavaIdentifierPart(string4.charAt(i))) {
                                        scriptEnvironment1.logWarning(
                                                "Line '"
                                                        + string3
                                                        + "' in file '"
                                                        + file2.getAbsolutePath()
                                                        + "' specified by '"
                                                        + string1
                                                        + "' parameter is not a valid Java identifier and will not be used. (B)"
                                        );
                                        continue label211;
                                    }
                                }
                            } else {
                                for (int i = 0; i < string4.length(); i++) {
                                    if (Character.isWhitespace(string4.charAt(i))) {
                                        scriptEnvironment1.logWarning(
                                                "Line '"
                                                        + string3
                                                        + "' in file '"
                                                        + file2.getAbsolutePath()
                                                        + "' specified by '"
                                                        + string1
                                                        + "' parameter contains white space at "
                                                        + i
                                                        + ". If you have specified a change log that needs to be parsable then it may cause unexpected problems."
                                        );
                                    }
                                }
                            }

                            syncIndexedSet.add(string4);
                        }
                    }
                } catch (FileNotFoundException fileNotFoundException) {
                    scriptEnvironment1.logFatalError(
                            "Could not read file '"
                                    + file2.getAbsolutePath()
                                    + "' specified by '"
                                    + string1
                                    + "' parameter : '"
                                    + fileNotFoundException.getMessage()
                                    + "'."
                    );
                } catch (IOException iOException1) {
                    scriptEnvironment1.logFatalError(
                            "Error reading file '" + file2.getAbsolutePath() + "' specified by '" + string1 + "' parameter : '" + iOException1.getMessage() + "'."
                    );
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException iOException) {
                        }
                    }
                }

                return new ArrayList(syncIndexedSet);
            }

            scriptEnvironment1.logFatalError("File '" + file2.getAbsolutePath() + "' specified by '" + string1 + "' parameter is a directory.");
        } else {
            scriptEnvironment1.logFatalError("File '" + file2.getAbsolutePath() + "' specified by '" + string1 + "' parameter does not exist.");
        }

        return null;
    }

    public static String xorStrings(String string, String string1) {
        char[] ba = string.toCharArray();
        char[] bb = string1.toCharArray();
        StringBuilder stringBuilder = new StringBuilder(ba.length);

        for (int i = 0; i < ba.length; i++) {
            char bd = ba[i];
            char be;
            if (i < bb.length - 1) {
                be = bb[i];
            } else {
                be = bb[i % bb.length];
            }

            stringBuilder.append((char) ((byte) bd ^ (byte) be));
        }

        return stringBuilder.toString();
    }

    public void removeAllMethodParameters() {
        for (ProgramClass programClass1 : super.programClasses) {
            programClass1.removeMethodParametersAttributes();
            if (programClass1.hasVersionedVariants()) {
                Iterator iterator = programClass1.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ((ProgramClass) ((ClassFileBase) iterator.next())).removeMethodParametersAttributes();
                }
            }
        }
    }

    
    
    public boolean checkLicenseTimestamps(long ba, long bb, Object object) throws ZkmException, IOException {
        if (isPermanentLicense()) {
            return true;
        }
        String string = object.getClass().getName();
        if (HiddenOptionFlags.DEBUG_LICENSE_CHECK) {
        }

        if (ba > bb) {
            if (HiddenOptionFlags.DEBUG_LICENSE_CHECK) {
            }

            return false;
        } else {
            String string1 = xorStrings(LicenseCheckBase.getEncodedEvaluationPeriod(), LicenseCheckBase.getXorKey());
            String string3 = string1;
            String string5 = string3;
            long bc = this.m(string5);
            String string2 = xorStrings(LicenseCheckBase.getEncodedGracePeriod(), LicenseCheckBase.getXorKey());
            String string4 = string2;
            String string6 = string4;
            long bd = this.m(string6);
            if (ba < bb - bc) {
                if (HiddenOptionFlags.DEBUG_LICENSE_CHECK) {
                }

                return false;
            } else {
                long be = ba + bd;
                ClassPathResolver classPathResolver1 = null;
                boolean bl = false ;

                label339:
                {
                    boolean bl2;
                    label338:
                    {
                        label337:
                        {
                            label348:
                            {
                                try {
                                    bl = true;
                                    classPathResolver1 = new ClassPathResolver(System.getProperty("java.class.path"));
                                    Object[] objects = classPathResolver1.getEntries();

                                    for (int i = 0; i < objects.length; i++) {
                                        if (objects[i] instanceof File) {
                                            long bh = ((File) objects[i]).lastModified();
                                            if (bh > be) {
                                                if (HiddenOptionFlags.DEBUG_LICENSE_CHECK) {
                                                }

                                                bl = false;
                                                break label339;
                                            }
                                        } else {
                                            File file1 = new File(((ZipFile) objects[i]).getName());
                                            if (file1.exists()) {
                                                if (file1.lastModified() > be) {
                                                    if (HiddenOptionFlags.DEBUG_LICENSE_CHECK) {
                                                    }

                                                    bl2 = false;
                                                    bl = false;
                                                    break label338;
                                                }

                                                if (file1.getName().endsWith("ZKM.jar")) {
                                                    ArchiveZipFile archiveZipFile = null;
                                                    boolean bl1 = false ;

                                                    label325:
                                                    {
                                                        label324:
                                                        {
                                                            label323:
                                                            {
                                                                try {
                                                                    label321:
                                                                    {
                                                                        long bg;
                                                                        try {
                                                                            bl1 = true;
                                                                            archiveZipFile = new ArchiveZipFile(file1);
                                                                            ZipEntry zipEntry1 = archiveZipFile.getEntry(string.replace('.', '/') + ".class");
                                                                            if (zipEntry1 == null) {
                                                                                bl1 = false;
                                                                                break label323;
                                                                            }

                                                                            bg = zipEntry1.getTime();
                                                                            if (bg > be) {
                                                                                if (HiddenOptionFlags.DEBUG_LICENSE_CHECK) {
                                                                                }

                                                                                bl1 = false;
                                                                                break label325;
                                                                            }
                                                                        } catch (IOException iOException5) {
                                                                            bl1 = false;
                                                                            break label321;
                                                                        }

                                                                        if (bg != -1L) {
                                                                            if (ba > bg + bc) {
                                                                                if (HiddenOptionFlags.DEBUG_LICENSE_CHECK) {
                                                                                }

                                                                                bl1 = false;
                                                                                break label324;
                                                                            }

                                                                            bl1 = false;
                                                                        } else {
                                                                            bl1 = false;
                                                                        }
                                                                        break label323;
                                                                    }
                                                                } finally {
                                                                    if (bl1) {
                                                                        if (archiveZipFile != null) {
                                                                            try {
                                                                                com.zelix.klassmaster.util.ZkmUtils.<java.io.IOException>mayThrow();
                                                                                archiveZipFile.close();
                                                                            } catch (IOException iOException) {
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                try {
                                                                    if (archiveZipFile != null) {
                                                                        try {
                                                                            com.zelix.klassmaster.util.ZkmUtils.<java.io.IOException>mayThrow();
                                                                            archiveZipFile.close();
                                                                        } catch (IOException iOException3) {
                                                                        }
                                                                    }
                                                                    continue;
                                                                } catch (
                                                                        AssertionFailedException assertionFailedException) {
                                                                    throw assertionFailedException;
                                                                }
                                                            }

                                                            try {
                                                                com.zelix.klassmaster.util.ZkmUtils.<java.io.IOException>mayThrow();
                                                                archiveZipFile.close();
                                                            } catch (IOException iOException4) {
                                                            }
                                                            continue;
                                                        }

                                                        try {
                                                            com.zelix.klassmaster.util.ZkmUtils.<java.io.IOException>mayThrow();
                                                            archiveZipFile.close();
                                                            bl = false;
                                                        } catch (IOException iOException2) {
                                                            bl = false;
                                                        }
                                                        break label348;
                                                    }

                                                    try {
                                                        com.zelix.klassmaster.util.ZkmUtils.<java.io.IOException>mayThrow();
                                                        archiveZipFile.close();
                                                        bl = false;
                                                    } catch (IOException iOException1) {
                                                        bl = false;
                                                    }
                                                    break label337;
                                                }
                                            }
                                        }
                                    }

                                    bl = false;
                                } finally {
                                    if (bl) {
                                        if (classPathResolver1 != null) {
                                            classPathResolver1.close();
                                        }
                                    }
                                }

                                classPathResolver1.close();
                                if (HiddenOptionFlags.DEBUG_LICENSE_CHECK) {
                                }

                                return true;
                            }

                            classPathResolver1.close();
                            return false;
                        }

                        classPathResolver1.close();
                        return false;
                    }

                    classPathResolver1.close();
                    return bl2;
                }

                classPathResolver1.close();
                return false;
            }
        }
    }

    private static boolean isPermanentLicense() {
        return true;
    }

    public void obfuscateClassFlow(
            ProgramClass programClass1,
            HashMap hashMap,
            Map map1,
            FlowObfuscationExclusions flowObfuscationExclusions,
            ExceptionObfuscationExclusions exceptionObfuscationExclusions,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            Set set1,
            NestedMultiMap nestedMultiMap,
            Map map2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassRepository classRepository1,
            ScriptEnvironment scriptEnvironment1,
            boolean bl,
            boolean bl1,
            ProcessingStatistics processingStatistics1,
            IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException {
        FlowObfuscationGroup flowObfuscationGroup = (FlowObfuscationGroup) map1.get(programClass1);
        if (flowObfuscationGroup == null && programClass1.isVersionedVariant()) {
            flowObfuscationGroup = (FlowObfuscationGroup) map1.get(programClass1.getBaseVersionClass());
        }

        List list1 = (List) hashMap.get(flowObfuscationGroup);
        programClass1.obfuscateExceptions(
                set1,
                nestedMultiMap,
                map2,
                flowObfuscationExclusions,
                exceptionObfuscationExclusions,
                commonSuperTypeResolver1,
                classRepository1,
                scriptEnvironment1,
                list1,
                bl,
                bl1,
                ignoreMissingReferencesSpec1,
                processingStatistics1,
                inheritedMemberAnalyzer,
                classRepository1
        );
    }

    public ObfuscationEngine(
            ClassRepository classRepository1,
            ProgramClass[] programClass1,
            ModuleInfoClass[] moduleInfoClass,
            RootPackageNode rootPackageNode1,
            ObservableHolder observableHolder,
            ChangeLogInputFile[] changeLogInputFiles,
            boolean bl,
            PrintWriter printWriter,
            String string,
            String string1,
            List list1,
            List list2,
            List list3,
            List list4,
            List list5,
            List list6,
            List list7,
            List list8,
            List list9,
            List list10,
            List list11,
            List list12,
            List list13,
            List list14,
            List list15,
            List list16,
            List list17,
            List list18,
            List list19,
            List list20,
            List list21,
            List list22,
            List list23,
            boolean bl1,
            String string2,
            boolean bl2,
            boolean bl3,
            boolean bl4,
            boolean bl5,
            boolean bl6,
            boolean bl7,
            int ba,
            int bb,
            int bc,
            int bd,
            int be,
            int bf,
            int bg,
            int bh,
            int bi,
            int bj,
            boolean bl8,
            String string3,
            int bk,
            int bm,
            String string4,
            String string5,
            ObservableHolder observableHolder1,
            Map map1,
            SetValuedMap setValuedMap,
            int bn,
            boolean bl9,
            String string6,
            ObservableHolder observableHolder2,
            Map map2,
            SetValuedMap setValuedMap1,
            boolean bl10,
            boolean bl11,
            boolean bl12,
            MixedCaseNamesMode mixedCaseNamesMode1,
            boolean bl13,
            boolean bl14,
            int bo,
            String string7,
            Integer integer,
            String string8,
            String string9,
            List list24,
            List list25,
            boolean bl15,
            ReadOnlyMultiMapView readOnlyMultiMapView,
            ObservableHolder observableHolder3,
            Map map3,
            SetValuedMap setValuedMap2,
            Map map4,
            MessageReporter messageReporter1,
            DialogCallback dialogCallback1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {

        super(classRepository1, programClass1, rootPackageNode1);

        boolean bl34 = false;

        com.zelix.klassmaster.util.SetMultiMap setMultiMap7 = null;

        java.lang.String string82 = null;
        boolean bl21 = bo != 0;
        BooleanFlag booleanFlag = new BooleanFlag(bl21);
        if (classRepository1.isClasspathChanged()) {
            messageReporter1.reportFatalError(
                    "FATAL ERROR:", "Classpath has been changed but the classes have not been reopened. You must reopen the classes. (A)"
            );
        } else {
            scriptEnvironment1.getMessageCount();
            scriptEnvironment1.getWarningCount();
            scriptEnvironment1.getErrorCount();
            int seriousErrorCount = scriptEnvironment1.getSeriousErrorCount();
            scriptEnvironment1.clearLoggedMessages();

            try {
                CommonSuperTypeResolver commonSuperTypeResolver1 = new CommonSuperTypeResolver(classRepository1, super.classResolver);
                long ea = 111471356726263L;
                Boolean boolean1 = super.verbose;
                this.removeDeadInstructions(commonSuperTypeResolver1, classRepository1, boolean1, scriptEnvironment1);
                GroupingsSpec groupingsSpec2 = null;
                if (list15 != null && list15.size() > 0) {
                    groupingsSpec2 = new GroupingsSpec(classRepository1, list15, scriptEnvironment1);
                }

                ChangeLogMapping changeLogMapping1 = null;
                if (changeLogInputFiles != null && changeLogInputFiles.length > 0) {
                    observableHolder.setValue("Parsing Input Change Log");
                    Boolean boolean3 = bl4;
                    Boolean boolean2 = bl;
                    ScriptEnvironment scriptEnvironment4 = scriptEnvironment1;
                    MessageReporter messageReporter2 = messageReporter1;
                    changeLogMapping1 = this.loadChangeLogs(changeLogInputFiles, messageReporter2, scriptEnvironment4, boolean2, boolean3);
                }

                ProcessingStatistics processingStatistics1 = new ProcessingStatistics(super.programClasses.length * 5);
                ProgramClass[] programClass2 = super.programClasses;
                int bq = programClass2.length;

                for (int i = 0; i < bq; i += 1) {
                    ProgramClass programClass3 = programClass2[i];
                    programClass3.collectStatistics(processingStatistics1);
                }

                processingStatistics1.finishCollecting();
                ClassFileBase[] classFileBases5;
                if (changeLogMapping1 != null) {
                    this.registerChangeLogPackages(changeLogMapping1, bl6);
                    classFileBases5 = this.addChangeLogClasspathClasses(changeLogMapping1);
                } else {
                    classFileBases5 = (ClassFileBase[]) super.programClasses.clone();
                }

                MutableInt mutableInt4 = new MutableInt(0);
                MutableInt mutableInt5 = new MutableInt(0);
                LookupClassFactory.findMinClassVersion(mutableInt4, mutableInt5, classFileBases5, integer);
                ClassHierarchyNode.setCheckHierarchyGaps(true);
                classRepository1.clearHierarchyCaches();
                InheritedMemberAnalyzer inheritedMemberAnalyzer11 = new InheritedMemberAnalyzer(classFileBases5, super.classResolver);
                FixedClassesExclusionSet fixedClassesExclusionSet1 = null;
                if (list14 != null && list14.size() > 0) {
                    fixedClassesExclusionSet1 = new FixedClassesExclusionSet(classRepository1, list14, scriptEnvironment1);
                    scriptEnvironment1.setFixedClassesExclusionSet(fixedClassesExclusionSet1);
                }

                ExistingSerializedClassesHandler existingSerializedClassesHandler = null;
                if (list13 != null && list13.size() > 0) {
                    existingSerializedClassesHandler = new ExistingSerializedClassesHandler(
                            classRepository1, list13, scriptEnvironment1, fixedClassesExclusionSet1, inheritedMemberAnalyzer11
                    );
                }

                ReflectionAccessMatcher reflectionAccessMatcher = null;
                if (bm != 0 && classRepository1.hasReflectionCallSites() && (list16 != null && list16.size() > 0 || list17 != null && list17.size() > 0)) {
                    reflectionAccessMatcher = new ReflectionAccessMatcher(classRepository1, list16, list17, scriptEnvironment1);
                }

                FlowObfuscationExclusions flowObfuscationExclusions;
                if (bc == 0 && (bk == 0 || !HiddenOptionFlags.FLOW_EXCLUDES_EXCEPTION_OBFUSCATION) && bo != 3
                        || fixedClassesExclusionSet1 == null && (list3 == null || list3.size() <= 0) && (list4 == null || list4.size() <= 0)) {
                    flowObfuscationExclusions = null;
                } else {
                    flowObfuscationExclusions = new FlowObfuscationExclusions(classRepository1, list3, list4, changeLogMapping1, scriptEnvironment1);
                }

                ExceptionObfuscationExclusions exceptionObfuscationExclusions;
                if (bk == 0 || fixedClassesExclusionSet1 == null && (list5 == null || list5.size() <= 0) && (list6 == null || list6.size() <= 0)) {
                    exceptionObfuscationExclusions = null;
                } else {
                    exceptionObfuscationExclusions = new ExceptionObfuscationExclusions(classRepository1, list5, list6, changeLogMapping1, scriptEnvironment1);
                }

                ClassInitOrderHandler classInitOrderHandler1 = null;
                if (scriptEnvironment1.hasClassInitializationOrder()) {
                    classInitOrderHandler1 = new ClassInitOrderHandler(scriptEnvironment1);
                }

                ReferenceObfuscationExclusions referenceObfuscationExclusions;
                NestedMultiMap nestedMultiMap;
                ReferenceObfuscator referenceObfuscator;
                StaticInitCalleeAnalyzer staticInitCalleeAnalyzer1;
                List list26;
                referenceObfuscationExclusions = null;
                nestedMultiMap = null;
                referenceObfuscator = null;
                ClassHierarchy classHierarchy3 = super.classHierarchy;
                ClassResolver classResolver6 = super.classResolver;
                IgnoreMissingReferencesSpec ignoreMissingReferencesSpec3 = scriptEnvironment1.getIgnoreMissingReferencesSpec();
                boolean bl16 = bc != 0;
                IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1 = ignoreMissingReferencesSpec3;
                boolean bl22 = bl16;
                IgnoreMissingReferencesSpec ignoreMissingReferencesSpec2 = ignoreMissingReferencesSpec1;
                ClassResolver classResolver1 = classResolver6;
                FlowObfuscationExclusions flowObfuscationExclusions1 = flowObfuscationExclusions;
                ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
                ClassHierarchy classHierarchy1 = classHierarchy3;
                ClassRepository classRepository2 = classRepository1;
                ClassFileBase[] classFileBases = classFileBases5;
                staticInitCalleeAnalyzer1 = new StaticInitCalleeAnalyzer(
                        classFileBases,
                        classRepository2,
                        classHierarchy1,
                        scriptEnvironment2,
                        flowObfuscationExclusions1,
                        classResolver1,
                        ignoreMissingReferencesSpec2,
                        bl22
                );
                list26 = null;
                label2500:
                if (bn != 0) {
                    ScriptEnvironment scriptEnvironment32;
                    label2758:
                    {
                        if (list18 == null || list18.size() <= 0) {
                            if (list19 == null) {
                                scriptEnvironment32 = scriptEnvironment1;
                                long de = 99214412922351L;
                                string82 = "You elected to obfuscate references but you have not specified any references to be obfuscate.";
                                break label2758;
                            }

                            if (list19.size() <= 0) {
                                scriptEnvironment32 = scriptEnvironment1;
                                long ef = 99214412922351L;
                                string82 = "You elected to obfuscate references but you have not specified any references to be obfuscate.";
                                break label2758;
                            }
                        }

                        TwoKeySetMultiMap twoKeySetMultiMap = new TwoKeySetMultiMap();
                        if (string9 != null && string9.length() > 0) {
                            ScriptEnvironment scriptEnvironment26 = scriptEnvironment1;
                            String string66 = "newClassNameFile";
                            String string10 = string9;
                            ScriptEnvironment scriptEnvironment5 = scriptEnvironment26;
                            String string17 = string66;
                            String string16 = string10;
                            list26 = readNameListFile(string16, string17, scriptEnvironment5);
                        }

                        referenceObfuscator = new ReferenceObfuscator(booleanFlag, list26, bl5);
                        ProgramClass[] programClass4 = super.programClasses;
                        int bs = programClass4.length;

                        for (int i = 0; i < bs; i += 1) {
                            ProgramClass programClass5 = programClass4[i];
                            if (programClass5.isMultiRelease()) {
                                Iterator iterator = programClass5.getAllVersions().iterator();

                                while (iterator.hasNext()) {
                                    ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                                    ProgramClass programClass56 = (ProgramClass) classFileBase;
                                    ClassRepository classRepository4 = classRepository1;
                                    CommonSuperTypeResolver commonSuperTypeResolver3 = commonSuperTypeResolver1;
                                    programClass56.collectObfuscatableReferences(
                                            twoKeySetMultiMap, referenceObfuscator, commonSuperTypeResolver3, classRepository4, scriptEnvironment1
                                    );
                                }
                            } else {
                                ClassRepository classRepository5 = classRepository1;
                                CommonSuperTypeResolver commonSuperTypeResolver4 = commonSuperTypeResolver1;
                                programClass5.collectObfuscatableReferences(
                                        twoKeySetMultiMap, referenceObfuscator, commonSuperTypeResolver4, classRepository5, scriptEnvironment1
                                );
                            }
                        }

                        referenceObfuscationExclusions = new ReferenceObfuscationExclusions(
                                classRepository1, twoKeySetMultiMap.flattenToSetMultiMap(), list18, list19, scriptEnvironment1
                        );
                        nestedMultiMap = referenceObfuscationExclusions.collectIncludedReferences(
                                twoKeySetMultiMap, staticInitCalleeAnalyzer1, fixedClassesExclusionSet1
                        );
                        break label2500;
                    }

                    String string18 = string82;
                    scriptEnvironment32.logWarning(string18);
                }

                NameExclusionSet nameExclusionSet6 = new NameExclusionSet(classRepository1, list1, list2, classFileBases5, scriptEnvironment1);
                if (existingSerializedClassesHandler != null) {
                    PrintWriter printWriter6 = scriptEnvironment1.getLogWriter();
                    long du = 62422924578472L;
                    PrintWriter printWriter2 = printWriter6;
                    existingSerializedClassesHandler.applyNameExclusions(nameExclusionSet6, printWriter2);
                }

                Integer integer2 = 2;
                Integer integer1 = 3;
                ObfuscationEngine obfuscationEngine1 = this;
                classRepository1.recordClassDigests(obfuscationEngine1, integer1, integer2);
                if (scriptEnvironment1.getSeriousErrorCount() > seriousErrorCount) {
                    String string101 = scriptEnvironment1.getSeriousErrorsText();
                    ea = 81764775538649L;
                    String string19 = string101;
                    messageReporter1.reportFatalErrorWithDetail("FATAL ERROR:", "Serious Errors detected during obfuscation (A).", string19);
                }

                StringEncryptionExclusionSpec stringEncryptionExclusionSpec1 = null;
                if (be != 0 && (fixedClassesExclusionSet1 != null || list7 != null && list7.size() > 0 || list8 != null && list8.size() > 0)) {
                    stringEncryptionExclusionSpec1 = new StringEncryptionExclusionSpec(classRepository1, list7, list8, scriptEnvironment1);
                }

                if (bf != 0 && classRepository1.isIntegerConstantsEncrypted()) {
                    scriptEnvironment1.logWarning(
                            "Integer Constant Encryption : Cannot perform Integer Constant Encryption more than once on the same classes. Setting of the 'encryptIntegerConstants' parameter of the 'obfuscate' statement will be ignored."
                    );
                    bf = 0;
                }

                IntegerEncryptionExclusions integerEncryptionExclusions5 = null;
                if (bf != 0 && (fixedClassesExclusionSet1 != null || list9 != null && list9.size() > 0 || list10 != null && list10.size() > 0)) {
                    integerEncryptionExclusions5 = new IntegerEncryptionExclusions(classRepository1, list9, list10, scriptEnvironment1);
                }

                if (bg != 0 && classRepository1.isLongConstantsEncrypted()) {
                    scriptEnvironment1.logWarning(
                            "Long Constant Encryption : Cannot perform Long Constant Encryption more than once on the same classes. Setting of the 'encryptLongConstants' parameter of the 'obfuscate' statement will be ignored."
                    );
                    bg = 0;
                }

                LongEncryptionExclusionHandler longEncryptionExclusionHandler2 = null;
                if (bg != 0 && (fixedClassesExclusionSet1 != null || list11 != null && list11.size() > 0 || list12 != null && list12.size() > 0)) {
                    longEncryptionExclusionHandler2 = new LongEncryptionExclusionHandler(classRepository1, list11, list12, scriptEnvironment1);
                }

                MethodParameterExclusions methodParameterExclusions5 = null;
                Set set2 = null;
                ListMultimap listMultimap4 = null;
                if (booleanFlag.getValue()) {
                    if (be == 4 || bf != 0 || bg != 0 || bn != 0 || bo == 3 || HiddenOptionFlags.CHECK_REPEATED_PARAMETER_CHANGES) {
                        if (!setValuedMap2.isEmpty()) {
                            booleanFlag.setValue(false);
                            scriptEnvironment1.logWarning(
                                    "Method Parameter List Changing : Cannot allow method parameter changing more than once on the same classes. Setting of the 'methodParameterChanges' parameter of the 'obfuscate' statement will be ignored."
                            );
                        } else {
                            ObservableHolder observableHolder5 = new ObservableHolder();
                            ObservableHolder observableHolder6 = new ObservableHolder();
                            FixedClassesExclusionSet fixedClassesExclusionSet3 = fixedClassesExclusionSet1;
                            ReflectionAccessMatcher reflectionAccessMatcher1 = reflectionAccessMatcher;
                            NameExclusionSet nameExclusionSet = nameExclusionSet6;
                            List list29 = list21;
                            List list28 = list20;
                            Integer integer3 = ba;
                            ObservableHolder observableHolder10 = observableHolder6;
                            ObservableHolder observableHolder9 = observableHolder5;
                            methodParameterExclusions5 = this.createMethodParameterExclusions(
                                    false,
                                    observableHolder9,
                                    observableHolder10,
                                    integer3,
                                    list28,
                                    list29,
                                    nameExclusionSet,
                                    reflectionAccessMatcher1,
                                    fixedClassesExclusionSet3,
                                    scriptEnvironment1
                            );
                            set2 = (Set) observableHolder5.getValue();
                            listMultimap4 = (ListMultimap) observableHolder6.getValue();
                        }
                    }
                } else if (changeLogMapping1 != null && changeLogMapping1.hasParameterChangeData() && bl) {
                    String string83 = "Input change log '"
                            + changeLogMapping1.getChangeLogName()
                            + "' contains method parameter change data but the '"
                            + "methodParameterChanges"
                            + "' parameter of the '"
                            + "obfuscate"
                            + "' statement is missing or is set to 'none'. The method parameter change data will be ignored because the change log is specified as loose.";
                    long df = 99214412922351L;
                    String string20 = string83;
                    scriptEnvironment1.logWarning(string20);
                }

                MethodParameterExclusions methodParameterExclusions6 = null;
                if (classRepository1.isParametersObfuscated()) {
                    ProgramClass[] programClass25 = super.programClasses;
                    int bu = programClass25.length;

                    for (int i = 0; i < bu; i += 1) {
                        ProgramClass programClass6 = programClass25[i];
                        programClass6.clearParameterObfuscatedFlags();
                        if (programClass6.hasVersionedVariants()) {
                            Iterator iterator1 = programClass6.getVersionedVariants().iterator();

                            while (iterator1.hasNext()) {
                                ClassFileBase classFileBase1 = (ClassFileBase) iterator1.next();
                                ((ProgramClass) classFileBase1).clearParameterObfuscatedFlags();
                            }
                        }
                    }
                }

                if (bd != 0) {
                    if (classRepository1.isParametersObfuscated()) {
                        scriptEnvironment1.logWarning(
                                "Parameter Obfuscation : Cannot allow method parameter obfuscation more than once on the same classes. Setting of the 'obfuscateParameters' parameter of the 'obfuscate' statement will be ignored."
                        );
                        bd = 0;
                    } else {
                        classRepository1.markParametersObfuscated();
                        ObservableHolder observableHolder20 = new ObservableHolder();
                        ObservableHolder observableHolder21 = new ObservableHolder();
                        FixedClassesExclusionSet fixedClassesExclusionSet4 = fixedClassesExclusionSet1;
                        ReflectionAccessMatcher reflectionAccessMatcher2 = reflectionAccessMatcher;
                        NameExclusionSet nameExclusionSet1 = nameExclusionSet6;
                        List list31 = list23;
                        List list30 = list22;
                        Integer integer4 = ba;
                        ObservableHolder observableHolder12 = observableHolder21;
                        ObservableHolder observableHolder11 = observableHolder20;
                        methodParameterExclusions6 = this.createMethodParameterExclusions(
                                true,
                                observableHolder11,
                                observableHolder12,
                                integer4,
                                list30,
                                list31,
                                nameExclusionSet1,
                                reflectionAccessMatcher2,
                                fixedClassesExclusionSet4,
                                scriptEnvironment1
                        );
                        set2 = (Set) observableHolder20.getValue();
                        listMultimap4 = (ListMultimap) observableHolder21.getValue();
                    }
                }

                if (fixedClassesExclusionSet1 != null) {
                    PrintWriter printWriter3 = scriptEnvironment1.getLogWriter();
                    LongEncryptionExclusionHandler longEncryptionExclusionHandler = longEncryptionExclusionHandler2;
                    IntegerEncryptionExclusions integerEncryptionExclusions1 = integerEncryptionExclusions5;
                    StringEncryptionExclusionSpec stringEncryptionExclusionSpec = stringEncryptionExclusionSpec1;
                    ExceptionObfuscationExclusions exceptionObfuscationExclusions1 = exceptionObfuscationExclusions;
                    FlowObfuscationExclusions flowObfuscationExclusions2 = flowObfuscationExclusions;
                    NameExclusionSet nameExclusionSet2 = nameExclusionSet6;
                    fixedClassesExclusionSet1.applyExclusions(
                            nameExclusionSet2,
                            flowObfuscationExclusions2,
                            exceptionObfuscationExclusions1,
                            stringEncryptionExclusionSpec,
                            integerEncryptionExclusions1,
                            longEncryptionExclusionHandler,
                            printWriter3
                    );
                }

                ZkmUtils.createHashMap();
                FlowObfuscationManager flowObfuscationManager4 = null;
                if (changeLogMapping1 != null) {
                    if (!bl) {
                        FixedClassesExclusionSet fixedClassesExclusionSet5 = fixedClassesExclusionSet1;
                        ChangeLogMapping changeLogMapping4 = changeLogMapping1;
                        nameExclusionSet6.unexcludeRenamedPackages(changeLogMapping4, fixedClassesExclusionSet5);
                        FixedClassesExclusionSet fixedClassesExclusionSet6 = fixedClassesExclusionSet1;
                        ChangeLogMapping changeLogMapping5 = changeLogMapping1;
                        nameExclusionSet6.unexcludeRenamedClasses(changeLogMapping5, fixedClassesExclusionSet6);
                    } else {
                        NameExclusionSet nameExclusionSet3 = nameExclusionSet6;
                        changeLogMapping1.applyLooseClassAndPackageExclusions(nameExclusionSet3);
                    }
                }

                this.validateClassHierarchy(messageReporter1);
                String string76 = this.getLicenseDateCode();
                String string84 = scriptEnvironment1.getLogFileName();
                short dg = 20803;
                int dw = -557565592;
                String string21 = string84;
                long ch = this.getFileTimestamp(string21);
                Boolean boolean4 = true;
                String string22 = string76;
                long bw = this.decodeLicenseTimestamp(string22, boolean4);
                ClassRepository classRepository6 = classRepository1;
                Long long3 = bw;
                boolean bl33 = this.checkLicenseTimestamps(ch, long3, classRepository6);
                if (!bl33) {
                    scriptEnvironment1.incrementPendingMarkerCount();
                }

                Random random7;
                label2414:
                {
                    setMultiMap7 = null;
                    bl34 = bi == 1 || bi == 2;
                    byte cz;
                    if (!bl5) {
                        if (!HiddenOptionFlags.RANDOMIZE_OBFUSCATION) {
                            random7 = ZkmUtils.createSeededRandom(super.programClasses.length);
                            random7.nextBoolean();
                            break label2414;
                        }

                        cz = 67;
                    } else {
                        cz = 67;
                    }

                    random7 = ZkmUtils.createRandom(cz);
                }

                HashSet hashSet10 = ZkmUtils.createHashSet();
                if (bc != 0) {
                    String string11 = xorStrings("\u0002'5&2$$N$+4s\"(+N?*?s'+*Mm,=s.))Cm*=6a(7\u001a92<s,\"1R\"! s()e_,&;s\"+$I>", "MESSAGE:");
                    observableHolder.setValue(string11);
                    String string85 = string11 + (bl33 ? "" : (HiddenOptionFlags.DOT_MESSAGE_SUFFIX ? " ." : " "));
                    long dh = 129120864439474L;
                    String string23 = string85;
                    scriptEnvironment1.logMessage(string23);
                    flowObfuscationManager4 = new FlowObfuscationManager(
                            super.programClasses,
                            classFileBases5,
                            scriptEnvironment1,
                            groupingsSpec2,
                            changeLogMapping1,
                            classRepository1,
                            processingStatistics1,
                            staticInitCalleeAnalyzer1,
                            bl7,
                            bl5
                    );
                    NestedMultiMap nestedMultiMap4 = null;
                    MutableInt mutableInt = mutableInt4;
                    MethodParameterExclusions methodParameterExclusions1 = methodParameterExclusions5;
                    BooleanFlag booleanFlag2 = booleanFlag;
                    ReferenceObfuscationExclusions referenceObfuscationExclusions1 = referenceObfuscationExclusions;
                    if (this.canObfuscateReferences(referenceObfuscationExclusions1, booleanFlag2, methodParameterExclusions1, mutableInt, scriptEnvironment1)) {
                        nestedMultiMap4 = new NestedMultiMap(HiddenOptionFlags.USE_PARALLEL);
                    }

                    Boolean boolean6 = bl12;
                    Boolean boolean5 = bl33;
                    Random random3 = random7;
                    CommonSuperTypeResolver commonSuperTypeResolver5 = commonSuperTypeResolver1;
                    InheritedMemberAnalyzer inheritedMemberAnalyzer = inheritedMemberAnalyzer11;
                    NestedMultiMap nestedMultiMap2 = nestedMultiMap4;
                    StaticInitCalleeAnalyzer staticInitCalleeAnalyzer2 = staticInitCalleeAnalyzer1;
                    NameExclusionSet nameExclusionSet4 = nameExclusionSet6;
                    FlowObfuscationManager flowObfuscationManager1 = flowObfuscationManager4;
                    ClassInitOrderHandler classInitOrderHandler2 = classInitOrderHandler1;
                    FlowObfuscationExclusions flowObfuscationExclusions3 = flowObfuscationExclusions;
                    ScriptEnvironment scriptEnvironment6 = scriptEnvironment1;
                    Integer integer5 = bc;
                    setMultiMap7 = this.insertOpaquePredicates(
                            integer5,
                            scriptEnvironment6,
                            flowObfuscationExclusions3,
                            classInitOrderHandler2,
                            flowObfuscationManager1,
                            nameExclusionSet4,
                            staticInitCalleeAnalyzer2,
                            nestedMultiMap2,
                            inheritedMemberAnalyzer,
                            commonSuperTypeResolver5,
                            random3,
                            boolean5,
                            boolean6
                    );
                    hashSet10.addAll(flowObfuscationManager4.getGeneratedMethods());
                    if (nestedMultiMap4 != null) {
                        if (!nestedMultiMap4.isEmpty()) {
                            String string12 = "Flow Obfuscation";
                            String string24 = string12;
                            ReferenceObfuscationExclusions referenceObfuscationExclusions2 = referenceObfuscationExclusions;
                            this.addReferenceObfuscationExclusions(nestedMultiMap4, nestedMultiMap, referenceObfuscationExclusions2, string24);
                            nestedMultiMap4.clearAll();
                            Thread.yield();
                        } else {
                            Thread.yield();
                        }
                    } else {
                        Thread.yield();
                    }
                } else {
                    Thread.yield();
                }

                Map map33 = null;
                if (changeLogMapping1 != null) {
                    if (changeLogMapping1.hasParameterChangeData()) {
                        long dj = 45353750368893L;
                        ClassResolver classResolver2 = super.classResolver;
                        changeLogMapping1.resolveParameterChangeData(classResolver2);
                        if (booleanFlag.getValue()) {
                            map33 = changeLogMapping1.getParameterChangeKeysByMethod();
                        }
                    }

                    if (changeLogMapping1.hasParameterObfuscation()) {
                        dg = 28368;
                        ClassResolver classResolver5 = super.classResolver;
                        Integer integer7 = 53487;
                        ClassResolver classResolver3 = classResolver5;
                        Integer integer6 = 30291;
                        int dx = integer6;
                        int cd = integer7;
                        ClassResolver classResolver4 = classResolver3;
                        int ce = dx;
                        changeLogMapping1.collectParameterObfuscatedMethods(ce, classResolver4, cd);
                    }
                }

                if (ba != 1) {
                    ChangeLogMapping changeLogMapping6 = changeLogMapping1;
                    NameExclusionSet nameExclusionSet5 = nameExclusionSet6;
                    this.excludeOuterClassesOfInners(ba, nameExclusionSet5, changeLogMapping6);
                }

                HashMap hashMap39;
                if (nameExclusionSet6.hasNoIncludedPackages()) {
                    ChangeLogMapping changeLogMapping7 = changeLogMapping1;
                    hashMap39 = rootPackageNode1.buildRetainedPackageMap(changeLogMapping7);
                    if (bl8) {
                        if (!rootPackageNode1.hasNoChildren()) {
                            scriptEnvironment1.logWarning("You elected to collapse packages but all packages excluded from being renamed.");
                        } else {
                            scriptEnvironment1.logWarning("You elected to collapse packages but no packages found to collapse.");
                        }
                    }
                } else {
                    List list37 = null;
                    if (string8 != null && string8.length() > 0) {
                        ScriptEnvironment scriptEnvironment27 = scriptEnvironment1;
                        String string67 = "newPackageNameFile";
                        String string70 = string8;
                        ScriptEnvironment scriptEnvironment7 = scriptEnvironment27;
                        String string26 = string67;
                        String string25 = string70;
                        list37 = readNameListFile(string25, string26, scriptEnvironment7);
                    }

                    ArrayEnumeration arrayEnumeration = new ArrayEnumeration(super.programClasses);
                    ClassPathResolver classPathResolver2 = super.classpathClassLoader.getClassPathResolver();
                    long eh = 137239538626458L;
                    Iterator iterator33 = classRepository1.iterateResourceFileNames();
                    ScriptEnvironment scriptEnvironment8 = scriptEnvironment1;
                    List list32 = list37;
                    Iterator iterator10 = iterator33;
                    String string28 = string3;
                    Boolean boolean10 = bl8;
                    ClassPathResolver classPathResolver1 = classPathResolver2;
                    MixedCaseNamesMode mixedCaseNamesMode2 = mixedCaseNamesMode1;
                    String string27 = string2;
                    Boolean boolean9 = bl1;
                    Boolean boolean8 = bl15;
                    Boolean boolean7 = bl5;
                    hashMap39 = rootPackageNode1.renamePackages(
                            arrayEnumeration,
                            nameExclusionSet6,
                            changeLogMapping1,
                            boolean7,
                            boolean8,
                            boolean9,
                            string27,
                            mixedCaseNamesMode2,
                            classPathResolver1,
                            boolean10,
                            string28,
                            iterator10,
                            list32,
                            scriptEnvironment8
                    );
                    ModuleInfoClass[] moduleInfoClass1 = moduleInfoClass;
                    int bx = moduleInfoClass1.length;

                    for (int i = 0; i < bx; i += 1) {
                        ModuleInfoClass moduleInfoClass2 = moduleInfoClass1[i];
                        HashMap hashMap9 = hashMap39;
                        moduleInfoClass2.applyPackageRenames(hashMap9);
                        if (moduleInfoClass2.hasVersionedVariants()) {
                            Iterator iterator2 = moduleInfoClass2.getVersionedVariants().iterator();

                            while (iterator2.hasNext()) {
                                ClassFileBase classFileBase2 = (ClassFileBase) iterator2.next();
                                ModuleInfoClass moduleInfoClass3 = (ModuleInfoClass) classFileBase2;
                                HashMap hashMap10 = hashMap39;
                                moduleInfoClass3.applyPackageRenames(hashMap10);
                            }
                        }
                    }
                }

                classRepository1.mergeCumulativeNameMapping(hashMap39);
                Thread.yield();
                ProgramClass[] programClass45;
                if (scriptEnvironment1.getSeriousErrorCount() > seriousErrorCount) {
                    String string94 = scriptEnvironment1.getSeriousErrorsText();
                    ea = 81764775538649L;
                    String string29 = string94;
                    messageReporter1.reportFatalErrorWithDetail("FATAL ERROR:", "Serious Errors detected during obfuscation (B).", string29);
                    programClass45 = super.programClasses;
                } else {
                    programClass45 = super.programClasses;
                }

                HashMap hashMap40;
                HashMap hashMap41;
                HashMap hashMap42;
                label2387:
                {
                    int ci = ZkmUtils.getPrimeCapacity(programClass45.length);
                    hashMap40 = ZkmUtils.createHashMap(ci);
                    hashMap41 = ZkmUtils.createHashMap(ci);
                    hashMap42 = ZkmUtils.createHashMap(ci);
                    ObservableHolder observableHolder24;
                    String string79;
                    if (nameExclusionSet6.hasNoIncludedClasses()) {
                        if (nameExclusionSet6.hasNoIncludedPackages()) {
                            ChangeLogMapping changeLogMapping8 = changeLogMapping1;
                            boolean bl35 = this.initClassNameMaps(hashMap41, hashMap42, hashMap40, changeLogMapping8);
                            if (bl35 || ba != 0 && bb != 1) {
                                ModuleInfoClass[] moduleInfoClass5 = classRepository1.getModuleInfoClasses();
                                HashMap hashMap16 = hashMap39;
                                HashMap hashMap15 = hashMap41;
                                Integer integer11 = bb;
                                Integer integer10 = ba;
                                ModuleInfoClass[] moduleInfoClass4 = moduleInfoClass5;
                                ClassFileBase[] classFileBases2 = classFileBases5;
                                classRepository1.applyNameChanges(classFileBases2, moduleInfoClass4, integer10, integer11, hashMap15, hashMap16, messageReporter1);
                                classRepository1.refreshIndexes();
                            }

                            if (bl35) {
                                classRepository1.indexFields();
                                classRepository1.indexMethods();
                                classRepository1.snapshotPreRenameCaches();
                            }
                            break label2387;
                        }

                        observableHolder24 = observableHolder;
                        string79 = "Obfuscating class names";
                    } else {
                        observableHolder24 = observableHolder;
                        string79 = "Obfuscating class names";
                    }

                    observableHolder24.setValue(string79);
                    MessageReporter messageReporter3 = messageReporter1;
                    ClassFileBase[] classFileBases1 = classFileBases5;
                    HashMap hashMap14 = hashMap42;
                    HashMap hashMap13 = hashMap41;
                    HashMap hashMap12 = hashMap40;
                    HashMap hashMap11 = hashMap39;
                    String string31 = string9;
                    Boolean boolean13 = bl13;
                    MixedCaseNamesMode mixedCaseNamesMode3 = mixedCaseNamesMode1;
                    Integer integer9 = bb;
                    Integer integer8 = ba;
                    String string30 = string2;
                    Boolean boolean12 = bl1;
                    Boolean boolean11 = bl15;
                    this.renameClasses(
                            nameExclusionSet6,
                            changeLogMapping1,
                            bl5,
                            boolean11,
                            boolean12,
                            string30,
                            integer8,
                            integer9,
                            mixedCaseNamesMode3,
                            boolean13,
                            string31,
                            hashMap11,
                            hashMap12,
                            hashMap13,
                            hashMap14,
                            classFileBases1,
                            messageReporter3,
                            scriptEnvironment1
                    );
                    classRepository1.snapshotPreRenameCaches();
                }

                commonSuperTypeResolver1.clearCache();
                Thread.yield();
                if (HiddenOptionFlags.SHUFFLE_MEMBERS) {
                    this.shuffleMembers();
                }

                NestedMultiMap nestedMultiMap5 = null;
                switch (bh) {
                    case 0:
                        observableHolder.setValue("Removing line numbers");
                        this.removeLineNumbers();
                        break;
                    case 1:
                        observableHolder.setValue("Scrambling line numbers");
                        nestedMultiMap5 = new NestedMultiMap(super.programClasses.length, 75, 2);
                        Boolean boolean14 = bl;
                        ChangeLogMapping changeLogMapping9 = changeLogMapping1;
                        FixedClassesExclusionSet fixedClassesExclusionSet7 = fixedClassesExclusionSet1;
                        HashMap hashMap17 = hashMap42;
                        this.scrambleLineNumbers(nestedMultiMap5, hashMap17, fixedClassesExclusionSet7, changeLogMapping9, boolean14, scriptEnvironment1);
                }

                ObservableHolder observableHolder25;
                String string80;
                if (changeLogMapping1 != null) {
                    if (changeLogMapping1.hasLineNumberMappings()) {
                        if (nestedMultiMap5 == null) {
                            nestedMultiMap5 = new NestedMultiMap(super.programClasses.length, 75, 2);
                        }

                        ChangeLogMapping changeLogMapping10 = changeLogMapping1;
                        this.addChangeLogLineNumberMappings(nestedMultiMap5, changeLogMapping10);
                        observableHolder25 = observableHolder;
                        string80 = "Obfuscating method names";
                    } else {
                        observableHolder25 = observableHolder;
                        string80 = "Obfuscating method names";
                    }
                } else {
                    observableHolder25 = observableHolder;
                    string80 = "Obfuscating method names";
                }

                observableHolder25.setValue(string80);
                Thread.yield();
                if (changeLogMapping1 != null) {
                    ClasspathClassLoader classpathClassLoader1 = super.classpathClassLoader;
                    CommonSuperTypeResolver commonSuperTypeResolver6 = commonSuperTypeResolver1;
                    ClassRepository classRepository7 = classRepository1;
                    changeLogMapping1.resolveMethodMappings(hashMap41, hashMap42, classRepository7, commonSuperTypeResolver6, classpathClassLoader1);
                }

                if (scriptEnvironment1.getSeriousErrorCount() > seriousErrorCount) {
                    String string95 = scriptEnvironment1.getSeriousErrorsText();
                    ea = 81764775538649L;
                    String string32 = string95;
                    messageReporter1.reportFatalErrorWithDetail("FATAL ERROR:", "Serious Errors detected during obfuscation (E).", string32);
                }

                TwoKeyMap twoKeyMap12 = new TwoKeyMap(super.programClasses.length, 25);
                TwoKeyMap twoKeyMap13 = new TwoKeyMap(super.programClasses.length, 25);
                ObservableHolder observableHolder22 = new ObservableHolder();
                MethodParameterExclusions methodParameterExclusions7 = bd != 0 ? methodParameterExclusions6 : null;
                ObservableHolder observableHolder13 = observableHolder22;
                List list33 = list25;
                Map map15 = map4;
                ClassFileBase[] classFileBases3 = classFileBases5;
                TwoKeyMap twoKeyMap9 = twoKeyMap13;
                TwoKeyMap twoKeyMap8 = twoKeyMap12;
                HashMap hashMap18 = hashMap42;
                Boolean boolean21 = bl3;
                String string33 = string2;
                Boolean boolean20 = bl1;
                Boolean boolean19 = bl15;
                Boolean boolean18 = bl33;
                Boolean boolean17 = bl5;
                Boolean boolean16 = bl14;
                Boolean boolean15 = bl4;
                Integer integer12 = ba;
                this.renameMethods(
                        nameExclusionSet6,
                        methodParameterExclusions7,
                        fixedClassesExclusionSet1,
                        changeLogMapping1,
                        bl,
                        integer12,
                        boolean15,
                        boolean16,
                        boolean17,
                        boolean18,
                        boolean19,
                        boolean20,
                        string33,
                        boolean21,
                        hashMap18,
                        twoKeyMap8,
                        twoKeyMap9,
                        classFileBases3,
                        map15,
                        list33,
                        observableHolder13,
                        scriptEnvironment1
                );
                classRepository1.markMethodNamesObfuscated();
                if (changeLogMapping1 != null) {
                    ClasspathClassLoader classpathClassLoader2 = super.classpathClassLoader;
                    ClassRepository classRepository8 = classRepository1;
                    SetMultiMap setMultiMap = setMultiMap7;
                    HashMap hashMap20 = hashMap42;
                    HashMap hashMap19 = hashMap41;
                    changeLogMapping1.resolveFieldMappings(hashMap19, hashMap20, setMultiMap, classRepository8, classpathClassLoader2);
                    if (!bl) {
                        FixedClassesExclusionSet fixedClassesExclusionSet8 = fixedClassesExclusionSet1;
                        HashMap hashMap21 = hashMap42;
                        ChangeLogMapping changeLogMapping11 = changeLogMapping1;
                        nameExclusionSet6.unexcludeRenamedFields(changeLogMapping11, hashMap21, fixedClassesExclusionSet8);
                    } else {
                        HashMap hashMap22 = hashMap42;
                        changeLogMapping1.applyLooseFieldExclusions(nameExclusionSet6, hashMap22, classRepository1);
                    }
                }

                if (scriptEnvironment1.getSeriousErrorCount() > seriousErrorCount) {
                    String string96 = scriptEnvironment1.getSeriousErrorsText();
                    ea = 81764775538649L;
                    String string34 = string96;
                    messageReporter1.reportFatalErrorWithDetail("FATAL ERROR:", "Serious Errors detected during obfuscation (C).", string34);
                }

                TwoKeyMap twoKeyMap3;
                label2713:
                {
                    TwoKeyMap twoKeyMap2 = new TwoKeyMap(super.programClasses.length, 20);
                    twoKeyMap3 = new TwoKeyMap(super.programClasses.length, 20);
                    ObservableHolder observableHolder26;
                    String string81;
                    if (nameExclusionSet6.hasNoIncludedFields()) {
                        if (bc == 0 || changeLogMapping1 == null) {
                            boolean bl20 = this.initFieldNameMaps(twoKeyMap2, twoKeyMap3, changeLogMapping1);
                            if (bl20) {
                                classRepository1.refreshClassReferences(classFileBases5, scriptEnvironment1);
                            }
                            break label2713;
                        }

                        observableHolder26 = observableHolder;
                        string81 = "Obfuscating field names";
                    } else {
                        observableHolder26 = observableHolder;
                        string81 = "Obfuscating field names";
                    }

                    observableHolder26.setValue(string81);
                    List list34 = list24;
                    ClassFileBase[] classFileBases4 = classFileBases5;
                    TwoKeyMap twoKeyMap11 = twoKeyMap3;
                    TwoKeyMap twoKeyMap10 = twoKeyMap2;
                    HashMap hashMap23 = hashMap42;
                    Boolean boolean25 = bl2;
                    String string35 = string2;
                    Boolean boolean24 = bl5;
                    Boolean boolean23 = bl1;
                    Boolean boolean22 = bl15;
                    Integer integer13 = ba;
                    this.renameFields(
                            nameExclusionSet6,
                            changeLogMapping1,
                            integer13,
                            boolean22,
                            boolean23,
                            boolean24,
                            string35,
                            boolean25,
                            hashMap23,
                            twoKeyMap10,
                            twoKeyMap11,
                            classFileBases4,
                            list34,
                            scriptEnvironment1
                    );
                    classRepository1.markFieldNamesObfuscated();
                }

                if (scriptEnvironment1.getSeriousErrorCount() > seriousErrorCount) {
                    String string97 = scriptEnvironment1.getSeriousErrorsText();
                    ea = 81764775538649L;
                    String string36 = string97;
                    messageReporter1.reportFatalErrorWithDetail("FATAL ERROR:", "Serious Errors detected during obfuscation (D).", string36);
                }

                ObservableHolder observableHolder14 = observableHolder;
                this.processLocalVariables(bi, observableHolder14);
                this.processMethodParameters(bj, observableHolder);
                Object object = observableHolder22.getValue();
                boolean bl41 = false;
                short eb = 13246;
                Map map16 = (Map) object;
                Integer integer14 = 672164757;
                this.markDebugInfoPresence(integer14, map16);
                SetMultiMap setMultiMap8 = null;
                if (booleanFlag.getValue()) {
                    setMultiMap8 = MethodParameterChanger.buildClassDependencyMap(super.classHierarchy);
                }

                if (ba != 0) {
                    observableHolder.setValue("Removing inner class information");
                    FixedClassesExclusionSet fixedClassesExclusionSet9 = fixedClassesExclusionSet1;
                    HashMap hashMap24 = hashMap42;
                    Integer integer15 = ba;
                    this.removeInnerClassInfo(integer15, hashMap24, fixedClassesExclusionSet9, scriptEnvironment1);
                }

                if (bb != 0) {
                    this.removeGenericSignatures(bb);
                }

                classRepository1.updateNameStringConstants(hashMap42, hashMap41, hashMap39, scriptEnvironment1);
                if (scriptEnvironment1.getSeriousErrorCount() > seriousErrorCount) {
                    String string98 = scriptEnvironment1.getSeriousErrorsText();
                    ea = 81764775538649L;
                    String string37 = string98;
                    messageReporter1.reportFatalErrorWithDetail("FATAL ERROR:", "Serious Errors detected during obfuscation (F).", string37);
                }

                HashMap hashMap2 = classRepository1.findJ2meClasses();
                scriptEnvironment1.setMicroEditionClassesPresent(!hashMap2.isEmpty());
                MultiMapTable multiMapTable = new MultiMapTable(super.programClasses.length);
                PairMultiMap pairMultiMap1 = new PairMultiMap(super.programClasses.length);
                ListMultimap listMultimap = new ListMultimap(super.programClasses.length);
                if (be != 0) {
                    ProgramClass[] programClass7 = super.programClasses;
                    int bz = programClass7.length;

                    for (int ca = 0; ca < bz; ca += 1) {
                        ProgramClass programClass8 = programClass7[ca];
                        if (fixedClassesExclusionSet1 == null || !fixedClassesExclusionSet1.isMatchedClass(programClass8)) {
                            if (stringEncryptionExclusionSpec1 != null) {
                                ProgramClass programClass15 = programClass8;
                                if (stringEncryptionExclusionSpec1.isClassExcluded(programClass15)) {
                                    continue;
                                }
                            }

                            ScriptEnvironment scriptEnvironment9 = scriptEnvironment1;
                            ClassRepository classRepository9 = classRepository1;
                            CommonSuperTypeResolver commonSuperTypeResolver7 = commonSuperTypeResolver1;
                            programClass8.collectEncryptableStrings(
                                    multiMapTable,
                                    pairMultiMap1,
                                    listMultimap,
                                    stringEncryptionExclusionSpec1,
                                    false,
                                    commonSuperTypeResolver7,
                                    classRepository9,
                                    scriptEnvironment9
                            );
                            if (programClass8.hasVersionedVariants()) {
                                Iterator iterator3 = programClass8.getVersionedVariants().iterator();

                                while (iterator3.hasNext()) {
                                    ClassFileBase classFileBase3 = (ClassFileBase) iterator3.next();
                                    ProgramClass programClass46 = (ProgramClass) classFileBase3;
                                    ScriptEnvironment scriptEnvironment10 = scriptEnvironment1;
                                    ClassRepository classRepository10 = classRepository1;
                                    CommonSuperTypeResolver commonSuperTypeResolver8 = commonSuperTypeResolver1;
                                    programClass46.collectEncryptableStrings(
                                            multiMapTable,
                                            pairMultiMap1,
                                            listMultimap,
                                            stringEncryptionExclusionSpec1,
                                            false,
                                            commonSuperTypeResolver8,
                                            classRepository10,
                                            scriptEnvironment10
                                    );
                                }
                            }
                        }
                    }

                    Iterator iterator11 = multiMapTable.entrySet().iterator();

                    while (iterator11.hasNext()) {
                        Entry entry2 = (Entry) iterator11.next();
                        Iterator iterator12 = ((PairMultiMap) entry2.getValue()).entrySet().iterator();

                        while (iterator12.hasNext()) {
                            Entry entry3 = (Entry) iterator12.next();
                            MethodInfo methodInfo7 = (MethodInfo) entry3.getKey();
                            if (!methodInfo7.isStaticInitializer() && staticInitCalleeAnalyzer1.isCalledBySubclassInitializer(methodInfo7)) {
                                iterator12.remove();
                                if (scriptEnvironment1.isVerbose()) {
                                    String string86 = "String Encryption : Strings in method '"
                                            + methodInfo7.toOriginalDisplayString()
                                            + "' in class '"
                                            + methodInfo7.getOriginalDottedName()
                                            + "' will not be encrypted because the method is called by the static initializer of a super class.";
                                    long dk = 129120864439474L;
                                    String string38 = string86;
                                    scriptEnvironment1.logMessage(string38);
                                }
                            }
                        }
                    }
                }

                MultiMapTable multiMapTable3 = new MultiMapTable(super.programClasses.length);
                PairMultiMap pairMultiMap8 = new PairMultiMap(super.programClasses.length);
                PairMultiMap pairMultiMap9 = new PairMultiMap(super.programClasses.length);
                if (bf != 0) {
                    ProgramClass[] programClass26 = super.programClasses;
                    int cj = programClass26.length;

                    for (int ck = 0; ck < cj; ck += 1) {
                        ProgramClass programClass9 = programClass26[ck];
                        if (fixedClassesExclusionSet1 == null || !fixedClassesExclusionSet1.isMatchedClass(programClass9)) {
                            if (integerEncryptionExclusions5 != null) {
                                ProgramClass programClass16 = programClass9;
                                if (integerEncryptionExclusions5.isClassExcluded(programClass16)) {
                                    continue;
                                }
                            }

                            boolean bl17 = bf == 2;
                            IntegerEncryptionExclusions integerEncryptionExclusions = integerEncryptionExclusions5;
                            PairMultiMap pairMultiMap = pairMultiMap9;
                            Boolean boolean27 = bl17;
                            Boolean boolean26 = false;
                            IntegerEncryptionExclusions integerEncryptionExclusions2 = integerEncryptionExclusions;
                            PairMultiMap pairMultiMap2 = pairMultiMap;
                            programClass9.collectEncryptableIntegers(multiMapTable3, pairMultiMap8, pairMultiMap2, integerEncryptionExclusions2, boolean26, boolean27);
                            if (programClass9.hasVersionedVariants()) {
                                Iterator iterator4 = programClass9.getVersionedVariants().iterator();

                                while (iterator4.hasNext()) {
                                    ClassFileBase classFileBase4 = (ClassFileBase) iterator4.next();
                                    ProgramClass programClass47 = (ProgramClass) classFileBase4;
                                    bl17 = bf == 2;
                                    integerEncryptionExclusions = integerEncryptionExclusions5;
                                    pairMultiMap = pairMultiMap9;
                                    Boolean boolean29 = bl17;
                                    Boolean boolean28 = false;
                                    IntegerEncryptionExclusions integerEncryptionExclusions3 = integerEncryptionExclusions;
                                    PairMultiMap pairMultiMap3 = pairMultiMap;
                                    programClass47.collectEncryptableIntegers(
                                            multiMapTable3, pairMultiMap8, pairMultiMap3, integerEncryptionExclusions3, boolean28, boolean29
                                    );
                                }
                            }
                        }
                    }

                    Iterator iterator13 = multiMapTable3.entrySet().iterator();

                    while (iterator13.hasNext()) {
                        Entry entry4 = (Entry) iterator13.next();
                        Iterator iterator14 = ((PairMultiMap) entry4.getValue()).entrySet().iterator();

                        while (iterator14.hasNext()) {
                            Entry entry5 = (Entry) iterator14.next();
                            MethodInfo methodInfo8 = (MethodInfo) entry5.getKey();
                            if (!methodInfo8.isStaticInitializer() && staticInitCalleeAnalyzer1.isCalledBySubclassInitializer(methodInfo8)) {
                                iterator14.remove();
                                if (scriptEnvironment1.isVerbose()) {
                                    String string87 = "Integer Constant Encryption : Values in method '"
                                            + methodInfo8.toOriginalDisplayString()
                                            + "' in class '"
                                            + methodInfo8.getOriginalDottedName()
                                            + "' will not be encrypted because the method is called by the static initializer of a super class. (A)";
                                    long dl = 129120864439474L;
                                    String string39 = string87;
                                    scriptEnvironment1.logMessage(string39);
                                }
                            }
                        }
                    }
                }

                MultiMapTable multiMapTable4 = new MultiMapTable(super.programClasses.length);
                PairMultiMap pairMultiMap10 = new PairMultiMap(super.programClasses.length);
                PairMultiMap pairMultiMap11 = new PairMultiMap(super.programClasses.length);
                if (bg != 0) {
                    ProgramClass[] programClass27 = super.programClasses;
                    int cl = programClass27.length;

                    for (int cm = 0; cm < cl; cm += 1) {
                        ProgramClass programClass10 = programClass27[cm];
                        if (fixedClassesExclusionSet1 == null || !fixedClassesExclusionSet1.isMatchedClass(programClass10)) {
                            if (longEncryptionExclusionHandler2 != null) {
                                ProgramClass programClass17 = programClass10;
                                if (longEncryptionExclusionHandler2.isClassExcluded(programClass17)) {
                                    continue;
                                }
                            }

                            programClass10.collectEncryptableLongs(multiMapTable4, pairMultiMap10, pairMultiMap11, longEncryptionExclusionHandler2);
                            if (programClass10.hasVersionedVariants()) {
                                Iterator iterator5 = programClass10.getVersionedVariants().iterator();

                                while (iterator5.hasNext()) {
                                    ClassFileBase classFileBase5 = (ClassFileBase) iterator5.next();
                                    ((ProgramClass) classFileBase5)
                                            .collectEncryptableLongs(multiMapTable4, pairMultiMap10, pairMultiMap11, longEncryptionExclusionHandler2);
                                }
                            }
                        }
                    }

                    Iterator iterator15 = multiMapTable4.entrySet().iterator();

                    while (iterator15.hasNext()) {
                        Entry entry6 = (Entry) iterator15.next();
                        Iterator iterator16 = ((PairMultiMap) entry6.getValue()).entrySet().iterator();

                        while (iterator16.hasNext()) {
                            Entry entry7 = (Entry) iterator16.next();
                            MethodInfo methodInfo9 = (MethodInfo) entry7.getKey();
                            if (!methodInfo9.isStaticInitializer() && staticInitCalleeAnalyzer1.isCalledBySubclassInitializer(methodInfo9)) {
                                iterator16.remove();
                                if (scriptEnvironment1.isVerbose()) {
                                    String string88 = "Integer Constant Encryption : Values in method '"
                                            + methodInfo9.toOriginalDisplayString()
                                            + "' in class '"
                                            + methodInfo9.getOriginalDottedName()
                                            + "' will not be encrypted because the method is called by the static initializer of a super class. (A)";
                                    long dm = 129120864439474L;
                                    String string40 = string88;
                                    scriptEnvironment1.logMessage(string40);
                                }
                            }
                        }
                    }
                }

                HashSet hashSet11 = null;
                HashSet hashSet12 = null;
                HashSet hashSet13 = null;
                HashSet hashSet14 = null;
                if (booleanFlag.getValue() && methodParameterExclusions5 != null) {
                    methodParameterExclusions5.applyDefaultNameExclusions();
                    hashSet11 = ZkmUtils.createHashSet();
                    hashSet12 = ZkmUtils.createHashSet();
                    hashSet13 = ZkmUtils.createHashSet();
                    hashSet14 = ZkmUtils.createHashSet();
                    boolean bl36 = false;
                    if (nestedMultiMap != null) {
                        Iterator iterator17 = nestedMultiMap.entrySet().iterator();

                        while (iterator17.hasNext()) {
                            Entry entry = (Entry) iterator17.next();
                            Iterator iterator6 = ((ListMultimap) entry.getValue()).keySet().iterator();

                            while (iterator6.hasNext()) {
                                MethodBytecode methodBytecode1 = (MethodBytecode) iterator6.next();
                                bl36 = true;
                                MethodInfo methodInfo1 = (MethodInfo) methodBytecode1.getMethod();
                                if (!methodInfo1.isManufactured()) {
                                    hashSet11.add(methodInfo1);
                                }
                            }
                        }
                    }

                    if (be == 4) {
                        Iterator iterator18 = multiMapTable.entrySet().iterator();

                        while (iterator18.hasNext()) {
                            Entry entry8 = (Entry) iterator18.next();
                            Iterator iterator22 = ((PairMultiMap) entry8.getValue()).keySet().iterator();

                            while (iterator22.hasNext()) {
                                MethodInfo methodInfo10 = (MethodInfo) iterator22.next();
                                bl36 = true;
                                if (!methodInfo10.isManufactured()) {
                                    hashSet11.add(methodInfo10);
                                }
                            }

                            ProgramClass programClass33 = (ProgramClass) entry8.getKey();
                            ProgramClass programClass18 = programClass33;
                            if (StringEncryptor.canUseIndyStrings(programClass18)) {
                                hashSet12.add(programClass33);
                            }
                        }

                        iterator18 = pairMultiMap1.keySet().iterator();

                        while (iterator18.hasNext()) {
                            ProgramClass programClass30 = (ProgramClass) iterator18.next();
                            long da = 84583614461798L;
                            ProgramClass programClass19 = programClass30;
                            if (StringEncryptor.canUseIndyStrings(programClass19)) {
                                hashSet12.add(programClass30);
                            }
                        }
                    }

                    if (bf != 0) {
                        Iterator iterator19 = multiMapTable3.entrySet().iterator();

                        while (iterator19.hasNext()) {
                            Entry entry9 = (Entry) iterator19.next();
                            Iterator iterator23 = ((PairMultiMap) entry9.getValue()).keySet().iterator();

                            while (iterator23.hasNext()) {
                                MethodInfo methodInfo11 = (MethodInfo) iterator23.next();
                                bl36 = true;
                                if (!methodInfo11.isManufactured()) {
                                    hashSet11.add(methodInfo11);
                                }
                            }

                            ProgramClass programClass34 = (ProgramClass) entry9.getKey();
                            ProgramClass programClass20 = programClass34;
                            if (IntegerConstantEncryptor.canUseIndyEncryption(programClass20)) {
                                hashSet13.add(programClass34);
                            }
                        }

                        iterator19 = pairMultiMap8.keySet().iterator();

                        while (iterator19.hasNext()) {
                            ProgramClass programClass31 = (ProgramClass) iterator19.next();
                            ProgramClass programClass21 = programClass31;
                            if (IntegerConstantEncryptor.canUseIndyEncryption(programClass21)) {
                                hashSet13.add(programClass31);
                            }
                        }
                    }

                    if (bg != 0) {
                        Iterator iterator20 = multiMapTable4.entrySet().iterator();

                        while (iterator20.hasNext()) {
                            Entry entry10 = (Entry) iterator20.next();
                            Iterator iterator24 = ((PairMultiMap) entry10.getValue()).keySet().iterator();

                            while (iterator24.hasNext()) {
                                MethodInfo methodInfo12 = (MethodInfo) iterator24.next();
                                bl36 = true;
                                if (!methodInfo12.isManufactured()) {
                                    hashSet11.add(methodInfo12);
                                }
                            }

                            ProgramClass programClass35 = (ProgramClass) entry10.getKey();
                            ProgramClass programClass22 = programClass35;
                            if (LongConstantEncryptor.isLongEncryptionEnabled(programClass22)) {
                                hashSet14.add(programClass35);
                            }
                        }

                        iterator20 = pairMultiMap10.keySet().iterator();

                        while (iterator20.hasNext()) {
                            ProgramClass programClass32 = (ProgramClass) iterator20.next();
                            long db = 113615182408173L;
                            ProgramClass programClass23 = programClass32;
                            if (LongConstantEncryptor.isLongEncryptionEnabled(programClass23)) {
                                hashSet14.add(programClass32);
                            }
                        }
                    }

                    if (bo == 3) {
                        ProgramClass[] programClass28 = super.programClasses;
                        int co = programClass28.length;

                        for (int cq = 0; cq < co; cq += 1) {
                            ProgramClass programClass36 = programClass28[cq];
                            if (fixedClassesExclusionSet1 == null || !fixedClassesExclusionSet1.isMatchedClass(programClass36)) {
                                programClass36.collectParameterChangeFlowCandidates(hashSet11, flowObfuscationExclusions, methodParameterExclusions5);
                            }
                        }
                    }

                    boolean bl39;
                    if (flowObfuscationManager4 != null) {
                        if (!flowObfuscationManager4.isAdvancedModeEnabled() && !flowObfuscationManager4.hasNonZeroValuedField()) {
                            bl39 = HiddenOptionFlags.CHECK_REPEATED_PARAMETER_CHANGES;
                        } else {
                            ScriptEnvironment scriptEnvironment11 = scriptEnvironment1;
                            MutableInt mutableInt1 = mutableInt4;
                            MethodParameterExclusions methodParameterExclusions2 = methodParameterExclusions5;
                            BooleanFlag booleanFlag3 = booleanFlag;
                            ReferenceObfuscationExclusions referenceObfuscationExclusions3 = referenceObfuscationExclusions;
                            if (this.canObfuscateReferences(
                                    referenceObfuscationExclusions3, booleanFlag3, methodParameterExclusions2, mutableInt1, scriptEnvironment11
                            )) {
                                Set set3 = flowObfuscationManager4.getAllPairFields();
                                Iterator iterator21 = set3.iterator();

                                while (iterator21.hasNext()) {
                                    OpaquePredicateField opaquePredicateField = (OpaquePredicateField) iterator21.next();
                                    ProgramClass programClass37 = (ProgramClass) opaquePredicateField.getOwnerClass();
                                    if (programClass37.isProgramClass()) {
                                        MethodSignature methodSignature1 = MethodSignature.STATIC_INITIALIZER;
                                        MethodInfo methodInfo13 = classRepository1.findDeclaredMethod(programClass37, methodSignature1);
                                        if (methodInfo13 == null) {
                                            ArrayList arrayList1 = new ArrayList();
                                            Integer integer16 = 2;
                                            ClassRepository classRepository11 = classRepository1;
                                            ArrayList arrayList3 = arrayList1;
                                            methodInfo13 = programClass37.getOrCreateStaticInitializer(arrayList3, classRepository11, "Flow Obfuscation", integer16);
                                            programClass37.addPoolConstants(arrayList1);
                                            set2.add(methodInfo13);
                                        }

                                        hashSet11.add(methodInfo13);
                                    }
                                }

                                bl39 = HiddenOptionFlags.CHECK_REPEATED_PARAMETER_CHANGES;
                            } else {
                                bl39 = HiddenOptionFlags.CHECK_REPEATED_PARAMETER_CHANGES;
                            }
                        }
                    } else {
                        bl39 = HiddenOptionFlags.CHECK_REPEATED_PARAMETER_CHANGES;
                    }

                    if (bl39) {
                        ProgramClass[] programClass29 = super.programClasses;
                        int cp = programClass29.length;

                        for (int cr = 0; cr < cp; cr += 1) {
                            ProgramClass programClass38 = programClass29[cr];
                            MethodInfo[] methodInfos = programClass38.getMethodInfos();
                            int cs = methodInfos.length;

                            for (int cb = 0; cb < cs; cb += 1) {
                                MethodInfo methodInfo2 = methodInfos[cb];
                                if (!methodInfo2.isManufactured()) {
                                    hashSet11.add(methodInfo2);
                                }
                            }
                        }
                    }

                    if (hashSet11 == null || hashSet11.size() == 0) {
                        String string89 = "Method Parameter List Changing : 'methodParameterChanges' is not 'none' but no method parameter lists "
                                + (bl36 ? "available" : "required")
                                + " to be changed.";
                        long dn = 129120864439474L;
                        String string41 = string89;
                        scriptEnvironment1.logMessage(string41);
                    }
                }

                classRepository1.recordNameChanges(classFileBases5);
                int cn = readOnlyMultiMapView.getKeyCount();
                SetMultiMap setMultiMap9 = new SetMultiMap(ZkmUtils.getPrimeCapacity(cn));
                SetMultiMap setMultiMap10 = new SetMultiMap(ZkmUtils.getPrimeCapacity(cn));
                SetMultiMap setMultiMap2 = setMultiMap10;
                SetMultiMap setMultiMap1 = setMultiMap9;
                ReadOnlyMultiMapView readOnlyMultiMapView1 = readOnlyMultiMapView;
                ReadOnlyMultiMapView readOnlyMultiMapView4 = LookupClassFactory.filterModuleArchives(readOnlyMultiMapView1, setMultiMap1, setMultiMap2);
                Map map34 = null;
                Map map35 = null;
                HashMap hashMap43 = null;
                MethodParameterChanger methodParameterChanger10 = null;
                HashSet hashSet15 = ZkmUtils.createHashSet();
                if (hashSet11 != null && hashSet11.size() > 0) {
                    if (map33 == null) {
                        map33 = ZkmUtils.createHashMap();
                    }

                    MethodOverrideAnalyzer methodOverrideAnalyzer1 = new MethodOverrideAnalyzer(
                            classRepository1, super.classHierarchy, super.classResolver, scriptEnvironment1, super.programClasses.length, bl4, bl33, true
                    );
                    int ec = super.programClasses.length;
                    MethodOverrideAnalyzer methodOverrideAnalyzer3 = methodOverrideAnalyzer1;
                    ChangeLogMapping changeLogMapping12 = changeLogMapping1;
                    Integer integer17 = ec;
                    map33 = MethodParameterChanger.assignMethodKeys(set2, map33, bl5, integer17, changeLogMapping12, methodOverrideAnalyzer3);
                    hashMap43 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(map33.size()));
                    Set set1 = flowObfuscationManager4 == null ? null : flowObfuscationManager4.getFieldOwnerClasses();
                    if (list26 == null && string9 != null && string9.length() > 0) {
                        ScriptEnvironment scriptEnvironment28 = scriptEnvironment1;
                        String string68 = "newClassNameFile";
                        String string71 = string9;
                        ScriptEnvironment scriptEnvironment12 = scriptEnvironment28;
                        String string43 = string68;
                        String string42 = string71;
                        list26 = readNameListFile(string42, string43, scriptEnvironment12);
                    }

                    MethodParameterChangeSet methodParameterChangeSet;
                    Random random1;
                    label2128:
                    {
                        methodParameterChanger10 = new MethodParameterChanger(
                                map33,
                                multiMapTable.keys(),
                                pairMultiMap1.keys(),
                                multiMapTable3.keys(),
                                pairMultiMap8.keys(),
                                multiMapTable4.keys(),
                                pairMultiMap10.keys(),
                                setMultiMap8,
                                super.programClasses.length,
                                readOnlyMultiMapView4,
                                string2,
                                mixedCaseNamesMode1,
                                bl13,
                                mutableInt4.getValue(),
                                mutableInt5.getValue(),
                                processingStatistics1.meetsFullSizeThreshold(),
                                changeLogMapping1,
                                classRepository1,
                                classRepository1.getOriginalToCurrentNameMap(),
                                classRepository1.getMethodSignatureChanges(),
                                set1,
                                staticInitCalleeAnalyzer1,
                                classInitOrderHandler1,
                                list26,
                                super.classpathClassLoader,
                                bl5,
                                scriptEnvironment1
                        );
                        boolean bl50 = bd != 0;
                        ScriptEnvironment scriptEnvironment29 = scriptEnvironment1;
                        boolean bl28 = bl50;
                        BooleanFlag booleanFlag6 = booleanFlag;
                        Map map31 = map4;
                        SetValuedMap setValuedMap3 = setValuedMap2;
                        String string73 = string7;
                        Map map32 = map3;
                        ObservableHolder observableHolder4 = observableHolder3;
                        TwoKeyMap twoKeyMap = twoKeyMap13;
                        TwoKeyMap twoKeyMap1 = twoKeyMap12;
                        HashMap hashMap = hashMap42;
                        HashSet hashSet = hashSet15;
                        HashMap hashMap1 = hashMap43;
                        Map map5 = map33;
                        boolean bl19 = bl5;
                        MethodOverrideAnalyzer methodOverrideAnalyzer = methodOverrideAnalyzer1;
                        ScriptEnvironment scriptEnvironment3 = scriptEnvironment29;
                        boolean bl23 = bl28;
                        BooleanFlag booleanFlag1 = booleanFlag6;
                        Map map6 = map31;
                        SetValuedMap setValuedMap4 = setValuedMap3;
                        String string13 = string73;
                        Map map7 = map32;
                        ObservableHolder observableHolder8 = observableHolder4;
                        TwoKeyMap twoKeyMap4 = twoKeyMap;
                        TwoKeyMap twoKeyMap5 = twoKeyMap1;
                        HashMap hashMap3 = hashMap;
                        HashSet hashSet1 = hashSet;
                        HashMap hashMap4 = hashMap1;
                        Map map8 = map5;
                        boolean bl24 = bl19;
                        MethodOverrideAnalyzer methodOverrideAnalyzer2 = methodOverrideAnalyzer;
                        boolean bl25 = bl4;
                        int cc = ba;
                        CommonSuperTypeResolver commonSuperTypeResolver2 = commonSuperTypeResolver1;
                        ClassHierarchy classHierarchy2 = super.classHierarchy;
                        ProgramClass[] programClass12 = super.programClasses;
                        ClassRepository classRepository3 = classRepository1;
                        boolean bl26 = bl;
                        ChangeLogMapping changeLogMapping2 = changeLogMapping1;
                        FixedClassesExclusionSet fixedClassesExclusionSet2 = fixedClassesExclusionSet1;
                        MethodParameterExclusions methodParameterExclusions = methodParameterExclusions5;
                        methodParameterChangeSet = new MethodParameterChangeSet(
                                methodParameterExclusions,
                                fixedClassesExclusionSet2,
                                changeLogMapping2,
                                bl26,
                                classRepository3,
                                programClass12,
                                classHierarchy2,
                                commonSuperTypeResolver2,
                                cc,
                                bl25,
                                methodOverrideAnalyzer2,
                                bl24,
                                map8,
                                hashMap4,
                                hashSet1,
                                hashMap3,
                                twoKeyMap5,
                                twoKeyMap4,
                                observableHolder8,
                                map7,
                                string13,
                                setValuedMap4,
                                map6,
                                booleanFlag1,
                                bl23,
                                scriptEnvironment3
                        );
                        byte dc;
                        if (!bl5) {
                            if (!HiddenOptionFlags.RANDOMIZE_OBFUSCATION) {
                                random1 = ZkmUtils.createSeededRandom(super.programClasses.length);
                                random1.nextBoolean();
                                break label2128;
                            }

                            dc = 93;
                        } else {
                            dc = 93;
                        }

                        random1 = ZkmUtils.createRandom(dc);
                    }

                    bl41 = bo == 2 || bo == 3;
                    Random random2 = random1;
                    boolean bl27 = bl41;
                    MethodParameterChangeSet methodParameterChangeSet1 = methodParameterChangeSet;
                    ParameterListGenerator parameterListGenerator = new ParameterListGenerator(methodParameterChangeSet1, bl27, random2);
                    ObservableHolder observableHolder7 = new ObservableHolder();
                    FlowObfuscationManager flowObfuscationManager5 = processingStatistics1.meetsBasicSizeThreshold() ? flowObfuscationManager4 : null;
                    bl16 = processingStatistics1.meetsFullSizeThreshold();
                    ObservableHolder observableHolder19 = observableHolder7;
                    MethodParameterChanger methodParameterChanger9 = methodParameterChanger10;
                    FlowObfuscationManager flowObfuscationManager3 = flowObfuscationManager5;
                    InheritedMemberAnalyzer inheritedMemberAnalyzer10 = inheritedMemberAnalyzer11;
                    ParameterListGenerator parameterListGenerator2 = parameterListGenerator;
                    HashSet hashSet9 = hashSet10;
                    ListMultimap listMultimap3 = listMultimap4;
                    Boolean boolean30 = bl16;
                    ObservableHolder observableHolder15 = observableHolder19;
                    MethodParameterChanger methodParameterChanger = methodParameterChanger9;
                    FlowObfuscationManager flowObfuscationManager2 = flowObfuscationManager3;
                    InheritedMemberAnalyzer inheritedMemberAnalyzer1 = inheritedMemberAnalyzer10;
                    ParameterListGenerator parameterListGenerator1 = parameterListGenerator2;
                    HashSet hashSet2 = hashSet9;
                    ListMultimap listMultimap2 = listMultimap3;
                    map34 = methodParameterChangeSet.changeParameterLists(
                            hashSet11,
                            hashSet12,
                            hashSet13,
                            hashSet14,
                            listMultimap2,
                            hashSet2,
                            parameterListGenerator1,
                            inheritedMemberAnalyzer1,
                            flowObfuscationManager2,
                            methodParameterChanger,
                            observableHolder15,
                            boolean30
                    );
                    map35 = methodParameterChangeSet.getInitNodeMap();
                    if (!observableHolder7.isValueNull()) {
                        super.programClasses = (ProgramClass[]) observableHolder7.getValue();
                    }

                    if (!booleanFlag.getValue()) {
                        methodParameterChanger10 = null;
                    } else {
                        classRepository1.recordMethodSignatureChanges(classFileBases5);
                    }
                }

                if (bd != 0) {
                    SetMultiMap setMultiMap11 = new SetMultiMap();
                    ProgramClass[] programClass39 = super.programClasses;
                    int ct = programClass39.length;

                    for (int cu = 0; cu < ct; cu += 1) {
                        ProgramClass programClass40 = programClass39[cu];
                        if (programClass40.hasVersionedVariants()) {
                            Iterator iterator26 = programClass40.getVersionedVariants().iterator();

                            while (iterator26.hasNext()) {
                                ClassFileBase classFileBase6 = (ClassFileBase) iterator26.next();
                                if (classFileBase6.isProgramClass()) {
                                    ProgramClass programClass48 = (ProgramClass) classFileBase6;
                                    HashSet hashSet17 = ZkmUtils.createHashSet();
                                    long dy = 78565326089416L;
                                    HashSet hashSet3 = hashSet17;
                                    programClass48.collectCallGraph(setMultiMap11, hashSet3);
                                }
                            }
                        }
                    }

                    Iterator iterator25 = setMultiMap11.entrySet().iterator();

                    while (iterator25.hasNext()) {
                        Entry entry11 = (Entry) iterator25.next();
                        listMultimap4.appendValues(entry11.getKey(), (Collection) entry11.getValue());
                    }

                    Map map36 = (Map) observableHolder22.getValue();
                    ListMultimap listMultimap5 = new ListMultimap(super.programClasses.length, 5, HiddenOptionFlags.USE_PARALLEL);
                    NestedMultiMap nestedMultiMap6 = new NestedMultiMap(super.programClasses.length, HiddenOptionFlags.USE_PARALLEL);
                    PrintWriter printWriter4 = scriptEnvironment1.getLogWriter();
                    Iterator iterator27 = set2.iterator();

                    while (iterator27.hasNext()) {
                        MethodInfo methodInfo14 = (MethodInfo) iterator27.next();
                        AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) ZkmUtils.mapOrSelf(methodInfo14, map36);
                        if (!methodInfo14.isNameChanged()) {
                            MethodInfo methodInfo6 = methodInfo14;
                            if (methodParameterExclusions6.isMethodExcluded(methodInfo6) && methodInfo14.isRenamed()) {
                                StringBuilder stringBuilder = new StringBuilder()
                                        .append("\tEXcluded from Method Parameter Obfuscation \"")
                                        .append(AbstractExclusionSpec.formatMethodSignature(methodInfo14, 85284247280692L))
                                        .append("\" in class \"");
                                ProgramClass programClass51 = methodInfo14.getOwnerProgramClass();
                                Boolean boolean34 = false;
                                printWriter4.println(
                                        stringBuilder.append(AbstractExclusionSpec.formatClass(programClass51, (ClassHierarchyQuery) null, boolean34))
                                                .append("\" because its name or the name of its containing class have been obfuscated.")
                                                .toString()
                                );
                            }
                        } else if (!abstractMethodInfo.hadMethodParameters()) {
                            listMultimap5.addValue(methodInfo14.getOwnerProgramClass(), methodInfo14);
                            List list27 = listMultimap4.getValues(methodInfo14);
                            if (list27 != null) {
                                Iterator iterator7 = list27.iterator();

                                while (iterator7.hasNext()) {
                                    MethodInfo methodInfo3 = (MethodInfo) iterator7.next();
                                    nestedMultiMap6.addValue(methodInfo3.getOwnerProgramClass(), methodInfo3, methodInfo14);
                                }
                            }
                        } else {
                            Boolean boolean31 = false;
                            methodInfo14.setNameChanged(boolean31);
                            StringBuilder stringBuilder1 = new StringBuilder()
                                    .append("\tEXcluded from Method Parameter Obfuscation \"")
                                    .append(AbstractExclusionSpec.formatMethodSignature(methodInfo14, 85284247280692L))
                                    .append("\" in class \"");
                            ProgramClass programClass52 = methodInfo14.getOwnerProgramClass();
                            Boolean boolean32 = false;
                            StringBuilder stringBuilder2 = stringBuilder1.append(
                                            AbstractExclusionSpec.formatClass(programClass52, (ClassHierarchyQuery) null, boolean32)
                                    )
                                    .append("\" because of the presence of a ")
                                    .append("MethodParameters")
                                    .append(" attribute.");
                            String string90;
                            if (methodInfo14 != abstractMethodInfo) {
                                StringBuilder stringBuilder4 = new StringBuilder()
                                        .append(" ")
                                        .append(AbstractExclusionSpec.formatMethodSignature(abstractMethodInfo, 85284247280692L))
                                        .append("\" in class \"");
                                ProgramClass programClass54 = methodInfo14.getOwnerProgramClass();
                                Boolean boolean33 = false;
                                string90 = stringBuilder4.append(AbstractExclusionSpec.formatClass(programClass54, (ClassHierarchyQuery) null, boolean33)).toString();
                            } else {
                                string90 = "";
                            }

                            printWriter4.println(stringBuilder2.append(string90).toString());
                        }
                    }

                    if (!listMultimap5.isEmpty()) {
                        new MethodParameterObfuscator(
                                super.programClasses,
                                classRepository1,
                                listMultimap5,
                                nestedMultiMap6,
                                hashMap42,
                                twoKeyMap12,
                                twoKeyMap13,
                                hashMap43,
                                hashSet10,
                                inheritedMemberAnalyzer11,
                                super.classResolver,
                                scriptEnvironment1,
                                processingStatistics1.meetsBasicSizeThreshold()
                        );
                        classRepository1.indexMethods();
                        classRepository1.recordMethodSignatureChanges(classFileBases5);
                    }
                }

                if (listMultimap4 != null) {
                    listMultimap4.clear();
                }

                if (set2 != null) {
                    set2.clear();
                }

                boolean bl37 = false;
                if (bk != 0) {
                    if (bk == 1) {
                        observableHolder.setValue("Performing light exception obfuscation");
                    } else if (bk == 2) {
                        observableHolder.setValue("Performing heavy exception obfuscation");
                        bl37 = true;
                    }

                    List list38;
                    Map map37;
                    if (flowObfuscationManager4 != null) {
                        list38 = flowObfuscationManager4.getGroups();
                        map37 = flowObfuscationManager4.getGroupsByClass();
                    } else {
                        ObservableHolder observableHolder23 = new ObservableHolder();
                        programClass45 = super.programClasses;
                        EnumerableMap enumerableMap1 = classRepository1.getOriginalToCurrentNameMap();
                        long dz = 39080577441246L;
                        ObservableHolder observableHolder16 = observableHolder23;
                        Boolean boolean35 = bl7;
                        ClassRepository classRepository12 = classRepository1;
                        ChangeLogMapping changeLogMapping13 = changeLogMapping1;
                        GroupingsSpec groupingsSpec1 = groupingsSpec2;
                        ScriptEnvironment scriptEnvironment13 = scriptEnvironment1;
                        EnumerableMap enumerableMap = enumerableMap1;
                        list38 = FlowObfuscationManager.buildFlowGroups(
                                programClass45,
                                classFileBases5,
                                enumerableMap,
                                scriptEnvironment13,
                                groupingsSpec1,
                                changeLogMapping13,
                                classRepository12,
                                boolean35,
                                observableHolder16
                        );
                        map37 = (Map) observableHolder23.getValue();
                    }

                    if (list38 != null && map37 != null) {
                        NestedMultiMap nestedMultiMap7 = null;
                        MutableInt mutableInt2 = mutableInt4;
                        MethodParameterExclusions methodParameterExclusions3 = methodParameterExclusions5;
                        BooleanFlag booleanFlag4 = booleanFlag;
                        ReferenceObfuscationExclusions referenceObfuscationExclusions4 = referenceObfuscationExclusions;
                        if (this.canObfuscateReferences(referenceObfuscationExclusions4, booleanFlag4, methodParameterExclusions3, mutableInt2, scriptEnvironment1)) {
                            nestedMultiMap7 = new NestedMultiMap();
                        }

                        this.obfuscateFlowAndExceptions(
                                list38,
                                map37,
                                fixedClassesExclusionSet1,
                                flowObfuscationExclusions,
                                exceptionObfuscationExclusions,
                                inheritedMemberAnalyzer11,
                                hashSet10,
                                nestedMultiMap7,
                                hashMap43,
                                commonSuperTypeResolver1,
                                classRepository1,
                                scriptEnvironment1,
                                bl37,
                                bl12,
                                processingStatistics1
                        );
                        if (nestedMultiMap7 != null && !nestedMultiMap7.isEmpty()) {
                            String string77 = "Exception Obfuscation";
                            String string44 = string77;
                            ReferenceObfuscationExclusions referenceObfuscationExclusions5 = referenceObfuscationExclusions;
                            this.addReferenceObfuscationExclusions(nestedMultiMap7, nestedMultiMap, referenceObfuscationExclusions5, string44);
                            nestedMultiMap7.clearAll();
                        }
                    }
                }

                HashSet hashSet16 = null;
                if (be != 0) {
                    observableHolder.setValue("Encrypting strings");
                    if (methodParameterChanger10 != null && changeLogMapping1 != null && changeLogMapping1.hasParameterChangeData()) {
                        Map map38 = changeLogMapping1.getParameterChangeKeysByClass();
                        Map map17 = map38;
                        methodParameterChanger10.setClassKeys(map17);
                    }

                    hashSet16 = ZkmUtils.createHashSet();
                    switch (be) {
                        case 1:
                            StringEncryptor stringEncryptor3 = new StringEncryptor(false, false);
                            boolean bl45 = processingStatistics1.meetsFullSizeThreshold();
                            ScriptEnvironment scriptEnvironment14 = scriptEnvironment1;
                            Boolean boolean37 = bl45;
                            CommonSuperTypeResolver commonSuperTypeResolver9 = commonSuperTypeResolver1;
                            HashMap hashMap26 = hashMap43;
                            Map map18 = map33;
                            MethodParameterChanger methodParameterChanger1 = methodParameterChanger10;
                            HashMap hashMap25 = hashMap2;
                            InheritedMemberAnalyzer inheritedMemberAnalyzer2 = inheritedMemberAnalyzer11;
                            HashSet hashSet4 = hashSet16;
                            Boolean boolean36 = false;
                            this.encryptStrings(
                                    stringEncryptionExclusionSpec1,
                                    multiMapTable,
                                    pairMultiMap1,
                                    listMultimap,
                                    stringEncryptor3,
                                    false,
                                    boolean36,
                                    hashSet4,
                                    inheritedMemberAnalyzer2,
                                    hashMap25,
                                    methodParameterChanger1,
                                    map18,
                                    hashMap26,
                                    commonSuperTypeResolver9,
                                    boolean37,
                                    scriptEnvironment14
                            );
                            break;
                        case 2:
                            StringEncryptor stringEncryptor2 = new StringEncryptor(false, false);
                            boolean bl44 = processingStatistics1.meetsFullSizeThreshold();
                            ScriptEnvironment scriptEnvironment15 = scriptEnvironment1;
                            Boolean boolean39 = bl44;
                            CommonSuperTypeResolver commonSuperTypeResolver10 = commonSuperTypeResolver1;
                            HashMap hashMap28 = hashMap43;
                            Map map19 = map33;
                            MethodParameterChanger methodParameterChanger2 = methodParameterChanger10;
                            HashMap hashMap27 = hashMap2;
                            InheritedMemberAnalyzer inheritedMemberAnalyzer3 = inheritedMemberAnalyzer11;
                            HashSet hashSet5 = hashSet16;
                            Boolean boolean38 = false;
                            this.encryptStrings(
                                    stringEncryptionExclusionSpec1,
                                    multiMapTable,
                                    pairMultiMap1,
                                    listMultimap,
                                    stringEncryptor2,
                                    true,
                                    boolean38,
                                    hashSet5,
                                    inheritedMemberAnalyzer3,
                                    hashMap27,
                                    methodParameterChanger2,
                                    map19,
                                    hashMap28,
                                    commonSuperTypeResolver10,
                                    boolean39,
                                    scriptEnvironment15
                            );
                            break;
                        case 3:
                            StringEncryptor stringEncryptor1 = new StringEncryptor(true, false);
                            boolean bl43 = processingStatistics1.meetsFullSizeThreshold();
                            ScriptEnvironment scriptEnvironment16 = scriptEnvironment1;
                            Boolean boolean41 = bl43;
                            CommonSuperTypeResolver commonSuperTypeResolver11 = commonSuperTypeResolver1;
                            HashMap hashMap30 = hashMap43;
                            Map map20 = map33;
                            MethodParameterChanger methodParameterChanger3 = methodParameterChanger10;
                            HashMap hashMap29 = hashMap2;
                            InheritedMemberAnalyzer inheritedMemberAnalyzer4 = inheritedMemberAnalyzer11;
                            HashSet hashSet6 = hashSet16;
                            Boolean boolean40 = false;
                            this.encryptStrings(
                                    stringEncryptionExclusionSpec1,
                                    multiMapTable,
                                    pairMultiMap1,
                                    listMultimap,
                                    stringEncryptor1,
                                    true,
                                    boolean40,
                                    hashSet6,
                                    inheritedMemberAnalyzer4,
                                    hashMap29,
                                    methodParameterChanger3,
                                    map20,
                                    hashMap30,
                                    commonSuperTypeResolver11,
                                    boolean41,
                                    scriptEnvironment16
                            );
                            break;
                        case 4:
                            StringEncryptor stringEncryptor = new StringEncryptor(true, true);
                            boolean bl48 = processingStatistics1.meetsFullSizeThreshold();
                            ScriptEnvironment scriptEnvironment17 = scriptEnvironment1;
                            Boolean boolean43 = bl48;
                            CommonSuperTypeResolver commonSuperTypeResolver12 = commonSuperTypeResolver1;
                            HashMap hashMap32 = hashMap43;
                            Map map21 = map33;
                            MethodParameterChanger methodParameterChanger4 = methodParameterChanger10;
                            HashMap hashMap31 = hashMap2;
                            InheritedMemberAnalyzer inheritedMemberAnalyzer5 = inheritedMemberAnalyzer11;
                            HashSet hashSet7 = hashSet16;
                            Boolean boolean42 = true;
                            this.encryptStrings(
                                    stringEncryptionExclusionSpec1,
                                    multiMapTable,
                                    pairMultiMap1,
                                    listMultimap,
                                    stringEncryptor,
                                    true,
                                    boolean42,
                                    hashSet7,
                                    inheritedMemberAnalyzer5,
                                    hashMap31,
                                    methodParameterChanger4,
                                    map21,
                                    hashMap32,
                                    commonSuperTypeResolver12,
                                    boolean43,
                                    scriptEnvironment17
                            );
                    }
                }

                multiMapTable.clear();
                pairMultiMap1.clear();
                listMultimap.clear();
                if (bf != 0) {
                    observableHolder.setValue("Encrypting integer constants");
                    if (methodParameterChanger10 != null && changeLogMapping1 != null && changeLogMapping1.hasParameterChangeData()) {
                        Map map39 = changeLogMapping1.getParameterChangeKeysByClass();
                        Map map22 = map39;
                        methodParameterChanger10.setClassKeys(map22);
                    }

                    IntegerConstantEncryptor integerConstantEncryptor1 = new IntegerConstantEncryptor();
                    processingStatistics1.meetsFullSizeThreshold();
                    ScriptEnvironment scriptEnvironment18 = scriptEnvironment1;
                    CommonSuperTypeResolver commonSuperTypeResolver13 = commonSuperTypeResolver1;
                    HashMap hashMap33 = hashMap43;
                    Map map23 = map33;
                    MethodParameterChanger methodParameterChanger5 = methodParameterChanger10;
                    ScriptEnvironment scriptEnvironment24 = scriptEnvironment18;
                    CommonSuperTypeResolver commonSuperTypeResolver20 = commonSuperTypeResolver13;
                    HashMap hashMap37 = hashMap33;
                    Map map29 = map23;
                    MethodParameterChanger methodParameterChanger7 = methodParameterChanger5;
                    InheritedMemberAnalyzer inheritedMemberAnalyzer8 = inheritedMemberAnalyzer11;
                    IntegerConstantEncryptor integerConstantEncryptor = integerConstantEncryptor1;
                    PairMultiMap pairMultiMap4 = pairMultiMap9;
                    PairMultiMap pairMultiMap5 = pairMultiMap8;
                    MultiMapTable multiMapTable1 = multiMapTable3;
                    IntegerEncryptionExclusions integerEncryptionExclusions4 = integerEncryptionExclusions5;
                    this.encryptIntegerConstants(
                            integerEncryptionExclusions4,
                            multiMapTable1,
                            pairMultiMap5,
                            pairMultiMap4,
                            integerConstantEncryptor,
                            inheritedMemberAnalyzer8,
                            methodParameterChanger7,
                            map29,
                            hashMap37,
                            commonSuperTypeResolver20,
                            scriptEnvironment24
                    );
                }

                if (bg != 0) {
                    observableHolder.setValue("Encrypting long constants");
                    if (methodParameterChanger10 != null && changeLogMapping1 != null && changeLogMapping1.hasParameterChangeData()) {
                        Map map40 = changeLogMapping1.getParameterChangeKeysByClass();
                        Map map24 = map40;
                        methodParameterChanger10.setClassKeys(map24);
                    }

                    LongConstantEncryptor longConstantEncryptor1 = new LongConstantEncryptor();
                    processingStatistics1.meetsFullSizeThreshold();
                    ScriptEnvironment scriptEnvironment19 = scriptEnvironment1;
                    CommonSuperTypeResolver commonSuperTypeResolver14 = commonSuperTypeResolver1;
                    HashMap hashMap34 = hashMap43;
                    Map map25 = map33;
                    MethodParameterChanger methodParameterChanger6 = methodParameterChanger10;
                    InheritedMemberAnalyzer inheritedMemberAnalyzer6 = inheritedMemberAnalyzer11;
                    ScriptEnvironment scriptEnvironment25 = scriptEnvironment19;
                    CommonSuperTypeResolver commonSuperTypeResolver21 = commonSuperTypeResolver14;
                    HashMap hashMap38 = hashMap34;
                    Map map30 = map25;
                    MethodParameterChanger methodParameterChanger8 = methodParameterChanger6;
                    InheritedMemberAnalyzer inheritedMemberAnalyzer9 = inheritedMemberAnalyzer6;
                    LongConstantEncryptor longConstantEncryptor = longConstantEncryptor1;
                    PairMultiMap pairMultiMap6 = pairMultiMap11;
                    PairMultiMap pairMultiMap7 = pairMultiMap10;
                    MultiMapTable multiMapTable2 = multiMapTable4;
                    LongEncryptionExclusionHandler longEncryptionExclusionHandler1 = longEncryptionExclusionHandler2;
                    this.encryptLongConstants(
                            longEncryptionExclusionHandler1,
                            multiMapTable2,
                            pairMultiMap7,
                            pairMultiMap6,
                            longConstantEncryptor,
                            inheritedMemberAnalyzer9,
                            methodParameterChanger8,
                            map30,
                            hashMap38,
                            commonSuperTypeResolver21,
                            scriptEnvironment25
                    );
                }

                if (bm != 0) {
                    if (!setValuedMap.isEmpty()) {
                        scriptEnvironment1.logSeriousError_v("Auto Reflection Handling : Cannot perform autoReflection handling more than once on the same classes.");
                    } else {
                        label2057:
                        if (hashMap2.size() == 0) {
                            boolean bl40;
                            if (nameExclusionSet6.hasNoIncludedPackages()) {
                                if (nameExclusionSet6.hasNoIncludedClasses()) {
                                    if (nameExclusionSet6.hasNoIncludedFields()) {
                                        if (nameExclusionSet6.hasNoIncludedMethods()) {
                                            scriptEnvironment1.logMessage(
                                                    "Auto Reflection Handling : Will not perform automatic Reflection API handling because no packages, classes, fields or methods have been renamed."
                                            );
                                            break label2057;
                                        }

                                        bl40 = super.verbose;
                                    } else {
                                        bl40 = super.verbose;
                                    }
                                } else {
                                    bl40 = super.verbose;
                                }
                            } else {
                                bl40 = super.verbose;
                            }

                            if (bl40) {
                                if (list16 != null && list16.size() != 0) {
                                    scriptEnvironment1.logMessage(
                                            "Auto Reflection Handling : There is an 'accessedByReflection' statement in effect so only classes, fields and methods specified by that statement are eligible for AutoReflection mapping."
                                    );
                                } else {
                                    scriptEnvironment1.logMessage(
                                            "Auto Reflection Handling : There is no 'accessedByReflection' statement in effect so all classes, fields and methods are by default eligible for AutoReflection mapping."
                                    );
                                }
                            }

                            MethodKeyInjector methodKeyInjector3 = null;
                            if (HiddenOptionFlags.AUTO_REFLECTION_METHOD_KEYS
                                    && !HiddenOptionFlags.NO_AUTO_REFLECTION_METHOD_KEYS
                                    && methodParameterChanger10 != null
                                    && methodParameterChanger10.hasHelperTriples()
                                    && readOnlyMultiMapView4.isEmpty()) {
                                methodKeyInjector3 = new MethodKeyInjector(
                                        methodParameterChanger10, (ObjectTriple) observableHolder3.getValue(), map3, classRepository1, super.classResolver, random7
                                );
                            }

                            if (list26 == null && string9 != null && string9.length() > 0) {
                                ScriptEnvironment scriptEnvironment30 = scriptEnvironment1;
                                String string69 = "newClassNameFile";
                                String string72 = string9;
                                ScriptEnvironment scriptEnvironment20 = scriptEnvironment30;
                                String string46 = string69;
                                String string45 = string72;
                                list26 = readNameListFile(string45, string46, scriptEnvironment20);
                            }

                            ListMultimap listMultimap7 = classRepository1.getReflectionCallSites();
                            boolean bl42 = list16 != null && list16.size() > 0;
                            boolean bl47 = list17 != null && list17.size() > 0;
                            int ei = mutableInt4.getValue();
                            int ej = mutableInt5.getValue();
                            boolean bl49 = processingStatistics1.meetsFullSizeThreshold();
                            ScriptEnvironment scriptEnvironment31 = scriptEnvironment1;
                            List list36 = list26;
                            FixedClassesExclusionSet fixedClassesExclusionSet13 = fixedClassesExclusionSet1;
                            MethodKeyInjector methodKeyInjector2 = methodKeyInjector3;
                            boolean bl29 = bl49;
                            int cf = ej;
                            int cg = ei;
                            boolean bl30 = bl13;
                            String string74 = string2;
                            boolean bl31 = bl5;
                            String string75 = string5;
                            boolean bl18 = bl47;
                            boolean bl32 = bl42;
                            ReflectionAccessMatcher reflectionAccessMatcher4 = reflectionAccessMatcher;
                            ScriptEnvironment scriptEnvironment21 = scriptEnvironment31;
                            List list35 = list36;
                            FixedClassesExclusionSet fixedClassesExclusionSet10 = fixedClassesExclusionSet13;
                            MethodKeyInjector methodKeyInjector = methodKeyInjector2;
                            Boolean boolean48 = bl29;
                            Integer integer19 = cf;
                            Integer integer18 = cg;
                            Boolean boolean47 = bl30;
                            String string49 = string74;
                            Boolean boolean46 = bl31;
                            String string48 = string75;
                            Boolean boolean45 = bl18;
                            Boolean boolean44 = bl32;
                            ReflectionAccessMatcher reflectionAccessMatcher3 = reflectionAccessMatcher4;
                            String string47 = string4;
                            ChangeLogMapping changeLogMapping14 = changeLogMapping1;
                            ObservableHolder observableHolder17 = observableHolder2;
                            SetValuedMap setValuedMap5 = setValuedMap;
                            Map map26 = map1;
                            SetMultiMap setMultiMap4 = setMultiMap10;
                            SetMultiMap setMultiMap3 = setMultiMap9;
                            ReadOnlyMultiMapView readOnlyMultiMapView2 = readOnlyMultiMapView4;
                            ClassRepository classRepository13 = classRepository1;
                            CommonSuperTypeResolver commonSuperTypeResolver15 = commonSuperTypeResolver1;
                            this.applyAutoReflectionHandling(
                                    listMultimap7,
                                    commonSuperTypeResolver15,
                                    classRepository13,
                                    readOnlyMultiMapView2,
                                    setMultiMap3,
                                    setMultiMap4,
                                    map26,
                                    setValuedMap5,
                                    observableHolder17,
                                    changeLogMapping14,
                                    string47,
                                    reflectionAccessMatcher3,
                                    boolean44,
                                    boolean45,
                                    string48,
                                    boolean46,
                                    string49,
                                    boolean47,
                                    integer18,
                                    integer19,
                                    boolean48,
                                    methodKeyInjector,
                                    fixedClassesExclusionSet10,
                                    list35,
                                    scriptEnvironment21
                            );
                        } else {
                            scriptEnvironment1.logMessage(
                                    "Auto Reflection Handling : Will not perform automatic Reflection API handling because CLDC classes are present."
                            );
                        }
                    }
                }

                if (flowObfuscationManager4 != null && (flowObfuscationManager4.isAdvancedModeEnabled() || flowObfuscationManager4.hasNonZeroValuedField())) {
                    NestedMultiMap nestedMultiMap8 = null;
                    MutableInt mutableInt3 = mutableInt4;
                    MethodParameterExclusions methodParameterExclusions4 = methodParameterExclusions5;
                    BooleanFlag booleanFlag5 = booleanFlag;
                    ReferenceObfuscationExclusions referenceObfuscationExclusions6 = referenceObfuscationExclusions;
                    if (this.canObfuscateReferences(referenceObfuscationExclusions6, booleanFlag5, methodParameterExclusions4, mutableInt3, scriptEnvironment1)) {
                        nestedMultiMap8 = new NestedMultiMap();
                    }

                    Set set4 = flowObfuscationManager4.getAllPairFields();
                    Iterator iterator28 = set4.iterator();

                    while (iterator28.hasNext()) {
                        OpaquePredicateField opaquePredicateField1 = (OpaquePredicateField) iterator28.next();
                        ClassFileBase classFileBase8 = opaquePredicateField1.getOwnerClass();
                        if (classFileBase8.isProgramClass()) {
                            ProgramClass programClass49 = (ProgramClass) classFileBase8;
                            Random random4 = flowObfuscationManager4.getRandom();
                            StaticInitCalleeAnalyzer staticInitCalleeAnalyzer3 = staticInitCalleeAnalyzer1;
                            ClassRepository classRepository14 = classRepository1;
                            CommonSuperTypeResolver commonSuperTypeResolver16 = commonSuperTypeResolver1;
                            programClass49.addOpaquePredicateInitialization(
                                    opaquePredicateField1, nestedMultiMap8, commonSuperTypeResolver16, classRepository14, staticInitCalleeAnalyzer3, random4
                            );
                        }
                    }

                    if (nestedMultiMap8 != null && !nestedMultiMap8.isEmpty()) {
                        String string78 = "Flow Obfuscation";
                        String string50 = string78;
                        ReferenceObfuscationExclusions referenceObfuscationExclusions7 = referenceObfuscationExclusions;
                        this.addReferenceObfuscationExclusions(nestedMultiMap8, nestedMultiMap, referenceObfuscationExclusions7, string50);
                        nestedMultiMap8.clearAll();
                    }
                }

                if (referenceObfuscationExclusions != null
                        && (
                        !referenceObfuscationExclusions.hasNoMatchedClasses()
                                || !referenceObfuscationExclusions.hasNoMatchedMethods()
                                || !referenceObfuscationExclusions.hasNoMatchedFields()
                )) {
                    if (!setValuedMap1.isEmpty()) {
                        scriptEnvironment1.logSeriousError_v("Reference Obfuscation : Cannot perform Reference Obfuscation more than once on the same classes.");
                    } else if (!scriptEnvironment1.hasMicroEditionClasses()) {
                        MethodKeyInjector methodKeyInjector4 = null;
                        if (HiddenOptionFlags.REFERENCE_OBFUSCATION_METHOD_KEYS
                                && !HiddenOptionFlags.NO_REFERENCE_METHOD_KEYS
                                && methodParameterChanger10 != null
                                && methodParameterChanger10.hasHelperTriples()
                                && readOnlyMultiMapView4.isEmpty()) {
                            methodKeyInjector4 = new MethodKeyInjector(
                                    methodParameterChanger10, (ObjectTriple) observableHolder3.getValue(), map3, classRepository1, super.classResolver, random7
                            );
                        }

                        int ed = mutableInt4.getValue();
                        int ee = mutableInt5.getValue();
                        boolean bl46 = processingStatistics1.meetsFullSizeThreshold();
                        ScriptEnvironment scriptEnvironment22 = scriptEnvironment1;
                        Boolean boolean50 = bl46;
                        CommonSuperTypeResolver commonSuperTypeResolver17 = commonSuperTypeResolver1;
                        InheritedMemberAnalyzer inheritedMemberAnalyzer7 = inheritedMemberAnalyzer11;
                        FixedClassesExclusionSet fixedClassesExclusionSet11 = fixedClassesExclusionSet1;
                        ClassRepository classRepository15 = classRepository1;
                        HashMap hashMap35 = hashMap43;
                        Map map28 = map33;
                        MethodKeyInjector methodKeyInjector1 = methodKeyInjector4;
                        ReferenceObfuscator referenceObfuscator1 = referenceObfuscator;
                        NestedMultiMap nestedMultiMap3 = nestedMultiMap;
                        SetValuedMap setValuedMap6 = setValuedMap1;
                        Map map27 = map2;
                        ObservableHolder observableHolder18 = observableHolder1;
                        SetMultiMap setMultiMap6 = setMultiMap10;
                        SetMultiMap setMultiMap5 = setMultiMap9;
                        ReadOnlyMultiMapView readOnlyMultiMapView3 = readOnlyMultiMapView4;
                        Boolean boolean49 = bl13;
                        String string52 = string6;
                        String string51 = string2;
                        ChangeLogMapping changeLogMapping15 = changeLogMapping1;
                        Integer integer21 = ee;
                        Integer integer20 = ed;
                        this.obfuscateReferences(
                                referenceObfuscationExclusions,
                                bl9,
                                integer20,
                                integer21,
                                changeLogMapping15,
                                string51,
                                string52,
                                boolean49,
                                readOnlyMultiMapView3,
                                setMultiMap5,
                                setMultiMap6,
                                observableHolder18,
                                map27,
                                setValuedMap6,
                                nestedMultiMap3,
                                referenceObfuscator1,
                                methodKeyInjector1,
                                map28,
                                hashMap35,
                                classRepository15,
                                fixedClassesExclusionSet11,
                                inheritedMemberAnalyzer7,
                                commonSuperTypeResolver17,
                                boolean50,
                                scriptEnvironment22
                        );
                    } else {
                        scriptEnvironment1.logMessage("Reference Obfuscation : Will not perform obfuscation of references because CLDC classes are present.");
                    }
                }

                if (booleanFlag.getValue() && map34 != null && !map34.isEmpty() && bo == 3) {
                    AbstractMap abstractMap = HiddenOptionFlags.USE_PARALLEL ? new ConcurrentHashMap() : ZkmUtils.createHashMap();
                    byte cw;
                    if (!HiddenOptionFlags.SINGLE_REFERENCE_PASS) {
                        cw = 3;
                    } else {
                        cw = 1;
                    }

                    ArrayList arrayList5 = new ArrayList(super.programClasses.length + 5);
                    int cy = 0;
                    int dd = 0;

                    for (ProgramClass[] programClass50 = super.programClasses; dd < programClass50.length; programClass50 = super.programClasses) {
                        ProgramClass programClass43 = super.programClasses[cy];
                        arrayList5.add(programClass43);
                        if (programClass43.hasVersionedVariants()) {
                            Iterator iterator8 = programClass43.getVersionedVariants().iterator();

                            while (iterator8.hasNext()) {
                                ClassFileBase classFileBase7 = (ClassFileBase) iterator8.next();
                                arrayList5.add((ProgramClass) classFileBase7);
                            }
                        }

                        cy += 1;
                        dd = cy;
                    }

                    ListMultimap listMultimap6;
                    if (HiddenOptionFlags.USE_PARALLEL && HiddenOptionFlags.PROCESSOR_COUNT >= 2) {
                        boolean bl38 = bl33;
                        listMultimap6 = new ListMultimap(true);
                        Vector vector = new Vector();
                        arrayList5.parallelStream()
                                .forEach(
                                        programClass58 -> {
                                            try {
                                                try {
                                                    ((ProgramClass) programClass58).obfuscateFlowForParameterChanges(
                                                            listMultimap6,
                                                            commonSuperTypeResolver1,
                                                            classRepository1,
                                                            abstractMap,
                                                            cw,
                                                            bl38,
                                                            bl12,
                                                            scriptEnvironment1,
                                                            flowObfuscationExclusions,
                                                            random7
                                                    );
                                                } catch (ZkmProcessingException zkmProcessingException1) {
                                                    vector.add(zkmProcessingException1);
                                                }
                                            } catch (Throwable throwable) {
                                                throw ZkmUtils.sneakyThrow(throwable);
                                            }
                                        }
                                );
                        if (!vector.isEmpty()) {
                            throw (ZkmProcessingException) vector.get(0);
                        }
                    } else {
                        listMultimap6 = new ListMultimap();
                        Iterator iterator29 = arrayList5.iterator();

                        while (iterator29.hasNext()) {
                            ProgramClass programClass44 = (ProgramClass) iterator29.next();
                            Random random5 = random7;
                            FlowObfuscationExclusions flowObfuscationExclusions4 = flowObfuscationExclusions;
                            ScriptEnvironment scriptEnvironment23 = scriptEnvironment1;
                            Boolean boolean52 = bl12;
                            Boolean boolean51 = bl33;
                            Integer integer22 = Integer.valueOf(cw);
                            programClass44.obfuscateFlowForParameterChanges(
                                    listMultimap6,
                                    commonSuperTypeResolver1,
                                    classRepository1,
                                    abstractMap,
                                    integer22,
                                    boolean51,
                                    boolean52,
                                    scriptEnvironment23,
                                    flowObfuscationExclusions4,
                                    random5
                            );
                        }
                    }

                    NestedMultiMap nestedMultiMap9 = new NestedMultiMap();
                    Iterator iterator31 = listMultimap6.entrySet().iterator();

                    while (iterator31.hasNext()) {
                        Entry entry12 = (Entry) iterator31.next();
                        MethodBytecode methodBytecode4 = (MethodBytecode) entry12.getKey();
                        com.zelix.klassmaster.classfile.ClassFileBase programClass53 = methodBytecode4.getOwningClass();
                        long dp = 13976386222919L;
                        programClass53 = programClass53;
                        Collection collection1 = (Collection) entry12.getValue();
                        MethodBytecode methodBytecode3 = methodBytecode4;
                        com.zelix.klassmaster.classfile.ClassFileBase programClass24 = programClass53;
                        nestedMultiMap9.addValues(programClass24, methodBytecode3, collection1);
                    }

                    PrintWriter printWriter5 = scriptEnvironment1.getLogWriter();
                    Iterator iterator32 = nestedMultiMap9.entrySet().iterator();

                    while (iterator32.hasNext()) {
                        Entry entry13 = (Entry) iterator32.next();
                        ProgramClass programClass11 = (ProgramClass) entry13.getKey();
                        MethodInfo methodInfo4 = null;
                        Long long1 = null;
                        ListMultimap listMultimap1 = (ListMultimap) entry13.getValue();
                        ArrayList arrayList2 = new ArrayList();
                        Iterator iterator9 = listMultimap1.entrySet().iterator();

                        while (iterator9.hasNext()) {
                            Entry entry1 = (Entry) iterator9.next();
                            MethodBytecode methodBytecode2 = (MethodBytecode) entry1.getKey();
                            MethodInfo methodInfo5 = (MethodInfo) methodBytecode2.getMethod();
                            ChangedMethodDescriptor changedMethodDescriptor = (ChangedMethodDescriptor) map34.get(methodInfo5);
                            Long long2 = (Long) map33.get(methodInfo5);
                            if (changedMethodDescriptor != null) {
                                if (methodInfo4 == null && hashSet15.contains(methodInfo5)) {
                                    methodInfo4 = programClass11.findMethodBySignature(MethodSignature.STATIC_INITIALIZER);
                                    long1 = ((MethodParamChangeNode) map35.get(methodInfo4)).getEffectiveKey();
                                }

                                Long long4 = hashSet15.contains(methodInfo5) ? long1 : null;
                                List list39 = (List) entry1.getValue();
                                ProcessingStatistics processingStatistics2 = processingStatistics1;
                                Random random6 = random7;
                                CommonSuperTypeResolver commonSuperTypeResolver18 = commonSuperTypeResolver1;
                                ClassRepository classRepository16 = classRepository1;
                                methodBytecode2.applyKeyParameterFlowObfuscation(
                                        long2,
                                        long4,
                                        changedMethodDescriptor,
                                        list39,
                                        arrayList2,
                                        abstractMap,
                                        classRepository16,
                                        commonSuperTypeResolver18,
                                        random6,
                                        processingStatistics2
                                );
                                if (scriptEnvironment1.isVerbose()) {
                                    printWriter5.println(
                                            "\tObfuscated flow using new method parameters in method '"
                                                    + methodInfo5.getOriginalNameWithParameters()
                                                    + "' in class '"
                                                    + methodInfo5.getOriginalDottedName()
                                    );
                                }
                            }
                        }

                        if (!arrayList2.isEmpty()) {
                            programClass11.addPoolConstants(arrayList2);
                        }
                    }
                }

                if (bl11) {
                    ProgramClass[] programClass41 = super.programClasses;
                    int cv = programClass41.length;

                    for (int cx = 0; cx < cv; cx += 1) {
                        ProgramClass programClass42 = programClass41[cx];
                        if (fixedClassesExclusionSet1 != null && !fixedClassesExclusionSet1.isMatchedClass(programClass42)) {
                        }

                        if (programClass42.isMultiRelease()) {
                            Iterator iterator30 = programClass42.getAllVersions().iterator();

                            while (iterator30.hasNext()) {
                                ClassFileBase classFileBase9 = (ClassFileBase) iterator30.next();
                                ((ProgramClass) classFileBase9).makePublicAndNonFinal(scriptEnvironment1);
                            }
                        } else {
                            programClass42.makePublicAndNonFinal(scriptEnvironment1);
                        }
                    }
                }

                if (scriptEnvironment1.getSeriousErrorCount() > seriousErrorCount) {
                    String string99 = scriptEnvironment1.getSeriousErrorsText();
                    ea = 81764775538649L;
                    String string53 = string99;
                    messageReporter1.reportFatalErrorWithDetail("FATAL ERROR:", "Serious Errors detected during obfuscation (G).", string53);
                    Thread.yield();
                } else {
                    Thread.yield();
                }

                if (printWriter != null) {
                    observableHolder.setValue("Writing change log");
                }

                ChangeLogInputFile[] changeLogInputFiles2 = changeLogMapping1 != null ? changeLogInputFiles : null;
                ProgramClass programClass57 = (ProgramClass) observableHolder2.getValue();
                ProgramClass programClass55 = (ProgramClass) observableHolder1.getValue();
                ObjectTriple objectTriple1 = (ObjectTriple) observableHolder3.getValue();
                IntegerCache integerCache2 = super.integerCache;
                Enumeration enumeration1 = classRepository1.enumerateModuleInfoClasses();
                new ArrayEnumeration(super.programClasses);
                Enumeration enumeration = enumeration1;
                IntegerCache integerCache1 = integerCache2;
                ChangeLogMapping changeLogMapping3 = changeLogMapping1;
                Map map9 = map3;
                ObjectTriple objectTriple = objectTriple1;
                Map map10 = map33;
                Map map11 = map35;
                Map map12 = map34;
                Map map13 = map2;
                ProgramClass programClass13 = programClass55;
                Map map14 = map1;
                ProgramClass programClass14 = programClass57;
                FlowObfuscationManager flowObfuscationManager = flowObfuscationManager4;
                HashMap hashMap5 = hashMap40;
                NestedMultiMap nestedMultiMap1 = nestedMultiMap5;
                TwoKeyMap twoKeyMap6 = twoKeyMap13;
                TwoKeyMap twoKeyMap7 = twoKeyMap3;
                HashMap hashMap6 = hashMap42;
                HashMap hashMap7 = hashMap41;
                HashMap hashMap8 = hashMap39;
                String string14 = string1;
                String string15 = string;
                PrintWriter printWriter1 = printWriter;
                ChangeLogInputFile[] changeLogInputFiles1 = changeLogInputFiles2;
                new ChangeLogWriter(
                        changeLogInputFiles1,
                        printWriter1,
                        string15,
                        string14,
                        hashMap8,
                        hashMap7,
                        hashMap6,
                        twoKeyMap7,
                        twoKeyMap6,
                        nestedMultiMap1,
                        hashMap5,
                        flowObfuscationManager,
                        programClass14,
                        map14,
                        programClass13,
                        map13,
                        map12,
                        map11,
                        map10,
                        objectTriple,
                        map9,
                        changeLogMapping3,
                        integerCache1,
                        enumeration,
                        bl4
                );
                if (super.verbose && bl10) {
                    scriptEnvironment1.getLogWriter().println("\tStackMap algorithm=" + HiddenOptionFlags.STACK_MAP_ALGORITHM);
                }

                if (bl10) {
                    FixedClassesExclusionSet fixedClassesExclusionSet12 = fixedClassesExclusionSet1;
                    CommonSuperTypeResolver commonSuperTypeResolver19 = commonSuperTypeResolver1;
                    this.computeStackMapFrames(commonSuperTypeResolver19, fixedClassesExclusionSet12);
                }

                if (bl10) {
                    if (!scriptEnvironment1.isMidpPreverificationSkipped() && hashMap2.size() > 0) {
                        this.updatePreverification(hashMap2, commonSuperTypeResolver1, scriptEnvironment1);
                    }
                } else {
                    this.removeStackMapTables();
                }

                scriptEnvironment1.setStackMapUpdateSkipped(!bl10);
                if (bl34) {
                    this.updateLocalVariableTypes(commonSuperTypeResolver1, scriptEnvironment1);
                }

                commonSuperTypeResolver1.clearCache();
                classRepository1.clearHierarchyCaches();
                if (hashSet16 != null) {
                    HashSet hashSet8 = hashSet16;
                    classRepository1.removeNameStringRefs(hashSet8);
                }

                classRepository1.analyzeMethodStacks(commonSuperTypeResolver1, scriptEnvironment1);
                observableHolder.setValue("Reporting messages...");
                HashMap hashMap36 = hashMap39;
                classRepository1.applyPackageRenamesToResources(hashMap36, messageReporter1);
                observableHolder.setValue(" ");
            } catch (MissingClassException missingClassException) {
                String string93 = missingClassException.getMessage();
                long dt = 37549368807840L;
                String string54 = string93;
                String string58 = string54;
                String string59 = "FILE ERROR:";
                messageReporter1.reportSeriousError(string59, string58);
            } catch (MethodAnalysisException methodAnalysisException) {
                ArrayList arrayList = new ArrayList();
                if (bc != 0) {
                    arrayList.add("Flow Obfuscation");
                }

                if (bk != 0) {
                    arrayList.add("Exception Obfuscation");
                }

                if (be != 0) {
                    arrayList.add("String Encryption");
                }

                if (bf != 0) {
                    arrayList.add("Integer Constant Encryption");
                }

                if (bg != 0) {
                    arrayList.add("Long Constant Encryption");
                }

                if ((be == 4 || bf != 0 || bg != 0 || bn != 0 || bo == 3) && booleanFlag.getValue()) {
                    arrayList.add("Method Parameter List Changing");
                }

                if (bd != 0) {
                    arrayList.add("Method Parameter Obfuscation");
                }

                StringBuilder stringBuilder3 = new StringBuilder()
                        .append("Method '")
                        .append(methodAnalysisException.getMethodName())
                        .append("' could not be analyzed (F). ");
                String string92;
                if (arrayList.isEmpty()) {
                    string92 = "Consider excluding it.";
                } else {
                    StringBuilder stringBuilder5 = new StringBuilder().append("Consider excluding it from ");
                    ArrayList arrayList4 = arrayList;
                    string92 = stringBuilder5.append(ZkmUtils.toEnglishList(arrayList4)).append(".").toString();
                }

                string92 = stringBuilder3.append(string92).toString();
                long ds = 37549368807840L;
                String string55 = string92;
                String string60 = string55;
                String string61 = "SERIOUS ERROR:";
                messageReporter1.reportSeriousError(string61, string60);
            } catch (ZkmProcessingException zkmProcessingException) {
                String string91 = zkmProcessingException.getMessage();
                long dq = 37549368807840L;
                String string56 = string91;
                String string62 = string56;
                String string63 = "SERIOUS ERROR:";
                messageReporter1.reportSeriousError(string63, string62);
            } catch (StackAnalysisException stackAnalysisException) {
                String string100 = stackAnalysisException.getMessage();
                long eg = 37549368807840L;
                String string57 = string100;
                String string64 = string57;
                String string65 = "SERIOUS ERROR:";
                messageReporter1.reportSeriousError(string65, string64);
            } finally {
                dialogCallback1.onDialogCancelled();
                ClassHierarchyNode.setCheckHierarchyGaps(false);
            }
        }
    }

    public void computeStackMapFrames(CommonSuperTypeResolver commonSuperTypeResolver1, FixedClassesExclusionSet fixedClassesExclusionSet1) throws ZkmException, IOException {
        ProgramClass[] programClass3;
        if (HiddenOptionFlags.USE_PARALLEL) {
            if (HiddenOptionFlags.PROCESSOR_COUNT >= 2) {
                ArrayList arrayList = new ArrayList(super.programClasses.length);

                for (ProgramClass programClass1 : super.programClasses) {
                    if (programClass1.supportsJava6() && (fixedClassesExclusionSet1 == null || !fixedClassesExclusionSet1.isMatchedClass(programClass1))) {
                        arrayList.add(programClass1);
                        if (programClass1.hasVersionedVariants()) {
                            Iterator iterator = programClass1.getVersionedVariants().iterator();

                            while (iterator.hasNext()) {
                                ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                                arrayList.add((ProgramClass) classFileBase);
                            }
                        }
                    }
                }

                Vector vector = new Vector();
                arrayList.parallelStream().forEach(programClass4 -> {
                    try {
                        try {
                            ((ProgramClass) programClass4).rebuildStackMapTables(commonSuperTypeResolver1, super.classRepository);
                        } catch (ZkmProcessingException zkmProcessingException) {
                            vector.add(zkmProcessingException);
                        }
                    } catch (Throwable throwable) {
                        throw ZkmUtils.sneakyThrow(throwable);
                    }
                });
                if (!vector.isEmpty()) {
                    throw (ZkmProcessingException) vector.get(0);
                }

                return;
            }

            programClass3 = super.programClasses;
        } else {
            programClass3 = super.programClasses;
        }

        for (ProgramClass programClass2 : programClass3) {
            if (programClass2.supportsJava6() && (fixedClassesExclusionSet1 == null || !fixedClassesExclusionSet1.isMatchedClass(programClass2))) {
                programClass2.rebuildStackMapTables(commonSuperTypeResolver1, super.classRepository);
                if (programClass2.hasVersionedVariants()) {
                    Iterator iterator1 = programClass2.getVersionedVariants().iterator();

                    while (iterator1.hasNext()) {
                        ClassFileBase classFileBase1 = (ClassFileBase) iterator1.next();
                        ((ProgramClass) classFileBase1).rebuildStackMapTables(commonSuperTypeResolver1, super.classRepository);
                    }
                }
            }
        }
    }

    public long m(String string) {
        char[] ba = string.toCharArray();
        char[] bb = new char[ba.length];

        for (int i = 0; i < ba.length; i++) {
            char bd = ba[i];
            if (bd >= '0' && bd <= '9') {
                if (bd > '0') {
                    bd = (char) (':' - bd + 48);
                }
            } else if (bd >= 'A' && bd <= 'J') {
                bd = (char) (bd - 17);
            } else if (bd >= 'K' && bd <= 'T') {
                bd = (char) (bd - 27);
            } else if (bd >= 'U' && bd <= 'Z') {
                bd = (char) (bd - '%');
            } else if (bd >= 'a' && bd <= 'd') {
                bd = (char) (bd - '+');
            } else if (bd >= 'e' && bd <= 'n') {
                bd = (char) (bd - '5');
            } else if (bd >= 'o' && bd <= 'x') {
                bd = (char) (bd - '?');
            }

            bb[i] = bd;
        }

        Class<Long> class1 = Long.class;
        Class[] class2 = new Class[]{String.class};
        long be = 0L;

        try {
            Method method1 = class1.getMethod(xorStrings(LicenseCheckBase.getEncodedParseLong(), LicenseCheckBase.getXorKey()), class2);
            Object[] objects = new Object[]{new String(bb)};
            be = (Long) method1.invoke(null, objects);
        } catch (AssertionFailedException assertionFailedException) {
            throw assertionFailedException;
        } catch (Exception exception) {
        }

        return be;
    }

    public SetMultiMap insertOpaquePredicates(
            int ba,
            ScriptEnvironment scriptEnvironment1,
            FlowObfuscationExclusions flowObfuscationExclusions,
            ClassInitOrderHandler classInitOrderHandler1,
            FlowObfuscationManager flowObfuscationManager,
            NameExclusionSet nameExclusionSet,
            StaticInitCalleeAnalyzer staticInitCalleeAnalyzer1,
            NestedMultiMap nestedMultiMap,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            Random random1,
            boolean bl,
            boolean bl1
    ) throws ZkmException, IOException {
        HashSet hashSet = ZkmUtils.createHashSet();
        int bb = 0;
        int bf = 0;

        for (ProgramClass[] programClass6 = super.programClasses; bf < programClass6.length; programClass6 = super.programClasses) {
            hashSet.add(super.programClasses[bb].getPackagePath());
            bf = ++bb;
        }

        bb = hashSet.size();
        SetMultiMap setMultiMap = flowObfuscationManager.assignPredicateFields(
                hashSet, nameExclusionSet, super.classHierarchy, inheritedMemberAnalyzer, super.classRepository, flowObfuscationExclusions, classInitOrderHandler1
        );
        if (flowObfuscationManager.getGroupFieldCount() == 0) {
            scriptEnvironment1.logWarning("Couldn't obfuscate control flow. All candidate 'intersection' classes may be interfaces, non-public or excluded.");
            return null;
        }

        ProgramClass[] programClass4;
        if (flowObfuscationManager.getGroupFieldCount() < flowObfuscationManager.getGroupCount()) {
            List list1 = flowObfuscationManager.getGroupingNamesWithoutField();
            StringBuffer stringBuffer = new StringBuffer();
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                stringBuffer.append("'" + (String) iterator.next() + "'");
                if (iterator.hasNext()) {
                    stringBuffer.append(", ");
                }
            }

            scriptEnvironment1.logWarning(
                    "Couldn't obfuscate control flow in some or all of the classes in the groupings "
                            + stringBuffer
                            + ". All candidate 'intersection' classes in these groupings may be interfaces, non-public or excluded."
            );
            programClass4 = super.programClasses;
        } else {
            programClass4 = super.programClasses;
        }

        int bd = programClass4.length;
        ListMultimap listMultimap = new ListMultimap(bd, HiddenOptionFlags.USE_PARALLEL);
        ArrayList arrayList = new ArrayList(super.programClasses.length + 5);
        int bc = 0;
        bf = 0;

        for (ProgramClass[] programClass5 = super.programClasses; bf < programClass5.length; programClass5 = super.programClasses) {
            ProgramClass programClass1 = super.programClasses[bc];
            arrayList.add(programClass1);
            if (programClass1.hasVersionedVariants()) {
                Iterator iterator1 = programClass1.getVersionedVariants().iterator();

                while (iterator1.hasNext()) {
                    ClassFileBase classFileBase = (ClassFileBase) iterator1.next();
                    arrayList.add((ProgramClass) classFileBase);
                }
            }

            bf = ++bc;
        }

        AbstractMap abstractMap = HiddenOptionFlags.USE_PARALLEL ? new ConcurrentHashMap() : ZkmUtils.createHashMap();
        if (HiddenOptionFlags.USE_PARALLEL && HiddenOptionFlags.PROCESSOR_COUNT >= 2) {
            Vector vector = new Vector();
            arrayList.parallelStream()
                    .forEach(
                            programClass7 -> {
                                try {
                                    try {
                                        FlowFieldPair flowFieldPair2 = flowObfuscationManager.getFieldPair(programClass7);
                                        if (flowFieldPair2 != null && flowFieldPair2.getGroupField() != null) {
                                            Thread.yield();
                                            ((ProgramClass) programClass7).obfuscateFlow(
                                                    listMultimap,
                                                    commonSuperTypeResolver1,
                                                    super.classRepository,
                                                    abstractMap,
                                                    ba,
                                                    bl,
                                                    bl1,
                                                    flowObfuscationManager,
                                                    scriptEnvironment1,
                                                    flowObfuscationExclusions,
                                                    staticInitCalleeAnalyzer1,
                                                    random1
                                            );
                                        }
                                    } catch (ZkmProcessingException zkmProcessingException) {
                                        vector.add(zkmProcessingException);
                                    }
                                } catch (Throwable throwable) {
                                    throw ZkmUtils.sneakyThrow(throwable);
                                }
                            }
                    );
            if (!vector.isEmpty()) {
                throw (ZkmProcessingException) vector.get(0);
            }
        } else {
            Iterator iterator2 = arrayList.iterator();

            while (iterator2.hasNext()) {
                ProgramClass programClass3 = (ProgramClass) iterator2.next();
                FlowFieldPair flowFieldPair1 = flowObfuscationManager.getFieldPair(programClass3);
                if (flowFieldPair1 != null) {
                    OpaquePredicateField opaquePredicateField = flowFieldPair1.getGroupField();
                    if (opaquePredicateField != null) {
                        Thread.yield();
                        programClass3.obfuscateFlow(
                                listMultimap,
                                commonSuperTypeResolver1,
                                super.classRepository,
                                abstractMap,
                                ba,
                                bl,
                                bl1,
                                flowObfuscationManager,
                                scriptEnvironment1,
                                flowObfuscationExclusions,
                                staticInitCalleeAnalyzer1,
                                random1
                        );
                    }
                }
            }
        }

        ListMultimap listMultimap1 = new ListMultimap(bd, 5);

        for (int i = 0; i < bd; i++) {
            super.programClasses[i].collectFlowObfuscationCandidates(listMultimap1);
        }

        HashSet hashSet1 = ZkmUtils.createHashSet(bb);
        HashSet hashSet2 = ZkmUtils.createHashSet(bb);
        boolean bl2;
        if (super.programClasses.length > 1) {
            flowObfuscationManager.selectCallerMethods(hashSet1, hashSet2, listMultimap1, flowObfuscationExclusions, staticInitCalleeAnalyzer1);
            bl2 = HiddenOptionFlags.USE_PARALLEL;
        } else {
            bl2 = HiddenOptionFlags.USE_PARALLEL;
        }

        if (bl2 && HiddenOptionFlags.PROCESSOR_COUNT >= 2) {
            Vector vector1 = new Vector();
            arrayList.parallelStream()
                    .forEach(
                            programClass1x -> {
                                try {
                                    try {
                                        FlowFieldPair flowFieldPairx = flowObfuscationManager.getFieldPair(programClass1x);
                                        if (flowFieldPairx != null) {
                                            OpaquePredicateField opaquePredicateFieldx = flowFieldPairx.getGroupField();
                                            if (opaquePredicateFieldx != null) {
                                                OpaquePredicateField opaquePredicateField1x = flowFieldPairx.getPackageField();
                                                ((ProgramClass) programClass1x).applyOpaquePredicateFlow(
                                                        hashSet1,
                                                        hashSet2,
                                                        opaquePredicateFieldx,
                                                        opaquePredicateField1x,
                                                        nestedMultiMap,
                                                        listMultimap,
                                                        abstractMap,
                                                        super.classRepository,
                                                        commonSuperTypeResolver1,
                                                        super.classResolver,
                                                        random1
                                                );
                                            }
                                        }
                                    } catch (ZkmProcessingException zkmProcessingException) {
                                        vector1.add(zkmProcessingException);
                                    }
                                } catch (Throwable throwable) {
                                    throw ZkmUtils.sneakyThrow(throwable);
                                }
                            }
                    );
            if (!vector1.isEmpty()) {
                throw (ZkmProcessingException) vector1.get(0);
            }
        } else {
            Iterator iterator3 = arrayList.iterator();

            while (iterator3.hasNext()) {
                ProgramClass programClass2 = (ProgramClass) iterator3.next();
                FlowFieldPair flowFieldPair = flowObfuscationManager.getFieldPair(programClass2);
                if (flowFieldPair != null) {
                    OpaquePredicateField opaquePredicateField1 = flowFieldPair.getGroupField();
                    if (opaquePredicateField1 != null) {
                        OpaquePredicateField opaquePredicateField2 = flowFieldPair.getPackageField();
                        programClass2.applyOpaquePredicateFlow(
                                hashSet1,
                                hashSet2,
                                opaquePredicateField1,
                                opaquePredicateField2,
                                nestedMultiMap,
                                listMultimap,
                                abstractMap,
                                super.classRepository,
                                commonSuperTypeResolver1,
                                super.classResolver,
                                random1
                        );
                    }
                }
            }
        }

        listMultimap.clear();
        Iterator iterator4 = abstractMap.entrySet().iterator();

        while (iterator4.hasNext()) {
            Entry entry = (Entry) iterator4.next();
            ((MethodFlowAnalyzer) entry.getValue()).release();
        }

        return setMultiMap;
    }

    public boolean removeDeadInstructions(
            CommonSuperTypeResolver commonSuperTypeResolver1, ClassRepository classRepository1, boolean bl, ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        boolean bl1 = false;
        HashMap hashMap = ZkmUtils.createHashMap();
        IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1 = scriptEnvironment1.getIgnoreMissingReferencesSpec();

        for (ProgramClass programClass1 : super.programClasses) {
            if (this.removeDeadInstructionsInClass(
                    programClass1, hashMap, commonSuperTypeResolver1, classRepository1, bl, scriptEnvironment1, ignoreMissingReferencesSpec1
            )) {
                bl1 = true;
            }

            if (programClass1.hasVersionedVariants()) {
                Iterator iterator = programClass1.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                    if (this.removeDeadInstructionsInClass(
                            (ProgramClass) classFileBase, hashMap, commonSuperTypeResolver1, classRepository1, bl, scriptEnvironment1, ignoreMissingReferencesSpec1
                    )) {
                        bl1 = true;
                    }
                }
            }
        }

        if (!hashMap.isEmpty()) {
            ClassMemberSets classMemberSets = scriptEnvironment1.getTrimExclusionMemberSets();

            for (ProgramClass programClass2 : super.programClasses) {
                if (hashMap.containsKey(programClass2)) {
                    String string = (String) hashMap.get(programClass2);
                    super.programClasses = classRepository1.removeClass(programClass2);
                    if (classMemberSets != null) {
                        classMemberSets.removeClass(programClass2);
                    }

                    scriptEnvironment1.logError(
                            "Class '"
                                    + programClass2.getDisplayLocationName()
                                    + "' will be ignored because 1) it references class '"
                                    + string
                                    + "' that could not be found and 2) that missing class was specified to be ignored and 3) the missing class could not be safely ignored in '"
                                    + programClass2.getOriginalDottedName()
                                    + "'."
                    );
                }
            }
        }

        return bl1;
    }

    public boolean canObfuscateReferences(
            ReferenceObfuscationExclusions referenceObfuscationExclusions,
            BooleanFlag booleanFlag,
            MethodParameterExclusions methodParameterExclusions,
            MutableInt mutableInt,
            ScriptEnvironment scriptEnvironment1
    ) {
        return !HiddenOptionFlags.NO_REFERENCE_PARAMETER_KEYS
                && HiddenOptionFlags.OBFUSCATE_REFERENCES_INDY
                && referenceObfuscationExclusions != null
                && !scriptEnvironment1.hasMicroEditionClasses()
                && booleanFlag.getValue()
                && methodParameterExclusions != null
                && (
                JavaRuntimeVersion.isAtLeastJava8(mutableInt.getValue())
                        || JavaRuntimeVersion.isAtLeastJava7(mutableInt.getValue()) && !HiddenOptionFlags.NO_JAVA7_INDY
        );
    }

    public void removeMethodParameters(boolean bl, boolean bl1) {
        for (ProgramClass programClass1 : super.programClasses) {
            programClass1.trimMethodParametersAttributes(bl, bl1);
            if (programClass1.hasVersionedVariants()) {
                Iterator iterator = programClass1.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ((ProgramClass) ((ClassFileBase) iterator.next())).trimMethodParametersAttributes(bl, bl1);
                }
            }
        }
    }

    public static void collectInheritedMethods(
            ClassFileBase classFileBase, SetMultiMap setMultiMap, boolean bl, Set set1, ClassResolver classResolver1, String string
    ) throws ZkmException, IOException {
        if (set1.add(classFileBase) && !classFileBase.getClassName().equals("java/lang/Object")) {
            String string1 = classFileBase.getSuperclassName();
            ClassFileBase classFileBase1 = classResolver1.getClassFile(string1, string);

            for (AbstractMethodInfo abstractMethodInfo : classFileBase1.getDeclaredMethods()) {
                setMultiMap.addValue(bl ? abstractMethodInfo.getSignature() : abstractMethodInfo.getNameTypeSignature(), abstractMethodInfo);
            }

            collectInheritedMethods(classFileBase1, setMultiMap, bl, set1, classResolver1, string);
            String[] strings = classFileBase.getInterfaceNames();

            for (int i = 0; i < strings.length; i++) {
                String string2 = strings[i];
                ClassFileBase classFileBase2 = classResolver1.getClassFile(string2, string);

                for (AbstractMethodInfo abstractMethodInfo1 : classFileBase2.getDeclaredMethods()) {
                    setMultiMap.addValue(bl ? abstractMethodInfo1.getSignature() : abstractMethodInfo1.getNameTypeSignature(), abstractMethodInfo1);
                }

                collectInheritedMethods(classFileBase2, setMultiMap, bl, set1, classResolver1, string);
            }
        }
    }

    public void removeLocalVariableTables(boolean bl, boolean bl1) {
        for (ProgramClass programClass1 : super.programClasses) {
            Boolean boolean1 = bl1;
            programClass1.trimLocalVariableTables(bl, boolean1);
            if (programClass1.hasVersionedVariants()) {
                Iterator iterator = programClass1.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ((ProgramClass) ((ClassFileBase) iterator.next())).trimLocalVariableTables(bl, bl1);
                }
            }
        }
    }

    public void updatePreverification(HashMap hashMap, CommonSuperTypeResolver commonSuperTypeResolver1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        Iterator iterator = hashMap.keySet().iterator();

        while (iterator.hasNext()) {
            ProgramClass programClass1 = (ProgramClass) iterator.next();
            programClass1.updatePreverification(scriptEnvironment1, commonSuperTypeResolver1, super.classRepository);
            if (programClass1.hasVersionedVariants()) {
                Iterator iterator1 = programClass1.getVersionedVariants().iterator();

                while (iterator1.hasNext()) {
                    ((ProgramClass) ((ClassFileBase) iterator1.next())).updatePreverification(scriptEnvironment1, commonSuperTypeResolver1, super.classRepository);
                }
            }
        }
    }

    public boolean applyAutoReflectionHandling(
            ListMultimap listMultimap,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassRepository classRepository1,
            ReadOnlyMultiMapView readOnlyMultiMapView,
            SetMultiMap setMultiMap,
            SetMultiMap setMultiMap1,
            Map map1,
            SetValuedMap setValuedMap,
            ObservableHolder observableHolder,
            ChangeLogMapping changeLogMapping1,
            String string,
            ReflectionAccessMatcher reflectionAccessMatcher,
            boolean bl,
            boolean bl1,
            String string1,
            Boolean boolean1,
            String string2,
            boolean bl2,
            int ba,
            int bb,
            boolean bl3,
            MethodKeyInjector methodKeyInjector,
            FixedClassesExclusionSet fixedClassesExclusionSet1,
            List list1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        boolean bl4 = false;
        if (listMultimap.isEmpty()) {
            scriptEnvironment1.logMessage("Auto Reflection Handling : No remaining unresolved Reflection API calls found so no processing required or done.");
            return false;
        }

        boolean bl5 = false;
        int bc = super.programClasses.length;
        PrintWriter printWriter = scriptEnvironment1.getLogWriter();

        for (int i = 0; i < bc; i += 1) {
            bl5 = !super.programClasses[i].supportsJava4() || bl5;
        }

        String string3 = bl5 ? "SHA-1" : "SHA-512";
        if (string1 != null && string1.length() > 0) {
            if (super.verbose) {
                printWriter.println("\tAuto Reflection Handling hash algorithm changed from '" + string3 + "' to '" + string1 + "' as specified.");
            }

            string3 = string1;
        }

        HashMap hashMap = ZkmUtils.createHashMap();
        TwoKeyMap twoKeyMap = new TwoKeyMap();
        TwoKeyMap twoKeyMap1 = new TwoKeyMap();
        EnumerableMap enumerableMap = classRepository1.getCumulativeNameMap();
        EnumerableMap enumerableMap1 = classRepository1.getOriginalToCurrentNameMap();
        EnumerableMap enumerableMap2 = classRepository1.getCurrentToOriginalNameMap();
        AutoReflectionHandler autoReflectionHandler = new AutoReflectionHandler(reflectionAccessMatcher, list1, classRepository1, string3, boolean1);
        ReadOnlyMultiMap readOnlyMultiMap = classRepository1.getFieldSignatureChanges();
        ReadOnlyMultiMap readOnlyMultiMap1 = classRepository1.getMethodSignatureChanges();
        autoReflectionHandler.processReflectionCallSites(
                listMultimap,
                enumerableMap1,
                enumerableMap2,
                readOnlyMultiMap,
                readOnlyMultiMap1,
                hashMap,
                twoKeyMap,
                twoKeyMap1,
                bl,
                bl1,
                classRepository1,
                scriptEnvironment1
        );
        if (hashMap.size() > 0 || twoKeyMap.getKeyCount() > 0 || twoKeyMap1.getKeyCount() > 0) {
            HashMap hashMap1 = ZkmUtils.createHashMap();
            HashMap hashMap2 = ZkmUtils.createHashMap();
            HashSet hashSet = ZkmUtils.createHashSet();
            ObservableHolder observableHolder1 = new ObservableHolder();
            Set set1 = autoReflectionHandler.createAutoReflectionClasses(
                    ZkmUtils.toUniqueSet(super.programClasses),
                    observableHolder1,
                    hashMap1,
                    hashMap2,
                    changeLogMapping1,
                    string,
                    setMultiMap,
                    setMultiMap1,
                    readOnlyMultiMapView,
                    enumerableMap,
                    enumerableMap1,
                    enumerableMap2,
                    string2,
                    bl2,
                    super.classpathClassLoader,
                    scriptEnvironment1
            );
            HashMap hashMap3 = ZkmUtils.createHashMap();
            super.programClasses = LookupClassFactory.createLookupClasses(
                    set1,
                    hashMap2,
                    observableHolder1,
                    observableHolder,
                    map1,
                    hashMap3,
                    hashSet,
                    hashMap1,
                    ba,
                    bb,
                    autoReflectionHandler,
                    classRepository1,
                    (LookupClassOption) null
            );
            bc = super.programClasses.length;
            boolean bl6 = hashMap.size() > 0;
            boolean bl7 = twoKeyMap.getKeyCount() > 0;
            boolean bl8 = twoKeyMap1.getKeyCount() > 0;
            ArrayList arrayList = new ArrayList();

            for (int i = 0; i < bc; i += 1) {
                ProgramClass programClass1 = super.programClasses[i];
                if ((fixedClassesExclusionSet1 == null || !fixedClassesExclusionSet1.isMatchedClass(programClass1)) && !programClass1.isGenerated()) {
                    ProgramClass programClass2 = (ProgramClass) hashMap3.get(programClass1);
                    boolean bl9 = programClass1.applyAutoReflectionHandling(
                            listMultimap, programClass2, setValuedMap, bl6, bl7, bl8, commonSuperTypeResolver1, classRepository1, super.verbose, scriptEnvironment1
                    );
                    if (bl9) {
                        arrayList.add(programClass1);
                    }

                    bl4 = bl4 || bl9;
                    if (programClass1.isMultiRelease()) {
                        Iterator iterator = programClass1.getVersionedVariants().iterator();

                        while (iterator.hasNext()) {
                            ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                            ProgramClass programClass3 = (ProgramClass) classFileBase;
                            ProgramClass programClass4 = (ProgramClass) hashMap3.get(programClass3);
                            bl9 = programClass3.applyAutoReflectionHandling(
                                    listMultimap, programClass4, setValuedMap, bl6, bl7, bl8, commonSuperTypeResolver1, classRepository1, super.verbose, scriptEnvironment1
                            );
                            if (bl9) {
                                arrayList.add(programClass3);
                            }

                            bl4 = bl4 || bl9;
                        }
                    }
                }
            }

            if (bl4) {
                HashSet hashSet1 = ZkmUtils.createHashSetFrom(setValuedMap.keySet());
                ListMultimap listMultimap1 = ZkmUtils.invertMultiMap(readOnlyMultiMapView);
                HashSet hashSet2 = ZkmUtils.createHashSet();
                Iterator iterator3 = arrayList.iterator();

                while (iterator3.hasNext()) {
                    ProgramClass programClass7 = (ProgramClass) iterator3.next();
                    if (listMultimap1.containsKey(programClass7)) {
                        Iterator iterator5 = listMultimap1.getValues(programClass7).iterator();

                        while (iterator5.hasNext()) {
                            SourceArchive sourceArchive1 = (SourceArchive) iterator5.next();
                            hashSet2.add(sourceArchive1);
                        }
                    }
                }

                iterator3 = hashSet.iterator();

                while (iterator3.hasNext()) {
                    ProgramClass programClass8 = (ProgramClass) iterator3.next();
                    if (!hashSet1.contains(programClass8)) {
                        super.programClasses = classRepository1.removeClass(programClass8);
                        iterator3.remove();
                    }
                }

                Iterator iterator4 = map1.entrySet().iterator();

                while (iterator4.hasNext()) {
                    Entry entry1 = (Entry) iterator4.next();
                    SourceArchive sourceArchive2 = (SourceArchive) entry1.getKey();
                    if (!hashSet2.contains(sourceArchive2)) {
                        iterator4.remove();
                    }
                }

                if (!observableHolder.isValueNull() && !hashSet1.contains(observableHolder.getValue())) {
                    observableHolder.clearValue();
                }

                Iterator iterator6 = setValuedMap.keySet().iterator();

                while (iterator6.hasNext()) {
                    ProgramClass programClass9 = (ProgramClass) iterator6.next();
                    Set set2 = setValuedMap.getValues(programClass9);
                    Iterator iterator1 = set2.iterator();

                    while (iterator1.hasNext()) {
                        ProgramClass programClass5 = (ProgramClass) iterator1.next();
                        if (!programClass5.isFromArchive() && programClass9.isFromArchive()) {
                            programClass9.markAsFromArchive();
                        }
                    }
                }

                if (scriptEnvironment1.isVerbose()) {
                    String string4 = LookupClassFactory.describeLookupClassesByArchive("Auto Reflection Handling", hashSet, map1);
                    printWriter.print(string4);
                    if (AutoReflectionHandler.MIN_SAFE_GUESS_LENGTH != 11) {
                        printWriter.println(
                                "\tAuto Reflection Handling : Minimum reasonable AutoReflection object name length changed from 11 to "
                                        + AutoReflectionHandler.MIN_SAFE_GUESS_LENGTH
                        );
                    }

                    autoReflectionHandler.printIncludedMappings(enumerableMap2, hashMap, twoKeyMap, twoKeyMap1, scriptEnvironment1.getLogWriter());
                }

                try {
                    Iterator iterator7 = hashSet.iterator();

                    while (iterator7.hasNext()) {
                        ProgramClass programClass10 = (ProgramClass) iterator7.next();
                        AutoReflectionHandler autoReflectionHandler1;
                        HashMap hashMap4;
                        TwoKeyMap twoKeyMap2;
                        TwoKeyMap twoKeyMap3;
                        Set set3;
                        if (methodKeyInjector == null) {
                            autoReflectionHandler1 = autoReflectionHandler;
                            hashMap4 = hashMap;
                            twoKeyMap2 = twoKeyMap;
                            twoKeyMap3 = twoKeyMap1;
                            set3 = enumerableMap2.keySet();
                        } else {
                            SourceArchive sourceArchive3 = null;
                            Iterator iterator10 = map1.entrySet().iterator();

                            while (iterator10.hasNext()) {
                                Entry entry = (Entry) iterator10.next();
                                if (entry.getValue() == programClass10) {
                                    sourceArchive3 = (SourceArchive) entry.getKey();
                                    break;
                                }
                            }

                            methodKeyInjector.setCurrentArchive(sourceArchive3);
                            autoReflectionHandler1 = autoReflectionHandler;
                            hashMap4 = hashMap;
                            twoKeyMap2 = twoKeyMap;
                            twoKeyMap3 = twoKeyMap1;
                            set3 = enumerableMap2.keySet();
                        }

                        int bg = autoReflectionHandler1.populateLookupClass(
                                hashMap4,
                                twoKeyMap2,
                                twoKeyMap3,
                                set3,
                                programClass10,
                                string3,
                                methodKeyInjector,
                                classRepository1,
                                super.classResolver,
                                scriptEnvironment1
                        );
                        int bi = ZkmUtils.getPrimeCapacity(bg);
                        long bh = 86928442684906L;
                        int bf = bi;
                        programClass10.replaceIntegerConstant(bf);
                    }
                } catch (UnsupportedEncodingException unsupportedEncodingException) {
                    throw new ZkmProcessingException("Auto Reflection Handling : UnsupportedEncodingException : " + unsupportedEncodingException.getMessage());
                }

                if (HiddenOptionFlags.AUTO_REFLECTION_METHOD_KEYS) {
                    Iterator iterator8 = hashSet.iterator();

                    while (iterator8.hasNext()) {
                        ProgramClass programClass11 = (ProgramClass) iterator8.next();
                        this.encryptClassStringsForced(
                                programClass11, bl3 && methodKeyInjector == null, methodKeyInjector, commonSuperTypeResolver1, classRepository1, scriptEnvironment1
                        );
                    }
                }

                Iterator iterator9 = hashSet.iterator();

                while (iterator9.hasNext()) {
                    ProgramClass programClass12 = (ProgramClass) iterator9.next();
                    if (programClass12.getClassConstantPool().getEntryCount() > 65535) {
                        programClass12.checkConstantPoolSize(
                                "Auto Reflection Handling",
                                "Consider using ZKM Script 'accessedByReflection' statement to specify only those classes, fields and methods which are actually accessed by Reflection and for which the Reflection access has not been resolved by Zelix KlassMaster."
                        );
                    }
                }
            } else {
                Iterator iterator2 = hashSet.iterator();

                while (iterator2.hasNext()) {
                    ProgramClass programClass6 = (ProgramClass) iterator2.next();
                    super.programClasses = classRepository1.removeClass(programClass6);
                }

                observableHolder.clearValue();
                map1.clear();
                setValuedMap.clear();
                scriptEnvironment1.logMessage(
                        "Auto Reflection Handling : No remaining unresolved Reflection API calls could be processed with the matching renamed classes, fields or methods. No processing done."
                );
            }
        } else {
            scriptEnvironment1.logMessage("Auto Reflection Handling : No matching renamed classes, fields or methods so no processing required or done.");
        }

        return bl4;
    }

    public void removeStackMapTables() {
        for (ProgramClass programClass1 : super.programClasses) {
            programClass1.removeStackMaps();
            if (programClass1.hasVersionedVariants()) {
                Iterator iterator = programClass1.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ((ProgramClass) ((ClassFileBase) iterator.next())).removeStackMaps();
                }
            }
        }
    }

    public long getFileTimestamp(String string) {
        long ba = 0L;
        if (string != null) {
            File file1 = new File(string);
            if (file1.exists()) {
                Class<?> class1 = file1.getClass();
                Class[] class2 = new Class[0];

                try {
                    ba = (Long) class1.getMethod(xorStrings(LicenseCheckBase.getEncodedLastModified(), LicenseCheckBase.getXorKey()), class2).invoke(file1, class2);
                } catch (AssertionFailedException assertionFailedException) {
                    throw assertionFailedException;
                } catch (Exception exception) {
                }
            }
        }

        return ba;
    }

    public void addReferenceObfuscationExclusions(
            NestedMultiMap nestedMultiMap, NestedMultiMap nestedMultiMap1, ReferenceObfuscationExclusions referenceObfuscationExclusions, String string
    ) throws ZkmException, IOException {
        Iterator iterator = nestedMultiMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            ClassMemberRef classMemberRef = (ClassMemberRef) entry.getKey();
            MemberInfo memberInfo1 = classMemberRef.getMember();
            if (memberInfo1.isField()) {
                referenceObfuscationExclusions.includeAllFieldReferences((AbstractFieldInfo) memberInfo1, string);
            } else {
                referenceObfuscationExclusions.includeAllMethodReferences((AbstractMethodInfo) memberInfo1, string);
            }

            Iterator iterator1 = ((ListMultimap) entry.getValue()).entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry1 = (Entry) iterator1.next();
                MethodBytecode methodBytecode1 = (MethodBytecode) entry1.getKey();
                Iterator iterator2 = ((List) entry1.getValue()).iterator();

                while (iterator2.hasNext()) {
                    ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) iterator2.next();
                    nestedMultiMap1.addValue(classMemberRef, methodBytecode1, constantRefInstruction);
                }
            }
        }
    }

    public final void renameFields(
            NameExclusionSet nameExclusionSet,
            ChangeLogMapping changeLogMapping1,
            int ba,
            boolean bl,
            boolean bl1,
            boolean bl2,
            String string,
            boolean bl3,
            HashMap hashMap,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            ClassFileBase[] classFileBases,
            List list1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        FieldRenamer fieldRenamer = new FieldRenamer(
                nameExclusionSet,
                changeLogMapping1,
                super.classRepository,
                super.programClasses,
                classFileBases,
                super.classHierarchy,
                hashMap,
                twoKeyMap,
                twoKeyMap1,
                bl2
        );
        DefaultFieldNameGenerator defaultFieldNameGenerator = new DefaultFieldNameGenerator(ba, bl, bl1, bl2, string, bl3, list1);
        fieldRenamer.renameFields(defaultFieldNameGenerator, scriptEnvironment1);
    }

    public void processLocalVariables(int ba, ObservableHolder observableHolder) throws ZkmException, IOException {
        switch (ba) {
            case 0:
                observableHolder.setValue("Removing local variables");
                this.removeLocalVariables();
            case 1:
            default:
                break;
            case 2:
                observableHolder.setValue("Obfuscating local variables");
                this.obfuscateLocalVariables();
                break;
            case 3:
                observableHolder.setValue("Removing most local variables");
                this.removeLocalVariableTables(true, false);
                break;
            case 4:
                observableHolder.setValue("Removing most local variables");
                this.removeLocalVariableTables(true, true);
                break;
            case 5:
                observableHolder.setValue("Removing most local variables");
                this.removeLocalVariableTables(false, true);
        }
    }

    public void obfuscateLocalVariables() {
        for (ProgramClass programClass1 : super.programClasses) {
            programClass1.obfuscateLocalVariableNames();
            if (programClass1.hasVersionedVariants()) {
                Iterator iterator = programClass1.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ((ProgramClass) ((ClassFileBase) iterator.next())).obfuscateLocalVariableNames();
                }
            }
        }
    }

    public void obfuscateMethodParameters() {
        for (ProgramClass programClass1 : super.programClasses) {
            programClass1.obfuscateMethodParameterNames();
            if (programClass1.hasVersionedVariants()) {
                Iterator iterator = programClass1.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ((ProgramClass) ((ClassFileBase) iterator.next())).obfuscateMethodParameterNames();
                }
            }
        }
    }

    public void removeLineNumbers() {
        for (ProgramClass programClass1 : super.programClasses) {
            programClass1.removeLineNumberTables();
            if (programClass1.hasVersionedVariants()) {
                Iterator iterator = programClass1.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ((ProgramClass) ((ClassFileBase) iterator.next())).removeLineNumberTables();
                }
            }
        }
    }

    public void obfuscateReferencesInHierarchy(
            ClassHierarchyNode classHierarchyNode,
            ProgramClass programClass1,
            boolean bl,
            Map map1,
            TwoKeyMap twoKeyMap,
            int[] ba,
            TwoKeyMap twoKeyMap1,
            NestedMultimapView nestedMultimapView,
            NestedMultiMap nestedMultiMap,
            ListMultimap listMultimap,
            Map map2,
            TwoKeyMap twoKeyMap2,
            BooleanFlag booleanFlag,
            SetValuedMap setValuedMap,
            Map map3,
            Map map4,
            Map map5,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            MutableInt mutableInt2,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            Map map6,
            Map map7,
            ReferenceObfuscator referenceObfuscator,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        ProgramClass programClass2 = (ProgramClass) map1.get(programClass1);
        if (nestedMultimapView.containsKey(programClass1)) {
            ProgramClass programClass3 = bl ? programClass2 : programClass1;
            Map map8 = bl ? twoKeyMap.getInnerMap(programClass2) : twoKeyMap.getInnerMap(programClass1);
            ListMultimap listMultimap1 = nestedMultimapView.copyMultimap(programClass1);
            Map map9 = bl ? twoKeyMap2.getInnerMap(programClass2) : null;
            Map map10 = bl ? twoKeyMap1.getInnerMap(programClass2) : null;
            ResolvedMethodRef resolvedMethodRef = null;
            ResolvedMethodRef resolvedMethodRef1 = null;
            ResolvedMethodRef resolvedMethodRef2 = null;
            if (bl) {
                resolvedMethodRef = (ResolvedMethodRef) ((ObservableHolder) map3.get(programClass2)).getValue();
                resolvedMethodRef1 = (ResolvedMethodRef) ((ObservableHolder) map4.get(programClass2)).getValue();
                resolvedMethodRef2 = (ResolvedMethodRef) ((ObservableHolder) map5.get(programClass2)).getValue();
            }

            try {
                boolean bl1 = programClass1.obfuscateReferences(
                        bl,
                        programClass3,
                        map8,
                        listMultimap1,
                        nestedMultiMap,
                        listMultimap.getValues(programClass1),
                        map2,
                        map9,
                        inheritedMemberAnalyzer,
                        ba,
                        map10,
                        resolvedMethodRef,
                        resolvedMethodRef1,
                        resolvedMethodRef2,
                        mutableInt,
                        mutableInt1,
                        mutableInt2,
                        map6,
                        map7,
                        super.classRepository,
                        commonSuperTypeResolver1,
                        super.classResolver,
                        referenceObfuscator,
                        scriptEnvironment1
                );
                if (bl1) {
                    booleanFlag.setValue(true);
                    if (bl) {
                        setValuedMap.addValue(programClass2, programClass1);
                    }
                }
            } catch (MethodAnalysisException methodAnalysisException) {
                scriptEnvironment1.logMessage(
                        "Couldn't obfuscate references in '" + programClass1.getDisplayLocationName() + "' : \"" + methodAnalysisException.getMessage() + ""
                );
            } catch (StackAnalysisException stackAnalysisException) {
                scriptEnvironment1.logMessage(
                        "Couldn't obfuscate references in '" + programClass1.getDisplayLocationName() + "' : \"" + stackAnalysisException.getMessage() + "\""
                );
            }
        }

        if (!classHierarchyNode.isInterface() && !programClass1.isVersionedVariant()) {
            Enumeration enumeration = classHierarchyNode.enumerateSubclasses();
            if (enumeration != null) {
                while (enumeration.hasMoreElements()) {
                    ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) enumeration.nextElement();
                    this.obfuscateReferencesInHierarchy(
                            classHierarchyNode1,
                            classHierarchyNode1.getProgramClass(),
                            bl,
                            map1,
                            twoKeyMap,
                            ba,
                            twoKeyMap1,
                            nestedMultimapView,
                            nestedMultiMap,
                            listMultimap,
                            map2,
                            twoKeyMap2,
                            booleanFlag,
                            setValuedMap,
                            map3,
                            map4,
                            map5,
                            mutableInt,
                            mutableInt1,
                            mutableInt2,
                            inheritedMemberAnalyzer,
                            commonSuperTypeResolver1,
                            map6,
                            map7,
                            referenceObfuscator,
                            scriptEnvironment1
                    );
                }
            }
        }
    }

    public void encryptClassStringsForced(
            ProgramClass programClass1,
            boolean bl,
            MethodKeyInjector methodKeyInjector,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        this.encryptStringsInClass(
                programClass1, true, programClass1.supportsJava6(), bl, true, methodKeyInjector, commonSuperTypeResolver1, classHierarchyQuery, scriptEnvironment1, 9
        );
    }

    public void scrambleLineNumbers(
            NestedMultiMap nestedMultiMap,
            HashMap hashMap,
            FixedClassesExclusionSet fixedClassesExclusionSet1,
            ChangeLogMapping changeLogMapping1,
            boolean bl,
            ScriptEnvironment scriptEnvironment1
    ) throws IOException {
        Random random1 = ZkmUtils.createRandom(4096);
        int ba = 0;
        int bb = 0;

        for (ProgramClass[] programClass3 = super.programClasses; bb < programClass3.length; programClass3 = super.programClasses) {
            ListMultimap listMultimap = null;
            ProgramClass programClass1 = super.programClasses[ba];
            String string = (String) ZkmUtils.mapOrSelf(programClass1.getClassName(), hashMap);
            if (changeLogMapping1 != null) {
                listMultimap = changeLogMapping1.getLineNumberMappings(string);
            }

            if (fixedClassesExclusionSet1 == null || !fixedClassesExclusionSet1.isMatchedClass(programClass1)) {
                ListMultimap listMultimap2 = programClass1.scrambleLineNumbers(super.integerCache, listMultimap, random1);
                nestedMultiMap.putMultimap(programClass1.getClassName(), listMultimap2);
                if (programClass1.hasVersionedVariants()) {
                    Iterator iterator = programClass1.getVersionedVariants().iterator();

                    while (iterator.hasNext()) {
                        ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                        ((ProgramClass) classFileBase).removeLineNumberTables();
                        scriptEnvironment1.logWarning(
                                "Cannot scramble line numbers for multi-release versions.  Line numbers for version '"
                                        + classFileBase.getReleaseVersion()
                                        + "' of class '"
                                        + programClass1.getOriginalDottedName()
                                        + "' deleted."
                        );
                    }
                }
            } else {
                ProgramClass programClass2;
                IntegerCache integerCache1;
                if (listMultimap != null) {
                    if (!listMultimap.isEmpty()) {
                        if (!scriptEnvironment1.isVerbose() && bl) {
                            programClass2 = programClass1;
                            integerCache1 = super.integerCache;
                        } else {
                            scriptEnvironment1.logWarning(
                                    "Did not remap line numbers in class '"
                                            + ZkmUtils.slashesToDots(string)
                                            + "' as specified in input change log '"
                                            + changeLogMapping1.getChangeLogName()
                                            + "' because it is a fixed class."
                            );
                            programClass2 = programClass1;
                            integerCache1 = super.integerCache;
                        }
                    } else {
                        programClass2 = programClass1;
                        integerCache1 = super.integerCache;
                    }
                } else {
                    programClass2 = programClass1;
                    integerCache1 = super.integerCache;
                }

                ListMultimap listMultimap1 = programClass2.collectLineNumbers(integerCache1);
                nestedMultiMap.putMultimap(programClass1.getClassName(), listMultimap1);
            }

            bb = ++ba;
        }
    }

    public void shuffleMembers() {
        Random random1 = ZkmUtils.createRandom(93);

        for (ProgramClass programClass1 : super.programClasses) {
            programClass1.shuffleMembers(random1);
            if (programClass1.hasVersionedVariants()) {
                Iterator iterator = programClass1.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ((ProgramClass) ((ClassFileBase) iterator.next())).shuffleMembers(random1);
                }
            }
        }
    }

    public void encryptClassStringsDefault(
            ProgramClass programClass1,
            boolean bl,
            boolean bl1,
            MethodKeyInjector methodKeyInjector,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        boolean bl2 = programClass1.supportsJava6();
        this.encryptStringsInClass(
                programClass1,
                bl,
                bl2,
                bl1,
                !HiddenOptionFlags.NO_METHOD_KEY_FLOW_OBFUSCATION,
                methodKeyInjector,
                commonSuperTypeResolver1,
                classHierarchyQuery,
                scriptEnvironment1,
                4
        );
    }

    public void processMethodParameters(int ba, ObservableHolder observableHolder) throws ZkmException, IOException {
        switch (ba) {
            case 0:
                observableHolder.setValue("Removing method parameters");
                this.removeAllMethodParameters();
            case 1:
            default:
                break;
            case 2:
                observableHolder.setValue("Obfuscating method parameters");
                this.obfuscateMethodParameters();
                break;
            case 3:
                observableHolder.setValue("Removing most method parameters");
                this.removeMethodParameters(true, false);
                break;
            case 4:
                observableHolder.setValue("Removing most method parameters");
                this.removeMethodParameters(true, true);
                break;
            case 5:
                observableHolder.setValue("Removing most method parameters");
                this.removeMethodParameters(false, true);
        }
    }

    public void encryptLongConstants(
            LongEncryptionExclusionHandler longEncryptionExclusionHandler,
            MultiMapTable multiMapTable,
            PairMultiMap pairMultiMap,
            PairMultiMap pairMultiMap1,
            LongConstantEncryptor longConstantEncryptor,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            MethodParameterChanger methodParameterChanger,
            Map map1,
            Map map2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        super.classRepository.markLongConstantsEncrypted();
        ArrayList arrayList;
        ClassRepository classRepository1;
        if (scriptEnvironment1.isVerbose()) {
            arrayList = new ArrayList(multiMapTable.getKeyCount());
            classRepository1 = super.classRepository;
        } else {
            arrayList = null;
            classRepository1 = super.classRepository;
        }

        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(classRepository1.getTotalClassCount()));
        Iterator iterator = super.classHierarchy.getRootProgramInterfaces().iterator();

        while (iterator.hasNext()) {
            ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) iterator.next();
            ProgramClass programClass1 = classHierarchyNode.getProgramClass();
            if (!programClass1.isGenerated()) {
                classHierarchyNode.encryptLongConstants(
                        longEncryptionExclusionHandler,
                        multiMapTable,
                        pairMultiMap,
                        pairMultiMap1,
                        longConstantEncryptor,
                        inheritedMemberAnalyzer,
                        hashSet,
                        methodParameterChanger,
                        map1,
                        map2,
                        super.classRepository,
                        arrayList,
                        super.classResolver,
                        commonSuperTypeResolver1,
                        scriptEnvironment1,
                        true
                );
            }
        }

        List list1 = super.classHierarchy.getTopProgramNodes();
        Iterator iterator1 = list1.iterator();

        while (iterator1.hasNext()) {
            ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) iterator1.next();
            if (!classHierarchyNode1.isInterface()) {
                ProgramClass programClass2 = classHierarchyNode1.getProgramClass();
                if (!programClass2.isGenerated()) {
                    classHierarchyNode1.encryptLongConstants(
                            longEncryptionExclusionHandler,
                            multiMapTable,
                            pairMultiMap,
                            pairMultiMap1,
                            longConstantEncryptor,
                            inheritedMemberAnalyzer,
                            hashSet,
                            methodParameterChanger,
                            map1,
                            map2,
                            super.classRepository,
                            arrayList,
                            super.classResolver,
                            commonSuperTypeResolver1,
                            scriptEnvironment1,
                            false
                    );
                }
            }
        }

        if (scriptEnvironment1.isVerbose() && arrayList != null) {
            PrintWriter printWriter = scriptEnvironment1.getLogWriter();
            ClassByNameComparator classByNameComparator = new ClassByNameComparator(this);
            Collections.sort(arrayList, classByNameComparator);
            Iterator iterator2 = arrayList.iterator();

            while (iterator2.hasNext()) {
                ProgramClass programClass3 = (ProgramClass) iterator2.next();
                printWriter.println("\tLong Constant Encryption performed in class '" + programClass3.getDisplayLocationName() + "'");
            }
        }
    }

    public long decodeLicenseTimestamp(String string, boolean bl) {
        String string1 = string;
        return this.m(string1) + (bl ? 1272694208093L : 0L);
    }

    public final void renameClasses(
            NameExclusionSet nameExclusionSet,
            ChangeLogMapping changeLogMapping1,
            boolean bl,
            boolean bl1,
            boolean bl2,
            String string,
            int ba,
            int bb,
            MixedCaseNamesMode mixedCaseNamesMode1,
            boolean bl3,
            String string1,
            HashMap hashMap,
            HashMap hashMap1,
            HashMap hashMap2,
            HashMap hashMap3,
            ClassFileBase[] classFileBases,
            MessageReporter messageReporter1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        ListMultimap listMultimap = ObfuscatorEngineBase.invertToMultimap(hashMap);
        ClassRenameClashChecker classRenameClashChecker = new ClassRenameClashChecker(
                nameExclusionSet, changeLogMapping1, bl, super.classRepository, super.programClasses, classFileBases, super.classHierarchy, hashMap2, hashMap3
        );
        List list1 = null;
        if (string1 != null && string1.length() > 0) {
            list1 = readNameListFile(string1, "newClassNameFile", scriptEnvironment1);
        }

        FileClassNameGenerator fileClassNameGenerator;
        if (bl3) {
            fileClassNameGenerator = new SharedSequenceClassNamer(
                    classRenameClashChecker, bl1, bl2, string, ba, bb, bl, mixedCaseNamesMode1, hashMap, listMultimap, super.rootPackageNode.getNodesByPath(), list1
            );
        } else {
            fileClassNameGenerator = new FileClassNameGenerator(
                    classRenameClashChecker, bl1, bl2, string, ba, bb, bl, mixedCaseNamesMode1, hashMap, listMultimap, super.rootPackageNode.getNodesByPath(), list1
            );
        }

        classRenameClashChecker.renameClasses(fileClassNameGenerator, hashMap1, hashMap, listMultimap, messageReporter1);
    }

    public MethodParameterExclusions createMethodParameterExclusions(
            Boolean boolean1,
            ObservableHolder observableHolder,
            ObservableHolder observableHolder1,
            Integer integer,
            List list1,
            List list2,
            NameExclusionSet nameExclusionSet,
            ReflectionAccessMatcher reflectionAccessMatcher,
            FixedClassesExclusionSet fixedClassesExclusionSet1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        Set set1 = ZkmUtils.createHashSet();
        ListMultimap listMultimap;
        if (observableHolder1.isValueNull()) {
            SetMultiMap setMultiMap = new SetMultiMap();
            ProgramClass[] programClass1 = super.programClasses;
            int ba = programClass1.length;

            for (int i = 0; i < ba; i++) {
                programClass1[i].collectCallGraph(setMultiMap, set1);
            }

            listMultimap = setMultiMap.toListMultimap();
        } else {
            listMultimap = (ListMultimap) observableHolder1.getValue();
            set1 = (Set) observableHolder.getValue();
        }

        MethodParameterExclusions methodParameterExclusions = new MethodParameterExclusions(
                boolean1,
                super.classRepository,
                integer,
                list1,
                list2,
                scriptEnvironment1.getTrimExclusionMemberSets(),
                super.classRepository.getAllReflectedMethods(),
                super.classRepository.getResourceReferencedMethods(),
                super.classRepository.getMethodHandleTargets(),
                super.classRepository.getBootstrapMethodHandleTargets(),
                listMultimap,
                nameExclusionSet,
                reflectionAccessMatcher,
                fixedClassesExclusionSet1,
                scriptEnvironment1
        );
        observableHolder.setValue(set1);
        observableHolder1.setValue(listMultimap);
        return methodParameterExclusions;
    }

    public final void renameMethods(
            NameExclusionSet nameExclusionSet,
            MethodParameterExclusions methodParameterExclusions,
            FixedClassesExclusionSet fixedClassesExclusionSet1,
            ChangeLogMapping changeLogMapping1,
            boolean bl,
            int ba,
            boolean bl1,
            boolean bl2,
            boolean bl3,
            boolean bl4,
            boolean bl5,
            boolean bl6,
            String string,
            boolean bl7,
            HashMap hashMap,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            ClassFileBase[] classFileBases,
            Map map1,
            List list1,
            ObservableHolder observableHolder,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        MethodRenamer methodRenamer1 = new MethodRenamer(
                nameExclusionSet,
                methodParameterExclusions,
                fixedClassesExclusionSet1,
                changeLogMapping1,
                bl,
                super.classRepository,
                super.programClasses,
                classFileBases,
                super.classHierarchy,
                super.classResolver,
                ba,
                bl1,
                bl2,
                bl3,
                bl4,
                hashMap,
                twoKeyMap,
                twoKeyMap1,
                map1,
                observableHolder,
                scriptEnvironment1
        );
        MethodNameAssigner methodNameAssigner = new MethodNameAssigner(
                methodRenamer1, nameExclusionSet, methodParameterExclusions, bl5, bl6, bl3, string, bl7, list1
        );
        methodRenamer1.renameMethods(methodNameAssigner);
    }

    public void obfuscateFlowAndExceptions(
            List list1,
            Map map1,
            FixedClassesExclusionSet fixedClassesExclusionSet1,
            FlowObfuscationExclusions flowObfuscationExclusions,
            ExceptionObfuscationExclusions exceptionObfuscationExclusions,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            Set set1,
            NestedMultiMap nestedMultiMap,
            Map map2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassRepository classRepository1,
            ScriptEnvironment scriptEnvironment1,
            boolean bl,
            boolean bl1,
            ProcessingStatistics processingStatistics1
    ) throws ZkmException, IOException {
        IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1 = scriptEnvironment1.getIgnoreMissingReferencesSpec();
        ArrayList arrayList = new ArrayList();
        String string2 = HiddenOptionFlags.EXCEPTION_OBFUSCATION_USE_EXCEPTION;
        if (string2 != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(string2, ";");

            while (stringTokenizer.hasMoreTokens()) {
                String string3 = stringTokenizer.nextToken();
                String string4 = ZkmUtils.dotsToSlashes(string3);
                if (super.classResolver.findClassFile(string4) != null) {
                    if (!string4.equals("java/lang/Throwable") && !classRepository1.isSubclass(string4, "java/lang/Throwable")) {
                        scriptEnvironment1.logWarning(
                                "Class '"
                                        + string3
                                        + "' specified in "
                                        + "ZKM_EXCEPTION_OBFUSCATION_USE_EXCEPTION"
                                        + " String '"
                                        + string2
                                        + "' is not a Throwable. It will not be used."
                        );
                    } else {
                        arrayList.add(string4);
                    }
                } else {
                    scriptEnvironment1.logWarning(
                            "Class '"
                                    + string3
                                    + "' specified in "
                                    + "ZKM_EXCEPTION_OBFUSCATION_USE_EXCEPTION"
                                    + " String '"
                                    + string2
                                    + "' could not be found. It will not be used."
                    );
                }
            }
        }

        HashMap hashMap = ZkmUtils.createHashMap();
        String string10 = HiddenOptionFlags.EXCEPTION_OBFUSCATION_EXTRA_EXCEPTIONS;
        ArrayList arrayList1 = new ArrayList();
        if (string10 != null) {
            StringTokenizer stringTokenizer1 = new StringTokenizer(string10, ";");

            while (stringTokenizer1.hasMoreTokens()) {
                String string5 = stringTokenizer1.nextToken();
                String string6 = ZkmUtils.dotsToSlashes(string5);
                arrayList1.add(string6);
            }
        }

        Iterator iterator3 = list1.iterator();

        while (iterator3.hasNext()) {
            FlowObfuscationGroup flowObfuscationGroup = (FlowObfuscationGroup) iterator3.next();
            CountingBag countingBag = new CountingBag();
            Set set2 = flowObfuscationGroup.getCommonClasses();
            Iterator iterator = set2.iterator();

            while (iterator.hasNext()) {
                ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                if (classFileBase instanceof ProgramClass) {
                    ProgramClass programClass1 = (ProgramClass) classFileBase;
                    programClass1.countSubclassConstants(countingBag, classRepository1);
                }
            }

            StringLengthComparator stringLengthComparator = new StringLengthComparator(this);
            ArrayList arrayList3 = new ArrayList();
            List list2 = countingBag.getSortedByCount();
            float ba = -1.0F;
            Iterator iterator1 = list2.iterator();

            label139:
            while (iterator1.hasNext()) {
                String string7 = (String) iterator1.next();
                String string8 = classRepository1.lookupNewClassName(string7);
                Iterator iterator2 = arrayList1.iterator();

                while (iterator2.hasNext()) {
                    String string9 = (String) iterator2.next();
                    String string = string9;
                    String string1 = string7;
                    if (ZkmStringUtils.matchesWildcard(string1, string)) {
                        continue label139;
                    }

                    if (string8 != null) {
                        string = string9;
                        string1 = string8;
                        if (ZkmStringUtils.matchesWildcard(string1, string)) {
                            continue label139;
                        }
                    }
                }

                int bc = countingBag.getCount(string7);
                if (ba == -1.0F) {
                    ba = bc;
                }

                if (ba / bc > 0.5) {
                    arrayList3.add(string7);
                }
            }

            Collections.sort(arrayList3, stringLengthComparator);
            if (arrayList.size() > 0) {
                if (arrayList3.size() > 0) {
                    iterator1 = arrayList3.iterator();

                    while (iterator1.hasNext()) {
                        String string11 = (String) iterator1.next();
                        if (!arrayList.contains(string11)) {
                            arrayList.add(string11);
                        }
                    }
                }

                arrayList3 = arrayList;
            }

            hashMap.put(flowObfuscationGroup, arrayList3);
        }

        ArrayList arrayList2 = new ArrayList(super.programClasses.length);
        int bb = 0;
        int bd = 0;

        for (ProgramClass[] programClass4 = super.programClasses; bd < programClass4.length; programClass4 = super.programClasses) {
            ProgramClass programClass2 = super.programClasses[bb];
            label111:
            if (!programClass2.isGenerated() && (fixedClassesExclusionSet1 == null || !fixedClassesExclusionSet1.isMatchedClass(programClass2))) {
                boolean bl2;
                if (exceptionObfuscationExclusions != null) {
                    if (exceptionObfuscationExclusions.isClassExcluded(programClass2)) {
                        break label111;
                    }

                    bl2 = HiddenOptionFlags.FLOW_EXCLUDES_EXCEPTION_OBFUSCATION;
                } else {
                    bl2 = HiddenOptionFlags.FLOW_EXCLUDES_EXCEPTION_OBFUSCATION;
                }

                if (!bl2 || flowObfuscationExclusions == null || !flowObfuscationExclusions.isClassExcluded(programClass2)) {
                    arrayList2.add(programClass2);
                    if (programClass2.hasVersionedVariants()) {
                        Iterator iterator5 = programClass2.getVersionedVariants().iterator();

                        while (iterator5.hasNext()) {
                            ClassFileBase classFileBase1 = (ClassFileBase) iterator5.next();
                            arrayList2.add((ProgramClass) classFileBase1);
                        }
                    }
                }
            }

            bd = ++bb;
        }

        if (HiddenOptionFlags.USE_PARALLEL && HiddenOptionFlags.PROCESSOR_COUNT >= 2) {
            Vector vector = new Vector();
            arrayList2.parallelStream()
                    .forEach(
                            programClass1x -> {
                                try {
                                    try {
                                        com.zelix.klassmaster.util.ZkmUtils.<com.zelix.klassmaster.exceptions.ZkmProcessingException>mayThrow();
                                        this.obfuscateClassFlow(
                                                ((com.zelix.klassmaster.classfile.ProgramClass) programClass1x),
                                                hashMap,
                                                map1,
                                                flowObfuscationExclusions,
                                                exceptionObfuscationExclusions,
                                                inheritedMemberAnalyzer,
                                                set1,
                                                nestedMultiMap,
                                                map2,
                                                commonSuperTypeResolver1,
                                                classRepository1,
                                                scriptEnvironment1,
                                                bl,
                                                bl1,
                                                processingStatistics1,
                                                ignoreMissingReferencesSpec1
                                        );
                                    } catch (ZkmProcessingException zkmProcessingException) {
                                        vector.add(zkmProcessingException);
                                    }
                                } catch (Throwable throwable) {
                                    throw ZkmUtils.sneakyThrow(throwable);
                                }
                            }
                    );
            if (!vector.isEmpty()) {
                throw (ZkmProcessingException) vector.get(0);
            }
        } else {
            Iterator iterator4 = arrayList2.iterator();

            while (iterator4.hasNext()) {
                ProgramClass programClass3 = (ProgramClass) iterator4.next();
                this.obfuscateClassFlow(
                        programClass3,
                        hashMap,
                        map1,
                        flowObfuscationExclusions,
                        exceptionObfuscationExclusions,
                        inheritedMemberAnalyzer,
                        set1,
                        nestedMultiMap,
                        map2,
                        commonSuperTypeResolver1,
                        classRepository1,
                        scriptEnvironment1,
                        bl,
                        bl1,
                        processingStatistics1,
                        ignoreMissingReferencesSpec1
                );
            }
        }
    }

    public void updateLocalVariableTypes(CommonSuperTypeResolver commonSuperTypeResolver1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        ProgramClass[] programClass3;
        if (HiddenOptionFlags.USE_PARALLEL) {
            if (HiddenOptionFlags.PROCESSOR_COUNT >= 2) {
                ArrayList arrayList = new ArrayList(super.programClasses.length);

                for (ProgramClass programClass1 : super.programClasses) {
                    arrayList.add(programClass1);
                    if (programClass1.hasVersionedVariants()) {
                        Iterator iterator = programClass1.getVersionedVariants().iterator();

                        while (iterator.hasNext()) {
                            ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                            arrayList.add((ProgramClass) classFileBase);
                        }
                    }
                }

                Vector vector = new Vector();
                arrayList.parallelStream().forEach(programClass1x -> {
                    try {
                        try {
                            ((ProgramClass) programClass1x).updateLocalVariableTypes(commonSuperTypeResolver1, super.classRepository, scriptEnvironment1);
                        } catch (ZkmProcessingException zkmProcessingException) {
                            vector.add(zkmProcessingException);
                        }
                    } catch (Throwable throwable) {
                        throw ZkmUtils.sneakyThrow(throwable);
                    }
                });
                if (!vector.isEmpty()) {
                    throw (ZkmProcessingException) vector.get(0);
                }

                return;
            }

            programClass3 = super.programClasses;
        } else {
            programClass3 = super.programClasses;
        }

        for (ProgramClass programClass2 : programClass3) {
            programClass2.updateLocalVariableTypes(commonSuperTypeResolver1, super.classRepository, scriptEnvironment1);
            if (programClass2.hasVersionedVariants()) {
                Iterator iterator1 = programClass2.getVersionedVariants().iterator();

                while (iterator1.hasNext()) {
                    ClassFileBase classFileBase1 = (ClassFileBase) iterator1.next();
                    ((ProgramClass) classFileBase1).updateLocalVariableTypes(commonSuperTypeResolver1, super.classRepository, scriptEnvironment1);
                }
            }
        }
    }

    public void encryptStrings(
            StringEncryptionExclusionSpec stringEncryptionExclusionSpec,
            MultiMapTable multiMapTable,
            PairMultiMap pairMultiMap,
            ListMultimap listMultimap,
            StringEncryptor stringEncryptor,
            boolean bl,
            boolean bl1,
            Set set1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            HashMap hashMap,
            MethodParameterChanger methodParameterChanger,
            Map map1,
            Map map2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            boolean bl2,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        boolean bl3 = HiddenOptionFlags.VERBOSE_STRING_ENCRYPTION;
        ArrayList arrayList;
        ClassRepository classRepository1;
        if (scriptEnvironment1.isVerbose()) {
            arrayList = new ArrayList(multiMapTable.getKeyCount());
            classRepository1 = super.classRepository;
        } else {
            arrayList = null;
            classRepository1 = super.classRepository;
        }

        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(classRepository1.getTotalClassCount()));
        Iterator iterator = super.classHierarchy.getRootProgramInterfaces().iterator();

        while (iterator.hasNext()) {
            ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) iterator.next();
            ProgramClass programClass1 = classHierarchyNode.getProgramClass();
            if (!programClass1.isGenerated()) {
                classHierarchyNode.encryptStrings(
                        stringEncryptionExclusionSpec,
                        pairMultiMap,
                        listMultimap,
                        multiMapTable,
                        stringEncryptor,
                        bl3,
                        bl,
                        bl1,
                        bl2,
                        set1,
                        hashMap,
                        inheritedMemberAnalyzer,
                        hashSet,
                        methodParameterChanger,
                        map1,
                        map2,
                        super.classRepository,
                        arrayList,
                        super.classResolver,
                        commonSuperTypeResolver1,
                        scriptEnvironment1,
                        true
                );
            }
        }

        List list1 = super.classHierarchy.getTopProgramNodes();
        Iterator iterator1 = list1.iterator();

        while (iterator1.hasNext()) {
            ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) iterator1.next();
            if (!classHierarchyNode1.isInterface()) {
                ProgramClass programClass2 = classHierarchyNode1.getProgramClass();
                if (!programClass2.isGenerated()) {
                    classHierarchyNode1.encryptStrings(
                            stringEncryptionExclusionSpec,
                            pairMultiMap,
                            listMultimap,
                            multiMapTable,
                            stringEncryptor,
                            bl3,
                            bl,
                            bl1,
                            bl2,
                            set1,
                            hashMap,
                            inheritedMemberAnalyzer,
                            hashSet,
                            methodParameterChanger,
                            map1,
                            map2,
                            super.classRepository,
                            arrayList,
                            super.classResolver,
                            commonSuperTypeResolver1,
                            scriptEnvironment1,
                            false
                    );
                }
            }
        }

        if (scriptEnvironment1.isVerbose() && arrayList != null) {
            PrintWriter printWriter = scriptEnvironment1.getLogWriter();
            ClassNameComparator classNameComparator = new ClassNameComparator(this);
            Collections.sort(arrayList, classNameComparator);
            Iterator iterator2 = arrayList.iterator();

            while (iterator2.hasNext()) {
                ProgramClass programClass3 = (ProgramClass) iterator2.next();
                printWriter.println("\tString Encryption performed in class '" + programClass3.getDisplayLocationName() + "'");
            }
        }
    }

    public void encryptStringsInClass(
            ProgramClass programClass1,
            boolean bl,
            boolean bl1,
            boolean bl2,
            boolean bl3,
            MethodKeyInjector methodKeyInjector,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery,
            ScriptEnvironment scriptEnvironment1,
            int ba
    ) throws ZkmException, IOException {
        String string = "analyzing fields and methods in the inheritance hierarchy of class '"
                + programClass1.getDottedClassName()
                + "' ("
                + programClass1.getCreationKind()
                + ")";
        HashSet hashSet = ZkmUtils.createHashSet();
        StringEncryptor stringEncryptor = new StringEncryptor(false, bl);
        ProgramClass[] programClass2 = new ProgramClass[]{programClass1};
        InheritedMemberAnalyzer inheritedMemberAnalyzer = new InheritedMemberAnalyzer(programClass2, super.classResolver);
        MultiMapTable multiMapTable = new MultiMapTable(super.programClasses.length);
        PairMultiMap pairMultiMap = new PairMultiMap(super.programClasses.length);
        ListMultimap listMultimap = new ListMultimap(super.programClasses.length);
        StringEncryptionExclusionSpec stringEncryptionExclusionSpec = new StringEncryptionExclusionSpec(
                super.classRepository, new ArrayList(), new ArrayList(), scriptEnvironment1
        );
        programClass1.collectEncryptableStrings(
                multiMapTable, pairMultiMap, listMultimap, (StringEncryptionExclusionSpec) null, bl3, commonSuperTypeResolver1, classHierarchyQuery, scriptEnvironment1
        );
        if (methodKeyInjector != null) {
            MethodParamChangeNode methodParamChangeNode = methodKeyInjector.getCurrentNode();
            ConstantPool constantPool1 = programClass1.getClassConstantPool();
            ArrayList arrayList = new ArrayList();
            Iterator iterator = multiMapTable.entrySet().iterator();

            while (iterator.hasNext()) {
                Iterator iterator1 = ((PairMultiMap) ((Entry) iterator.next()).getValue()).entrySet().iterator();

                while (iterator1.hasNext()) {
                    MethodInfo methodInfo1 = (MethodInfo) ((Entry) iterator1.next()).getKey();
                    if (!methodKeyInjector.hasMethodKey(methodInfo1)) {
                        ObservableHolder observableHolder = new ObservableHolder();
                        Long long1 = methodKeyInjector.createMethodKey(methodInfo1);
                        methodInfo1.insertMethodKeyInitialization(methodParamChangeNode, long1, observableHolder, arrayList, constantPool1, string, ba);
                        methodKeyInjector.putKeyLocal(methodInfo1, (LocalVariableIndex) observableHolder.getValue());
                    }
                }
            }

            if (!arrayList.isEmpty()) {
                programClass1.addPoolConstants(arrayList);
            }
        }

        ClassRepository classRepository1 = super.classRepository;
        ClassResolver classResolver1 = super.classResolver;
        List list1 = pairMultiMap.getPairs(programClass1);
        List list2 = listMultimap.getValues(programClass1);
        PairMultiMap pairMultiMap1 = multiMapTable.getPairMultiMap(programClass1);
        MethodParameterChanger methodParameterChanger = methodKeyInjector == null ? null : methodKeyInjector.getParameterChanger();
        Map map1 = methodKeyInjector == null ? null : methodKeyInjector.getMethodKeys();
        Map map2 = methodKeyInjector == null ? null : methodKeyInjector.getKeyLocals();
        ClassHierarchyQuery classHierarchyQuery1 = classHierarchyQuery;
        CommonSuperTypeResolver commonSuperTypeResolver2 = commonSuperTypeResolver1;
        programClass1.encryptStrings(
                inheritedMemberAnalyzer,
                stringEncryptor,
                true,
                bl,
                bl1,
                true,
                bl2,
                classRepository1,
                classResolver1,
                list1,
                list2,
                pairMultiMap1,
                stringEncryptionExclusionSpec,
                hashSet,
                methodParameterChanger,
                map1,
                map2,
                (List) null,
                commonSuperTypeResolver2,
                classHierarchyQuery1
        );
    }

    public void removeLocalVariables() {
        for (ProgramClass programClass1 : super.programClasses) {
            programClass1.removeLocalVariableTables();
            if (programClass1.hasVersionedVariants()) {
                Iterator iterator = programClass1.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ((ProgramClass) ((ClassFileBase) iterator.next())).removeLocalVariableTables();
                }
            }
        }
    }

    public String getLicenseDateCode() {
        return LicenseCheckBase.getEncodedLicenseKey();
    }

    public void markDebugInfoPresence(int ba, Map map1) {
        ProgramClass[] programClass1 = super.programClasses;
        int bb = programClass1.length;
        ZkmProcessingException.getBuildTag();
        int bc = 0;

        label30:
        while (bc < bb) {
            ProgramClass programClass2 = programClass1[bc];
            if (ba >= 0) {
                programClass2.propagateDebugAttributeFlags(map1);
                if (programClass2.hasVersionedVariants()) {
                    Iterator iterator = programClass2.getVersionedVariants().iterator();

                    while (iterator.hasNext()) {
                        ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                        ((ProgramClass) classFileBase).propagateDebugAttributeFlags(map1);
                        if (ba <= 0) {
                            continue label30;
                        }
                    }
                }

                bc++;
            }
        }
    }

    public void encryptIntegerConstants(
            IntegerEncryptionExclusions integerEncryptionExclusions,
            MultiMapTable multiMapTable,
            PairMultiMap pairMultiMap,
            PairMultiMap pairMultiMap1,
            IntegerConstantEncryptor integerConstantEncryptor,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            MethodParameterChanger methodParameterChanger,
            Map map1,
            Map map2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        super.classRepository.markIntegerConstantsEncrypted();
        ArrayList arrayList;
        ClassRepository classRepository1;
        if (scriptEnvironment1.isVerbose()) {
            arrayList = new ArrayList(multiMapTable.getKeyCount());
            classRepository1 = super.classRepository;
        } else {
            arrayList = null;
            classRepository1 = super.classRepository;
        }

        HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(classRepository1.getTotalClassCount()));
        Iterator iterator = super.classHierarchy.getRootProgramInterfaces().iterator();

        while (iterator.hasNext()) {
            ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) iterator.next();
            ProgramClass programClass1 = classHierarchyNode.getProgramClass();
            if (!programClass1.isGenerated()) {
                classHierarchyNode.encryptIntegerConstants(
                        integerEncryptionExclusions,
                        multiMapTable,
                        pairMultiMap,
                        pairMultiMap1,
                        integerConstantEncryptor,
                        inheritedMemberAnalyzer,
                        hashSet,
                        methodParameterChanger,
                        map1,
                        map2,
                        super.classRepository,
                        arrayList,
                        super.classResolver,
                        commonSuperTypeResolver1,
                        scriptEnvironment1,
                        true
                );
            }
        }

        List list1 = super.classHierarchy.getTopProgramNodes();
        Iterator iterator1 = list1.iterator();

        while (iterator1.hasNext()) {
            ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) iterator1.next();
            if (!classHierarchyNode1.isInterface()) {
                ProgramClass programClass2 = classHierarchyNode1.getProgramClass();
                if (!programClass2.isGenerated()) {
                    classHierarchyNode1.encryptIntegerConstants(
                            integerEncryptionExclusions,
                            multiMapTable,
                            pairMultiMap,
                            pairMultiMap1,
                            integerConstantEncryptor,
                            inheritedMemberAnalyzer,
                            hashSet,
                            methodParameterChanger,
                            map1,
                            map2,
                            super.classRepository,
                            arrayList,
                            super.classResolver,
                            commonSuperTypeResolver1,
                            scriptEnvironment1,
                            false
                    );
                }
            }
        }

        if (scriptEnvironment1.isVerbose() && arrayList != null) {
            PrintWriter printWriter = scriptEnvironment1.getLogWriter();
            ProgramClassNameComparator programClassNameComparator = new ProgramClassNameComparator(this);
            Collections.sort(arrayList, programClassNameComparator);
            Iterator iterator2 = arrayList.iterator();

            while (iterator2.hasNext()) {
                ProgramClass programClass3 = (ProgramClass) iterator2.next();
                printWriter.println("\tInteger Constant Encryption performed in class '" + programClass3.getDisplayLocationName() + "'");
            }
        }
    }

    public void obfuscateReferences(
            ReferenceObfuscationExclusions referenceObfuscationExclusions,
            boolean bl,
            int ba,
            int bb,
            ChangeLogMapping changeLogMapping1,
            String string,
            String string1,
            boolean bl1,
            ReadOnlyMultiMapView readOnlyMultiMapView,
            SetMultiMap setMultiMap,
            SetMultiMap setMultiMap1,
            ObservableHolder observableHolder,
            Map map1,
            SetValuedMap setValuedMap,
            NestedMultiMap nestedMultiMap,
            ReferenceObfuscator referenceObfuscator,
            MethodKeyInjector methodKeyInjector,
            Map map2,
            Map map3,
            ClassRepository classRepository1,
            FixedClassesExclusionSet fixedClassesExclusionSet1,
            InheritedMemberAnalyzer inheritedMemberAnalyzer,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            boolean bl2,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        HashMap hashMap = ZkmUtils.createHashMap();
        TwoKeySetMultiMap twoKeySetMultiMap = new TwoKeySetMultiMap();
        SetMultiMap setMultiMap2 = new SetMultiMap();
        boolean bl3 = false;
        Iterator iterator = nestedMultiMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            ClassMemberRef classMemberRef = (ClassMemberRef) entry.getKey();
            MemberInfo memberInfo1 = classMemberRef.getMember();
            boolean bl4 = false;
            if (memberInfo1.isField() && referenceObfuscationExclusions.isFieldIncluded((AbstractFieldInfo) memberInfo1)
                    || memberInfo1.isMethod() && referenceObfuscationExclusions.isMethodIncluded((AbstractMethodInfo) memberInfo1)) {
                bl4 = true;
            }

            if (bl4) {
                Iterator iterator1 = ((ListMultimap) entry.getValue()).entrySet().iterator();

                while (iterator1.hasNext()) {
                    Entry entry1 = (Entry) iterator1.next();
                    MethodBytecode methodBytecode1 = (MethodBytecode) entry1.getKey();
                    List list1 = (List) entry1.getValue();
                    ProgramClass programClass1 = (ProgramClass) methodBytecode1.getOwningClass();
                    if ((fixedClassesExclusionSet1 == null || !fixedClassesExclusionSet1.isMatchedClass(programClass1))
                            && bl4
                            && (
                            memberInfo1.isField() && referenceObfuscationExclusions.isFieldReferenceIncluded((AbstractFieldInfo) memberInfo1, methodBytecode1)
                                    || memberInfo1.isMethod() && referenceObfuscationExclusions.isMethodReferenceIncluded((AbstractMethodInfo) memberInfo1, methodBytecode1)
                    )) {
                        bl3 = true;
                        twoKeySetMultiMap.addValue(programClass1, methodBytecode1, classMemberRef);
                        setMultiMap2.addValue(programClass1, classMemberRef);
                        Iterator iterator2 = list1.iterator();

                        while (iterator2.hasNext()) {
                            ConstantRefInstruction constantRefInstruction = (ConstantRefInstruction) iterator2.next();
                            hashMap.put((ResolvedMemberRef) constantRefInstruction.getConstantPoolEntry(), classMemberRef);
                        }
                    }
                }
            }
        }

        NestedMultimapView nestedMultimapView = twoKeySetMultiMap.toNestedMultimapView();
        ListMultimap listMultimap2 = setMultiMap2.toListMultimap();
        if (bl3) {
            boolean bl5 = bl;
            HashMap hashMap3 = ZkmUtils.createHashMap();
            HashMap hashMap4 = ZkmUtils.createHashMap();
            HashMap hashMap5 = ZkmUtils.createHashMap();
            TwoKeyMap twoKeyMap = new TwoKeyMap(true, false);
            TwoKeyMap twoKeyMap1 = new TwoKeyMap(true, false);
            int[] bd = null;
            TwoKeyMap twoKeyMap2 = null;
            HashMap hashMap6 = ZkmUtils.createHashMap();
            HashSet hashSet = ZkmUtils.createHashSet();
            if (bl) {
                twoKeyMap2 = new TwoKeyMap();
                EnumerableMap enumerableMap = classRepository1.getCumulativeNameMap();
                EnumerableMap enumerableMap1 = classRepository1.getOriginalToCurrentNameMap();
                EnumerableMap enumerableMap2 = classRepository1.getCurrentToOriginalNameMap();
                HashMap hashMap1 = ZkmUtils.createHashMap();
                HashMap hashMap2 = ZkmUtils.createHashMap();
                ObservableHolder observableHolder1 = new ObservableHolder();
                Set set1 = referenceObfuscator.assignLookupClassNames(
                        ZkmUtils.toUniqueSet(super.programClasses),
                        observableHolder1,
                        hashMap1,
                        hashMap2,
                        changeLogMapping1,
                        string1,
                        setMultiMap,
                        setMultiMap1,
                        readOnlyMultiMapView,
                        enumerableMap,
                        enumerableMap1,
                        enumerableMap2,
                        string,
                        bl1,
                        super.classpathClassLoader,
                        scriptEnvironment1
                );
                super.programClasses = LookupClassFactory.createLookupClasses(
                        set1,
                        hashMap2,
                        observableHolder1,
                        observableHolder,
                        map1,
                        hashMap6,
                        hashSet,
                        hashMap1,
                        ba,
                        bb,
                        referenceObfuscator,
                        classRepository1,
                        (LookupClassOption) null
                );
                ListMultimap listMultimap = nestedMultimapView.flattenToMultimap();
                Map map4 = referenceObfuscator.assignTypeIndices(listMultimap, super.classResolver, scriptEnvironment1.getIgnoreMissingReferencesSpec());
                SetMultiMap setMultiMap3 = new SetMultiMap();
                Iterator iterator3 = hashMap6.entrySet().iterator();

                while (iterator3.hasNext()) {
                    Entry entry2 = (Entry) iterator3.next();
                    ProgramClass programClass2 = (ProgramClass) entry2.getValue();
                    ProgramClass programClass3 = (ProgramClass) entry2.getKey();
                    setMultiMap3.addValue(programClass2, entry2.getKey());
                    if (!programClass3.isFromArchive() && programClass2.isFromArchive()) {
                        programClass2.markAsFromArchive();
                    }
                }

                iterator3 = hashSet.iterator();

                label260:
                while (true) {
                    BooleanFlag booleanFlag;
                    BooleanFlag booleanFlag1;
                    BooleanFlag booleanFlag2;
                    HashSet hashSet1;
                    ListMultimap listMultimap1;
                    ProgramClass programClass10;
                    boolean bl6;
                    BooleanFlag booleanFlag4;
                    do {
                        if (!iterator3.hasNext()) {
                            break label260;
                        }

                        programClass10 = (ProgramClass) iterator3.next();
                        bl6 = ReferenceObfuscator.bothSupportInvokedynamic(programClass10, (ProgramClass) null);
                        booleanFlag4 = new BooleanFlag();
                        booleanFlag = new BooleanFlag();
                        booleanFlag1 = new BooleanFlag();
                        booleanFlag2 = new BooleanFlag();
                        hashSet1 = ZkmUtils.createHashSet();
                        listMultimap1 = listMultimap;
                        if (hashSet.size() <= 1) {
                            break;
                        }

                        Set set2 = setMultiMap3.getValues(programClass10);
                        listMultimap1 = new ListMultimap(listMultimap.getKeyCount());
                        Iterator iterator4 = listMultimap.entrySet().iterator();

                        while (iterator4.hasNext()) {
                            Entry entry3 = (Entry) iterator4.next();
                            MethodBytecode methodBytecode2 = (MethodBytecode) entry3.getKey();
                            if (set2.contains(methodBytecode2.getOwningClass())) {
                                listMultimap1.appendValues(methodBytecode2, (Collection) entry3.getValue());
                            }
                        }
                    } while (listMultimap1.isEmpty());

                    Map map5 = referenceObfuscator.assignMemberRefIndices(
                            bl6, booleanFlag4, booleanFlag, listMultimap1, map4.size(), hashSet1, booleanFlag1, booleanFlag2, classRepository1
                    );
                    twoKeyMap1.putInnerMap(programClass10, map5);
                    bl5 = map4.size() + map5.size() <= 65535;
                    int[] bc = referenceObfuscator.shuffleKeyBitPositions();
                    if (bd == null) {
                        bd = referenceObfuscator.pickRandomLetters();
                    }

                    HashMap hashMap7 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(map4.size() + twoKeyMap1.getKeyCount()));
                    twoKeyMap2.putInnerMap(programClass10, hashMap7);
                    hashMap3.put(programClass10, new ObservableHolder());
                    hashMap4.put(programClass10, new ObservableHolder());
                    hashMap5.put(programClass10, new ObservableHolder());
                    ArrayList arrayList = new ArrayList();
                    ProgramClass[] programClass11 = new ProgramClass[]{programClass10};
                    InheritedMemberAnalyzer inheritedMemberAnalyzer1 = new InheritedMemberAnalyzer(programClass11, super.classResolver);
                    if (methodKeyInjector != null) {
                        SourceArchive sourceArchive1 = null;
                        Iterator iterator5 = map1.entrySet().iterator();

                        while (iterator5.hasNext()) {
                            Entry entry4 = (Entry) iterator5.next();
                            if (entry4.getValue() == programClass10) {
                                sourceArchive1 = (SourceArchive) entry4.getKey();
                                break;
                            }
                        }

                        methodKeyInjector.setCurrentArchive(sourceArchive1);
                    }

                    boolean value = booleanFlag4.getValue();
                    boolean bl8 = booleanFlag.getValue();
                    Map map6 = twoKeyMap1.getInnerMap(programClass10);
                    Map map7 = twoKeyMap2.getInnerMap(programClass10);
                    ClassResolver classResolver1 = super.classResolver;
                    ObservableHolder observableHolder2 = (ObservableHolder) hashMap3.get(programClass10);
                    ObservableHolder observableHolder3 = (ObservableHolder) hashMap4.get(programClass10);
                    ObservableHolder observableHolder4 = (ObservableHolder) hashMap5.get(programClass10);
                    booleanFlag1.getValue();
                    Boolean boolean1 = booleanFlag2.getValue();
                    programClass10.createReferenceObfuscationSupport(
                            true,
                            value,
                            bl8,
                            map4,
                            map6,
                            bc,
                            bd,
                            map7,
                            arrayList,
                            inheritedMemberAnalyzer1,
                            methodKeyInjector,
                            hashSet1,
                            classResolver1,
                            classRepository1,
                            referenceObfuscator,
                            twoKeyMap,
                            observableHolder2,
                            observableHolder3,
                            observableHolder4,
                            boolean1
                    );
                    programClass10.addPoolConstants(arrayList);
                }
            }

            MutableInt mutableInt = new MutableInt();
            MutableInt mutableInt1 = new MutableInt();
            MutableInt mutableInt2 = new MutableInt();
            BooleanFlag booleanFlag3 = new BooleanFlag();
            ClassHierarchy classHierarchy1 = classRepository1.getClassHierarchy();
            ProgramClass[] programClass4 = super.programClasses;
            int be = programClass4.length;

            for (int i = 0; i < be; i += 1) {
                ProgramClass programClass9 = programClass4[i];
                if (programClass9.isInterface() && !hashSet.contains(programClass9) && !programClass9.isGenerated()) {
                    ClassHierarchyNode classHierarchyNode1 = ClassHierarchyNode.findNode(programClass9.getClassName());
                    this.obfuscateReferencesInHierarchy(
                            classHierarchyNode1,
                            programClass9,
                            bl,
                            hashMap6,
                            twoKeyMap1,
                            bd,
                            twoKeyMap2,
                            nestedMultimapView,
                            nestedMultiMap,
                            listMultimap2,
                            hashMap,
                            twoKeyMap,
                            booleanFlag3,
                            setValuedMap,
                            hashMap3,
                            hashMap4,
                            hashMap5,
                            mutableInt,
                            mutableInt1,
                            mutableInt2,
                            inheritedMemberAnalyzer,
                            commonSuperTypeResolver1,
                            map2,
                            map3,
                            referenceObfuscator,
                            scriptEnvironment1
                    );
                    if (programClass9.hasVersionedVariants()) {
                        Iterator iterator12 = programClass9.getVersionedVariants().iterator();

                        while (iterator12.hasNext()) {
                            ClassFileBase classFileBase1 = (ClassFileBase) iterator12.next();
                            this.obfuscateReferencesInHierarchy(
                                    classHierarchyNode1,
                                    (ProgramClass) classFileBase1,
                                    bl,
                                    hashMap6,
                                    twoKeyMap1,
                                    bd,
                                    twoKeyMap2,
                                    nestedMultimapView,
                                    nestedMultiMap,
                                    listMultimap2,
                                    hashMap,
                                    twoKeyMap,
                                    booleanFlag3,
                                    setValuedMap,
                                    hashMap3,
                                    hashMap4,
                                    hashMap5,
                                    mutableInt,
                                    mutableInt1,
                                    mutableInt2,
                                    inheritedMemberAnalyzer,
                                    commonSuperTypeResolver1,
                                    map2,
                                    map3,
                                    referenceObfuscator,
                                    scriptEnvironment1
                            );
                        }
                    }
                }
            }

            Iterator iterator6 = classHierarchy1.getTopProgramNodes().iterator();

            while (iterator6.hasNext()) {
                ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) iterator6.next();
                ProgramClass programClass6 = classHierarchyNode.getProgramClass();
                if (!programClass6.isInterface() && !hashSet.contains(programClass6) && !programClass6.isGenerated()) {
                    this.obfuscateReferencesInHierarchy(
                            classHierarchyNode,
                            programClass6,
                            bl,
                            hashMap6,
                            twoKeyMap1,
                            bd,
                            twoKeyMap2,
                            nestedMultimapView,
                            nestedMultiMap,
                            listMultimap2,
                            hashMap,
                            twoKeyMap,
                            booleanFlag3,
                            setValuedMap,
                            hashMap3,
                            hashMap4,
                            hashMap5,
                            mutableInt,
                            mutableInt1,
                            mutableInt2,
                            inheritedMemberAnalyzer,
                            commonSuperTypeResolver1,
                            map2,
                            map3,
                            referenceObfuscator,
                            scriptEnvironment1
                    );
                    if (programClass6.hasVersionedVariants()) {
                        Iterator iterator11 = programClass6.getVersionedVariants().iterator();

                        while (iterator11.hasNext()) {
                            ClassFileBase classFileBase = (ClassFileBase) iterator11.next();
                            this.obfuscateReferencesInHierarchy(
                                    classHierarchyNode,
                                    (ProgramClass) classFileBase,
                                    bl,
                                    hashMap6,
                                    twoKeyMap1,
                                    bd,
                                    twoKeyMap2,
                                    nestedMultimapView,
                                    nestedMultiMap,
                                    listMultimap2,
                                    hashMap,
                                    twoKeyMap,
                                    booleanFlag3,
                                    setValuedMap,
                                    hashMap3,
                                    hashMap4,
                                    hashMap5,
                                    mutableInt,
                                    mutableInt1,
                                    mutableInt2,
                                    inheritedMemberAnalyzer,
                                    commonSuperTypeResolver1,
                                    map2,
                                    map3,
                                    referenceObfuscator,
                                    scriptEnvironment1
                            );
                        }
                    }
                }
            }

            if (booleanFlag3.getValue()) {
                if (scriptEnvironment1.isVerbose()) {
                    scriptEnvironment1.getLogWriter()
                            .println(
                                    "\tReference Obfuscation : Obfuscated "
                                            + mutableInt2.getValue()
                                            + " reference"
                                            + (mutableInt2.getValue() == 1 ? "" : 's')
                                            + " in "
                                            + mutableInt1.getValue()
                                            + " method"
                                            + (mutableInt1.getValue() == 1 ? "" : 's')
                                            + " in "
                                            + mutableInt.getValue()
                                            + " class"
                                            + (mutableInt.getValue() == 1 ? "" : "es")
                                            + "."
                            );
                }

                if (bl) {
                    HashSet hashSet2 = ZkmUtils.createHashSetFrom(setValuedMap.keySet());
                    Iterator iterator8 = hashSet2.iterator();

                    while (iterator8.hasNext()) {
                        ProgramClass programClass7 = (ProgramClass) iterator8.next();
                        if (HiddenOptionFlags.REFERENCE_OBFUSCATION_METHOD_KEYS) {
                            this.encryptClassStringsDefault(
                                    programClass7, bl5, bl2 && methodKeyInjector == null, methodKeyInjector, commonSuperTypeResolver1, classRepository1, scriptEnvironment1
                            );
                        }

                        if (programClass7.getClassConstantPool().getEntryCount() > 65535) {
                            String string4;
                            ProgramClass programClass12;
                            if (HiddenOptionFlags.REFERENCE_OBFUSCATION_METHOD_KEYS) {
                                programClass12 = programClass7;
                                long bh = 137574464584426L;
                                string4 = "Reference Obfuscation";
                            } else {
                                programClass12 = programClass7;
                                long bg = 137574464584426L;
                                string4 = "Reference Obfuscation";
                            }

                            String string2 = string4;
                            programClass12.checkConstantPoolSize(
                                    string2, "Consider limiting the number of references being obscured or using 'inReferencingClasses' setting. (B)"
                            );
                        }
                    }

                    Iterator iterator9 = hashSet.iterator();

                    while (iterator9.hasNext()) {
                        ProgramClass programClass8 = (ProgramClass) iterator9.next();
                        if (!hashSet2.contains(programClass8)) {
                            super.programClasses = classRepository1.removeClass(programClass8);
                            iterator9.remove();
                        }
                    }

                    if (!observableHolder.isValueNull() && !hashSet2.contains(observableHolder.getValue())) {
                        observableHolder.clearValue();
                    }

                    Iterator iterator10 = map1.entrySet().iterator();

                    while (iterator10.hasNext()) {
                        Entry entry5 = (Entry) iterator10.next();
                        if (!hashSet2.contains(entry5.getValue())) {
                            iterator10.remove();
                        }
                    }

                    if (scriptEnvironment1.isVerbose()) {
                        PrintWriter printWriter = scriptEnvironment1.getLogWriter();
                        String string3 = LookupClassFactory.describeLookupClassesByArchive("Reference Obfuscation", hashSet, map1);
                        printWriter.print(string3);
                    }
                }
            } else {
                Iterator iterator7 = hashSet.iterator();

                while (iterator7.hasNext()) {
                    ProgramClass programClass5 = (ProgramClass) iterator7.next();
                    super.programClasses = classRepository1.removeClass(programClass5);
                }

                observableHolder.clearValue();
                map1.clear();
                setValuedMap.clear();
                scriptEnvironment1.logMessage("Reference Obfuscation : No matching references found so no processing required or done.");
            }
        }
    }

    public boolean removeDeadInstructionsInClass(
            ProgramClass programClass1,
            Map map1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassMemberLookup classMemberLookup1,
            boolean bl,
            ScriptEnvironment scriptEnvironment1,
            IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException {
        boolean bl1 = false;

        try {
            return programClass1.removeDeadCode(commonSuperTypeResolver1, classMemberLookup1, bl, scriptEnvironment1);
        } catch (ZkmClassNotFoundException zkmClassNotFoundException) {
            if (HiddenOptionFlags.REMOVE_CORRUPT_CLASS && ignoreMissingReferencesSpec1 != null) {
                String string = zkmClassNotFoundException.getMessage();
                int ba = string.indexOf(39);
                int bb = string.indexOf(39, ba + 1);
                if (bb > ba) {
                    String string1 = string.substring(ba + 1, bb).trim();
                    if (ignoreMissingReferencesSpec1.isClassIgnored(ZkmUtils.dotsToSlashes(string1))) {
                        int bc = string.lastIndexOf(58);
                        if (bc > 0) {
                            int bd = string.indexOf("'", bc);
                            if (bd > bc) {
                                int be = string.indexOf("'", bd + 1);
                                if (be > bd) {
                                    string.substring(bd + 1, be).trim();
                                    map1.put(programClass1, string1);
                                    return bl1;
                                }
                            }
                        }
                    }
                }
            }

            throw zkmClassNotFoundException;
        }
    }
}
