package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.AbstractFieldInfo;
import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ResolvedClassConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedFieldRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedInterfaceMethodRef;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRefConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchy;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassLoader;
import com.zelix.klassmaster.classfile.hierarchy.InheritedMemberAnalyzer;
import com.zelix.klassmaster.classfile.hierarchy.MethodOverrideAnalyzer;
import com.zelix.klassmaster.classfile.insn.BranchInstruction;
import com.zelix.klassmaster.classfile.insn.CodeInsertion;
import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;
import com.zelix.klassmaster.classfile.insn.ExceptionHandlerSpec;
import com.zelix.klassmaster.classfile.insn.GotoInstruction;
import com.zelix.klassmaster.classfile.insn.Instruction;
import com.zelix.klassmaster.classfile.insn.InvokeInterfaceInstruction;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.LocalVariableAllocator;
import com.zelix.klassmaster.classfile.insn.LocalVariableIndex;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;
import com.zelix.klassmaster.classfile.insn.LocalVariableSlot;
import com.zelix.klassmaster.classfile.insn.MonitorEnterInstruction;
import com.zelix.klassmaster.classfile.insn.MonitorExitInstruction;
import com.zelix.klassmaster.classfile.insn.NewArrayInstruction;
import com.zelix.klassmaster.classfile.insn.SimpleInstruction;
import com.zelix.klassmaster.classfile.insn.TableSwitchInstruction;
import com.zelix.klassmaster.classfile.insn.TypeInstruction;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.MixedCaseNamesMode;
import com.zelix.klassmaster.engine.ClassInitOrderHandler;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.obfuscator.flow.GeneratedCodeBlock;
import com.zelix.klassmaster.obfuscator.flow.GeneratedMethodChunk;
import com.zelix.klassmaster.obfuscator.flow.StaticInitCalleeAnalyzer;
import com.zelix.klassmaster.obfuscator.references.LookupClassFactory;
import com.zelix.klassmaster.obfuscator.references.SyntheticClassFactory;
import com.zelix.klassmaster.obfuscator.trim.ClassMemberSets;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.JavaRuntimeVersion;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ListenerRegistry;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.MutableLong;
import com.zelix.klassmaster.util.MutablePair;
import com.zelix.klassmaster.util.ObjectTriple;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;
import com.zelix.klassmaster.util.ReadOnlyMultiMapView;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.SetValuedMap;
import com.zelix.klassmaster.util.SyncIndexedSet;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import com.zelix.klassmaster.exceptions.ZkmRuntimeException;

import java.util.Collection;

public class MethodParameterChanger extends OpaquePredicateBase implements SyntheticClassFactory {
    public static Iterator randomKeyIterator;
    public static Random sharedRandom;
    private static final IntegerCache INTEGER_CACHE = IntegerCache.getInstance();
    private static final long[] BIT_MASKS = new long[64];
    public Map classKeys;
    public Map initNodes;
    public int[] finalBitLayout;
    public List nodePairKeys;
    public final Map helperMethodTriples = ZkmUtils.createHashMap();
    public final Set specifiedInitMethods = ZkmUtils.createHashSet();
    public final String[] classNamePlaceholders = new String[]{"IiIiI", "LlLlL", "HhHhH"};
    public final Map methodKeys;
    public final SetMultiMap initDependencies;
    public final Set stringEncryptionClasses;
    public final Set integerEncryptionClasses;
    public final Set longEncryptionClasses;
    public final ReadOnlyMultiMapView classesByArchive;
    public final String lookupClassNamePrefix;
    public final MixedCaseNamesMode mixedCaseNamesMode;
    public final boolean avoidSimpleNameClash;
    public final int classMajorVersion;
    public final int classMinorVersion;
    private final boolean keyChainingEnabled;
    public final ClassRepository classRepository;
    public final EnumerableMap classNameMap;
    public final ReadOnlyMultiMap memberNameMap;
    public final ChangeLogMapping changeLogMapping;
    public final Set flowObfuscatedClasses;
    public final StaticInitCalleeAnalyzer staticInitCalleeAnalyzer;
    public final ClassInitOrderHandler classInitOrderHandler;
    public final List nameDictionary;
    public final ClasspathClassLoader classpathClassLoader;
    public final ScriptEnvironment scriptEnvironment;
    public final boolean randomizeNames;
    public final Map classHelperTriples;

    public boolean isKeyChainingEnabled() {
        return this.keyChainingEnabled;
    }

    public static long packIndexKey(int ba, int bb, long bc) {
        new StringBuilder().append("%1$").append(bb).append("s").toString();
        new StringBuilder().append("%1$").append(64 - bb).append("s").toString();
        long bd = 0L | (long) ba << 64 - bb;
        return bd | bc;
    }

    static {
        long ba = 1L;
        int bb = 0;
        int bc = 0;

        for (byte bd = 64; bc < bd; bd = 64) {
            BIT_MASKS[bb] = ba;
            ba <<= 1;
            bc = ++bb;
        }
    }

    
    
    public void readRelationshipFile() {
        if (HiddenOptionFlags.RELATIONSHIP_FILE != null) {
            File file1 = new File(HiddenOptionFlags.RELATIONSHIP_FILE);
            ScriptEnvironment scriptEnvironment1;
            if (file1.exists()) {
                if (!file1.isDirectory()) {
                    if (this.scriptEnvironment.isVerbose()) {
                        this.scriptEnvironment
                                .logLine("Reading '" + file1 + "' to find special " + "Method Parameter List Changing" + " class to method initialization specifications.");
                    }

                    try {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file1));
                        Throwable throwable = null;

                        while (true) {
                            boolean bl = false ;

                            try {
                                bl = true;
                                String string;
                                if ((string = bufferedReader.readLine()) == null) {
                                    bl = false;
                                    break;
                                }

                                string = string.trim();
                                if (string.length() > 0 && !string.startsWith("//")) {
                                    int ba;
                                    label162:
                                    {
                                        ba = string.indexOf(32);
                                        if (ba != -1) {
                                            if (ba < string.length() - 3) {
                                                break label162;
                                            }

                                            scriptEnvironment1 = this.scriptEnvironment;
                                        } else {
                                            scriptEnvironment1 = this.scriptEnvironment;
                                        }

                                        scriptEnvironment1.logFatalError(
                                                "Invalid line in relationship specification file '"
                                                        + file1.getAbsolutePath()
                                                        + "'. Line not in format like 'mypackage.MyClass0 void methodName(int[], mypackage.Class1)' : '"
                                                        + string
                                                        + "' (1)"
                                        );
                                    }

                                    String string1;
                                    String string2;
                                    ClassHierarchyNode classHierarchyNode;
                                    label155:
                                    {
                                        string1 = string.substring(0, ba);
                                        string2 = string.substring(ba + 1);
                                        classHierarchyNode = ClassHierarchyNode.findNode((String) ZkmUtils.mapOrSelf(ZkmUtils.dotsToSlashes(string1), this.classNameMap));
                                        if (classHierarchyNode != null) {
                                            if (classHierarchyNode.isProgramClass()) {
                                                break label155;
                                            }

                                            scriptEnvironment1 = this.scriptEnvironment;
                                        } else {
                                            scriptEnvironment1 = this.scriptEnvironment;
                                        }

                                        scriptEnvironment1.logFatalError(
                                                "Relationship specification file '"
                                                        + file1.getAbsolutePath()
                                                        + "' specifies class '"
                                                        + string1
                                                        + "' which has not been opened."
                                        );
                                    }

                                    ProgramClass programClass1;
                                    int bb;
                                    int bc;
                                    label148:
                                    {
                                        programClass1 = (ProgramClass) classHierarchyNode.getClassFile();
                                        bb = string2.indexOf(40);
                                        bc = string2.indexOf(41);
                                        if (bb >= 1) {
                                            if (bc != -1) {
                                                if (bc == string2.length() - 1) {
                                                    break label148;
                                                }

                                                scriptEnvironment1 = this.scriptEnvironment;
                                            } else {
                                                scriptEnvironment1 = this.scriptEnvironment;
                                            }
                                        } else {
                                            scriptEnvironment1 = this.scriptEnvironment;
                                        }

                                        scriptEnvironment1.logFatalError(
                                                "Relationship specification file '"
                                                        + file1.getAbsolutePath()
                                                        + "' specifies invalid method signature '"
                                                        + string2
                                                        + "' which is not in format like 'void methodName(int[], mypackage.Class1)' (1)."
                                        );
                                    }

                                    String string3;
                                    int bd;
                                    label140:
                                    {
                                        string3 = string2.substring(0, bb);
                                        bd = string3.indexOf(32);
                                        if (bd >= 1) {
                                            if (bd != string3.length() - 1) {
                                                break label140;
                                            }

                                            scriptEnvironment1 = this.scriptEnvironment;
                                        } else {
                                            scriptEnvironment1 = this.scriptEnvironment;
                                        }

                                        scriptEnvironment1.logFatalError(
                                                "Relationship specification file '"
                                                        + file1.getAbsolutePath()
                                                        + "' specifies invalid method signature '"
                                                        + string2
                                                        + "' which is not in format like 'void methodName(int[], mypackage.Class1)' (2)."
                                        );
                                    }

                                    String string4 = MethodSignature.javaTypeToDescriptor(string3.substring(0, bd));
                                    String string5 = string3.substring(bd + 1);
                                    String string6 = MethodSignature.parseParameterList(string2.substring(bb + 1, bc));
                                    MethodSignature methodSignature1 = new MethodSignature(string5, string6, string4);
                                    if (methodSignature1.isStaticInitializer()) {
                                        this.scriptEnvironment
                                                .logFatalError(
                                                        "Relationship specification file '"
                                                                + file1.getAbsolutePath()
                                                                + "' specifies a class initializer method in the line '"
                                                                + string
                                                                + "'."
                                                );
                                    }

                                    MethodSignature methodSignature2 = (MethodSignature) ZkmUtils.multiMapOrSelf(programClass1, methodSignature1, this.memberNameMap);
                                    MethodInfo methodInfo1 = programClass1.findMethodBySignature(methodSignature2);
                                    if (methodInfo1 == null) {
                                        this.scriptEnvironment
                                                .logFatalError(
                                                        "Method '"
                                                                + methodSignature1
                                                                + "' not found in class '"
                                                                + string1
                                                                + "'. The method was specified in relationship specification file '"
                                                                + file1.getAbsolutePath()
                                                                + "' in the line '"
                                                                + string
                                                                + "'."
                                                );
                                    } else {
                                        this.specifiedInitMethods.add(methodInfo1);
                                    }
                                }
                            } catch (Throwable throwable2) {
                                throwable = throwable2;
                                throw throwable2;
                            } finally {
                                if (bl) {
                                    if (throwable != null) {
                                        try {
                                            bufferedReader.close();
                                        } catch (Throwable throwable1) {
                                            throwable.addSuppressed(throwable1);
                                        }
                                    } else {
                                        bufferedReader.close();
                                    }
                                }
                            }
                        }

                        bufferedReader.close();
                    } catch (IOException iOException) {
                        this.scriptEnvironment.logFatalError("Couldn't open relationship file '" + file1.getAbsolutePath() + "'. : " + iOException + "' (2)");
                    }

                    return;
                }

                scriptEnvironment1 = this.scriptEnvironment;
            } else {
                scriptEnvironment1 = this.scriptEnvironment;
            }

            scriptEnvironment1.logFatalError("Relationship file '" + file1.getAbsolutePath() + "' not found or is a directory. (2)");
        }
    }

    public List buildKeyMapChunks(
            List list1,
            ProgramClass programClass1,
            ProgramClass programClass2,
            ProgramClass programClass3,
            ConstantPool constantPool1,
            List list2,
            ObservableHolder observableHolder,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            boolean bl
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        GeneratedMethodChunk generatedMethodChunk = new GeneratedMethodChunk(31000);
        ArrayList arrayList1 = generatedMethodChunk.getInstructions();
        arrayList.add(generatedMethodChunk);
        String string = getMapClassName(bl);
        String string1 = getMapTypeDescriptor(bl);
        FieldInfo fieldInfo = (FieldInfo) programClass1.findField("b", string1);
        ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef(fieldInfo, list2);
        String string2 = "(J)L" + programClass2.getClassName() + ';';
        MethodInfo methodInfo1 = programClass3.findMethodBySignature(new MethodSignature("c", string2));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list2);
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                string, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", list2, classMemberLookup1, classResolver1
        );
        int ba = 0;
        ba++;
        arrayList1.add(Instruction.createObjectLoad(0, generatedMethodChunk.getLocalVariables(), 5));
        ConstantRefInstruction constantRefInstruction = new ConstantRefInstruction(180, resolvedFieldRef);
        ba += constantRefInstruction.getLength();
        arrayList1.add(constantRefInstruction);
        int bb = list1.size();

        for (int i = 0; i < bb; i++) {
            Object object = list1.get(i++);
            ba++;
            arrayList1.add(SimpleInstruction.forOpcode(89));
            if (list2.size() > 32000) {
                int bd = Instruction.appendLongConstantAsShorts((Long) object, arrayList1, constantPool1, list2, observableHolder);
                ba += bd;
            } else {
                Instruction instruction1 = Instruction.createLongConstantLoad((Long) object, constantPool1, list2);
                ba += instruction1.getLength();
                arrayList1.add(instruction1);
            }

            constantRefInstruction = new ConstantRefInstruction(184, resolvedMethodRefConstant);
            ba += constantRefInstruction.getLength();
            arrayList1.add(constantRefInstruction);
            Object object1 = list1.get(i);
            if (object1 instanceof Long) {
                if (list2.size() > 32000) {
                    int be = Instruction.appendLongConstantAsShorts((Long) object1, arrayList1, constantPool1, list2, observableHolder);
                    ba += be;
                } else {
                    Instruction instruction2 = Instruction.createLongConstantLoad((Long) object1, constantPool1, list2);
                    ba += instruction2.getLength();
                    arrayList1.add(instruction2);
                }

                constantRefInstruction = new ConstantRefInstruction(184, resolvedMethodRefConstant);
                ba += constantRefInstruction.getLength();
                arrayList1.add(constantRefInstruction);
            } else {
                ba++;
                arrayList1.add(Instruction.createObjectLoad(0, generatedMethodChunk.getLocalVariables(), 5));
            }

            constantRefInstruction = new ConstantRefInstruction(182, resolvedMethodRefConstant1);
            ba += constantRefInstruction.getLength();
            arrayList1.add(constantRefInstruction);
            ba++;
            arrayList1.add(SimpleInstruction.forOpcode(87));
            if (ba > 65035 && i < bb - 1) {
                arrayList1.add(SimpleInstruction.forOpcode(177));
                generatedMethodChunk = new GeneratedMethodChunk(arrayList1.size() + 100);
                arrayList1 = generatedMethodChunk.getInstructions();
                arrayList.add(generatedMethodChunk);
                arrayList1.add(Instruction.createObjectLoad(0, generatedMethodChunk.getLocalVariables(), 5));
                arrayList1.add(new ConstantRefInstruction(180, resolvedFieldRef));
                ba = 4;
            }
        }

        arrayList1.add(SimpleInstruction.forOpcode(177));
        arrayList1.trimToSize();
        return arrayList;
    }

    public void createInitNodes(ListMultimap listMultimap, ListMultimap listMultimap1, Set set1, Set set2, Set set3) throws ZkmProcessingException {
        HashSet hashSet = ZkmUtils.createHashSet(
                ZkmUtils.getPrimeCapacity(listMultimap.getKeyCount() + listMultimap1.getKeyCount() + set1.size() + set2.size() + set3.size())
        );
        hashSet.addAll(listMultimap.keySet());
        hashSet.addAll(listMultimap1.keySet());
        hashSet.addAll(set1);
        hashSet.addAll(set2);
        hashSet.addAll(set3);
        Iterator iterator = hashSet.iterator();

        while (iterator.hasNext()) {
            ProgramClass programClass1 = (ProgramClass) iterator.next();
            if (!programClass1.isMultiRelease()) {
                List list1 = listMultimap.getValues(programClass1);
                Set set4;
                if (list1 != null) {
                    set4 = this.findStaticInitCallees(programClass1, list1);
                } else {
                    set4 = Collections.emptySet();
                }

                boolean bl = false;
                boolean bl1 = false;
                boolean bl2 = !set4.isEmpty();
                MethodInfo methodInfo1 = null;
                if (list1 != null) {
                    Iterator iterator1 = list1.iterator();

                    while (iterator1.hasNext()) {
                        AbstractMethodInfo abstractMethodInfo = (AbstractMethodInfo) iterator1.next();
                        if (abstractMethodInfo.isStaticInitializer()) {
                            methodInfo1 = (MethodInfo) abstractMethodInfo;
                        } else {
                            bl = true;
                            if (set4.isEmpty() || !set4.contains(abstractMethodInfo)) {
                                bl1 = true;
                            }
                        }
                    }
                }

                List list2 = listMultimap1.getValues(programClass1);
                if (list2 != null) {
                    Iterator iterator2 = list2.iterator();

                    while (iterator2.hasNext()) {
                        AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) iterator2.next();
                        bl = true;
                        if (set4.isEmpty() || !set4.contains(abstractMethodInfo1)) {
                            bl1 = true;
                        }
                    }
                }

                label99:
                if (methodInfo1 == null) {
                    ProgramClass programClass2;
                    ClassRepository classRepository1;
                    if (!bl) {
                        if (!set1.contains(programClass1)) {
                            if (!set2.contains(programClass1)) {
                                if (!set3.contains(programClass1)) {
                                    break label99;
                                }

                                programClass2 = programClass1;
                                classRepository1 = this.classRepository;
                            } else {
                                programClass2 = programClass1;
                                classRepository1 = this.classRepository;
                            }
                        } else {
                            programClass2 = programClass1;
                            classRepository1 = this.classRepository;
                        }
                    } else {
                        programClass2 = programClass1;
                        classRepository1 = this.classRepository;
                    }

                    methodInfo1 = programClass2.getOrCreateStaticInitializer(classRepository1);
                }

                MethodParamChangeNode methodParamChangeNode1 = null;
                if (methodInfo1 != null) {
                    boolean bl3 = !bl1 && bl2;
                    boolean bl4 = bl;
                    MethodInfo methodInfo3 = methodInfo1;
                    methodParamChangeNode1 = new MethodParamChangeNode(methodInfo3, bl4, bl3);
                    this.initNodes.put(methodInfo1, methodParamChangeNode1);
                }

                Iterator iterator3 = set4.iterator();

                while (iterator3.hasNext()) {
                    MethodInfo methodInfo2 = (MethodInfo) iterator3.next();
                    MethodParamChangeNode methodParamChangeNode = new MethodParamChangeNode(methodInfo2);
                    Map map1;
                    if (!HiddenOptionFlags.UNLINKED_PARAMETER_CHANGE_NODES) {
                        methodParamChangeNode1.addDependent(methodParamChangeNode);
                        map1 = this.initNodes;
                    } else {
                        map1 = this.initNodes;
                    }

                    map1.put(methodInfo2, methodParamChangeNode);
                }
            }
        }
    }

    public boolean hasInitNode(Object object) {
        return this.initNodes != null ? this.initNodes.get(object) != null : false;
    }

    public static long permuteAllBits(long ba, int[] bb) {
        return extractPermutedBits(ba, 0, 63, bb);
    }

    @Override
    public ProgramClass createSyntheticClass(String string, int ba, int bb, int bc, Object object) throws ZkmException {
        int bi = (byte) ((OpaquePredicateBase.getPredicateFlag()) ? 1 : 0);
        HelperClassTriple helperClassTriple = (HelperClassTriple) object;
        int be = helperClassTriple.indexOfClassName(string);
        byte bd = (byte) bi;

        try {
            byte[] bf;
            label45:
            {
                byte bj;
                switch (be) {
                    case 0:
                        if (ba < 49) {
                            bf = ZkmUtils.decodeCustomBaseBytes(
                                    "A0A'A)A~zzz7zzzfzzz5zyzzz~zyzzzaz_zzz)z8z*_zzUz_zzz)z8z/z*z/z_zzz)z8z/z*z!z_zzzmz8zXzU_OzU_OzUz=z*z!z_zzzmz8zXzU_OzU_OzUz=z*z|z_zzzhzU_OzU_OzUz_zzz__yz_zzz__Fz_zzzG_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_ZzAz_zzz_zzzAzzzzzzzzzzzhz)z_zzzmzzzFzzzzz)z_zzz+zzz7zzzzz)z_zzzmzzz)zzzzz)z_zzz+zzzhzzzzz)z_zzz+zzzyzzzzzzzz",
                                    "z_A7)hFy~+ma5RO2G`lj0?EBTZ{1s4oWd}-t3w(L8*v,xfDPHS@Vu&Y#6]$= <[.qKiJbn>M'U/cXkC;IQgrN:!ep9|"
                            );
                            break label45;
                        }

                        bf = ZkmUtils.decodeCustomBaseBytes(
                                "A0A'A)A~zzzzzzzSzzz5zyzzzmzyzzzaz_zzz__Fz_zzzmz8zXzU_OzU_OzUz=z*z!z_zzz__yz_zzz)z8z*_zzUz_zzz)z8z/z*z/z_zzz)z8z/z*z!z_zzzmz8zXzU_OzU_OzUz=z*z|z_zzzhzU_OzU_OzUz_zzzG_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_ZzFz_zzz_zzzAzzzzzzzzzzzhz)z_zzz7zzz)zzzzz)z_zzzhzzzFzzzzz)z_zzz7zzzyzzzzz)z_zzzhzzz~zzzzz)z_zzzhzzz+zzzzzzzz",
                                "z_A7)hFy~+ma5RO2G`lj0?EBTZ{1s4oWd}-t3w(L8*v,xfDPHS@Vu&Y#6]$= <[.qKiJbn>M'U/cXkC;IQgrN:!ep9|"
                        );
                        bi = bd;
                        if (bb > 0) {
                            break label45;
                        }

                        bj = 49;
                        break;
                    case 1:
                        bi = ba;
                        bj = 49;
                        break;
                    default:
                        if (ba < 49) {
                            Object[] objects = new Object[]{
                                    31548923297284L,
                                    "A0A'A)A~zzz7zzzfzz_wzyzz_7zyzz_Fzyzz_azyzz_Ozyzz_ozyzz_Wzmzzzhzzz(zmzzzFzzzLzmzzz_zzz8z+zzz_zzz*z+zzz_zzzvz+zzz_zzz,zmzzzFzzzxz+zzz_zzzfz+zzz_zzzDz+zzz_zzzPzmzzzhzzzHzmzzz_zzzSzmzzzFzzz@z+zzz_zzzVzmzzz7zzzuzmzzz_zzz&zazzzAzzzYz+zzz_zzz#zazzzAzzz6z+zzz_zzz]z+zzz_zzz$zmzzz_zzz=zmzzz_zzz z+zzz_zzz<zmzzzFzzz[zazzzAzzz.zmzzzFzzzqz+zzz_zzzKz+zzz_zzzizmzzz7zzzJz+zzz_zzzbz5zz__zzzMz5zz__zzzcz5zz__zzzQz5zz_hzz_)z5zz_5zz_)z5zz_Rzz_)z5zz_Gzzzpz5zz_`zz_2z5zz_lzz_2z5zz_jzz_mz5zz_0zzz>z5zz_?zzzgz5zz_Ezzz/z5zz_?zz_yz5zz_?zzz!z5zz_{zzzIz5zz_?zzz;z5zz_4zz_mz5zz_1zzz:z5zz_dzz_+z5zz_}zz_Oz5zz_?zzzkz5zz_?zzzCz5zz_Zzz_~z5zz_-zzz|z5zz_?zzzNz5zz_tzzznz5zz_{zz_yz5zz_1zz_Oz5zz_?zz_zz5zz_3zz_)z_zzz7z8z*zUz_zzz0z8z*zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z_zzz7z8z*z!z_zzz)z8z*_zzUz_zzz)z8zUz*z/z_zzz?z8zUz*zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z_zzz)z8zUz*z!z_zzzhz8zUzUz*z/z_zzzvz8zUzUzUzX_2_F_1_FzP_{_Z_O_`zPz!_m_~_Z_0_Bz=zX_2_F_1_FzP_{_Z_O_`zPz!_m_~_Z_0_Bz=z*z!z_zzzvz8zUzUzX_2_F_1_FzP_{_Z_O_`zPz!_m_~_Z_0_Bz=zX_2_F_1_FzP_{_Z_O_`zPz!_m_~_Z_0_Bz=zUz*z!z_zzz)z8z/z*z/z_zzzmz8z/z*zXzU_OzU_OzUz=z_zzz)z8z/z*z!z_zzzmz8z/zUzU_zzU_zz/z*z/z_zzz4z8z/z/zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*zXzU_OzU_OzUz=z_zzzmz8zXzU_OzU_OzUz=z*z!z_zzzmz8zXzU_OzU_OzUz=z*z|z_zzzBz8zXzU_OzU_OzUz=zXzU_OzU_OzUz=z*zXzU_OzU_OzUz=z_zzzmz8zXzX_`zX_`zXz=z*z!z_zzz?z8zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*z!z_zzz?z8zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*z|z_zzzEz8zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=zUz*z!z_zzz)z8z|z*z!z_zzzFz _O_j_O_Zz[z_zzz)zJ_0_+_mz_zzzhz'_Rz'_Rz'z_zzz_zUz_zzz__Rz_zzzhzU_OzU_OzUz_zzz_z/z_zzzyzXzU_OzU_OzUz=z_zzzlzX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z_zzzlzX_2_F_1_FzP_{_Z_O_`zPz!_m_~_Z_0_Bz=z_zzzhzX_`zX_`zXz_zzz__jz_zzz__`z_zzzA_zzUz_zzzA_zz/z_zzzm_F_+_+zn_`_m_l_m_j_Zz_zzz__Gz_zzz__2z_zzz__lz_zzzh_~_`_0_j_mz_zzz__Fz_zzz+_m_`_m_l_m_j_ZzK_Zz_zzzF_m_E_{_F_`_Tz_zzz__5z_zzz__~z_zzz__+z_zzz__yz_zzz~_R_F_T_RzJ_0_+_mz_zzz__az_zzzG_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz_zzzG_2_F_1_FzP_{_Z_O_`zPz!_m_~_Z_0_Bz_zzz__0z_zzz__mz_zzz5_T_m_Zzn_`_m_l_m_j_ZzK_Zz_zzz)_T_O_W_mz_zzz__Ozzz_zzz_zzzhzzz_zzzAzzzOzzzAzz_?zz_yzzzzzzzAzz_1zz_OzzzzzzzAzz_Zzz_~zzzzzzzAzz_{zz_yzzzzzzzAzz_lzz_2zzzzzzzmzz_`zz_2zzzzzzz~zz_}zz_Ozzzzzzzmzz_4zz_mzzzzzzzmzz_jzz_mzzzzzzzmzz_3zz_)zzzzzzzmzz_dzz_+zzzzzzzmzz_hzz_)zzzzzzzmzz_Rzz_)zzzzzzzmzz_5zz_)zzzzzzz0zzz+zz_Tzzz>zzz_zz_AzzzzzzzGzzz_zzzzzzzzzzz)_ezzz{_:zzzzzzzzzzz+zz_?zzzrzzz_zz_Azzzzzzz.zzz)zzz~zzzzzzzVzoz+_]_izzzyz7_Xzzz)z)AAzzz3zoAAzzzEz$zhzdAAzzzEz$zFzZzhzZzFAAzzz?z$zyzZz)AGzzza_ezzzGzZz)AzzzzRzZzy_:zzzzzzzzzzz~zz_ZzzzIzzz_zz_Azzzzzz__zzzFzzz)zzzzzzzIzo_ezzzmzGz._ezzz1_ezzzOAAzzzl_fz<zs_ezzzw_MzzzG_ezzzTzsAzzzzjAmzzzAzCzf_:_ezzzTAzzzz}_ezzz5_?_.zzz2_ezzz1Azzzz`Amzzz)_pzzz1Ahzzz_z9zoA_zzz+zC_ezzzTzfAzzzzRzf_:zzzzzzzzzzzmzz_{zzzIzzz_zz_AzzzzzzzBzzz)zzz7zzzzzzzaAhzzz_z9zoA_zzz+zkzx_:zzzzzzzzzzz~zz_?zzzezzz_zz_AzzzzzzzRzzzzzzz_zzzzzzz__!zzzzzzzzzzz~zz_1zzzezzz_zz_AzzzzzzzRzzzzzzz_zzzzzzz__!zzzzzzzzzzzAzz__zzzQzzz_zz_Azzzzzzz3zzz7zzz7zzzzzzzTzvA_zzzyzvzW_|zzz0zv_ezzz1_|zzztzv_ezzzO_|zzz2_!zzzzzzzzzzz_zz_?zzz;zzz_zz_AzzzzzzzMzzzFzzzyzzzzzzz=zv_9zzz0zGz~zGz#zv_9zzztzv_9zzz2AAzzzlzizv_9zzz0zWzv_9zzz-_8_8z#zhzvzEzh_|zzz0zv_9zzzoAGzzzOzv_9zzzozWA7zzzBz7zzzpz}_gzzzzzzzzzzz_zz_1zzzQzzz_zz_Azzzzzzzlzzz7zzz7zzzzzzzFzvzW_|zzz-_!zzzzzzzzzzz_zz_?zzzNzzz_zz_Azzzzzzz*zzzAzzzAzzzzzzz4zvz,_/zzz{zv_9zzzoA`zzz+zvz,_|zzzo_!zv_9zzzoz,A7zzzdzAzz_!zzzzzzzzzzz_zz_szzznzzz_zz_AzzzzzzzdzzzFzzz_zzzzzzz0zv_9zzz0z7zGzyzv_9zzztzv_9zzz2AAzzzl_f_Qzzzzzzzzzzz_zz_Bzzz9zzz_zz_Azzzzzzzkzzz~zzz7zzzzzzzKzvz,_czzzhz)_Qz,Aazzz__[zzzuzv_9zzz0z7zGz#zv_9zzztzv_9zzz2AAzzzlz,Amzzz_zkzx_9zzz0z7zGz#zx_9zzztzx_9zzz2AAzzzl_]_[zzzhz7_Qz)_Qz7_Qzzzzzzzzzzz_zz_1zzz:zzz_zz_AzzzzzzzQzzz~zzz7zzzzzzznzvz,_czzzhz)_Qz,Aazzz__[zzz6zv_9zzz0zGz6zGz.zv_9zzztzv_9zzz2AAzzzlz,Amzzz_zkzx_9zzz0zGz6zGz.zx_9zzztzx_9zzz2AAzzzl_mz+_]_Jzzzhz7_Qz)_Qz)_Qzzzzzzzzzzz_zz_1zzz'zzz_zz_Azzzzzzz`zzz_zzz_zzzzzzzhzv_9zzzt_:zzzzzzzzzzzAzz_?zzzUzzz_zz_Azzzzzzz-zzzFzzz7zzzzzzzEz1z)_+z<zv_9zzz0z7zszv_9zzztzv_9zzz2AAzzzl_gzzzzzzzzzzzAzz_?zzzXzzz_zz_AzzzzzzzozzzFzzz7zzzzzzzlzv_9zzz0z1zszv_9zzztzv_9zzz2AAzzzl_gzzzzzzzzzzzmzz_?zzzgzzz_zz_Azzzzzz_ zzz)zzzOzzzzzz_Hz+z#zFzZz)A~zYz~z7zYz+_Xzzzbzo_ezzzOz?z+zP_3z#zmzZz)z?z+zDzYz5zEzmz+_]_[zzz*z?z5_JzzzRzEzmz?z5_-z#zm_Xzzzjz?z5_KzzzOzEzmz?z5zA_Lz)_h_oz#zmzEzFzEzm_(z#zF_*z+z_z?z+z?z~_>AUAhzGzqzYz~zEzFz#zmz?z~z)_+z4_+zYz5z?z5_JzzzmzEzmz?z5_oz#zmzsz?z~_hz)_+z4_+zYzRz?zR_JzzzmzEzmz?zR_-z#zmzEzm_gzzzzzzzzzzzmzz_ZzzzMzzz_zz_Azzzzzzz>zzzhzzz7zzzzzzz$_ezzzTAzzzz}z=AhzzzFz9z{A_zzz~zXz7z<_Xzzz`z,_ezzzTzsAzzzzjAzzzzR_*zAz_zsz{_>AUA$z7_ezzzTAzzzz}z)_+_ezzzTz,z7AAzzz4_!zzzzzzzzzzzmzz_?zzzCzzz_zz_AzzzzzzznzzzhzzzFzzzzzzz]z{z1_MzzzYz{z1z{_+zh_`_hzYzh_*z)z_z?z)_ezzza_MzzzZz{z?zhzxzfz?z)AAzzz4z?zhz)_hz1zxzfz?z)AAzzz4z{z?zhz1zxzfAAzzzs_!zzzzzzzzzzzmzz_?zzzkzzz_zz_Azzzzzz_Mzzz7zzz+zzzzzz_=z{zYzhz1z)_hzYzFz{zYzy_XzzzjzZz)zfz?zyAzzzzjz?zyAzzzzW_*zyz_z?zyzs_UAUA#_Xzzz/zZz)z?zhAzzzzjAmzzzAzZz)z?zFAzzzzjAmzzzAA7zzzZzAzz_[zzz?zZz)z?zh_*zhz_AzzzzjAmzzzAz$z~_XzzzlzZz)z?zF_*zFz_AzzzzjAmzzzAz$z~zfzZz~z{AzzzzW_*zzz_z?zhz1_'zzzoz?zFzs_UAU_:_Xzzz?zfzZz)z?zhAzzzzjz{AzzzzW_*zzz__*zhz_z?zhz1_UAUA&_!zzzzzzzzzzzz",
                                    "z_A7)hFy~+ma5RO2G`lj0?EBTZ{1s4oWd}-t3w(L8*v,xfDPHS@Vu&Y#6]$= <[.qKiJbn>M'U/cXkC;IQgrN:!ep9|"
                            };
                            bf = ZkmUtils.decodeCustomBaseBytes((String) objects[1], (String) objects[2]);
                        } else {
                            Object[] objects1 = new Object[]{
                                    31548923297284L,
                                    "A0A'A)A~zzzzzzzSzz_xz+zzzjzzzNzmzzz:zzz!zmzzzjzzzezmzzz:zzzpz+zzzjzzz9zmzzz|zz_zz+zzzjzz__z+zzzjzz_Az+zzzjzz_7zmzzzjzz_)z+zzzjzz_hz+zzzjzz_Fzmzzzdzz_yzyzz_~zmzzzdzz_+z+zzzjzz_mzmzzzlzz_azyzzz,zyzz_5zmzzzjzz_Rzmzzzdzz_zzmzzzLzz_Oz+zzzjzz_2z+zzzjzz_Gz+zzzjzz_`zmzzzjzz_lz+zzzjzz_jz+zzzjzz_0zazzzOzz_?zazzzOzz_Ezmzzzjzz_Bzyzz_Tzmzzzdzz_Zzmzzzjzz_{z+zzzjzz_1zmzzzjzz_szmzzzdzz_4zazzzOzz_ozyzz_Wz_zzz__Fz_zzz_z/z_zzz__yz_zzzA_zzUz_zzz__~z_zzzyzXzU_OzU_OzUz=z_zzz__+z_zzz__2z_zzzA_zz/z_zzz__Gz_zzz__mz_zzz__az_zzz?zX_2_F_1_FzP_{_Z_O_`zPzK_B_B_F_ozX_O_T_Zz=z_zzz__lz_zzzlzX_2_F_1_FzP_{_Z_O_`zPz!_m_~_Z_0_Bz=z_zzz__Oz_zzz_zUz_zzz__0z_zzzlzX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z_zzz__Rz_zzz__`z_zzz__jz_zzz__5z_zzz0z8z*zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z_zzz)zJ_0_+_mz_zzz4z8z/z/zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*zXzU_OzU_OzUz=z_zzzmz8z/z*zXzU_OzU_OzUz=z_zzzmz8zXzX_`zX_`zXz=z*z!z_zzzFz _O_j_O_Zz[z_zzz)z8z/z*z!z_zzz)z8z/z*z/z_zzzmz8zXzU_OzU_OzUz=z*z!z_zzz~_R_F_T_RzJ_0_+_mz_zzz7z8z*zUz_zzzF_m_E_{_F_`_Tz_zzz?z8zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*z|z_zzzmz8zXzU_OzU_OzUz=z*z|z_zzz)z8z*_zzUz_zzz)z8zUz*z/z_zzzhz8zUzUz*z/z_zzzmz8z/zUzU_zzU_zz/z*z/z_zzz7z8z*z!z_zzzHz8zUzUzX_2_F_1_FzP_{_Z_O_`zPzK_B_B_F_ozX_O_T_Zz=zX_2_F_1_FzP_{_Z_O_`zPzK_B_B_F_ozX_O_T_Zz=zUz*z!z_zzzHz8zUzUzUzX_2_F_1_FzP_{_Z_O_`zPzK_B_B_F_ozX_O_T_Zz=zX_2_F_1_FzP_{_Z_O_`zPzK_B_B_F_ozX_O_T_Zz=z*z!z5zzz]zzz$zyzz_dz5zzz8zz_}z5zzzDzzziz5zzz8zz_-z5zzz&zzzYzyzz_tz5zz_3zzzcz5zzz=zzz6z5zzz@zzz,z5zzzSzzzHz5zzz8zzzIz5zzz#zzz6z5zzzVzzzuz5zz_wzz_(z_zzzhzU_OzU_OzUz5zz_LzzzUz5zzz zzz6z5zz_8zzz.z_zzzhz'_Rz'_Rz'z5zzzbzzznz5zzzbzzzQz5zzz8zzz*z5zzzvzzz,z5zzzPzzzHz5zzz8zzz;z5zzzDzzz*z5zzzxzzzfz5zzz8zzz>z5zzz8zzzMz5zzz8zzzCz_zzzj_2_F_1_FzP_{_Z_O_`zPzK_B_B_F_ozX_O_T_Zz5zzzbzz_*z5zzz8zzzgz5zzz<zzz6z5zzz8zzzrz5zz_vzz_,z5zzzvzzzXz_zzzG_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz_zzzhzX_`zX_`zXz_zzz)z8z|z*z!z_zzzBz8zXzU_OzU_OzUz=zXzU_OzU_OzUz=z*zXzU_OzU_OzUz=z_zzzG_2_F_1_FzP_{_Z_O_`zPz!_m_~_Z_0_Bz_zzz7_F_+_+z_zzz7_5_m_Zz_zzz?z8zUz*zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z_zzz)_T_O_W_mz_zzzh_~_`_0_j_mz_zzzZz8zX_2_F_1_FzP_{_Z_O_`zPzJ_0_`_`_m_~_Z_O_0_jz=z*z!z_zzz7_T_m_Zz_zzzLz8zUzX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=zzz}zzzjzzzLzzz_zzzOzzzOzzzAzzz8zzz*zzzzzzzAzzzvzzz,zzzzzzzAzzzxzzzfzzzzzzzAzzzDzzz*zzzzzzzAzzzPzzzHzzzzzzzmzzzSzzzHzzzzzzz~zzz@zzz,zzzzzzzmzzzVzzzuzzzzzzzmzzz&zzzYzzzzzzzmzzz#zzz6zzzzzzzmzzz]zzz$zzzzzzzmzzz=zzz6zzzzzzzmzzz zzz6zzzzzzzmzzz<zzz6zzzzzzz0zzz+zzz[zzz.zzz_zzzqzzzzzzzGzzz_zzzzzzzzzzz)_ezzz__:zzzzzzzzzzz+zzz8zzzKzzz_zzzqzzzzzzzqzzz)zzz~zzzzzzzuzoz+_]_Jzzzyz)_Xzzz)z7AAzzzAzoAAzzz7z$zhzdAAzzz7z$zFzZzhzZzFAAzzz)z$zyzZz)AGzzz5_ezzzhzZz)AzzzzFzezZzy_:zzzzzzzzzzz~zzzxzzzizzz_zzzqzzzzzz_hzzzFzzz)zzzzzzzNzo_ezzzyzGz._ezzz~_ezzz+AAzzzm_fz<zs_ezzza_MzzzG_ezzz5zsAzzzzRAmzzzOzCzf_:_ezzz5Azzzz2_ezzzG_?_.zzzl_ezzz~Azzzz`AmzzzlAmzzzl_pzzz~Ahzzzjz9zoA_zzz0zC_ezzz5zfAzzzz?zezf_:zzzzzzzzzzzmzzzDzzzizzz_zzzqzzzzzzzBzzz)zzz7zzzzzzzaAhzzzjz9zoA_zzz0zkzx_:zzzzzzzzzzz~zzz8zzzJzzz_zzzqzzzzzzzRzzzzzzz_zzzzzzz__!zzzzzzzzzzz~zzzvzzzJzzz_zzzqzzzzzzzRzzzzzzz_zzzzzzz__!zzzzzzzzzzzAzzzbzzznzzz_zzzqzzzzzzz3zzz7zzz7zzzzzzzTzvA_zzzEzvzW_|zzzBzv_ezzz~_|zzzTzv_ezzz+_|zzzZ_!zzzzzzzzzzz_zzz8zzz>zzz_zzzqzzzzzzz zzzFzzzyzzzzzzzHzvzGz~zGz#A_zzz{zizv_9zzzBzWzv_9zzz1_8_8z#zhzvzEzh_|zzzBzv_9zzzsAGzzzOzv_9zzzszWA7zzz4z7zzzpz}_gzzzzzzzzzzz_zzzvzzznzzz_zzzqzzzzzzzlzzz7zzz7zzzzzzzFzvzW_|zzz1_!zzzzzzzzzzz_zzz8zzzMzzz_zzzqzzzzzzz,zzzAzzzAzzzzzzzWzvz,_/zzzszv_9zzzsA`zzzazvz,_|zzzs_XzzzRzv_9zzzsz,A7zzzozAzz_!zzzzzzzzzzz_zzz'zzzUzzz_zzzqzzzzzzz0zzzAzzz_zzzzzzz~zvzGz~A_zzzW_f_Qzzzzzzzzzzz_zzz/zzzczzz_zzzqzzzzzzz&zzz)zzzAzzzzzzz*zvz,_czzzhz)_Qz,Aazzzj_[zzzszvzGz6A_zzzWz,AmzzzjzGz6A_zzzW_]_.zzzyz)_Xzzz)z7_Qz7_Qzzzzzzzzzzz_zzzvzzzXzzz_zzzqzzzzzzz=zzzhzzzAzzzzzzzPzvz,_czzzhz)_Qz,Aazzzj_[zzz-zvzGz6zGz.A_zzz{z,AmzzzjzGz6zGz.A_zzz{_mz+_]_izzzyz)_Xzzz)z7_Qz)_Qzzzzzzzzzzz_zzzvzzzkzzz_zzzqzzzzzzz`zzz_zzz_zzzzzzzhzv_9zzzT_:zzzzzzzzzzzAzzz8zzzCzzz_zzzqzzzzzzz?zzz)zzzAzzzzzzz+zvz7z1z)_+A_zzz{_gzzzzzzzzzzzAzzz8zzz;zzz_zzzqzzzzzzzozzzFzzz7zzzzzzzlzv_9zzzBz1zszv_9zzzTzv_9zzzZAAzzzm_gzzzzzzzzzzzmzzz8zzzIzzz_zzzqzzzzzz_ zzz)zzzRzzzzzz_Hz+z#zFzZz)A~zYz~z7zYz+z?z+z?z~_MzzzMzo_ezzz+z?z+zP_3z#zmzZz)z?z+zDzYz5zEzmz+_]_[zzz*z?z5_JzzzRzEzmz?z5_-z#zm_Xzzzjz?z5_KzzzOzEzmz?z5zA_Lz)_h_oz#zmzEzFzEzm_(z#zF_*z+z__XAUAAzGzqzYz~zEzFz#z+z?z~z)_+z4_+zYzaz?za_JzzzmzEz+z?za_oz#z+zsz?z~_hz)_+z4_+zYz5z?z5_JzzzmzEz+z?z5_-z#z+zEz+_gzzzzzzzzzzzmzzzxzzzQzzz_zzzqzzzzzzz*zzzFzzz_zzzzzzz4z7z=z7_ezzz5Azzzz2z)_+_ezzz5Ahzzzdz9_ezzz5A_zzz}z{AAzzz-_!zzzzzzzzzzzmzzz8zzzgzzz_zzzqzzzzzzznzzzhzzzFzzzzzzz]z{z1_MzzzYz{z1z{_+zh_`_hzYzh_*z)z_z?z)_ezzzt_MzzzZz{z?zhzxzfz?z)AAzzz-z?zhz)_hz1zxzfz?z)AAzzz-z{z?zhz1zxzfAAzzz3_!zzzzzzzzzzzmzzz8zzzrzzz_zzzqzzzzzz_/zzz)zzz~zzzzzz_[z{zYzhz1z)_hzYzFz{zYzyz?zyzs_'zzzBzZz)z?zyzfz?zyAzzzzRAzzzzwze_*zyz__XAUAVz?zhz1_'zzzNz?zFzs_'zzzCzZz)z?zhAzzzzRAmzzzOzZz)z?zFAzzzzRAmzzzOA7zzz(zAzz_[zzz?zZz)z?zh_*zhz_AzzzzRAmzzzOz$zy_XzzzlzZz)z?zF_*zFz_AzzzzRAmzzzOz$zyzfz{zZzyAzzzzwze_*zzz__XAU_Qz?zhz1_'zzzZzfz{zZz)z?zhAzzzzRAzzzzwze_*zzz__*zhz__XAUAS_!zzzzzzzzzzzz",
                                    "z_A7)hFy~+ma5RO2G`lj0?EBTZ{1s4oWd}-t3w(L8*v,xfDPHS@Vu&Y#6]$= <[.qKiJbn>M'U/cXkC;IQgrN:!ep9|"
                            };
                            bf = ZkmUtils.decodeCustomBaseBytes((String) objects1[1], (String) objects1[2]);
                        }
                        break label45;
                }

                String string2;
                if (bi < bj) {
                    long bg = 31548923297284L;
                    if (bb > 0) {
                        bf = ZkmUtils.decodeCustomBaseBytes(
                                "A0A'A)A~zzz7zzzfzzz[zyzzz*zyzzzDzyzzzYzyzzz#zyzzz6zmzzz7zzz`zmzzzhzzzlzmzzzAzzzjzmzzz)zzz0z+zzzAzzz?z+zzzAzzzEz+zzzAzzzBz+zzzAzzzTzmzzzhzzzZzazzz_zzz{z+zzzAzzz1z5zzzLzzzsz5zzzSzzz3z5zzz@zzz-z5zzzuzzztz5zzz&zzzPz5zzz@zzzfz5zzzVzzzxz5zzz]zzz,z5zzz$zzzwz5zzzVzzzdz5zzz zzzvz_zzz7z8z*z!z_zzz)z8z*_zzUz_zzz)z8z/z*z/z_zzz)z8z/z*z!z_zzzmz8zXzU_OzU_OzUz=z*z!z_zzzmz8zXzU_OzU_OzUz=z*z|z_zzzBz8zXzU_OzU_OzUz=zXzU_OzU_OzUz=z*zXzU_OzU_OzUz=z_zzz?z8zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*zUz_zzz(z8zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z_zzz6z8zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z_zzz)z8z|z*z!z_zzzFz _O_j_O_Zz[z_zzz)zJ_0_+_mz_zzzhzU_OzU_OzUz_zzz_z/z_zzzyzXzU_OzU_OzUz=z_zzzyzXzX_`zX_`zXz=z_zzz?zX_2_F_1_FzP_{_Z_O_`zPz'_F_T_R_Z_F_y_`_mz=z_zzzhzX_`zX_`zXz_zzz_z|z_zzzA_zzUz_zzz7_5_m_Zz_zzz__yz_zzz__Fz_zzzG_O_+_m_j_Z_O_Z_oz'_F_T_RzJ_0_+_mz_zzz__mz_zzzG_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz_zzzG_2_F_1_FzP_`_F_j_5zPzr_o_T_Z_m_lz_zzzj_2_F_1_FzP_{_Z_O_`zPz'_F_T_R_Z_F_y_`_mz_zzz__~z_zzz7_?_{_Zz_zzz__+z_zzz__az_zzz__5zzz_zzzAzzz7zzz_zzz_zzzFzzzmzzz&zzzPzzzzzzzmzzzVzzzxzzzzzzzAzzz@zzzfzzzzzzzAzzz]zzz,zzzzzzzAzzz<zzzHzzzzzzzAzzz zzzvzzzzzzzazzz~zzzVzzz(zzz_zzz8zzzzzzz`zzz_zzz_zzzzzzzhz{_pzzzm_!zzzzzzzzzzz~zzzVzzz-zzz_zzz8zzzzzzzBzzz7zzz7zzzzzzza_ezzz5zvz,A_zzz~zkzx_:zzzzzzzzzzzAzzz@zzz-zzz_zzz8zzzzzzzvzzz7zzz)zzzzzzzozv_9zzzaz,AzzzzyAmzzz_zCzfA`zzzhzvzCzv_9zzzazxz,AzzzzOzezf_:zzzzzzzzzzzzzzz=zzzszzz_zzz8zzzzzzzRzzzzzzz_zzzzzzz__!zzzzzzzzzzzzzzz]zzzszzz_zzz8zzzzzzzRzzzzzzz_zzzzzzz__!zzzzzzzzzzz_zzzVzzzdzzz_zzz8zzzzzzz*zzzAzzzAzzzzzzz4zvz,_/zzz{zv_9zzzRA`zzz+zvz,_|zzzR_!zv_9zzzRz,A7zzz2zAzz_!zzzzzzzzzzz_zzzVzzzozzz_zzz8zzzzzzzOzzzAzzz7zzzzzzzAz+_gzzzzzzzzzzz_zzz@zzz4zzz_zzz8zzzzzzzOzzz_zzz_zzzzzzzAz__:zzzzzzzzzzz_zzz@zzz}zzz_zzz8zzzzzzzxzzzAzzzAzzzzzzzdzvz,_czzzhz)_Qz,AazzzA_[zzzjzvAAzzz+z,AAzzz+_+_Jzzzhz7_Qz)_Qz7_Qzzzzzzzzzzz_zzz@zzzWzzz_zzz8zzzzzzzlzzz7zzz7zzzzzzzFzvzW_|zzzG_!zzzzzzzzzzz_zzzLzzzszzz_zzz8zzzzzzz`zzz_zzz_zzzzzzzhzvA_zzzF_!zzzzzzzzzzzz",
                                "z_A7)hFy~+ma5RO2G`lj0?EBTZ{1s4oWd}-t3w(L8*v,xfDPHS@Vu&Y#6]$= <[.qKiJbn>M'U/cXkC;IQgrN:!ep9|"
                        );
                        break label45;
                    }

                    string2 = "A0A'A)A~zzzzzzzSzzz[zmzzzOzzz(z+zzzazzzLz+zzzazzz8zmzzzazzz*z+zzzazzzvzmzzz,zzzxzyzzzfzmzzz,zzzDz+zzzazzzPzazzzyzzzHzyzzzSzmzzz@zzzVz+zzzazzzuzyzzz&z_zzz__mz_zzz_z|z_zzz__Fz_zzzyzXzX_`zX_`zXz=z_zzz__yz_zzz8zX_2_F_1_FzP_{_Z_O_`zP_~_0_j_~_{_B_B_m_j_ZzPzJ_0_j_~_{_B_B_m_j_Zz'_F_T_Rzk_F_?z=z_zzz__~z_zzzyzXzU_OzU_OzUz=z_zzz__5z_zzzA_zzUz_zzz__az_zzz_z/z_zzzFz _O_j_O_Zz[z_zzz7z8z*z!z_zzz)zJ_0_+_mz_zzz)z8z|z*z!z_zzzBz8zXzU_OzU_OzUz=zXzU_OzU_OzUz=z*zXzU_OzU_OzUz=z_zzz__+z_zzzmz8zXzU_OzU_OzUz=z*z!z_zzz)z8z/z*z/z_zzz)z8z*_zzUz_zzzmz8zXzU_OzU_OzUz=z*z|z_zzz)z8z/z*z!z5zzz1zzzsz5zzz2zzzGz5zzz`zzzlz5zzzjzzzWz5zzzjzzz0zyzzzYz5zzz#zzz6z_zzzhzU_OzU_OzUz5zzz]zzz$z5zzz?zzzEz5zzz`zzz}z_zzzhzX_`zX_`zXzyzzz=z5zzz zzz<z5zzzZzzz{z_zzzG_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz_zzz(_2_F_1_FzP_{_Z_O_`zP_~_0_j_~_{_B_B_m_j_ZzPzJ_0_j_~_{_B_B_m_j_Zz'_F_T_Rzk_F_?z_zzz7_5_m_Zz_zzz(z8zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z_zzz7_?_{_Zz_zzz6z8zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z_zzzG_2_F_1_FzP_`_F_j_5zPzr_o_T_Z_m_lz_zzzG_O_+_m_j_Z_O_Z_oz'_F_T_RzJ_0_+_mz_zzz?z8zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*zUzzz}zzzazzzOzzz_zzzyzzzFzzzmzzz2zzzGzzzzzzzmzzz`zzzlzzzzzzzAzzzjzzz0zzzzzzzAzzz?zzzEzzzzzzzAzzzBzzzTzzzzzzzAzzzZzzz{zzzzzzzazzz_zzz1zzzszzz_zzz4zzzzzzz`zzz_zzz_zzzzzzzhzvA_zzz__!zzzzzzzzzzz~zzz`zzzozzz_zzz4zzzzzzz`zzz_zzz_zzzzzzzhz{_pzzzA_!zzzzzzzzzzz~zzz`zzzWzzz_zzz4zzzzzzzBzzz7zzz7zzzzzzza_ezzz7zvz,A_zzz)zkzx_:zzzzzzzzzzzAzzzjzzzWzzz_zzz4zzzzzzz,zzz7zzzhzzzzzzzWzv_9zzzhz,AzzzzFAmzzzyzCzfA`zzzhzvzCzv_9zzzhzxz,Azzzz~z$z)zf_:zzzzzzzzzzzzzzzdzzzszzz_zzz4zzzzzzzRzzzzzzz_zzzzzzz__!zzzzzzzzzzzzzzz?zzzszzz_zzz4zzzzzzzRzzzzzzz_zzzzzzz__!zzzzzzzzzzz_zzz`zzz}zzz_zzz4zzzzzzz,zzzAzzzAzzzzzzzWzvz,_/zzzszv_9zzz+A`zzzazvz,_|zzz+_XzzzRzv_9zzz+z,A7zzzmzAzz_!zzzzzzzzzzz_zzz`zzz-zzz_zzz4zzzzzzzOzzzAzzz7zzzzzzzAz+_gzzzzzzzzzzz_zzzjzzztzzz_zzz4zzzzzzzOzzz_zzz_zzzzzzzAz__:zzzzzzzzzzz_zzzjzzz3zzz_zzz4zzzzzzzDzzzAzzzAzzzzzzz-zvz,_czzzhz)_Qz,Aazzza_[zzz?zvAAzzz5z,AAzzz5_+_izzzyz)_Xzzz)z7_Qz7_Qzzzzzzzzzzz_zzzjzzzwzzz_zzz4zzzzzzzlzzz7zzz7zzzzzzzFzvzW_|zzzR_!zzzzzzzzzzzz";
                } else {
                    long bh = 31548923297284L;
                    string2 = "A0A'A)A~zzzzzzzSzzz[zmzzzOzzz(z+zzzazzzLz+zzzazzz8zmzzzazzz*z+zzzazzzvzmzzz,zzzxzyzzzfzmzzz,zzzDz+zzzazzzPzazzzyzzzHzyzzzSzmzzz@zzzVz+zzzazzzuzyzzz&z_zzz__mz_zzz_z|z_zzz__Fz_zzzyzXzX_`zX_`zXz=z_zzz__yz_zzz8zX_2_F_1_FzP_{_Z_O_`zP_~_0_j_~_{_B_B_m_j_ZzPzJ_0_j_~_{_B_B_m_j_Zz'_F_T_Rzk_F_?z=z_zzz__~z_zzzyzXzU_OzU_OzUz=z_zzz__5z_zzzA_zzUz_zzz__az_zzz_z/z_zzzFz _O_j_O_Zz[z_zzz7z8z*z!z_zzz)zJ_0_+_mz_zzz)z8z|z*z!z_zzzBz8zXzU_OzU_OzUz=zXzU_OzU_OzUz=z*zXzU_OzU_OzUz=z_zzz__+z_zzzmz8zXzU_OzU_OzUz=z*z!z_zzz)z8z/z*z/z_zzz)z8z*_zzUz_zzzmz8zXzU_OzU_OzUz=z*z|z_zzz)z8z/z*z!z5zzz1zzzsz5zzz2zzzGz5zzz`zzzlz5zzzjzzzWz5zzzjzzz0zyzzzYz5zzz#zzz6z_zzzhzU_OzU_OzUz5zzz]zzz$z5zzz?zzzEz5zzz`zzz}z_zzzhzX_`zX_`zXzyzzz=z5zzz zzz<z5zzzZzzz{z_zzzG_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz_zzz(_2_F_1_FzP_{_Z_O_`zP_~_0_j_~_{_B_B_m_j_ZzPzJ_0_j_~_{_B_B_m_j_Zz'_F_T_Rzk_F_?z_zzz7_5_m_Zz_zzz(z8zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z_zzz7_?_{_Zz_zzz6z8zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z_zzzG_2_F_1_FzP_`_F_j_5zPzr_o_T_Z_m_lz_zzzG_O_+_m_j_Z_O_Z_oz'_F_T_RzJ_0_+_mz_zzz?z8zX_2_F_1_FzP_`_F_j_5zPz;_y_2_m_~_Zz=z*zUzzz}zzzazzzOzzz_zzzyzzzFzzzmzzz2zzzGzzzzzzzmzzz`zzzlzzzzzzzAzzzjzzz0zzzzzzzAzzz?zzzEzzzzzzzAzzzBzzzTzzzzzzzAzzzZzzz{zzzzzzzazzz_zzz1zzzszzz_zzz4zzzzzzz`zzz_zzz_zzzzzzzhzvA_zzz__!zzzzzzzzzzz~zzz`zzzozzz_zzz4zzzzzzz`zzz_zzz_zzzzzzzhz{_pzzzA_!zzzzzzzzzzz~zzz`zzzWzzz_zzz4zzzzzzzBzzz7zzz7zzzzzzza_ezzz7zvz,A_zzz)zkzx_:zzzzzzzzzzzAzzzjzzzWzzz_zzz4zzzzzzz,zzz7zzzhzzzzzzzWzv_9zzzhz,AzzzzFAmzzzyzCzfA`zzzhzvzCzv_9zzzhzxz,Azzzz~z$z)zf_:zzzzzzzzzzzzzzzdzzzszzz_zzz4zzzzzzzRzzzzzzz_zzzzzzz__!zzzzzzzzzzzzzzz?zzzszzz_zzz4zzzzzzzRzzzzzzz_zzzzzzz__!zzzzzzzzzzz_zzz`zzz}zzz_zzz4zzzzzzz,zzzAzzzAzzzzzzzWzvz,_/zzzszv_9zzz+A`zzzazvz,_|zzz+_XzzzRzv_9zzz+z,A7zzzmzAzz_!zzzzzzzzzzz_zzz`zzz-zzz_zzz4zzzzzzzOzzzAzzz7zzzzzzzAz+_gzzzzzzzzzzz_zzzjzzztzzz_zzz4zzzzzzzOzzz_zzz_zzzzzzzAz__:zzzzzzzzzzz_zzzjzzz3zzz_zzz4zzzzzzzDzzzAzzzAzzzzzzz-zvz,_czzzhz)_Qz,Aazzza_[zzz?zvAAzzz5z,AAzzz5_+_izzzyz)_Xzzz)z7_Qz7_Qzzzzzzzzzzz_zzzjzzzwzzz_zzz4zzzzzzzlzzz7zzz7zzzzzzzFzvzW_|zzzR_!zzzzzzzzzzzz";
                }

                String string1 = string2;
                bf = ZkmUtils.decodeCustomBaseBytes(string1, "z_A7)hFy~+ma5RO2G`lj0?EBTZ{1s4oWd}-t3w(L8*v,xfDPHS@Vu&Y#6]$= <[.qKiJbn>M'U/cXkC;IQgrN:!ep9|");
            }

            ClassFileInputStream classFileInputStream = ClassFileInputStream.fromBytes(bf, false);
            InputFileLocation inputFileLocation = InputFileLocation.createNamedPlaceholder("MethodParameterListChanging." + string);
            ObservableHolder observableHolder = new ObservableHolder();
            ListenerRegistry listenerRegistry1 = new ListenerRegistry();
            PrintWriter printWriter = new PrintWriter(new StringWriter());
            new PrintWriter(new StringWriter());
            ThreeKeyMultiMap threeKeyMultiMap = new ThreeKeyMultiMap();
            ProgramClass programClass1 = new ProgramClass(
                    classFileInputStream, inputFileLocation, observableHolder, listenerRegistry1, printWriter, threeKeyMultiMap, 5
            );
            helperClassTriple.setHelperClass(programClass1, be);
            programClass1.renameClass(string, ZkmUtils.createHashMap(13));
            this.replaceHelperPlaceholders(programClass1, helperClassTriple);
            programClass1.setMajorVersion(ba);
            programClass1.setMinorVersion(bc);
            return programClass1;
        } catch (IOException iOException) {
            throw new ZkmProcessingException("Method Parameter List Changing (A) : " + iOException.getMessage(), iOException);
        }
    }

    public static long extractBitRange(long ba, int bb, int bc) {
        boolean predicateFlagClear = OpaquePredicateBase.isPredicateFlagClear();
        long bd = ba;
        long be = (long) (63 - bc);
        boolean bl = predicateFlagClear;
        long bg = be;
        if (!bl) {
            if (be > 0) {
                bd <<= be;
            }

            bg = (long) (bb + 64 - 1 - bc);
        }

        long bf = bg;
        if (bf > 0) {
            bd >>>= bf;
        }

        return bd;
    }

    public void buildHelperConstructorCode(
            ProgramClass programClass1,
            ProgramClass programClass2,
            LocalVariableList localVariableList1,
            List list1,
            ArrayList arrayList,
            ConstantPool constantPool1,
            List list2,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ObservableHolder observableHolder,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            boolean bl
    ) throws ZkmException, IOException {
        observableHolder.clearValue();
        mutableInt.setValue(1);
        mutableInt1.setValue(4);
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 5));
        String string = getMapClassName(bl);
        String string1 = getMapTypeDescriptor(bl);
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant(string, list2);
        arrayList.add(new TypeInstruction(resolvedClassConstant));
        arrayList.add(SimpleInstruction.forOpcode(89));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(string, "<init>", "()V", list2, classMemberLookup1, classResolver1);
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant));
        FieldInfo fieldInfo = (FieldInfo) programClass1.findField("b", string1);
        ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef(fieldInfo, list2);
        arrayList.add(new ConstantRefInstruction(181, resolvedFieldRef));
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator.next();
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list2);
            arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 5));
            arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
        }

        FieldInfo fieldInfo1 = (FieldInfo) programClass1.findField("g", "[I");
        ResolvedFieldRef resolvedFieldRef1 = constantPool1.getOrCreateFieldRef(fieldInfo1, list2);
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 5));
        arrayList.add(Instruction.createIntPush(64));
        arrayList.add(new NewArrayInstruction(10));
        arrayList.add(new ConstantRefInstruction(181, resolvedFieldRef1));
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 5));
        arrayList.add(new ConstantRefInstruction(180, resolvedFieldRef1));
        int[] bb = createBitPermutation(sharedRandom);
        int ba = 0;
        int bc = 0;

        for (byte bd = 64; bc < bd; bd = 64) {
            if (ba < 63) {
                arrayList.add(SimpleInstruction.forOpcode(89));
            }

            arrayList.add(Instruction.createIntPush(ba));
            arrayList.add(Instruction.createIntPush(bb[ba]));
            arrayList.add(SimpleInstruction.forOpcode(79));
            bc = ++ba;
        }

        String string2 = "(L" + programClass1.getClassName() + ";)V";
        MethodInfo methodInfo2 = programClass2.findMethodBySignature(new MethodSignature("a", string2));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrCreateMethodRefConstant(methodInfo2, list2);
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 5));
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant2));
    }

    public void buildChunkInvokerCode(LocalVariableList localVariableList1, List list1, ArrayList arrayList, ConstantPool constantPool1, List list2) {
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator.next();
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list2);
            arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 5));
            arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant));
        }
    }

    public static SetMultiMap buildClassDependencyMap(ClassHierarchy classHierarchy1) {
        SetMultiMap setMultiMap = new SetMultiMap();
        Enumeration enumeration = classHierarchy1.enumerateInnerClassesHolders();

        while (enumeration.hasMoreElements()) {
            ClassHierarchyNode classHierarchyNode = (ClassHierarchyNode) enumeration.nextElement();
            if (classHierarchyNode.isProgramClass()) {
                ProgramClass programClass1 = (ProgramClass) classHierarchyNode.getClassFile();
                if (programClass1.hasEnclosingMethod() && !programClass1.isStaticInnerClass()) {
                    ClassHierarchyNode classHierarchyNode1 = classHierarchyNode.getEnclosingNode();
                    if (classHierarchyNode1.isProgramClass()) {
                        ProgramClass programClass2 = (ProgramClass) classHierarchyNode1.getClassFile();
                        setMultiMap.addValue(programClass2, programClass1);
                    }
                }
            }
        }

        return setMultiMap;
    }

    public List createMapInitMethods(List list1, ProgramClass programClass1, List list2, ClassMemberLookup classMemberLookup1) throws ZkmProcessingException {
        ArrayList arrayList = new ArrayList(list1.size());
        int ba = 0;
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            GeneratedMethodChunk generatedMethodChunk = (GeneratedMethodChunk) iterator.next();
            String string = "b" + ba++;
            ArrayList arrayList1 = generatedMethodChunk.getInstructions();
            LocalVariableList localVariableList1 = generatedMethodChunk.getLocalVariables();
            ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
            Integer integer = 5;
            ClassMemberLookup classMemberLookup2 = classMemberLookup1;
            MethodInfo methodInfo1 = programClass1.createMethod(
                    string,
                    "()V",
                    arrayList1,
                    4,
                    1,
                    1,
                    false,
                    localVariableList1,
                    exceptionHandlerSpecs,
                    "MethodParameterListChanging",
                    list2,
                    (InheritedMemberAnalyzer) null,
                    classMemberLookup2,
                    integer
            );
            arrayList.add(methodInfo1);
        }

        return arrayList;
    }

    public void buildLinkInvokerCode(
            ProgramClass programClass1,
            ProgramClass programClass2,
            LocalVariableList localVariableList1,
            List list1,
            ArrayList arrayList,
            ConstantPool constantPool1,
            List list2
    ) {
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator.next();
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list2);
            arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 5));
            arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant));
        }

        String string = "(L" + programClass1.getClassName() + ";)V";
        MethodInfo methodInfo2 = programClass2.findMethodBySignature(new MethodSignature("b", string));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrCreateMethodRefConstant(methodInfo2, list2);
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 5));
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant1));
    }

    public void replaceHelperPlaceholders(ProgramClass programClass1, HelperClassTriple helperClassTriple) throws ZkmException, IOException {
        HashMap hashMap = ZkmUtils.createHashMap(13);
        hashMap.put(this.classNamePlaceholders[0], helperClassTriple.getFirstClassName());
        hashMap.put(this.classNamePlaceholders[1], helperClassTriple.getSecondClassName());
        hashMap.put(this.classNamePlaceholders[2], helperClassTriple.getThirdClassName());
        programClass1.remapClassNames(1, 1, hashMap, ZkmUtils.createHashSet(), ZkmUtils.createHashSet(), ZkmUtils.createHashMap(), (MessageReporter) null);
    }

    public void buildHelperStaticInit(
            ProgramClass programClass1,
            LocalVariableList localVariableList1,
            List list1,
            int ba,
            int bb,
            int bc,
            List list2,
            ConstantPool constantPool1,
            List list3,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ObservableHolder observableHolder,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            boolean bl
    ) throws ZkmException, IOException {
        observableHolder.clearValue();
        mutableInt.setValue(3);
        mutableInt1.setValue(4);
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 1);
        list2.add(Instruction.createIntPush(ba));
        FieldInfo fieldInfo = (FieldInfo) programClass1.findField("h", "I");
        ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef(fieldInfo, list3);
        list2.add(new ConstantRefInstruction(179, resolvedFieldRef));
        list2.add(Instruction.createIntPush(bb));
        FieldInfo fieldInfo1 = (FieldInfo) programClass1.findField("n", "I");
        ResolvedFieldRef resolvedFieldRef1 = constantPool1.getOrCreateFieldRef(fieldInfo1, list3);
        list2.add(new ConstantRefInstruction(179, resolvedFieldRef1));
        list2.add(Instruction.createIntPush(bc));
        FieldInfo fieldInfo2 = (FieldInfo) programClass1.findField("l", "I");
        ResolvedFieldRef resolvedFieldRef2 = constantPool1.getOrCreateFieldRef(fieldInfo2, list3);
        list2.add(new ConstantRefInstruction(179, resolvedFieldRef2));
        list2.add(Instruction.createIntPush(64));
        list2.add(new NewArrayInstruction(11));
        FieldInfo fieldInfo3 = (FieldInfo) programClass1.findField("k", "[J");
        ResolvedFieldRef resolvedFieldRef3 = constantPool1.getOrCreateFieldRef(fieldInfo3, list3);
        list2.add(new ConstantRefInstruction(179, resolvedFieldRef3));
        list2.add(SimpleInstruction.forOpcode(10));
        list2.add(Instruction.createLongStore(0, localVariableList1, 5));
        list2.add(SimpleInstruction.forOpcode(3));
        list2.add(Instruction.createIntStore(2, localVariableList1, 5));
        list2.add(labelInstruction);
        list2.add(Instruction.createIntLoad(2, localVariableList1, 5));
        list2.add(Instruction.createIntPush(64));
        list2.add(new BranchInstruction(162, labelInstruction1));
        list2.add(new ConstantRefInstruction(178, resolvedFieldRef3));
        list2.add(Instruction.createIntLoad(2, localVariableList1, 5));
        list2.add(SimpleInstruction.createLongLoad(0, localVariableList1, 5));
        list2.add(SimpleInstruction.forOpcode(80));
        list2.add(SimpleInstruction.createLongLoad(0, localVariableList1, 5));
        list2.add(SimpleInstruction.forOpcode(4));
        list2.add(SimpleInstruction.forOpcode(121));
        list2.add(Instruction.createLongStore(0, localVariableList1, 5));
        list2.add(Instruction.createIntIncrement(2, 1, localVariableList1, 5));
        list2.add(new GotoInstruction(labelInstruction));
        list2.add(labelInstruction1);
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Object", list3);
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/Object", "<init>", "()V", list3, classMemberLookup1, classResolver1
        );
        list2.add(new TypeInstruction(resolvedClassConstant));
        list2.add(SimpleInstruction.forOpcode(89));
        list2.add(new ConstantRefInstruction(183, resolvedMethodRefConstant));
        FieldInfo fieldInfo4 = (FieldInfo) programClass1.findField("o", "Ljava/lang/Object;");
        ResolvedFieldRef resolvedFieldRef4 = constantPool1.getOrCreateFieldRef(fieldInfo4, list3);
        list2.add(new ConstantRefInstruction(179, resolvedFieldRef4));
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/util/Vector", list3);
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/util/Vector", "<init>", "()V", list3, classMemberLookup1, classResolver1
        );
        list2.add(new TypeInstruction(resolvedClassConstant1));
        list2.add(SimpleInstruction.forOpcode(89));
        list2.add(new ConstantRefInstruction(183, resolvedMethodRefConstant1));
        FieldInfo fieldInfo5 = (FieldInfo) programClass1.findField("m", "Ljava/util/Vector;");
        ResolvedFieldRef resolvedFieldRef5 = constantPool1.getOrCreateFieldRef(fieldInfo5, list3);
        list2.add(new ConstantRefInstruction(179, resolvedFieldRef5));
        String string;
        String string1;
        if (bl) {
            string = "java/util/ArrayList";
            string1 = "Ljava/util/ArrayList;";
        } else {
            string = "java/util/Vector";
            string1 = "Ljava/util/Vector;";
        }

        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant(string, list3);
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(string, "<init>", "()V", list3, classMemberLookup1, classResolver1);
        list2.add(new TypeInstruction(resolvedClassConstant2));
        list2.add(SimpleInstruction.forOpcode(89));
        list2.add(new ConstantRefInstruction(183, resolvedMethodRefConstant2));
        FieldInfo fieldInfo6 = (FieldInfo) programClass1.findField("f", string1);
        ResolvedFieldRef resolvedFieldRef6 = constantPool1.getOrCreateFieldRef(fieldInfo6, list3);
        list2.add(new ConstantRefInstruction(179, resolvedFieldRef6));
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator.next();
            ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list3);
            list2.add(new ConstantRefInstruction(184, resolvedMethodRefConstant3));
        }

        FieldInfo fieldInfo7 = (FieldInfo) programClass1.findField("i", "I");
        ResolvedFieldRef resolvedFieldRef7 = constantPool1.getOrCreateFieldRef(fieldInfo7, list3);
        list2.add(new ConstantRefInstruction(178, resolvedFieldRef6));
        ResolvedMethodRefConstant resolvedMethodRefConstant5 = constantPool1.getOrAddMethodRef(string, "size", "()I", list3, classMemberLookup1, classResolver1);
        list2.add(new ConstantRefInstruction(182, resolvedMethodRefConstant5));
        list2.add(new ConstantRefInstruction(179, resolvedFieldRef7));
        MethodInfo methodInfo2 = programClass1.findMethodBySignature(new MethodSignature("c", "()V"));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrCreateMethodRefConstant(methodInfo2, list3);
        list2.add(new ConstantRefInstruction(184, resolvedMethodRefConstant4));
    }

    public List buildNodeCreationBlocks(
            List list1,
            ProgramClass programClass1,
            ConstantPool constantPool1,
            List list2,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            boolean bl
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        GeneratedCodeBlock generatedCodeBlock = new GeneratedCodeBlock();
        ArrayList arrayList1 = generatedCodeBlock.getInstructions();
        arrayList.add(generatedCodeBlock);
        FieldInfo fieldInfo = (FieldInfo) programClass1.findField("e", "[I");
        ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef(fieldInfo, list2);
        String string;
        String string1;
        String string2;
        String string3;
        if (bl) {
            string = "java/util/ArrayList";
            string1 = "Ljava/util/ArrayList;";
            string2 = "add";
            string3 = "(Ljava/lang/Object;)Z";
        } else {
            string = "java/util/Vector";
            string1 = "Ljava/util/Vector;";
            string2 = "addElement";
            string3 = "(Ljava/lang/Object;)V";
        }

        FieldInfo fieldInfo1 = (FieldInfo) programClass1.findField("f", string1);
        ResolvedFieldRef resolvedFieldRef1 = constantPool1.getOrCreateFieldRef(fieldInfo1, list2);
        String string4 = programClass1.getClassName();
        MethodInfo methodInfo1 = programClass1.findMethodBySignature(new MethodSignature("<init>", "(J)V"));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list2);
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                string, string2, string3, list2, classMemberLookup1, classResolver1
        );
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/Object", "clone", "()Ljava/lang/Object;", list2, classMemberLookup1, classResolver1
        );
        int ba = 0;
        int[] bb = null;
        int bc = list1.size();
        ObservableHolder observableHolder = new ObservableHolder();

        for (int i = 0; i < bc; i++) {
            Object object = list1.get(i);
            if (object instanceof Long) {
                ba += this.buildNodeCreationCode(
                        (Long) object,
                        arrayList1,
                        resolvedFieldRef1,
                        resolvedMethodRefConstant,
                        resolvedMethodRefConstant1,
                        string4,
                        constantPool1,
                        list2,
                        observableHolder,
                        bl
                );
            } else if (object instanceof long[]) {
                ba += this.buildRuntimeCheckCode(
                        (long[]) object, string4, programClass1, resolvedFieldRef1, generatedCodeBlock, constantPool1, list2, classMemberLookup1, classResolver1
                );
            } else {
                int[] be = (int[]) object;
                boolean bl1;
                if (bb == null) {
                    bb = be;
                    bl1 = true;
                } else if (ZkmUtils.intArraysEqual(be, bb)) {
                    bl1 = false;
                } else {
                    bl1 = true;
                }

                ba += this.buildLayoutAssignCode(bl1, be, arrayList1, resolvedFieldRef, resolvedMethodRefConstant2, constantPool1, list2);
            }

            if (ba > 65035 && i < bc - 1) {
                arrayList1.add(SimpleInstruction.forOpcode(177));
                generatedCodeBlock = new GeneratedCodeBlock();
                arrayList1 = generatedCodeBlock.getInstructions();
                arrayList.add(generatedCodeBlock);
                ba = 0;
            }
        }

        arrayList1.add(SimpleInstruction.forOpcode(177));
        arrayList1.trimToSize();
        return arrayList;
    }

    public void cacheHelperMethods(ObjectTriple objectTriple) {
        ProgramClass programClass1 = (ProgramClass) objectTriple.getAt(2);
        MethodSignature methodSignature1 = new MethodSignature("g", "()Ljava/lang/Object;");
        MethodInfo methodInfo1 = programClass1.findMethodBySignature(methodSignature1);
        ProgramClass programClass2 = (ProgramClass) objectTriple.getAt(0);
        String string = "(JJLjava/lang/Object;)" + programClass2.getTypeDescriptor();
        MethodSignature methodSignature2 = new MethodSignature("a", string);
        MethodInfo methodInfo2 = programClass1.findMethodBySignature(methodSignature2);
        MethodSignature methodSignature3 = new MethodSignature("a", "(J)J");
        MethodInfo methodInfo3 = programClass2.findMethodBySignature(methodSignature3);
        this.helperMethodTriples.put(objectTriple, new ObjectTriple(methodInfo1, methodInfo2, methodInfo3));
    }

    public List buildLinkChunks(
            List list1,
            List list2,
            ProgramClass programClass1,
            ProgramClass programClass2,
            ConstantPool constantPool1,
            List list3,
            ObservableHolder observableHolder,
            MutableInt mutableInt,
            MutableInt mutableInt1
    ) throws ZkmException, IOException {
        mutableInt.setValue(1);
        mutableInt1.setValue(2);
        ArrayList arrayList = new ArrayList();
        GeneratedMethodChunk generatedMethodChunk = new GeneratedMethodChunk(27000);
        ArrayList arrayList1 = generatedMethodChunk.getInstructions();
        arrayList.add(generatedMethodChunk);
        int ba = 0;
        String string = "(J)L" + programClass1.getClassName() + ';';
        MethodInfo methodInfo1 = programClass2.findMethodBySignature(new MethodSignature("c", string));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list3);
        String string1 = "(L" + programClass1.getClassName() + ";)V";
        MethodInfo methodInfo2 = programClass1.findMethodBySignature(new MethodSignature("a", string1));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = constantPool1.getOrCreateInterfaceMethodRef(methodInfo2, list3);
        int bb = list1.size();

        for (int i = 0; i < bb; i++) {
            MutablePair mutablePair = (MutablePair) list1.get(i);
            if (list3.size() > 32000) {
                int bd = Instruction.appendLongConstantAsShorts((Long) mutablePair.getKey(), arrayList1, constantPool1, list3, observableHolder);
                ba += bd;
            } else {
                Instruction instruction1 = Instruction.createLongConstantLoad((Long) mutablePair.getKey(), constantPool1, list3);
                arrayList1.add(instruction1);
                ba += instruction1.getLength();
            }

            com.zelix.klassmaster.classfile.insn.ConstantRefInstruction invokeInterfaceInstruction = new ConstantRefInstruction(184, resolvedMethodRefConstant);
            ba += invokeInterfaceInstruction.getLength();
            arrayList1.add(invokeInterfaceInstruction);
            if (list3.size() > 32000) {
                int bg = Instruction.appendLongConstantAsShorts((Long) mutablePair.getValue(), arrayList1, constantPool1, list3, observableHolder);
                ba += bg;
            } else {
                Instruction instruction2 = Instruction.createLongConstantLoad((Long) mutablePair.getValue(), constantPool1, list3);
                arrayList1.add(instruction2);
                ba += instruction2.getLength();
            }

            invokeInterfaceInstruction = new ConstantRefInstruction(184, resolvedMethodRefConstant);
            ba += invokeInterfaceInstruction.getLength();
            arrayList1.add(invokeInterfaceInstruction);
            invokeInterfaceInstruction = new InvokeInterfaceInstruction(resolvedInterfaceMethodRef);
            ba += invokeInterfaceInstruction.getLength();
            arrayList1.add(invokeInterfaceInstruction);
            if (ba > 65035) {
                arrayList1.add(SimpleInstruction.forOpcode(177));
                generatedMethodChunk = new GeneratedMethodChunk(arrayList1.size() + 100);
                arrayList1 = generatedMethodChunk.getInstructions();
                arrayList.add(generatedMethodChunk);
                ba = 0;
            }
        }

        MethodInfo methodInfo3 = programClass1.findMethodBySignature(new MethodSignature("b", "(J)V"));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef1 = constantPool1.getOrCreateInterfaceMethodRef(methodInfo3, list3);
        int bh = list2.size();

        for (int i = 0; i < bh; i++) {
            MutablePair mutablePair1 = (MutablePair) list2.get(i);
            if (list3.size() > 32000) {
                int bf = Instruction.appendLongConstantAsShorts((Long) mutablePair1.getKey(), arrayList1, constantPool1, list3, observableHolder);
                ba += bf;
            } else {
                Instruction instruction3 = Instruction.createLongConstantLoad((Long) mutablePair1.getKey(), constantPool1, list3);
                ba += instruction3.getLength();
                arrayList1.add(instruction3);
            }

            com.zelix.klassmaster.classfile.insn.ConstantRefInstruction invokeInterfaceInstruction1 = new ConstantRefInstruction(184, resolvedMethodRefConstant);
            ba += invokeInterfaceInstruction1.getLength();
            arrayList1.add(invokeInterfaceInstruction1);
            if (list3.size() > 32000) {
                int bi = Instruction.appendLongConstantAsShorts((Long) mutablePair1.getValue(), arrayList1, constantPool1, list3, observableHolder);
                ba += bi;
            } else {
                Instruction instruction4 = Instruction.createLongConstantLoad((Long) mutablePair1.getValue(), constantPool1, list3);
                ba += instruction4.getLength();
                arrayList1.add(instruction4);
            }

            invokeInterfaceInstruction1 = new InvokeInterfaceInstruction(resolvedInterfaceMethodRef1);
            ba += invokeInterfaceInstruction1.getLength();
            arrayList1.add(invokeInterfaceInstruction1);
            if (ba > 65035 && i < bh - 1) {
                arrayList1.add(SimpleInstruction.forOpcode(177));
                generatedMethodChunk = new GeneratedMethodChunk(arrayList1.size() + 100);
                arrayList1 = generatedMethodChunk.getInstructions();
                arrayList.add(generatedMethodChunk);
                ba = 0;
            }
        }

        arrayList1.add(SimpleInstruction.forOpcode(177));
        arrayList1.trimToSize();
        return arrayList;
    }

    public void buildSingletonInit(
            ProgramClass programClass1,
            ArrayList arrayList,
            ConstantPool constantPool1,
            List list1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        String string = programClass1.getClassName();
        String string1 = 'L' + string + ';';
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant(string, list1);
        arrayList.add(new TypeInstruction(resolvedClassConstant));
        arrayList.add(SimpleInstruction.forOpcode(89));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(string, "<init>", "()V", list1, classMemberLookup1, classResolver1);
        arrayList.add(new ConstantRefInstruction(183, resolvedMethodRefConstant));
        FieldInfo fieldInfo = (FieldInfo) programClass1.findField("a", string1);
        ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef(fieldInfo, list1);
        arrayList.add(new ConstantRefInstruction(179, resolvedFieldRef));
    }

    public List buildLayoutChunks(
            List list1,
            ProgramClass programClass1,
            ProgramClass programClass2,
            ConstantPool constantPool1,
            List list2,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        mutableInt.setValue(1);
        mutableInt1.setValue(4);
        ArrayList arrayList = new ArrayList();
        GeneratedMethodChunk generatedMethodChunk = new GeneratedMethodChunk(44000);
        ArrayList arrayList1 = generatedMethodChunk.getInstructions();
        arrayList.add(generatedMethodChunk);
        int be = 0;
        String string = "(J)L" + programClass1.getClassName() + ';';
        MethodInfo methodInfo1 = programClass2.findMethodBySignature(new MethodSignature("c", string));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list2);
        MethodInfo methodInfo2 = programClass1.findMethodBySignature(new MethodSignature("b", "()[I"));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = constantPool1.getOrCreateInterfaceMethodRef(methodInfo2, list2);
        int ba = list1.size();

        for (int i = 0; i < ba; i++) {
            Object object = list1.get(i++);
            int[] bc = (int[]) list1.get(i);
            if (list2.size() > 32000) {
                int bd = Instruction.appendLongConstantAsShorts((Long) object, arrayList1, constantPool1, list2, observableHolder);
                be += bd;
            } else {
                Instruction instruction2 = Instruction.createLongConstantLoad((Long) object, constantPool1, list2);
                be += instruction2.getLength();
                arrayList1.add(instruction2);
            }

            com.zelix.klassmaster.classfile.insn.ConstantRefInstruction invokeInterfaceInstruction = new ConstantRefInstruction(184, resolvedMethodRefConstant);
            be += invokeInterfaceInstruction.getLength();
            arrayList1.add(invokeInterfaceInstruction);
            invokeInterfaceInstruction = new InvokeInterfaceInstruction(resolvedInterfaceMethodRef);
            be += invokeInterfaceInstruction.getLength();
            arrayList1.add(invokeInterfaceInstruction);
            be++;
            arrayList1.add(SimpleInstruction.forOpcode(89));
            int bf = 0;
            int bh = 0;

            for (byte bg = 64; bh < bg; bg = 64) {
                Instruction instruction1 = Instruction.createIntPush(bf);
                be += instruction1.getLength();
                arrayList1.add(instruction1);
                instruction1 = Instruction.createIntPush(bc[bf]);
                be += instruction1.getLength();
                arrayList1.add(instruction1);
                be++;
                arrayList1.add(SimpleInstruction.forOpcode(79));
                if (bf < 63) {
                    be++;
                    arrayList1.add(SimpleInstruction.forOpcode(89));
                }

                bh = ++bf;
            }

            if (be > 65035 && i < ba - 1) {
                arrayList1.add(SimpleInstruction.forOpcode(177));
                generatedMethodChunk = new GeneratedMethodChunk(arrayList1.size() + 100);
                arrayList1 = generatedMethodChunk.getInstructions();
                arrayList.add(generatedMethodChunk);
                be = 0;
            }
        }

        arrayList1.add(SimpleInstruction.forOpcode(177));
        arrayList1.trimToSize();
        return arrayList;
    }

    public static long composeKey(int ba, long bb, int bc, int[] bd) {
        return applyBitPermutation(packKey(ba, bb, bc), bd);
    }

    public static long extractPermutedBits(long ba, int bb, int bc, int[] bd) {
        return extractBitRange(permuteBits(ba, bd), bb, bc);
    }

    public void addStringEncryptionClass(Object object) {
        this.stringEncryptionClasses.add(object);
    }

    public int buildInitKeyCode(
            ProgramClass programClass1,
            List list1,
            MethodParamChangeNode methodParamChangeNode,
            MethodParamChangeNode methodParamChangeNode1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        ObjectTriple objectTriple = this.getHelperTriple(programClass1);
        ConstantPool constantPool1 = programClass1.getClassConstantPool();
        byte ba;
        if (objectTriple != null) {
            ObjectTriple objectTriple1 = this.getHelperMethods(objectTriple);
            MethodInfo methodInfo1 = (MethodInfo) objectTriple1.getFirst();
            MethodInfo methodInfo2 = (MethodInfo) objectTriple1.getSecond();
            MethodInfo methodInfo3 = (MethodInfo) objectTriple1.getThird();
            if (HiddenOptionFlags.SYNCHRONIZE_PARAMETER_LOOKUP) {
                ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list1);
                arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
                arrayList.add(new MonitorEnterInstruction());
            }

            ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrCreateMethodRefConstant(methodInfo2, list1);
            ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = constantPool1.getOrCreateInterfaceMethodRef(methodInfo3, list1);
            arrayList.add(Instruction.createLongConstantLoad(methodParamChangeNode.getPrimaryKey(), constantPool1, list1));
            arrayList.add(Instruction.createLongConstantLoad(methodParamChangeNode.getSecondaryKey(), constantPool1, list1));
            if (programClass1.supportsJava7() && !HiddenOptionFlags.NO_METHOD_HANDLES_LOOKUP) {
                ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                        "java/lang/invoke/MethodHandles", "lookup", "()Ljava/lang/invoke/MethodHandles$Lookup;", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant1));
                ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                        "java/lang/invoke/MethodHandles$Lookup", "lookupClass", "()Ljava/lang/Class;", list1, classMemberLookup1, classResolver1
                );
                arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
            } else {
                arrayList.add(SimpleInstruction.forOpcode(1));
            }

            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant3));
            arrayList.add(Instruction.createLongConstantLoad(methodParamChangeNode.getRandomKey(), constantPool1, list1));
            arrayList.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef));
            boolean bl;
            if (methodParamChangeNode1 != null) {
                if (methodParamChangeNode1.isKeyFieldShared()) {
                    arrayList.add(new ConstantRefInstruction(178, methodParamChangeNode1.getKeyFieldRef()));
                    arrayList.add(SimpleInstruction.forOpcode(131));
                    bl = HiddenOptionFlags.SYNCHRONIZE_PARAMETER_LOOKUP;
                } else {
                    bl = HiddenOptionFlags.SYNCHRONIZE_PARAMETER_LOOKUP;
                }
            } else {
                bl = HiddenOptionFlags.SYNCHRONIZE_PARAMETER_LOOKUP;
            }

            if (bl) {
                ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list1);
                arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant4));
                arrayList.add(new MonitorExitInstruction());
            }

            ba = 5;
        } else {
            arrayList.add(Instruction.createLongConstantLoad(methodParamChangeNode.getEffectiveKey(), constantPool1, list1));
            ba = 2;
        }

        methodParamChangeNode.setInitInstructions(arrayList);
        return ba;
    }

    public static long extractLowPermutedBits(long ba, int bb, int[] bc) {
        return extractPermutedBits(ba, 0, bb - 1, bc);
    }

    public int buildNodeCreationCode(
            Long long1,
            List list1,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedMethodRefConstant resolvedMethodRefConstant,
            ResolvedMethodRefConstant resolvedMethodRefConstant1,
            String string,
            ConstantPool constantPool1,
            List list2,
            ObservableHolder observableHolder,
            boolean bl
    ) throws ZkmException, IOException {
        ConstantRefInstruction constantRefInstruction = new ConstantRefInstruction(178, resolvedFieldRef);
        int ba = 0 + constantRefInstruction.getLength();
        list1.add(constantRefInstruction);
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant(string, list2);
        constantRefInstruction = new TypeInstruction(resolvedClassConstant);
        ba += constantRefInstruction.getLength();
        list1.add(constantRefInstruction);
        ba++;
        list1.add(SimpleInstruction.forOpcode(89));
        if (list2.size() > 32000) {
            int bb = Instruction.appendLongConstantAsShorts(long1, list1, constantPool1, list2, observableHolder);
            ba += bb;
        } else {
            Instruction instruction1 = Instruction.createLongConstantLoad(long1, constantPool1, list2);
            ba += instruction1.getLength();
            list1.add(instruction1);
        }

        constantRefInstruction = new ConstantRefInstruction(183, resolvedMethodRefConstant);
        ba += constantRefInstruction.getLength();
        list1.add(constantRefInstruction);
        constantRefInstruction = new ConstantRefInstruction(182, resolvedMethodRefConstant1);
        ba += constantRefInstruction.getLength();
        list1.add(constantRefInstruction);
        if (bl) {
            ba++;
            list1.add(SimpleInstruction.forOpcode(87));
        }

        return ba;
    }

    public static long permuteBits(long ba, int[] bb) {
        long bh = 0L;
        boolean predicateFlagClear = OpaquePredicateBase.isPredicateFlagClear();
        long bc = 0L;
        boolean bl = predicateFlagClear;
        int bd = bb.length;
        int be = 0;

        while (true) {
            if (be < bd) {
                bh = ba;
                if (bl) {
                    break;
                }

                long bf = ba & BIT_MASKS[be];
                long bg = (long) bb[be];
                if (!bl) {
                    if (bf != 0L) {
                        label31:
                        {
                            long bi = bg;
                            if (!bl) {
                                if (bg > 0) {
                                    bf >>>= bg;
                                    if (!bl) {
                                        break label31;
                                    }
                                }

                                bi = bg;
                            }

                            if (bi < 0) {
                                bf <<= ~bg + 1;
                            }
                        }

                        bc |= bf;
                    }

                    be++;
                }

                if (!bl) {
                    continue;
                }
            }

            bh = bc;
            break;
        }

        return bh;
    }

    public void addDependency(ClassHierarchyNode classHierarchyNode, ClassHierarchyNode classHierarchyNode1, Map map1, String string, boolean bl) {
        this.addDependencyChecked(classHierarchyNode, classHierarchyNode1, false, map1, string, bl);
    }

    public void generateHelperClasses(
            Set set1,
            int ba,
            int bb,
            int bc,
            List list1,
            List list2,
            ObservableHolder observableHolder,
            List list3,
            List list4,
            List list5,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        Iterator iterator = set1.iterator();

        while (iterator.hasNext()) {
            ObjectTriple objectTriple = (ObjectTriple) iterator.next();
            ProgramClass programClass1 = (ProgramClass) objectTriple.getAt(0);
            ProgramClass programClass2 = (ProgramClass) objectTriple.getAt(1);
            ProgramClass programClass3 = (ProgramClass) objectTriple.getAt(2);
            this.buildKeyTableClass(programClass3, programClass2, ba, bb, bc, list1, observableHolder, classMemberLookup1, classResolver1);
            this.buildLookupClass(programClass2, programClass1, programClass3, list2, list3, list4, list5, classMemberLookup1, classResolver1);
        }
    }

    public int buildLayoutAssignCode(
            boolean bl,
            int[] ba,
            List list1,
            ResolvedFieldRef resolvedFieldRef,
            ResolvedMethodRefConstant resolvedMethodRefConstant,
            ConstantPool constantPool1,
            List list2
    ) {
        int bb = 0;
        if (bl) {
            Instruction instruction1 = Instruction.createIntPush(64);
            bb = 0 + instruction1.getLength();
            list1.add(instruction1);
            bb += 2;
            list1.add(new NewArrayInstruction(10));
            int bc = 0;
            int bd = 0;

            for (byte be = 64; bd < be; be = 64) {
                bb++;
                list1.add(SimpleInstruction.forOpcode(89));
                instruction1 = Instruction.createIntPush(bc);
                bb += instruction1.getLength();
                list1.add(instruction1);
                instruction1 = Instruction.createIntPush(ba[bc]);
                bb += instruction1.getLength();
                list1.add(instruction1);
                bb++;
                list1.add(SimpleInstruction.forOpcode(79));
                bd = ++bc;
            }
        } else {
            ConstantRefInstruction constantRefInstruction = new ConstantRefInstruction(178, resolvedFieldRef);
            bb += constantRefInstruction.getLength();
            list1.add(constantRefInstruction);
            constantRefInstruction = new ConstantRefInstruction(182, resolvedMethodRefConstant);
            bb += constantRefInstruction.getLength();
            list1.add(constantRefInstruction);
            ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("[I", list2);
            constantRefInstruction = new ConstantRefInstruction(192, resolvedClassConstant);
            bb += constantRefInstruction.getLength();
            list1.add(constantRefInstruction);
        }

        ConstantRefInstruction constantRefInstruction1 = new ConstantRefInstruction(179, resolvedFieldRef);
        bb += constantRefInstruction1.getLength();
        list1.add(constantRefInstruction1);
        return bb;
    }

    public static long composeIndexKey(int ba, int bb, long bc, int[] bd) {
        return applyBitPermutation(packIndexKey(ba, bb, bc), bd);
    }

    public void setClassKeys(Map map1) {
        this.classKeys = map1;
    }

    public Set findStaticInitCallees(ProgramClass programClass1, List list1) {
        HashSet hashSet = ZkmUtils.createHashSet();
        ClassMemberSets classMemberSets = this.scriptEnvironment.getTrimExclusionMemberSets();
        Set set1;
        if (classMemberSets != null) {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                MethodInfo methodInfo1 = (MethodInfo) iterator.next();
                if (!methodInfo1.isStaticInitializer()
                        && classMemberSets.containsMethod(methodInfo1)
                        && this.isMainMethod(methodInfo1)
                        && !methodInfo1.getOwnerProgramClass().isMultiRelease()) {
                    hashSet.add(methodInfo1);
                }
            }

            set1 = this.specifiedInitMethods;
        } else {
            set1 = this.specifiedInitMethods;
        }

        Iterator iterator1 = set1.iterator();

        while (iterator1.hasNext()) {
            MethodInfo methodInfo2 = (MethodInfo) iterator1.next();
            if (methodInfo2.getOwnerProgramClass() == programClass1) {
                if (list1.contains(methodInfo2)) {
                    hashSet.add(methodInfo2);
                    if (this.scriptEnvironment.isVerbose()) {
                        this.scriptEnvironment
                                .logLine(
                                        "Setting initialization of method '"
                                                + methodInfo2.getOriginalNameWithParameters()
                                                + "' in class '"
                                                + methodInfo2.getOriginalDottedName()
                                                + "' as specified in relationship file '"
                                                + HiddenOptionFlags.RELATIONSHIP_FILE
                                                + "'."
                                );
                    }
                } else {
                    this.scriptEnvironment
                            .logError(
                                    "Method '"
                                            + methodInfo2.getOriginalNameWithParameters()
                                            + "' in class '"
                                            + methodInfo2.getOriginalDottedName()
                                            + "' specified in relationship file '"
                                            + HiddenOptionFlags.RELATIONSHIP_FILE
                                            + "' but it has no relevant structures."
                            );
                }
            }
        }

        return hashSet;
    }

    public ObjectTriple getHelperTriple(Object object) {
        return (ObjectTriple) this.classHelperTriples.get(object);
    }

    public boolean canKeyLongEncryption(MethodInfo methodInfo1) {
        if (methodInfo1 == null) {
            return false;
        }

        ProgramClass programClass1 = methodInfo1.getOwnerProgramClass();
        return (programClass1.supportsJava8() || programClass1.supportsJava4() && !programClass1.isInterface())
                && this.hasInitNode(methodInfo1)
                && this.longEncryptionClasses.contains(programClass1)
                && HiddenOptionFlags.LONG_ENCRYPT_DES
                && ZkmUtils.isClassAvailable();
    }

    public static Map assignMethodKeys(Set set1, Map map1, boolean bl, int ba, ChangeLogMapping changeLogMapping1, MethodOverrideAnalyzer methodOverrideAnalyzer) {
        Random random1;
        label147:
        {
            byte bc;
            if (!bl) {
                if (!HiddenOptionFlags.RANDOMIZE_OBFUSCATION) {
                    sharedRandom = ZkmUtils.createSeededRandom(ba);
                    random1 = sharedRandom;
                    break label147;
                }

                bc = 69;
            } else {
                bc = 69;
            }

            sharedRandom = ZkmUtils.createRandom(bc);
            random1 = sharedRandom;
        }

        randomKeyIterator = random1.longs(1L, 140737488355328L).iterator();
        HashSet hashSet = ZkmUtils.createHashSet();
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(Math.max(ba * 5, map1.size())));
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            MethodInfo methodInfo1 = (MethodInfo) entry.getKey();
            Long long1 = (Long) entry.getValue();
            AbstractMethodInfo abstractMethodInfo = methodOverrideAnalyzer.findRootMethod(methodInfo1);
            if (abstractMethodInfo != null) {
                Long long2 = (Long) map1.get(abstractMethodInfo);
                if (long2 == null) {
                    long2 = (Long) hashMap.get(abstractMethodInfo);
                }

                if (long2 != null) {
                    if (!long2.equals(long1)) {
                        changeLogMapping1.logError(
                                "Method '"
                                        + methodInfo1.getOriginalNameWithParameters()
                                        + "' in class '"
                                        + methodInfo1.getOriginalDottedName()
                                        + "' has a different '"
                                        + "Method Parameter List Changing"
                                        + "' mapping than method '"
                                        + abstractMethodInfo.getOriginalNameWithParameters()
                                        + "' in class '"
                                        + abstractMethodInfo.getOriginalDottedName()
                                        + "'. Will use the mapping of the method in class '"
                                        + abstractMethodInfo.getOriginalDottedName()
                                        + "'. You must distribute this application as a whole."
                        );
                    }

                    hashMap.put((MethodInfo) abstractMethodInfo, long2);
                    hashMap.put(methodInfo1, long2);
                    hashSet.add(long2);
                } else {
                    if (abstractMethodInfo.isProgramMember()) {
                        hashMap.put((MethodInfo) abstractMethodInfo, long1);
                    }

                    hashMap.put(methodInfo1, long1);
                    hashSet.add(long1);
                }
            } else {
                hashMap.put(methodInfo1, long1);
                hashSet.add(long1);
            }
        }

        HashSet hashSet1 = ZkmUtils.createHashSet();
        Iterator iterator2 = set1.iterator();

        while (iterator2.hasNext()) {
            MethodInfo methodInfo3 = (MethodInfo) iterator2.next();
            if (methodInfo3.getOwnerProgramClass().hasVersionedVariants()) {
                hashSet1.add(methodInfo3.getOwnerProgramClass());
            }

            if (!hashMap.containsKey(methodInfo3) && (!methodInfo3.isManufactured() || methodInfo3.isStaticInitializer() && methodInfo3.isFlowObfuscationMember())
            ) {
                AbstractMethodInfo abstractMethodInfo1 = methodOverrideAnalyzer.findRootMethod(methodInfo3);
                if (abstractMethodInfo1 != null) {
                    Long long4 = (Long) hashMap.get(abstractMethodInfo1);
                    if (long4 != null) {
                        hashMap.put(methodInfo3, long4);
                        continue;
                    }
                }

                Long long3;
                if (HiddenOptionFlags.FIXED_PARAMETER_KEY) {
                    long3 = 255L;
                } else {
                    boolean bl1 = false;

                    do {
                        long bb = nextRandomKey();
                        long3 = bb;
                    } while (!hashSet.add(long3));

                    byte bd = 2;
                }

                hashMap.put(methodInfo3, long3);
                if (abstractMethodInfo1 != null && abstractMethodInfo1.isProgramMember()) {
                    hashMap.put((MethodInfo) abstractMethodInfo1, long3);
                }
            }
        }

        iterator2 = hashSet1.iterator();

        while (iterator2.hasNext()) {
            ProgramClass programClass1 = (ProgramClass) iterator2.next();

            for (MethodInfo methodInfo4 : programClass1.getMethodInfos()) {
                Long long5 = (Long) hashMap.get(methodInfo4);
                if (long5 != null) {
                    MethodSignature methodSignature1 = methodInfo4.getSignature();
                    Iterator iterator1 = programClass1.getVersionedVariants().iterator();

                    while (iterator1.hasNext()) {
                        ClassFileBase classFileBase = (ClassFileBase) iterator1.next();
                        MethodInfo methodInfo2 = (MethodInfo) classFileBase.findMethod(methodSignature1);
                        if (methodInfo2 != null) {
                            hashMap.put(methodInfo2, long5);
                        }
                    }
                }
            }

            HashMap hashMap1 = ZkmUtils.createHashMap();
            Iterator iterator3 = programClass1.getVersionedVariants().iterator();

            while (iterator3.hasNext()) {
                ClassFileBase classFileBase1 = (ClassFileBase) iterator3.next();

                for (AbstractMethodInfo abstractMethodInfo2 : classFileBase1.getDeclaredMethods()) {
                    if (!hashMap.containsKey(abstractMethodInfo2)) {
                        MethodSignature methodSignature2 = abstractMethodInfo2.getSignature();
                        Long long6 = (Long) hashMap1.get(methodSignature2);
                        if (long6 != null) {
                            hashMap.put((MethodInfo) abstractMethodInfo2, long6);
                        } else {
                            long6 = nextRandomKey();
                            hashMap.put((MethodInfo) abstractMethodInfo2, long6);
                            hashMap1.put(methodSignature2, long6);
                        }
                    }
                }
            }
        }

        return hashMap;
    }

    public void generateKeyInfrastructure(
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            Set set1,
            Set set2,
            Set set3,
            ObservableHolder observableHolder,
            Map map1,
            SetValuedMap setValuedMap,
            String string,
            EnumerableMap enumerableMap,
            EnumerableMap enumerableMap1,
            EnumerableMap enumerableMap2,
            Map map2,
            ObservableHolder observableHolder1
    ) throws ZkmException, IOException {
        this.initNodes = map2;
        HashSet hashSet = ZkmUtils.createHashSet(listMultimap.getKeyCount());
        Iterator iterator = listMultimap.keySet().iterator();

        while (iterator.hasNext()) {
            ProgramClass programClass1 = (ProgramClass) iterator.next();
            if (programClass1.isProgramClass()) {
                hashSet.add(programClass1);
            }
        }

        iterator = listMultimap1.keySet().iterator();

        while (iterator.hasNext()) {
            ProgramClass programClass6 = (ProgramClass) iterator.next();
            if (programClass6.isProgramClass()) {
                hashSet.add(programClass6);
            }
        }

        hashSet.addAll(set1);
        hashSet.addAll(set2);
        hashSet.addAll(set3);
        int ba = hashSet.size();
        int keyCount = this.classesByArchive.getKeyCount();
        ListMultimap listMultimap2 = new ListMultimap(keyCount);
        Iterator iterator1 = this.classesByArchive.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry entry = (Entry) iterator1.next();
            SourceArchive sourceArchive1 = (SourceArchive) entry.getKey();
            List list1 = (List) entry.getValue();
            Iterator iterator2 = list1.iterator();

            while (iterator2.hasNext()) {
                ProgramClass programClass2 = (ProgramClass) iterator2.next();
                if (hashSet.contains(programClass2)) {
                    listMultimap2.addValue(sourceArchive1, programClass2);
                }
            }
        }

        ReadOnlyMultiMapView readOnlyMultiMapView = new ReadOnlyMultiMapView(listMultimap2);
        int bc = readOnlyMultiMapView.getKeyCount();
        SetMultiMap setMultiMap = new SetMultiMap(bc);
        SetMultiMap setMultiMap1 = new SetMultiMap(bc);
        LookupClassFactory.filterModuleArchives(readOnlyMultiMapView, setMultiMap, setMultiMap1);
        ObservableHolder observableHolder4 = new ObservableHolder();
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(bc));
        HashMap hashMap1 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        List list2 = this.assignHelperClasses(
                hashSet, readOnlyMultiMapView, setMultiMap, setMultiMap1, observableHolder4, hashMap, hashMap1, string, enumerableMap, enumerableMap1, enumerableMap2
        );
        ProgramClass[] programClass3 = null;
        HashMap hashMap2 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(list2.size()));
        Iterator iterator3 = list2.iterator();

        while (iterator3.hasNext()) {
            ObjectTriple objectTriple = (ObjectTriple) iterator3.next();
            LinkedHashSet linkedHashSet = new LinkedHashSet(13);
            linkedHashSet.add(objectTriple.getFirst());
            linkedHashSet.add(objectTriple.getSecond());
            linkedHashSet.add(objectTriple.getThird());
            ObservableHolder observableHolder2 = new ObservableHolder();
            HashMap hashMap3 = ZkmUtils.createHashMap();
            HashMap hashMap4 = ZkmUtils.createHashMap();
            SyncIndexedSet syncIndexedSet = new SyncIndexedSet(3);
            HashMap hashMap5 = ZkmUtils.createHashMap();
            ObservableHolder observableHolder3 = new ObservableHolder();
            HashMap hashMap6 = ZkmUtils.createHashMap();
            HelperClassTriple helperClassTriple = new HelperClassTriple(objectTriple);
            programClass3 = LookupClassFactory.createLookupClasses(
                    linkedHashSet,
                    hashMap5,
                    observableHolder3,
                    observableHolder2,
                    hashMap3,
                    hashMap4,
                    syncIndexedSet,
                    hashMap6,
                    this.classMajorVersion,
                    this.classMinorVersion,
                    this,
                    this.classRepository,
                    helperClassTriple
            );
            if (HiddenOptionFlags.METHOD_PARAMETER_MAP_CLASS != null) {
                HashMap hashMap7 = ZkmUtils.createHashMap(13);
                String string1 = HiddenOptionFlags.METHOD_PARAMETER_MAP_CLASS.replace('.', '/');
                Iterator iterator8;
                if (((ProgramClass) syncIndexedSet.iterator().next()).supportsJava5()) {
                    hashMap7.put("java/util/concurrent/ConcurrentHashMap", string1);
                    iterator8 = syncIndexedSet.iterator();
                } else {
                    hashMap7.put("java/util/Hashtable", string1);
                    iterator8 = syncIndexedSet.iterator();
                }

                Iterator iterator4 = iterator8;

                while (iterator4.hasNext()) {
                    ProgramClass programClass5 = (ProgramClass) iterator4.next();
                    programClass5.remapClassNames(
                            1, 1, hashMap7, ZkmUtils.createHashSet(), ZkmUtils.createHashSet(), ZkmUtils.createHashMap(), (MessageReporter) null
                    );
                }
            }

            ObjectTriple objectTriple3 = new ObjectTriple(syncIndexedSet.getElementAt(0), syncIndexedSet.getElementAt(1), syncIndexedSet.getElementAt(2));
            hashMap2.put(objectTriple, objectTriple3);
        }

        observableHolder1.setValue(programClass3);
        if (!observableHolder4.isValueNull()) {
            observableHolder.setValue(hashMap2.get(observableHolder4.getValue()));
        }

        iterator3 = hashMap.entrySet().iterator();

        while (iterator3.hasNext()) {
            Entry entry1 = (Entry) iterator3.next();
            ObjectTriple objectTriple1 = (ObjectTriple) hashMap2.get(entry1.getValue());
            map1.put(entry1.getKey(), objectTriple1);
        }

        iterator3 = hashMap1.entrySet().iterator();

        while (iterator3.hasNext()) {
            Entry entry2 = (Entry) iterator3.next();
            ObjectTriple objectTriple2 = (ObjectTriple) hashMap2.get(entry2.getValue());
            ProgramClass programClass4 = (ProgramClass) entry2.getKey();
            this.classHelperTriples.put(entry2.getKey(), objectTriple2);

            for (int i = 0; i < 3; i += 1) {
                ProgramClass programClass7 = (ProgramClass) objectTriple2.getAt(i);
                setValuedMap.addValue(programClass7, programClass4);
                if (!programClass4.isFromArchive() && programClass7.isFromArchive()) {
                    programClass7.markAsFromArchive();
                }
            }
        }

        HashSet hashSet1 = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(hashMap2.size() * 3));
        Iterator iterator5 = hashMap2.entrySet().iterator();

        while (iterator5.hasNext()) {
            Entry entry3 = (Entry) iterator5.next();
            hashSet1.add(entry3.getValue());
        }

        if (this.scriptEnvironment.isVerbose()) {
            PrintWriter printWriter = this.scriptEnvironment.getLogWriter();
            ListMultimap listMultimap3 = new ListMultimap(hashMap2.size() * 3, map1.size());
            Iterator iterator6 = map1.entrySet().iterator();

            while (iterator6.hasNext()) {
                Entry entry4 = (Entry) iterator6.next();
                SourceArchive sourceArchive2 = (SourceArchive) entry4.getKey();
                listMultimap3.addValue(entry4.getValue(), sourceArchive2);
            }

            String string2 = LookupClassFactory.describeLookupClassTriples("Method Parameter List Changing", listMultimap3, hashSet1);
            printWriter.print(string2);
        }

        if (HiddenOptionFlags.DEBUG_PARAMETER_CHANGE_LOG) {
        }

        this.createInitNodes(listMultimap, listMultimap1, set1, set2, set3);
        List list3 = this.buildInitNodeHierarchy();
        ArrayList arrayList = new ArrayList(this.initNodes.size());
        Iterator iterator7 = this.initNodes.entrySet().iterator();

        while (iterator7.hasNext()) {
            Entry entry5 = (Entry) iterator7.next();
            arrayList.add(entry5.getValue());
        }

        int be = ParameterKeyGenerator.computeKeyCount(arrayList.size(), new MutableInt());
        ArrayList arrayList1 = new ArrayList(be / 2);
        ArrayList arrayList2 = new ArrayList(be);
        ObservableHolder observableHolder5 = new ObservableHolder();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList(be);
        ArrayList arrayList5 = new ArrayList(be);
        if (HiddenOptionFlags.DEBUG_PARAMETER_CHANGE_LOG) {
        }

        this.nodePairKeys = new ArrayList();
        ObservableHolder observableHolder6 = new ObservableHolder();
        ParameterKeyGenerator parameterKeyGenerator = new ParameterKeyGenerator(
                list3,
                arrayList,
                arrayList1,
                arrayList2,
                observableHolder5,
                arrayList3,
                arrayList4,
                arrayList5,
                this.nodePairKeys,
                observableHolder6,
                randomKeyIterator,
                sharedRandom,
                JavaRuntimeVersion.isAtLeastJava7(this.classMajorVersion)
        );
        this.finalBitLayout = (int[]) observableHolder6.getValue();
        int indexBitOffset = parameterKeyGenerator.getIndexBitOffset();
        int mergeSortDepthLimit = parameterKeyGenerator.getMergeSortDepthLimit();
        int layoutGroupSize = parameterKeyGenerator.getLayoutGroupSize();
        this.generateHelperClasses(
                hashSet1,
                indexBitOffset,
                mergeSortDepthLimit,
                layoutGroupSize,
                arrayList1,
                arrayList2,
                observableHolder5,
                arrayList3,
                arrayList4,
                arrayList5,
                this.classRepository,
                this.classRepository.getClassResolver()
        );
    }

    public List createLinkMethods(List list1, ProgramClass programClass1, List list2, int ba, int bb, ClassMemberLookup classMemberLookup1) throws ZkmProcessingException {
        ArrayList arrayList = new ArrayList(list1.size());
        int bc = 0;
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            GeneratedMethodChunk generatedMethodChunk = (GeneratedMethodChunk) iterator.next();
            String string = "d" + bc++;
            ArrayList arrayList1 = generatedMethodChunk.getInstructions();
            LocalVariableList localVariableList1 = generatedMethodChunk.getLocalVariables();
            ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
            Integer integer = 5;
            ClassMemberLookup classMemberLookup2 = classMemberLookup1;
            MethodInfo methodInfo1 = programClass1.createMethod(
                    string,
                    "()V",
                    arrayList1,
                    ba,
                    bb,
                    1,
                    false,
                    localVariableList1,
                    exceptionHandlerSpecs,
                    "MethodParameterListChanging",
                    list2,
                    (InheritedMemberAnalyzer) null,
                    classMemberLookup2,
                    integer
            );
            arrayList.add(methodInfo1);
        }

        return arrayList;
    }

    public int[] getFinalBitLayout() {
        return this.finalBitLayout;
    }

    public boolean hasHelperTriples() {
        return this.classHelperTriples != null && !this.classHelperTriples.isEmpty();
    }

    public static int[] createBitPermutation(Random random1) {
        ArrayList arrayList = new ArrayList();
        int ba = 0;
        int bh = 0;

        for (byte bi = 64; bh < bi; bi = 64) {
            arrayList.add(INTEGER_CACHE.valueOf(ba));
            bh = ++ba;
        }

        ZkmUtils.shuffleList(arrayList, random1);
        int[] bf = new int[64];
        int bb = 0;
        bh = 0;

        for (byte bg = 64; bh < bg; bg = 64) {
            int bc = (Integer) arrayList.get(bb);
            int bd = (Integer) arrayList.get(bb + 1);
            int be = bc - bd;
            bf[bc] = be;
            bf[bd] = -be;
            bb++;
            bh = ++bb;
        }

        return bf;
    }

    public boolean isMainMethod(AbstractMethodInfo abstractMethodInfo) {
        MethodSignature methodSignature1 = abstractMethodInfo.getSignature();
        return abstractMethodInfo.isPublic() && abstractMethodInfo.isStatic() && methodSignature1.equals(MainMethodConstants.MAIN_METHOD_SIGNATURE);
    }

    public static boolean isNotOverridable(AbstractMethodInfo abstractMethodInfo, MethodOverrideAnalyzer methodOverrideAnalyzer) {
        boolean bl;
        if (!abstractMethodInfo.isStrictlyPrivate()) {
            if (!abstractMethodInfo.isStatic()) {
                if (!abstractMethodInfo.isConstructor()) {
                    if (methodOverrideAnalyzer.hasRootMethod(abstractMethodInfo) || methodOverrideAnalyzer.hasOverridingMethods(abstractMethodInfo)) {
                        return false;
                    }

                    bl = HiddenOptionFlags.CHANGE_OVERRIDDEN_METHOD_PARAMETERS;
                } else {
                    bl = HiddenOptionFlags.CHANGE_OVERRIDDEN_METHOD_PARAMETERS;
                }
            } else {
                bl = HiddenOptionFlags.CHANGE_OVERRIDDEN_METHOD_PARAMETERS;
            }
        } else {
            bl = HiddenOptionFlags.CHANGE_OVERRIDDEN_METHOD_PARAMETERS;
        }

        return !bl;
    }

    public void buildRegisterMethodCode(
            ProgramClass programClass1,
            ProgramClass programClass2,
            LocalVariableList localVariableList1,
            ArrayList arrayList,
            ConstantPool constantPool1,
            List list1,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            boolean bl
    ) throws ZkmException, IOException {
        mutableInt.setValue(1);
        mutableInt1.setValue(4);
        String string;
        String string1;
        if (bl) {
            string = "java/util/ArrayList";
            string1 = "Ljava/util/ArrayList;";
        } else {
            string = "java/util/Vector";
            string1 = "Ljava/util/Vector;";
        }

        FieldInfo fieldInfo = (FieldInfo) programClass1.findField("f", string1);
        ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef(fieldInfo, list1);
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(string, "size", "()I", list1, classMemberLookup1, classResolver1);
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant));
        FieldInfo fieldInfo1 = (FieldInfo) programClass1.findField("i", "I");
        ResolvedFieldRef resolvedFieldRef1 = constantPool1.getOrCreateFieldRef(fieldInfo1, list1);
        arrayList.add(new ConstantRefInstruction(179, resolvedFieldRef1));
        MethodInfo methodInfo1 = programClass1.findMethodBySignature(new MethodSignature("c", "()V"));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list1);
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant1));
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 5));
        MethodInfo methodInfo2 = programClass2.findMethodBySignature(new MethodSignature("d", "()V"));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrCreateMethodRefConstant(methodInfo2, list1);
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
    }

    public List createNodeCreationMethods(List list1, ProgramClass programClass1, List list2, ClassMemberLookup classMemberLookup1) throws ZkmProcessingException {
        ArrayList arrayList = new ArrayList(list1.size());
        int ba = 0;

        for (int i = 0; i < list1.size(); i++) {
            GeneratedCodeBlock generatedCodeBlock = (GeneratedCodeBlock) list1.get(i);
            String string = "a" + ba++;
            ArrayList arrayList1 = generatedCodeBlock.getInstructions();
            int localCount = generatedCodeBlock.getLocalCount();
            LocalVariableList localVariableList1 = generatedCodeBlock.getLocalVariables();
            ExceptionHandlerSpec[] exceptionHandlerSpecs = generatedCodeBlock.getExceptionHandlers();
            Integer integer = 5;
            ClassMemberLookup classMemberLookup2 = classMemberLookup1;
            MethodInfo methodInfo1 = programClass1.createStaticMethod(
                    string,
                    "()V",
                    arrayList1,
                    4,
                    localCount,
                    1,
                    localVariableList1,
                    exceptionHandlerSpecs,
                    "MethodParameterListChanging",
                    list2,
                    (InheritedMemberAnalyzer) null,
                    classMemberLookup2,
                    integer
            );
            arrayList.add(methodInfo1);
        }

        return arrayList;
    }

    public List assignHelperClasses(final Set set, final ReadOnlyMultiMapView readOnlyMultiMapView, final SetMultiMap setMultiMap, final SetMultiMap setMultiMap2, final ObservableHolder observableHolder, final Map map, final Map map2, final String s, final EnumerableMap enumerableMap, final EnumerableMap enumerableMap2, final EnumerableMap enumerableMap3) throws ZkmException, IOException {
        final Map map3 = map2;
        final Set set2 = set;
        final Map map4 = map;
        final SetMultiMap setMultiMap3 = setMultiMap2;
        final ReadOnlyMultiMapView readOnlyMultiMapView2 = readOnlyMultiMapView;
        final SetMultiMap setMultiMap4 = setMultiMap;
        final EnumerableMap enumerableMap4 = enumerableMap2;
        final String s2 = s;
        final ObservableHolder observableHolder2 = observableHolder;
        final EnumerableMap enumerableMap5 = enumerableMap3;
        final EnumerableMap enumerableMap6 = enumerableMap;
        final ArrayList list = new ArrayList();
        final int size = map4.size();
        final HashSet hashSet = ZkmUtils.createHashSet();
        final HashSet hashSet2 = ZkmUtils.createHashSet();
        final HashSet hashSet3 = ZkmUtils.createHashSet();
        final Set partitionClassesByModule = LookupClassFactory.partitionClassesByModule(set2, hashSet2, hashSet3, hashSet);
        final int size2 = hashSet2.size();
        Map hashMap = ZkmUtils.createHashMap();
        final HashSet hashSet4 = ZkmUtils.createHashSet();
        if (this.changeLogMapping != null) {
            final ObjectTriple methodParameterChangeClasses = this.changeLogMapping.getMethodParameterChangeClasses();
            if (methodParameterChangeClasses != null) {
                final ObjectTriple resolveChangeLogHelperNames = this.resolveChangeLogHelperNames(methodParameterChangeClasses, enumerableMap5, hashSet, hashMap);
                if (resolveChangeLogHelperNames != null) {
                    observableHolder2.setValue(resolveChangeLogHelperNames);
                    hashSet4.add(resolveChangeLogHelperNames.getFirst());
                    hashSet4.add(resolveChangeLogHelperNames.getSecond());
                    hashSet4.add(resolveChangeLogHelperNames.getThird());
                    final Iterator iterator = hashSet3.iterator();
                    while (iterator.hasNext()) {
                        map3.put(iterator.next(), resolveChangeLogHelperNames);
                    }
                }
            }
            for (final SourceArchive sourceArchive : (java.lang.Iterable<SourceArchive>) (Object) ((this.classesByArchive.keySet()))) {
                final String moduleName = sourceArchive.getModuleName();
                final ObjectTriple moduleMethodParameterChangeClasses = this.changeLogMapping.getModuleMethodParameterChangeClasses(moduleName);
                Label_0660:
                {
                    ObjectTriple resolveChangeLogHelperNames2 = null;
                    Label_0446:
                    {
                        try {
                            if (moduleMethodParameterChangeClasses == null) {
                                continue;
                            }
                            final ReadOnlyMultiMapView readOnlyMultiMapView3 = readOnlyMultiMapView2;
                            final SourceArchive sourceArchive2 = sourceArchive;
                            final ReadOnlyMultiMapView readOnlyMultiMapView4 = readOnlyMultiMapView3;
                            final SourceArchive sourceArchive3 = sourceArchive2;
                            final boolean b = readOnlyMultiMapView4.containsKey(sourceArchive3);
                            if (b) {
                                break Label_0446;
                            }
                            break Label_0660;
                        } catch (final ZkmRuntimeException ex) {
                            throw ex;
                        }

                    }
                    final ObjectTriple objectTriple = resolveChangeLogHelperNames2;
                    try {
                        if (objectTriple == null) {
                            continue;
                        }
                        map4.put(sourceArchive, objectTriple);
                    } catch (final ZkmRuntimeException ex3) {
                        throw ex3;
                    }
                    hashSet4.add(objectTriple.getFirst());
                    hashSet4.add(objectTriple.getSecond());
                    hashSet4.add(objectTriple.getThird());
                    final List values = readOnlyMultiMapView2.getValues(sourceArchive);
                    if (values != null) {
                        final Iterator iterator3 = values.iterator();
                        while (iterator3.hasNext()) {
                            map3.put(iterator3.next(), objectTriple);
                        }
                    }
                    try {
                        continue;

                    } catch (final ZkmRuntimeException ex4) {
                        throw ex4;
                    }
                }
            }
            final int n = size2;
            Label_0839:
            {
                Label_0749:
                {
                    try {
                        if (n != 0) {
                            final ObservableHolder observableHolder3 = observableHolder2;
                            final boolean b2 = observableHolder3.isValueNull();
                            break Label_0749;
                        }
                        break Label_0749;
                    } catch (final ZkmRuntimeException ex5) {
                        throw ex5;
                    }

                }
                final int n2 = size;
                try {
                    if (n2 != map4.size()) {
                        break Label_0839;
                    }
                } catch (final ZkmRuntimeException ex7) {
                    throw ex7;
                }
                for (final Map.Entry entry : (java.lang.Iterable<Map.Entry>) (Object) ((hashMap.entrySet()))) {
                    try {
                        list.add(entry.getValue());
                        continue;
                    } catch (final ZkmRuntimeException ex8) {
                        throw ex8;
                    }

                }
                return list;
            }
            if (size2 > 0) {
                final String autoReflectionClass = this.changeLogMapping.getAutoReflectionClass();
                ChangeLogMapping changeLogMapping = null;
                Label_0904:
                {
                    Label_0897:
                    {
                        try {
                            if (autoReflectionClass == null) {
                                break Label_0897;
                            }
                            hashSet4.add(ZkmUtils.dotsToSlashes(autoReflectionClass));
                        } catch (final ZkmRuntimeException ex9) {
                            throw ex9;
                        }
                        changeLogMapping = this.changeLogMapping;
                        break Label_0904;
                    }
                    changeLogMapping = this.changeLogMapping;
                }
                final String referenceObfuscationClass = changeLogMapping.getReferenceObfuscationClass();
                try {
                    if (referenceObfuscationClass != null) {
                        hashSet4.add(ZkmUtils.dotsToSlashes(referenceObfuscationClass));
                    }
                } catch (final ZkmRuntimeException ex10) {
                    throw ex10;
                }
            }
            final Iterator iterator5 = readOnlyMultiMapView2.keySet().iterator();
            while (iterator5.hasNext()) {
                final String moduleName2 = ((SourceArchive) iterator5.next()).getModuleName();
                final String moduleAutoReflectionClass = this.changeLogMapping.getModuleAutoReflectionClass(moduleName2);
                ChangeLogMapping changeLogMapping2 = null;
                Label_1057:
                {
                    Label_1050:
                    {
                        try {
                            if (moduleAutoReflectionClass == null) {
                                break Label_1050;
                            }
                            hashSet4.add(ZkmUtils.dotsToSlashes(moduleAutoReflectionClass));
                        } catch (final ZkmRuntimeException ex11) {
                            throw ex11;
                        }
                        changeLogMapping2 = this.changeLogMapping;
                        break Label_1057;
                    }
                    changeLogMapping2 = this.changeLogMapping;
                }
                final String moduleReferenceObfuscationClass = changeLogMapping2.getModuleReferenceObfuscationClass(moduleName2);
                try {
                    if (moduleReferenceObfuscationClass == null) {
                        continue;
                    }
                    hashSet4.add(ZkmUtils.dotsToSlashes(moduleReferenceObfuscationClass));
                } catch (final ZkmRuntimeException ex12) {
                    throw ex12;
                }
            }
        }
        final ObservableHolder observableHolder4 = new ObservableHolder();
        final HashMap hashMap2 = ZkmUtils.createHashMap();
        final HashMap hashMap3 = ZkmUtils.createHashMap();
        for (int i = 0; i < 3; ++i) {
            String s3 = null;
            ObjectTriple objectTriple3 = null;
            Label_1168:
            {
                Label_1152:
                {
                    ObservableHolder observableHolder5;
                    try {
                        observableHolder5 = observableHolder2;
                        final boolean b3 = observableHolder5.isValueNull();
                        if (b3) {
                            break Label_1152;
                        }
                        break Label_1152;
                    } catch (final ZkmRuntimeException ex13) {
                        throw ex13;
                    }

                }
                objectTriple3 = (ObjectTriple) observableHolder2.getValue();
            }
            final ObjectTriple objectTriple4 = objectTriple3;
            Label_1214:
            {
                Object at;
                try {
                    if (objectTriple4 == null) {
                        break Label_1214;
                    }
                    at = objectTriple4.getAt(i);
                } catch (final ZkmRuntimeException ex15) {
                    throw ex15;
                }
                s3 = (String) at;
            }
            observableHolder4.clearValue();
            hashMap2.clear();
            for (final Map.Entry entry2 : (java.lang.Iterable<Map.Entry>) (Object) ((map4.entrySet()))) {
                final String s4 = (String) ((ObjectTriple) entry2.getValue()).getAt(i);
                try {
                    if (s4 == null) {
                        continue;
                    }
                    hashMap2.put(entry2.getKey(), s4);
                } catch (final ZkmRuntimeException ex16) {
                    throw ex16;
                }
            }
            hashMap3.clear();
            for (final Map.Entry entry3 : (java.lang.Iterable<Map.Entry>) (Object) ((map3.entrySet()))) {
                final String s5 = (String) ((ObjectTriple) entry3.getValue()).getAt(i);
                try {
                    if (s5 == null) {
                        continue;
                    }
                    hashMap3.put(entry3.getKey(), s5);
                } catch (final ZkmRuntimeException ex17) {
                    throw ex17;
                }
            }
            final Set set3 = partitionClassesByModule;
            final String s6 = s3;
            final ObservableHolder observableHolder6 = observableHolder4;
            final HashMap hashMap4 = hashMap2;
            final HashMap hashMap5 = hashMap3;
            final String s7 = s2;
            final HashSet set4 = hashSet2;
            final HashSet set5 = hashSet3;
            final ReadOnlyMultiMapView readOnlyMultiMapView5 = readOnlyMultiMapView2;
            final SetMultiMap setMultiMap5 = setMultiMap4;
            final SetMultiMap setMultiMap6 = setMultiMap3;
            final EnumerableMap enumerableMap7 = enumerableMap6;
            final EnumerableMap enumerableMap8 = enumerableMap4;
            final EnumerableMap enumerableMap9 = enumerableMap5;
            final HashSet set6 = hashSet4;
            final String lookupClassNamePrefix = this.lookupClassNamePrefix;
            final MixedCaseNamesMode mixedCaseNamesMode = this.mixedCaseNamesMode;
            final boolean avoidSimpleNameClash = this.avoidSimpleNameClash;
            final boolean randomizeNames = this.randomizeNames;
            final List nameDictionary = this.nameDictionary;
            final ClasspathClassLoader classpathClassLoader = this.classpathClassLoader;
            final ScriptEnvironment scriptEnvironment = this.scriptEnvironment;
            final ClasspathClassLoader classpathClassLoader2 = classpathClassLoader;
            final List list2 = nameDictionary;
            final Boolean value = randomizeNames;
            final Boolean value2 = avoidSimpleNameClash;
            final MixedCaseNamesMode mixedCaseNamesMode2 = mixedCaseNamesMode;
            final String s8 = lookupClassNamePrefix;
            final HashSet set7 = set6;
            final EnumerableMap enumerableMap10 = enumerableMap9;
            final EnumerableMap enumerableMap11 = enumerableMap8;
            final EnumerableMap enumerableMap12 = enumerableMap7;
            final SetMultiMap setMultiMap7 = setMultiMap6;
            final SetMultiMap setMultiMap8 = setMultiMap5;
            final ReadOnlyMultiMapView readOnlyMultiMapView6 = readOnlyMultiMapView5;
            final HashSet set8 = set5;
            final HashSet set9 = set4;
            final String s9 = s7;
            final HashMap hashMap6 = hashMap5;
            final HashMap hashMap7 = hashMap4;
            final ObservableHolder observableHolder7 = observableHolder6;
            final String s10 = s6;
            final Set set10 = set3;
            final String s11 = s10;
            final ObservableHolder observableHolder8 = observableHolder7;
            final Map map5 = hashMap7;
            final Map map6 = hashMap6;
            final String s12 = "methodParameterChangesPackage";
            final String s13 = s9;
            final Set set11 = set9;
            final Set set12 = set8;
            final ReadOnlyMultiMapView readOnlyMultiMapView7 = readOnlyMultiMapView6;
            final SetMultiMap setMultiMap9 = setMultiMap8;
            final SetMultiMap setMultiMap10 = setMultiMap7;
            final EnumerableMap enumerableMap13 = enumerableMap12;
            final EnumerableMap enumerableMap14 = enumerableMap11;
            final EnumerableMap enumerableMap15 = enumerableMap10;
            final Set set13 = set7;
            final String s14 = s8;
            final MixedCaseNamesMode mixedCaseNamesMode3 = mixedCaseNamesMode2;
            LookupClassFactory.assignLookupClassNames(set10, s11, observableHolder8, map5, map6, s12, s13, set11, set12, readOnlyMultiMapView7, setMultiMap9, setMultiMap10, enumerableMap13, enumerableMap14, enumerableMap15, set13, s14, value2, value, list2, classpathClassLoader2, scriptEnvironment);
            boolean empty = false;
            Label_2148:
            {
                Object value3 = null;
                Label_1980:
                {
                    boolean valueNull;
                    try {
                        valueNull = observableHolder4.isValueNull();
                        if (!valueNull) {
                            break Label_1980;
                        }
                        break Label_2148;
                    } catch (final ZkmRuntimeException ex18) {
                        throw ex18;
                    }

                }
                final String s15 = (String) value3;
                final ObjectTriple objectTriple5 = (ObjectTriple) observableHolder2.getValue();
                if (objectTriple5 == null) {
                    Object value4 = hashMap.get(s15);
                    if (value4 == null) {
                        value4 = new ObjectTriple(s15, null, null);
                        hashMap.put(s15, value4);
                    }
                    observableHolder2.setValue(value4);
                } else {
                    Object at2;
                    try {
                        at2 = objectTriple5.getAt(i);
                    } catch (final ZkmRuntimeException ex20) {
                        throw ex20;
                    }
                    try {
                        if (at2 == null) {
                            objectTriple5.setAt(s15, i);
                        }
                    } catch (final ZkmRuntimeException ex21) {
                        throw ex21;
                    }
                }
                try {
                    empty = hashMap2.isEmpty();
                } catch (final ZkmRuntimeException ex22) {
                    throw ex22;
                }
            }
            if (!empty) {
                for (final Map.Entry entry4 : (java.lang.Iterable<Map.Entry>) (Object) ((hashMap2.entrySet()))) {
                    final SourceArchive sourceArchive4 = (SourceArchive) entry4.getKey();
                    final String s16 = (String) entry4.getValue();
                    final ObjectTriple objectTriple6 = ((com.zelix.klassmaster.util.ObjectTriple) (map4.get(sourceArchive4)));
                    if (objectTriple6 == null) {
                        Object o = hashMap.get(s16);
                        if (o == null) {
                            o = new ObjectTriple(s16, null, null);
                            hashMap.put(s16, o);
                        }
                        map4.put(sourceArchive4, o);
                    } else {
                        Object at3;
                        try {
                            at3 = objectTriple6.getAt(i);
                        } catch (final ZkmRuntimeException ex23) {
                            throw ex23;
                        }
                        try {
                            if (at3 != null) {
                                continue;
                            }
                            objectTriple6.setAt(s16, i);
                        } catch (final ZkmRuntimeException ex24) {
                            throw ex24;
                        }
                    }
                }
            }
            for (final Map.Entry entry5 : (java.lang.Iterable<Map.Entry>) (Object) ((hashMap3.entrySet()))) {
                final ProgramClass programClass = (ProgramClass) entry5.getKey();
                final String s17 = (String) entry5.getValue();
                if (map3.get(programClass) == null) {
                    map3.put(programClass, hashMap.get(s17));
                }
            }
            if (i == 0) {
                for (final Map.Entry entry6 : (java.lang.Iterable<Map.Entry>) (Object) ((hashMap.entrySet()))) {
                    try {
                        list.add(entry6.getValue());
                        continue;
                    } catch (final ZkmRuntimeException ex25) {
                        throw ex25;
                    }

                }
                hashMap = new EnumerableMap(hashMap);
            }
        }
        return list;
    }

    public boolean canKeyStringEncryption(MethodInfo methodInfo1) {
        if (methodInfo1 == null) {
            return false;
        }

        ProgramClass programClass1 = methodInfo1.getOwnerProgramClass();
        return (programClass1.supportsJava8() || programClass1.supportsJava4() && !programClass1.isInterface())
                && this.hasInitNode(methodInfo1)
                && this.stringEncryptionClasses.contains(programClass1)
                && HiddenOptionFlags.STRING_ENCRYPT_DES
                && ZkmUtils.isClassAvailable();
    }

    public List buildKeyLookupCode(
            ProgramClass programClass1,
            Long long1,
            Long long2,
            Long long3,
            MutableInt mutableInt,
            List list1,
            ConstantPool constantPool1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        ObjectTriple objectTriple = this.getHelperTriple(programClass1);
        int ba = 0;
        ObjectTriple objectTriple1 = this.getHelperMethods(objectTriple);
        MethodInfo methodInfo1 = (MethodInfo) objectTriple1.getFirst();
        MethodInfo methodInfo2 = (MethodInfo) objectTriple1.getSecond();
        MethodInfo methodInfo3 = (MethodInfo) objectTriple1.getThird();
        if (HiddenOptionFlags.SYNCHRONIZE_PARAMETER_LOOKUP) {
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list1);
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
            arrayList.add(new MonitorEnterInstruction());
            ba += 4;
        }

        ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrCreateMethodRefConstant(methodInfo2, list1);
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = constantPool1.getOrCreateInterfaceMethodRef(methodInfo3, list1);
        arrayList.add(Instruction.createLongConstantLoad(long1, constantPool1, list1));
        arrayList.add(Instruction.createLongConstantLoad(long2, constantPool1, list1));
        ba += 6;
        if (programClass1.supportsJava7() && !HiddenOptionFlags.NO_METHOD_HANDLES_LOOKUP) {
            ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                    "java/lang/invoke/MethodHandles", "lookup", "()Ljava/lang/invoke/MethodHandles$Lookup;", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant1));
            ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                    "java/lang/invoke/MethodHandles$Lookup", "lookupClass", "()Ljava/lang/Class;", list1, classMemberLookup1, classResolver1
            );
            arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
            ba += 6;
        } else {
            arrayList.add(SimpleInstruction.forOpcode(1));
            ba++;
        }

        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant3));
        arrayList.add(Instruction.createLongConstantLoad(long3, constantPool1, list1));
        arrayList.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef));
        ba += 11;
        if (HiddenOptionFlags.SYNCHRONIZE_PARAMETER_LOOKUP) {
            ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list1);
            arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant4));
            arrayList.add(new MonitorExitInstruction());
            ba += 4;
        }

        mutableInt.setValue(ba);
        return arrayList;
    }

    public List createLayoutMethods(List list1, ProgramClass programClass1, List list2, int ba, int bb, ClassMemberLookup classMemberLookup1) throws ZkmProcessingException {
        ArrayList arrayList = new ArrayList(list1.size());
        int bc = 0;
        Iterator iterator = list1.iterator();

        while (iterator.hasNext()) {
            GeneratedMethodChunk generatedMethodChunk = (GeneratedMethodChunk) iterator.next();
            String string = "c" + bc++;
            ArrayList arrayList1 = generatedMethodChunk.getInstructions();
            LocalVariableList localVariableList1 = generatedMethodChunk.getLocalVariables();
            ExceptionHandlerSpec[] exceptionHandlerSpecs = new ExceptionHandlerSpec[0];
            Integer integer = 5;
            ClassMemberLookup classMemberLookup2 = classMemberLookup1;
            MethodInfo methodInfo1 = programClass1.createMethod(
                    string,
                    "()V",
                    arrayList1,
                    ba,
                    bb,
                    1,
                    false,
                    localVariableList1,
                    exceptionHandlerSpecs,
                    "MethodParameterListChanging",
                    list2,
                    (InheritedMemberAnalyzer) null,
                    classMemberLookup2,
                    integer
            );
            arrayList.add(methodInfo1);
        }

        return arrayList;
    }

    public static long packKey(int ba, long bb, int bc) {
        long bd = 0L | (long) ba << 56;
        bd |= (bb & 281474976710655L) << 8;
        return bd | bc;
    }

    public Long assignMethodKey(MethodInfo methodInfo1) {
        ProgramClass programClass1 = methodInfo1.getOwnerProgramClass();
        Long long1 = null;
        if (this.classKeys != null) {
            long1 = (Long) this.classKeys.get(programClass1);
        }

        Map map1;
        if (long1 == null) {
            long1 = !HiddenOptionFlags.FIXED_PARAMETER_KEY ? nextRandomKey() : 255L;
            map1 = this.methodKeys;
        } else {
            map1 = this.methodKeys;
        }

        map1.put(methodInfo1, long1);
        return long1;
    }

    public String xorString(String string, char[] ba) {
        char[] bb = string.toCharArray();

        for (int i = 0; i < bb.length; i++) {
            bb[i] ^= ba[i];
        }

        return new String(bb);
    }

    public void linkSubclassNodes(ClassHierarchyNode classHierarchyNode, Map map1) throws ZkmException, IOException {
        ProgramClass programClass1 = (ProgramClass) classHierarchyNode.getClassFile();
        MethodParamChangeNode methodParamChangeNode = (MethodParamChangeNode) map1.get(programClass1);
        if (!classHierarchyNode.isInterface()) {
            List list1 = classHierarchyNode.getSubclassNodes();
            if (list1 != null) {
                if (methodParamChangeNode != null) {
                    Set set1 = this.staticInitCalleeAnalyzer.getDependentSubclasses(methodParamChangeNode.getInitMethod());
                    Iterator iterator = list1.iterator();

                    while (iterator.hasNext()) {
                        ClassHierarchyNode classHierarchyNode1 = (ClassHierarchyNode) iterator.next();
                        ProgramClass programClass2 = (ProgramClass) classHierarchyNode1.getClassFile();
                        if (!set1.contains(programClass2)) {
                            ClassFileBase classFileBase = classHierarchyNode1.getClassFile();
                            MethodParamChangeNode methodParamChangeNode1 = (MethodParamChangeNode) map1.get(classFileBase);
                            if (methodParamChangeNode1 != null) {
                                methodParamChangeNode.addDependent(methodParamChangeNode1);
                            }
                        }
                    }
                }

                Iterator iterator1 = list1.iterator();

                while (iterator1.hasNext()) {
                    ClassHierarchyNode classHierarchyNode2 = (ClassHierarchyNode) iterator1.next();
                    this.linkSubclassNodes(classHierarchyNode2, map1);
                }
            }
        }
    }

    public void addEncodedStringCase(
            String string, int ba, Object object, LabelInstruction labelInstruction, List list1, char[] bb, ConstantPool constantPool1, List list2
    ) {
        list1.add(Instruction.createIntPush(ba));
        String string1 = this.xorString(string, bb);
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant(string1, list2, false);
        list1.add(new ConstantRefInstruction(19, resolvedStringConstant));
        list1.add(new GotoInstruction(labelInstruction));
        list1.add(object);
    }

    public LocalVariableIndex emitKeyLocalInit(
            Long long1, LocalVariableAllocator localVariableAllocator, List list1, MethodInfo methodInfo1, Map map1, ConstantPool constantPool1, List list2
    ) {
        ArrayList arrayList = new ArrayList();
        MethodParamChangeNode methodParamChangeNode = null;
        if (this.initNodes != null) {
            methodParamChangeNode = (MethodParamChangeNode) this.initNodes.get(methodInfo1);
        }

        int bc;
        if (methodParamChangeNode != null) {
            ResolvedFieldRef resolvedFieldRef = methodParamChangeNode.getKeyFieldRef();
            if (resolvedFieldRef != null) {
                arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
            }

            long ba = long1 ^ methodParamChangeNode.getEffectiveKey();
            arrayList.add(Instruction.createLongConstantLoad(ba, constantPool1, list2));
            arrayList.add(SimpleInstruction.forOpcode(131));
            bc = localVariableAllocator.getValue();
        } else {
            arrayList.add(Instruction.createLongConstantLoad(long1, constantPool1, list2));
            bc = localVariableAllocator.getValue();
        }

        int bb = bc;
        localVariableAllocator.addAndGet(2);
        LocalVariableList localVariableList1 = methodInfo1.getLocalVariableList();
        arrayList.add(Instruction.createLongStore(bb, localVariableList1, 5));
        LocalVariableSlot localVariableSlot1 = localVariableList1.getSlotAt(bb);
        list1.addAll(arrayList);
        map1.put(methodInfo1, localVariableSlot1);
        return localVariableSlot1;
    }

    public static long applyBitPermutation(long ba, int[] bb) {
        return permuteBits(ba, bb);
    }

    public static String getMapTypeDescriptor(boolean bl) {
        String string;
        if (HiddenOptionFlags.METHOD_PARAMETER_MAP_CLASS != null) {
            string = "L" + HiddenOptionFlags.METHOD_PARAMETER_MAP_CLASS.replace('.', '/') + ";";
        } else if (bl) {
            string = "Ljava/util/concurrent/ConcurrentHashMap;";
        } else {
            string = "Ljava/util/Hashtable;";
        }

        return string;
    }

    public void putInitNode(Object object, Object object1) {
        if (this.initNodes != null) {
            this.initNodes.put(object, object1);
        }
    }

    public ObjectTriple getHelperMethods(ObjectTriple objectTriple) {
        if (!this.helperMethodTriples.containsKey(objectTriple)) {
            this.cacheHelperMethods(objectTriple);
        }

        return (ObjectTriple) this.helperMethodTriples.get(objectTriple);
    }

    public static long nextRandomKey() {
        return nextRandomLong();
    }

    public void buildKeyTableClass(
            ProgramClass programClass1,
            ProgramClass programClass2,
            int ba,
            int bb,
            int bc,
            List list1,
            ObservableHolder observableHolder,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ArrayList arrayList2 = new ArrayList();
        MethodInfo methodInfo1 = programClass1.getOrCreateStaticInitializer(arrayList2, classMemberLookup1, "Method Parameter List Changing", 5);
        boolean bl = programClass1.supportsJava5();
        ConstantPool constantPool1 = programClass1.getClassConstantPool();
        List list2 = this.buildNodeCreationBlocks(list1, programClass1, constantPool1, arrayList2, classMemberLookup1, classResolver1, bl);
        List list3 = this.createNodeCreationMethods(list2, programClass1, arrayList2, classMemberLookup1);
        LocalVariableList localVariableList1 = methodInfo1.getLocalVariableList();
        ArrayList arrayList3 = new ArrayList();
        MutableInt mutableInt = new MutableInt();
        MutableInt mutableInt1 = new MutableInt();
        ObservableHolder observableHolder1 = new ObservableHolder();
        this.buildHelperStaticInit(
                programClass1,
                localVariableList1,
                list3,
                ba,
                bb,
                bc,
                arrayList3,
                constantPool1,
                arrayList2,
                mutableInt,
                mutableInt1,
                observableHolder1,
                classMemberLookup1,
                classResolver1,
                bl
        );
        String string1 = "(L" + programClass2.getClassName() + ";)V";
        MethodInfo methodInfo2 = programClass1.findMethodBySignature(new MethodSignature("a", string1));
        ArrayList arrayList4 = new ArrayList();
        MutableInt mutableInt2 = new MutableInt();
        MutableInt mutableInt3 = new MutableInt();
        LocalVariableList localVariableList2 = methodInfo2.getLocalVariableList();
        this.buildRegisterMethodCode(
                programClass1,
                programClass2,
                localVariableList2,
                arrayList4,
                constantPool1,
                arrayList2,
                mutableInt2,
                mutableInt3,
                classMemberLookup1,
                classResolver1,
                bl
        );
        String string2 = "(L" + programClass2.getClassName() + ";)V";
        MethodInfo methodInfo3 = programClass1.findMethodBySignature(new MethodSignature("b", string2));
        LocalVariableList localVariableList3 = methodInfo3.getLocalVariableList();
        ArrayList arrayList5 = new ArrayList();
        MutableInt mutableInt4 = new MutableInt();
        MutableInt mutableInt5 = new MutableInt();
        this.buildLayoutInitCode(
                programClass1, programClass2, localVariableList3, arrayList5, observableHolder, constantPool1, arrayList2, mutableInt2, mutableInt3
        );
        programClass1.addPoolConstants(arrayList2);
        int value = mutableInt.getValue();
        int bg = mutableInt1.getValue();
        ArrayList arrayList = new ArrayList();
        String string = "Method Parameter List Changing";
        int bd = bg;
        int be = value;
        ArrayList arrayList1 = arrayList3;
        methodInfo1.insertInstructions(arrayList1, be, bd, 0, string, arrayList);
        if (!observableHolder1.isValueNull()) {
            methodInfo1.setExceptionHandlers((ExceptionHandlerSpec[]) observableHolder1.getValue());
        }

        methodInfo2.insertInstructions(arrayList4, mutableInt2.getValue(), mutableInt3.getValue(), 0, "Method Parameter List Changing", new ArrayList());
        value = mutableInt4.getValue();
        bg = mutableInt5.getValue();
        arrayList = new ArrayList();
        string = "Method Parameter List Changing";
        bd = bg;
        be = value;
        arrayList1 = arrayList5;
        methodInfo3.insertInstructions(arrayList1, be, bd, 0, string, arrayList);
    }

    public void buildLookupClass(
            ProgramClass programClass1,
            ProgramClass programClass2,
            ProgramClass programClass3,
            List list1,
            List list2,
            List list3,
            List list4,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        ArrayList arrayList2 = new ArrayList();
        MethodInfo methodInfo1 = programClass1.getOrCreateStaticInitializer(arrayList2, classMemberLookup1, "Method Parameter List Changing", 5);
        boolean bl = programClass1.supportsJava5();
        ConstantPool constantPool1 = programClass1.getClassConstantPool();
        ObservableHolder observableHolder = new ObservableHolder();
        ObservableHolder observableHolder1 = new ObservableHolder();
        List list5 = this.buildKeyMapChunks(
                list1, programClass1, programClass2, programClass3, constantPool1, arrayList2, observableHolder1, classMemberLookup1, classResolver1, bl
        );
        List list6 = this.createMapInitMethods(list5, programClass1, arrayList2, classMemberLookup1);
        MutableInt mutableInt = new MutableInt();
        MutableInt mutableInt1 = new MutableInt();
        MethodInfo methodInfo2 = programClass1.findMethodBySignature(MethodSignature.DEFAULT_CONSTRUCTOR);
        LocalVariableList localVariableList1 = methodInfo2.getLocalVariableList();
        ArrayList arrayList3 = new ArrayList();
        this.buildHelperConstructorCode(
                programClass1,
                programClass3,
                localVariableList1,
                list6,
                arrayList3,
                constantPool1,
                arrayList2,
                mutableInt,
                mutableInt1,
                observableHolder,
                classMemberLookup1,
                classResolver1,
                bl
        );
        MutableInt mutableInt2 = new MutableInt();
        MutableInt mutableInt3 = new MutableInt();
        ArrayList arrayList4 = new ArrayList();
        this.buildSingletonInit(programClass1, arrayList4, constantPool1, arrayList2, classMemberLookup1, classResolver1);
        MutableInt mutableInt4 = new MutableInt();
        MutableInt mutableInt5 = new MutableInt();
        MethodInfo methodInfo3 = programClass1.findMethodBySignature(new MethodSignature("a", "(J)J"));
        LocalVariableList localVariableList2 = methodInfo3.getLocalVariableList();
        CodeInsertion codeInsertion = this.buildKeyDecodeCode(
                programClass1,
                programClass2,
                programClass3,
                localVariableList2,
                constantPool1,
                arrayList2,
                mutableInt5,
                mutableInt4,
                classMemberLookup1,
                classResolver1
        );
        MutableInt mutableInt6 = new MutableInt();
        MutableInt mutableInt7 = new MutableInt();
        MethodInfo methodInfo4 = programClass1.findMethodBySignature(new MethodSignature("b", "()[I"));
        methodInfo4.getLocalVariableList();
        CodeInsertion codeInsertion1 = this.buildLayoutGetterCode(programClass3, constantPool1, arrayList2, mutableInt6, mutableInt7);
        MutableInt mutableInt8 = new MutableInt();
        MutableInt mutableInt9 = new MutableInt();
        MethodInfo methodInfo5 = programClass1.findMethodBySignature(new MethodSignature("d", "()V"));
        List list7 = this.buildLayoutChunks(list2, programClass2, programClass3, constantPool1, arrayList2, mutableInt8, mutableInt9, observableHolder1);
        List list8 = this.createLayoutMethods(list7, programClass1, arrayList2, mutableInt9.getValue(), mutableInt8.getValue(), classMemberLookup1);
        LocalVariableList localVariableList3 = methodInfo5.getLocalVariableList();
        ArrayList arrayList5 = new ArrayList();
        this.buildLinkInvokerCode(programClass1, programClass3, localVariableList3, list8, arrayList5, constantPool1, arrayList2);
        MutableInt mutableInt10 = new MutableInt();
        MutableInt mutableInt11 = new MutableInt();
        MethodInfo methodInfo6 = programClass1.findMethodBySignature(new MethodSignature("c", "()V"));
        List list9 = this.buildLinkChunks(list3, list4, programClass2, programClass3, constantPool1, arrayList2, observableHolder1, mutableInt10, mutableInt11);
        List list10 = this.createLinkMethods(list9, programClass1, arrayList2, mutableInt11.getValue(), mutableInt10.getValue(), classMemberLookup1);
        LocalVariableList localVariableList4 = methodInfo6.getLocalVariableList();
        ArrayList arrayList6 = new ArrayList();
        this.buildChunkInvokerCode(localVariableList4, list10, arrayList6, constantPool1, arrayList2);
        programClass1.addPoolConstants(arrayList2);
        int value = mutableInt.getValue();
        int bg = mutableInt1.getValue();
        ArrayList arrayList = new ArrayList();
        String string = "Method Parameter List Changing";
        int bb = bg;
        int bc = value;
        ArrayList arrayList1 = arrayList3;
        methodInfo2.insertInstructions(arrayList1, bc, bb, 2, string, arrayList);
        if (!observableHolder.isValueNull()) {
            methodInfo2.setExceptionHandlers((ExceptionHandlerSpec[]) observableHolder.getValue());
        }

        value = mutableInt2.getValue();
        bg = mutableInt3.getValue();
        arrayList = new ArrayList();
        string = "Method Parameter List Changing";
        byte ba = 0;
        bb = bg;
        bc = value;
        arrayList1 = arrayList4;
        methodInfo1.insertInstructions(arrayList1, bc, bb, ba, string, arrayList);
        if (!observableHolder.isValueNull()) {
            methodInfo1.setExceptionHandlers((ExceptionHandlerSpec[]) observableHolder.getValue());
        }

        methodInfo3.applyCodeInsertion(codeInsertion, mutableInt5.getValue(), mutableInt4.getValue());
        methodInfo4.applyCodeInsertion(codeInsertion1, mutableInt7.getValue(), mutableInt6.getValue());
        methodInfo5.insertInstructions(arrayList5, 1, 1, 0, "Method Parameter List Changing", new ArrayList());
        arrayList = new ArrayList();
        string = "Method Parameter List Changing";
        ba = 0;
        byte bd = 1;
        byte be = 1;
        arrayList1 = arrayList6;
        methodInfo6.insertInstructions(arrayList1, be, bd, ba, string, arrayList);
    }

    public static void reshufflePermutation(int[] ba, Random random1) {
        ArrayList arrayList = new ArrayList(56);
        int bb = 8;
        int bg = 8;

        for (byte bh = 64; bg < bh; bh = 64) {
            if (bb - ba[bb] > 7) {
                arrayList.add(INTEGER_CACHE.valueOf(bb));
            }

            bg = ++bb;
        }

        ZkmUtils.shuffleList(arrayList, random1);
        bb = arrayList.size();

        for (int i = 0; i < bb; i++) {
            int bd = (Integer) arrayList.get(i);
            int be = (Integer) arrayList.get(i + 1);
            int bf = bd - be;
            ba[bd] = bf;
            ba[be] = -bf;
            i++;
        }
    }

    public boolean canKeyIntegerEncryption(MethodInfo methodInfo1) {
        if (methodInfo1 == null) {
            return false;
        }

        ProgramClass programClass1 = methodInfo1.getOwnerProgramClass();
        return (programClass1.supportsJava8() || programClass1.supportsJava4() && !programClass1.isInterface())
                && this.hasInitNode(methodInfo1)
                && this.integerEncryptionClasses.contains(programClass1)
                && HiddenOptionFlags.INTEGER_ENCRYPT_DES
                && ZkmUtils.isClassAvailable();
    }

    public int buildRuntimeCheckCode(
            long[] ba,
            String string,
            ProgramClass programClass1,
            ResolvedFieldRef resolvedFieldRef,
            GeneratedCodeBlock generatedCodeBlock,
            ConstantPool constantPool1,
            List list1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        char[] bb = new char[ba.length];

        for (int i = 0; i < ba.length; i++) {
            bb[i] = (char) (ba[i] & 127L);
        }

        ArrayList arrayList1 = new ArrayList();
        ArrayList arrayList = new ArrayList();
        this.xorString("java.lang.management.RuntimeMXBean", bb);
        LabelInstruction labelInstruction = new LabelInstruction(true, 384);
        LabelInstruction labelInstruction1 = new LabelInstruction(true, 640);
        LabelInstruction labelInstruction2 = new LabelInstruction(true, 1152);
        LabelInstruction labelInstruction3 = new LabelInstruction(true, 1);
        ResolvedClassConstant resolvedClassConstant = constantPool1.getOrCreateClassConstant("java/lang/Exception", list1);
        arrayList.add(new ExceptionHandlerSpec(resolvedClassConstant, labelInstruction, labelInstruction1, labelInstruction2));
        LabelInstruction labelInstruction4 = new LabelInstruction(true, 384);
        LabelInstruction labelInstruction5 = new LabelInstruction(true, 640);
        LabelInstruction labelInstruction6 = new LabelInstruction(true, 1152);
        LabelInstruction labelInstruction7 = new LabelInstruction(true, 1);
        arrayList.add(new ExceptionHandlerSpec(resolvedClassConstant, labelInstruction4, labelInstruction5, labelInstruction6));
        LabelInstruction labelInstruction8 = new LabelInstruction(true, 384);
        LabelInstruction labelInstruction9 = new LabelInstruction(true, 640);
        LabelInstruction labelInstruction10 = new LabelInstruction(true, 1152);
        arrayList.add(new ExceptionHandlerSpec(resolvedClassConstant, labelInstruction8, labelInstruction9, labelInstruction10));
        LabelInstruction labelInstruction11 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction12 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction13 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction14 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction15 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction16 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction17 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction18 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction19 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction20 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction21 = new LabelInstruction(true, 1);
        LabelInstruction[] labelInstructions = new LabelInstruction[]{
                labelInstruction13,
                labelInstruction14,
                labelInstruction15,
                labelInstruction16,
                labelInstruction17,
                labelInstruction18,
                labelInstruction19,
                labelInstruction20
        };
        LabelInstruction labelInstruction22 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction23 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction24 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction25 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction26 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction27 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction28 = new LabelInstruction(true, 1);
        LabelInstruction labelInstruction29 = new LabelInstruction(true, 1);
        int bd = generatedCodeBlock.allocateLocal();
        int be = generatedCodeBlock.allocateLocal();
        int bf = generatedCodeBlock.allocateLocal();
        int bg = generatedCodeBlock.allocateLocal();
        int bh = generatedCodeBlock.allocateLocal();
        int bi = generatedCodeBlock.allocateLocal();
        int bj = generatedCodeBlock.allocateLocal();
        int bk = generatedCodeBlock.allocateLocal();
        int bl = generatedCodeBlock.allocateLocal();
        int bm = generatedCodeBlock.allocateLocal();
        int bn = generatedCodeBlock.allocateLocal();
        LocalVariableList localVariableList1 = generatedCodeBlock.getLocalVariables();
        arrayList1.add(SimpleInstruction.forOpcode(1));
        arrayList1.add(Instruction.createObjectStore(bd, localVariableList1, 5));
        arrayList1.add(labelInstruction);
        arrayList1.add(SimpleInstruction.forOpcode(1));
        arrayList1.add(Instruction.createObjectStore(bh, localVariableList1, 5));
        arrayList1.add(SimpleInstruction.forOpcode(1));
        arrayList1.add(Instruction.createObjectStore(bg, localVariableList1, 5));
        arrayList1.add(SimpleInstruction.forOpcode(1));
        arrayList1.add(Instruction.createObjectStore(bi, localVariableList1, 5));
        arrayList1.add(SimpleInstruction.forOpcode(1));
        arrayList1.add(Instruction.createObjectStore(bj, localVariableList1, 5));
        arrayList1.add(SimpleInstruction.forOpcode(1));
        arrayList1.add(Instruction.createObjectStore(bk, localVariableList1, 5));
        arrayList1.add(SimpleInstruction.forOpcode(1));
        arrayList1.add(Instruction.createObjectStore(bl, localVariableList1, 5));
        arrayList1.add(SimpleInstruction.forOpcode(1));
        arrayList1.add(Instruction.createObjectStore(bm, localVariableList1, 5));
        arrayList1.add(SimpleInstruction.forOpcode(1));
        arrayList1.add(Instruction.createObjectStore(bn, localVariableList1, 5));
        this.addEncodedStringCase("java.lang.management.RuntimeMXBean", 0, labelInstruction13, labelInstruction11, arrayList1, bb, constantPool1, list1);
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", list1, classMemberLookup1, classResolver1
        );
        arrayList1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        arrayList1.add(Instruction.createObjectStore(bd, localVariableList1, 5));
        arrayList1.add(labelInstruction1);
        arrayList1.add(new GotoInstruction(labelInstruction3));
        arrayList1.add(labelInstruction2);
        arrayList1.add(SimpleInstruction.forOpcode(87));
        arrayList1.add(new GotoInstruction(labelInstruction12));
        arrayList1.add(labelInstruction3);
        arrayList1.add(Instruction.createObjectLoad(bd, localVariableList1, 5));
        arrayList1.add(new BranchInstruction(198, labelInstruction12));
        arrayList1.add(labelInstruction4);
        this.addEncodedStringCase("getPlatformMXBeans", 1, labelInstruction14, labelInstruction11, arrayList1, bb, constantPool1, list1);
        arrayList1.add(Instruction.createObjectStore(bh, localVariableList1, 5));
        this.addEncodedStringCase("java.lang.management.ManagementFactory", 2, labelInstruction15, labelInstruction11, arrayList1, bb, constantPool1, list1);
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", list1, classMemberLookup1, classResolver1
        );
        arrayList1.add(new ConstantRefInstruction(184, resolvedMethodRefConstant1));
        arrayList1.add(Instruction.createObjectLoad(bh, localVariableList1, 5));
        arrayList1.add(SimpleInstruction.forOpcode(4));
        ResolvedClassConstant resolvedClassConstant1 = constantPool1.getOrCreateClassConstant("java/lang/Class", list1);
        arrayList1.add(new ConstantRefInstruction(189, resolvedClassConstant1));
        arrayList1.add(SimpleInstruction.forOpcode(89));
        arrayList1.add(SimpleInstruction.forOpcode(3));
        arrayList1.add(new ConstantRefInstruction(19, resolvedClassConstant1));
        arrayList1.add(SimpleInstruction.forOpcode(83));
        ResolvedMethodRefConstant resolvedMethodRefConstant2 = constantPool1.getOrAddMethodRef(
                "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", list1, classMemberLookup1, classResolver1
        );
        arrayList1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
        arrayList1.add(SimpleInstruction.forOpcode(1));
        arrayList1.add(SimpleInstruction.forOpcode(4));
        ResolvedClassConstant resolvedClassConstant2 = constantPool1.getOrCreateClassConstant("java/lang/Object", list1);
        arrayList1.add(new ConstantRefInstruction(189, resolvedClassConstant2));
        arrayList1.add(SimpleInstruction.forOpcode(89));
        arrayList1.add(SimpleInstruction.forOpcode(3));
        arrayList1.add(Instruction.createObjectLoad(bd, localVariableList1, 5));
        arrayList1.add(SimpleInstruction.forOpcode(83));
        ResolvedMethodRefConstant resolvedMethodRefConstant3 = constantPool1.getOrAddMethodRef(
                "java/lang/reflect/Method", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", list1, classMemberLookup1, classResolver1
        );
        arrayList1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant3));
        ResolvedClassConstant resolvedClassConstant3 = constantPool1.getOrCreateClassConstant("java/util/List", list1);
        arrayList1.add(new ConstantRefInstruction(192, resolvedClassConstant3));
        arrayList1.add(SimpleInstruction.forOpcode(3));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = constantPool1.getOrAddInterfaceMethodRef(
                "java/util/List", "get", "(I)Ljava/lang/Object;", list1, classMemberLookup1, classResolver1
        );
        arrayList1.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef));
        arrayList1.add(Instruction.createObjectStore(bg, localVariableList1, 5));
        arrayList1.add(labelInstruction5);
        arrayList1.add(new GotoInstruction(labelInstruction7));
        arrayList1.add(labelInstruction6);
        arrayList1.add(SimpleInstruction.forOpcode(87));
        arrayList1.add(new GotoInstruction(labelInstruction12));
        arrayList1.add(labelInstruction7);
        arrayList1.add(Instruction.createObjectLoad(bg, localVariableList1, 5));
        arrayList1.add(new BranchInstruction(198, labelInstruction12));
        arrayList1.add(labelInstruction8);
        this.addEncodedStringCase("getInputArguments", 3, labelInstruction16, labelInstruction11, arrayList1, bb, constantPool1, list1);
        arrayList1.add(Instruction.createObjectStore(bi, localVariableList1, 5));
        this.addEncodedStringCase("-Xdebug", 4, labelInstruction17, labelInstruction11, arrayList1, bb, constantPool1, list1);
        arrayList1.add(Instruction.createObjectStore(bj, localVariableList1, 5));
        this.addEncodedStringCase("-Xrunjdwp", 5, labelInstruction18, labelInstruction11, arrayList1, bb, constantPool1, list1);
        arrayList1.add(Instruction.createObjectStore(bk, localVariableList1, 5));
        this.addEncodedStringCase("-agentlib:jdwp", 6, labelInstruction19, labelInstruction11, arrayList1, bb, constantPool1, list1);
        arrayList1.add(Instruction.createObjectStore(bl, localVariableList1, 5));
        this.addEncodedStringCase("-agentpath:", 7, labelInstruction20, labelInstruction11, arrayList1, bb, constantPool1, list1);
        arrayList1.add(Instruction.createObjectStore(bm, localVariableList1, 5));
        this.addEncodedStringCase("-javaagent:", 8, labelInstruction21, labelInstruction11, arrayList1, bb, constantPool1, list1);
        arrayList1.add(Instruction.createObjectStore(bn, localVariableList1, 5));
        arrayList1.add(Instruction.createObjectLoad(bd, localVariableList1, 5));
        arrayList1.add(Instruction.createObjectLoad(bi, localVariableList1, 5));
        arrayList1.add(SimpleInstruction.forOpcode(3));
        arrayList1.add(new ConstantRefInstruction(189, resolvedClassConstant1));
        arrayList1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant2));
        arrayList1.add(Instruction.createObjectLoad(bg, localVariableList1, 5));
        arrayList1.add(SimpleInstruction.forOpcode(3));
        arrayList1.add(new ConstantRefInstruction(189, resolvedClassConstant2));
        arrayList1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant3));
        ResolvedClassConstant resolvedClassConstant4 = constantPool1.getOrCreateClassConstant("java/util/Collection", list1);
        arrayList1.add(new ConstantRefInstruction(192, resolvedClassConstant4));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef1 = constantPool1.getOrAddInterfaceMethodRef(
                "java/util/Collection", "iterator", "()Ljava/util/Iterator;", list1, classMemberLookup1, classResolver1
        );
        arrayList1.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef1));
        arrayList1.add(labelInstruction24);
        arrayList1.add(SimpleInstruction.forOpcode(89));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef2 = constantPool1.getOrAddInterfaceMethodRef(
                "java/util/Iterator", "hasNext", "()Z", list1, classMemberLookup1, classResolver1
        );
        arrayList1.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef2));
        arrayList1.add(new BranchInstruction(153, labelInstruction28));
        arrayList1.add(SimpleInstruction.forOpcode(89));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef3 = constantPool1.getOrAddInterfaceMethodRef(
                "java/util/Iterator", "next", "()Ljava/lang/Object;", list1, classMemberLookup1, classResolver1
        );
        arrayList1.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef3));
        ResolvedClassConstant resolvedClassConstant5 = constantPool1.getOrCreateClassConstant("java/lang/String", list1);
        arrayList1.add(new ConstantRefInstruction(192, resolvedClassConstant5));
        arrayList1.add(SimpleInstruction.forOpcode(89));
        arrayList1.add(Instruction.createObjectLoad(bj, localVariableList1, 5));
        ResolvedMethodRefConstant resolvedMethodRefConstant4 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "equals", "(Ljava/lang/Object;)Z", list1, classMemberLookup1, classResolver1
        );
        arrayList1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant4));
        arrayList1.add(new BranchInstruction(154, labelInstruction29));
        arrayList1.add(SimpleInstruction.forOpcode(89));
        arrayList1.add(Instruction.createObjectLoad(bk, localVariableList1, 5));
        ResolvedMethodRefConstant resolvedMethodRefConstant5 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", list1, classMemberLookup1, classResolver1
        );
        arrayList1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant5));
        arrayList1.add(new BranchInstruction(154, labelInstruction29));
        arrayList1.add(SimpleInstruction.forOpcode(89));
        arrayList1.add(Instruction.createObjectLoad(bl, localVariableList1, 5));
        arrayList1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant5));
        arrayList1.add(new BranchInstruction(154, labelInstruction29));
        arrayList1.add(SimpleInstruction.forOpcode(89));
        arrayList1.add(Instruction.createObjectLoad(bm, localVariableList1, 5));
        arrayList1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant5));
        arrayList1.add(new BranchInstruction(154, labelInstruction29));
        arrayList1.add(SimpleInstruction.forOpcode(89));
        arrayList1.add(Instruction.createObjectLoad(bn, localVariableList1, 5));
        arrayList1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant5));
        arrayList1.add(new BranchInstruction(153, labelInstruction27));
        arrayList1.add(labelInstruction29);
        arrayList1.add(SimpleInstruction.forOpcode(87));
        FieldInfo fieldInfo = (FieldInfo) programClass1.findField("n", "I");
        ResolvedFieldRef resolvedFieldRef1 = constantPool1.getOrCreateFieldRef(fieldInfo, list1);
        arrayList1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        arrayList1.add(Instruction.createIntPush(1));
        arrayList1.add(new BranchInstruction(164, labelInstruction26));
        arrayList1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        arrayList1.add(Instruction.createIntPush(17));
        arrayList1.add(new BranchInstruction(159, labelInstruction25));
        arrayList1.add(new ConstantRefInstruction(178, resolvedFieldRef1));
        arrayList1.add(SimpleInstruction.forOpcode(4));
        arrayList1.add(SimpleInstruction.forOpcode(100));
        arrayList1.add(new ConstantRefInstruction(179, resolvedFieldRef1));
        arrayList1.add(new GotoInstruction(labelInstruction28));
        arrayList1.add(labelInstruction25);
        arrayList1.add(Instruction.createIntPush(10));
        arrayList1.add(new ConstantRefInstruction(179, resolvedFieldRef1));
        arrayList1.add(new GotoInstruction(labelInstruction28));
        arrayList1.add(labelInstruction26);
        arrayList1.add(Instruction.createIntPush(17));
        arrayList1.add(new ConstantRefInstruction(179, resolvedFieldRef1));
        arrayList1.add(new GotoInstruction(labelInstruction28));
        arrayList1.add(labelInstruction27);
        arrayList1.add(SimpleInstruction.forOpcode(87));
        arrayList1.add(new GotoInstruction(labelInstruction24));
        arrayList1.add(labelInstruction28);
        arrayList1.add(SimpleInstruction.forOpcode(87));
        arrayList1.add(labelInstruction9);
        arrayList1.add(new GotoInstruction(labelInstruction12));
        arrayList1.add(labelInstruction10);
        arrayList1.add(SimpleInstruction.forOpcode(87));
        arrayList1.add(new GotoInstruction(labelInstruction12));
        arrayList1.add(labelInstruction11);
        ResolvedMethodRefConstant resolvedMethodRefConstant6 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "toCharArray", "()[C", list1, classMemberLookup1, classResolver1
        );
        arrayList1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant6));
        arrayList1.add(Instruction.createObjectStore(be, localVariableList1, 5));
        arrayList1.add(SimpleInstruction.forOpcode(3));
        arrayList1.add(Instruction.createIntStore(bf, localVariableList1, 5));
        arrayList1.add(labelInstruction22);
        arrayList1.add(Instruction.createIntLoad(bf, localVariableList1, 5));
        arrayList1.add(Instruction.createObjectLoad(be, localVariableList1, 5));
        arrayList1.add(SimpleInstruction.forOpcode(190));
        arrayList1.add(new BranchInstruction(162, labelInstruction23));
        arrayList1.add(Instruction.createObjectLoad(be, localVariableList1, 5));
        arrayList1.add(Instruction.createIntLoad(bf, localVariableList1, 5));
        arrayList1.add(SimpleInstruction.forOpcode(92));
        arrayList1.add(SimpleInstruction.forOpcode(52));
        arrayList1.add(new ConstantRefInstruction(178, resolvedFieldRef));
        ResolvedMethodRefConstant resolvedMethodRefConstant7 = constantPool1.getOrAddMethodRef(
                "java/util/ArrayList", "get", "(I)Ljava/lang/Object;", list1, classMemberLookup1, classResolver1
        );
        arrayList1.add(Instruction.createIntLoad(bf, localVariableList1, 5));
        arrayList1.add(new ConstantRefInstruction(182, resolvedMethodRefConstant7));
        ResolvedClassConstant resolvedClassConstant6 = constantPool1.getOrCreateClassConstant(string, list1);
        arrayList1.add(new ConstantRefInstruction(192, resolvedClassConstant6));
        arrayList1.add(SimpleInstruction.forOpcode(3));
        arrayList1.add(Instruction.createIntPush(6));
        MethodInfo methodInfo1 = programClass1.findMethodBySignature(new MethodSignature("a", "(II)J"));
        ResolvedMethodRefConstant resolvedMethodRefConstant8 = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list1);
        arrayList1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant8));
        arrayList1.add(Instruction.createLongConstantLoad(127L, constantPool1, list1));
        arrayList1.add(SimpleInstruction.forOpcode(127));
        arrayList1.add(SimpleInstruction.forOpcode(136));
        arrayList1.add(SimpleInstruction.forOpcode(146));
        arrayList1.add(SimpleInstruction.forOpcode(130));
        arrayList1.add(SimpleInstruction.forOpcode(146));
        arrayList1.add(SimpleInstruction.forOpcode(85));
        arrayList1.add(Instruction.createIntIncrement(bf, 1, localVariableList1, 5));
        arrayList1.add(new GotoInstruction(labelInstruction22));
        arrayList1.add(labelInstruction23);
        arrayList1.add(new TypeInstruction(resolvedClassConstant5));
        arrayList1.add(SimpleInstruction.forOpcode(89));
        arrayList1.add(Instruction.createObjectLoad(be, localVariableList1, 5));
        ResolvedMethodRefConstant resolvedMethodRefConstant9 = constantPool1.getOrAddMethodRef(
                "java/lang/String", "<init>", "([C)V", list1, classMemberLookup1, classResolver1
        );
        arrayList1.add(new ConstantRefInstruction(183, resolvedMethodRefConstant9));
        arrayList1.add(SimpleInstruction.forOpcode(95));
        arrayList1.add(new TableSwitchInstruction(labelInstruction21, 7, labelInstructions));
        arrayList1.add(labelInstruction12);
        generatedCodeBlock.setExceptionHandlers(((com.zelix.klassmaster.classfile.insn.ExceptionHandlerSpec[]) (arrayList.toArray(new ExceptionHandlerSpec[arrayList.size()]))));
        int bo = 0;
        Iterator iterator = arrayList1.iterator();

        while (iterator.hasNext()) {
            Instruction instruction1 = (Instruction) iterator.next();
            bo += instruction1.getLength();
        }

        generatedCodeBlock.getInstructions().addAll(arrayList1);
        return bo;
    }

    public static String getMapClassName(boolean bl) {
        String string;
        if (HiddenOptionFlags.METHOD_PARAMETER_MAP_CLASS != null) {
            string = HiddenOptionFlags.METHOD_PARAMETER_MAP_CLASS.replace('.', '/');
        } else if (bl) {
            string = "java/util/concurrent/ConcurrentHashMap";
        } else {
            string = "java/util/Hashtable";
        }

        return string;
    }

    public void addDependencyChecked(
            ClassHierarchyNode classHierarchyNode, ClassHierarchyNode classHierarchyNode1, boolean bl, Map map1, String string, boolean bl1
    ) {
        ClassFileBase classFileBase = classHierarchyNode.getClassFile();
        if (bl && this.flowObfuscatedClasses != null && this.flowObfuscatedClasses.contains(classFileBase)) {
            if (bl1) {
                this.scriptEnvironment
                        .logError("Class '" + classFileBase.getOriginalDottedName() + "' specified as a false dominant class but it is used in Flow Obfuscation.");
            }
        } else {
            MethodParamChangeNode methodParamChangeNode = (MethodParamChangeNode) map1.get(classFileBase);
            if (methodParamChangeNode != null) {
                ClassFileBase classFileBase1 = classHierarchyNode1.getClassFile();
                if (!bl && this.flowObfuscatedClasses != null && this.flowObfuscatedClasses.contains(classFileBase1)) {
                    if (bl1) {
                        this.scriptEnvironment
                                .logError(
                                        "Class '"
                                                + classFileBase1.getOriginalDottedName()
                                                + "' specified as dependent upon initialization of "
                                                + classFileBase.getOriginalDottedName()
                                                + "' but '"
                                                + classFileBase1.getOriginalDottedName()
                                                + "' is used in Flow Obfuscation."
                                );
                    }

                    return;
                }

                MethodParamChangeNode methodParamChangeNode1 = (MethodParamChangeNode) map1.get(classFileBase1);
                if (methodParamChangeNode1 != null) {
                    boolean bl2 = false;
                    boolean bl3 = false;
                    if (!methodParamChangeNode1.hasParent() && !methodParamChangeNode1.hasFalseParent()) {
                        if (bl) {
                            methodParamChangeNode.addFalseDependent(methodParamChangeNode1);
                        } else {
                            methodParamChangeNode.addDependent(methodParamChangeNode1);
                        }

                        bl2 = true;
                        if (string != null && this.scriptEnvironment.isVerbose()) {
                            this.scriptEnvironment
                                    .logLine(
                                            "Setting initialization of class '"
                                                    + classFileBase1.getOriginalDottedName()
                                                    + " as"
                                                    + (bl ? " FALSELY" : "")
                                                    + " dependent upon initialization of '"
                                                    + classFileBase.getOriginalDottedName()
                                                    + "' because '"
                                                    + string
                                                    + "'"
                                    );
                        }
                    } else {
                        bl3 = true;
                        if (bl1) {
                            if (methodParamChangeNode1.hasParent()) {
                                this.scriptEnvironment
                                        .logError(
                                                "Class '"
                                                        + classFileBase1.getOriginalDottedName()
                                                        + "' specified as a"
                                                        + (bl ? " FALSE" : "")
                                                        + " dependent on '"
                                                        + classFileBase.getOriginalDottedName()
                                                        + "' but the relationship could not be established because '"
                                                        + classFileBase1.getOriginalDottedName()
                                                        + "' is already dependent upon class '"
                                                        + methodParamChangeNode1.getParent().getInitMethod().getOriginalDottedName()
                                                        + "'."
                                        );
                            } else {
                                this.scriptEnvironment
                                        .logError(
                                                "Class '"
                                                        + classFileBase1.getOriginalDottedName()
                                                        + "' specified as a"
                                                        + (bl ? " FALSE" : "")
                                                        + " dependent on '"
                                                        + classFileBase.getOriginalDottedName()
                                                        + "' but the relationship could not be established because '"
                                                        + classFileBase1.getOriginalDottedName()
                                                        + "' is already dependent upon class '"
                                                        + methodParamChangeNode1.getFalseParent().getInitMethod().getOriginalDottedName()
                                                        + "'."
                                        );
                            }
                        }
                    }

                    if (bl1 && !bl2 && !bl3) {
                        this.scriptEnvironment
                                .logError(
                                        "Class '"
                                                + classFileBase1.getOriginalDottedName()
                                                + "' specified as a"
                                                + (bl ? " FALSE" : "")
                                                + " dependent on '"
                                                + classFileBase.getOriginalDottedName()
                                                + "' but the relationship could not be established because '"
                                                + classFileBase1.getOriginalDottedName()
                                                + "' has no relevant structures."
                                );
                    }
                } else if (bl1) {
                    this.scriptEnvironment
                            .logError(
                                    "Class '"
                                            + classFileBase1.getOriginalDottedName()
                                            + "' specified as a"
                                            + (bl ? " FALSE" : "")
                                            + " dependent on '"
                                            + classFileBase.getOriginalDottedName()
                                            + "' but it has no relevant structures."
                            );
                }
            } else if (bl1) {
                this.scriptEnvironment
                        .logError(
                                "Class '"
                                        + classFileBase.getOriginalDottedName()
                                        + "' specified as a"
                                        + (bl ? " FALSE" : "")
                                        + " dominant class but it has no relevant structures."
                        );
            }
        }
    }

    public CodeInsertion buildLayoutGetterCode(ProgramClass programClass1, ConstantPool constantPool1, List list1, MutableInt mutableInt, MutableInt mutableInt1) {
        mutableInt1.setValue(1);
        mutableInt.setValue(1);
        ArrayList arrayList = new ArrayList();
        FieldInfo fieldInfo = (FieldInfo) programClass1.findField("e", "[I");
        ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef(fieldInfo, list1);
        arrayList.add(new ConstantRefInstruction(178, resolvedFieldRef));
        return new CodeInsertion(arrayList, -1, 1, 1);
    }

    public CodeInsertion buildKeyDecodeCode(
            ProgramClass programClass1,
            ProgramClass programClass2,
            ProgramClass programClass3,
            LocalVariableList localVariableList1,
            ConstantPool constantPool1,
            List list1,
            MutableInt mutableInt,
            MutableInt mutableInt1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        mutableInt.setValue(5);
        mutableInt1.setValue(7);
        ArrayList arrayList = new ArrayList();
        LabelInstruction labelInstruction = new LabelInstruction(true, 1);
        String string = "L" + programClass2.getClassName() + ";";
        String string1 = "(J)" + string;
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 5));
        MethodInfo methodInfo1 = programClass3.findMethodBySignature(new MethodSignature("c", string1));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list1);
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(Instruction.createObjectStore(3, localVariableList1, 5));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 5));
        MethodInfo methodInfo2 = programClass2.findMethodBySignature(new MethodSignature("a", "(J)J"));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef = constantPool1.getOrCreateInterfaceMethodRef(methodInfo2, list1);
        arrayList.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef));
        arrayList.add(Instruction.createLongStore(4, localVariableList1, 5));
        arrayList.add(Instruction.createObjectLoad(3, localVariableList1, 5));
        MethodInfo methodInfo3 = programClass2.findMethodBySignature(new MethodSignature("b", "()[I"));
        ResolvedInterfaceMethodRef resolvedInterfaceMethodRef1 = constantPool1.getOrCreateInterfaceMethodRef(methodInfo3, list1);
        arrayList.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef1));
        arrayList.add(Instruction.createObjectStore(6, localVariableList1, 5));
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 5));
        AbstractFieldInfo abstractFieldInfo = programClass1.findField("g", "[I");
        ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef(abstractFieldInfo, list1);
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 5));
        arrayList.add(new ConstantRefInstruction(180, resolvedFieldRef));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(Instruction.createObjectLoad(6, localVariableList1, 5));
        arrayList.add(SimpleInstruction.forOpcode(3));
        arrayList.add(Instruction.createIntPush(64));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrAddMethodRef(
                "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V", list1, classMemberLookup1, classResolver1
        );
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant1));
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 5));
        AbstractFieldInfo abstractFieldInfo1 = programClass1.findField("c", string);
        ResolvedFieldRef resolvedFieldRef1 = constantPool1.getOrCreateFieldRef(abstractFieldInfo1, list1);
        arrayList.add(new ConstantRefInstruction(180, resolvedFieldRef1));
        arrayList.add(new BranchInstruction(198, labelInstruction));
        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 5));
        arrayList.add(new ConstantRefInstruction(180, resolvedFieldRef1));
        arrayList.add(Instruction.createLongLoad(1, localVariableList1, 5));
        arrayList.add(new InvokeInterfaceInstruction(resolvedInterfaceMethodRef));
        arrayList.add(SimpleInstruction.forOpcode(88));
        arrayList.add(labelInstruction);
        arrayList.add(Instruction.createLongLoad(4, localVariableList1, 5));
        return new CodeInsertion(arrayList, -1, 1, 2);
    }

    public MethodParameterChanger(
            Map map1,
            Enumeration enumeration,
            Enumeration enumeration1,
            Enumeration enumeration2,
            Enumeration enumeration3,
            Enumeration enumeration4,
            Enumeration enumeration5,
            SetMultiMap setMultiMap,
            int ba,
            ReadOnlyMultiMapView readOnlyMultiMapView,
            String string,
            MixedCaseNamesMode mixedCaseNamesMode1,
            boolean avoidSimpleNameClash,
            int classMajorVersion,
            int classMinorVersion,
            boolean keyChainingEnabled,
            ChangeLogMapping changeLogMapping1,
            ClassRepository classRepository1,
            EnumerableMap enumerableMap,
            ReadOnlyMultiMap readOnlyMultiMap,
            Set set1,
            StaticInitCalleeAnalyzer staticInitCalleeAnalyzer1,
            ClassInitOrderHandler classInitOrderHandler1,
            List list1,
            ClasspathClassLoader classpathClassLoader1,
            boolean randomizeNames,
            ScriptEnvironment scriptEnvironment1
    ) {
        this.methodKeys = map1;
        this.initDependencies = setMultiMap;
        this.stringEncryptionClasses = ZkmUtils.createHashSet();

        while (enumeration.hasMoreElements()) {
            this.stringEncryptionClasses.add(enumeration.nextElement());
        }

        while (enumeration1.hasMoreElements()) {
            ProgramClass programClass1 = (ProgramClass) enumeration1.nextElement();
            this.stringEncryptionClasses.add(programClass1);
        }

        this.integerEncryptionClasses = ZkmUtils.createHashSet();

        while (enumeration2.hasMoreElements()) {
            this.integerEncryptionClasses.add(enumeration2.nextElement());
        }

        while (enumeration3.hasMoreElements()) {
            ProgramClass programClass2 = (ProgramClass) enumeration3.nextElement();
            this.integerEncryptionClasses.add(programClass2);
        }

        this.longEncryptionClasses = ZkmUtils.createHashSet();

        while (enumeration4.hasMoreElements()) {
            this.longEncryptionClasses.add(enumeration4.nextElement());
        }

        while (enumeration5.hasMoreElements()) {
            ProgramClass programClass3 = (ProgramClass) enumeration5.nextElement();
            this.longEncryptionClasses.add(programClass3);
        }

        this.classesByArchive = readOnlyMultiMapView;
        this.lookupClassNamePrefix = string;
        this.mixedCaseNamesMode = mixedCaseNamesMode1;
        this.avoidSimpleNameClash = avoidSimpleNameClash;
        this.classMajorVersion = classMajorVersion;
        this.classMinorVersion = classMinorVersion;
        this.keyChainingEnabled = keyChainingEnabled;
        this.classRepository = classRepository1;
        this.classNameMap = enumerableMap;
        this.memberNameMap = readOnlyMultiMap;
        this.changeLogMapping = changeLogMapping1;
        this.flowObfuscatedClasses = set1;
        this.staticInitCalleeAnalyzer = staticInitCalleeAnalyzer1;
        this.classInitOrderHandler = classInitOrderHandler1;
        this.nameDictionary = list1;
        this.classpathClassLoader = classpathClassLoader1;
        this.scriptEnvironment = scriptEnvironment1;
        this.randomizeNames = randomizeNames;
        this.classHelperTriples = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        this.readRelationshipFile();

        try {
            Class<?> class1 = Class.forName(OpaquePredicateBase.OBFUSCATED_STRINGS[OpaquePredicateBase.OBFUSCATED_STRINGS.length - 1]);
            class1.newInstance();
        } catch (Throwable throwable) {
        }
    }

    public static long rerandomizeHighByte(long ba, int[] bb) {
        long bc = permuteAllBits(ba, bb);
        MutableInt mutableInt = new MutableInt();
        MutableLong mutableLong = new MutableLong();
        MutableInt mutableInt1 = new MutableInt();
        splitKey(bc, mutableInt, mutableLong, mutableInt1);
        Random random1 = sharedRandom;

        while (true) {
            int bd = random1.nextInt(256);
            if (bd != mutableInt.getValue()) {
                return composeKey(bd, mutableLong.getValue(), mutableInt1.getValue(), bb);
            }

            random1 = sharedRandom;
        }
    }

    public void putHelperTriple(Object object, Object object1) {
        ObjectTriple objectTriple = (ObjectTriple) this.classHelperTriples.put(object, object1);
    }

    public NodePairKeys getNodePairKeys(int ba) {
        return (NodePairKeys) this.nodePairKeys.get(ba);
    }

    public static void splitKey(long ba, MutableInt mutableInt, MutableLong mutableLong, MutableInt mutableInt1) {
        long bb = ba >>> 56;
        mutableInt.setValue((int) bb);
        long bc = ba << 8;
        bc >>>= 16;
        mutableLong.setValue(bc);
        long bd = ba << 56;
        bd >>>= 56;
        mutableInt1.setValue((int) bd);
    }

    public void buildLayoutInitCode(
            ProgramClass programClass1,
            ProgramClass programClass2,
            LocalVariableList localVariableList1,
            ArrayList arrayList,
            ObservableHolder observableHolder,
            ConstantPool constantPool1,
            List list1,
            MutableInt mutableInt,
            MutableInt mutableInt1
    ) {
        mutableInt.setValue(1);
        mutableInt1.setValue(3);
        MethodInfo methodInfo1 = programClass1.findMethodBySignature(new MethodSignature("c", "()V"));
        ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrCreateMethodRefConstant(methodInfo1, list1);
        arrayList.add(new ConstantRefInstruction(184, resolvedMethodRefConstant));
        FieldInfo fieldInfo = (FieldInfo) programClass1.findField("e", "[I");
        ResolvedFieldRef resolvedFieldRef = constantPool1.getOrCreateFieldRef(fieldInfo, list1);
        arrayList.add(Instruction.createIntPush(64));
        arrayList.add(new NewArrayInstruction(10));
        arrayList.add(SimpleInstruction.forOpcode(89));
        arrayList.add(new ConstantRefInstruction(179, resolvedFieldRef));
        int[] ba = (int[]) observableHolder.getValue();
        int bb = 0;
        int bc = 0;

        for (byte bd = 64; bc < bd; bd = 64) {
            if (bb < 63) {
                arrayList.add(SimpleInstruction.forOpcode(89));
            }

            arrayList.add(Instruction.createIntPush(bb));
            arrayList.add(Instruction.createIntPush(ba[bb]));
            arrayList.add(SimpleInstruction.forOpcode(79));
            bc = ++bb;
        }

        arrayList.add(Instruction.createObjectLoad(0, localVariableList1, 5));
        MethodInfo methodInfo2 = programClass2.findMethodBySignature(new MethodSignature("c", "()V"));
        ResolvedMethodRefConstant resolvedMethodRefConstant1 = constantPool1.getOrCreateMethodRefConstant(methodInfo2, list1);
        arrayList.add(new ConstantRefInstruction(182, resolvedMethodRefConstant1));
    }

    
    
    public List buildInitNodeHierarchy() throws ZkmException, IOException {
        ClassHierarchy classHierarchy1 = this.classRepository.getClassHierarchy();
        HashMap hashMap = ZkmUtils.createHashMap(this.initNodes.size());
        ArrayList arrayList = new ArrayList(this.initNodes.size());
        Iterator iterator = this.initNodes.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            MethodParamChangeNode methodParamChangeNode = (MethodParamChangeNode) entry.getValue();
            if (((MethodInfo) entry.getKey()).isStaticInitializer()) {
                hashMap.put(((MethodInfo) entry.getKey()).getOwnerProgramClass(), methodParamChangeNode);
            }

            arrayList.add(methodParamChangeNode);
        }

        if (!HiddenOptionFlags.SKIP_HIERARCHY_INIT_ORDER) {
            iterator = classHierarchy1.getTopProgramNodes().iterator();

            while (iterator.hasNext()) {
                ClassHierarchyNode classHierarchyNode4 = (ClassHierarchyNode) iterator.next();
                if (!classHierarchyNode4.isInterface()) {
                    this.linkSubclassNodes(classHierarchyNode4, hashMap);
                }
            }
        }

        if (this.initDependencies != null && !HiddenOptionFlags.SKIP_SPECIFIED_INIT_ORDER) {
            iterator = this.initDependencies.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry1 = (Entry) iterator.next();
                ProgramClass programClass2 = (ProgramClass) entry1.getKey();
                Set set1 = (Set) entry1.getValue();
                Iterator iterator1 = set1.iterator();

                while (iterator1.hasNext()) {
                    ProgramClass programClass1 = (ProgramClass) iterator1.next();
                    ClassHierarchyNode classHierarchyNode = ClassHierarchyNode.findNode(programClass2.getClassName());
                    ClassHierarchyNode classHierarchyNode1 = ClassHierarchyNode.findNode(programClass1.getClassName());
                    if (classHierarchyNode != null && classHierarchyNode1 != null) {
                        Boolean boolean1 = false;
                        this.addDependency(classHierarchyNode, classHierarchyNode1, hashMap, (String) null, boolean1);
                    }
                }
            }
        }

        label487:
        if (HiddenOptionFlags.CLASS_INIT_SPEC_FILE != null) {
            File file1 = new File(HiddenOptionFlags.CLASS_INIT_SPEC_FILE);
            ScriptEnvironment scriptEnvironment2;
            if (file1.exists()) {
                if (!file1.isDirectory()) {
                    if (this.scriptEnvironment.isVerbose()) {
                        this.scriptEnvironment
                                .logLine("Reading '" + file1 + "' to find special " + "Method Parameter List Changing" + " class initialization specifications.");
                    }

                    try {
                        FileReader fileReader = new FileReader(file1);
                        Throwable throwable4 = null;
                        boolean bl1 = false ;

                        try {
                            bl1 = true;
                            Properties properties1 = new Properties();
                            properties1.load(fileReader);
                            String string2 = "specified in file \"" + file1.getAbsolutePath() + "\"";
                            Iterator<String> iterator4 = properties1.stringPropertyNames().iterator();

                            while (iterator4.hasNext()) {
                                String string4 = iterator4.next();
                                String string6 = (String) ZkmUtils.mapOrSelf(ZkmUtils.dotsToSlashes(string4.trim()), this.classNameMap);
                                String string = properties1.getProperty(string4);
                                String string1 = (String) ZkmUtils.mapOrSelf(ZkmUtils.dotsToSlashes(string.trim()), this.classNameMap);
                                ClassHierarchyNode classHierarchyNode2 = ClassHierarchyNode.findNode(string1);
                                if (classHierarchyNode2 == null) {
                                    this.scriptEnvironment
                                            .logError(
                                                    "Class '"
                                                            + string
                                                            + "' not opened. It was specified as initialized before class '"
                                                            + string4
                                                            + "' in file '"
                                                            + file1.getAbsolutePath()
                                                            + "'. (1)"
                                            );
                                } else if (!classHierarchyNode2.isProgramClass()) {
                                    this.scriptEnvironment
                                            .logError(
                                                    "Class '"
                                                            + string
                                                            + "' not opened. It was specified as initialized before class '"
                                                            + string4
                                                            + "' in file '"
                                                            + file1.getAbsolutePath()
                                                            + "'. (2)"
                                            );
                                } else {
                                    ClassHierarchyNode classHierarchyNode3 = ClassHierarchyNode.findNode(string6);
                                    if (classHierarchyNode3 == null) {
                                        this.scriptEnvironment
                                                .logError(
                                                        "Class '"
                                                                + string4
                                                                + "' not opened. It was specified as initialized after class '"
                                                                + string
                                                                + "' in file '"
                                                                + file1.getAbsolutePath()
                                                                + "'. (3)"
                                                );
                                    } else if (!classHierarchyNode3.isProgramClass()) {
                                        this.scriptEnvironment
                                                .logError(
                                                        "Class '"
                                                                + string4
                                                                + "' not opened. It was specified as initialized after class '"
                                                                + string
                                                                + "' in file '"
                                                                + file1.getAbsolutePath()
                                                                + "'. (4)"
                                                );
                                    } else {
                                        this.addDependency(classHierarchyNode2, classHierarchyNode3, hashMap, string2, true);
                                    }
                                }
                            }

                            bl1 = false;
                        } catch (Throwable throwable3) {
                            throwable4 = throwable3;
                            throw throwable3;
                        } finally {
                            if (bl1) {
                                if (throwable4 != null) {
                                    try {
                                        fileReader.close();
                                    } catch (Throwable throwable1) {
                                        throwable4.addSuppressed(throwable1);
                                    }
                                } else {
                                    fileReader.close();
                                }
                            }
                        }

                        fileReader.close();
                    } catch (IOException iOException1) {
                        this.scriptEnvironment.logFatalError("Couldn't open relationship file '" + file1.getAbsolutePath() + "'. : " + iOException1 + "' (1)");
                    }
                    break label487;
                }

                scriptEnvironment2 = this.scriptEnvironment;
            } else {
                scriptEnvironment2 = this.scriptEnvironment;
            }

            scriptEnvironment2.logFatalError("Relationship file '" + file1.getAbsolutePath() + "' not found or is directory. (1)");
        }

        label463:
        if (HiddenOptionFlags.FALSE_CLASS_INIT_SPEC_FILE != null) {
            File file2 = new File(HiddenOptionFlags.FALSE_CLASS_INIT_SPEC_FILE);
            ScriptEnvironment scriptEnvironment1;
            if (file2.exists()) {
                if (!file2.isDirectory()) {
                    if (this.scriptEnvironment.isVerbose()) {
                        this.scriptEnvironment
                                .logLine("Reading '" + file2 + "' to find special " + "Method Parameter List Changing" + " FALSE class initialization specifications.");
                    }

                    try {
                        FileReader fileReader1 = new FileReader(file2);
                        Throwable throwable5 = null;
                        boolean bl = false ;

                        try {
                            bl = true;
                            Properties properties2 = new Properties();
                            properties2.load(fileReader1);
                            String string3 = "specified in file \"" + file2.getAbsolutePath() + "\"";
                            Iterator<String> iterator5 = properties2.stringPropertyNames().iterator();

                            while (iterator5.hasNext()) {
                                String string5 = iterator5.next();
                                String string7 = (String) ZkmUtils.mapOrSelf(ZkmUtils.dotsToSlashes(string5.trim()), this.classNameMap);
                                String string8 = properties2.getProperty(string5);
                                String string9 = (String) ZkmUtils.mapOrSelf(ZkmUtils.dotsToSlashes(string8.trim()), this.classNameMap);
                                ClassHierarchyNode classHierarchyNode6 = ClassHierarchyNode.findNode(string9);
                                if (classHierarchyNode6 == null) {
                                    this.scriptEnvironment
                                            .logError(
                                                    "Class '"
                                                            + string8
                                                            + "' not opened. It was specified as falsely initialized before class '"
                                                            + string5
                                                            + "' in file '"
                                                            + file2.getAbsolutePath()
                                                            + "'. (5)"
                                            );
                                } else if (!classHierarchyNode6.isProgramClass()) {
                                    this.scriptEnvironment
                                            .logError(
                                                    "Class '"
                                                            + string8
                                                            + "' not opened. It was specified as falsely initialized before class '"
                                                            + string5
                                                            + "' in file '"
                                                            + file2.getAbsolutePath()
                                                            + "'. (6)"
                                            );
                                } else {
                                    ClassHierarchyNode classHierarchyNode8 = ClassHierarchyNode.findNode(string7);
                                    if (classHierarchyNode8 == null) {
                                        this.scriptEnvironment
                                                .logError(
                                                        "Class '"
                                                                + string5
                                                                + "' not opened. It was specified as falsely initialized after class '"
                                                                + string8
                                                                + "' in file '"
                                                                + file2.getAbsolutePath()
                                                                + "'. (7)"
                                                );
                                    } else if (!classHierarchyNode8.isProgramClass()) {
                                        this.scriptEnvironment
                                                .logError(
                                                        "Class '"
                                                                + string5
                                                                + "' not opened. It was specified as falsely initialized after class '"
                                                                + string8
                                                                + "' in file '"
                                                                + file2.getAbsolutePath()
                                                                + "'. (8)"
                                                );
                                    } else {
                                        this.addDependencyChecked(classHierarchyNode6, classHierarchyNode8, true, hashMap, string3, true);
                                    }
                                }
                            }

                            bl = false;
                        } catch (Throwable throwable2) {
                            throwable5 = throwable2;
                            throw throwable2;
                        } finally {
                            if (bl) {
                                if (throwable5 != null) {
                                    try {
                                        fileReader1.close();
                                    } catch (Throwable throwable) {
                                        throwable5.addSuppressed(throwable);
                                    }
                                } else {
                                    fileReader1.close();
                                }
                            }
                        }

                        fileReader1.close();
                    } catch (IOException iOException) {
                        this.scriptEnvironment.logFatalError("Couldn't open 'false' relationship file '" + file2.getAbsolutePath() + "'. : " + iOException + "' (1)");
                    }
                    break label463;
                }

                scriptEnvironment1 = this.scriptEnvironment;
            } else {
                scriptEnvironment1 = this.scriptEnvironment;
            }

            scriptEnvironment1.logFatalError("'False' relationship file '" + file2.getAbsolutePath() + "' not found or is directory. (1)");
        }

        if (this.classInitOrderHandler != null) {
            SetMultiMap setMultiMap = this.classInitOrderHandler.getInitOrderMapCopy();
            Iterator iterator3 = setMultiMap.entrySet().iterator();

            while (iterator3.hasNext()) {
                Entry entry2 = (Entry) iterator3.next();
                ProgramClass programClass3 = (ProgramClass) entry2.getKey();
                Iterator iterator6 = ((Set) entry2.getValue()).iterator();

                while (iterator6.hasNext()) {
                    ClassHierarchyNode classHierarchyNode5;
                    ClassHierarchyNode classHierarchyNode7;
                    label426:
                    {
                        ProgramClass programClass4 = (ProgramClass) iterator6.next();
                        MethodParamChangeNode methodParamChangeNode2 = (MethodParamChangeNode) hashMap.get(programClass3);
                        boolean bl2 = methodParamChangeNode2 != null && !methodParamChangeNode2.hasParent() && !methodParamChangeNode2.hasFalseParent();
                        classHierarchyNode5 = ClassHierarchyNode.findNode(programClass3.getClassName());
                        classHierarchyNode7 = ClassHierarchyNode.findNode(programClass4.getClassName());
                        MethodParameterChanger methodParameterChanger1;
                        ClassHierarchyNode classHierarchyNode9;
                        ClassHierarchyNode classHierarchyNode10;
                        boolean bl3;
                        HashMap hashMap1;
                        String string10;
                        if (bl2) {
                            if (HiddenOptionFlags.NO_CLASS_INIT_LINKS) {
                                break label426;
                            }

                            if (sharedRandom.nextInt(4) == 0) {
                                if (!HiddenOptionFlags.RANDOM_CLASS_INIT_LINKS) {
                                    break label426;
                                }

                                methodParameterChanger1 = this;
                                classHierarchyNode9 = classHierarchyNode5;
                                classHierarchyNode10 = classHierarchyNode7;
                                bl3 = false;
                                hashMap1 = hashMap;
                                string10 = "specified in a \"classInitializationOrder\" statement";
                            } else {
                                methodParameterChanger1 = this;
                                classHierarchyNode9 = classHierarchyNode5;
                                classHierarchyNode10 = classHierarchyNode7;
                                bl3 = false;
                                hashMap1 = hashMap;
                                string10 = "specified in a \"classInitializationOrder\" statement";
                            }
                        } else {
                            methodParameterChanger1 = this;
                            classHierarchyNode9 = classHierarchyNode5;
                            classHierarchyNode10 = classHierarchyNode7;
                            bl3 = false;
                            hashMap1 = hashMap;
                            string10 = "specified in a \"classInitializationOrder\" statement";
                        }

                        methodParameterChanger1.addDependencyChecked(
                                classHierarchyNode9, classHierarchyNode10, bl3, hashMap1, string10, HiddenOptionFlags.STRICT_CLASS_INIT_ORDER
                        );
                        continue;
                    }

                    this.addDependencyChecked(
                            classHierarchyNode7,
                            classHierarchyNode5,
                            true,
                            hashMap,
                            "specified in a \"classInitializationOrder\" statement",
                            HiddenOptionFlags.STRICT_CLASS_INIT_ORDER
                    );
                }
            }
        }

        ArrayList arrayList1 = new ArrayList();
        Iterator iterator2 = arrayList.iterator();

        while (iterator2.hasNext()) {
            MethodParamChangeNode methodParamChangeNode1 = (MethodParamChangeNode) iterator2.next();
            if (!methodParamChangeNode1.hasParent()) {
                arrayList1.add(methodParamChangeNode1);
            }
        }

        return arrayList1;
    }

    public static long nextRandomLong() {
        return (Long) randomKeyIterator.next();
    }

    public ObjectTriple resolveChangeLogHelperNames(ObjectTriple objectTriple, EnumerableMap enumerableMap, Set set1, Map map1) throws IOException {
        ObjectTriple objectTriple1 = objectTriple;

        for (int i = 0; i < 3; i++) {
            String string2 = (String) objectTriple.getAt(i);
            ChangeLogMapping changeLogMapping1 = this.changeLogMapping;
            Set set2 = set1;
            Object object = null;
            String string = LookupClassFactory.validateChangeLogLookupName(
                    "MethodParameterChangeClasses:", string2, (String) null, (String) object, "Method Parameter List Changing", set2, changeLogMapping1, enumerableMap
            );
            if (string == null) {
                return null;
            }

            objectTriple.setAt(string, i);
        }

        String string1 = (String) objectTriple1.getFirst();
        if (map1.containsKey(string1)) {
            objectTriple1 = (ObjectTriple) map1.get(string1);
        } else {
            map1.put(string1, objectTriple1);
        }

        return objectTriple1;
    }

    public ScriptEnvironment getScriptEnvironment() {
        return this.scriptEnvironment;
    }
}
