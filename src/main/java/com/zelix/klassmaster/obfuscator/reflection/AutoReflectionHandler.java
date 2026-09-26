package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.archive.InputFileLocation;
import com.zelix.klassmaster.archive.SourceArchive;
import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.FieldSignature;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRefConstant;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassLoader;
import com.zelix.klassmaster.classfile.hierarchy.InheritedMemberAnalyzer;
import com.zelix.klassmaster.classfile.insn.ConstantRefInstruction;
import com.zelix.klassmaster.classfile.insn.Instruction;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;
import com.zelix.klassmaster.classfile.insn.SimpleInstruction;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.parameters.MethodKeyInjector;
import com.zelix.klassmaster.obfuscator.references.LookupClassFactory;
import com.zelix.klassmaster.obfuscator.references.SyntheticClassFactory;
import com.zelix.klassmaster.obfuscator.rename.IndexedNameGenerator;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.EnumerableMap;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ListenerRegistry;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;
import com.zelix.klassmaster.util.ReadOnlyMultiMapView;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmAssert;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

public class AutoReflectionHandler implements SyntheticClassFactory {
    public static final String[] COMMON_FIELD_NAME_PREFIXES = new String[]{"is"};
    public static final String[] COMMON_METHOD_NAME_PREFIXES = new String[]{"get", "set", "is"};
    public static final MethodSignature[] HASHTABLE_LOOKUP_METHOD_SIGS = new MethodSignature[]{
            new MethodSignature("a(Ljava/util/Hashtable;)V"),
            new MethodSignature("b(Ljava/util/Hashtable;)V"),
            new MethodSignature("c(Ljava/util/Hashtable;)V"),
            new MethodSignature("d(Ljava/util/Hashtable;)V"),
            new MethodSignature("e(Ljava/util/Hashtable;)V"),
            new MethodSignature("f(Ljava/util/Hashtable;)V"),
            new MethodSignature("g(Ljava/util/Hashtable;)V"),
            new MethodSignature("h(Ljava/util/Hashtable;)V"),
            new MethodSignature("i(Ljava/util/Hashtable;)V"),
            new MethodSignature("j(Ljava/util/Hashtable;)V"),
            new MethodSignature("k(Ljava/util/Hashtable;)V")
    };
    public static final MethodSignature[] CONCURRENT_MAP_LOOKUP_METHOD_SIGS = new MethodSignature[]{
            new MethodSignature("a(Ljava/util/concurrent/ConcurrentHashMap;)V"),
            new MethodSignature("b(Ljava/util/concurrent/ConcurrentHashMap;)V"),
            new MethodSignature("c(Ljava/util/concurrent/ConcurrentHashMap;)V"),
            new MethodSignature("d(Ljava/util/concurrent/ConcurrentHashMap;)V"),
            new MethodSignature("e(Ljava/util/concurrent/ConcurrentHashMap;)V"),
            new MethodSignature("f(Ljava/util/concurrent/ConcurrentHashMap;)V"),
            new MethodSignature("g(Ljava/util/concurrent/ConcurrentHashMap;)V"),
            new MethodSignature("h(Ljava/util/concurrent/ConcurrentHashMap;)V"),
            new MethodSignature("i(Ljava/util/concurrent/ConcurrentHashMap;)V"),
            new MethodSignature("j(Ljava/util/concurrent/ConcurrentHashMap;)V"),
            new MethodSignature("k(Ljava/util/concurrent/ConcurrentHashMap;)V")
    };
    public static final int MIN_SAFE_GUESS_LENGTH;
    public static final int MIN_LOOKUP_ENTRY_COUNT;
    public final ReflectionAccessMatcher accessMatcher;
    public final ClassMemberLookup classMemberLookup;
    public final String digestAlgorithm;
    public final boolean randomizeNames;
    public final List candidateNames;

    public int populateLookupClass(
            Map map1,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            Set set1,
            ProgramClass programClass1,
            String string,
            MethodKeyInjector methodKeyInjector,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        boolean bl = programClass1.supportsJava5();
        ConstantPool constantPool1 = programClass1.getClassConstantPool();

        MessageDigest messageDigest1;
        try {
            messageDigest1 = MessageDigest.getInstance(string);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new ZkmProcessingException(noSuchAlgorithmException.getMessage(), noSuchAlgorithmException);
        }

        int ba = map1.size() + twoKeyMap.getValueCount() + twoKeyMap1.getValueCount();
        ArrayList arrayList2 = new ArrayList(ZkmUtils.getPrimeCapacity(ba));
        MethodSignature[] methodSignatures;
        if (bl) {
            methodSignatures = CONCURRENT_MAP_LOOKUP_METHOD_SIGS;
        } else {
            methodSignatures = HASHTABLE_LOOKUP_METHOD_SIGS;
        }

        ArrayList arrayList3 = new ArrayList(methodSignatures.length);

        for (MethodSignature methodSignature1 : methodSignatures) {
            MethodInfo methodInfo1 = programClass1.findMethodBySignature(methodSignature1);
            arrayList3.add(methodInfo1);
        }

        Random random1 = ZkmUtils.createRandom(31);
        int bd = -1;

        while (++bd <= ReflectionLookupTemplate.KEY_SEPARATOR_CHARS.length) {
            String string2 = String.valueOf(ReflectionLookupTemplate.KEY_SEPARATOR_CHARS[bd]);
            Map map2 = this.buildClassLookupEntries(string2, map1);
            Map map3 = this.buildFieldLookupEntries(string2, twoKeyMap);
            Map map4 = this.buildMethodLookupEntries(string2, twoKeyMap1);
            boolean bl1 = false;
            ArrayList arrayList1 = new ArrayList();
            HashSet hashSet = ZkmUtils.createHashSet();
            String string4 = programClass1.getClassName();
            String string5 = bl ? "(Ljava/util/concurrent/ConcurrentHashMap;Ljava/lang/String;)V" : "(Ljava/util/Hashtable;Ljava/lang/String;)V";
            ArrayList arrayList = arrayList1;
            String string1 = string5;
            ResolvedMethodRefConstant resolvedMethodRefConstant = constantPool1.getOrAddMethodRef(
                    string4, "a", string1, arrayList, classMemberLookup1, classResolver1
            );
            HashSet hashSet1 = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(ba));
            hashSet1.addAll(map2.entrySet());
            hashSet1.addAll(map3.entrySet());
            hashSet1.addAll(map4.entrySet());
            if (hashSet1.size() < MIN_LOOKUP_ENTRY_COUNT) {
                this.addDecoyLookupEntries(hashSet1, map1, set1, map2, map3, map4);
            }

            ArrayList arrayList4 = new ArrayList(hashSet1);
            ZkmUtils.shuffleList(arrayList4, random1);
            ArrayList arrayList5 = new ArrayList();

            label97:
            {
                try {
                    this.encodeLookupChunks(arrayList4, arrayList5, hashSet, messageDigest1);
                } catch (DigestCollisionException digestCollisionException) {
                    bl1 = true;
                    break label97;
                }

                this.addChunkLoadInstructions(arrayList2, arrayList5, resolvedMethodRefConstant, constantPool1, arrayList1);
                if (set1 != null) {
                    Iterator iterator = set1.iterator();

                    while (iterator.hasNext()) {
                        String string3 = (String) iterator.next();
                        if (!hashSet.add(digestToBase36(string3, messageDigest1))) {
                            bl1 = true;
                        }
                    }
                }
            }

            if (!bl1) {
                programClass1.replaceStringConstant(String.valueOf('~'), string2);
                int be = arrayList2.size();
                if (be <= arrayList3.size() * 5950 && (HiddenOptionFlags.REFERENCE_OBFUSCATION_METHOD_KEYS || arrayList1.size() <= 65000)) {
                    if (methodKeyInjector != null) {
                        ClassFileBase[] classFileBases = new ClassFileBase[]{programClass1};
                        InheritedMemberAnalyzer inheritedMemberAnalyzer = new InheritedMemberAnalyzer(classFileBases, classResolver1);
                        MethodInfo methodInfo2 = programClass1.findMethodBySignature(MethodSignature.STATIC_INITIALIZER);
                        methodKeyInjector.initializeForMethod(methodInfo2, 0);
                        ArrayList arrayList6 = new ArrayList();
                        arrayList6.addAll(methodKeyInjector.buildKeyInitInstructions(methodInfo2, arrayList1, inheritedMemberAnalyzer, 9));
                        methodInfo2.insertInstructionsAtStart(arrayList6, 0, "Auto Reflection Handling");
                    }

                    constantPool1.appendEntries(arrayList1);
                    ZkmUtils.shuffleList(arrayList2, random1);
                    int bf = 0;
                    int bg = 0;
                    Iterator iterator1 = arrayList3.iterator();

                    while (iterator1.hasNext()) {
                        MethodInfo methodInfo3 = (MethodInfo) iterator1.next();
                        arrayList4 = new ArrayList(17850);
                        LocalVariableList localVariableList1 = methodInfo3.getLocalVariableList();
                        arrayList4.add(Instruction.createObjectLoad(0, localVariableList1, 9));
                        int bc = be - bf;
                        int bh = Math.min(5950, bc);
                        int bi = bf + bh;

                        for (int i = bf; i < bi; i++) {
                            for (Instruction instruction1 : (Instruction[]) arrayList2.get(i)) {
                                arrayList4.add(instruction1);
                                bg++;
                            }
                        }

                        bf = bi;
                        arrayList4.add(SimpleInstruction.forOpcode(87));
                        methodInfo3.insertInstructionsAtStart(arrayList4, 7, "Auto Reflection Handling");
                    }

                    return ba;
                }

                scriptEnvironment1.logSeriousError_v(
                        "Auto Reflection Handling : Too many class, method and/or field name lookup mappings. : "
                                + be
                                + " : "
                                + arrayList1.size()
                                + " : Consider using ZKM Script 'accessedByReflection' statement to specify only those classes, fields and methods which are actually accessed by Reflection and for which the Reflection access has not been resolved by "
                                + "Zelix KlassMaster"
                                + "."
                                + ""
                );
                return 0;
            }
        }

        throw new ZkmProcessingException(
                "Unable to automatically handle Reflection calls after " + ReflectionLookupTemplate.KEY_SEPARATOR_CHARS.length + " attempts."
        );
    }

    public void addAccessibleFieldMappings(
            boolean bl,
            boolean bl1,
            Object object,
            boolean bl2,
            String string,
            MemberScope memberScope,
            String string1,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            Map map1,
            ScriptEnvironment scriptEnvironment1
    ) throws IOException {
        Map map2 = twoKeyMap.getInnerMap(string1);
        ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string1);
        if (map2 != null) {
            Iterator iterator = map2.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                FieldSignature fieldSignature = (FieldSignature) entry.getKey();
                FieldSignature fieldSignature1 = (FieldSignature) entry.getValue();
                if (!fieldSignature1.getName().equals(fieldSignature.getName()) && (string == null || fieldSignature.getName().equals(string))) {
                    FieldInfo fieldInfo = programClass1.findFieldBySignature(fieldSignature1);
                    switch (ReflectionKindSwitchMap.MEMBER_SCOPE_SWITCH[memberScope.ordinal()]) {
                        case 1:
                            if (fieldInfo.isStatic()) {
                                continue;
                            }
                            break;
                        case 2:
                            if (!fieldInfo.isStatic()) {
                                continue;
                            }
                    }

                    if (fieldInfo.isPublic()
                            || bl && fieldInfo.isProtected()
                            || bl1 && fieldInfo.isPackagePrivate() && programClass1.getPackagePath().equals(object)
                            || bl2 && fieldInfo.isStrictlyPrivate()) {
                        twoKeyMap1.putValue(string1, fieldSignature, fieldSignature1);
                        this.checkFieldMappingHashStrength(string1, fieldSignature, fieldSignature1, map1, scriptEnvironment1);
                    }
                }
            }
        }
    }

    public Map buildFieldLookupEntries(String string, TwoKeyMap twoKeyMap) {
        HashMap hashMap = ZkmUtils.createHashMap();
        Enumeration enumeration = twoKeyMap.keys();

        while (enumeration.hasMoreElements()) {
            String string1 = (String) enumeration.nextElement();
            Iterator iterator = twoKeyMap.getInnerMap(string1).entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                FieldSignature fieldSignature = (FieldSignature) entry.getKey();
                FieldSignature fieldSignature1 = (FieldSignature) entry.getValue();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(string1);
                stringBuilder.append(string);
                stringBuilder.append(fieldSignature.getName());
                hashMap.put(stringBuilder.toString(), fieldSignature1.getName());
            }
        }

        return hashMap;
    }

    public void addFieldMappingsForHierarchy(
            String string,
            String string1,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            Map map1,
            ClassMemberLookup classMemberLookup1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        Map map2 = map1;
        TwoKeyMap twoKeyMap3 = twoKeyMap1;
        TwoKeyMap twoKeyMap2 = twoKeyMap;
        String string3 = string;
        MemberScope memberScope = MemberScope.BOTH;
        String string2 = string1;
        Boolean boolean1 = false;
        this.addFieldMappingsWithSupertypes(false, false, (String) null, boolean1, string2, memberScope, string3, twoKeyMap2, twoKeyMap3, map2, scriptEnvironment1);
        ClassMemberLookup classMemberLookup2 = classMemberLookup1;
        Map map3 = map1;
        TwoKeyMap twoKeyMap5 = twoKeyMap1;
        TwoKeyMap twoKeyMap4 = twoKeyMap;
        String string5 = string;
        MemberScope memberScope1 = MemberScope.BOTH;
        String string4 = string1;
        Boolean boolean2 = false;
        this.addFieldMappingsWithSubclasses(
                false, false, (String) null, boolean2, string4, memberScope1, string5, twoKeyMap4, twoKeyMap5, map3, classMemberLookup2, scriptEnvironment1
        );
    }

    public void checkClassMappingHashStrength(String string, String string1, ScriptEnvironment scriptEnvironment1) throws IOException {
        String string2 = ClassFileBase.stripPackage(string);
        String string3 = ClassFileBase.stripPackage(string1);
        String string4 = ClassFileBase.getPackagePath(string);
        String string5 = ClassFileBase.getPackagePath(string1);
        if (string4.equals(string5)) {
            if (string2.length() < MIN_SAFE_GUESS_LENGTH) {
                scriptEnvironment1.logWarning(
                        "Auto Reflection Handling : AutoReflection hash is "
                                + (string2.length() < 8 ? "very " : "")
                                + "vulnerable to brute force attack : Class mapping '"
                                + ZkmUtils.slashesToDots(string)
                                + "'=>'"
                                + ZkmUtils.slashesToDots(string1)
                                + "'. Only "
                                + string2.length()
                                + " characters have to be guessed in class name. If class is not actually accessed via Reflection then use ZKM Script 'accessedByReflection' statement to specify only those classes, methods and fields that are accessed by Reflection. (A)"
                );
            }
        } else {
            StringTokenizer stringTokenizer = new StringTokenizer(string4, "/");
            StringTokenizer stringTokenizer1 = new StringTokenizer(string5, "/");
            int ba = 0;
            int bb = 0;
            String string6 = "";
            int bc = stringTokenizer.countTokens();

            for (int i = 0; i < bc; i++) {
                String string7 = stringTokenizer.nextToken();
                if (stringTokenizer1.hasMoreTokens()) {
                    String string8 = stringTokenizer1.nextToken();
                    if (!string7.equals(string8)) {
                        string6 = string7;
                        if (string7.length() > ba) {
                            ba = string7.length();
                        }
                    }
                } else {
                    bb += string6.length();
                    string6 = "";
                    bb += string7.length() + (bb > 0 ? 1 : 0);
                    if (string7.length() > ba) {
                        ba = string7.length();
                    }
                }
            }

            int be = Math.max(ba, bb);
            if (string2.equals(string3)) {
                if (be < MIN_SAFE_GUESS_LENGTH) {
                    scriptEnvironment1.logWarning(
                            "Auto Reflection Handling : AutoReflection hash is "
                                    + (be < 8 ? "very " : "")
                                    + "vulnerable to brute force attack : Class mapping '"
                                    + ZkmUtils.slashesToDots(string)
                                    + "'=>'"
                                    + ZkmUtils.slashesToDots(string1)
                                    + "'. As few as "
                                    + be
                                    + " characters would have to be guessed in package name. If class is not actually accessed via Reflection then use ZKM Script 'accessedByReflection' statement to specify only those classes, methods and fields that are accessed by Reflection. (B)"
                    );
                }
            } else {
                int bf = Math.max(be, string2.length() + bb + (bb > 0 ? 1 : 0));
                if (bf < MIN_SAFE_GUESS_LENGTH) {
                    scriptEnvironment1.logWarning(
                            "Auto Reflection Handling : AutoReflection hash is "
                                    + (bf < 8 ? "very " : "")
                                    + "vulnerable to brute force attack : Class mapping '"
                                    + ZkmUtils.slashesToDots(string)
                                    + "'=>'"
                                    + ZkmUtils.slashesToDots(string1)
                                    + "'. As few as "
                                    + bf
                                    + " characters have to be guessed in package or class name. If class is not actually accessed via Reflection then use ZKM Script 'accessedByReflection' statement to specify only those classes, methods and fields that are accessed by Reflection. (C)"
                    );
                }
            }
        }
    }

    public void addDecoyLookupEntries(Set set1, Map map1, Set set2, Map map2, Map map3, Map map4) {
        Random random1 = ZkmUtils.createRandom(32);
        int ba = MIN_LOOKUP_ENTRY_COUNT - set1.size();
        HashMap hashMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        Collection collection1 = map1.values();
        HashSet hashSet = ZkmUtils.createHashSetFrom(set2);
        hashSet.removeAll(collection1);
        int bb;
        if (map2.size() == 0) {
            bb = 0;
        } else if (map4.size() == 0 && map3.size() == 0) {
            bb = Math.min(ba, hashSet.size());
        } else {
            bb = Math.min(ba / 2, hashSet.size());
        }

        HashSet hashSet1 = ZkmUtils.createHashSet();
        int bc = ba - bb;
        Iterator iterator = hashSet.iterator();

        for (int i = 0; i < bb; i++) {
            String string = this.generateUniqueDecoyKey(random1, hashSet1);
            String string1 = (String) iterator.next();
            hashMap.put(string, string1);
        }

        IndexedNameGenerator indexedNameGenerator = new IndexedNameGenerator();

        for (int i = 0; i < bc; i++) {
            String string2 = this.generateUniqueDecoyKey(random1, hashSet1);
            hashMap.put(string2, indexedNameGenerator.getNonKeywordName(random1.nextInt(200)));
        }

        set1.addAll(hashMap.entrySet());
    }

    public String encryptMappedName(String string, String string1, MessageDigest messageDigest1) throws UnsupportedEncodingException {
        messageDigest1.reset();
        byte[] ba = (string + messageDigest1.getAlgorithm()).getBytes("UTF-8");
        messageDigest1.update(ba);
        byte[] bb = messageDigest1.digest();
        char[] bc = string1.toCharArray();
        StringBuffer stringBuffer = new StringBuffer(bc.length);

        for (int i = 0; i < bc.length; i++) {
            char be = bc[i];
            byte bf;
            if (i < bb.length - 1) {
                bf = bb[i];
            } else {
                bf = bb[i % bb.length];
            }

            stringBuffer.append((char) (be ^ (char) bf));
        }

        return stringBuffer.toString();
    }

    public void warnNoMatchingMembers(
            ReflectionCallSite reflectionCallSite, boolean bl, boolean bl1, MemberCategory memberCategory, ScriptEnvironment scriptEnvironment1
    ) {
        String string;
        if (bl && bl1) {
            string = "net affect of 'accessedByReflection' and 'accessedByReflectionExclude' statements specifies";
        } else if (bl) {
            string = "'accessedByReflection' statement specifies";
        } else {
            string = "'accessedByReflectionExclude' statement leaves";
        }

        scriptEnvironment1.logWarning(
                "AutoReflection : A "
                        + memberCategory.toString().toLowerCase()
                        + " is accessed by reflection in '"
                        + reflectionCallSite.getClassName()
                        + "' but "
                        + string
                        + " no corresponding "
                        + memberCategory.toString().toLowerCase()
                        + " as accessed by Reflection."
        );
    }

    public void checkFieldMappingHashStrength(
            String string, FieldSignature fieldSignature, FieldSignature fieldSignature1, Map map1, ScriptEnvironment scriptEnvironment1
    ) throws IOException {
        String string1 = fieldSignature.getName();
        int ba = string1.length();
        String string2 = null;

        for (String string3 : COMMON_FIELD_NAME_PREFIXES) {
            if (string1.startsWith(string3)) {
                string2 = string3;
                break;
            }
        }

        if (string2 != null) {
            int bb = ba - string2.length();
            if (bb < MIN_SAFE_GUESS_LENGTH) {
                String string5 = (String) ZkmUtils.mapOrSelf(string, map1);
                scriptEnvironment1.logWarning(
                        "Auto Reflection Handling : AutoReflection hash is "
                                + (ba < 8 ? "very " : "")
                                + "vulnerable to brute force attack : Field mapping '"
                                + fieldSignature.formatDeclaration(map1)
                                + "'=>'"
                                + fieldSignature1.formatDeclaration(map1)
                                + "' in class '"
                                + ZkmUtils.slashesToDots(string5)
                                + "'. Given that the prefix '"
                                + string2
                                + "' is common, only "
                                + bb
                                + " characters have to be guessed in field name. If field is not actually accessed via Reflection then use ZKM Script 'accessedByReflection' statement to specify only those classes, methods and fields that are accessed by Reflection. (A)"
                );
            }
        } else if (ba < MIN_SAFE_GUESS_LENGTH) {
            String string4 = (String) ZkmUtils.mapOrSelf(string, map1);
            scriptEnvironment1.logWarning(
                    "Auto Reflection Handling : AutoReflection hash is "
                            + (ba < 8 ? "very " : "")
                            + "vulnerable to brute force attack : Field mapping '"
                            + fieldSignature.formatDeclaration(map1)
                            + "'=>'"
                            + fieldSignature1.formatDeclaration(map1)
                            + "' in class '"
                            + ZkmUtils.slashesToDots(string4)
                            + "'. Only "
                            + ba
                            + " characters have to be guessed in field name. If field is not actually accessed via Reflection then use ZKM Script 'accessedByReflection' statement to specify only those classes, methods and fields that are accessed by Reflection. (B)"
            );
        }
    }

    public static String digestToBase36(String string, MessageDigest messageDigest1) throws UnsupportedEncodingException {
        messageDigest1.reset();
        messageDigest1.update(string.getBytes("UTF-8"));
        return new BigInteger(messageDigest1.digest()).toString(36);
    }

    public void checkMethodMappingHashStrength(
            String string, MethodSignature methodSignature1, MethodSignature methodSignature2, Map map1, ScriptEnvironment scriptEnvironment1
    ) throws IOException {
        String string1 = methodSignature1.getName();
        int ba = string1.length();
        String string2 = null;

        for (String string3 : COMMON_METHOD_NAME_PREFIXES) {
            if (string1.startsWith(string3)) {
                string2 = string3;
                break;
            }
        }

        if (string2 != null) {
            int bb = ba - string2.length();
            if (bb < MIN_SAFE_GUESS_LENGTH) {
                String string5 = (String) ZkmUtils.mapOrSelf(string, map1);
                scriptEnvironment1.logWarning(
                        "Auto Reflection Handling : AutoReflection hash is "
                                + (bb < 8 ? "very " : "")
                                + "vulnerable to brute force attack : Method mapping '"
                                + methodSignature1.getNameWithParameters(map1)
                                + "'=>'"
                                + methodSignature2.getNameWithParameters(map1)
                                + "' in class '"
                                + ZkmUtils.slashesToDots(string5)
                                + "'. Given that the prefix '"
                                + string2
                                + "' is common, only "
                                + bb
                                + " characters have to be guessed in method name. If method is not actually accessed via Reflection then use ZKM Script 'accessedByReflection' statement to specify only those classes, methods and fields that are accessed by Reflection. (A)"
                );
            }
        } else if (ba < MIN_SAFE_GUESS_LENGTH) {
            String string4 = (String) ZkmUtils.mapOrSelf(string, map1);
            scriptEnvironment1.logWarning(
                    "Auto Reflection Handling : AutoReflection hash is "
                            + (ba < 8 ? "very " : "")
                            + "vulnerable to brute force attack : Method mapping '"
                            + methodSignature1.getNameWithParameters(map1)
                            + "'=>'"
                            + methodSignature2.getNameWithParameters(map1)
                            + "' in class '"
                            + ZkmUtils.slashesToDots(string4)
                            + "'. Only "
                            + ba
                            + " characters have to be guessed in method name. If method is not actually accessed via Reflection then use ZKM Script 'accessedByReflection' statement to specify only those classes, methods and fields that are accessed by Reflection. (B)"
            );
        }
    }

    static {
        int ba = 11;
        if (HiddenOptionFlags.AUTO_REFLECTION_MIN_HASH_LENGTH != null) {
            try {
                int bb = Integer.parseInt(HiddenOptionFlags.AUTO_REFLECTION_MIN_HASH_LENGTH);
                if (bb >= 8) {
                    ba = bb;
                }
            } catch (NumberFormatException numberFormatException1) {
            }
        }

        MIN_SAFE_GUESS_LENGTH = ba;
        int bc = 100;
        if (HiddenOptionFlags.AUTO_REFLECTION_MAX_CANDIDATES != null) {
            try {
                bc = Integer.parseInt(HiddenOptionFlags.AUTO_REFLECTION_MAX_CANDIDATES);
            } catch (NumberFormatException numberFormatException) {
            }
        }

        MIN_LOOKUP_ENTRY_COUNT = bc;
    }

    public void printIncludedMappings(EnumerableMap enumerableMap, Map map1, TwoKeyMap twoKeyMap, TwoKeyMap twoKeyMap1, PrintWriter printWriter) throws IOException {
        Iterator iterator = map1.keySet().iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            printWriter.println("\tIncluded AutoReflection mapping for class '" + ZkmUtils.slashesToDots(string) + "'");
        }

        iterator = twoKeyMap.keySet().iterator();

        while (iterator.hasNext()) {
            String string2 = (String) iterator.next();
            String string1 = (String) ZkmUtils.mapOrSelf(string2, enumerableMap);
            Map map2 = twoKeyMap.getInnerMap(string2);
            Iterator iterator1 = map2.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry = (Entry) iterator1.next();
                printWriter.println(
                        "\tIncluded AutoReflection mapping for field '"
                                + MethodSignature.descriptorToJavaType(((FieldSignature) entry.getKey()).getDescriptor(), enumerableMap)
                                + " "
                                + ((FieldSignature) entry.getKey()).getName()
                                + "' in class '"
                                + ZkmUtils.slashesToDots(string1)
                                + "'"
                );
            }
        }

        iterator = twoKeyMap1.keySet().iterator();

        while (iterator.hasNext()) {
            String string3 = (String) iterator.next();
            String string4 = (String) ZkmUtils.mapOrSelf(string3, enumerableMap);
            Map map3 = twoKeyMap1.getInnerMap(string3);
            Iterator iterator2 = map3.entrySet().iterator();

            while (iterator2.hasNext()) {
                Entry entry1 = (Entry) iterator2.next();
                printWriter.println(
                        "\tIncluded AutoReflection mapping for method '"
                                + ((MethodSignature) entry1.getKey()).getName()
                                + MethodSignature.formatParameterTypes(((MethodSignature) entry1.getKey()).getDescriptor(), enumerableMap)
                                + "' in class '"
                                + ZkmUtils.slashesToDots(string4)
                                + "'"
                );
            }
        }
    }

    public void processReflectionCallSites(
            ListMultimap listMultimap,
            EnumerableMap enumerableMap,
            EnumerableMap enumerableMap1,
            ReadOnlyMultiMap readOnlyMultiMap,
            ReadOnlyMultiMap readOnlyMultiMap1,
            Map map1,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            boolean bl,
            boolean bl1,
            ClassMemberLookup classMemberLookup1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        HashMap hashMap1 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(enumerableMap.size()));
        HashMap hashMap2 = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(enumerableMap1.size()));
        TwoKeyMap twoKeyMap4 = new TwoKeyMap(ZkmUtils.getPrimeCapacity(readOnlyMultiMap.getKeyCount()), false);
        TwoKeyMap twoKeyMap5 = new TwoKeyMap(ZkmUtils.getPrimeCapacity(readOnlyMultiMap1.getKeyCount()), false);
        this.collectAccessedMappings(enumerableMap, readOnlyMultiMap, readOnlyMultiMap1, hashMap1, hashMap2, twoKeyMap4, twoKeyMap5);
        boolean bl2 = false;
        boolean bl3 = false;
        boolean bl4 = false;
        boolean bl5 = false;
        boolean bl6 = false;
        boolean bl7 = false;
        boolean bl8 = false;
        boolean bl9 = false;
        Iterator iterator = listMultimap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            ((MethodInfo) entry.getKey()).getOwnerProgramClass().getClassName();
            Iterator iterator1 = ((List) entry.getValue()).iterator();

            while (iterator1.hasNext()) {
                ReflectionCallSite reflectionCallSite = (ReflectionCallSite) iterator1.next();
                ReflectionTargetScope reflectionTargetScope = reflectionCallSite.getApiMethod().getTargetScope();
                String string2 = reflectionCallSite.getMemberName();
                switch (ReflectionKindSwitchMap.TARGET_SCOPE_SWITCH[reflectionTargetScope.ordinal()]) {
                    case 1:
                        if (bl2) {
                            break;
                        }

                        bl2 = true;
                        if (hashMap1.size() == 0) {
                            this.warnNoMatchingMembers(reflectionCallSite, bl, bl1, MemberCategory.CLASS, scriptEnvironment1);
                        } else {
                            Iterator iterator3 = hashMap1.entrySet().iterator();

                            while (iterator3.hasNext()) {
                                Entry entry2 = (Entry) iterator3.next();
                                String string12 = (String) entry2.getKey();
                                String string19 = (String) entry2.getValue();
                                if (!string12.equals(string19)) {
                                    map1.put(string12, string19);
                                    this.checkClassMappingHashStrength(string12, string19, scriptEnvironment1);
                                }
                            }
                        }
                        break;
                    case 2:
                        if (bl6 || bl4 || bl5) {
                            break;
                        }

                        if (twoKeyMap5.getKeyCount() == 0) {
                            this.warnNoMatchingMembers(reflectionCallSite, bl, bl1, MemberCategory.METHOD, scriptEnvironment1);
                        } else {
                            if (reflectionCallSite.hasResolvedTargetClasses()) {
                                List list4 = reflectionCallSite.getTargetClassNames();
                                Iterator iterator7 = list4.iterator();

                                while (iterator7.hasNext()) {
                                    String string11 = (String) iterator7.next();
                                    String string18 = (String) ZkmUtils.mapOrSelf(string11, enumerableMap);
                                    ScriptEnvironment scriptEnvironment3 = scriptEnvironment1;
                                    ClassMemberLookup classMemberLookup2 = classMemberLookup1;
                                    HashMap hashMap3 = hashMap2;
                                    TwoKeyMap twoKeyMap6 = twoKeyMap1;
                                    TwoKeyMap twoKeyMap7 = twoKeyMap5;
                                    String string6 = string2;
                                    String string7 = string18;
                                    this.addMethodMappingsForHierarchy(string7, string6, twoKeyMap7, twoKeyMap6, hashMap3, classMemberLookup2, scriptEnvironment3);
                                }
                                break;
                            }

                            this.addAllMethodMappings(twoKeyMap5, twoKeyMap1, true, string2, MemberScope.BOTH, hashMap2, scriptEnvironment1);
                            bl6 = string2 == null;
                        }
                        break;
                    case 3:
                        if (bl9 || bl7 || bl8) {
                            break;
                        }

                        if (twoKeyMap4.getKeyCount() == 0) {
                            this.warnNoMatchingMembers(reflectionCallSite, bl, bl1, MemberCategory.FIELD, scriptEnvironment1);
                        } else {
                            if (reflectionCallSite.hasResolvedTargetClasses()) {
                                List list3 = reflectionCallSite.getTargetClassNames();
                                Iterator iterator6 = list3.iterator();

                                while (iterator6.hasNext()) {
                                    String string10 = (String) iterator6.next();
                                    String string17 = (String) ZkmUtils.mapOrSelf(string10, enumerableMap);
                                    this.addFieldMappingsForHierarchy(string17, string2, twoKeyMap4, twoKeyMap, hashMap2, classMemberLookup1, scriptEnvironment1);
                                }
                                break;
                            }

                            this.addAllFieldMappings(twoKeyMap4, twoKeyMap, true, string2, MemberScope.BOTH, hashMap2, scriptEnvironment1);
                            bl9 = string2 == null;
                        }
                        break;
                    case 4:
                        if (bl4 || bl5) {
                            break;
                        }

                        if (twoKeyMap5.getKeyCount() == 0) {
                            this.warnNoMatchingMembers(reflectionCallSite, bl, bl1, MemberCategory.METHOD, scriptEnvironment1);
                        } else {
                            if (reflectionCallSite.hasResolvedTargetClasses()) {
                                List list2 = reflectionCallSite.getTargetClassNames();
                                Iterator iterator5 = list2.iterator();

                                while (iterator5.hasNext()) {
                                    String string9 = (String) iterator5.next();
                                    String string16 = (String) ZkmUtils.mapOrSelf(string9, enumerableMap);
                                    this.addMethodMappingsWithSubclasses(
                                            true,
                                            true,
                                            ClassFileBase.getPackagePath(string16),
                                            true,
                                            string2,
                                            MemberScope.BOTH,
                                            string16,
                                            twoKeyMap5,
                                            twoKeyMap1,
                                            hashMap2,
                                            classMemberLookup1,
                                            scriptEnvironment1
                                    );
                                }
                                break;
                            }

                            this.addAllMethodMappings(twoKeyMap5, twoKeyMap1, false, string2, MemberScope.BOTH, hashMap2, scriptEnvironment1);
                            bl4 = string2 == null;
                            bl5 = string2 == null;
                            bl6 = string2 == null;
                        }
                        break;
                    case 5:
                    case 6:
                        if (bl7 || bl8) {
                            break;
                        }

                        if (twoKeyMap4.getKeyCount() == 0) {
                            this.warnNoMatchingMembers(reflectionCallSite, bl, bl1, MemberCategory.FIELD, scriptEnvironment1);
                        } else {
                            if (reflectionCallSite.hasResolvedTargetClasses()) {
                                List list1 = reflectionCallSite.getTargetClassNames();
                                Iterator iterator4 = list1.iterator();

                                while (iterator4.hasNext()) {
                                    String string8 = (String) iterator4.next();
                                    String string15 = (String) ZkmUtils.mapOrSelf(string8, enumerableMap);
                                    this.addFieldMappingsWithSubclasses(
                                            true,
                                            true,
                                            ClassFileBase.getPackagePath(string15),
                                            true,
                                            string2,
                                            MemberScope.BOTH,
                                            string15,
                                            twoKeyMap4,
                                            twoKeyMap,
                                            hashMap2,
                                            classMemberLookup1,
                                            scriptEnvironment1
                                    );
                                }
                                break;
                            }

                            this.addAllFieldMappings(twoKeyMap4, twoKeyMap, false, string2, MemberScope.BOTH, hashMap2, scriptEnvironment1);
                            bl7 = string2 == null;
                            bl8 = string2 == null;
                            bl9 = string2 == null;
                        }
                        break;
                    case 7:
                    case 8:
                        MemberScope memberScope2;
                        if (reflectionTargetScope == ReflectionTargetScope.ALL_VIRTUAL_METHODS_ACCESSIBLE_FROM_CLASS) {
                            memberScope2 = MemberScope.INSTANCE;
                        } else {
                            memberScope2 = MemberScope.STATIC;
                        }

                        if ((memberScope2 != MemberScope.INSTANCE || bl4) && (memberScope2 != MemberScope.STATIC || bl5)) {
                            break;
                        }

                        if (twoKeyMap5.getKeyCount() == 0) {
                            this.warnNoMatchingMembers(reflectionCallSite, bl, bl1, MemberCategory.METHOD, scriptEnvironment1);
                        } else if (reflectionCallSite.hasResolvedTargetClasses()) {
                            List list6 = reflectionCallSite.getTargetClassNames();
                            Iterator iterator9 = list6.iterator();

                            while (iterator9.hasNext()) {
                                String string14 = (String) iterator9.next();
                                String string20 = (String) ZkmUtils.mapOrSelf(string14, enumerableMap);
                                String string21 = ClassFileBase.getPackagePath(string20);
                                ScriptEnvironment scriptEnvironment2 = scriptEnvironment1;
                                HashMap hashMap = hashMap2;
                                TwoKeyMap twoKeyMap2 = twoKeyMap1;
                                TwoKeyMap twoKeyMap3 = twoKeyMap5;
                                String string = string20;
                                MemberScope memberScope = memberScope2;
                                String string1 = string2;
                                this.addMethodMappingsWithSupertypes(
                                        true, true, string21, true, string1, memberScope, string, twoKeyMap3, twoKeyMap2, hashMap, scriptEnvironment2
                                );
                                this.addMethodMappingsWithSubclasses(
                                        true,
                                        true,
                                        ClassFileBase.getPackagePath(string20),
                                        true,
                                        string2,
                                        memberScope2,
                                        string20,
                                        twoKeyMap5,
                                        twoKeyMap1,
                                        hashMap2,
                                        classMemberLookup1,
                                        scriptEnvironment1
                                );
                            }
                        } else {
                            this.addAllMethodMappings(twoKeyMap5, twoKeyMap1, false, string2, memberScope2, hashMap2, scriptEnvironment1);
                            if (memberScope2 == MemberScope.INSTANCE) {
                                bl4 = string2 == null;
                            } else {
                                bl5 = string2 == null;
                            }
                        }
                        break;
                    case 9:
                    case 10:
                        MemberScope memberScope1;
                        if (reflectionTargetScope == ReflectionTargetScope.ALL_INSTANCE_FIELDS_ACCESSIBLE_FROM_CLASS) {
                            memberScope1 = MemberScope.INSTANCE;
                        } else {
                            memberScope1 = MemberScope.STATIC;
                        }

                        if (bl9 || (memberScope1 != MemberScope.INSTANCE || bl7) && (memberScope1 != MemberScope.STATIC || bl8)) {
                            break;
                        }

                        if (twoKeyMap4.getKeyCount() == 0) {
                            this.warnNoMatchingMembers(reflectionCallSite, bl, bl1, MemberCategory.FIELD, scriptEnvironment1);
                        } else if (reflectionCallSite.hasResolvedTargetClasses()) {
                            List list5 = reflectionCallSite.getTargetClassNames();
                            Iterator iterator8 = list5.iterator();

                            while (iterator8.hasNext()) {
                                String string13 = (String) iterator8.next();
                                String string5 = (String) ZkmUtils.mapOrSelf(string13, enumerableMap);
                                this.addFieldMappingsWithSupertypes(
                                        true,
                                        true,
                                        ClassFileBase.getPackagePath(string5),
                                        true,
                                        string2,
                                        memberScope1,
                                        string5,
                                        twoKeyMap4,
                                        twoKeyMap,
                                        hashMap2,
                                        scriptEnvironment1
                                );
                                this.addFieldMappingsWithSubclasses(
                                        true,
                                        true,
                                        ClassFileBase.getPackagePath(string5),
                                        true,
                                        string2,
                                        memberScope1,
                                        string5,
                                        twoKeyMap4,
                                        twoKeyMap,
                                        hashMap2,
                                        classMemberLookup1,
                                        scriptEnvironment1
                                );
                            }
                        } else {
                            this.addAllFieldMappings(twoKeyMap4, twoKeyMap, true, string2, memberScope1, hashMap2, scriptEnvironment1);
                            if (memberScope1 == MemberScope.INSTANCE) {
                                bl7 = string2 == null;
                            } else {
                                bl8 = string2 == null;
                            }
                        }
                        break;
                    case 11:
                        if (!bl2 && !bl3) {
                            bl3 = true;
                            if (hashMap1.size() == 0) {
                                this.warnNoMatchingMembers(reflectionCallSite, bl, bl1, MemberCategory.CLASS, scriptEnvironment1);
                            } else {
                                Iterator iterator2 = hashMap1.entrySet().iterator();

                                while (iterator2.hasNext()) {
                                    Entry entry1 = (Entry) iterator2.next();
                                    String string3 = (String) entry1.getKey();
                                    String string4 = (String) entry1.getValue();
                                    if (!string3.equals(string4) && classMemberLookup1.isSubclass(string4, "java/util/ListResourceBundle")) {
                                        map1.put(string3, string4);
                                        this.checkClassMappingHashStrength(string3, string4, scriptEnvironment1);
                                    }
                                }
                            }
                        }
                    case 12:
                }
            }
        }
    }

    public Map buildClassLookupEntries(String string, Map map1) {
        HashMap hashMap = ZkmUtils.createHashMap();
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append((String) entry.getKey());
            stringBuilder.append(string);
            hashMap.put(stringBuilder.toString(), entry.getValue());
        }

        return hashMap;
    }

    public AutoReflectionHandler(ReflectionAccessMatcher reflectionAccessMatcher, List list1, ClassMemberLookup classMemberLookup1, String string, boolean randomizeNames) {
        this.accessMatcher = reflectionAccessMatcher;
        this.classMemberLookup = classMemberLookup1;
        this.digestAlgorithm = string;
        this.randomizeNames = randomizeNames;
        this.candidateNames = list1;
        ZkmAssert.assertTrue(true, new String[]{"MAX_RADIX is too small : 36 < 36"});
    }

    public void addChunkLoadInstructions(List list1, List list2, ResolvedMethodRefConstant resolvedMethodRefConstant, ConstantPool constantPool1, List list3) {
        Iterator iterator = list2.iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            Instruction[] instructions1 = this.createChunkLoadInstructions(string, resolvedMethodRefConstant, constantPool1, list3);
            list1.add(instructions1);
        }
    }

    public void encodeLookupChunks(List list1, List list2, Set set1, MessageDigest messageDigest1) throws DigestCollisionException, UnsupportedEncodingException {
        int ba = list1.size();
        int bb = 0;
        int bc = 0;
        StringBuilder stringBuilder = new StringBuilder();

        for (Iterator iterator = list1.iterator(); iterator.hasNext(); bb++) {
            Entry entry = (Entry) iterator.next();
            StringBuilder stringBuilder1 = new StringBuilder();
            String string = (String) entry.getKey();
            String string1 = (String) entry.getValue();
            String string2 = digestToBase36(string, messageDigest1);
            if (!set1.add(string2)) {
                throw new DigestCollisionException();
            }

            String string3 = this.encryptMappedName(string, string1, messageDigest1);
            stringBuilder1.append((char) string3.length());
            stringBuilder1.append(string3);
            stringBuilder1.append((char) string2.length());
            stringBuilder1.append(string2);
            int bd = ZkmUtils.getModifiedUtf8Length(stringBuilder1.toString());
            if (bd > 16384) {
                ZkmAssert.assertTrue(false, new String[]{string3.length() + " : " + bd, string3.substring(0, 100)});
            }

            if (bc + bd > 16384 || list2.size() == 0 && ba > 3 && bb == ba - 2) {
                list2.add(stringBuilder.toString());
                bc = 0;
                stringBuilder.setLength(0);
            }

            bc += bd;
            stringBuilder.append(stringBuilder1);
        }

        if (bc > 0) {
            list2.add(stringBuilder.toString());
        }
    }

    public Instruction[] createChunkLoadInstructions(String string, ResolvedMethodRefConstant resolvedMethodRefConstant, ConstantPool constantPool1, List list1) {
        ResolvedStringConstant resolvedStringConstant = constantPool1.addStringConstant(string, list1);
        return new Instruction[]{
                SimpleInstruction.forOpcode(89), new ConstantRefInstruction(19, resolvedStringConstant), new ConstantRefInstruction(184, resolvedMethodRefConstant)
        };
    }

    public void addMethodMappingsWithSubclasses(
            boolean bl,
            boolean bl1,
            String string,
            boolean bl2,
            String string1,
            MemberScope memberScope,
            String string2,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            Map map1,
            ClassMemberLookup classMemberLookup1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        this.addAccessibleMethodMappings(bl, bl1, string, bl2, string1, memberScope, string2, twoKeyMap, twoKeyMap1, map1, scriptEnvironment1);
        Iterator iterator = map1.keySet().iterator();

        while (iterator.hasNext()) {
            String string3 = (String) iterator.next();
            if (classMemberLookup1.isSubclass(string3, string2)) {
                this.addAccessibleMethodMappings(
                        bl, bl1, ClassFileBase.getPackagePath(string3), bl2, string1, memberScope, string3, twoKeyMap, twoKeyMap1, map1, scriptEnvironment1
                );
            }
        }
    }

    public void addAllFieldMappings(
            TwoKeyMap twoKeyMap, TwoKeyMap twoKeyMap1, boolean bl, String string, MemberScope memberScope, Map map1, ScriptEnvironment scriptEnvironment1
    ) throws IOException {
        Enumeration enumeration = twoKeyMap.keys();

        while (enumeration.hasMoreElements()) {
            String string1 = (String) enumeration.nextElement();
            ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string1);
            Map map2 = twoKeyMap.getInnerMap(string1);
            if (map2 != null) {
                Iterator iterator = map2.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    FieldSignature fieldSignature = (FieldSignature) entry.getKey();
                    FieldSignature fieldSignature1 = (FieldSignature) entry.getValue();
                    FieldInfo fieldInfo = programClass1.findFieldBySignature(fieldSignature1);
                    if (!fieldSignature1.getName().equals(fieldSignature.getName())
                            && (string == null || fieldSignature.getName().equals(string))
                            && (!bl || fieldInfo.isPublic())
                            && (
                            memberScope == MemberScope.BOTH
                                    || memberScope == MemberScope.INSTANCE && !fieldInfo.isStatic()
                                    || memberScope == MemberScope.STATIC && fieldInfo.isStatic()
                    )) {
                        twoKeyMap1.putValue(string1, fieldSignature, fieldSignature1);
                        this.checkFieldMappingHashStrength(string1, fieldSignature, fieldSignature1, map1, scriptEnvironment1);
                    }
                }
            }
        }
    }

    public Set createAutoReflectionClasses(
            Set set1,
            ObservableHolder observableHolder,
            Map map1,
            Map map2,
            ChangeLogMapping changeLogMapping1,
            String string,
            SetMultiMap setMultiMap,
            SetMultiMap setMultiMap1,
            ReadOnlyMultiMapView readOnlyMultiMapView,
            EnumerableMap enumerableMap,
            EnumerableMap enumerableMap1,
            EnumerableMap enumerableMap2,
            String string1,
            boolean bl,
            ClasspathClassLoader classpathClassLoader1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        Set set2 = set1;
        HashSet hashSet = ZkmUtils.createHashSet();
        HashSet hashSet1 = ZkmUtils.createHashSet();
        HashSet hashSet2 = ZkmUtils.createHashSet();
        set2 = LookupClassFactory.partitionClassesByModule(set2, hashSet1, hashSet2, hashSet);
        int ba = hashSet2.size();
        HashSet hashSet3 = ZkmUtils.createHashSet();
        Object object = null;
        HashSet hashSet4 = ZkmUtils.createHashSet();
        if (changeLogMapping1 != null) {
            String string2 = changeLogMapping1.getAutoReflectionClass();
            if (string2 != null) {
                String string3 = LookupClassFactory.validateChangeLogLookupName(
                        "AutoReflectionClass:", string2, "autoReflectionPackage", string, "Auto Reflection Handling", hashSet, changeLogMapping1, enumerableMap2
                );
                if (string3 != null) {
                    observableHolder.setValue(string3);
                    hashSet3.add(string3);
                    hashSet4.add(ZkmUtils.dotsToSlashes(string3));
                    Iterator iterator = hashSet1.iterator();

                    while (iterator.hasNext()) {
                        ProgramClass programClass1 = (ProgramClass) iterator.next();
                        map2.put(programClass1, string3);
                    }
                }
            }

            Iterator iterator2 = readOnlyMultiMapView.keySet().iterator();

            while (iterator2.hasNext()) {
                SourceArchive sourceArchive1 = (SourceArchive) iterator2.next();
                String string9 = sourceArchive1.getModuleName();
                String string4 = changeLogMapping1.getModuleAutoReflectionClass(string9);
                if (string4 != null) {
                    String string5 = LookupClassFactory.validateChangeLogLookupName(
                            "AutoReflectionClass:",
                            string4,
                            "autoReflectionPackage",
                            string,
                            "Auto Reflection Handling",
                            setMultiMap1.getValues(sourceArchive1),
                            changeLogMapping1,
                            enumerableMap2
                    );
                    if (string5 != null) {
                        map1.put(sourceArchive1, string5);
                        hashSet3.add(string5);
                        hashSet4.add(ZkmUtils.dotsToSlashes(string5));
                        Iterator iterator1 = readOnlyMultiMapView.getValues(sourceArchive1).iterator();

                        while (iterator1.hasNext()) {
                            ProgramClass programClass2 = (ProgramClass) iterator1.next();
                            map2.put(programClass2, string5);
                        }
                    }
                }
            }

            if ((ba == 0 || !observableHolder.isValueNull()) && readOnlyMultiMapView.getKeyCount() == map1.size()) {
                LookupClassFactory.assignClassesToLookupNames(observableHolder, map1, hashSet2, map2, readOnlyMultiMapView);
                return hashSet3;
            }

            if (ba > 0) {
                String string8 = changeLogMapping1.getReferenceObfuscationClass();
                if (string8 != null) {
                    hashSet4.add(ZkmUtils.dotsToSlashes(string8));
                }
            }

            iterator2 = readOnlyMultiMapView.keySet().iterator();

            while (iterator2.hasNext()) {
                SourceArchive sourceArchive2 = (SourceArchive) iterator2.next();
                String string10 = sourceArchive2.getModuleName();
                String string11 = changeLogMapping1.getModuleReferenceObfuscationClass(string10);
                if (string11 != null) {
                    hashSet4.add(ZkmUtils.dotsToSlashes(string11));
                }
            }
        }

        ClasspathClassLoader classpathClassLoader2 = classpathClassLoader1;
        List list1 = this.candidateNames;
        Boolean boolean2 = this.randomizeNames;
        Boolean boolean1 = bl;
        String string7 = string1;
        HashSet hashSet7 = hashSet4;
        EnumerableMap enumerableMap5 = enumerableMap2;
        EnumerableMap enumerableMap4 = enumerableMap1;
        EnumerableMap enumerableMap3 = enumerableMap;
        SetMultiMap setMultiMap3 = setMultiMap1;
        SetMultiMap setMultiMap2 = setMultiMap;
        ReadOnlyMultiMapView readOnlyMultiMapView1 = readOnlyMultiMapView;
        HashSet hashSet6 = hashSet2;
        HashSet hashSet5 = hashSet1;
        String string6 = string;
        Map map4 = map2;
        Map map3 = map1;
        ObservableHolder observableHolder1 = observableHolder;
        return LookupClassFactory.assignLookupClassNames(
                set2,
                (String) object,
                observableHolder1,
                map3,
                map4,
                "autoReflectionPackage",
                string6,
                hashSet5,
                hashSet6,
                readOnlyMultiMapView1,
                setMultiMap2,
                setMultiMap3,
                enumerableMap3,
                enumerableMap4,
                enumerableMap5,
                hashSet7,
                string7,
                boolean1,
                boolean2,
                list1,
                classpathClassLoader2,
                scriptEnvironment1
        );
    }

    public void addAccessibleMethodMappings(
            boolean bl,
            boolean bl1,
            Object object,
            boolean bl2,
            String string,
            MemberScope memberScope,
            String string1,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            Map map1,
            ScriptEnvironment scriptEnvironment1
    ) throws IOException {
        Map map2 = twoKeyMap.getInnerMap(string1);
        ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string1);
        if (map2 != null) {
            Iterator iterator = map2.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                MethodSignature methodSignature1 = (MethodSignature) entry.getKey();
                MethodSignature methodSignature2 = (MethodSignature) entry.getValue();
                if (!methodSignature2.getName().equals(methodSignature1.getName()) && (string == null || methodSignature1.getName().equals(string))) {
                    MethodInfo methodInfo1 = programClass1.findMethodBySignature(methodSignature2);
                    switch (ReflectionKindSwitchMap.MEMBER_SCOPE_SWITCH[memberScope.ordinal()]) {
                        case 1:
                            if (methodInfo1.isStatic()) {
                                continue;
                            }
                            break;
                        case 2:
                            if (!methodInfo1.isStatic()) {
                                continue;
                            }
                    }

                    if (methodInfo1.isPublic()
                            || bl && methodInfo1.isProtected()
                            || bl1 && methodInfo1.isPackagePrivate() && programClass1.getPackagePath().equals(object)
                            || bl2 && methodInfo1.isStrictlyPrivate()) {
                        twoKeyMap1.putValue(string1, methodSignature1, methodSignature2);
                        this.checkMethodMappingHashStrength(string1, methodSignature1, methodSignature2, map1, scriptEnvironment1);
                    }
                }
            }
        }
    }

    @Override
    public ProgramClass createSyntheticClass(String string, int ba, int bb, int bc, Object object) throws ZkmException {
        int bd = ba;
        if (HiddenOptionFlags.AUTO_REFLECTION_OVERRIDE_INDEX != null && HiddenOptionFlags.AUTO_REFLECTION_OVERRIDE_LENGTH != null) {
            try {
                bd = Integer.parseInt(HiddenOptionFlags.AUTO_REFLECTION_OVERRIDE_INDEX);
            } catch (NumberFormatException numberFormatException1) {
            }
        }

        int be;
        label54:
        {
            be = bc;
            String string2 = HiddenOptionFlags.AUTO_REFLECTION_OVERRIDE_INDEX;
            if (bb >= 0) {
                if (HiddenOptionFlags.AUTO_REFLECTION_OVERRIDE_INDEX == null) {
                    break label54;
                }

                string2 = HiddenOptionFlags.AUTO_REFLECTION_OVERRIDE_LENGTH;
            }

            if (string2 != null) {
                try {
                    be = Integer.parseInt(HiddenOptionFlags.AUTO_REFLECTION_OVERRIDE_LENGTH);
                } catch (NumberFormatException numberFormatException) {
                }
            }
        }

        try {
            byte[] bf;
            label47:
            {
                byte[] bg;
                if (bd >= 49) {
                    bf = ReflectionLookupTemplate.JAVA5_LOOKUP_CLASS_BYTES;
                    if (bb > 0) {
                        break label47;
                    }

                    bg = ReflectionLookupTemplate.LEGACY_LOOKUP_CLASS_BYTES;
                } else {
                    bg = ReflectionLookupTemplate.LEGACY_LOOKUP_CLASS_BYTES;
                }

                bf = bg;
            }

            ClassFileInputStream classFileInputStream = ClassFileInputStream.fromBytes(bf, false);
            InputFileLocation inputFileLocation = InputFileLocation.createNamedPlaceholder("AutoReflection." + string);
            ObservableHolder observableHolder = new ObservableHolder();
            ListenerRegistry listenerRegistry1 = new ListenerRegistry();
            PrintWriter printWriter = new PrintWriter(new StringWriter());
            new PrintWriter(new StringWriter());
            ThreeKeyMultiMap threeKeyMultiMap = new ThreeKeyMultiMap();
            ProgramClass programClass1 = new ProgramClass(
                    classFileInputStream, inputFileLocation, observableHolder, listenerRegistry1, printWriter, threeKeyMultiMap, 9
            );
            Object[] objects = new Object[]{null, 109121987136612L};
            objects[0] = 9;
            programClass1.setMethodsCreationKind((Integer) objects[0]);
            ProgramClass programClass2;
            byte bi;
            String string3;
            if (!this.digestAlgorithm.equals("SHA-512")) {
                programClass1.replaceStringConstant("SHA-512", this.digestAlgorithm);
                programClass2 = programClass1;
                long bj = 14612748829929L;
                string3 = string;
                bi = 13;
            } else {
                programClass2 = programClass1;
                long bh = 14612748829929L;
                string3 = string;
                bi = 13;
            }

            HashMap hashMap = ZkmUtils.createHashMap(bi);
            String string1 = string3;
            programClass2.renameClass(string1, hashMap);
            programClass1.setMajorVersion(bd);
            Object[] objects1 = new Object[]{null, 118477269378260L};
            objects1[0] = be;
            programClass1.setMinorVersion((Integer) objects1[0]);
            return programClass1;
        } catch (IOException iOException) {
            throw new AutoReflectionException("AutoReflection (A) : " + iOException.getMessage(), iOException);
        } catch (ZkmProcessingException zkmProcessingException) {
            throw new AutoReflectionException("AutoReflection (B) : " + zkmProcessingException.getMessage(), zkmProcessingException);
        }
    }

    public void addAllMethodMappings(
            TwoKeyMap twoKeyMap, TwoKeyMap twoKeyMap1, boolean bl, String string, MemberScope memberScope, Map map1, ScriptEnvironment scriptEnvironment1
    ) throws IOException {
        Enumeration enumeration = twoKeyMap.keys();

        while (enumeration.hasMoreElements()) {
            String string1 = (String) enumeration.nextElement();
            ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string1);
            Map map2 = twoKeyMap.getInnerMap(string1);
            if (map2 != null) {
                Iterator iterator = map2.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    MethodSignature methodSignature1 = (MethodSignature) entry.getKey();
                    MethodSignature methodSignature2 = (MethodSignature) entry.getValue();
                    MethodInfo methodInfo1 = programClass1.findMethodBySignature(methodSignature2);
                    if (!methodSignature2.getName().equals(methodSignature1.getName())
                            && (!bl || methodInfo1.isPublic())
                            && (string == null || methodSignature1.getName().equals(string))
                            && (
                            memberScope == MemberScope.BOTH
                                    || memberScope == MemberScope.INSTANCE && !methodInfo1.isStatic()
                                    || memberScope == MemberScope.STATIC && methodInfo1.isStatic()
                    )) {
                        twoKeyMap1.putValue(string1, methodSignature1, methodSignature2);
                        this.checkMethodMappingHashStrength(string1, methodSignature1, methodSignature2, map1, scriptEnvironment1);
                    }
                }
            }
        }
    }

    public Map buildMethodLookupEntries(String string, TwoKeyMap twoKeyMap) {
        HashMap hashMap = ZkmUtils.createHashMap();
        Enumeration enumeration = twoKeyMap.keys();

        while (enumeration.hasMoreElements()) {
            String string1 = (String) enumeration.nextElement();
            Iterator iterator = twoKeyMap.getInnerMap(string1).entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                MethodSignature methodSignature1 = (MethodSignature) entry.getKey();
                MethodSignature methodSignature2 = (MethodSignature) entry.getValue();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(string1);
                stringBuilder.append(string);
                stringBuilder.append(methodSignature1.getName());
                stringBuilder.append(string);
                Iterator iterator1 = ConstantPoolEntry.getParameterTypes(methodSignature2.getDescriptor()).iterator();

                while (iterator1.hasNext()) {
                    String string2 = (String) iterator1.next();
                    if (string2.startsWith("L") && string2.endsWith(";")) {
                        string2 = string2.substring(1, string2.length() - 1);
                    }

                    stringBuilder.append(string2);
                    stringBuilder.append(string);
                }

                hashMap.put(stringBuilder.toString(), methodSignature2.getName());
            }
        }

        return hashMap;
    }

    public void addFieldMappingsWithSubclasses(
            boolean bl,
            boolean bl1,
            String string,
            boolean bl2,
            String string1,
            MemberScope memberScope,
            String string2,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            Map map1,
            ClassMemberLookup classMemberLookup1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        this.addAccessibleFieldMappings(bl, bl1, string, bl2, string1, memberScope, string2, twoKeyMap, twoKeyMap1, map1, scriptEnvironment1);
        Iterator iterator = map1.keySet().iterator();

        while (iterator.hasNext()) {
            String string3 = (String) iterator.next();
            if (classMemberLookup1.isSubclass(string3, string2)) {
                this.addAccessibleFieldMappings(
                        bl, bl1, ClassFileBase.getPackagePath(string3), bl2, string1, memberScope, string3, twoKeyMap, twoKeyMap1, map1, scriptEnvironment1
                );
            }
        }
    }

    public void addMethodMappingsWithSupertypes(
            boolean bl,
            boolean bl1,
            String string,
            boolean bl2,
            String string1,
            MemberScope memberScope,
            String string2,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            Map map1,
            ScriptEnvironment scriptEnvironment1
    ) throws IOException {
        this.addAccessibleMethodMappings(bl, bl1, string, bl2, string1, memberScope, string2, twoKeyMap, twoKeyMap1, map1, scriptEnvironment1);
        ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string2);
        if (programClass1 != null) {
            String string3 = programClass1.getSuperclassName();
            ProgramClass programClass2 = ClassHierarchyNode.findProgramClass(string3);
            if (programClass2 != null && programClass2.isProgramClass()) {
                this.addMethodMappingsWithSupertypes(bl, bl1, string, false, string1, memberScope, string3, twoKeyMap, twoKeyMap1, map1, scriptEnvironment1);
            }

            for (String string4 : programClass1.getInterfaceNames()) {
                ProgramClass programClass3 = ClassHierarchyNode.findProgramClass(string4);
                if (programClass3 != null && programClass3.isProgramClass()) {
                    this.addMethodMappingsWithSupertypes(bl, bl1, string, false, string1, memberScope, string4, twoKeyMap, twoKeyMap1, map1, scriptEnvironment1);
                }
            }
        }
    }

    public void addFieldMappingsWithSupertypes(
            boolean bl,
            boolean bl1,
            String string,
            boolean bl2,
            String string1,
            MemberScope memberScope,
            String string2,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            Map map1,
            ScriptEnvironment scriptEnvironment1
    ) throws IOException {
        this.addAccessibleFieldMappings(bl, bl1, string, bl2, string1, memberScope, string2, twoKeyMap, twoKeyMap1, map1, scriptEnvironment1);
        ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string2);
        if (programClass1 != null) {
            String string3 = programClass1.getSuperclassName();
            ProgramClass programClass2 = ClassHierarchyNode.findProgramClass(string3);
            if (programClass2 != null && programClass2.isProgramClass()) {
                this.addFieldMappingsWithSupertypes(bl, bl1, string, false, string1, memberScope, string3, twoKeyMap, twoKeyMap1, map1, scriptEnvironment1);
            }

            for (String string4 : programClass1.getInterfaceNames()) {
                ProgramClass programClass3 = ClassHierarchyNode.findProgramClass(string4);
                if (programClass3 != null && programClass3.isProgramClass()) {
                    this.addFieldMappingsWithSupertypes(bl, bl1, string, false, string1, memberScope, string4, twoKeyMap, twoKeyMap1, map1, scriptEnvironment1);
                }
            }
        }
    }

    public void addMethodMappingsForHierarchy(
            String string,
            String string1,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1,
            Map map1,
            ClassMemberLookup classMemberLookup1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        Map map2 = map1;
        TwoKeyMap twoKeyMap3 = twoKeyMap1;
        TwoKeyMap twoKeyMap2 = twoKeyMap;
        String string3 = string;
        MemberScope memberScope = MemberScope.BOTH;
        String string2 = string1;
        Boolean boolean1 = false;
        this.addMethodMappingsWithSupertypes(
                false, false, (String) null, boolean1, string2, memberScope, string3, twoKeyMap2, twoKeyMap3, map2, scriptEnvironment1
        );
        ClassMemberLookup classMemberLookup2 = classMemberLookup1;
        Map map3 = map1;
        TwoKeyMap twoKeyMap5 = twoKeyMap1;
        TwoKeyMap twoKeyMap4 = twoKeyMap;
        String string5 = string;
        MemberScope memberScope1 = MemberScope.BOTH;
        String string4 = string1;
        Boolean boolean2 = false;
        this.addMethodMappingsWithSubclasses(
                false, false, (String) null, boolean2, string4, memberScope1, string5, twoKeyMap4, twoKeyMap5, map3, classMemberLookup2, scriptEnvironment1
        );
    }

    public void collectAccessedMappings(
            EnumerableMap enumerableMap,
            ReadOnlyMultiMap readOnlyMultiMap,
            ReadOnlyMultiMap readOnlyMultiMap1,
            Map map1,
            Map map2,
            TwoKeyMap twoKeyMap,
            TwoKeyMap twoKeyMap1
    ) {
        Iterator iterator = enumerableMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string = (String) entry.getKey();
            String string1 = (String) entry.getValue();
            if (this.accessMatcher == null || this.accessMatcher.isClassNameExcluded(string1)) {
                map1.put(string, string1);
                map2.put(string1, string);
            }
        }

        Enumeration enumeration = readOnlyMultiMap.keys();

        while (enumeration.hasMoreElements()) {
            ClassFileBase classFileBase = (ClassFileBase) enumeration.nextElement();
            String string2 = classFileBase.getClassName();
            EnumerableMap enumerableMap1 = readOnlyMultiMap.getInnerMap(classFileBase);
            Iterator iterator1 = enumerableMap1.entrySet().iterator();

            while (iterator1.hasNext()) {
                Entry entry1 = (Entry) iterator1.next();
                FieldSignature fieldSignature = (FieldSignature) entry1.getKey();
                FieldSignature fieldSignature1 = (FieldSignature) entry1.getValue();
                FieldInfo fieldInfo = this.classMemberLookup.findField(ZkmUtils.dotsToSlashes(string2), fieldSignature1.getName(), fieldSignature1.getDescriptor());
                Object[] objects = new Object[]{string2, fieldSignature, fieldSignature1};
                ZkmAssert.assertNotNullArgs(fieldInfo, objects);
                if (this.accessMatcher == null || this.accessMatcher.isFieldExcluded(fieldInfo)) {
                    twoKeyMap.putValue(string2, fieldSignature, fieldSignature1);
                }
            }
        }

        Enumeration enumeration1 = readOnlyMultiMap1.keys();

        while (enumeration1.hasMoreElements()) {
            ClassFileBase classFileBase1 = (ClassFileBase) enumeration1.nextElement();
            String string3 = classFileBase1.getClassName();
            if (classFileBase1.isProgramClass()) {
                EnumerableMap enumerableMap2 = readOnlyMultiMap1.getInnerMap(classFileBase1);
                Iterator iterator2 = enumerableMap2.entrySet().iterator();

                while (iterator2.hasNext()) {
                    Entry entry2 = (Entry) iterator2.next();
                    MethodSignature methodSignature1 = (MethodSignature) entry2.getKey();
                    MethodSignature methodSignature2 = (MethodSignature) entry2.getValue();
                    MethodInfo methodInfo1 = this.classMemberLookup.findDeclaredMethod((ProgramClass) classFileBase1, methodSignature2);
                    Object[] objects1 = new Object[]{classFileBase1, string3, methodSignature1, methodSignature2};
                    ZkmAssert.assertNotNullArgs(methodInfo1, objects1);
                    if (this.accessMatcher == null || this.accessMatcher.isMethodExcluded(methodInfo1)) {
                        twoKeyMap1.putValue(string3, methodSignature1, methodSignature2);
                    }
                }
            }
        }
    }

    public String generateUniqueDecoyKey(Random random1, Set set1) {
        char[] bc = ReflectionLookupTemplate.DECOY_KEY_PAD_CHARS;

        while (true) {
            char ba = bc[random1.nextInt(ReflectionLookupTemplate.DECOY_KEY_PAD_CHARS.length)];
            String string1 = Long.toString(random1.nextLong(), 36);
            byte bd;
            byte be;
            if (random1.nextBoolean()) {
                bd = 82;
                be = 17;
            } else {
                bd = 76;
                be = 17;
            }

            String string = ZkmStringUtils.pad(string1, bd, Math.max(be, string1.length() + 1), ba);
            char[] bb = string.toCharArray();
            ZkmUtils.shuffleChars(bb);
            string = new String(bb);
            if (set1.add(string)) {
                return string;
            }

            bc = ReflectionLookupTemplate.DECOY_KEY_PAD_CHARS;
        }
    }
}
