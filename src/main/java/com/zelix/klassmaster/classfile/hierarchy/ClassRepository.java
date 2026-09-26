package com.zelix.klassmaster.classfile.hierarchy;

import com.zelix.klassmaster.KlassMaster;
import com.zelix.klassmaster.archive.ArchiveManifest;
import com.zelix.klassmaster.archive.ArchiveZipFile;
import com.zelix.klassmaster.archive.ClassArchiveSaver;
import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.JadFile;
import com.zelix.klassmaster.archive.JadFileException;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.archive.TempFileManager;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.FieldNameTypeSignature;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ModuleInfoClass;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.attribute.UnknownAttribute;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.classfile.insn.MethodBytecode;
import com.zelix.klassmaster.classfile.insn.StringConstantNameRef;
import com.zelix.klassmaster.classfile.insn.TrackedValue;
import com.zelix.klassmaster.config.ChangeLogInputFile;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.config.MixedCaseNamesMode;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.engine.ObfuscationEngine;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ClassLoadFailureException;
import com.zelix.klassmaster.exceptions.CorruptHierarchyException;
import com.zelix.klassmaster.exceptions.DuplicateFieldException;
import com.zelix.klassmaster.exceptions.MemberNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmClassNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.license.EvaluationExpiredException;
import com.zelix.klassmaster.license.ObfuscatedLongDecoder;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.obfuscator.exclude.ExistingSerializedClassesHandler;
import com.zelix.klassmaster.obfuscator.exclude.FixedClassesExclusionSet;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionCallSite;
import com.zelix.klassmaster.obfuscator.rename.CumulativeNameMapping;
import com.zelix.klassmaster.obfuscator.rename.RenamedClassLookup;
import com.zelix.klassmaster.obfuscator.rename.RootPackageNode;
import com.zelix.klassmaster.obfuscator.trim.ClassMemberSets;
import com.zelix.klassmaster.obfuscator.trim.RemoveMethodCallsHandler;
import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ast.ASTComplexAnnotationSpecifier;
import com.zelix.klassmaster.ui.OperationStatusCallback;
import com.zelix.klassmaster.util.ArrayEnumeration;
import com.zelix.klassmaster.util.ChangeObservable;
import com.zelix.klassmaster.util.CollectionSnapshotEnumeration;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.EntryIntValueComparator;
import com.zelix.klassmaster.util.EntryIntegerValueComparator;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.IndexedValueRelation;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ListenerRegistry;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.NodeVisitor;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ObservableModel;
import com.zelix.klassmaster.util.PairMultiMap;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;
import com.zelix.klassmaster.util.ReadOnlyMultiMapView;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.SetValuedMap;
import com.zelix.klassmaster.util.SnapshotEnumeration;
import com.zelix.klassmaster.util.SoftReferenceMap;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;
import com.zelix.klassmaster.util.ThreeKeyConcurrentMap;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;
import com.zelix.klassmaster.util.Triple;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;
import com.zelix.klassmaster.xml.ResourceFileAnalyzer;
import com.zelix.klassmaster.xml.ResourceFileIndex;
import com.zelix.klassmaster.xml.ResourcePathTranslator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassRepository extends ChangeObservable implements NodeVisitor, ClassMemberLookup, RenamedClassLookup {
    public static Integer cldc10ArchiveMarker = 1;
    public static Integer cldcArchiveMarker = 2;
    public static final Boolean CACHED_TRUE = Boolean.TRUE;
    public static final Boolean CACHED_FALSE = Boolean.FALSE;
    public static IntegerCache integerCache = IntegerCache.getInstance();
    public boolean fieldNamesObfuscated;
    public boolean methodNamesObfuscated;
    public List newMethodNames;
    public ThreeKeyConcurrentMap fieldsByName;
    public RootPackageNode rootPackageNode;
    public TwoKeyMap filteredOutFiles;
    public boolean hasClassesOutsideArchives;
    public TwoKeyMap skippedPropertiesFiles;
    public SetValuedMap autoReflectionHandledClasses;
    public Map manifestEntryMethods;
    public boolean classpathChanged;
    public ProgramClass[] programClasses;
    public InputFileLocation[] propertiesFileLocations;
    public Map resourceReferencedClasses;
    public Map moduleNameByArchive;
    public TwoKeyMap skippedXmlFiles;
    public List newFieldNames;
    public HashMap xmlSaveFiles;
    public boolean integerConstantsEncrypted;
    public TwoKeyMap xmlReferencedMethods;
    public InputFileLocation[] yamlFileLocations;
    public ThreeKeyConcurrentMap fieldsByClassNameDesc;
    public boolean classNamesChanged;
    public String openErrorMessage;
    public Set skippedArchives;
    public ListMultimap classesByArchive;
    public ClassHierarchy classHierarchy;
    public boolean parametersObfuscated;
    public InputFileLocation[] xmlFileLocations;
    private TwoKeyMap methodsByClass;
    public HashMap propertiesSaveFiles;
    public Map resourceReferencedMethods;
    public ModuleInfoClass[] moduleInfoClasses;
    public SetValuedMap parameterChangedClasses;
    public boolean longConstantsEncrypted;
    public HashMap archiveSaveFiles;
    public SetMultiMap preRenameAnnotationCache;
    public TwoKeyMap skippedYamlFiles;
    private NestedMultiMap methodsByClassAndNameType;
    public SetValuedMap referenceObfuscatedClasses;
    public HashMap jadSaveFiles;
    public TwoKeyMap preRenameInterfaceCache;
    public SourceArchive[] sourceArchives;
    public HashMap yamlSaveFiles;
    public TwoKeyMap skippedClassFiles;
    public ListMultimap archivesByModuleName;
    public TwoKeyMap fieldsBySignature;
    public TwoKeyMap preRenameSuperclassCache;
    private NestedMultiMap methodDeclaringClasses;
    public Map resourceReferencedFields;
    private final TwoKeyMap superclassCache = HiddenOptionFlags.USE_PARALLEL ? new TwoKeyMap(true) : new TwoKeyMap();
    private final TwoKeyMap interfaceCache = HiddenOptionFlags.USE_PARALLEL ? new TwoKeyMap(true) : new TwoKeyMap();
    private final SetMultiMap annotationCache = new SetMultiMap(HiddenOptionFlags.USE_PARALLEL);
    public final CumulativeNameMapping cumulativeNameMapping = new CumulativeNameMapping();
    public Map originalToCurrentName = ZkmUtils.createHashMap();
    public Map currentToOriginalName = ZkmUtils.createHashMap();
    public TwoKeyMap fieldSignatureChanges = new TwoKeyMap();
    public TwoKeyMap methodSignatureChanges = new TwoKeyMap();
    public TwoKeyMap classNameStringRefs = new TwoKeyMap(101, 7);
    public ListMultimap resourceNameStringRefs = new ListMultimap();
    public TwoKeyMap packageNameStringRefs = new TwoKeyMap();
    public TwoKeyMap fieldNameStringRefs = new TwoKeyMap();
    public TwoKeyMap methodNameStringRefs = new TwoKeyMap();
    public HashSet classNameStringConstants = ZkmUtils.createHashSet();
    public ListMultimap reflectionTargetsByCaller = new ListMultimap();
    public ListMultimap reflectionCallSites = new ListMultimap();
    public ObservableHolder autoReflectionLookupClass = new ObservableHolder();
    public Map autoReflectionMap = ZkmUtils.createHashMap();
    public Map referenceObfuscationMap = ZkmUtils.createHashMap();
    public ObservableHolder referenceLookupClass = new ObservableHolder();
    public ObservableHolder methodKeyTriple = new ObservableHolder();
    public Map methodKeyMap = ZkmUtils.createHashMap();
    public HashSet resourceFileNames = ZkmUtils.createHashSet();
    public final Set methodHandleTargets = HiddenOptionFlags.USE_PARALLEL ? Collections.newSetFromMap(new ConcurrentHashMap()) : ZkmUtils.createHashSet();
    public final Set bootstrapMethodHandleTargets = HiddenOptionFlags.USE_PARALLEL
            ? Collections.newSetFromMap(new ConcurrentHashMap())
            : ZkmUtils.createHashSet();
    public final Set classDigests = new LinkedHashSet();
    public HashMap jadFileArchives = ZkmUtils.createHashMap(13);
    public ResourceFileIndex resourceFileIndex = new ResourceFileIndex();
    public Map objectMethods = ZkmUtils.createHashMap();
    public ObservableHolder statusHolder;
    public ZkmClasspath classpath;
    public final ClasspathClassLoader classpathLoader;
    public final ClassResolver classResolver;
    public boolean verbose;
    public boolean editionFlag;
    public boolean scriptModeFlag;
    public final KlassMaster application;
    public final ClasspathClassLoader selfClassLoader;

    @Override
    public final boolean implementsInterface(String string, String string1) throws ZkmException, IOException {
        try {
            boolean bl = string1.indexOf(42) != -1;
            return this.implementsInterface(string, string1, bl);
        } catch (StackOverflowError stackOverflowError) {
            ClassFileBase classFileBase = this.classResolver.getClassFile(string, "reporting corrupt inheritance heirarchy");
            throw new CorruptHierarchyException(
                    stackOverflowError.getMessage() + " : Class " + classFileBase.getDisplayLocationName() + " has corrupt hierarchy (B)", stackOverflowError
            );
        }
    }

    @Override
    public final boolean implementsInterfaceByOriginalName(String string, String string1) throws ZkmException, IOException {
        try {
            boolean bl = string1.indexOf(42) != -1;
            return this.implementsInterfaceOriginal(string, string1, bl);
        } catch (StackOverflowError stackOverflowError) {
            ClassFileBase classFileBase = this.classResolver
                    .getClassFile((String) ZkmUtils.mapOrSelf(string1, this.originalToCurrentName), "reporting corrupt inheritance heirarchy");
            throw new CorruptHierarchyException(
                    stackOverflowError.getMessage() + " : Class " + classFileBase.getDisplayLocationName() + " has corrupt hierarchy (D)", stackOverflowError
            );
        }
    }

    public List getReflectedMethods(Object object) {
        ArrayList arrayList = new ArrayList();
        List list1 = this.reflectionTargetsByCaller.getValues(object);
        if (list1 != null) {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                ClassFileComponent classFileComponent = (ClassFileComponent) iterator.next();
                if (classFileComponent instanceof MethodInfo && ((MethodInfo) classFileComponent).isProgramMember()) {
                    arrayList.add((MethodInfo) classFileComponent);
                }
            }
        }

        return arrayList;
    }

    public SourceArchive[] getSourceArchives() {
        SourceArchive[] sourceArchives1 = new SourceArchive[this.sourceArchives.length];
        System.arraycopy(this.sourceArchives, 0, sourceArchives1, 0, this.sourceArchives.length);
        return sourceArchives1;
    }

    public final void indexMethods() {
        this.methodDeclaringClasses = new NestedMultiMap(Math.max(15, this.programClasses.length * 5), HiddenOptionFlags.USE_PARALLEL);
        this.methodsByClass = new TwoKeyMap(Math.max(15, this.programClasses.length), 5, HiddenOptionFlags.USE_PARALLEL);
        this.methodsByClassAndNameType = new NestedMultiMap(Math.max(15, this.programClasses.length), 5, 2, HiddenOptionFlags.USE_PARALLEL);
        int ba = 0;
        int bb = 0;

        for (ProgramClass[] programClass1 = this.programClasses; bb < programClass1.length; programClass1 = this.programClasses) {
            this.programClasses[ba].registerMethods(this);
            bb = ++ba;
        }
    }

    public Enumeration getOriginalInterfaceNames(String string, ObservableHolder observableHolder) throws ZkmException, IOException {
        ClassFileBase classFileBase;
        String[] strings;
        label27:
        {
            ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode((String) ZkmUtils.mapOrSelf(string, this.originalToCurrentName));
            ClasspathClassLoader classpathClassLoader1;
            if (classHierarchyNode != null) {
                if (!classHierarchyNode.hasNoClassFile()) {
                    classFileBase = classHierarchyNode.getClassFile();
                    strings = classHierarchyNode.getInterfaceNames();
                    break label27;
                }

                classpathClassLoader1 = this.classpathLoader;
            } else {
                classpathClassLoader1 = this.classpathLoader;
            }

            classFileBase = classpathClassLoader1.getClassFile(string);
            strings = classFileBase.getInterfaceNames();
        }

        int ba = strings.length;

        for (int i = 0; i < ba; i++) {
            String string1 = strings[i];
            String string2 = (String) ZkmUtils.mapOrSelf(string1, this.currentToOriginalName);
            TwoKeyMap twoKeyMap;
            if (!string1.equals(string2)) {
                strings[i] = string2;
                twoKeyMap = this.preRenameInterfaceCache;
            } else {
                twoKeyMap = this.preRenameInterfaceCache;
            }

            twoKeyMap.putValue(string2, string, CACHED_TRUE);
        }

        observableHolder.setValue(classFileBase);
        return new ArrayEnumeration(strings);
    }

    public Set getOriginalAnnotations(boolean bl, String string, String string1, Integer integer) throws ZkmException, IOException {
        try {
            if (this.preRenameAnnotationCache.containsKey(string1)) {
                return new LinkedHashSet(this.preRenameAnnotationCache.getValues(string1));
            }

            LinkedHashSet linkedHashSet = new LinkedHashSet(13);
            String string3 = "analyzing the annotations of class '" + ZkmUtils.slashesToDots(string1) + "'";
            this.collectOriginalAnnotations(bl, string, integer, linkedHashSet, false, string3);
            this.preRenameAnnotationCache.putValueSet(string1, linkedHashSet);
            return linkedHashSet;
        } catch (StackOverflowError stackOverflowError) {
            String string2 = "reporting corrupt inheritance hierarchy";
            ClassFileBase classFileBase = this.classResolver.getClassFile(string, string2);
            throw new CorruptHierarchyException(
                    stackOverflowError.getMessage() + " : Class " + classFileBase.getDisplayLocationName() + " has corrupt hierarchy (C)", stackOverflowError
            );
        }
    }

    public final ClassHierarchyNode getHierarchyNode(int ba) {
        return this.classHierarchy.getNodeAt(ba);
    }

    public Iterator iterateResourceFileNames() {
        return Collections.unmodifiableSet(this.resourceFileNames).iterator();
    }

    public Set collectAnnotations(boolean bl, String string, Integer integer, Set set1, boolean bl1, String string1) throws ZkmException, IOException {
        ClassFileBase classFileBase = this.classResolver.getVersionedClass(string, integer, string1);
        Enumeration enumeration = classFileBase.enumerateAnnotationTypes();

        while (enumeration.hasMoreElements()) {
            String string2 = (String) enumeration.nextElement();
            if (bl1) {
                if (this.classResolver.getClassFile(string2, string1).hasAnnotation("java/lang/annotation/Inherited")) {
                    set1.add(string2);
                }
            } else {
                set1.add(string2);
            }
        }

        if (bl) {
            String string3 = classFileBase.getSuperclassName();
            if (string3 != null) {
                this.collectAnnotations(bl, string3, integer, set1, true, string1);
            }
        }

        return set1;
    }

    @Override
    public final ClassResolver getClassResolver() {
        return this.classResolver;
    }

    public ProgramClass[] removeClass(Object object) throws ZkmException, IOException {
        int ba = Arrays.binarySearch(this.programClasses, object);
        ProgramClass[] programClass2;
        if (ba >= 0) {
            ProgramClass[] programClass1 = new ProgramClass[this.programClasses.length - 1];
            if (ba > 0) {
                System.arraycopy(this.programClasses, 0, programClass1, 0, ba);
            }

            if (ba < this.programClasses.length - 1) {
                System.arraycopy(this.programClasses, ba + 1, programClass1, ba, programClass1.length - ba);
                this.programClasses = programClass1;
            } else {
                this.programClasses = programClass1;
            }

            Arrays.sort(this.programClasses);
            ClassHierarchyNode.disposeAllNodes();
            this.classHierarchy.build(this.programClasses);
            this.rootPackageNode.buildFromClasses(this.programClasses);
            this.indexMethods();
            this.indexFields();
            programClass2 = this.programClasses;
        } else {
            programClass2 = this.programClasses;
        }

        return programClass2;
    }

    public void resolveMultiReleaseModules(List list1, Map map1, PairMultiMap pairMultiMap, PrintWriter printWriter, PrintWriter printWriter1) {
        PairMultiMap pairMultiMap1 = null;
        TwoKeyMap twoKeyMap = null;
        Iterator iterator = pairMultiMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string = (String) entry.getKey();
            List list2 = (List) entry.getValue();
            ModuleInfoClass moduleInfoClass = (ModuleInfoClass) map1.get(string);
            ProgramClass programClass1 = (ProgramClass) ((ObjectPair) list2.get(0)).getFirst();
            if (moduleInfoClass == null) {
                if (twoKeyMap == null) {
                    twoKeyMap = new TwoKeyMap();
                }

                Iterator iterator4 = list2.iterator();

                while (iterator4.hasNext()) {
                    ObjectPair objectPair2 = (ObjectPair) iterator4.next();
                    ModuleInfoClass moduleInfoClass4 = (ModuleInfoClass) objectPair2.getFirst();
                    ModuleInfoClass moduleInfoClass5 = (ModuleInfoClass) twoKeyMap.putValue(
                            moduleInfoClass4.getModuleName(), objectPair2.getSecond(), moduleInfoClass4
                    );
                    if (moduleInfoClass5 != null) {
                        printWriter.println(
                                "ERROR: Modules '"
                                        + moduleInfoClass5.getDisplayLocationName()
                                        + "' and '"
                                        + moduleInfoClass4.getDisplayLocationName()
                                        + "' both have the same multi-release version '"
                                        + objectPair2.getSecond()
                                        + "'. Class '"
                                        + moduleInfoClass5.getDisplayLocationName()
                                        + "'. The muti-release version will be ignored. (D)"
                        );
                    }
                }
            } else {
                int inputLocationCount = moduleInfoClass.getInputLocationCount();
                if (inputLocationCount <= 1) {
                    InputFileLocation inputFileLocation = moduleInfoClass.getInputLocation();
                    if (!inputFileLocation.isArchiveEntry()) {
                        printWriter.println(
                                "ERROR: Module '"
                                        + moduleInfoClass.getDottedClassName()
                                        + "' has a multi-release version '"
                                        + programClass1.getDisplayLocationName()
                                        + "' but the base class is not in an archive : '"
                                        + moduleInfoClass.getDisplayLocationName()
                                        + "'. The muti-release version will be ignored. (F)"
                        );
                    } else {
                        SourceArchive sourceArchive2 = inputFileLocation.getSourceArchive();
                        Iterator iterator1 = list2.iterator();

                        while (iterator1.hasNext()) {
                            ObjectPair objectPair = (ObjectPair) iterator1.next();
                            ModuleInfoClass moduleInfoClass1 = (ModuleInfoClass) objectPair.getFirst();
                            if (moduleInfoClass1.getInputLocation().getSourceArchive().equals(sourceArchive2)) {
                                int bb = (Integer) objectPair.getSecond();
                                moduleInfoClass.addVersionedVariant(moduleInfoClass1, bb);
                                if (this.verbose) {
                                    if (pairMultiMap1 == null) {
                                        pairMultiMap1 = new PairMultiMap();
                                    }

                                    pairMultiMap1.addPair(moduleInfoClass, moduleInfoClass1, bb);
                                }
                            } else {
                                printWriter.println(
                                        "ERROR: Module '"
                                                + moduleInfoClass.getDisplayLocationName()
                                                + "' has a multi-release version '"
                                                + moduleInfoClass1.getDisplayLocationName()
                                                + "' but they are not inside the same JAR file. The multi-release version will be ignored. (A)"
                                );
                            }
                        }
                    }
                } else {
                    ArrayList arrayList = new ArrayList(inputLocationCount);
                    Enumeration enumeration = moduleInfoClass.enumerateInputLocations();

                    while (enumeration.hasMoreElements()) {
                        arrayList.add(((InputFileLocation) enumeration.nextElement()).getQualifiedName());
                    }

                    printWriter.println(
                            "ERROR: Module '"
                                    + moduleInfoClass.getDottedClassName()
                                    + "' has a multi-release version '"
                                    + programClass1.getDisplayLocationName()
                                    + "' but it has been opened from more than one place : "
                                    + ZkmUtils.toQuotedListString(arrayList)
                                    + ". The muti-release version will be ignored. (E)"
                    );
                }
            }
        }

        if (twoKeyMap != null) {
            iterator = twoKeyMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry1 = (Entry) iterator.next();
                String string1 = (String) entry1.getKey();
                java.util.Map treeMap = (Map) entry1.getValue();
                treeMap = new TreeMap(treeMap);
                ModuleInfoClass moduleInfoClass3 = null;
                SourceArchive sourceArchive1 = null;
                Iterator iterator5 = treeMap.entrySet().iterator();

                while (iterator5.hasNext()) {
                    Entry entry2 = (Entry) iterator5.next();
                    int bc = (Integer) entry2.getKey();
                    ModuleInfoClass moduleInfoClass6 = (ModuleInfoClass) entry2.getValue();
                    if (moduleInfoClass3 == null) {
                        moduleInfoClass3 = moduleInfoClass6;
                        sourceArchive1 = moduleInfoClass3.getInputLocation().getSourceArchive();
                        moduleInfoClass6.setReleaseVersion((Integer) entry2.getKey());
                        list1.add(moduleInfoClass3);
                    } else {
                        SourceArchive sourceArchive3 = moduleInfoClass6.getInputLocation().getSourceArchive();
                        if (sourceArchive3 == sourceArchive1) {
                            moduleInfoClass3.addVersionedVariant(moduleInfoClass6, bc);
                            if (this.verbose) {
                                if (pairMultiMap1 == null) {
                                    pairMultiMap1 = new PairMultiMap();
                                }

                                pairMultiMap1.addPair(moduleInfoClass3, moduleInfoClass6, bc);
                            }
                        } else {
                            printWriter.println(
                                    "ERROR: Class '"
                                            + moduleInfoClass3.getDisplayLocationName()
                                            + "' has a multi-release version '"
                                            + moduleInfoClass6.getDisplayLocationName()
                                            + "' but they are not inside the same JAR file. The multi-release version will be ignored. (B)"
                            );
                        }
                    }
                }
            }
        }

        if (this.verbose && pairMultiMap1 != null) {
            printWriter1.println("Multi-release modules found...");
            TreeSet treeSet = new TreeSet(pairMultiMap1.keySet());
            Iterator iterator2 = treeSet.iterator();

            while (iterator2.hasNext()) {
                ModuleInfoClass moduleInfoClass2 = (ModuleInfoClass) iterator2.next();
                List list3 = pairMultiMap1.getPairs(moduleInfoClass2);
                printWriter1.println(
                        "\tModule '"
                                + moduleInfoClass2.getModuleName()
                                + "' ("
                                + moduleInfoClass2.getDisplayLocationName()
                                + ") has version"
                                + (list3.size() > 0 ? "s" : "")
                );
                EntryIntegerValueComparator entryIntegerValueComparator = new EntryIntegerValueComparator(this);
                Collections.sort(list3, entryIntegerValueComparator);
                Iterator iterator3 = list3.iterator();

                while (iterator3.hasNext()) {
                    ObjectPair objectPair1 = (ObjectPair) iterator3.next();
                    printWriter1.println(
                            "\t\tVersion " + objectPair1.getSecond() + " '" + ((ModuleInfoClass) objectPair1.getFirst()).getInputLocation().getName() + "'"
                    );
                }
            }
        }
    }

    public final int getClassCount() {
        return this.programClasses != null ? this.programClasses.length : 0;
    }

    public final synchronized void applyFieldPrefixRenames(HashMap hashMap) throws ZkmException, IOException {
        Iterator iterator = hashMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string = (String) entry.getKey();
            String string1 = (String) entry.getValue();
            this.renameFieldPrefix(string, string1);
        }
    }

    public HashMap getJadFileArchives() {
        return ZkmUtils.copyToHashMap(this.jadFileArchives);
    }

    public void registerKotlinCallableReference(ProgramClass programClass1) {
        MethodSignature methodSignature1 = new MethodSignature("getOwner", "()Lkotlin/reflect/KDeclarationContainer;");
        MethodInfo methodInfo1 = programClass1.findMethodBySignature(methodSignature1);
        if (methodInfo1 != null) {
            ConstantPoolEntry constantPoolEntry = methodInfo1.getFirstLoadedConstant();
            if (constantPoolEntry instanceof ResolvedClassConstant) {
                String string = ((ResolvedClassConstant) constantPoolEntry).getClassName();
                MethodSignature methodSignature2 = new MethodSignature("getName", "()Ljava/lang/String;");
                MethodInfo methodInfo2 = programClass1.findMethodBySignature(methodSignature2);
                if (methodInfo2 != null) {
                    constantPoolEntry = methodInfo2.getFirstLoadedConstant();
                    if (constantPoolEntry instanceof ResolvedStringConstant) {
                        ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) constantPoolEntry;
                        MethodSignature methodSignature3 = new MethodSignature("getSignature", "()Ljava/lang/String;");
                        MethodInfo methodInfo3 = programClass1.findMethodBySignature(methodSignature3);
                        if (methodInfo3 != null) {
                            constantPoolEntry = methodInfo3.getFirstLoadedConstant();
                            if (constantPoolEntry instanceof ResolvedStringConstant) {
                                ResolvedStringConstant resolvedStringConstant1 = (ResolvedStringConstant) constantPoolEntry;
                                ProgramClass programClass2 = ClassHierarchyNode.findProgramClass(string);
                                if (programClass2 != null) {
                                    String string1 = resolvedStringConstant1.getValueString();
                                    if (string1.indexOf(40) > 0) {
                                        MethodSignature methodSignature4 = new MethodSignature(string1);
                                        MethodInfo methodInfo4 = programClass2.findMethodBySignature(methodSignature4);
                                        if (methodInfo4 != null) {
                                            if (string1.startsWith(resolvedStringConstant.getValueString())) {
                                                StringConstantNameRef stringConstantNameRef = new StringConstantNameRef(resolvedStringConstant);
                                                this.methodNameStringRefs.putValue(methodInfo4, stringConstantNameRef, stringConstantNameRef);
                                            } else {
                                                String string3 = methodSignature4.getName();
                                                String string2 = null;
                                                if (string3.startsWith("get")) {
                                                    string2 = "get";
                                                } else if (string3.startsWith("set")) {
                                                    string2 = "set";
                                                } else if (string3.startsWith("is")) {
                                                    string2 = "is";
                                                }

                                                if (string2 != null && MethodSignature.toPropertyName(string3, string2).equals(resolvedStringConstant.getValueString())) {
                                                    StringConstantNameRef stringConstantNameRef1 = new StringConstantNameRef(resolvedStringConstant, string2);
                                                    this.methodNameStringRefs.putValue(methodInfo4, stringConstantNameRef1, stringConstantNameRef1);
                                                }
                                            }

                                            StringConstantNameRef stringConstantNameRef2 = new StringConstantNameRef(resolvedStringConstant1, true);
                                            this.methodNameStringRefs.putValue(methodInfo4, stringConstantNameRef2, stringConstantNameRef2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void resolveMultiReleaseClasses(List list1, Map map1, PairMultiMap pairMultiMap, PrintWriter printWriter, PrintWriter printWriter1) {
        PairMultiMap pairMultiMap1 = null;
        TwoKeyMap twoKeyMap = null;
        Iterator iterator = pairMultiMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string = (String) entry.getKey();
            List list2 = (List) entry.getValue();
            ProgramClass programClass1 = (ProgramClass) map1.get(string);
            ProgramClass programClass2 = (ProgramClass) ((ObjectPair) list2.get(0)).getFirst();
            if (programClass1 == null) {
                if (twoKeyMap == null) {
                    twoKeyMap = new TwoKeyMap();
                }

                Iterator iterator4 = list2.iterator();

                while (iterator4.hasNext()) {
                    ObjectPair objectPair2 = (ObjectPair) iterator4.next();
                    ProgramClass programClass6 = (ProgramClass) objectPair2.getFirst();
                    ProgramClass programClass7 = (ProgramClass) twoKeyMap.putValue(programClass6.getClassName(), objectPair2.getSecond(), programClass6);
                    if (programClass7 != null) {
                        printWriter.println(
                                "ERROR: Classes '"
                                        + programClass7.getDisplayLocationName()
                                        + "' and '"
                                        + programClass6.getDisplayLocationName()
                                        + "' both have the same multi-release version '"
                                        + objectPair2.getSecond()
                                        + "'. Class '"
                                        + programClass7.getDisplayLocationName()
                                        + "'. The muti-release version will be ignored. (A)"
                        );
                    }
                }
            } else {
                int inputLocationCount = programClass1.getInputLocationCount();
                if (inputLocationCount <= 1) {
                    InputFileLocation inputFileLocation = programClass1.getInputLocation();
                    if (!inputFileLocation.isArchiveEntry()) {
                        printWriter.println(
                                "ERROR: Class '"
                                        + programClass1.getDottedClassName()
                                        + "' has a multi-release version '"
                                        + programClass2.getDisplayLocationName()
                                        + "' but the base class is not in an archive : '"
                                        + programClass1.getDisplayLocationName()
                                        + "'. The muti-release version will be ignored. (C)"
                        );
                    } else {
                        SourceArchive sourceArchive2 = inputFileLocation.getSourceArchive();
                        Iterator iterator1 = list2.iterator();

                        while (iterator1.hasNext()) {
                            ObjectPair objectPair = (ObjectPair) iterator1.next();
                            ProgramClass programClass3 = (ProgramClass) objectPair.getFirst();
                            if (programClass3.getInputLocation().getSourceArchive().equals(sourceArchive2)) {
                                int bb = (Integer) objectPair.getSecond();
                                programClass1.addVersionedVariant(programClass3, bb);
                                if (this.verbose) {
                                    if (pairMultiMap1 == null) {
                                        pairMultiMap1 = new PairMultiMap();
                                    }

                                    pairMultiMap1.addPair(programClass1, programClass3, bb);
                                }
                            } else {
                                printWriter.println(
                                        "ERROR: Class '"
                                                + programClass1.getDisplayLocationName()
                                                + "' has a multi-release version '"
                                                + programClass3.getDisplayLocationName()
                                                + "' but they are not inside the same JAR file. The multi-release version will be ignored. (A)"
                                );
                            }
                        }
                    }
                } else {
                    ArrayList arrayList = new ArrayList(inputLocationCount);
                    Enumeration enumeration = programClass1.enumerateInputLocations();

                    while (enumeration.hasMoreElements()) {
                        arrayList.add(((InputFileLocation) enumeration.nextElement()).getQualifiedName());
                    }

                    printWriter.println(
                            "ERROR: Class '"
                                    + programClass1.getDottedClassName()
                                    + "' has a multi-release version '"
                                    + programClass2.getDisplayLocationName()
                                    + "' but it has been opened from more than one place : "
                                    + ZkmUtils.toQuotedListString(arrayList)
                                    + ". The muti-release version will be ignored. (B)"
                    );
                }
            }
        }

        if (twoKeyMap != null) {
            iterator = twoKeyMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry1 = (Entry) iterator.next();
                String string1 = (String) entry1.getKey();
                java.util.Map treeMap = (Map) entry1.getValue();
                treeMap = new TreeMap(treeMap);
                ProgramClass programClass5 = null;
                SourceArchive sourceArchive1 = null;
                Iterator iterator5 = treeMap.entrySet().iterator();

                while (iterator5.hasNext()) {
                    Entry entry2 = (Entry) iterator5.next();
                    int bc = (Integer) entry2.getKey();
                    ProgramClass programClass8 = (ProgramClass) entry2.getValue();
                    if (programClass5 == null) {
                        programClass5 = programClass8;
                        sourceArchive1 = programClass5.getInputLocation().getSourceArchive();
                        programClass8.setReleaseVersion((Integer) entry2.getKey());
                        list1.add(programClass5);
                    } else {
                        SourceArchive sourceArchive3 = programClass8.getInputLocation().getSourceArchive();
                        if (sourceArchive3 == sourceArchive1) {
                            programClass5.addVersionedVariant(programClass8, bc);
                            if (this.verbose) {
                                if (pairMultiMap1 == null) {
                                    pairMultiMap1 = new PairMultiMap();
                                }

                                pairMultiMap1.addPair(programClass5, programClass8, bc);
                            }
                        } else {
                            printWriter.println(
                                    "ERROR: Class '"
                                            + programClass5.getDisplayLocationName()
                                            + "' has a multi-release version '"
                                            + programClass8.getDisplayLocationName()
                                            + "' but they are not inside the same JAR file. The multi-release version will be ignored. (B)"
                            );
                        }
                    }
                }
            }
        }

        if (this.verbose && pairMultiMap1 != null) {
            printWriter1.println("Multi-release classes found...");
            TreeSet treeSet = new TreeSet(pairMultiMap1.keySet());
            Iterator iterator2 = treeSet.iterator();

            while (iterator2.hasNext()) {
                ProgramClass programClass4 = (ProgramClass) iterator2.next();
                List list3 = pairMultiMap1.getPairs(programClass4);
                printWriter1.println("\tClass '" + programClass4.getDisplayLocationName() + "' has version" + (list3.size() > 0 ? "s" : ""));
                EntryIntValueComparator entryIntValueComparator = new EntryIntValueComparator(this);
                Collections.sort(list3, entryIntValueComparator);
                Iterator iterator3 = list3.iterator();

                while (iterator3.hasNext()) {
                    ObjectPair objectPair1 = (ObjectPair) iterator3.next();
                    printWriter1.println("\t\tVersion " + objectPair1.getSecond() + " '" + ((ProgramClass) objectPair1.getFirst()).getInputLocation().getName() + "'");
                }
            }
        }
    }

    public void applyFieldRenamesToVersions(ClassFileBase[] classFileBases, TwoKeyMap twoKeyMap) throws ZkmException, IOException {
        for (int i = 0; i < classFileBases.length; i++) {
            ClassFileBase classFileBase = classFileBases[i];
            if (classFileBase.hasVersionedVariants()) {
                classFileBase.applyFieldRenames(twoKeyMap);
            }
        }
    }

    public ProgramClass[] addClass(ProgramClass programClass1) throws ZkmException, IOException {
        ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(programClass1.getClassName());
        ProgramClass[] programClass3;
        if (classHierarchyNode != null) {
            ClassFileBase classFileBase = classHierarchyNode.getClassFile();
            ZkmAssert.assertTrue(
                    false,
                    new String[]{
                            "Class '"
                                    + programClass1.getClassName()
                                    + "' already exists. : "
                                    + programClass1.getCreationKind()
                                    + " : "
                                    + (classFileBase == null ? "null" : classFileBase.getCreationKind() + " : " + classFileBase.getInputPath())
                    }
            );
            programClass3 = this.programClasses;
        } else {
            programClass3 = this.programClasses;
        }

        ProgramClass[] programClass2 = new ProgramClass[programClass3.length + 1];
        System.arraycopy(this.programClasses, 0, programClass2, 0, this.programClasses.length);
        programClass2[this.programClasses.length] = programClass1;
        this.programClasses = programClass2;
        Arrays.sort(this.programClasses);
        ClassHierarchyNode.disposeAllNodes();
        this.classHierarchy.build(this.programClasses);
        this.rootPackageNode.buildFromClasses(this.programClasses);
        this.indexMethods();
        this.indexFields();
        Object object1 = null;
        Object object = null;
        ClassResolver classResolver1 = this.classResolver;
        ClassRepository classRepository2 = this;
        programClass1.resolveReferences((Set) null, classRepository2, classResolver1, (IgnoreMissingReferencesSpec) object, (ScriptEnvironment) object1);
        return this.programClasses;
    }

    @Override
    public final boolean extendsAnnotatedClass(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        try {
            boolean bl = string1.indexOf(42) != -1;
            return this.extendsAnnotatedClass(string, string1, bl, aSTComplexAnnotationSpecifier);
        } catch (StackOverflowError stackOverflowError) {
            ClassFileBase classFileBase = this.classResolver.getClassFile(string, "reporting corrupt inheritance hierarchy");
            throw new CorruptHierarchyException(
                    stackOverflowError.getMessage() + " : Class " + classFileBase.getDisplayLocationName() + " has corrupt inheritance hierarchy (B)",
                    stackOverflowError
            );
        }
    }

    @Override
    public final boolean extendsAnnotatedClassByOriginalName(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        try {
            boolean bl = string1.indexOf(42) != -1;
            return this.extendsAnnotatedClassOriginal(string, string1, bl, aSTComplexAnnotationSpecifier);
        } catch (StackOverflowError stackOverflowError) {
            ClassFileBase classFileBase = this.classResolver.getClassFile(string, "reporting corrupt inheritance hierarchy");
            throw new CorruptHierarchyException(
                    stackOverflowError.getMessage() + " : Class " + classFileBase.getDisplayLocationName() + " has corrupt inheritance hierarchy (E)",
                    stackOverflowError
            );
        }
    }

    public EnumerableMap getCurrentToOriginalNameMap() {
        return new EnumerableMap(this.currentToOriginalName);
    }

    @Override
    public void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        this.clearHierarchyCaches();
        this.classpathChanged = true;
    }

    public boolean isIntegerConstantsEncrypted() {
        return this.integerConstantsEncrypted;
    }

    public Set collectOriginalAnnotations(boolean bl, String string, Integer integer, Set set1, boolean bl1, String string1) throws ZkmException, IOException {
        ClassFileBase classFileBase = this.classResolver.getVersionedClass(string, integer, string1);
        Enumeration enumeration = classFileBase.enumerateAnnotationTypes();

        while (enumeration.hasMoreElements()) {
            String string2 = (String) enumeration.nextElement();
            if (bl1) {
                ClassFileBase classFileBase1 = this.classResolver.getClassFile(string2, string1);
                if (classFileBase1.hasAnnotation("java/lang/annotation/Inherited")) {
                    set1.add(classFileBase1.getOriginalClassName());
                }
            } else {
                set1.add(ZkmUtils.mapOrSelf(string2, this.currentToOriginalName));
            }
        }

        if (bl) {
            String string3 = classFileBase.getSuperclassName();
            String string4 = (String) ZkmUtils.mapOrSelf(string3, this.currentToOriginalName);
            if (string3 != null) {
                this.collectOriginalAnnotations(bl, string3, integer, set1, true, string1);
            }
        }

        return set1;
    }

    @Override
    public final FieldInfo findDeclaredField(Object object, Object object1) {
        return (FieldInfo) this.fieldsBySignature.getValue(object1, object);
    }

    @Override
    public String getOriginalClassName(Object object) {
        return (String) ZkmUtils.mapOrSelf(object, this.currentToOriginalName);
    }

    public Map getResourceReferencedClasses() {
        return this.resourceReferencedClasses;
    }

    public String getSuperclassName(String string) throws ZkmException, IOException {
        String string1;
        ClassFileBase classFileBase;
        label23:
        {
            ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(string);
            ClasspathClassLoader classpathClassLoader1;
            if (classHierarchyNode != null) {
                if (!classHierarchyNode.hasNoClassFile()) {
                    classFileBase = classHierarchyNode.getClassFile();
                    ClassHierarchyNode classHierarchyNode1 = classHierarchyNode.getSuperclassNode();
                    string1 = classHierarchyNode1.getClassName();
                    break label23;
                }

                classpathClassLoader1 = this.classpathLoader;
            } else {
                classpathClassLoader1 = this.classpathLoader;
            }

            classFileBase = classpathClassLoader1.getClassFile(string);
            string1 = classFileBase.getSuperclassName();
        }

        String string2;
        if (HiddenOptionFlags.TEST_HIERARCHY
                && !classFileBase.isGenerated()
                && (string2 = ClassHierarchyNode.getHierarchyGapMessage(classFileBase, this.classResolver.getClassFile(string1), 1)) != null) {
            throw new ZkmRuntimeException(string2);
        }

        this.superclassCache.putValue(string1, string, CACHED_TRUE);
        return string1;
    }

    public final Enumeration enumerateModuleInfoClasses() {
        return new ArrayEnumeration(this.moduleInfoClasses);
    }

    public Set getAllReflectedMethods() {
        HashSet hashSet = ZkmUtils.createHashSet();
        Enumeration enumeration = this.reflectionTargetsByCaller.allValues();

        while (enumeration.hasMoreElements()) {
            ClassFileComponent classFileComponent = (ClassFileComponent) enumeration.nextElement();
            if (classFileComponent instanceof MethodInfo && ((MethodInfo) classFileComponent).isProgramMember()) {
                hashSet.add((MethodInfo) classFileComponent);
            }
        }

        return hashSet;
    }

    public void reportTrimPreview(
            Vector vector, Vector vector1, PrintWriter printWriter, DialogCallback dialogCallback1, TrimOptions trimOptions1, ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        try {
            TrimProcessor trimProcessor1 = new TrimProcessor(
                    this,
                    this.classHierarchy,
                    vector,
                    vector1,
                    trimOptions1,
                    scriptEnvironment1,
                    this.reflectionTargetsByCaller,
                    (ExistingSerializedClassesHandler) null,
                    (FixedClassesExclusionSet) null
            );
            trimProcessor1.performTrim();
            if (trimProcessor1.hasNoExcludedClasses()) {
                printWriter.println("All classes would be trimmed. You must change the trim exclusions to specify all application entry points.");
            } else if (trimProcessor1.hasNoIncludedClasses() && trimProcessor1.hasNoIncludedFields() && trimProcessor1.hasNoIncludedMethods()) {
                printWriter.println("No classes, fields or methods would be trimmed.");
            } else {
                trimProcessor1.reportTrimmedMembers(printWriter, this, true);
            }
        } finally {
            dialogCallback1.onDialogCancelled();
        }
    }

    @Override
    public List getNewFieldNames() {
        return this.newFieldNames;
    }

    @Override
    public boolean isObjectMethodSignature(Object object) {
        return this.objectMethods.containsKey(object);
    }

    public synchronized int openClasses(
            InputFileLocation[] inputFileLocations,
            SourceArchive[] sourceArchives1,
            Set set1,
            Set set2,
            Set set3,
            Set set4,
            Set set5,
            Set set6,
            String string,
            InputFileLocation[] inputFileLocations1,
            InputFileLocation[] inputFileLocations2,
            InputFileLocation[] inputFileLocations3,
            InputFileLocation[] inputFileLocations4,
            boolean bl,
            MessageReporter messageReporter1,
            DialogCallback dialogCallback1,
            OperationStatusCallback operationStatusCallback,
            ScriptEnvironment scriptEnvironment1,
            PrintWriter printWriter,
            PrintWriter printWriter1,
            MutableInt mutableInt,
            PrintWriter printWriter2
    ) throws ZkmException, IOException {
        Runtime runtime2;
        if (!HiddenOptionFlags.TEST_HIERARCHY) {
            messageReporter1.reportWarning(
                    "WARNING:",
                    "'ZKM_TEST_HIERARCHY' command line option set to 'false'. The inheritance hierarchy will not be tested for gaps. Gaps in the inheritance hierarchy can cause problems in the Trim and Name Obfuscation functions."
            );
            runtime2 = Runtime.getRuntime();
        } else {
            runtime2 = Runtime.getRuntime();
        }

        Runtime runtime1 = runtime2;
        long ba = System.currentTimeMillis();
        this.sourceArchives = sourceArchives1;
        this.skippedArchives = set1;
        if (set2 != null && set2.size() > 0) {
            this.skippedClassFiles = new TwoKeyMap();
            Iterator iterator = set2.iterator();

            while (iterator.hasNext()) {
                InputFileLocation inputFileLocation = (InputFileLocation) iterator.next();
                this.skippedClassFiles.putValue(inputFileLocation.getSourceArchive(), inputFileLocation.getName(), inputFileLocation);
            }
        }

        if (set3 != null && set3.size() > 0) {
            this.skippedXmlFiles = new TwoKeyMap();
            Iterator iterator2 = set3.iterator();

            while (iterator2.hasNext()) {
                InputFileLocation inputFileLocation4 = (InputFileLocation) iterator2.next();
                this.skippedXmlFiles.putValue(inputFileLocation4.getSourceArchive(), inputFileLocation4.getName(), inputFileLocation4);
            }
        }

        if (set4 != null && set4.size() > 0) {
            this.skippedYamlFiles = new TwoKeyMap();
            Iterator iterator3 = set4.iterator();

            while (iterator3.hasNext()) {
                InputFileLocation inputFileLocation5 = (InputFileLocation) iterator3.next();
                this.skippedYamlFiles.putValue(inputFileLocation5.getSourceArchive(), inputFileLocation5.getName(), inputFileLocation5);
            }
        }

        if (set5 != null && set5.size() > 0) {
            this.skippedPropertiesFiles = new TwoKeyMap();
            Iterator iterator4 = set5.iterator();

            while (iterator4.hasNext()) {
                InputFileLocation inputFileLocation6 = (InputFileLocation) iterator4.next();
                this.skippedPropertiesFiles.putValue(inputFileLocation6.getSourceArchive(), inputFileLocation6.getName(), inputFileLocation6);
            }
        }

        if (set6 != null && set6.size() > 0) {
            this.filteredOutFiles = new TwoKeyMap();
            Iterator iterator5 = set6.iterator();

            while (iterator5.hasNext()) {
                InputFileLocation inputFileLocation7 = (InputFileLocation) iterator5.next();
                this.filteredOutFiles.putValue(inputFileLocation7.getSourceArchive(), inputFileLocation7.getName(), inputFileLocation7);
            }
        }

        this.clearHierarchyCaches();
        this.programClasses = null;
        this.moduleInfoClasses = null;
        this.archivesByModuleName = null;
        this.moduleNameByArchive = null;
        this.classesByArchive = null;
        this.hasClassesOutsideArchives = false;
        this.classHierarchy = null;
        this.methodDeclaringClasses = null;
        this.methodsByClass = null;
        this.methodsByClassAndNameType = null;
        this.fieldsByName = null;
        this.fieldsBySignature = null;
        this.fieldsByClassNameDesc = null;
        this.rootPackageNode = new RootPackageNode();
        this.cumulativeNameMapping.clearMapping();
        this.originalToCurrentName.clear();
        this.currentToOriginalName.clear();
        this.fieldSignatureChanges.clear();
        this.methodSignatureChanges.clear();
        this.classNameStringRefs.clear();
        this.resourceNameStringRefs.clear();
        this.packageNameStringRefs.clear();
        this.fieldNameStringRefs.clear();
        this.methodNameStringRefs.clear();
        this.classNameStringConstants.clear();
        this.reflectionTargetsByCaller.clear();
        this.reflectionCallSites.clear();
        this.referenceLookupClass.clearValue();
        this.referenceObfuscationMap.clear();
        this.autoReflectionLookupClass.clearValue();
        this.autoReflectionMap.clear();
        this.methodKeyTriple.clearValue();
        this.methodKeyMap.clear();
        this.resourceFileIndex.clear();
        this.resourceFileNames.clear();
        this.jadFileArchives.clear();
        this.xmlFileLocations = null;
        this.resourceReferencedClasses = null;
        this.resourceReferencedFields = null;
        this.resourceReferencedMethods = null;
        this.xmlReferencedMethods = null;
        this.manifestEntryMethods = null;
        this.classpathLoader.resetReaderCaches();
        this.methodHandleTargets.clear();
        this.bootstrapMethodHandleTargets.clear();
        ClassHierarchyNode.disposeAllNodes();
        Thread.currentThread();
        if (this.skippedArchives != null && this.skippedArchives.size() > 0) {
            StringBuilder stringBuilder1 = new StringBuilder();
            StringBuilder stringBuilder = new StringBuilder();
            Iterator iterator1 = this.skippedArchives.iterator();

            while (iterator1.hasNext()) {
                SourceArchive sourceArchive1 = (SourceArchive) iterator1.next();
                String string1 = sourceArchive1.getFilePath();
                stringBuilder1.append(string1);
                stringBuilder.append(sourceArchive1.getQualifiedPath());
                if (iterator1.hasNext()) {
                    stringBuilder1.append(SystemEnvironmentConstants.PATH_SEPARATOR_CHAR);
                    stringBuilder.append(", ");
                }
            }

            if (this.verbose && stringBuilder.length() > 0) {
                String string3;
                if (bl) {
                    string3 = "Prepending unopened nested archive";
                } else {
                    string3 = "Prepending skipped archive";
                }

                String string5 = stringBuilder.indexOf(", ") == -1 ? "" : "s";
                printWriter2.println(string3 + string5 + " '" + stringBuilder + "' to classpath as temporary file" + string5 + " : '" + stringBuilder1 + "'");
            }

            this.classpath.prependClasspath(stringBuilder1.toString());
        }

        if (string != null) {
            if (string.length() > 0) {
                this.classpath.prependClasspath(string);
                if (this.verbose) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    HashSet hashSet = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(set2.size()));
                    Iterator iterator6 = set2.iterator();

                    while (iterator6.hasNext()) {
                        InputFileLocation inputFileLocation8 = (InputFileLocation) iterator6.next();
                        if (hashSet.add(inputFileLocation8.getName())) {
                            if (stringBuilder2.length() > 0) {
                                stringBuilder2.append(", ");
                            }

                            stringBuilder2.append(inputFileLocation8.getQualifiedName());
                        }
                    }

                    String string4 = stringBuilder2.indexOf(", ") == -1 ? "" : "es";
                    printWriter2.println("Prepending skipped class" + string4 + " '" + stringBuilder2 + "' to classpath within temporary file '" + string + "'");
                    this.classpathChanged = false;
                } else {
                    this.classpathChanged = false;
                }
            } else {
                this.classpathChanged = false;
            }
        } else {
            this.classpathChanged = false;
        }

        this.fieldNamesObfuscated = false;
        this.methodNamesObfuscated = false;
        this.classNamesChanged = false;
        this.parametersObfuscated = false;
        this.integerConstantsEncrypted = false;
        this.longConstantsEncrypted = false;
        this.openErrorMessage = null;
        List list2 = new Vector();
        Vector vector = new Vector();

        try {
            list2 = this.readInputClasses(inputFileLocations, vector, runtime1, printWriter, printWriter1, printWriter2);
        } catch (ZkmProcessingException zkmProcessingException1) {
            messageReporter1.reportFatalError("FATAL ERROR:", zkmProcessingException1.getMessage());
        }

        ClassRepository classRepository2;
        KlassMaster klassMaster;
        if (set2 != null) {
            if (set2.size() > 0) {
                Iterator iterator7 = set2.iterator();

                while (iterator7.hasNext()) {
                    InputFileLocation inputFileLocation9 = (InputFileLocation) iterator7.next();
                    inputFileLocation9.releaseResources();
                }

                classRepository2 = this;
                klassMaster = this.application;
            } else {
                classRepository2 = this;
                klassMaster = this.application;
            }
        } else {
            classRepository2 = this;
            klassMaster = this.application;
        }

        classRepository2.recordClassDigests(klassMaster, 2, 0);
        this.statusHolder.setValue("Analyzing classes");
        int be = list2.size();
        Collections.sort(list2);
        this.programClasses = new ProgramClass[be];

        for (int i = 0; i < be; i += 1) {
            this.programClasses[i] = (ProgramClass) list2.get(i);
            String string6 = this.programClasses[i].getClassName();
            ZkmAssert.assertTrue(
                    string6.equals("java/lang/Object") || this.programClasses[i].getSuperclassName() != null,
                    new String[]{"Class '" + string6 + "' appears to be corrupt (C) : '" + this.programClasses[i].getDisplayLocationName() + "'"}
            );
            if (string6.equals("java/lang/Object")
                    || string6.equals("java/lang/invoke/MethodHandle")
                    || this.programClasses[i].getSuperclassName().equals("java/lang/invoke/MethodHandle")) {
                if (operationStatusCallback != null) {
                    operationStatusCallback.setStatus((String) null);
                }

                messageReporter1.reportFatalError(
                        "FATAL ERROR:",
                        "Cannot open "
                                + (string6.equals("java/lang/Object") ? "java/lang/Object" : "java/lang/invoke/MethodHandle or its subclasses")
                                + " in this version : '"
                                + this.programClasses[i].getLocationName()
                                + "'"
                );
            }
        }

        ProgramClass[] programClass2 = this.programClasses;
        int bh = programClass2.length;

        for (int i = 0; i < bh; i += 1) {
            ProgramClass programClass1 = programClass2[i];
            if (programClass1.hasZkmAnnotationValues() || programClass1.hasZkmMethodAnnotations()) {
                programClass1.removeZkmAnnotations(scriptEnvironment1);
            }
        }

        this.moduleInfoClasses = new ModuleInfoClass[vector.size()];
        if (this.moduleInfoClasses.length > 0) {
            this.archivesByModuleName = new ListMultimap(this.moduleInfoClasses.length, HiddenOptionFlags.DETERMINISTIC_OUTPUT);
            this.moduleNameByArchive = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.sourceArchives.length));
            int bg = 0;
            int bt = 0;

            for (ModuleInfoClass[] moduleInfoClass1 = this.moduleInfoClasses; bt < moduleInfoClass1.length; moduleInfoClass1 = this.moduleInfoClasses) {
                ModuleInfoClass moduleInfoClass = (ModuleInfoClass) vector.get(bg);
                this.moduleInfoClasses[bg] = moduleInfoClass;
                String string7 = moduleInfoClass.getModuleName();
                Enumeration enumeration = moduleInfoClass.enumerateInputLocations();

                while (enumeration.hasMoreElements()) {
                    InputFileLocation inputFileLocation1 = (InputFileLocation) enumeration.nextElement();
                    if (inputFileLocation1.isArchiveEntry()) {
                        SourceArchive sourceArchive2 = inputFileLocation1.getSourceArchive();
                        this.archivesByModuleName.addValue(string7, sourceArchive2);
                        this.moduleNameByArchive.put(sourceArchive2, string7);
                    }
                }

                bg += 1;
                bt = bg;
            }

            SourceArchive[] sourceArchives2 = this.sourceArchives;
            bh = sourceArchives2.length;

            for (int i = 0; i < bh; i += 1) {
                SourceArchive sourceArchive6 = sourceArchives2[i];
                if (this.moduleNameByArchive.containsKey(sourceArchive6)) {
                    sourceArchive6.setModuleName((String) this.moduleNameByArchive.get(sourceArchive6));
                }
            }
        }

        ArrayList arrayList = new ArrayList(this.programClasses.length + 10);
        ProgramClass[] programClass3 = this.programClasses;
        int bj = programClass3.length;

        for (int i = 0; i < bj; i += 1) {
            ProgramClass programClass6 = programClass3[i];
            arrayList.add(programClass6);
            if (programClass6.hasVersionedVariants()) {
                Iterator iterator8 = programClass6.getVersionedVariants().iterator();

                while (iterator8.hasNext()) {
                    ClassFileBase classFileBase = (ClassFileBase) iterator8.next();
                    arrayList.add((ProgramClass) classFileBase);
                }
            }
        }

        ProgramClass[] programClass4 = ((com.zelix.klassmaster.classfile.ProgramClass[]) (arrayList.toArray(new ProgramClass[arrayList.size()])));
        this.autoReflectionHandledClasses = new IndexedValueRelation(programClass4, 4, false);
        this.referenceObfuscatedClasses = new IndexedValueRelation(programClass4, 4, false);
        this.parameterChangedClasses = new IndexedValueRelation(programClass4, 4, false);
        this.classesByArchive = new ListMultimap(this.sourceArchives.length);
        ProgramClass[] programClass5 = this.programClasses;
        int bm = programClass5.length;

        for (int i = 0; i < bm; i += 1) {
            ProgramClass programClass7 = programClass5[i];
            Enumeration enumeration1 = programClass7.enumerateInputLocations();

            while (enumeration1.hasMoreElements()) {
                InputFileLocation inputFileLocation2 = (InputFileLocation) enumeration1.nextElement();
                if (inputFileLocation2.isArchiveEntry()) {
                    SourceArchive sourceArchive3 = inputFileLocation2.getSourceArchive();
                    this.classesByArchive.addValue(sourceArchive3, programClass7);
                    if (!sourceArchive3.hasModuleName()) {
                        this.hasClassesOutsideArchives = true;
                    }
                } else {
                    this.hasClassesOutsideArchives = true;
                }
            }
        }

        this.classesByArchive.trimToSize();
        this.rootPackageNode.buildFromClasses(this.programClasses);

        try {
            this.classHierarchy = new ClassHierarchy(this.programClasses);
        } catch (ZkmProcessingException zkmProcessingException) {
            messageReporter1.reportFatalError("FATAL ERROR:", zkmProcessingException.getMessage());
        }

        this.indexMethods();
        this.indexFields();

        try {
            this.resolveReferences(
                    this.methodHandleTargets,
                    this.bootstrapMethodHandleTargets,
                    scriptEnvironment1 != null ? scriptEnvironment1.getIgnoreMissingReferencesSpec() : null,
                    scriptEnvironment1
            );
        } catch (ZkmClassNotFoundException zkmClassNotFoundException) {
            messageReporter1.reportProblem("ERROR:", zkmClassNotFoundException.getMessage());
            this.openErrorMessage = zkmClassNotFoundException.getMessage();
            if (operationStatusCallback != null) {
                operationStatusCallback.setStatus("'" + zkmClassNotFoundException.getClassName() + "' not found");
            }
        } catch (MemberNotFoundException memberNotFoundException) {
            messageReporter1.reportProblem("ERROR:", memberNotFoundException.getMessage());
            this.openErrorMessage = memberNotFoundException.getMessage();
            if (operationStatusCallback != null) {
                operationStatusCallback.setStatus("'" + memberNotFoundException.getMessage());
            }
        } catch (ZkmProcessingException zkmProcessingException4) {
            messageReporter1.reportFatalError("FATAL ERROR:", zkmProcessingException4.getMessage());
        }

        NestedMultiMap nestedMultiMap = new NestedMultiMap(this.programClasses.length * 5, 7, 2);
        NestedMultiMap nestedMultiMap1 = new NestedMultiMap(this.programClasses.length * 5, 7, 2);
        NestedMultiMap nestedMultiMap2 = new NestedMultiMap(this.programClasses.length * 5, 7, 2);
        this.collectBytecodeReferences(nestedMultiMap, nestedMultiMap1, nestedMultiMap2);

        try {
            this.analyzeClassCode(nestedMultiMap, nestedMultiMap1, nestedMultiMap2, printWriter1, mutableInt, this.verbose, messageReporter1);
        } catch (ZkmProcessingException zkmProcessingException3) {
            MessageReporter messageReporter2;
            String string9;
            if (operationStatusCallback != null) {
                operationStatusCallback.setStatus((String) null);
                messageReporter2 = messageReporter1;
                string9 = "FATAL ERROR:";
            } else {
                messageReporter2 = messageReporter1;
                string9 = "FATAL ERROR:";
            }

            messageReporter2.reportFatalError(string9, zkmProcessingException3.getMessage());
        }

        try {
            this.indexArchiveResourceFiles();
        } catch (ZkmProcessingException zkmProcessingException2) {
            MessageReporter messageReporter3;
            String string10;
            if (operationStatusCallback != null) {
                operationStatusCallback.setStatus((String) null);
                messageReporter3 = messageReporter1;
                string10 = "FATAL ERROR:";
            } else {
                messageReporter3 = messageReporter1;
                string10 = "FATAL ERROR:";
            }

            messageReporter3.reportFatalError(string10, zkmProcessingException2.getMessage());
        }

        ListMultimap listMultimap = new ListMultimap();
        int bo = 0;
        int bu = 0;

        for (SourceArchive[] sourceArchives3 = this.sourceArchives; bu < sourceArchives3.length; sourceArchives3 = this.sourceArchives) {
            if (this.skippedArchives == null || !this.skippedArchives.contains(this.sourceArchives[bo])) {
                String string8 = this.sourceArchives[bo].getFileName();
                ListMultimap listMultimap1;
                String string11;
                SourceArchive[] sourceArchives4;
                if (!ZkmFileUtils.caseSensitiveFileSystem) {
                    string8 = string8.toLowerCase();
                    listMultimap1 = listMultimap;
                    string11 = string8;
                    sourceArchives4 = this.sourceArchives;
                } else {
                    listMultimap1 = listMultimap;
                    string11 = string8;
                    sourceArchives4 = this.sourceArchives;
                }

                listMultimap1.addValue(string11, sourceArchives4[bo]);
            }

            bo += 1;
            bu = bo;
        }

        for (int i = 0; i < inputFileLocations1.length; i += 1) {
            try {
                JadFile jadFile1 = new JadFile(inputFileLocations1[i]);
                long br = -1L;

                try {
                    br = jadFile1.getJarSize();
                } catch (JadFileException jadFileException) {
                    messageReporter1.reportProblem("WARNING:", "Jad file '" + inputFileLocations1[i].getQualifiedName() + "' : " + jadFileException.getMessage());
                }

                String string2 = jadFile1.getJarFileName();
                if (!ZkmFileUtils.caseSensitiveFileSystem) {
                    string2 = string2.toLowerCase();
                }

                List list1 = listMultimap.getValues(string2);
                SourceArchive sourceArchive4 = null;
                if (list1 != null) {
                    if (list1.size() == 1) {
                        sourceArchive4 = (SourceArchive) list1.get(0);
                    } else {
                        for (int j = 0; j < list1.size(); j += 1) {
                            SourceArchive sourceArchive5 = (SourceArchive) list1.get(j);
                            if (sourceArchive5.getFileSize() == br) {
                                sourceArchive4 = sourceArchive5;
                                break;
                            }
                        }
                    }
                }

                HashMap hashMap;
                if (sourceArchive4 != null) {
                    hashMap = this.jadFileArchives;
                } else {
                    messageReporter1.reportProblem(
                            "WARNING:",
                            "JAD file '"
                                    + jadFile1.getJadFilePath()
                                    + "' has no associated JAR file '"
                                    + string2
                                    + "'"
                                    + (br > -1L && list1 != null && list1.size() > 0 ? " with length " + br : "")
                                    + ". Cannot automatically update JAR size in JAD file."
                    );
                    hashMap = this.jadFileArchives;
                }

                hashMap.put(jadFile1, sourceArchive4);
            } catch (ZkmException zkmException) {
                messageReporter1.reportError("ERROR:", "Jad file '" + inputFileLocations1[i].getQualifiedName() + "' : " + zkmException.getMessage());
            }
        }

        this.xmlFileLocations = inputFileLocations2;
        InputFileLocation[] inputFileLocations5 = this.xmlFileLocations;
        int bq = inputFileLocations5.length;

        for (int i = 0; i < bq; i += 1) {
            InputFileLocation inputFileLocation3 = inputFileLocations5[i];
            this.resourceFileIndex.addFile(inputFileLocation3.getFile());
        }

        this.yamlFileLocations = inputFileLocations3;
        this.propertiesFileLocations = inputFileLocations4;
        this.resourceReferencedClasses = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.programClasses.length));
        this.resourceReferencedFields = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.programClasses.length * 5));
        this.resourceReferencedMethods = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.programClasses.length * 5));
        this.xmlReferencedMethods = new TwoKeyMap(ZkmUtils.getPrimeCapacity(this.programClasses.length * 5));
        this.manifestEntryMethods = ZkmUtils.createHashMap();
        new ResourceFileAnalyzer(
                this.sourceArchives,
                this.skippedArchives,
                this.skippedXmlFiles,
                this.skippedYamlFiles,
                this.filteredOutFiles,
                Collections.enumeration(this.jadFileArchives.keySet()),
                new ArrayEnumeration(this.xmlFileLocations),
                this.resourceFileIndex,
                new ArrayEnumeration(this.yamlFileLocations),
                new ArrayEnumeration(this.propertiesFileLocations),
                this,
                this.classResolver,
                messageReporter1,
                this.resourceReferencedClasses,
                this.resourceReferencedFields,
                this.resourceReferencedMethods,
                this.xmlReferencedMethods,
                this.manifestEntryMethods
        );
        this.setChanged();
        this.notifyObservers();
        dialogCallback1.onDialogCancelled();
        long bb = System.currentTimeMillis();
        StringBuilder stringBuilder3 = new StringBuilder();
        if (this.programClasses.length > 0) {
            stringBuilder3.append(this.programClasses.length + " class" + (this.programClasses.length > 1 ? "es" : ""));
        }

        if (this.moduleInfoClasses.length > 0) {
            if (stringBuilder3.length() > 0) {
                stringBuilder3.append(" and ");
            }

            stringBuilder3.append(this.moduleInfoClasses.length + " " + "module-info.class" + " class" + (this.moduleInfoClasses.length > 1 ? "es" : ""));
        }

        if (stringBuilder3.length() > 0) {
            stringBuilder3.append(" opened in " + String.format("%d seconds. ", (bb - ba) / 1000L));
        } else {
            stringBuilder3.append("No classes opened. ");
        }

        stringBuilder3.append((runtime1.totalMemory() - runtime1.freeMemory()) / 1024L + "K of memory used.");
        this.statusHolder.setValue(stringBuilder3.toString());
        stringBuilder3.append(" " + runtime1.freeMemory() / 1024L + "K of memory free.");
        printWriter2.println(stringBuilder3.toString());
        return this.getTotalClassCount();
    }

    public void applyPackageRenamesToResources(HashMap hashMap, MessageReporter messageReporter1) throws ZkmException, IOException {
        EnumerableMap enumerableMap = new EnumerableMap(hashMap);
        HashMap hashMap1 = ZkmUtils.createHashMap();
        Iterator iterator = this.resourceFileNames.iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            String string1 = ResourcePathTranslator.renameResourceParentPath(string, enumerableMap);
            String string2 = null;
            if (!string1.equals(string)) {
                iterator.remove();
                string2 = ((java.lang.String) (hashMap1.put(string1, string)));
            }

            if (string2 != null || string1.equals(string) && hashMap1.containsKey(string1)) {
                messageReporter1.reportFatalError(
                        "File path clash",
                        "Files '"
                                + (string2 == null ? (String) hashMap1.get(string1) : string2)
                                + "' and '"
                                + string
                                + "' both mapped to '"
                                + string1
                                + "' after package name changes. Did you specify inconsistent input change logs?"
                );
            }
        }

        iterator = hashMap1.keySet().iterator();

        while (iterator.hasNext()) {
            String string3 = (String) iterator.next();
            this.resourceFileNames.add(string3);
        }
    }

    @Override
    public final void registerField(ProgramClass programClass1, FieldInfo fieldInfo) {
        String string = fieldInfo.getSourceName();
        FieldInfo fieldInfo1 = (FieldInfo) this.fieldsBySignature.putValue(fieldInfo.getSignature(), programClass1, fieldInfo);
        String[] strings = new String[]{
                fieldInfo1 == null
                        ? ""
                        : "Duplicate field (1) '" + programClass1.getLocationName() + "' '" + fieldInfo.toDisplayString() + "' : '" + fieldInfo1.toDisplayString() + "'"
        };
        ZkmAssert.assertTrue(fieldInfo1 == null, strings);
        FieldInfo fieldInfo2 = (FieldInfo) this.fieldsByName.putValue(string, programClass1, fieldInfo, fieldInfo);
        strings = new String[]{
                fieldInfo2 == null
                        ? ""
                        : "Duplicate field (2) '" + programClass1.getLocationName() + "' '" + fieldInfo.toDisplayString() + "' : '" + fieldInfo2.toDisplayString() + "'"
        };
        ZkmAssert.assertTrue(fieldInfo2 == null, strings);
        FieldInfo fieldInfo3 = (FieldInfo) this.fieldsByClassNameDesc.putValue(programClass1.getClassName(), string, fieldInfo.getDescriptor(), fieldInfo);
        strings = new String[]{
                fieldInfo3 == null
                        ? ""
                        : "Duplicate field (3) '" + programClass1.getLocationName() + "' '" + fieldInfo.toDisplayString() + "' : '" + fieldInfo3.toDisplayString() + "'"
        };
        ZkmAssert.assertTrue(fieldInfo3 == null, strings);
    }

    public void resetClassReferences(ClassFileBase[] classFileBases, ScriptEnvironment scriptEnvironment1, boolean bl) throws ZkmException, IOException {
        for (int i = 0; i < classFileBases.length; i++) {
            ClassFileBase classFileBase = classFileBases[i];
            boolean bl1 = bl;
            ClassRepository classRepository2 = this;
            ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
            classFileBase.updateMethodReferences(scriptEnvironment2, classRepository2, bl1);
            if (classFileBase.hasVersionedVariants()) {
                Iterator iterator = classFileBase.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ClassFileBase classFileBase1 = (ClassFileBase) iterator.next();
                    bl1 = bl;
                    classRepository2 = this;
                    scriptEnvironment2 = scriptEnvironment1;
                    classFileBase1.updateMethodReferences(scriptEnvironment2, classRepository2, bl1);
                }
            }
        }
    }

    @Override
    public final boolean implementsAnnotatedInterfaceByOriginalName(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        try {
            boolean bl = string1.indexOf(42) != -1;
            return this.implementsAnnotatedInterfaceOriginal(string, string1, bl, aSTComplexAnnotationSpecifier);
        } catch (StackOverflowError stackOverflowError) {
            String string2 = "reporting corrupt inheritance heirarchy";
            ClassFileBase classFileBase = this.classResolver.getClassFile(string, string2);
            throw new CorruptHierarchyException(
                    stackOverflowError.getMessage() + " : Class " + classFileBase.getDisplayLocationName() + " has corrupt hierarchy (D)", stackOverflowError
            );
        }
    }

    public void removeStringRefsFrom(TwoKeyMap twoKeyMap, Set set1) {
        SnapshotEnumeration snapshotEnumeration = new SnapshotEnumeration(twoKeyMap.keys());

        while (snapshotEnumeration.hasMoreElements()) {
            Object object = snapshotEnumeration.nextElement();
            Map map1 = twoKeyMap.getInnerMap(object);
            CollectionSnapshotEnumeration collectionSnapshotEnumeration = new CollectionSnapshotEnumeration(map1.keySet());

            while (collectionSnapshotEnumeration.hasMoreElements()) {
                TrackedValue trackedValue = (TrackedValue) collectionSnapshotEnumeration.nextElement();
                if (trackedValue instanceof StringConstantNameRef) {
                    ResolvedStringConstant resolvedStringConstant = ((StringConstantNameRef) trackedValue).getStringConstant();
                    if (set1.contains(resolvedStringConstant)) {
                        twoKeyMap.removeValue(object, trackedValue);
                    }
                }
            }
        }
    }

    public final RootPackageNode getRootPackageNode() {
        return this.rootPackageNode;
    }

    public Set getMethodHandleTargets() {
        return this.methodHandleTargets;
    }

    public boolean isModuleInfoFile(InputFileLocation inputFileLocation) {
        String string = inputFileLocation.getName();
        String string1;
        if (inputFileLocation.isPlainFile()) {
            string1 = string.substring(string.lastIndexOf(ZkmFileUtils.FILE_SEPARATOR) + 1);
            if (!ZkmFileUtils.caseSensitiveFileSystem) {
                string1 = string1.toLowerCase();
            }
        } else {
            string1 = string.substring(string.lastIndexOf("/") + 1);
        }

        return string1.equals("module-info.class");
    }

    public boolean extendsAnnotatedClass(String string, String string1, boolean bl, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        if (string.equals("java/lang/Object")) {
            return false;
        } else if (string.equals(string1)) {
            return false;
        } else {
            String string2 = this.getSuperclassName(string);
            if (!bl && string2.equals(string1)) {
                return aSTComplexAnnotationSpecifier.matchesAnyAnnotation(this.getClassAnnotations(string2, null, false));
            } else if (bl
                    && ZkmStringUtils.matchesWildcard(string2, string1)
                    && aSTComplexAnnotationSpecifier.matchesAnyAnnotation(this.getClassAnnotations(string2, null, false))) {
                return true;
            } else {
                return string2.equals("java/lang/Object") ? false : this.extendsAnnotatedClass(string2, string1, bl, aSTComplexAnnotationSpecifier);
            }
        }
    }

    public boolean isLongConstantsEncrypted() {
        return this.longConstantsEncrypted;
    }

    public void markIntegerConstantsEncrypted() {
        this.integerConstantsEncrypted = true;
    }

    public boolean isClasspathChanged() {
        return this.classpathChanged;
    }

    public final synchronized void obfuscate(
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
            int bn,
            boolean bl9,
            String string6,
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
            String string10,
            String string11,
            boolean bl15,
            MessageReporter messageReporter1,
            DialogCallback dialogCallback1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        try {
            String string12 = this.application.getEncodedExpiryTime();
            checkEvaluationExpiry(string12, this.application.getEncodedEvaluationPeriod(), this.application.getEncodedGracePeriod(), this);
            if (!string12.equals(KlassMaster.validatedExpiryCode)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
                TimeZone timeZone = TimeZone.getDefault();
                simpleDateFormat.setTimeZone(timeZone);
                long bp = ObfuscatedLongDecoder.decodeLong(string12);
                Date date = new Date(bp);
                String string13 = simpleDateFormat.format(date);
                String string14 = string13 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (17)" : "");
                throw new EvaluationExpiredException(string14);
            }
        } catch (RuntimeException runtimeException) {
            if (HiddenOptionFlags.DEBUG_LICENSE_CHECK) {
            }

            scriptEnvironment1.getLogWriter().print(' ');
            return;
        }

        if (string10 != null && string10.length() > 0) {
            this.newFieldNames = ObfuscationEngine.readNameListFile(string10, "newFieldNameFile", scriptEnvironment1);
        }

        if (string11 != null && string11.length() > 0) {
            this.newMethodNames = ObfuscationEngine.readNameListFile(string11, "newMethodNameFile", scriptEnvironment1);
        }

        new ObfuscationEngine(
                this,
                this.programClasses,
                this.moduleInfoClasses,
                this.rootPackageNode,
                this.statusHolder,
                changeLogInputFiles,
                bl,
                printWriter,
                string,
                string1,
                list1,
                list2,
                list3,
                list4,
                list5,
                list6,
                list7,
                list8,
                list9,
                list10,
                list11,
                list12,
                list13,
                list14,
                list15,
                list16,
                list17,
                list18,
                list19,
                list20,
                list21,
                list22,
                list23,
                bl1,
                string2,
                bl2,
                bl3,
                bl4,
                bl5,
                bl6,
                bl7,
                ba,
                bb,
                bc,
                bd,
                be,
                bf,
                bg,
                bh,
                bi,
                bj,
                bl8,
                string3,
                bk,
                bm,
                string4,
                string5,
                this.autoReflectionLookupClass,
                this.autoReflectionMap,
                this.autoReflectionHandledClasses,
                bn,
                bl9,
                string6,
                this.referenceLookupClass,
                this.referenceObfuscationMap,
                this.referenceObfuscatedClasses,
                bl10,
                bl11,
                bl12,
                mixedCaseNamesMode1,
                bl13,
                bl14,
                bo,
                string7,
                integer,
                string8,
                string9,
                this.newFieldNames,
                this.newMethodNames,
                bl15,
                new ReadOnlyMultiMapView(this.classesByArchive),
                this.methodKeyTriple,
                this.methodKeyMap,
                this.parameterChangedClasses,
                this.manifestEntryMethods,
                messageReporter1,
                dialogCallback1,
                scriptEnvironment1
        );
    }

    public final void reindexRenamedField(ProgramClass programClass1, FieldInfo fieldInfo, String string) {
        String string1 = fieldInfo.getSourceName();
        String string2 = fieldInfo.getDescriptor();
        String string3 = programClass1.getClassName();
        FieldInfo fieldInfo1 = (FieldInfo) this.fieldsByName.removeValue(string, programClass1, fieldInfo);
        fieldInfo1 = (FieldInfo) this.fieldsByName.putValue(string1, programClass1, fieldInfo, fieldInfo);
        fieldInfo1 = (FieldInfo) this.fieldsByClassNameDesc.removeValue(string3, string, string2);
        fieldInfo1 = (FieldInfo) this.fieldsByClassNameDesc.putValue(string3, string1, string2, fieldInfo);
        fieldInfo1 = (FieldInfo) this.fieldsBySignature.removeValue(new FieldSignature(string, fieldInfo.getDescriptor()), programClass1);
        fieldInfo1 = (FieldInfo) this.fieldsBySignature.putValue(fieldInfo.getSignature(), programClass1, fieldInfo);
    }

    @Override
    public boolean isHierarchyMarked() {
        return this.methodNamesObfuscated;
    }

    @Override
    public final boolean isSubclassByOriginalName(String string, String string1) throws ZkmException, IOException {
        try {
            boolean bl = string1.indexOf(42) != -1;
            return this.isSubclassOfOriginal(string, string1, bl);
        } catch (StackOverflowError stackOverflowError) {
            ClassFileBase classFileBase = this.classResolver
                    .getClassFile((String) ZkmUtils.mapOrSelf(string, this.originalToCurrentName), "reporting corrupt inheritance hierarchy");
            throw new CorruptHierarchyException(
                    stackOverflowError.getMessage() + " : Class " + classFileBase.getDisplayLocationName() + " has corrupt inheritance hierarchy (C) '" + string + "'",
                    stackOverflowError
            );
        }
    }

    @Override
    public final FieldInfo findField(String string, String string1, String string2) {
        String string3 = string2;
        ThreeKeyConcurrentMap threeKeyConcurrentMap;
        if (!ConstantPoolEntry.isFieldDescriptor(string3)) {
            string3 = ConstantPoolEntry.toDescriptor(string3);
            threeKeyConcurrentMap = this.fieldsByClassNameDesc;
        } else {
            threeKeyConcurrentMap = this.fieldsByClassNameDesc;
        }

        return (FieldInfo) threeKeyConcurrentMap.getValue(string, string1, string3);
    }

    public final Enumeration enumerateHierarchyNodes() {
        return this.classHierarchy.enumerateOrderedNodes();
    }

    public ReadOnlyMultiMap getFieldSignatureChanges() {
        return new ReadOnlyMultiMap(this.fieldSignatureChanges);
    }

    public synchronized void renameFieldPrefix(String string, String string1) throws ZkmException, IOException {
        String string2 = FieldInfo.toIndefiniteArticleName(string);
        String string3 = FieldInfo.toIndefiniteArticleName(string1);
        Enumeration enumeration = this.fieldsByName.keys();
        HashMap hashMap = ZkmUtils.createHashMap();
        TwoKeyMap twoKeyMap = new TwoKeyMap();

        while (enumeration.hasMoreElements()) {
            String string4 = (String) enumeration.nextElement();
            if (string4.startsWith(string2)) {
                Enumeration enumeration1 = this.fieldsByName.getTwoKeyMap(string4).keys();

                while (enumeration1.hasMoreElements()) {
                    ProgramClass programClass1 = (ProgramClass) enumeration1.nextElement();
                    if (!hashMap.containsKey(programClass1)) {
                        programClass1.renameFieldsWithPrefix(string2, string3, twoKeyMap, this);
                        hashMap.put(programClass1, programClass1);
                    }
                }
            }
        }

        int ba = 0;
        int bb = ba;

        for (ProgramClass[] programClass2 = this.programClasses; bb < programClass2.length; programClass2 = this.programClasses) {
            this.programClasses[ba].updateFieldReferences((ScriptEnvironment) null, this);
            bb = ++ba;
        }
    }

    public void trim(
            TrimOptions trimOptions1,
            List list1,
            List list2,
            List list3,
            List list4,
            PrintWriter printWriter,
            MessageReporter messageReporter1,
            DialogCallback dialogCallback1,
            OperationStatusCallback operationStatusCallback,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        if (this.classpathChanged) {
            messageReporter1.reportFatalError(
                    "FATAL ERROR:", "Classpath has been changed but the classes have not been reopened. You must reopen the classes. (B)"
            );
        } else if (!this.hasProgramClasses()) {
            messageReporter1.reportInfo("TRIM MESSAGE:", "No classes opened other than module-info.class so no classes, fields or methods were trimmed.");
        } else {
            this.statusHolder.setValue("Performing trim analysis");

            try {
                FixedClassesExclusionSet fixedClassesExclusionSet1 = null;
                if (list4 != null && list4.size() > 0) {
                    fixedClassesExclusionSet1 = new FixedClassesExclusionSet(this, list4, scriptEnvironment1);
                    scriptEnvironment1.setFixedClassesExclusionSet(fixedClassesExclusionSet1);
                }

                ExistingSerializedClassesHandler existingSerializedClassesHandler = null;
                if (list3 != null && list3.size() > 0) {
                    existingSerializedClassesHandler = new ExistingSerializedClassesHandler(this, list3, scriptEnvironment1, fixedClassesExclusionSet1, null);
                }

                TrimProcessor trimProcessor1 = new TrimProcessor(
                        this,
                        this.classHierarchy,
                        list1,
                        list2,
                        trimOptions1,
                        scriptEnvironment1,
                        this.reflectionTargetsByCaller,
                        existingSerializedClassesHandler,
                        fixedClassesExclusionSet1
                );
                int ba = 0;
                int bg = 0;

                for (ProgramClass[] programClass12 = this.programClasses; bg < programClass12.length; programClass12 = this.programClasses) {
                    ProgramClass programClass1 = this.programClasses[ba];
                    if (fixedClassesExclusionSet1 == null || !fixedClassesExclusionSet1.isClassExcluded(programClass1)) {
                        programClass1.trimAttributes(trimProcessor1, trimOptions1, scriptEnvironment1, printWriter);
                    }

                    ba += 1;
                    bg = ba;
                }

                trimProcessor1.performTrim();
                Thread.yield();
                if (!trimProcessor1.hasNoExcludedClasses()) {
                    label466:
                    {
                        ObservableHolder observableHolder;
                        if (trimProcessor1.hasNoIncludedClasses()) {
                            if (trimProcessor1.hasNoIncludedFields()) {
                                if (trimProcessor1.hasNoIncludedMethods()) {
                                    messageReporter1.reportInfo("TRIM MESSAGE:", "No classes, fields or methods were trimmed.");
                                    this.statusHolder.setValue("Trimming unused attributes");
                                    break label466;
                                }

                                observableHolder = this.statusHolder;
                            } else {
                                observableHolder = this.statusHolder;
                            }
                        } else {
                            observableHolder = this.statusHolder;
                        }

                        observableHolder.setValue("Trimming unused classes, fields and methods");
                    }

                    ArrayList arrayList = trimProcessor1.getKeptClassesWithTrimmedFields();

                    for (int i = 0; i < arrayList.size(); i += 1) {
                        ProgramClass programClass2 = (ProgramClass) arrayList.get(i);
                        String string = programClass2.getClassName();
                        EnumerableMap enumerableMap = trimProcessor1.getTrimmedFieldsOfClass(programClass2);
                        programClass2.removeFields(enumerableMap);
                        if (!programClass2.isVersionedVariant()) {
                            Enumeration enumeration = enumerableMap.keys();

                            while (enumeration.hasMoreElements()) {
                                FieldInfo fieldInfo = (FieldInfo) enumeration.nextElement();
                                FieldInfo fieldInfo2 = (FieldInfo) this.fieldsByClassNameDesc.removeValue(string, fieldInfo.getSourceName(), fieldInfo.getDescriptor());
                                this.fieldSignatureChanges.removeValue(programClass2, fieldInfo.getOriginalSignature());
                            }
                        }
                    }

                    ArrayList arrayList1 = trimProcessor1.getKeptClassesWithTrimmedMethods();

                    for (int i = 0; i < arrayList1.size(); i += 1) {
                        ProgramClass programClass4 = (ProgramClass) arrayList1.get(i);
                        EnumerableMap enumerableMap1 = trimProcessor1.getTrimmedMethodsOfClass(programClass4);
                        programClass4.removeMethods(enumerableMap1);
                        if (!programClass4.isVersionedVariant()) {
                            Enumeration enumeration4 = enumerableMap1.keys();

                            while (enumeration4.hasMoreElements()) {
                                MethodInfo methodInfo3 = (MethodInfo) enumeration4.nextElement();
                                MethodInfo methodInfo7 = (MethodInfo) this.methodsByClass.removeValue(programClass4, methodInfo3.getSignature());
                                this.methodsByClassAndNameType.removeValue(programClass4, methodInfo3.getNameTypeSignature(), methodInfo3);
                                this.methodSignatureChanges.removeValue(programClass4, methodInfo3.getOriginalSignature());
                            }
                        }

                        Iterator iterator1 = this.bootstrapMethodHandleTargets.iterator();

                        while (iterator1.hasNext()) {
                            MethodInfo methodInfo4 = (MethodInfo) iterator1.next();
                            if (enumerableMap1.containsKey(methodInfo4)) {
                                iterator1.remove();
                            }
                        }

                        Iterator iterator2 = this.methodHandleTargets.iterator();

                        while (iterator2.hasNext()) {
                            MethodInfo methodInfo1 = (MethodInfo) iterator2.next();
                            if (enumerableMap1.containsKey(methodInfo1)) {
                                iterator2.remove();
                            }
                        }
                    }

                    Enumeration enumeration1 = trimProcessor1.getIncludedClasses();

                    while (enumeration1.hasMoreElements()) {
                        ProgramClass programClass5 = (ProgramClass) enumeration1.nextElement();
                        this.fieldsByClassNameDesc.removeTwoKeyMap(programClass5.getClassName());
                        this.methodsByClass.removeInnerMap(programClass5);
                        this.methodsByClassAndNameType.removeMultimap(programClass5);
                        Map map1 = this.classNameStringRefs.removeInnerMap(programClass5);
                        if (map1 != null) {
                            Iterator iterator4 = map1.keySet().iterator();

                            while (iterator4.hasNext()) {
                                TrackedValue trackedValue = (TrackedValue) iterator4.next();
                                if (trackedValue instanceof StringConstantNameRef) {
                                    StringConstantNameRef stringConstantNameRef = (StringConstantNameRef) trackedValue;
                                    this.classNameStringConstants.remove(stringConstantNameRef.getStringConstant());
                                }
                            }
                        }
                    }

                    Thread.yield();
                    Enumeration enumeration2 = trimProcessor1.enumerateTrimmedFields();

                    while (enumeration2.hasMoreElements()) {
                        FieldInfo fieldInfo1 = (FieldInfo) enumeration2.nextElement();
                        if (!fieldInfo1.getProgramClass().isVersionedVariant()) {
                            FieldInfo fieldInfo3 = (FieldInfo) this.fieldsByName.removeValue(fieldInfo1.getSourceName(), fieldInfo1.getProgramClass(), fieldInfo1);
                            FieldInfo fieldInfo4 = (FieldInfo) this.fieldsBySignature.removeValue(fieldInfo1.getSignature(), fieldInfo1.getProgramClass());
                            this.fieldNameStringRefs.removeInnerMap(fieldInfo1);
                        }
                    }

                    Enumeration enumeration3 = trimProcessor1.enumerateTrimmedMethods();

                    while (enumeration3.hasMoreElements()) {
                        MethodInfo methodInfo2 = (MethodInfo) enumeration3.nextElement();
                        ProgramClass programClass6 = methodInfo2.getOwnerProgramClass();
                        if (!programClass6.isVersionedVariant()) {
                            this.methodDeclaringClasses.removeValue(methodInfo2.getJvmName(), methodInfo2.getSignature(), programClass6);
                            this.methodNameStringRefs.removeInnerMap(methodInfo2);
                            this.reflectionTargetsByCaller.removeKey(methodInfo2);
                            this.reflectionCallSites.removeKey(methodInfo2);
                        }
                    }

                    this.classHierarchy.trimInnerClassesAttributes(trimProcessor1);
                    this.classHierarchy.trimNestAttributes(trimProcessor1);
                    this.classHierarchy.trimPermittedSubclasses(trimProcessor1);
                    ArrayList arrayList2 = new ArrayList(this.programClasses.length);
                    int bf = 0;
                    bg = 0;

                    for (ProgramClass[] programClass10 = this.programClasses; bg < programClass10.length; programClass10 = this.programClasses) {
                        ProgramClass programClass7 = this.programClasses[bf];
                        if (trimProcessor1.isClassOrOuterExcluded(programClass7)) {
                            arrayList2.add(programClass7);
                        } else {
                            String string3 = programClass7.getClassName();
                            String string4 = (String) this.currentToOriginalName.remove(string3);
                            if (string4 != null) {
                                String string5 = (String) this.originalToCurrentName.remove(string4);
                            }

                            if (this.fieldSignatureChanges != null) {
                                this.fieldSignatureChanges.removeInnerMap(string3);
                            }

                            if (this.methodSignatureChanges != null) {
                                this.methodSignatureChanges.removeInnerMap(string3);
                            }
                        }

                        bf += 1;
                        bg = bf;
                    }

                    Iterator iterator3 = this.bootstrapMethodHandleTargets.iterator();

                    while (iterator3.hasNext()) {
                        MethodInfo methodInfo5 = (MethodInfo) iterator3.next();
                        if (trimProcessor1.isClassIncluded(methodInfo5.getOwnerProgramClass())) {
                            iterator3.remove();
                        }
                    }

                    Iterator iterator5 = this.methodHandleTargets.iterator();

                    while (iterator5.hasNext()) {
                        MethodInfo methodInfo6 = (MethodInfo) iterator5.next();
                        if (trimProcessor1.isClassIncluded(methodInfo6.getOwnerProgramClass())) {
                            iterator5.remove();
                        }
                    }

                    this.programClasses = ((com.zelix.klassmaster.classfile.ProgramClass[]) (arrayList2.toArray(new ProgramClass[arrayList2.size()])));
                    Arrays.sort(this.programClasses);
                    ClassHierarchyNode.disposeAllNodes();
                    this.classHierarchy.build(this.programClasses);
                    this.rootPackageNode.buildFromClasses(this.programClasses);
                    ArrayList arrayList3 = new ArrayList(this.programClasses.length);
                    ProgramClass[] programClass8 = this.programClasses;
                    int bb = programClass8.length;

                    for (int i = 0; i < bb; i += 1) {
                        ProgramClass programClass3 = programClass8[i];
                        if (!programClass3.isGenerated()) {
                            arrayList3.add(programClass3);
                            if (programClass3.hasVersionedVariants()) {
                                Iterator iterator = programClass3.getVersionedVariants().iterator();

                                while (iterator.hasNext()) {
                                    ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                                    arrayList3.add((ProgramClass) classFileBase);
                                }
                            }
                        }
                    }

                    programClass8 = ((com.zelix.klassmaster.classfile.ProgramClass[]) (arrayList3.toArray(new ProgramClass[arrayList3.size()])));
                    this.autoReflectionHandledClasses.retainValues(programClass8);
                    this.referenceObfuscatedClasses.retainValues(programClass8);
                    this.parameterChangedClasses.retainValues(programClass8);
                    bb = 0;
                    bg = 0;

                    for (ProgramClass[] programClass11 = this.programClasses; bg < programClass11.length; programClass11 = this.programClasses) {
                        ProgramClass programClass9 = this.programClasses[bb];
                        programClass9.trimUnusedReferences(trimProcessor1);
                        if (programClass9.hasVersionedVariants()) {
                            Iterator iterator6 = programClass9.getVersionedVariants().iterator();

                            while (iterator6.hasNext()) {
                                ClassFileBase classFileBase1 = (ClassFileBase) iterator6.next();
                                ((ProgramClass) classFileBase1).trimUnusedReferences(trimProcessor1);
                            }
                        }

                        bb += 1;
                        bg = bb;
                    }

                    trimProcessor1.reportTrimmedMembers(printWriter, this, false);
                    ClassMemberSets classMemberSets = scriptEnvironment1.getTrimExclusionMemberSets();
                    classMemberSets.retainAcceptedMembers(trimProcessor1);
                    this.setChanged();
                    this.notifyObservers();
                    return;
                }

                MessageReporter messageReporter2;
                String string6;
                if (operationStatusCallback != null) {
                    operationStatusCallback.setStatus((String) null);
                    messageReporter2 = messageReporter1;
                    string6 = "TRIM ERROR:";
                } else {
                    messageReporter2 = messageReporter1;
                    string6 = "TRIM ERROR:";
                }

                String string1 = "All classes would be trimmed. You must change the trim exclusions to specify all application entry points.";
                String string2 = string6;
                messageReporter2.reportSeriousError(string2, string1);
            } catch (ClassLoadFailureException classLoadFailureException) {
                messageReporter1.reportError("FILE ERROR:", classLoadFailureException.getMessage());
                return;
            } catch (ZkmProcessingException zkmProcessingException) {
                messageReporter1.reportError("SERIOUS ERROR:", zkmProcessingException.getMessage());
                return;
            } finally {
                dialogCallback1.onDialogCancelled();
            }
        }
    }

    public static ClassFileBase loadClassFile(ClasspathClassLoader classpathClassLoader1, String string, boolean bl) throws ZkmException, IOException {
        ClasspathClassFile classpathClassFile = null;
        if (!bl) {
            classpathClassFile = classpathClassLoader1.readUncachedClassFile(string);
        }

        if (classpathClassFile == null) {
            classpathClassFile = classpathClassLoader1.tryLoadClassFile(string, !bl);
        }

        return classpathClassFile;
    }

    private boolean isSubclassOf(String string, String string1, boolean bl) throws ZkmException, IOException {
        if (string.equals("java/lang/Object")) {
            return false;
        }

        if (string1.equals("java/lang/Object")) {
            return true;
        }

        if (string.equals(string1)) {
            return false;
        }

        Boolean boolean1 = (Boolean) this.superclassCache.getValue(string1, string);
        if (boolean1 != null) {
            return boolean1;
        }

        label47:
        {
            String string2 = this.getSuperclassName(string);
            String string3;
            String string4;
            if (!bl) {
                if (string2.equals(string1)) {
                    break label47;
                }

                string3 = string2;
                string4 = "java/lang/Object";
            } else {
                if (ZkmStringUtils.matchesWildcard(string2, string1)) {
                    break label47;
                }

                string3 = string2;
                string4 = "java/lang/Object";
            }

            if (string3.equals(string4)) {
                this.superclassCache.putValue(string1, string, CACHED_FALSE);
                return false;
            }

            boolean bl1 = this.isSubclassOf(string2, string1, bl);
            this.superclassCache.putValue(string1, string, bl1 ? CACHED_TRUE : CACHED_FALSE);
            return bl1;
        }

        this.superclassCache.putValue(string1, string, CACHED_TRUE);
        return true;
    }

    @Override
    public final synchronized Enumeration enumerateClassesDeclaringMethod(MethodSignature methodSignature1) {
        List list1 = this.methodDeclaringClasses.getValues(methodSignature1.getName(), methodSignature1);
        return list1 != null ? Collections.enumeration(list1) : null;
    }

    public void readInputClass(
            InputFileLocation inputFileLocation,
            List list1,
            List list2,
            ListenerRegistry listenerRegistry1,
            Map map1,
            Map map2,
            PairMultiMap pairMultiMap,
            PairMultiMap pairMultiMap1,
            ThreeKeyMultiMap threeKeyMultiMap,
            PrintWriter printWriter,
            Runtime runtime1
    ) throws ZkmException, IOException {
        try {
            MutableInt mutableInt = new MutableInt(0);
            ClassFileInputStream classFileInputStream = ClassFileInputStream.fromStream(inputFileLocation.openInputStream(mutableInt), mutableInt.getValue());
            int ba = 0;
            boolean bl = false;
            long bb = 2000L;
            MutableInt mutableInt1 = new MutableInt();

            do {
                try {
                    if (this.isModuleInfoFile(inputFileLocation)) {
                        boolean bl1 = false;
                        if (inputFileLocation.isArchiveEntry()) {
                            bl1 = ArchiveManifest.isVersionedEntryPath(inputFileLocation.getName(), mutableInt1);
                        }

                        ModuleInfoClass moduleInfoClass = new ModuleInfoClass(
                                classFileInputStream, inputFileLocation, this.statusHolder, listenerRegistry1, printWriter, threeKeyMultiMap
                        );
                        String string = moduleInfoClass.getModuleName();
                        if (bl1) {
                            pairMultiMap1.addPair(string, moduleInfoClass, integerCache.valueOf(mutableInt1.getValue()));
                        } else {
                            ModuleInfoClass moduleInfoClass1 = ((com.zelix.klassmaster.classfile.ModuleInfoClass) (map2.put(string, moduleInfoClass)));
                            if (moduleInfoClass1 != null) {
                                map2.put(string, moduleInfoClass1);
                                InputFileLocation inputFileLocation1 = moduleInfoClass1.getInputLocation();
                                if (inputFileLocation.isPlainFile() && inputFileLocation1.isPlainFile()) {
                                    throw new ClassFileFormatException(
                                            "Module '"
                                                    + moduleInfoClass.getModuleName()
                                                    + "' found more than once. File "
                                                    + inputFileLocation1.getName()
                                                    + " used. File "
                                                    + inputFileLocation.getName()
                                                    + " ignored."
                                    );
                                }

                                moduleInfoClass1.addInputLocation(inputFileLocation);
                                if (!Arrays.equals(moduleInfoClass.getClassDigest(), moduleInfoClass1.getClassDigest())) {
                                    throw new ClassFileFormatException(
                                            "Multiple versions of module '"
                                                    + moduleInfoClass.getModuleName()
                                                    + "' detected. File "
                                                    + inputFileLocation1.getQualifiedName()
                                                    + " used. File "
                                                    + inputFileLocation.getQualifiedName()
                                                    + " ignored."
                                    );
                                }
                            } else {
                                list2.add(moduleInfoClass);
                            }
                        }

                        if (!inputFileLocation.isArchiveEntry()) {
                            throw new ZkmProcessingException(
                                    "Module '" + moduleInfoClass.getModuleName() + "' not in an archive. : '" + inputFileLocation.getName() + "'"
                            );
                        }
                    } else {
                        boolean bl2 = false;
                        if (inputFileLocation.isArchiveEntry()) {
                            bl2 = ArchiveManifest.isVersionedEntryPath(inputFileLocation.getName(), mutableInt1);
                        }

                        ProgramClass programClass1 = new ProgramClass(
                                classFileInputStream, inputFileLocation, this.statusHolder, listenerRegistry1, printWriter, threeKeyMultiMap
                        );
                        String string1 = programClass1.getClassName();
                        if (bl2) {
                            pairMultiMap.addPair(string1, programClass1, integerCache.valueOf(mutableInt1.getValue()));
                        } else {
                            ProgramClass programClass2 = ((com.zelix.klassmaster.classfile.ProgramClass) (map1.put(string1, programClass1)));
                            if (programClass2 != null) {
                                map1.put(string1, programClass2);
                                InputFileLocation inputFileLocation2 = programClass2.getInputLocation();
                                if (inputFileLocation.isPlainFile() && inputFileLocation2.isPlainFile()) {
                                    throw new ClassFileFormatException(
                                            "Class "
                                                    + ZkmUtils.slashesToDots(string1)
                                                    + " found more than once. File "
                                                    + inputFileLocation2.getName()
                                                    + " used. File "
                                                    + inputFileLocation.getName()
                                                    + " ignored."
                                    );
                                }

                                programClass2.addInputLocation(inputFileLocation);
                                if (!Arrays.equals(programClass1.getClassDigest(), programClass2.getClassDigest())) {
                                    throw new ClassFileFormatException(
                                            "Multiple versions of class "
                                                    + ZkmUtils.slashesToDots(string1)
                                                    + " detected. File "
                                                    + inputFileLocation2.getQualifiedName()
                                                    + " used. File "
                                                    + inputFileLocation.getQualifiedName()
                                                    + " ignored."
                                    );
                                }
                            } else {
                                list1.add(programClass1);
                            }
                        }
                    }

                    bl = true;
                } catch (OutOfMemoryError outOfMemoryError) {
                    ba++;
                    runtime1.gc();
                    this.statusHolder
                            .setValue("Waiting for garbage collection. Retry " + ba + ". " + (runtime1.totalMemory() - runtime1.freeMemory()) + "bytes used.");
                    bb *= ba;

                    try {
                        Thread.sleep(bb);
                    } catch (InterruptedException interruptedException1) {
                        classFileInputStream.reset();
                        continue;
                    }

                    classFileInputStream.reset();
                }
            } while (!bl && ba < 2);

            Thread.yield();
            if (!bl) {
                this.statusHolder.setValue("Insufficient Memory");
                throw new ZkmProcessingException("Insufficient Memory to open classes");
            }
        } catch (FileNotFoundException fileNotFoundException) {
            throw new ZkmProcessingException("File not found: " + fileNotFoundException.getMessage());
        } catch (ClassFileFormatException classFileFormatException) {
            printWriter.println("ERROR: " + classFileFormatException.getMessage());
        } catch (IOException iOException) {
            throw new ZkmProcessingException("IOException: " + iOException.getMessage());
        } catch (OutOfMemoryError outOfMemoryError1) {
            runtime1.gc();
            this.statusHolder.setValue("Insufficient Memory (2)");

            try {
                Thread.sleep(2000L);
            } catch (InterruptedException interruptedException) {
            }

            throw new ZkmProcessingException("Insufficient Memory to open classes");
        }
    }

    @Override
    public final boolean isSubclass(String string, String string1) throws ZkmException, IOException {
        try {
            boolean bl = string1.indexOf(42) != -1;
            return this.isSubclassOf(string, string1, bl);
        } catch (StackOverflowError stackOverflowError) {
            ClassFileBase classFileBase = this.classResolver.getClassFile(string, "reporting corrupt inheritance hierarchy");
            throw new CorruptHierarchyException(
                    stackOverflowError.getMessage() + " : Class " + classFileBase.getDisplayLocationName() + " has corrupt inheritance hierarchy (A)",
                    stackOverflowError
            );
        }
    }

    public final synchronized void renameField(FieldInfo fieldInfo, String string) throws ZkmException, IOException {
        ProgramClass programClass1 = fieldInfo.getProgramClass();
        if (this.fieldsByClassNameDesc.containsKeys(programClass1.getClassName(), string, fieldInfo.getDescriptor())) {
            throw new DuplicateFieldException();
        }

        fieldInfo.setName(string);
        int ba = 0;
        int bb = 0;

        for (ProgramClass[] programClass2 = this.programClasses; bb < programClass2.length; programClass2 = this.programClasses) {
            this.programClasses[ba].updateFieldReferences((ScriptEnvironment) null, this);
            bb = ++ba;
        }

        this.indexFields();
    }

    public void markLongConstantsEncrypted() {
        this.longConstantsEncrypted = true;
    }

    public void checkSharedNameString(
            Map map1,
            ResolvedStringConstant resolvedStringConstant,
            Object object,
            String string,
            ClassFileComponent classFileComponent,
            HashMap hashMap,
            ScriptEnvironment scriptEnvironment1
    ) throws IOException {
        Triple triple = (Triple) map1.get(resolvedStringConstant);
        if (triple != null) {
            String string1 = (String) triple.getFirst();
            String string2 = (String) triple.getSecond();
            ClassFileComponent classFileComponent1 = (ClassFileComponent) triple.getThird();
            if (!string2.equals(string)) {
                String string3 = this.getComponentKindName(classFileComponent1);
                String string4 = this.getComponentKindName(classFileComponent);
                String string5 = ZkmUtils.slashesToDots((String) ZkmUtils.mapOrSelf(resolvedStringConstant.getOwnerClassName(), hashMap));
                String string6 = (String) ZkmUtils.mapOrSelf(classFileComponent1.getClassName(), hashMap);
                String string7 = (String) ZkmUtils.mapOrSelf(classFileComponent.getClassName(), hashMap);
                String string8 = ZkmUtils.slashesToDots(string6);
                String string9 = ZkmUtils.slashesToDots(string7);
                String string10 = classFileComponent1.getLocationName();
                String string11 = classFileComponent.getLocationName();
                StringBuilder stringBuilder = new StringBuilder();
                if (string8.equals(string9)) {
                    if (string3.equals(string4)) {
                        stringBuilder.append(
                                "The one String '"
                                        + string1
                                        + "' in class '"
                                        + string5
                                        + "' is used to access two different "
                                        + string3
                                        + "s which will be given different names. The "
                                        + string3
                                        + " name '"
                                        + string1
                                        + "' must be excluded from obfuscation in the class '"
                                        + string8
                                        + "' in file '"
                                        + string10
                                        + "'. (a)"
                        );
                    } else {
                        stringBuilder.append(
                                "The one String '"
                                        + string1
                                        + "' in class '"
                                        + string5
                                        + "' is used to access a "
                                        + string3
                                        + " and a "
                                        + string4
                                        + " which will be given different names. The "
                                        + string3
                                        + " and "
                                        + string4
                                        + " names '"
                                        + string1
                                        + "' must be excluded from obfuscation in class '"
                                        + string8
                                        + "' in file '"
                                        + string10
                                        + "'. (b)"
                        );
                    }
                } else if (string3.equals(string4)) {
                    stringBuilder.append(
                            "The one String '"
                                    + string1
                                    + "' in class '"
                                    + string5
                                    + "' is used to access two different "
                                    + string3
                                    + "s which will be given different names. The "
                                    + string3
                                    + " name '"
                                    + string1
                                    + "' must be excluded from obfuscation in the classes '"
                                    + string8
                                    + "' and '"
                                    + string9
                                    + "' in files '"
                                    + string10
                                    + "' and '"
                                    + string11
                                    + "'. (c)"
                    );
                } else {
                    stringBuilder.append(
                            "The one String '"
                                    + string1
                                    + "' in class '"
                                    + string5
                                    + "' is used to access a "
                                    + string3
                                    + " and a "
                                    + string4
                                    + " which will be given different names. The "
                                    + string3
                                    + " name in class '"
                                    + string8
                                    + "' and "
                                    + string4
                                    + " name in class '"
                                    + string9
                                    + "' must be excluded from obfuscation.  Files names are '"
                                    + string10
                                    + "' and '"
                                    + string11
                                    + "'. (d)"
                    );
                }

                scriptEnvironment1.logSeriousError_v(stringBuilder.toString());
            }

            map1.put(resolvedStringConstant, new Triple(string1, string, classFileComponent));
        } else {
            map1.put(resolvedStringConstant, new Triple(object, string, classFileComponent));
        }
    }

    @Override
    public final boolean hasFieldNamed(String string, String string1) {
        return this.fieldsByClassNameDesc.containsKeyPair(string, string1);
    }

    public void updateNameStringConstants(HashMap hashMap, HashMap hashMap1, HashMap hashMap2, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        HashMap hashMap3 = ZkmUtils.createHashMap();
        TwoKeyMap twoKeyMap = new TwoKeyMap();
        Iterator iterator = this.classNameStringRefs.keySet().iterator();

        while (iterator.hasNext()) {
            ClassFileBase classFileBase = (ClassFileBase) iterator.next();
            if (hashMap.containsKey(classFileBase.getClassName())) {
                Map map1 = this.classNameStringRefs.getInnerMap(classFileBase);
                Iterator iterator1 = map1.entrySet().iterator();

                while (iterator1.hasNext()) {
                    Entry entry = (Entry) iterator1.next();
                    TrackedValue trackedValue = (TrackedValue) entry.getKey();
                    if (trackedValue instanceof StringConstantNameRef) {
                        ResolvedStringConstant resolvedStringConstant = ((StringConstantNameRef) trackedValue).getStringConstant();
                        twoKeyMap.putValue(classFileBase, resolvedStringConstant, resolvedStringConstant.getEditableValue());
                        Integer integer = (Integer) entry.getValue();
                        String string;
                        ClassRepository classRepository2;
                        HashMap hashMap4;
                        ResolvedStringConstant resolvedStringConstant5;
                        String string12;
                        if (integer == 0) {
                            string = classFileBase.getDottedClassName();
                            classRepository2 = this;
                            hashMap4 = hashMap3;
                            resolvedStringConstant5 = resolvedStringConstant;
                            string12 = resolvedStringConstant.getValueString();
                        } else {
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append(ZkmStringUtils.pad("", 76, integer, 91));
                            stringBuffer.append("L");
                            stringBuffer.append(classFileBase.getDottedClassName());
                            stringBuffer.append(";");
                            string = stringBuffer.toString();
                            classRepository2 = this;
                            hashMap4 = hashMap3;
                            resolvedStringConstant5 = resolvedStringConstant;
                            string12 = resolvedStringConstant.getValueString();
                        }

                        classRepository2.checkSharedNameString(hashMap4, resolvedStringConstant5, string12, string, classFileBase, hashMap, scriptEnvironment1);
                        resolvedStringConstant.setValueFromString(string);
                    }
                }
            }
        }

        EnumerableMap enumerableMap = new EnumerableMap(hashMap2);
        Enumeration enumeration = this.resourceNameStringRefs.keys();

        while (enumeration.hasMoreElements()) {
            String string3 = (String) enumeration.nextElement();
            boolean bl = string3.indexOf(47) == -1;
            String string5 = ZkmUtils.dotsToSlashes(string3);
            String string6 = (String) hashMap1.get(string5);
            ClassFileBase classFileBase1 = null;
            if (string6 != null) {
                classFileBase1 = ClassHierarchyNode.findClassFile(string6);
            }

            String string7 = ClassFileBase.getPackagePath(string5);
            if (string7.length() > 0) {
                List list1 = this.resourceNameStringRefs.getValues(string3);

                for (int i = 0; i < list1.size(); i++) {
                    TrackedValue trackedValue1 = (TrackedValue) list1.get(i);
                    if (trackedValue1 instanceof StringConstantNameRef) {
                        ResolvedStringConstant resolvedStringConstant1 = ((StringConstantNameRef) trackedValue1).getStringConstant();
                        String string1 = ResourcePathTranslator.renameResourceParentPath(string5, enumerableMap);
                        if (classFileBase1 != null
                                && classFileBase1.isProgramClass()
                                && twoKeyMap.containsKeys((ProgramClass) classFileBase1, resolvedStringConstant1)
                                && !classFileBase1.getClassName().equals(ZkmUtils.dotsToSlashes(string1))) {
                            scriptEnvironment1.logSeriousError_v(
                                    "The one String '"
                                            + (String) twoKeyMap.getValue((ProgramClass) classFileBase1, resolvedStringConstant1)
                                            + "' is used to access both a resource and the class '"
                                            + classFileBase1.getDottedClassName()
                                            + "'.  The class is renamed but the resource cannot be renamed. The class name must be excluded from obfuscation."
                            );
                        }

                        if (!string1.equals(string5)) {
                            if (bl) {
                                string1 = ZkmUtils.slashesToDots(string1);
                            }

                            resolvedStringConstant1.setValueFromString(string1);
                        }
                    }
                }
            }
        }

        Enumeration enumeration1 = this.packageNameStringRefs.keys();

        while (enumeration1.hasMoreElements()) {
            String string4 = (String) enumeration1.nextElement();
            if (hashMap2.containsKey(string4)) {
                Map map2 = this.packageNameStringRefs.getInnerMap(string4);
                Iterator iterator2 = map2.keySet().iterator();

                while (iterator2.hasNext()) {
                    TrackedValue trackedValue2 = (TrackedValue) iterator2.next();
                    if (trackedValue2 instanceof StringConstantNameRef) {
                        ResolvedStringConstant resolvedStringConstant2 = ((StringConstantNameRef) trackedValue2).getStringConstant();
                        String string8 = (String) hashMap2.get(string4);
                        String string9 = ZkmUtils.slashesToDots(string8);
                        resolvedStringConstant2.setValueFromString(string9);
                    }
                }
            }
        }

        Enumeration enumeration2 = this.fieldNameStringRefs.keys();

        while (enumeration2.hasMoreElements()) {
            AbstractFieldInfo abstractFieldInfo = (AbstractFieldInfo) enumeration2.nextElement();
            Map map3 = this.fieldNameStringRefs.getInnerMap(abstractFieldInfo);
            Iterator iterator3 = map3.keySet().iterator();

            while (iterator3.hasNext()) {
                TrackedValue trackedValue3 = (TrackedValue) iterator3.next();
                if (trackedValue3 instanceof StringConstantNameRef) {
                    ResolvedStringConstant resolvedStringConstant3 = ((StringConstantNameRef) trackedValue3).getStringConstant();
                    if (!abstractFieldInfo.getSourceName().equals(resolvedStringConstant3.getEditableValue())) {
                        this.checkSharedNameString(
                                hashMap3,
                                resolvedStringConstant3,
                                resolvedStringConstant3.getValueString(),
                                abstractFieldInfo.getSourceName(),
                                abstractFieldInfo,
                                hashMap,
                                scriptEnvironment1
                        );
                        resolvedStringConstant3.setValueFromString(abstractFieldInfo.getSourceName());
                    } else {
                        this.checkSharedNameString(
                                hashMap3,
                                resolvedStringConstant3,
                                resolvedStringConstant3.getValueString(),
                                abstractFieldInfo.getSourceName(),
                                abstractFieldInfo,
                                hashMap,
                                scriptEnvironment1
                        );
                    }
                }
            }
        }

        Enumeration enumeration3 = this.methodNameStringRefs.keys();

        while (enumeration3.hasMoreElements()) {
            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) enumeration3.nextElement();
            Map map4 = this.methodNameStringRefs.getInnerMap(abstractMethodInfo);
            Iterator iterator4 = map4.keySet().iterator();

            while (iterator4.hasNext()) {
                TrackedValue trackedValue4 = (TrackedValue) iterator4.next();
                if (trackedValue4 instanceof StringConstantNameRef) {
                    StringConstantNameRef stringConstantNameRef = (StringConstantNameRef) trackedValue4;
                    ResolvedStringConstant resolvedStringConstant4 = stringConstantNameRef.getStringConstant();
                    if (stringConstantNameRef.isFullSignature()) {
                        if (!abstractMethodInfo.getNameAndDescriptor().equals(resolvedStringConstant4.getEditableValue())) {
                            resolvedStringConstant4.setValueFromString(abstractMethodInfo.getNameAndDescriptor());
                        }
                    } else if (stringConstantNameRef.hasAccessorPrefix()) {
                        String string10 = stringConstantNameRef.getAccessorPrefix();
                        String string11 = abstractMethodInfo.getJvmName();
                        if (string11.startsWith(string10) && string11.length() > string10.length()) {
                            String string2 = MethodSignature.toPropertyName(abstractMethodInfo.getJvmName(), string10);
                            resolvedStringConstant4.setValueFromString(string2);
                        } else {
                            scriptEnvironment1.logWarning(
                                    "Could not update property '"
                                            + resolvedStringConstant4.getValueString()
                                            + "' to reflect method '"
                                            + abstractMethodInfo.getOriginalNameWithParameters()
                                            + "' in class '"
                                            + abstractMethodInfo.getDottedClassName()
                                            + "' which has been renamed to '"
                                            + abstractMethodInfo.getSourceName()
                                            + "'"
                            );
                        }
                    } else if (!abstractMethodInfo.getJvmName().equals(resolvedStringConstant4.getEditableValue())) {
                        this.checkSharedNameString(
                                hashMap3,
                                resolvedStringConstant4,
                                resolvedStringConstant4.getValueString(),
                                abstractMethodInfo.getJvmName(),
                                abstractMethodInfo,
                                hashMap,
                                scriptEnvironment1
                        );
                        resolvedStringConstant4.setValueFromString(abstractMethodInfo.getJvmName());
                    } else {
                        this.checkSharedNameString(
                                hashMap3,
                                resolvedStringConstant4,
                                resolvedStringConstant4.getValueString(),
                                abstractMethodInfo.getJvmName(),
                                abstractMethodInfo,
                                hashMap,
                                scriptEnvironment1
                        );
                    }
                }
            }
        }
    }

    public boolean isParametersObfuscated() {
        return this.parametersObfuscated;
    }

    public void analyzeClassCode(
            NestedMultiMap nestedMultiMap,
            NestedMultiMap nestedMultiMap1,
            NestedMultiMap nestedMultiMap2,
            PrintWriter printWriter,
            MutableInt mutableInt,
            boolean bl,
            MessageReporter messageReporter1
    ) throws ZkmException, IOException {
        HashSet hashSet = ZkmUtils.createHashSet();
        HashMap hashMap = ZkmUtils.createHashMap();
        int ba = 0;
        int bc = 0;

        for (ProgramClass[] programClass3 = this.programClasses; bc < programClass3.length; programClass3 = this.programClasses) {
            this.programClasses[ba].findReflectionApiCalls(hashSet, hashMap, this, messageReporter1);
            if (this.programClasses[ba].hasVersionedVariants()) {
                Iterator iterator = this.programClasses[ba].getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                    ((ProgramClass) classFileBase).findReflectionApiCalls(hashSet, hashMap, this, messageReporter1);
                }
            }

            bc = ++ba;
        }

        Map map1 = Collections.unmodifiableMap(hashMap);
        HashMap hashMap3 = ZkmUtils.createHashMap();
        HashMap hashMap4 = ZkmUtils.createHashMap();
        HashMap hashMap1 = ZkmUtils.createHashMap();
        HashMap hashMap2 = ZkmUtils.createHashMap();
        SoftReferenceMap softReferenceMap = new SoftReferenceMap("GC");
        String string = ZkmStringUtils.pad("", 76, 4, 32);
        CommonSuperTypeResolver commonSuperTypeResolver1 = new CommonSuperTypeResolver(this, this.classResolver);
        ArrayList arrayList = new ArrayList();
        int bb = 0;
        bc = 0;

        for (ProgramClass[] programClass2 = this.programClasses; bc < programClass2.length; programClass2 = this.programClasses) {
            ProgramClass programClass1 = this.programClasses[bb];
            programClass1.reportReflectionApiCalls(
                    hashSet,
                    map1,
                    nestedMultiMap,
                    this.classpathLoader,
                    nestedMultiMap1,
                    nestedMultiMap2,
                    printWriter,
                    mutableInt,
                    this,
                    commonSuperTypeResolver1,
                    string,
                    hashMap3,
                    hashMap4,
                    hashMap1,
                    hashMap2,
                    softReferenceMap,
                    this.classNameStringRefs,
                    this.resourceNameStringRefs,
                    this.fieldNameStringRefs,
                    this.methodNameStringRefs,
                    this.packageNameStringRefs,
                    this.classNameStringConstants,
                    this.reflectionTargetsByCaller,
                    messageReporter1,
                    arrayList,
                    bl
            );
            if ((HiddenOptionFlags.TRANSLATE_KOTLIN || HiddenOptionFlags.TRANSLATE_KOTLIN_METADATA)
                    && programClass1.isFinal()
                    && this.isSubclass(programClass1.getClassName(), "kotlin/jvm/internal/CallableReference")) {
                this.registerKotlinCallableReference(programClass1);
                if (programClass1.hasVersionedVariants()) {
                    Iterator iterator1 = programClass1.getVersionedVariants().iterator();

                    while (iterator1.hasNext()) {
                        ClassFileBase classFileBase1 = (ClassFileBase) iterator1.next();
                        this.registerKotlinCallableReference((ProgramClass) classFileBase1);
                    }
                }
            }

            if (programClass1.hasVersionedVariants()) {
                Iterator iterator3 = programClass1.getVersionedVariants().iterator();

                while (iterator3.hasNext()) {
                    ClassFileBase classFileBase2 = (ClassFileBase) iterator3.next();
                    ((ProgramClass) classFileBase2)
                            .reportReflectionApiCalls(
                                    hashSet,
                                    map1,
                                    nestedMultiMap,
                                    this.classpathLoader,
                                    nestedMultiMap1,
                                    nestedMultiMap2,
                                    printWriter,
                                    mutableInt,
                                    this,
                                    commonSuperTypeResolver1,
                                    string,
                                    hashMap3,
                                    hashMap4,
                                    hashMap1,
                                    hashMap2,
                                    softReferenceMap,
                                    this.classNameStringRefs,
                                    this.resourceNameStringRefs,
                                    this.fieldNameStringRefs,
                                    this.methodNameStringRefs,
                                    this.packageNameStringRefs,
                                    this.classNameStringConstants,
                                    this.reflectionTargetsByCaller,
                                    messageReporter1,
                                    arrayList,
                                    bl
                            );
                }
            }

            bc = ++bb;
        }

        Iterator iterator2 = arrayList.iterator();

        while (iterator2.hasNext()) {
            ReflectionCallSite reflectionCallSite = (ReflectionCallSite) iterator2.next();
            this.reflectionCallSites.addValue((MethodInfo) reflectionCallSite.getMethodBytecode().getMethod(), reflectionCallSite);
        }
    }

    public List getSuperclasses(String string) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        String string1 = "Searching for superclasses of '" + string + "'";
        ClassFileBase classFileBase = this.classResolver.getClassFile(string, string1);
        String string2 = classFileBase.getSuperclassName();

        try {
            do {
                ClassFileBase classFileBase1 = this.classResolver.getClassFile(string2, string1);
                arrayList.add(classFileBase1);
                string2 = classFileBase1.getSuperclassName();
            } while (string2 != null);

            return arrayList;
        } catch (StackOverflowError stackOverflowError) {
            throw new CorruptHierarchyException(
                    stackOverflowError.getMessage() + " : Class " + classFileBase.getDisplayLocationName() + " has corrupt inheritance hierarchy (D)",
                    stackOverflowError
            );
        }
    }

    public void removeNameStringRefs(Set set1) {
        if (set1 != null && set1.size() > 0) {
            this.removeStringRefsFrom(this.classNameStringRefs, set1);
            SnapshotEnumeration snapshotEnumeration = new SnapshotEnumeration(this.resourceNameStringRefs.keys());

            while (snapshotEnumeration.hasMoreElements()) {
                String string = (String) snapshotEnumeration.nextElement();
                List list1 = this.resourceNameStringRefs.getValues(string);

                for (int i = 0; i < list1.size(); i++) {
                    TrackedValue trackedValue = (TrackedValue) list1.get(i);
                    if (trackedValue instanceof StringConstantNameRef) {
                        ResolvedStringConstant resolvedStringConstant = ((StringConstantNameRef) trackedValue).getStringConstant();
                        if (set1.contains(resolvedStringConstant)) {
                            this.resourceNameStringRefs.removeValue(string, trackedValue);
                        }
                    }
                }
            }

            this.removeStringRefsFrom(this.packageNameStringRefs, set1);
            this.removeStringRefsFrom(this.fieldNameStringRefs, set1);
            this.removeStringRefsFrom(this.methodNameStringRefs, set1);
        }
    }

    @Override
    public final boolean implementsAnnotatedInterface(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        try {
            boolean bl = string1.indexOf(42) != -1;
            return this.implementsAnnotatedInterface(string, string1, bl, aSTComplexAnnotationSpecifier);
        } catch (StackOverflowError stackOverflowError) {
            String string2 = "reporting corrupt inheritance heirarchy";
            ClassFileBase classFileBase = this.classResolver.getClassFile(string, string2);
            throw new CorruptHierarchyException(
                    stackOverflowError.getMessage() + " : Class " + classFileBase.getDisplayLocationName() + " has corrupt hierarchy (B)", stackOverflowError
            );
        }
    }

    @Override
    public String lookupNewClassName(Object object) {
        return (String) this.currentToOriginalName.get(object);
    }

    @Override
    public List getNewMethodNames() {
        return this.newMethodNames;
    }

    public final ClasspathClassLoader getClasspathLoader() {
        return this.classpathLoader;
    }

    public Map getResourceReferencedFields() {
        return this.resourceReferencedFields;
    }

    public void snapshotPreRenameCaches() {
        if (!this.classNamesChanged) {
            this.preRenameSuperclassCache = this.superclassCache.deepCopy();
            this.preRenameInterfaceCache = this.interfaceCache.deepCopy();
            this.preRenameAnnotationCache = this.annotationCache.deepCopy();
            this.classNamesChanged = true;
        }

        this.clearHierarchyCaches();
    }

    public static void collectClassDigests(String string, int ba, int bb, Set set1, ClasspathClassLoader classpathClassLoader1, List list1, boolean bl) throws ZkmException, IOException {
        if (set1.size() == bb || string == null) {
            int bc = 0;

            for (ClassFileBase classFileBase = loadClassFile(classpathClassLoader1, string.replace('.', '/'), bl); classFileBase != null && bc < ba; bc++) {
                if (list1 != null) {
                    list1.add(classFileBase.getLocationName());
                }

                byte[] classDigest = classFileBase.getClassDigest();
                set1.add(ZkmUtils.toHexString(classDigest));
                String string1 = classFileBase.getSuperclassName();
                classFileBase = loadClassFile(classpathClassLoader1, string1, bl);
            }
        }
    }

    public InputFileLocation[] getYamlFileLocations() {
        InputFileLocation[] inputFileLocations = new InputFileLocation[this.yamlFileLocations.length];
        System.arraycopy(this.yamlFileLocations, 0, inputFileLocations, 0, this.yamlFileLocations.length);
        return inputFileLocations;
    }

    public KlassMaster getApplication() {
        return this.application;
    }

    @Override
    public boolean hasOriginalNameCaches() {
        return this.classNamesChanged;
    }

    @Override
    public final synchronized Enumeration enumerateFieldsNamed(String string) {
        TwoKeyMap twoKeyMap = this.fieldsByName.getTwoKeyMap(string);
        return twoKeyMap != null ? twoKeyMap.keys() : null;
    }

    @Override
    public final synchronized Enumeration enumerateMethodSignaturesNamed(String string) {
        ListMultimap listMultimap = this.methodDeclaringClasses.getMultimap(string);
        return listMultimap != null ? listMultimap.allValues() : null;
    }

    public void loadObjectMethods() throws ZkmException, IOException {
        try {
            for (AbstractMethodInfo abstractMethodInfo : this.classResolver.getClassFile("java/lang/Object").getDeclaredMethods()) {
                this.objectMethods.put(abstractMethodInfo.getSignature(), abstractMethodInfo);
            }
        } catch (ClassFileLoadException classFileLoadException) {
            ZkmAssert.assertTrue(
                    false, new String[]{"Could not find java/lang/Object" + (this.classpath != null ? " in '" + this.classpath.getClasspath() + "'" : "") + "."}
            );
        }
    }

    public void recordMethodSignatureChanges(ClassFileBase[] classFileBases) {
        this.methodSignatureChanges = new TwoKeyMap();

        for (ClassFileBase classFileBase : classFileBases) {
            for (AbstractMethodInfo abstractMethodInfo : classFileBase.getDeclaredMethods()) {
                this.methodSignatureChanges.putValue(classFileBase, abstractMethodInfo.getOriginalSignature(), abstractMethodInfo.getSignature());
            }
        }
    }

    public final void applyNameChanges(
            ClassFileBase[] classFileBases, ClassFileBase[] classFileBases1, int ba, int bb, HashMap hashMap, HashMap hashMap1, MessageReporter messageReporter1
    ) throws ZkmException, IOException {
        if (classFileBases != null) {
            for (int i = 0; i < classFileBases.length; i++) {
                ClassFileBase classFileBase = classFileBases[i];
                classFileBase.remapClassNames(ba, bb, hashMap, this.classNameStringConstants, this.resourceFileNames, hashMap1, messageReporter1);
                if (classFileBase.hasVersionedVariants()) {
                    Iterator iterator = classFileBase.getVersionedVariants().iterator();

                    while (iterator.hasNext()) {
                        ((ClassFileBase) iterator.next())
                                .remapClassNames(ba, bb, hashMap, this.classNameStringConstants, this.resourceFileNames, hashMap1, messageReporter1);
                    }
                }
            }
        }

        if (classFileBases1 != null) {
            for (int i = 0; i < classFileBases1.length; i++) {
                classFileBases1[i].remapClassNames(ba, bb, hashMap, this.classNameStringConstants, this.resourceFileNames, hashMap1, messageReporter1);
            }
        }
    }

    
    
    public void indexArchiveResourceFiles() throws ZkmProcessingException {
        int ba = 0;
        int bb = 0;

        for (SourceArchive[] sourceArchives1 = this.sourceArchives; bb < sourceArchives1.length; sourceArchives1 = this.sourceArchives) {
            SourceArchive sourceArchive1 = this.sourceArchives[ba];
            if (this.skippedArchives == null || !this.skippedArchives.contains(sourceArchive1)) {
                ArchiveZipFile archiveZipFile = null;
                boolean bl = false ;

                try {
                    bl = true;
                    archiveZipFile = new ArchiveZipFile(sourceArchive1.getFilePath());
                    Enumeration<? extends ZipEntry> enumeration = archiveZipFile.entries();

                    while (enumeration.hasMoreElements()) {
                        ZipEntry zipEntry1 = (ZipEntry) enumeration.nextElement();
                        if (!zipEntry1.isDirectory()) {
                            String string = zipEntry1.getName();
                            if (!string.endsWith(".class")) {
                                this.resourceFileNames.add(string);
                                if (ZkmFileUtils.isXmlFileName(string)) {
                                    this.resourceFileIndex.addArchiveEntry(string, sourceArchive1);
                                }
                            }
                        }
                    }

                    bl = false;
                } catch (IOException iOException2) {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("'" + sourceArchive1.getFilePath() + "' ");
                    if (!sourceArchive1.getFilePath().equals(sourceArchive1.getQualifiedPath())) {
                        stringBuffer.append(": \"" + sourceArchive1.getQualifiedPath() + "\" ");
                    }

                    stringBuffer.append(iOException2.toString());
                    throw new ZkmProcessingException(stringBuffer.toString());
                } finally {
                    if (bl) {
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
                    com.zelix.klassmaster.util.ZkmUtils.<java.io.IOException>mayThrow();
                    archiveZipFile.close();
                } catch (IOException iOException1) {
                }
            }

            bb = ++ba;
        }

        List list1 = ZkmFileUtils.getExtraXmlFilePatterns();
        if (list1 != null) {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                String string1 = (String) iterator.next();
                if (string1.indexOf("*") != 0) {
                    throw new ZkmProcessingException(
                            "All elements in 'ZKM_EXTRA_XML_FILE_TYPES' System property must start with the '*' wildcard. : '"
                                    + string1
                                    + "' : '"
                                    + HiddenOptionFlags.EXTRA_XML_FILE_TYPES
                                    + "'"
                    );
                }
            }
        }
    }

    @Override
    public final MethodInfo[] findMatchingMethods(ProgramClass programClass1, FieldNameTypeSignature fieldNameTypeSignature) {
        MethodInfo[] methodInfos = null;
        if (fieldNameTypeSignature.hasReturnType()) {
            MethodSignature methodSignature1 = new MethodSignature(fieldNameTypeSignature);
            MethodInfo methodInfo1 = (MethodInfo) this.methodsByClass.getValue(programClass1, methodSignature1);
            if (methodInfo1 != null) {
                methodInfos = new MethodInfo[]{methodInfo1};
            }
        }

        List list1 = this.methodsByClassAndNameType.getValues(programClass1, fieldNameTypeSignature);
        if (list1 != null) {
            methodInfos = new MethodInfo[list1.size()];
            methodInfos = ((com.zelix.klassmaster.classfile.MethodInfo[]) (list1.toArray(methodInfos)));
        }

        return methodInfos;
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    public final boolean isOpenedWithoutErrors() {
        return this.openErrorMessage == null;
    }

    public Set getBootstrapMethodHandleTargets() {
        return this.bootstrapMethodHandleTargets;
    }

    public void removeMethodCalls(List list1, List list2, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        this.statusHolder.setValue("Performing remove method call analysis");
        ListMultimap listMultimap = new ListMultimap();

        for (ProgramClass programClass1 : this.programClasses) {
            programClass1.collectMethodCallSites(listMultimap);
        }

        new CommonSuperTypeResolver(this, this.classResolver);
        RemoveMethodCallsHandler removeMethodCallsHandler = new RemoveMethodCallsHandler(this, listMultimap, list1, list2, scriptEnvironment1);
        SetMultiMap setMultiMap = new SetMultiMap();
        Iterator iterator1 = listMultimap.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry entry = (Entry) iterator1.next();
            AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) entry.getKey();
            Iterator iterator = ((List) entry.getValue()).iterator();

            while (iterator.hasNext()) {
                MethodBytecode methodBytecode1 = (MethodBytecode) iterator.next();
                if (removeMethodCallsHandler.shouldRemoveCall(abstractMethodInfo, methodBytecode1)) {
                    setMultiMap.addValue(methodBytecode1, abstractMethodInfo);
                }
            }
        }

        if (setMultiMap.isEmpty()) {
            scriptEnvironment1.logWarning("Remove Method Calls : No methods matched for removal.", true);
        } else {
            iterator1 = setMultiMap.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry1 = (Entry) iterator1.next();
                MethodBytecode methodBytecode2 = (MethodBytecode) entry1.getKey();
                methodBytecode2.removeMethodCalls((Set) entry1.getValue(), scriptEnvironment1);
            }
        }
    }

    public void analyzeMethodStacks(CommonSuperTypeResolver commonSuperTypeResolver1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        ProgramClass[] programClass1 = this.programClasses;
        int ba = programClass1.length;

        for (int i = 0; i < ba; i++) {
            programClass1[i].analyzeMethodFlows(commonSuperTypeResolver1, this, scriptEnvironment1);
        }
    }

    public static boolean hasCompatibleSignature(
            AbstractMethodInfo abstractMethodInfo, AbstractMethodInfo abstractMethodInfo1, CommonSuperTypeResolver commonSuperTypeResolver1
    ) throws ZkmException, IOException {
        List list1 = ConstantPoolEntry.getParameterTypes(abstractMethodInfo.getDescriptor());
        List list2 = ConstantPoolEntry.getParameterTypes(abstractMethodInfo1.getDescriptor());
        if (!abstractMethodInfo.getJvmName().equals(abstractMethodInfo1.getJvmName())) {
            return false;
        }

        if (list1.size() != list2.size()) {
            return false;
        }

        try {
            for (int i = 0; i < list1.size(); i++) {
                String string = (String) list1.get(i);
                String string1 = (String) list2.get(i);
                if (!commonSuperTypeResolver1.isDescriptorAssignable(string, string1)) {
                    return false;
                }
            }

            return commonSuperTypeResolver1.isDescriptorAssignable(abstractMethodInfo.getReturnDescriptor(), abstractMethodInfo1.getReturnDescriptor());
        } catch (ClassFileLoadException classFileLoadException) {
            return false;
        }
    }

    public boolean implementsInterfaceOriginal(String string, String string1, boolean bl) throws ZkmException, IOException {
        if (string.equals("java/lang/Object")) {
            return false;
        }

        Boolean boolean1 = (Boolean) this.preRenameInterfaceCache.getValue(string1, string);
        if (boolean1 != null) {
            return boolean1;
        }

        ObservableHolder observableHolder = new ObservableHolder();
        Enumeration enumeration = this.getOriginalInterfaceNames(string, observableHolder);

        TwoKeyMap twoKeyMap;
        while (true) {
            if (!enumeration.hasMoreElements()) {
                String string4 = this.getOriginalSuperclassName(string);
                boolean bl2 = this.implementsInterfaceOriginal(string4, string1, bl);
                this.preRenameInterfaceCache.putValue(string1, string, bl2 ? CACHED_TRUE : CACHED_FALSE);
                return bl2;
            }

            String string2 = (String) enumeration.nextElement();
            String string3;
            if (HiddenOptionFlags.TEST_HIERARCHY
                    && (
                    string3 = ClassHierarchyNode.getHierarchyGapMessage(
                            (ClassFileBase) observableHolder.getValue(),
                            this.classResolver.getClassFile((String) ZkmUtils.mapOrSelf(string2, this.originalToCurrentName)),
                            2
                    )
            )
                    != null) {
                throw new ZkmRuntimeException(string3);
            }

            if (!bl) {
                if (string2.equals(string1)) {
                    twoKeyMap = this.preRenameInterfaceCache;
                    break;
                }
            } else if (ZkmStringUtils.matchesWildcard(string2, string1)) {
                twoKeyMap = this.preRenameInterfaceCache;
                break;
            }

            boolean bl1 = this.implementsInterfaceOriginal(string2, string1, bl);
            if (bl1) {
                this.preRenameInterfaceCache.putValue(string1, string, CACHED_TRUE);
                return true;
            }
        }

        twoKeyMap.putValue(string1, string, CACHED_TRUE);
        return true;
    }

    public void refreshClassReferences(ClassFileBase[] classFileBases, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        for (int i = 0; i < classFileBases.length; i++) {
            ClassFileBase classFileBase = classFileBases[i];
            classFileBase.updateFieldReferences(scriptEnvironment1, this);
            if (classFileBase.hasVersionedVariants()) {
                Iterator iterator = classFileBase.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ((ClassFileBase) iterator.next()).updateFieldReferences(scriptEnvironment1, this);
                }
            }
        }
    }

    public InputFileLocation[] getPropertiesFileLocations() {
        InputFileLocation[] inputFileLocations = new InputFileLocation[this.propertiesFileLocations.length];
        System.arraycopy(this.propertiesFileLocations, 0, inputFileLocations, 0, this.propertiesFileLocations.length);
        return inputFileLocations;
    }

    public final int getTotalClassCount() {
        int ba = 0;
        if (this.programClasses != null) {
            ba = this.programClasses.length;
        }

        if (this.moduleInfoClasses != null) {
            ba += this.moduleInfoClasses.length;
        }

        return ba;
    }

    public HashMap findJ2meClasses() throws ZkmException, IOException {
        HashMap hashMap = ZkmUtils.createHashMap();
        int ba = this.programClasses.length;
        HashMap hashMap1 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.sourceArchives.length));
        int bb = 0;
        int be = 0;

        for (SourceArchive[] sourceArchives1 = this.sourceArchives; be < sourceArchives1.length; sourceArchives1 = this.sourceArchives) {
            SourceArchive sourceArchive1 = this.sourceArchives[bb];
            if (this.skippedArchives == null || !this.skippedArchives.contains(sourceArchive1)) {
                try {
                    if (sourceArchive1.isCldc10()) {
                        hashMap1.put(sourceArchive1, cldc10ArchiveMarker);
                    } else if (sourceArchive1.hasMicroEditionConfiguration()) {
                        hashMap1.put(sourceArchive1, cldcArchiveMarker);
                    }
                } catch (ZkmException zkmException) {
                    throw new ZkmProcessingException(zkmException.getMessage());
                }
            }

            be = ++bb;
        }

        boolean bl = false;

        for (int i = 0; i < ba; i++) {
            try {
                if (this.isSubclass(this.programClasses[i].getClassName(), "javax/microedition/midlet/MIDlet")) {
                    Enumeration enumeration = this.programClasses[i].enumerateInputLocations();

                    while (enumeration.hasMoreElements()) {
                        InputFileLocation inputFileLocation = (InputFileLocation) enumeration.nextElement();
                        SourceArchive sourceArchive2 = inputFileLocation.getSourceArchive();
                        if (sourceArchive2 != null) {
                            if (!hashMap1.containsKey(sourceArchive2)) {
                                hashMap1.put(sourceArchive2, cldc10ArchiveMarker);
                            }
                        } else {
                            bl = true;
                        }
                    }
                }
            } catch (ZkmClassNotFoundException zkmClassNotFoundException) {
            } catch (ClassFileLoadException classFileLoadException) {
            }
        }

        for (int i = 0; i < ba; i++) {
            boolean bl1 = false;
            boolean bl2 = true;
            boolean bl3 = false;
            Enumeration enumeration1 = this.programClasses[i].enumerateInputLocations();

            while (enumeration1.hasMoreElements()) {
                SourceArchive sourceArchive3 = ((InputFileLocation) enumeration1.nextElement()).getSourceArchive();
                if (sourceArchive3 != null) {
                    Integer integer = (Integer) hashMap1.get(sourceArchive3);
                    if (integer != null) {
                        bl1 = true;
                        if (integer.equals(cldc10ArchiveMarker)) {
                            bl2 = false;
                        }
                    }
                } else {
                    bl3 = true;
                }
            }

            if (bl3 && bl && !bl1) {
                bl1 = true;
                bl2 = false;
            }

            if (bl1) {
                hashMap.put(this.programClasses[i], bl2 ? Boolean.TRUE : Boolean.FALSE);
            }
        }

        return hashMap;
    }

    public boolean implementsAnnotatedInterface(String string, String string1, boolean bl, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        if (string.equals("java/lang/Object")) {
            return false;
        }

        ObservableHolder observableHolder = new ObservableHolder();
        Enumeration enumeration = this.getInterfaceNames(string, observableHolder);

        while (enumeration.hasMoreElements()) {
            String string2 = (String) enumeration.nextElement();
            String string3;
            if (HiddenOptionFlags.TEST_HIERARCHY
                    && (string3 = ClassHierarchyNode.getHierarchyGapMessage((ClassFileBase) observableHolder.getValue(), this.classResolver.getClassFile(string2), 2))
                    != null) {
                throw new ZkmRuntimeException(string3);
            }

            if (!bl && string2.equals(string1)) {
                return aSTComplexAnnotationSpecifier.matchesAnyAnnotation(this.getClassAnnotations(string2, null, false));
            }

            if (bl
                    && ZkmStringUtils.matchesWildcard(string2, string1)
                    && aSTComplexAnnotationSpecifier.matchesAnyAnnotation(this.getClassAnnotations(string2, null, false))) {
                return true;
            }

            boolean bl1 = this.implementsAnnotatedInterface(string2, string1, bl, aSTComplexAnnotationSpecifier);
            if (bl1) {
                return true;
            }
        }

        String string4 = this.getSuperclassName(string);
        return this.implementsAnnotatedInterface(string4, string1, bl, aSTComplexAnnotationSpecifier);
    }

    @Override
    public final void registerMethod(MethodInfo methodInfo1, ProgramClass programClass1) {
        MethodSignature methodSignature1 = methodInfo1.getSignature();
        this.methodDeclaringClasses.addValue(methodInfo1.getJvmName(), methodSignature1, programClass1);
        MethodInfo methodInfo2 = (MethodInfo) this.methodsByClass.putValue(programClass1, methodSignature1, methodInfo1);
        if (methodInfo2 != null) {
            ZkmAssert.assertTrue(
                    false,
                    new String[]{
                            "Duplicate method in class '"
                                    + programClass1.getDottedClassName()
                                    + "' opened from "
                                    + programClass1.getLocationName()
                                    + " - "
                                    + methodSignature1
                                    + " : "
                                    + methodInfo2.getSignature().toDeclarationString()
                                    + ""
                    }
            );
        }

        this.methodsByClassAndNameType.addValue(programClass1, methodSignature1.getNameTypeSignature(), methodInfo1);
    }

    public List getReflectedClasses(Object object) {
        ArrayList arrayList = new ArrayList();
        List list1 = this.reflectionTargetsByCaller.getValues(object);
        if (list1 != null) {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                ClassFileComponent classFileComponent = (ClassFileComponent) iterator.next();
                if (classFileComponent instanceof ProgramClass && ((ProgramClass) classFileComponent).isProgramClass()) {
                    arrayList.add((ProgramClass) classFileComponent);
                }
            }
        }

        return arrayList;
    }

    @Override
    public Set getClassAnnotations(String string, Integer integer, boolean bl) throws ZkmException, IOException {
        Boolean boolean1 = bl;
        Integer integer1 = integer;
        String string1 = string;
        return this.getClassAnnotations(true, string1, integer1, boolean1);
    }

    @Override
    public final FieldInfo[] findFieldsByName(String string, String string1) {
        Map map1 = this.fieldsByClassNameDesc.getInnerMap(string, string1);
        if (map1 == null) {
            return null;
        }

        FieldInfo[] fieldInfos = new FieldInfo[map1.size()];
        int ba = 0;
        Iterator iterator = map1.values().iterator();

        while (iterator.hasNext()) {
            fieldInfos[ba++] = (FieldInfo) iterator.next();
        }

        return fieldInfos;
    }

    public Enumeration getInterfaceNames(String string, ObservableHolder observableHolder) throws ZkmException, IOException {
        ClassFileBase classFileBase;
        String[] strings;
        label24:
        {
            ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(string);
            ClasspathClassLoader classpathClassLoader1;
            if (classHierarchyNode != null) {
                if (!classHierarchyNode.hasNoClassFile()) {
                    classFileBase = classHierarchyNode.getClassFile();
                    strings = classHierarchyNode.getInterfaceNames();
                    break label24;
                }

                classpathClassLoader1 = this.classpathLoader;
            } else {
                classpathClassLoader1 = this.classpathLoader;
            }

            classFileBase = classpathClassLoader1.getClassFile(string);
            strings = classFileBase.getInterfaceNames();
        }

        for (String string1 : strings) {
            this.interfaceCache.putValue(string1, string, CACHED_TRUE);
        }

        observableHolder.setValue(classFileBase);
        return new ArrayEnumeration(strings);
    }

    public void setPropertiesSaveFiles(HashMap hashMap) {
        this.propertiesSaveFiles = hashMap;
    }

    @Override
    public boolean isHierarchySealed() {
        return this.fieldNamesObfuscated;
    }

    public List readInputClasses(
            InputFileLocation[] inputFileLocations, List list1, Runtime runtime1, PrintWriter printWriter, PrintWriter printWriter1, PrintWriter printWriter2
    ) throws ZkmException, IOException {
        ThreeKeyMultiMap threeKeyMultiMap = new ThreeKeyMultiMap(4, 3, 100, HiddenOptionFlags.USE_PARALLEL);
        Vector vector = new Vector(inputFileLocations.length);
        AbstractMap abstractMap = HiddenOptionFlags.USE_PARALLEL
                ? new ConcurrentHashMap(ZkmUtils.getPrimeCapacity(inputFileLocations.length))
                : ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(inputFileLocations.length));
        PairMultiMap pairMultiMap = new PairMultiMap(HiddenOptionFlags.USE_PARALLEL);
        PairMultiMap pairMultiMap1 = new PairMultiMap(HiddenOptionFlags.USE_PARALLEL);
        AbstractMap abstractMap1 = HiddenOptionFlags.USE_PARALLEL ? new ConcurrentHashMap() : ZkmUtils.createHashMap();
        ListenerRegistry listenerRegistry1 = new ListenerRegistry(HiddenOptionFlags.USE_PARALLEL);
        List<InputFileLocation> list2 = Arrays.asList(inputFileLocations);
        if (HiddenOptionFlags.USE_PARALLEL && HiddenOptionFlags.PROCESSOR_COUNT >= 2) {
            Vector vector1 = new Vector();
            list2.parallelStream()
                    .forEach(
                            inputFileLocation1 -> {
                                try {
                                    try {
                                        this.readInputClass(
                                                inputFileLocation1,
                                                vector,
                                                list1,
                                                listenerRegistry1,
                                                abstractMap,
                                                abstractMap1,
                                                pairMultiMap,
                                                pairMultiMap1,
                                                threeKeyMultiMap,
                                                printWriter,
                                                runtime1
                                        );
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
            Iterator iterator = list2.iterator();

            while (iterator.hasNext()) {
                InputFileLocation inputFileLocation = (InputFileLocation) iterator.next();
                this.readInputClass(
                        inputFileLocation,
                        vector,
                        list1,
                        listenerRegistry1,
                        abstractMap,
                        abstractMap1,
                        pairMultiMap,
                        pairMultiMap1,
                        threeKeyMultiMap,
                        printWriter,
                        runtime1
                );
            }
        }

        if (!pairMultiMap.isEmpty()) {
            this.resolveMultiReleaseClasses(vector, abstractMap, pairMultiMap, printWriter, printWriter2);
        }

        if (!pairMultiMap1.isEmpty()) {
            this.resolveMultiReleaseModules(list1, abstractMap1, pairMultiMap1, printWriter, printWriter2);
        }

        for (int i = 0; i < inputFileLocations.length; i++) {
            inputFileLocations[i].releaseResources();
        }

        this.analyzeUnknownAttributes(threeKeyMultiMap);
        return vector;
    }

    public void applyMethodRenamesToVersions(ClassFileBase[] classFileBases, TwoKeyMap twoKeyMap) throws ZkmException, IOException {
        for (int i = 0; i < classFileBases.length; i++) {
            ClassFileBase classFileBase = classFileBases[i];
            if (classFileBase.hasVersionedVariants()) {
                classFileBase.applyMethodRenames(twoKeyMap);
            }
        }
    }

    public void setXmlSaveFiles(HashMap hashMap) {
        this.xmlSaveFiles = hashMap;
    }

    @Override
    public final FieldInfo[] findFieldsBySignature(Object object) {
        Map map1 = this.fieldsBySignature.getInnerMap(object);
        if (map1 == null) {
            return null;
        }

        FieldInfo[] fieldInfos = new FieldInfo[map1.size()];
        int ba = 0;
        Iterator iterator = map1.values().iterator();

        while (iterator.hasNext()) {
            FieldInfo fieldInfo = (FieldInfo) iterator.next();
            fieldInfos[ba++] = fieldInfo;
        }

        return fieldInfos;
    }

    public final boolean hasNoClassesOpened() {
        return this.programClasses == null || this.programClasses.length == 0 && (this.moduleInfoClasses == null || this.moduleInfoClasses.length == 0);
    }

    public Set getAnnotations(boolean bl, String string, Integer integer) throws ZkmException, IOException {
        try {
            if (this.annotationCache.containsKey(string)) {
                return new LinkedHashSet(this.annotationCache.getValues(string));
            }

            LinkedHashSet linkedHashSet = new LinkedHashSet(13);
            String string2 = "analyzing the annotations of class '" + ZkmUtils.slashesToDots(string) + "'";
            this.collectAnnotations(bl, string, integer, linkedHashSet, false, string2);
            this.annotationCache.putValueSet(string, linkedHashSet);
            return linkedHashSet;
        } catch (StackOverflowError stackOverflowError) {
            String string1 = "reporting corrupt inheritance hierarchy";
            ClassFileBase classFileBase = this.classResolver.getClassFile(string, string1);
            throw new CorruptHierarchyException(
                    stackOverflowError.getMessage() + " : Class " + classFileBase.getDisplayLocationName() + " has corrupt hierarchy (C)", stackOverflowError
            );
        }
    }

    public void recordClassDigests(Object object, int ba, int bb) throws ZkmException, IOException {
        String string = object.getClass().getName();
        Set set1 = this.classDigests;
        ClasspathClassLoader classpathClassLoader1 = this.selfClassLoader;
        Boolean boolean1 = false;
        collectClassDigests(string, ba, bb, set1, classpathClassLoader1, (List) null, boolean1);
    }

    public String getOriginalSuperclassName(String string) throws ZkmException, IOException {
        String string1;
        ClassFileBase classFileBase;
        label27:
        {
            ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode((String) ZkmUtils.mapOrSelf(string, this.originalToCurrentName));
            ClasspathClassLoader classpathClassLoader1;
            if (classHierarchyNode != null) {
                if (!classHierarchyNode.hasNoClassFile()) {
                    classFileBase = classHierarchyNode.getClassFile();
                    ClassHierarchyNode classHierarchyNode1 = classHierarchyNode.getSuperclassNode();
                    string1 = classHierarchyNode1.getOriginalClassName();
                    break label27;
                }

                classpathClassLoader1 = this.classpathLoader;
            } else {
                classpathClassLoader1 = this.classpathLoader;
            }

            classFileBase = classpathClassLoader1.getClassFile(string);
            string1 = classFileBase.getSuperclassName();
        }

        TwoKeyMap twoKeyMap;
        if (HiddenOptionFlags.TEST_HIERARCHY) {
            if (!classFileBase.isGenerated()) {
                String string2;
                if ((
                        string2 = ClassHierarchyNode.getHierarchyGapMessage(
                                classFileBase, this.classResolver.getClassFile((String) ZkmUtils.mapOrSelf(string1, this.originalToCurrentName)), 1
                        )
                )
                        != null) {
                    throw new ZkmRuntimeException(string2);
                }

                twoKeyMap = this.preRenameSuperclassCache;
            } else {
                twoKeyMap = this.preRenameSuperclassCache;
            }
        } else {
            twoKeyMap = this.preRenameSuperclassCache;
        }

        twoKeyMap.putValue(string1, string, CACHED_TRUE);
        return string1;
    }

    public final synchronized void saveAll(
            int ba,
            boolean bl,
            boolean bl1,
            String string,
            File file1,
            MessageReporter messageReporter1,
            ScriptEnvironment scriptEnvironment1,
            DialogCallback dialogCallback1
    ) throws ZkmException, IOException {
        scriptEnvironment1.getMessageCount();
        scriptEnvironment1.getWarningCount();
        scriptEnvironment1.getErrorCount();
        scriptEnvironment1.getSeriousErrorCount();
        scriptEnvironment1.clearLoggedMessages();
        CommonSuperTypeResolver commonSuperTypeResolver1 = new CommonSuperTypeResolver(this, this.classResolver);
        int bb = this.autoReflectionHandledClasses.getKeyCount() + this.referenceObfuscatedClasses.getKeyCount() + this.parameterChangedClasses.getKeyCount();
        IndexedValueRelation indexedValueRelation = new IndexedValueRelation((IndexedValueRelation) this.autoReflectionHandledClasses, bb);
        indexedValueRelation.mergeFrom((IndexedValueRelation) this.referenceObfuscatedClasses);
        indexedValueRelation.mergeFrom((IndexedValueRelation) this.parameterChangedClasses);
        new ClassArchiveSaver(
                this.programClasses,
                this.moduleInfoClasses,
                this.sourceArchives,
                this.skippedArchives,
                this.skippedClassFiles,
                this.skippedXmlFiles,
                this.skippedYamlFiles,
                this.skippedPropertiesFiles,
                this.filteredOutFiles,
                this.jadFileArchives,
                this.xmlFileLocations,
                this.yamlFileLocations,
                this.propertiesFileLocations,
                this.archiveSaveFiles,
                this.jadSaveFiles,
                this.xmlSaveFiles,
                this.yamlSaveFiles,
                this.propertiesSaveFiles,
                this.getCumulativeNameMap(),
                this.getOriginalToCurrentNameMap(),
                this.getCurrentToOriginalNameMap(),
                this.getFieldSignatureChanges(),
                this.getMethodSignatureChanges(),
                this,
                file1,
                ba,
                bl,
                bl1,
                string,
                messageReporter1,
                scriptEnvironment1,
                commonSuperTypeResolver1,
                scriptEnvironment1.getFixedClassesExclusionSet(),
                indexedValueRelation,
                this.resourceFileIndex,
                this.statusHolder
        );
        dialogCallback1.onDialogCancelled();
    }

    public boolean implementsAnnotatedInterfaceOriginal(String string, String string1, boolean bl, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        if (string.equals("java/lang/Object")) {
            return false;
        }

        ObservableHolder observableHolder = new ObservableHolder();
        Enumeration enumeration = this.getOriginalInterfaceNames(string, observableHolder);

        while (enumeration.hasMoreElements()) {
            String string2 = (String) enumeration.nextElement();
            String string3;
            if (HiddenOptionFlags.TEST_HIERARCHY
                    && (
                    string3 = ClassHierarchyNode.getHierarchyGapMessage(
                            (ClassFileBase) observableHolder.getValue(),
                            this.classResolver.getClassFile((String) ZkmUtils.mapOrSelf(string2, this.originalToCurrentName)),
                            2
                    )
            )
                    != null) {
                throw new ZkmRuntimeException(string3);
            }

            if (!bl && string2.equals(string1)) {
                return aSTComplexAnnotationSpecifier.matchesAnyAnnotation(this.getClassAnnotations(string2, null, true));
            }

            if (bl
                    && ZkmStringUtils.matchesWildcard(string2, string1)
                    && aSTComplexAnnotationSpecifier.matchesAnyAnnotation(this.getClassAnnotations(string2, null, true))) {
                return true;
            }

            boolean bl1 = this.implementsAnnotatedInterfaceOriginal(string2, string1, bl, aSTComplexAnnotationSpecifier);
            if (bl1) {
                return true;
            }
        }

        String string4 = this.getOriginalSuperclassName(string);
        return this.implementsAnnotatedInterfaceOriginal(string4, string1, bl, aSTComplexAnnotationSpecifier);
    }

    @Override
    public final boolean declaresMethod(MethodSignature methodSignature1, ProgramClass programClass1) {
        return this.methodsByClass.containsKeys(programClass1, methodSignature1);
    }

    public boolean markParametersObfuscated() {
        this.parametersObfuscated = true;
        return true;
    }

    public void markFieldNamesObfuscated() {
        this.fieldNamesObfuscated = true;
    }

    public ListMultimap getReflectionCallSites() {
        return this.reflectionCallSites;
    }

    public boolean extendsAnnotatedClassOriginal(String string, String string1, boolean bl, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        if (string.equals("java/lang/Object")) {
            return false;
        } else if (string.equals(string1)) {
            return false;
        } else {
            String string2 = this.getOriginalSuperclassName(string);
            if (!bl && string2.equals(string1)) {
                return aSTComplexAnnotationSpecifier.matchesAnyAnnotation(this.getClassAnnotations(string2, null, true));
            } else if (bl
                    && ZkmStringUtils.matchesWildcard(string2, string1)
                    && aSTComplexAnnotationSpecifier.matchesAnyAnnotation(this.getClassAnnotations(string2, null, true))) {
                return true;
            } else {
                return string2.equals("java/lang/Object") ? false : this.extendsAnnotatedClassOriginal(string2, string1, bl, aSTComplexAnnotationSpecifier);
            }
        }
    }

    public Set getClassDigests() {
        return this.classDigests;
    }

    public InputFileLocation[] getXmlFileLocations() {
        InputFileLocation[] inputFileLocations = new InputFileLocation[this.xmlFileLocations.length];
        System.arraycopy(this.xmlFileLocations, 0, inputFileLocations, 0, this.xmlFileLocations.length);
        return inputFileLocations;
    }

    @Override
    public Set getClassAnnotations(Object object, String string, Integer integer, boolean bl) throws ZkmException, IOException {
        boolean bl1 = (Boolean) object;
        Set set1;
        if (bl && HiddenOptionFlags.MATCH_ORIGINAL_NAMES && this.hasOriginalNameCaches()) {
            String string1 = (String) ZkmUtils.mapOrSelf(string, this.currentToOriginalName);
            set1 = this.getOriginalAnnotations(bl1, string, string1, integer);
        } else {
            set1 = this.getAnnotations(bl1, string, integer);
        }

        return set1;
    }

    public Set getInputRootDirectories() throws ZkmClassNotFoundException {
        HashSet hashSet = ZkmUtils.createHashSet();
        if (this.sourceArchives != null) {
            int ba = 0;
            int be = 0;

            for (SourceArchive[] sourceArchives1 = this.sourceArchives; be < sourceArchives1.length; sourceArchives1 = this.sourceArchives) {
                if (this.skippedArchives == null || !this.skippedArchives.contains(this.sourceArchives[ba])) {
                    String string = this.sourceArchives[ba].getName();
                    File file1 = new File(string);
                    File file2 = file1.getParentFile();
                    if (file2 != null) {
                        hashSet.add(file2);
                    }
                }

                be = ++ba;
            }
        }

        int bc = 0;
        int bd = 0;

        for (ProgramClass[] programClass2 = this.programClasses; bd < programClass2.length; programClass2 = this.programClasses) {
            ProgramClass programClass1 = this.programClasses[bc];
            if (!programClass1.isGenerated()) {
                Enumeration enumeration = this.programClasses[bc].enumerateInputLocations();

                while (enumeration.hasMoreElements()) {
                    InputFileLocation inputFileLocation = (InputFileLocation) enumeration.nextElement();
                    if (inputFileLocation.isPlainFile()) {
                        File file3 = inputFileLocation.getFile().getParentFile();
                        if (file3 != null) {
                            String string1 = file3.getAbsolutePath();
                            if (string1.endsWith(File.separator) && string1.length() > 1) {
                                string1 = string1.substring(0, string1.length() - 1);
                            }

                            String string2 = programClass1.getPackagePath();
                            if (string2.length() == 0) {
                                hashSet.add(file3);
                            } else {
                                string2 = string2.replace('/', File.separatorChar);
                                int bb = string1.indexOf(string2);
                                if (bb > -1) {
                                    String string3 = string1.substring(0, bb);
                                    hashSet.add(new File(string3));
                                }
                            }
                        }
                    }
                }
            }

            bd = ++bc;
        }

        return hashSet;
    }

    public boolean hasReflectionCallSites() {
        return this.reflectionCallSites.getKeyCount() > 0;
    }

    public EnumerableMap getCumulativeNameMap() {
        return this.cumulativeNameMapping.getMappingCopy();
    }

    private boolean implementsInterface(String string, String string1, boolean bl) throws ZkmException, IOException {
        if (string.equals("java/lang/Object")) {
            return false;
        }

        Boolean boolean1 = (Boolean) this.interfaceCache.getValue(string1, string);
        if (boolean1 != null) {
            return boolean1;
        }

        ObservableHolder observableHolder = new ObservableHolder();
        Enumeration enumeration = this.getInterfaceNames(string, observableHolder);

        while (enumeration.hasMoreElements()) {
            String string2 = (String) enumeration.nextElement();
            String string3;
            if (HiddenOptionFlags.TEST_HIERARCHY
                    && (string3 = ClassHierarchyNode.getHierarchyGapMessage((ClassFileBase) observableHolder.getValue(), this.classResolver.getClassFile(string2), 2))
                    != null) {
                throw new ZkmRuntimeException(string3);
            }

            if (!bl ? string2.equals(string1) : ZkmStringUtils.matchesWildcard(string2, string1)) {
                this.interfaceCache.putValue(string1, string, CACHED_TRUE);
                return true;
            }

            boolean bl1 = this.implementsInterface(string2, string1, bl);
            if (bl1) {
                this.interfaceCache.putValue(string1, string, CACHED_TRUE);
                return true;
            }
        }

        String string4 = this.getSuperclassName(string);
        boolean bl2 = this.implementsInterface(string4, string1, bl);
        this.interfaceCache.putValue(string1, string, bl2 ? CACHED_TRUE : CACHED_FALSE);
        return bl2;
    }

    public final boolean isOpened() {
        return this.programClasses != null;
    }

    public EnumerableMap getOriginalToCurrentNameMap() {
        return new EnumerableMap(this.originalToCurrentName);
    }

    public void setJadSaveFiles(HashMap hashMap) {
        this.jadSaveFiles = hashMap;
    }

    public void clearHierarchyCaches() {
        this.superclassCache.clear();
        this.interfaceCache.clear();
        this.annotationCache.clear();
    }

    @Override
    public final Enumeration enumerateProgramClasses() {
        return new ArrayEnumeration(this.programClasses);
    }

    @Override
    public final boolean containsField(FieldSignature fieldSignature, ProgramClass programClass1) {
        return this.fieldsBySignature.containsKeys(fieldSignature, programClass1);
    }

    public TwoKeyMap getXmlReferencedMethods() {
        return this.xmlReferencedMethods;
    }

    public final ModuleInfoClass[] getModuleInfoClasses() {
        ModuleInfoClass[] moduleInfoClass = new ModuleInfoClass[this.moduleInfoClasses.length];
        System.arraycopy(this.moduleInfoClasses, 0, moduleInfoClass, 0, this.moduleInfoClasses.length);
        return moduleInfoClass;
    }

    public ClassHierarchy getClassHierarchy() {
        return this.classHierarchy;
    }

    public String getOpenErrorMessage() {
        return this.openErrorMessage;
    }

    public ClassRepository(
            ObservableHolder observableHolder, ZkmClasspath zkmClasspath, boolean verbose, boolean editionFlag, boolean bl2, boolean scriptModeFlag, KlassMaster klassMaster
    ) throws ZkmException, IOException {
        this.statusHolder = observableHolder;
        this.classpath = zkmClasspath;
        this.classpathLoader = new ClasspathClassLoader(this.classpath, bl2);
        this.classResolver = new ClassResolver(this.classpathLoader);
        this.classpath.addObserver(this);
        this.verbose = verbose;
        this.editionFlag = editionFlag;
        this.scriptModeFlag = scriptModeFlag;
        this.application = klassMaster;
        ZkmClasspath zkmClasspath1 = new ZkmClasspath(ChangeObservable.JAVA_CLASS_PATH);
        this.selfClassLoader = new ClasspathClassLoader(zkmClasspath1, bl2);
        zkmClasspath1.locateRuntimeClasses(new ObservableHolder(), new ObservableHolder());
        TempFileManager.deleteStaleTempFiles();
    }

    public void recordNameChanges(ClassFileBase[] classFileBases) {
        this.originalToCurrentName = ZkmUtils.createHashMap();
        this.fieldSignatureChanges = new TwoKeyMap();
        this.methodSignatureChanges = new TwoKeyMap();

        for (ClassFileBase classFileBase : classFileBases) {
            String string = classFileBase.getOriginalClassName();
            String string1 = classFileBase.getClassName();
            this.originalToCurrentName.put(string, string1);

            for (AbstractFieldInfo abstractFieldInfo : classFileBase.getFields()) {
                this.fieldSignatureChanges.putValue(classFileBase, abstractFieldInfo.getOriginalSignature(), abstractFieldInfo.getSignature());
            }

            for (AbstractMethodInfo abstractMethodInfo : classFileBase.getDeclaredMethods()) {
                this.methodSignatureChanges.putValue(classFileBase, abstractMethodInfo.getOriginalSignature(), abstractMethodInfo.getSignature());
            }
        }

        this.currentToOriginalName = ZkmUtils.invertMap(this.originalToCurrentName);
    }

    public final synchronized List findSubclasses() throws ZkmException, IOException {
        ArrayList arrayList = null;
        int ba = 0;
        int bb = 0;

        for (ProgramClass[] programClass1 = this.programClasses; bb < programClass1.length; programClass1 = this.programClasses) {
            if (this.isSubclass(this.programClasses[ba].getClassName(), "java/applet/Applet")) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }

                arrayList.add(this.programClasses[ba]);
            }

            bb = ++ba;
        }

        return arrayList;
    }

    public final boolean hasProgramClasses() {
        return this.programClasses != null && this.programClasses.length > 0;
    }

    public final synchronized void refreshIndexes() throws ZkmException, IOException {
        ClassHierarchyNode.rehashNodeRegistry();
        this.indexMethods();
        this.indexFields();
        this.classHierarchy.rehashCollections();
        this.setChanged();
        this.notifyObservers();
        ZkmUtils.getHiddenOptionIndex();
    }

    public void resolveReferences(Set set1, Set set2, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        int ba = 0;
        int bb = 0;

        for (ProgramClass[] programClass4 = this.programClasses; bb < programClass4.length; programClass4 = this.programClasses) {
            ProgramClass programClass1 = this.programClasses[ba];
            programClass1.resolveReferences(set1, this, this.classResolver, ignoreMissingReferencesSpec1, scriptEnvironment1);
            if (programClass1.hasVersionedVariants()) {
                Iterator iterator = programClass1.getVersionedVariants().iterator();

                while (iterator.hasNext()) {
                    ClassFileBase classFileBase = (ClassFileBase) iterator.next();
                    ((ProgramClass) classFileBase).resolveReferences(set1, this, this.classResolver, ignoreMissingReferencesSpec1, scriptEnvironment1);
                }
            }

            bb = ++ba;
        }

        ba = 0;
        bb = 0;

        for (ProgramClass[] programClass3 = this.programClasses; bb < programClass3.length; programClass3 = this.programClasses) {
            ProgramClass programClass2 = this.programClasses[ba];
            programClass2.collectBootstrapMethodHandleTargets(set2);
            if (programClass2.hasVersionedVariants()) {
                Iterator iterator1 = programClass2.getVersionedVariants().iterator();

                while (iterator1.hasNext()) {
                    ClassFileBase classFileBase1 = (ClassFileBase) iterator1.next();
                    ((ProgramClass) classFileBase1).collectBootstrapMethodHandleTargets(set2);
                }
            }

            bb = ++ba;
        }
    }

    public int mergeCumulativeNameMapping(Map map1) {
        return this.cumulativeNameMapping.composeWith(map1);
    }

    public final void applyNameChangesToAll(HashMap hashMap, HashMap hashMap1, MessageReporter messageReporter1) throws ZkmException, IOException {
        this.applyNameChanges(this.programClasses, this.moduleInfoClasses, 0, 0, hashMap, hashMap1, messageReporter1);
    }

    public final void indexFields() {
        this.fieldsByName = new ThreeKeyConcurrentMap(Math.max(15, this.programClasses.length * 5));
        this.fieldsBySignature = new TwoKeyMap(Math.max(15, this.programClasses.length * 5));
        this.fieldsByClassNameDesc = new ThreeKeyConcurrentMap(this.programClasses.length, 5);
        int ba = 0;
        int bb = 0;

        for (ProgramClass[] programClass1 = this.programClasses; bb < programClass1.length; programClass1 = this.programClasses) {
            this.programClasses[ba].registerFields(this);
            bb = ++ba;
        }
    }

    public void setYamlSaveFiles(HashMap hashMap) {
        this.yamlSaveFiles = hashMap;
    }

    
    
    public static String checkEvaluationExpiry(String string, String string1, String string2, Object object) throws ZkmException, IOException {
        if (isPermanentLicense()) {
            KlassMaster.validatedExpiryCode = string;
            return "unlimited";
        }
        try {
            String string3 = object.getClass().getName();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
            TimeZone timeZone = TimeZone.getDefault();
            simpleDateFormat.setTimeZone(timeZone);
            long ba = ObfuscatedLongDecoder.decodeLong(string);
            long bb = ObfuscatedLongDecoder.decodeLong(string1);
            long bc = ObfuscatedLongDecoder.decodeLong(string2);
            Date date = new Date(ba);
            String string4 = simpleDateFormat.format(date);
            long bd = System.currentTimeMillis();
            if (bc * 4L * 4L != bb) {
                String string5 = string4 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (09)" : "");
                throw new EvaluationExpiredException(string5);
            }

            if (bd > ba) {
                String string6 = string4 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (10)" : "");
                throw new EvaluationExpiredException(string6);
            }

            if (bd < ba - bb) {
                String string7 = string4 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (11)" : "");
                throw new EvaluationExpiredException(string7);
            }

            long be = bd + bc;
            ClassPathResolver classPathResolver1 = new ClassPathResolver(ChangeObservable.JAVA_CLASS_PATH);
            Object[] objects = classPathResolver1.getEntries();

            for (int i = 0; i < objects.length; i++) {
                if (objects[i] instanceof File) {
                    long bh = ((File) objects[i]).lastModified();
                    if (bh > be) {
                        String string8 = string4 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (12)" : "");
                        throw new EvaluationExpiredException(string8);
                    }
                } else {
                    File file1 = new File(((ZipFile) objects[i]).getName());
                    if (file1.exists()) {
                        if (file1.lastModified() > be) {
                            String string9 = string4 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (13)" : "");
                            throw new EvaluationExpiredException(string9);
                        }

                        if (file1.getName().endsWith("ZKM.jar")) {
                            ArchiveZipFile archiveZipFile = null;
                            boolean bl = false ;

                            label201:
                            {
                                try {
                                    bl = true;
                                    archiveZipFile = new ArchiveZipFile(file1);
                                    ZipEntry zipEntry1 = archiveZipFile.getEntry(string3.replace('.', '/') + ".class");
                                    if (zipEntry1 != null) {
                                        long time = zipEntry1.getTime();
                                        if (time > be) {
                                            String string10 = string4 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (14)" : "");
                                            throw new EvaluationExpiredException(string10);
                                        }

                                        if (time != -1L) {
                                            if (bd > time + bb) {
                                                String string11 = string4 + (HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (15)" : "");
                                                throw new EvaluationExpiredException(string11);
                                            }

                                            bl = false;
                                        } else {
                                            bl = false;
                                        }
                                    } else {
                                        bl = false;
                                    }
                                    break label201;
                                } catch (IOException iOException3) {
                                    bl = false;
                                } finally {
                                    if (bl) {
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
                                        } catch (IOException iOException1) {
                                        }
                                    }
                                    continue;
                                } catch (NumberFormatException numberFormatException) {
                                    throw numberFormatException;
                                }
                            }

                            try {
                                com.zelix.klassmaster.util.ZkmUtils.<java.io.IOException>mayThrow();
                                archiveZipFile.close();
                            } catch (IOException iOException2) {
                            }
                        }
                    }
                }
            }

            classPathResolver1.close();
            return string4;
        } catch (NumberFormatException numberFormatException1) {
            String string12 = HiddenOptionFlags.SHOW_EXPIRY_ERROR_CODES ? " (16)" : "";
            throw new EvaluationExpiredException(string12);
        }
    }

    private static boolean isPermanentLicense() {
        return true;
    }

    public void setArchiveSaveFiles(HashMap hashMap) {
        this.archiveSaveFiles = hashMap;
    }

    public Map getResourceReferencedMethods() {
        return this.resourceReferencedMethods;
    }

    public void markMethodNamesObfuscated() {
        this.methodNamesObfuscated = true;
    }

    public boolean isSubclassOfOriginal(String string, String string1, boolean bl) throws ZkmException, IOException {
        if (string.equals("java/lang/Object")) {
            return false;
        }

        if (string1.equals("java/lang/Object")) {
            return true;
        }

        if (string.equals(string1)) {
            return false;
        }

        Boolean boolean1 = (Boolean) this.preRenameSuperclassCache.getValue(string1, string);
        if (boolean1 != null) {
            return boolean1;
        }

        TwoKeyMap twoKeyMap;
        label46:
        {
            String string2 = this.getOriginalSuperclassName(string);
            if (!bl) {
                if (string2.equals(string1)) {
                    twoKeyMap = this.preRenameSuperclassCache;
                    break label46;
                }
            } else if (ZkmStringUtils.matchesWildcard(string2, string1)) {
                twoKeyMap = this.preRenameSuperclassCache;
                break label46;
            }

            if (string2.equals("java/lang/Object")) {
                this.preRenameSuperclassCache.putValue(string1, string, CACHED_FALSE);
                return false;
            }

            boolean bl1 = this.isSubclassOfOriginal(string2, string1, bl);
            this.preRenameSuperclassCache.putValue(string1, string, bl1 ? CACHED_TRUE : CACHED_FALSE);
            return bl1;
        }

        twoKeyMap.putValue(string1, string, CACHED_TRUE);
        return true;
    }

    @Override
    public final MethodInfo findDeclaredMethod(Object object, Object object1) {
        return (MethodInfo) this.methodsByClass.getValue(object, object1);
    }

    public ReadOnlyMultiMap getMethodSignatureChanges() {
        return new ReadOnlyMultiMap(this.methodSignatureChanges);
    }

    public String getComponentKindName(ClassFileComponent classFileComponent) {
        if (classFileComponent instanceof ProgramClass) {
            return "class";
        } else if (classFileComponent instanceof FieldInfo) {
            return "field";
        } else {
            return classFileComponent instanceof MethodInfo ? "method" : "unknown";
        }
    }

    public void releaseResources() {
        if (this.classpathLoader != null) {
            this.classpathLoader.releaseReaders();
        }

        if (this.classResolver != null) {
            this.classResolver.releaseLoader();
        }

        if (this.selfClassLoader != null) {
            this.selfClassLoader.releaseReaders();
            this.programClasses = null;
        } else {
            this.programClasses = null;
        }

        this.classHierarchy = null;
        this.methodDeclaringClasses = null;
        this.methodsByClass = null;
        this.methodsByClassAndNameType = null;
        this.fieldsByName = null;
        this.fieldsBySignature = null;
        this.fieldsByClassNameDesc = null;
        this.resourceFileIndex.clear();
    }

    public void analyzeUnknownAttributes(ThreeKeyMultiMap threeKeyMultiMap) {
        Enumeration enumeration = threeKeyMultiMap.keys();

        while (enumeration.hasMoreElements()) {
            String string = (String) enumeration.nextElement();
            NestedMultiMap nestedMultiMap = threeKeyMultiMap.getNestedMultiMap(string);
            Enumeration enumeration1 = nestedMultiMap.keys();

            while (enumeration1.hasMoreElements()) {
                String string1 = (String) enumeration1.nextElement();
                ListMultimap listMultimap = nestedMultiMap.getMultimap(string1);
                boolean bl = true;
                boolean bl1 = false;
                ArrayList arrayList = null;
                HashSet hashSet = ZkmUtils.createHashSet();
                Enumeration enumeration2 = listMultimap.keys();

                while (enumeration2.hasMoreElements()) {
                    UnknownAttribute unknownAttribute = (UnknownAttribute) enumeration2.nextElement();
                    hashSet.add(unknownAttribute.getOwningClass());
                    List list1 = listMultimap.getValues(unknownAttribute);
                    if (arrayList == null) {
                        arrayList = new ArrayList(list1.size());

                        for (int i = 0; i < list1.size(); i++) {
                            Object object = list1.get(i);
                            arrayList.add(object.getClass().getName());
                        }
                    } else {
                        if (list1.size() != arrayList.size()) {
                            bl = false;
                            break;
                        }

                        for (int i = 0; i < list1.size(); i++) {
                            String string2 = (String) arrayList.get(i);
                            if (!list1.get(i).getClass().getName().equals(string2)) {
                                bl1 = true;
                                if (!string2.equals("java.lang.Object")) {
                                    arrayList.set(i, "java.lang.Object");
                                }
                            }
                        }
                    }
                }

                if (!bl) {
                    enumeration2 = listMultimap.keys();

                    while (enumeration2.hasMoreElements()) {
                        UnknownAttribute unknownAttribute1 = (UnknownAttribute) enumeration2.nextElement();
                        unknownAttribute1.setParsedAsIndices();
                        unknownAttribute1.setCommonAcrossClasses(false);
                    }
                } else {
                    boolean bl2 = hashSet.size() > 7;
                    Enumeration enumeration3 = listMultimap.keys();

                    while (enumeration3.hasMoreElements()) {
                        UnknownAttribute unknownAttribute2 = (UnknownAttribute) enumeration3.nextElement();
                        unknownAttribute2.setCommonAcrossClasses(bl2);
                        if (bl1) {
                            for (int i = 0; i < arrayList.size(); i++) {
                                String string3 = (String) arrayList.get(i);
                                if (string3.equals("java.lang.Object")) {
                                    unknownAttribute2.clearReferenceAt(i);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void collectBytecodeReferences(NestedMultiMap nestedMultiMap, NestedMultiMap nestedMultiMap1, NestedMultiMap nestedMultiMap2) {
        int ba = this.programClasses.length;

        for (int i = 0; i < ba; i++) {
            this.programClasses[i].indexMemberReferences(nestedMultiMap, nestedMultiMap1, nestedMultiMap2, integerCache);
        }
    }
}
