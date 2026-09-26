package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.attribute.AnnotationValueMap;
import com.zelix.klassmaster.classfile.attribute.AnnotationsAttribute;
import com.zelix.klassmaster.classfile.attribute.Attribute;
import com.zelix.klassmaster.classfile.attribute.BootstrapMethodIndex;
import com.zelix.klassmaster.classfile.attribute.BridgeAttribute;
import com.zelix.klassmaster.classfile.attribute.CodeAttributeBody;
import com.zelix.klassmaster.classfile.attribute.ExceptionsAttribute;
import com.zelix.klassmaster.classfile.attribute.MethodParametersAttribute;
import com.zelix.klassmaster.classfile.attribute.ReferencingAttribute;
import com.zelix.klassmaster.classfile.attribute.RuntimeInvisibleAnnotationsAttribute;
import com.zelix.klassmaster.classfile.attribute.SignatureAttribute;
import com.zelix.klassmaster.classfile.attribute.TypeAnnotationsAttribute;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ConstantUtf8;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyNode;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassLoader;
import com.zelix.klassmaster.classfile.hierarchy.CommonSuperTypeResolver;
import com.zelix.klassmaster.classfile.hierarchy.MethodOverrideAnalyzer;
import com.zelix.klassmaster.classfile.insn.CodeInsertion;
import com.zelix.klassmaster.classfile.insn.ExceptionHandlerSpec;
import com.zelix.klassmaster.classfile.insn.Instruction;
import com.zelix.klassmaster.classfile.insn.IntCounter;
import com.zelix.klassmaster.classfile.insn.LabelInstruction;
import com.zelix.klassmaster.classfile.insn.LocalVariableIndex;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;
import com.zelix.klassmaster.classfile.insn.MethodBytecode;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.config.TrimOptions;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.obfuscator.constants.IntegerEncryptionExclusions;
import com.zelix.klassmaster.obfuscator.constants.LongEncryptionExclusionHandler;
import com.zelix.klassmaster.obfuscator.exclude.AbstractExclusionSpec;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.obfuscator.flow.OpaquePredicateField;
import com.zelix.klassmaster.obfuscator.flow.StaticInitCalleeAnalyzer;
import com.zelix.klassmaster.obfuscator.parameters.AddedParameter;
import com.zelix.klassmaster.obfuscator.parameters.ChangedMethodDescriptor;
import com.zelix.klassmaster.obfuscator.parameters.MethodParamChangeNode;
import com.zelix.klassmaster.obfuscator.parameters.MethodParameterChangeSet;
import com.zelix.klassmaster.obfuscator.parameters.ParameterListGenerator;
import com.zelix.klassmaster.obfuscator.references.ReferenceObfuscator;
import com.zelix.klassmaster.obfuscator.rename.MethodNameGeneratorBase;
import com.zelix.klassmaster.obfuscator.rename.MethodRenamer;
import com.zelix.klassmaster.obfuscator.string.StringEncryptionExclusionSpec;
import com.zelix.klassmaster.obfuscator.trim.TrimProcessor;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.DisableableMap;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.MultiMapTable;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.NestedMultiMap;
import com.zelix.klassmaster.util.NodeVisitor;
import com.zelix.klassmaster.util.NumericStringUtil;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ObservableModel;
import com.zelix.klassmaster.util.PairValueMap;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ThreeKeyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.TwoKeySetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MethodInfo extends AbstractMethodInfo implements NodeVisitor, MethodChangeListener {
    public LabelInstruction initializerEndLabel;
    public AnnotationValueMap zkmAnnotationValues;

    public void setMaxLocals(int ba) throws ZkmProcessingException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).setMaxLocals(ba);
        }
    }

    public void applyIntegerEncryption(
            List list1,
            Long long1,
            LocalVariableIndex localVariableIndex1,
            IntCounter intCounter,
            ConstantPool constantPool1,
            List list2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .applyIntegerEncryption(
                            list1, this.isStaticInitializer(), long1, localVariableIndex1, intCounter, constantPool1, list2, commonSuperTypeResolver1, classHierarchyQuery
                    );
        }
    }

    public int getMaxLocals() {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).getMaxLocals() : 0;
    }

    public void insertInstructionsAtStart(List list1, int ba, String string) throws ZkmProcessingException {
        this.insertInstructions(list1, 0, ba, 0, string, (List) null);
    }

    @Override
    public final void trimAttributes(TrimProcessor trimProcessor1, TrimOptions trimOptions1, ScriptEnvironment scriptEnvironment1, PrintWriter printWriter) throws ZkmException, IOException {
        super.trimAttributes(trimProcessor1, trimOptions1, scriptEnvironment1, printWriter);
        if (trimOptions1.e && this.exceptionsAttributeIndex != -1) {
            for (int i = 0; i < this.attributeCount; i++) {
                if (this.attributes[i] instanceof TypeAnnotationsAttribute) {
                    ((TypeAnnotationsAttribute) this.attributes[i]).removeThrowsAnnotations();
                }
            }

            ArrayList arrayList = new ArrayList();

            for (Attribute attribute : this.attributes) {
                if (!(attribute instanceof ExceptionsAttribute)) {
                    if (attribute instanceof TypeAnnotationsAttribute) {
                        if (!((TypeAnnotationsAttribute) attribute).isEmpty()) {
                            arrayList.add(attribute);
                        }
                    } else {
                        arrayList.add(attribute);
                    }
                }
            }

            if (arrayList.size() < this.attributeCount) {
                this.attributes = ((com.zelix.klassmaster.classfile.attribute.Attribute[]) (arrayList.toArray(new Attribute[arrayList.size()])));
                this.attributeCount = this.attributes.length;
                this.onAttributesChanged();
            }
        }
    }

    public void printCode(PrintWriter printWriter) throws ZkmProcessingException {
        if (this.codeAttributeIndex_int != -1) {
            CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.attributes[this.codeAttributeIndex_int];
            codeAttributeBody.printInstructions(printWriter);
            codeAttributeBody.printExceptionTable(printWriter);
        }
    }

    public int getMaxStack() {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).getMaxStack() : 0;
    }

    public void collectSerialPersistentFields(List list1, ClassMemberLookup classMemberLookup1) throws ZkmProcessingException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectSerialPersistentFields(list1, classMemberLookup1);
        } else {
            throw new ZkmProcessingException("No Code attribute  found.");
        }
    }

    @Override
    public String toDisplayString() {
        return this.buildDeclaration(false);
    }

    @Override
    public final void updateAttributesAfterFieldRename() {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[i]).applyFieldRenames();
            } else if (this.attributes[i] instanceof CodeAttributeBody) {
                ((CodeAttributeBody) this.attributes[i]).updateAttributesAfterFieldRename();
            }
        }
    }

    public void obfuscateParameterNames() {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof MethodParametersAttribute) {
                ((MethodParametersAttribute) this.attributes[i]).obfuscateNames();
            }
        }
    }

    public boolean hasLineNumber(int ba) {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).hasLineNumber(ba) : false;
    }

    public void changeParameterList(
            ChangeLogMapping changeLogMapping1,
            MethodParameterChangeSet methodParameterChangeSet,
            ParameterListGenerator parameterListGenerator,
            MethodOverrideAnalyzer methodOverrideAnalyzer,
            SetMultiMap setMultiMap,
            ClassHierarchyNode classHierarchyNode,
            boolean bl,
            Map map1,
            Map map2,
            boolean bl1,
            Map map3
    ) throws ZkmException, IOException {
        this.getSignature();
        ChangedMethodDescriptor changedMethodDescriptor = null;
        ProgramClass programClass1 = classHierarchyNode.getProgramClass();
        boolean bl2 = false;
        if (changeLogMapping1 != null && changeLogMapping1.hasAddedParametersEntry(this)) {
            bl2 = true;
            AbstractMethodInfo abstractMethodInfo = methodOverrideAnalyzer.getRootMethod(this);
            if (changeLogMapping1.hasAddedParameters(abstractMethodInfo)) {
                AddedParameter[] addedParameters1 = changeLogMapping1.getAddedParameters(abstractMethodInfo);
                changedMethodDescriptor = new ChangedMethodDescriptor(this.getDescriptor(), addedParameters1);
                if (!this.isConstructor()) {
                    String string = changedMethodDescriptor.getDescriptor();
                    MemberSignatureBase memberSignatureBase = bl1
                            ? new MethodSignature(this.methodName, string)
                            : new FieldNameTypeSignature(this.methodName, string);
                    Set set1 = setMultiMap.getValues(memberSignatureBase);
                    if (set1 != null && !set1.isEmpty()) {
                        Iterator iterator = set1.iterator();

                        while (iterator.hasNext()) {
                            AbstractMethodInfo abstractMethodInfo1 = (AbstractMethodInfo) iterator.next();
                            if (abstractMethodInfo1.isManufactured()) {
                                bl2 = false;
                                changeLogMapping1.removeAddedParameters(abstractMethodInfo);
                                changeLogMapping1.logWarning(
                                        "Method '"
                                                + this.toOriginalDisplayString()
                                                + "' in class '"
                                                + this.getOriginalDottedName()
                                                + "' could not be changed to '"
                                                + memberSignatureBase
                                                + "' because of existing method '"
                                                + abstractMethodInfo1.toOriginalDisplayString()
                                                + "' in class '"
                                                + abstractMethodInfo1.getOriginalDottedName()
                                                + "'. You may need to distribute this application as a whole. (C)"
                                );
                            } else {
                                if (abstractMethodInfo1.isStrictlyPrivate() || abstractMethodInfo1.isStatic()) {
                                    continue;
                                }

                                if (!this.isStrictlyPrivate() && !this.isStatic()) {
                                    AbstractMethodInfo abstractMethodInfo2 = methodOverrideAnalyzer.getRootMethod(abstractMethodInfo1);
                                    if (abstractMethodInfo != abstractMethodInfo2) {
                                        bl2 = false;
                                        changeLogMapping1.removeAddedParameters(abstractMethodInfo);
                                        changeLogMapping1.logWarning(
                                                "Method '"
                                                        + this.toOriginalDisplayString()
                                                        + "' in class '"
                                                        + this.getOriginalDottedName()
                                                        + "' could not be changed to '"
                                                        + memberSignatureBase
                                                        + "' because of existing method '"
                                                        + abstractMethodInfo1.toOriginalDisplayString()
                                                        + "' in class '"
                                                        + abstractMethodInfo1.getOriginalDottedName()
                                                        + "'"
                                                        + (
                                                        !abstractMethodInfo1.isRenamed() && !abstractMethodInfo1.isDescriptorChanged()
                                                                ? ""
                                                                : " which which has been changed to '" + abstractMethodInfo1.getSignature() + "'"
                                                )
                                                        + ". You may need to distribute this application as a whole. (D)"
                                        );
                                    }
                                }
                            }

                            ClassFileBase classFileBase = programClass1.findInheritedMethodReferrer(abstractMethodInfo1);
                            if (classFileBase != null) {
                                bl2 = false;
                                changeLogMapping1.removeAddedParameters(abstractMethodInfo);
                                changeLogMapping1.logWarning(
                                        "Method '"
                                                + this.toOriginalDisplayString()
                                                + "' in class '"
                                                + this.getOriginalDottedName()
                                                + "' could not be changed to '"
                                                + memberSignatureBase
                                                + "' because of symbolic references to method '"
                                                + abstractMethodInfo1.toOriginalDisplayString()
                                                + "' in class '"
                                                + this.getOriginalDottedName()
                                                + "' that appear in '"
                                                + classFileBase.getOriginalDottedName()
                                                + "'. You may need to distribute this application as a whole. (E)"
                                );
                            }
                        }
                    }
                }
            }
        }

        if (!bl2) {
            if (!this.isStatic()) {
                if (!this.isAbstract() && this.isStrictlyPrivate()) {
                    changedMethodDescriptor = parameterListGenerator.generateForPrivateMethod(
                            classHierarchyNode, this, setMultiMap, map1, map2, this.isNameChanged(), bl1
                    );
                } else {
                    changedMethodDescriptor = parameterListGenerator.generateForVirtualMethod(
                            classHierarchyNode, this, setMultiMap, bl, map1, map2, this.isNameChanged(), bl1
                    );
                }
            } else if (!map3.containsKey(this) && (!this.isPublic() || !this.isStatic() || !this.getSignature().equals(MethodSignature.MAIN_METHOD))) {
                changedMethodDescriptor = parameterListGenerator.generateForStaticMethod(
                        classHierarchyNode, this, setMultiMap, map1, map2, this.isNameChanged(), bl1
                );
            }
        }

        MethodSignature methodSignature1 = methodParameterChangeSet.recordChangedDescriptor(this, changedMethodDescriptor);
        if (changedMethodDescriptor != null && !changedMethodDescriptor.getDescriptor().equals(this.getDescriptor())) {
            setMultiMap.addValue(bl1 ? methodSignature1 : methodSignature1.getNameTypeSignature(), this);
            map1.put(methodSignature1, this);
            map2.put(methodSignature1.getNameTypeSignature(), this);
            this.setDescriptor(changedMethodDescriptor.getDescriptor());
            this.setHasChangedParameters();
        } else {
            setMultiMap.addValue(methodSignature1, this);
            map1.put(methodSignature1, this);
            map2.put(methodSignature1.getNameTypeSignature(), this);
        }
    }

    public int getParameterIndexForSlot(int ba) {
        int bb = 0;
        List list1 = ConstantPoolEntry.getParameterTypes(this.descriptor);
        int bc = list1.size();

        for (int i = 0; i < bc; i++) {
            if (bb == ba) {
                return i;
            }

            String string = (String) list1.get(i);
            if (!string.equals("D") && !string.equals("J")) {
                bb++;
            } else {
                bb += 2;
            }
        }

        return -1;
    }

    public void collectLineNumberEntries(ArrayList arrayList) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectLineNumberEntries(arrayList);
        }
    }

    public void analyzeFlow(CommonSuperTypeResolver commonSuperTypeResolver1, ClassHierarchyQuery classHierarchyQuery, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).analyzeFlow(commonSuperTypeResolver1, classHierarchyQuery, scriptEnvironment1);
        }
    }

    public void setExceptionHandlers(ExceptionHandlerSpec[] exceptionHandlerSpecs) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).setExceptionHandlers(exceptionHandlerSpecs);
        }
    }

    public void applyParameterChanges(
            MethodParamChangeNode methodParamChangeNode,
            Map map1,
            Map map2,
            Map map3,
            Set set1,
            Map map4,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery,
            List list1,
            ConstantPool constantPool1,
            MethodOverrideAnalyzer methodOverrideAnalyzer,
            Set set2
    ) throws ZkmException, IOException {
        if (this.codeAttributeIndex_int != -1) {
            boolean bl = set1.contains(this);
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .applyParameterChanges(
                            methodParamChangeNode,
                            map1,
                            map2,
                            map3,
                            bl,
                            map4,
                            commonSuperTypeResolver1,
                            classHierarchyQuery,
                            list1,
                            constantPool1,
                            methodOverrideAnalyzer,
                            set2
                    );
        }
    }

    public boolean hasLocalVariableTable() {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).hasLocalVariableTable() : false;
    }

    public void pruneTypeAnnotationTargets(HashSet hashSet, SetMultiMap setMultiMap) {
        for (int i = 0; i < this.attributeCount; i++) {
            if (this.attributes[i] instanceof TypeAnnotationsAttribute) {
                ((TypeAnnotationsAttribute) this.attributes[i]).removeAnnotationsAtLabels(hashSet, setMultiMap);
            }
        }
    }

    public void applyOpaquePredicateFlow(
            Set set1,
            Set set2,
            OpaquePredicateField opaquePredicateField,
            OpaquePredicateField opaquePredicateField1,
            NestedMultiMap nestedMultiMap,
            ArrayList arrayList,
            ConstantPool constantPool1,
            ListMultimap listMultimap,
            Map map1,
            ClassMemberLookup classMemberLookup1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassResolver classResolver1,
            Random random1
    ) throws ZkmException, IOException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .applyOpaquePredicateFlow(
                            set1,
                            set2,
                            opaquePredicateField,
                            opaquePredicateField1,
                            nestedMultiMap,
                            arrayList,
                            constantPool1,
                            listMultimap,
                            map1,
                            classMemberLookup1,
                            commonSuperTypeResolver1,
                            classResolver1,
                            random1
                    );
        }
    }

    public void collectCallGraph(SetMultiMap setMultiMap, Set set1) {
        set1.add(this);
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectCallGraph(setMultiMap, set1);
        }
    }

    public LabelInstruction getInitializerEndLabel() {
        return this.initializerEndLabel;
    }

    public int getCallStackOffset() {
        int ba = 0;
        if (!this.isStatic()) {
            ba++;
        }

        if (!ConstantPoolEntry.getReturnDescriptor(this.descriptor).equals("V")) {
            ba += -1;
        }

        return ba;
    }

    public MethodInfo(ClassFileBase classFileBase, ConstantUtf8 constantUtf8, ConstantUtf8 constantUtf81, Attribute[] attributes1, int ba) {
        super(classFileBase, constantUtf8, constantUtf81, attributes1, ba);

        for (int i = 0; i < attributes1.length; i++) {
            if (attributes1[i] instanceof CodeAttributeBody) {
                this.codeAttributeIndex_int = i;
            } else if (attributes1[i] instanceof ExceptionsAttribute) {
                this.exceptionsAttributeIndex = i;
            }

            attributes1[i].setParent(this);
        }

        this.parameterTypes = ConstantPoolEntry.getParameterTypes(this.descriptor);
    }

    public int indexOfInstruction(Instruction instruction1) {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).indexOfInstruction(instruction1) : -1;
    }

    public boolean removeDeadCode(
            CommonSuperTypeResolver commonSuperTypeResolver1, ClassMemberLookup classMemberLookup1, boolean bl, ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        boolean bl1 = false;
        if (this.codeAttributeIndex_int != -1) {
            bl1 = ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .removeDeadCode(commonSuperTypeResolver1, classMemberLookup1, bl, scriptEnvironment1);
        }

        return bl1;
    }

    public boolean hasIntegerConstants() {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).hasIntegerConstants() : false;
    }

    public ConstantPoolEntry getFirstLoadedConstant() {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).getFirstLoadedConstant() : null;
    }

    public void collectMethodInvokeOpcodes(HashMap hashMap) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectMethodInvokeOpcodes(hashMap);
        }
    }

    public void indexMemberReferences(NestedMultiMap nestedMultiMap, NestedMultiMap nestedMultiMap1, NestedMultiMap nestedMultiMap2, IntegerCache integerCache1) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .indexMemberReferences(nestedMultiMap, nestedMultiMap1, nestedMultiMap2, integerCache1);
        }
    }

    public void addOpaquePredicateInitialization(
            OpaquePredicateField opaquePredicateField,
            NestedMultiMap nestedMultiMap,
            List list1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassMemberLookup classMemberLookup1,
            StaticInitCalleeAnalyzer staticInitCalleeAnalyzer1,
            Random random1
    ) throws ZkmException, IOException {
        ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                .addOpaquePredicateInitialization(
                        opaquePredicateField, nestedMultiMap, list1, commonSuperTypeResolver1, classMemberLookup1, staticInitCalleeAnalyzer1, random1
                );
    }

    public void trimLocalVariableTables(boolean bl, boolean bl1) {
        if (this.codeAttributeIndex_int != -1) {
            CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.attributes[this.codeAttributeIndex_int];
            if (bl && !this.isPublic() && !this.isProtected()) {
                codeAttributeBody.removeLocalVariableTables();
            } else if (bl1) {
                if (!this.isRenamed()) {
                    codeAttributeBody.retainParameterLocalVariables();
                } else {
                    codeAttributeBody.removeLocalVariableTables();
                }
            } else {
                codeAttributeBody.retainParameterLocalVariables();
            }
        }
    }

    public void removeZkmMethodAnnotation(ScriptEnvironment scriptEnvironment1) {
        RuntimeInvisibleAnnotationsAttribute runtimeInvisibleAnnotationsAttribute = this.zkmAnnotationValues.getSourceAttribute();
        ArrayList arrayList = new ArrayList(this.attributeCount);

        for (Attribute attribute : this.attributes) {
            if (attribute == runtimeInvisibleAnnotationsAttribute) {
                runtimeInvisibleAnnotationsAttribute.removeAnnotationsOfType("com/zelix/annotation/ZKMMethodLevel", scriptEnvironment1);
                if (runtimeInvisibleAnnotationsAttribute.getAnnotationCount() > 0) {
                    arrayList.add(attribute);
                }
            } else {
                arrayList.add(attribute);
            }
        }

        if (arrayList.size() < this.attributeCount) {
            this.attributes = ((com.zelix.klassmaster.classfile.attribute.Attribute[]) (arrayList.toArray(new Attribute[arrayList.size()])));
            this.attributeCount = this.attributes.length;
            this.onAttributesChanged();
        }
    }

    public MethodInfo(
            ClassFileComponent classFileComponent,
            ClassFileInputStream classFileInputStream,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3,
            ListMultimap listMultimap4,
            ListMultimap listMultimap5,
            ListMultimap listMultimap6,
            ListMultimap listMultimap7,
            PrintWriter printWriter,
            ThreeKeyMultiMap threeKeyMultiMap
    ) throws ZkmProcessingException, IOException {
        super(classFileComponent, classFileInputStream, listMultimap, printWriter);
        this.attributes = new Attribute[this.attributeCount];
        ListMultimap listMultimap8 = new ListMultimap();
        CodeAttributeBody codeAttributeBody = null;

        for (int i = 0; i < this.attributes.length; i++) {
            this.attributes[i] = Attribute.readAttribute(
                    this,
                    classFileInputStream,
                    listMultimap,
                    listMultimap1,
                    listMultimap2,
                    listMultimap3,
                    listMultimap4,
                    listMultimap5,
                    listMultimap6,
                    listMultimap7,
                    printWriter,
                    listMultimap8,
                    threeKeyMultiMap
            );
            if (this.attributes[i] instanceof CodeAttributeBody) {
                this.codeAttributeIndex_int = i;
                codeAttributeBody = (CodeAttributeBody) this.attributes[i];
            } else if (this.attributes[i] instanceof ExceptionsAttribute) {
                this.exceptionsAttributeIndex = i;
            }

            if (this.attributes[i] instanceof AnnotationsAttribute
                    && ((AnnotationsAttribute) this.attributes[i]).findAnnotation("java/lang/invoke/MethodHandle$PolymorphicSignature", false) != null) {
                throw new ZkmProcessingException(
                        "Cannot open a class with signature polymorphic methods in this version : '"
                                + this.getOwnerProgramClass().getLocationName()
                                + "' : '"
                                + this.toDisplayString()
                                + "'"
                );
            }
        }

        if (codeAttributeBody != null) {
            codeAttributeBody.resolveOffsetLabels(listMultimap8, printWriter);
        }

        if (this.parameterTypes != null && ConstantPoolEntry.descriptorToJavaType(this.descriptor, true) != null) {
            this.setValid(true);
        } else {
            printWriter.println("ERROR: " + this.getLocationName() + " : " + "Invalid Method Descriptor");
            this.setValid(false);
        }

        if (this.isConstructor()) {
            classFileComponent.addObserver(this);
        }
    }

    public void collectExcludedFieldStrings(Set set1, StringEncryptionExclusionSpec stringEncryptionExclusionSpec) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectExcludedFieldStrings(set1, stringEncryptionExclusionSpec);
        }
    }

    @Override
    public void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        if (object instanceof MutableInt && ((MutableInt) object).getValue() == 0 && object1 instanceof ProgramClass) {
            String string = ((ProgramClass) object1).getClassSimpleName();
            this.setName(string);
        }
    }

    public void removeMethodParametersAttribute() {
        ArrayList arrayList = new ArrayList(this.attributes.length);

        for (int i = 0; i < this.attributes.length; i++) {
            if (!(this.attributes[i] instanceof MethodParametersAttribute)) {
                arrayList.add(this.attributes[i]);
            }
        }

        if (arrayList.size() < this.attributes.length) {
            Attribute[] attributes1 = new Attribute[arrayList.size()];
            this.attributes = ((com.zelix.klassmaster.classfile.attribute.Attribute[]) (arrayList.toArray(attributes1)));
            this.attributeCount = this.attributes.length;
            this.onAttributesChanged();
        }
    }

    @Override
    public void collectCodeTypeReferences(Set set1, Set set2, Set set3, Set set4) throws ZkmProcessingException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectReferencedClasses(set1, set2, set3, set4);
        }

        if (this.exceptionsAttributeIndex != -1) {
            Enumeration enumeration = ((ExceptionsAttribute) this.attributes[this.exceptionsAttributeIndex]).getExceptionClassNames();

            while (enumeration.hasMoreElements()) {
                ClassFileBase classFileBase = ClassHierarchyNode.findClassFile((String) enumeration.nextElement());
                if (classFileBase != null) {
                    if (classFileBase.isMultiRelease()) {
                        set2.addAll(classFileBase.getAllVersions());
                    } else {
                        set2.add(classFileBase);
                    }
                }
            }
        }
    }

    public int obfuscateFlow(
            ListMultimap listMultimap,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ScriptEnvironment scriptEnvironment1,
            ClassHierarchyQuery classHierarchyQuery,
            int ba,
            boolean bl,
            boolean bl1,
            Map map1,
            boolean bl2,
            Random random1
    ) throws ZkmException, IOException {
        int bb = ba;
        if (this.codeAttributeIndex_int != -1) {
            if (this.zkmAnnotationValues != null && this.zkmAnnotationValues.hasValue("obfuscateFlow")) {
                String string = this.zkmAnnotationValues.getValue("obfuscateFlow");
                int bc = ObfuscateOptions.parseFlowLevel(string);
                if (bb != bc) {
                    String string1 = ObfuscateOptions.getFlowLevelName(bb);
                    bb = bc;
                    if (scriptEnvironment1.isVerbose()) {
                        scriptEnvironment1.getLogWriter()
                                .println(
                                        "Overriding "
                                                + (bl2 ? "Method Parameter Changing's " : "")
                                                + "Flow Obfuscation setting in method '"
                                                + AbstractExclusionSpec.formatMethod(this, true, classHierarchyQuery)
                                                + "' in class '"
                                                + AbstractExclusionSpec.formatClass(this.getOwnerProgramClass(), classHierarchyQuery, true)
                                                + "' : '"
                                                + string1
                                                + "' changed to '"
                                                + string
                                                + "'"
                                );
                    }
                }
            }

            if (bb != 0) {
                return ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                        .obfuscateFlow(listMultimap, commonSuperTypeResolver1, scriptEnvironment1, classHierarchyQuery, bb, bl, bl1, map1, random1);
            }
        }

        return 0;
    }

    public void applyStringEncryption(
            List list1,
            Map map1,
            ListMultimap listMultimap,
            Long long1,
            LocalVariableIndex localVariableIndex1,
            IntCounter intCounter,
            ConstantPool constantPool1,
            List list2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .applyStringEncryption(
                            list1,
                            this.isStaticInitializer(),
                            map1,
                            listMultimap,
                            long1,
                            localVariableIndex1,
                            intCounter,
                            constantPool1,
                            list2,
                            commonSuperTypeResolver1,
                            classHierarchyQuery
                    );
        }
    }

    public void setCodeModified() {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).setCodeModified();
        }
    }

    public void updateLocalVariableTypes(
            ConstantPool constantPool1, ArrayList arrayList, CommonSuperTypeResolver commonSuperTypeResolver1, ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        if (this.codeAttributeIndex_int != -1) {
            CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.attributes[this.codeAttributeIndex_int];
            this.getArgumentSlotCount();
            ClassHierarchyQuery classHierarchyQuery1 = classHierarchyQuery;
            CommonSuperTypeResolver commonSuperTypeResolver2 = commonSuperTypeResolver1;
            ArrayList arrayList1 = arrayList;
            ConstantPool constantPool2 = constantPool1;
            codeAttributeBody.updateLocalVariableTypes(constantPool2, arrayList1, commonSuperTypeResolver2, classHierarchyQuery1);
        }
    }

    public void expandStringConcatInvokes(
            BootstrapMethodIndex bootstrapMethodIndex1,
            ConstantPool constantPool1,
            Map map1,
            ListMultimap listMultimap,
            List list1,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .expandStringConcatInvokes(bootstrapMethodIndex1, constantPool1, map1, listMultimap, list1, commonSuperTypeResolver1, classHierarchyQuery);
        }
    }

    public void removeGenericSignatures(int ba) {
        if (ba == 1) {
            ArrayList arrayList = new ArrayList(this.attributes.length);

            for (int i = 0; i < this.attributes.length; i++) {
                if (!(this.attributes[i] instanceof SignatureAttribute) && !(this.attributes[i] instanceof BridgeAttribute)) {
                    if (this.attributes[i] instanceof TypeAnnotationsAttribute) {
                        TypeAnnotationsAttribute typeAnnotationsAttribute = (TypeAnnotationsAttribute) this.attributes[i];
                        typeAnnotationsAttribute.removeSignatureDependentAnnotations();
                        if (!typeAnnotationsAttribute.isEmpty()) {
                            arrayList.add(this.attributes[i]);
                        }
                    } else {
                        if (this.attributes[i] instanceof CodeAttributeBody) {
                            ((CodeAttributeBody) this.attributes[i]).removeLocalVariableTypeTable();
                        }

                        arrayList.add(this.attributes[i]);
                    }
                }
            }

            if (arrayList.size() < this.attributes.length) {
                Attribute[] attributes1 = new Attribute[arrayList.size()];
                this.attributes = ((com.zelix.klassmaster.classfile.attribute.Attribute[]) (arrayList.toArray(attributes1)));
                this.attributeCount = this.attributes.length;
                this.onAttributesChanged();
            }

            if (this.isBridge()) {
                this.setBridge();
                if (this.isSynthetic()) {
                    this.setSynthetic();
                }
            }

            if (this.isVarargs()) {
                this.setVarargs();
            }
        }
    }

    public int getShortestPathLength() {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).getShortestPathLength() : -1;
    }

    public void applyLongEncryption(
            List list1,
            Long long1,
            LocalVariableIndex localVariableIndex1,
            IntCounter intCounter,
            ConstantPool constantPool1,
            List list2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .applyLongEncryption(
                            list1, this.isStaticInitializer(), long1, localVariableIndex1, intCounter, constantPool1, list2, commonSuperTypeResolver1, classHierarchyQuery
                    );
        }
    }

    public CodeAttributeBody getCodeAttribute() {
        return this.codeAttributeIndex_int != -1 ? (CodeAttributeBody) this.attributes[this.codeAttributeIndex_int] : null;
    }

    @Override
    public void visitOtherAttribute() {
    }

    public void collectEncryptableIntegers(
            MultiMapTable multiMapTable, Set set1, IntegerEncryptionExclusions integerEncryptionExclusions, boolean bl, List list1, ConstantPool constantPool1
    ) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .collectEncryptableIntegers(multiMapTable, this, set1, integerEncryptionExclusions, bl, list1, constantPool1);
        }
    }

    public void collectReferencedProgramClasses(Set set1, Set set2, Set set3, Set set4) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectReferencedProgramClasses(set1, set2, set3, set4);
        }

        if (this.exceptionsAttributeIndex != -1) {
            ProgramClass programClass1 = this.getOwnerProgramClass();
            Enumeration enumeration = ((ExceptionsAttribute) this.attributes[this.exceptionsAttributeIndex]).getExceptionClassNames();

            while (enumeration.hasMoreElements()) {
                ProgramClass programClass2 = ClassHierarchyNode.findProgramClass((String) enumeration.nextElement());
                if (programClass2 != null) {
                    if (programClass1.isVersionedVariant() && programClass2.hasVersionedVariants()) {
                        programClass2 = (ProgramClass) programClass2.selectVersionForRelease(programClass1.getReleaseVersion());
                    }

                    set2.add(programClass2);
                }
            }
        }
    }

    public void collectEncryptableLongs(
            MultiMapTable multiMapTable, Set set1, LongEncryptionExclusionHandler longEncryptionExclusionHandler, List list1, ConstantPool constantPool1
    ) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .collectEncryptableLongs(multiMapTable, this, set1, longEncryptionExclusionHandler, list1, constantPool1);
        }
    }

    public void removeLocalVariableTables() {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).removeLocalVariableTables();
        }
    }

    public void removeStackMaps() {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).removeStackMapAttributes();
        }
    }

    public void removeLineNumberTable() {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).removeLineNumberTable();
        }
    }

    public String getThrowsClause() {
        if (this.exceptionsAttributeIndex == -1) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        ExceptionsAttribute exceptionsAttribute = (ExceptionsAttribute) this.attributes[this.exceptionsAttributeIndex];
        stringBuilder.append(" throws ");
        if (exceptionsAttribute.getExceptionCount() > 0) {
            for (int i = 0; i < exceptionsAttribute.getExceptionCount(); i++) {
                stringBuilder.append(exceptionsAttribute.getExceptionClass(i).getDottedClassName());
                if (i < exceptionsAttribute.getExceptionCount() - 1) {
                    stringBuilder.append(", ");
                }
            }
        }

        return stringBuilder.toString();
    }

    public boolean hasConditionalBranches() {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).hasConditionalBranches() : false;
    }

    public ListMultimap analyzeReflectionCalls(
            Map map1,
            ClasspathClassLoader classpathClassLoader1,
            NestedMultiMap nestedMultiMap,
            NestedMultiMap nestedMultiMap1,
            NestedMultiMap nestedMultiMap2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            Map map2,
            Map map3,
            Map map4,
            Map map5,
            ClassMemberLookup classMemberLookup1,
            Map map6,
            TwoKeyMap twoKeyMap,
            ListMultimap listMultimap,
            TwoKeyMap twoKeyMap1,
            TwoKeyMap twoKeyMap2,
            TwoKeyMap twoKeyMap3,
            Set set1,
            ListMultimap listMultimap1,
            List list1,
            boolean bl
    ) throws ZkmException, IOException {
        if (this.codeAttributeIndex_int != -1) {
            ArrayList arrayList = new ArrayList();
            ListMultimap listMultimap2 = ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .analyzeReflectionCalls(
                            map1,
                            classpathClassLoader1,
                            nestedMultiMap,
                            nestedMultiMap1,
                            nestedMultiMap2,
                            commonSuperTypeResolver1,
                            map2,
                            map3,
                            map4,
                            map5,
                            classMemberLookup1,
                            map6,
                            twoKeyMap,
                            listMultimap,
                            twoKeyMap1,
                            twoKeyMap2,
                            twoKeyMap3,
                            set1,
                            arrayList,
                            list1,
                            bl
                    );
            if (arrayList.size() > 0) {
                listMultimap1.putValues(this, arrayList);
            }

            return listMultimap2;
        } else {
            return null;
        }
    }

    public void applyParameterObfuscation(
            List list1,
            Map map1,
            ConstantPool constantPool1,
            List list2,
            LocalVariableIndex localVariableIndex1,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            boolean bl
    ) throws ZkmException, IOException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .applyParameterObfuscation(list1, map1, constantPool1, list2, localVariableIndex1, classMemberLookup1, classResolver1, bl);
        }
    }

    public LocalVariableList getLocalVariableList() {
        LocalVariableList localVariableList1 = null;
        if (this.codeAttributeIndex_int != -1) {
            localVariableList1 = ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).getBytecode().getLocalVariableList();
        }

        return localVariableList1;
    }

    public void setInitializerEndLabel(LabelInstruction labelInstruction) {
        this.initializerEndLabel = labelInstruction;
    }

    public void collectExcludedFieldLongs(Set set1, LongEncryptionExclusionHandler longEncryptionExclusionHandler) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectExcludedFieldLongs(set1, longEncryptionExclusionHandler);
        }
    }

    public ProgramClass getOwnerProgramClass() {
        return (ProgramClass) this.getParent();
    }

    @Override
    public final void trimAttribute(Object object, Object object1, Object object2, Object object3, Object object4) throws IOException {
        TrimOptions trimOptions1 = (TrimOptions) object1;
        ScriptEnvironment scriptEnvironment1 = (ScriptEnvironment) object3;
        TrimProcessor trimProcessor1 = (TrimProcessor) object;
        Attribute attribute = (Attribute) object2;
        PrintWriter printWriter = (PrintWriter) object4;
        if (attribute instanceof CodeAttributeBody) {
            ((CodeAttributeBody) attribute).trimAttributes(trimProcessor1, trimOptions1, scriptEnvironment1, printWriter);
        }
    }

    public int getCodeSize() {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).getCodeSize() : 0;
    }

    @Override
    public boolean isProgramMember() {
        return true;
    }

    public boolean hasStringConstants() {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).hasStringConstants() : false;
    }

    public void rebuildStackMap(
            CommonSuperTypeResolver commonSuperTypeResolver1, ClassHierarchyQuery classHierarchyQuery, ConstantPool constantPool1, List list1
    ) throws ZkmException, IOException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).rebuildStackMap(commonSuperTypeResolver1, classHierarchyQuery, constantPool1, list1);
        }
    }

    public boolean collectTypeAnnotationTargets(SetMultiMap setMultiMap) {
        boolean bl = false;

        for (int i = 0; i < this.attributeCount; i++) {
            if (this.attributes[i] instanceof TypeAnnotationsAttribute) {
                ((TypeAnnotationsAttribute) this.attributes[i]).registerLabelTargets(setMultiMap);
                bl = true;
            }
        }

        return bl;
    }

    public final void updateAttributesAfterMethodRename(ScriptEnvironment scriptEnvironment1, boolean bl) {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[i]).applyMethodRenames();
            } else if (this.attributes[i] instanceof CodeAttributeBody) {
                ((CodeAttributeBody) this.attributes[i]).updateAttributesAfterMethodRename();
            }
        }

        if (bl && (this.hasChangedParameters() || this.isNameChanged())) {
            ArrayList arrayList = new ArrayList();

            for (int i = 0; i < this.attributes.length; i++) {
                if (this.attributes[i] instanceof SignatureAttribute) {
                    if (scriptEnvironment1.isVerbose()) {
                        scriptEnvironment1.logMessage(
                                "Generic signature information removed from method '"
                                        + this.toOriginalDisplayString()
                                        + "' in class '"
                                        + this.getOwnerProgramClass().getOriginalClassName()
                                        + "' because its parameter list has been changed or obfuscated."
                        );
                    }
                } else {
                    arrayList.add(this.attributes[i]);
                }
            }

            if (arrayList.size() < this.attributeCount) {
                this.attributes = ((com.zelix.klassmaster.classfile.attribute.Attribute[]) (arrayList.toArray(new Attribute[arrayList.size()])));
                this.attributeCount = this.attributes.length;
                this.onAttributesChanged();
            }
        }
    }

    public void collectLongConstants(Set set1) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectLongConstants(set1);
        }
    }

    public void collectObfuscatableReferences(
            TwoKeySetMultiMap twoKeySetMultiMap,
            ReferenceObfuscator referenceObfuscator,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery
    ) throws ZkmException, IOException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .collectObfuscatableReferences(twoKeySetMultiMap, referenceObfuscator, commonSuperTypeResolver1, classHierarchyQuery);
        }
    }

    public boolean hasLongConstants() {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).hasLongConstants() : false;
    }

    public void collectEncryptableStrings(MultiMapTable multiMapTable, Set set1) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectEncryptableStrings(multiMapTable, this, set1);
        }
    }

    public void collectStringConstants(Set set1) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectStringConstants(set1);
        }
    }

    public void rebuildStackMapTable(
            CommonSuperTypeResolver commonSuperTypeResolver1, ClassHierarchyQuery classHierarchyQuery, ConstantPool constantPool1, ArrayList arrayList
    ) throws ZkmException, IOException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .rebuildStackMapTable(commonSuperTypeResolver1, classHierarchyQuery, constantPool1, arrayList);
        }
    }

    @Override
    public final void resolveAttributeReferences(
            ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1, ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof ReferencingAttribute) {
                ((ReferencingAttribute) this.attributes[i]).resolveReferences(classResolver1, ignoreMissingReferencesSpec1, scriptEnvironment1);
            } else if (this.attributes[i] instanceof CodeAttributeBody) {
                ((CodeAttributeBody) this.attributes[i]).resolveAttributeReferences(classResolver1, ignoreMissingReferencesSpec1, scriptEnvironment1);
            }
        }
    }

    public void trimMethodParametersAttribute(boolean bl, boolean bl1) {
        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof MethodParametersAttribute) {
                if (bl && !this.isPublic() && !this.isProtected()) {
                    this.removeMethodParametersAttribute();
                } else if (bl1 && this.isRenamed()) {
                    this.removeMethodParametersAttribute();
                }
            }
        }
    }

    public int[] insertInstructions(List list1, int ba, int bb, int bc, String string, List list2) throws ZkmProcessingException {
        return this.codeAttributeIndex_int != -1
                ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).insertInstructions(list1, ba, bb, bc, string, list2)
                : null;
    }

    public void setZkmAnnotationValues(AnnotationValueMap annotationValueMap) {
        this.zkmAnnotationValues = annotationValueMap;
        this.getOwnerProgramClass().markHasZkmMethodAnnotations();
    }

    public void renameMethod(
            NameExclusionSet nameExclusionSet,
            ChangeLogMapping changeLogMapping1,
            MethodRenamer methodRenamer1,
            MethodOverrideAnalyzer methodOverrideAnalyzer,
            MethodNameGeneratorBase methodNameGeneratorBase,
            Map map1,
            ListMultimap listMultimap,
            ClassHierarchyNode classHierarchyNode,
            String string,
            boolean bl,
            int ba,
            Map map2,
            Map map3,
            Map map4,
            DisableableMap disableableMap,
            DisableableMap disableableMap1,
            Set set1,
            boolean bl1,
            Map map5
    ) throws ZkmException, IOException {
        if (!this.isConstructor() && !this.isStaticInitializer()) {
            String string1 = null;
            ProgramClass programClass1 = classHierarchyNode.getProgramClass();
            MethodSignature methodSignature1 = this.getSignature();
            if (!nameExclusionSet.isMethodExcluded(this)) {
                AbstractMethodInfo abstractMethodInfo = methodOverrideAnalyzer.findRootMethod(this);
                String string2;
                if (abstractMethodInfo != null) {
                    string2 = abstractMethodInfo.getClassName();
                } else {
                    string2 = string;
                }

                boolean bl2 = false;
                if (changeLogMapping1 != null && changeLogMapping1.hasMethodMapping(string2, methodSignature1)) {
                    bl2 = true;
                    string1 = changeLogMapping1.lookupNewMethodName(string2, methodSignature1);
                    if (string1 != null) {
                        if (changeLogMapping1.isParameterObfuscatedMethod(
                                abstractMethodInfo != null && abstractMethodInfo.isProgramMember() ? (MethodInfo) abstractMethodInfo : this
                        )) {
                            if (methodNameGeneratorBase.hasParameterExclusion(this)) {
                                this.setNameChanged(true);
                            } else {
                                String string3 = AbstractExclusionSpec.formatClassWithModifiers(programClass1, nameExclusionSet);
                                changeLogMapping1.logWarning(
                                        "Method '"
                                                + this.buildDeclaration(false)
                                                + "' in class '"
                                                + string3
                                                + "' could not be parameter obfuscated as specified. You may need to distribute this application as a whole. (F)"
                                );
                            }
                        }

                        MemberSignatureBase memberSignatureBase;
                        AbstractMethodInfo abstractMethodInfo1;
                        if (bl1) {
                            memberSignatureBase = new MethodSignature(string1, this.getDescriptor());
                            abstractMethodInfo1 = (AbstractMethodInfo) map1.get(memberSignatureBase);
                        } else {
                            memberSignatureBase = new FieldNameTypeSignature(string1, this.getDescriptor());
                            abstractMethodInfo1 = (AbstractMethodInfo) map3.get(memberSignatureBase);
                        }

                        if (abstractMethodInfo1 != null && methodSignature1.equals(abstractMethodInfo1.getSignature())) {
                            ClassFileBase classFileBase = programClass1.findInheritedMethodReferrer(abstractMethodInfo1);
                            if (classFileBase != null) {
                                bl2 = false;
                                String string4 = methodRenamer1.getOriginalMemberName(abstractMethodInfo1.getClassName(), memberSignatureBase);
                                String string5 = AbstractExclusionSpec.formatClassWithModifiers(programClass1, nameExclusionSet);
                                String string6 = AbstractExclusionSpec.formatClassWithModifiers(classFileBase, nameExclusionSet);
                                String string7 = AbstractExclusionSpec.formatClassWithModifiers(abstractMethodInfo1.getOwningClass(), nameExclusionSet);
                                String string8;
                                if (abstractMethodInfo != null) {
                                    string8 = abstractMethodInfo.getOwningClass().getClassName();
                                } else {
                                    string8 = this.getClassName();
                                }

                                changeLogMapping1.removeMethodMapping(string8, methodSignature1);
                                changeLogMapping1.logWarning(
                                        "Method '"
                                                + this.buildDeclaration(false)
                                                + "' in class '"
                                                + string5
                                                + "' could not be renamed to '"
                                                + string1
                                                + "' because symbolic references to method '"
                                                + string7
                                                + "."
                                                + string4
                                                + "' appear as '"
                                                + string6
                                                + "."
                                                + string1
                                                + "'. You may need to distribute this application as a whole. (B)"
                                );
                            }
                        }
                    }
                }

                if (!bl2) {
                    if (this.isSynthetic()
                            && (ba == 0 || ba == 2 && !programClass1.isRenamed() && !this.isDescriptorChanged())
                            && this.methodName.startsWith("access$")
                            && NumericStringUtil.isInteger(this.methodName.substring("access$".length()))) {
                        map2.put(this.getSignature(), this);
                        map4.put(this.getNameTypeSignature(), this);
                        DisableableMap disableableMap2;
                        String string10;
                        if (bl) {
                            disableableMap.put(this.getSourceName(), this);
                            disableableMap2 = disableableMap1;
                            string10 = this.getSourceName();
                        } else {
                            disableableMap2 = disableableMap1;
                            string10 = this.getSourceName();
                        }

                        disableableMap2.put(string10, this);
                    } else {
                        boolean bl3 = methodNameGeneratorBase.hasParameterExclusion(this);
                        if (!this.isStatic()) {
                            if (!this.isAbstract() && this.isStrictlyPrivate()) {
                                string1 = methodNameGeneratorBase.assignNewNameForNode(
                                        classHierarchyNode, this, map1, map2, map3, map4, disableableMap, disableableMap1, set1, bl1, bl3
                                );
                            } else {
                                string1 = methodNameGeneratorBase.resolveNewMethodName(
                                        classHierarchyNode, this, map1, bl, map2, map3, map4, disableableMap, disableableMap1, set1, bl1, bl3
                                );
                            }
                        } else {
                            label102:
                            if (!map5.containsKey(this)) {
                                boolean bl4;
                                if (this.isPublic()) {
                                    if (this.isStatic()) {
                                        if (this.getSignature().equals(MethodSignature.MAIN_METHOD)) {
                                            break label102;
                                        }

                                        bl4 = HiddenOptionFlags.ALLOW_RENAME_MAIN;
                                    } else {
                                        bl4 = HiddenOptionFlags.ALLOW_RENAME_MAIN;
                                    }
                                } else {
                                    bl4 = HiddenOptionFlags.ALLOW_RENAME_MAIN;
                                }

                                if (!bl4) {
                                    string1 = methodNameGeneratorBase.assignMethodName(
                                            classHierarchyNode, this, map1, listMultimap, bl, map2, map3, map4, disableableMap, disableableMap1, set1, bl1, bl3
                                    );
                                }
                            }
                        }
                    }
                }
            }

            ObservableHolder observableHolder = new ObservableHolder();
            MethodSignature methodSignature2 = methodRenamer1.recordMethodName(this, string1, observableHolder);
            if (string1 != null && !string1.equals(this.getJvmName())) {
                map1.put(methodSignature2, this);
                map2.put(methodSignature2, this);
                map3.put(methodSignature2.getNameTypeSignature(), this);
                map4.put(methodSignature2.getNameTypeSignature(), this);
                disableableMap1.put(methodSignature2.getName(), this);
                if (bl) {
                    disableableMap.put(methodSignature2.getName(), this);
                }

                if (!observableHolder.isValueNull()) {
                    MethodSignature methodSignature3 = (MethodSignature) observableHolder.getValue();
                    map1.put(methodSignature3, this);
                    map2.put(methodSignature3, this);
                    map3.put(methodSignature3.getNameTypeSignature(), this);
                    map4.put(methodSignature3.getNameTypeSignature(), this);
                }

                this.setName(string1);
            } else {
                map1.put(methodSignature1, this);
                map2.put(methodSignature1, this);
                map3.put(methodSignature1.getNameTypeSignature(), this);
                map4.put(methodSignature1.getNameTypeSignature(), this);
                DisableableMap disableableMap3;
                String string9;
                if (bl) {
                    disableableMap.put(methodSignature2.getName(), this);
                    disableableMap3 = disableableMap1;
                    string9 = this.getSourceName();
                } else {
                    disableableMap3 = disableableMap1;
                    string9 = this.getSourceName();
                }

                disableableMap3.put(string9, this);
            }
        }
    }

    public void obfuscateLocalVariableNames() {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).obfuscateLocalVariableNames();
        }
    }

    public boolean storesToField(FieldInfo fieldInfo) {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).storesToField(fieldInfo) : false;
    }

    public boolean hasMethodParametersAttribute() {
        for (int i = 0; i < this.attributeCount; i++) {
            if (this.attributes[i] instanceof MethodParametersAttribute) {
                return true;
            }
        }

        return false;
    }

    public void applyCodeInsertion(CodeInsertion codeInsertion, int ba, int bb) throws ZkmProcessingException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).applyCodeInsertion(codeInsertion, ba, bb);
        }
    }

    public void collectIntegerConstants(Set set1) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectIntegerConstants(set1);
        }
    }

    public void collectMethodCallSites(ListMultimap listMultimap) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectMethodCallSites(listMultimap);
        }
    }

    @Override
    public void onAttributesChanged() {
        this.codeAttributeIndex_int = -1;
        this.exceptionsAttributeIndex = -1;

        for (int i = 0; i < this.attributes.length; i++) {
            if (this.attributes[i] instanceof CodeAttributeBody) {
                this.codeAttributeIndex_int = i;
            } else if (this.attributes[i] instanceof ExceptionsAttribute) {
                this.exceptionsAttributeIndex = i;
            }
        }
    }

    @Override
    public String formatDeclaration() throws ZkmProcessingException, IOException {
        StringBuffer stringBuffer = new StringBuffer();
        Enumeration enumeration = this.enumerateAnnotationTypes();

        while (enumeration.hasMoreElements()) {
            stringBuffer.append("   ");
            stringBuffer.append('@');
            stringBuffer.append(ZkmUtils.slashesToDots((String) enumeration.nextElement()));
            stringBuffer.append(HiddenOptionFlags.LINE_SEPARATOR);
        }

        stringBuffer.append("   ");
        Map map1 = null;
        if (this.codeAttributeIndex_int != -1) {
            CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.attributes[this.codeAttributeIndex_int];
            map1 = codeAttributeBody.getLocalVariableNameMap();
        }

        stringBuffer.append(this.buildDeclarationWithParamNames(map1));
        stringBuffer.append(this.getThrowsClause());
        stringBuffer.append(" {");
        stringBuffer.append(HiddenOptionFlags.LINE_SEPARATOR);
        int maxLocals = this.getMaxLocals();

        for (int i = this.getArgumentSlotCount(); i < maxLocals; i++) {
            stringBuffer.append("      " + "int local" + i + ";");
            stringBuffer.append(HiddenOptionFlags.LINE_SEPARATOR);
        }

        stringBuffer.append(HiddenOptionFlags.LINE_SEPARATOR);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        this.printCode(printWriter);
        stringBuffer.append(stringWriter.toString());
        stringBuffer.append("}");
        return stringBuffer.toString();
    }

    public void insertMethodKeyInitialization(
            MethodParamChangeNode methodParamChangeNode, Long long1, ObservableHolder observableHolder, List list1, ConstantPool constantPool1, String string, int ba
    ) throws ZkmException, IOException {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .insertMethodKeyInitialization(methodParamChangeNode, long1, observableHolder, list1, constantPool1, string, ba);
        }
    }

    public void collectExcludedFieldIntegers(Set set1, IntegerEncryptionExclusions integerEncryptionExclusions) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectExcludedFieldIntegers(set1, integerEncryptionExclusions);
        }
    }

    public boolean analyzeExceptionObfuscation(
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassHierarchyQuery classHierarchyQuery,
            List list1,
            List list2,
            List list3,
            PairValueMap pairValueMap,
            boolean bl,
            boolean bl1,
            IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        boolean bl2 = bl;
        boolean bl3 = false;
        ArrayList arrayList = null;
        if (this.codeAttributeIndex_int != -1) {
            if (this.zkmAnnotationValues != null && this.zkmAnnotationValues.hasValue("exceptionObfuscation")) {
                String string = this.zkmAnnotationValues.getValue("exceptionObfuscation");
                int ba = ObfuscateOptions.parseExceptionLevel(string);
                if (ba != (bl2 ? 2 : 1)) {
                    String string1 = ObfuscateOptions.getExceptionLevelName(bl2 ? 2 : 1);
                    if (scriptEnvironment1.isVerbose()) {
                        scriptEnvironment1.getLogWriter()
                                .println(
                                        "Overriding Exception Obfuscation setting in method '"
                                                + AbstractExclusionSpec.formatMethod(this, true, classHierarchyQuery)
                                                + "' in class '"
                                                + AbstractExclusionSpec.formatClass(this.getOwnerProgramClass(), classHierarchyQuery, true)
                                                + "' : '"
                                                + string1
                                                + "' changed to '"
                                                + string
                                                + "'"
                                );
                    }

                    switch (ba) {
                        case 0:
                            return false;
                        case 1:
                            bl2 = false;
                            break;
                        case 2:
                            bl2 = true;
                    }
                }
            }

            if (this.exceptionsAttributeIndex != -1) {
                ExceptionsAttribute exceptionsAttribute = (ExceptionsAttribute) this.attributes[this.exceptionsAttributeIndex];
                arrayList = new ArrayList(exceptionsAttribute.getExceptionCount());
                Enumeration enumeration = exceptionsAttribute.getExceptionClasses();

                while (enumeration.hasMoreElements()) {
                    arrayList.add(enumeration.nextElement());
                }
            }

            bl3 = ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                    .analyzeExceptionObfuscation(
                            commonSuperTypeResolver1, classHierarchyQuery, list1, list2, arrayList, list3, pairValueMap, bl2, bl1, ignoreMissingReferencesSpec1
                    );
        }

        return bl3;
    }

    @Override
    public ClassFileBase getOwningClass() {
        return this.getOwnerProgramClass();
    }

    public boolean hasZkmAnnotationValues() {
        return this.zkmAnnotationValues != null;
    }

    @Override
    public void clearSynthetic() {
        int attributeCount = this.attributeCount;
        super.clearSynthetic();
        if (attributeCount != this.attributeCount) {
            this.onAttributesChanged();
        }
    }

    public boolean applyAutoReflectionHandling(
            List list1,
            String string,
            List list2,
            boolean bl,
            boolean bl1,
            boolean bl2,
            CommonSuperTypeResolver commonSuperTypeResolver1,
            ClassMemberLookup classMemberLookup1,
            boolean bl3,
            ScriptEnvironment scriptEnvironment1
    ) throws ZkmException, IOException {
        return this.codeAttributeIndex_int != -1
                ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int])
                .applyAutoReflectionHandling(list1, string, list2, bl, bl1, bl2, commonSuperTypeResolver1, classMemberLookup1, bl3, scriptEnvironment1)
                : false;
    }

    @Override
    public void collectReachableMethods(Set set1, Set set2) throws ZkmProcessingException {
        if (set1.add(this) && this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectReachableMethods(set1, set2);
        }
    }

    public ArrayList getDescriptorProgramClasses() {
        ArrayList arrayList = new ArrayList();
        String string = this.getDescriptor();
        List list1 = ConstantPoolEntry.getParameterTypes(string);
        String string1 = ConstantPoolEntry.getReturnDescriptor(string);
        list1.add(string1);

        for (int i = 0; i < list1.size(); i++) {
            String string2 = ClassFileBase.extractClassName((String) list1.get(i));
            if (string2 != null) {
                ProgramClass programClass1 = ClassHierarchyNode.findProgramClass(string2);
                if (programClass1 != null) {
                    arrayList.add(programClass1);
                }
            }
        }

        return arrayList;
    }

    public int getLocalVariableCount() {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).getMaxLocals() : 0;
    }

    @Override
    public MethodBytecode getBytecode() throws ZkmProcessingException {
        return this.codeAttributeIndex_int != -1 ? ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).getBytecode() : null;
    }

    @Override
    public final void collectAttributeReferences(Set set1) {
        for (int i = 0; i < this.attributeCount; i++) {
            if (this.attributes[i] instanceof ReferencingAttribute) {
                ReferencingAttribute referencingAttribute = (ReferencingAttribute) this.attributes[i];
                referencingAttribute.collectReferencedClasses(set1);
            } else if (this.attributes[i] instanceof CodeAttributeBody) {
                CodeAttributeBody codeAttributeBody = (CodeAttributeBody) this.attributes[i];
                codeAttributeBody.collectAttributeReferences(set1);
            }
        }
    }

    public void collectUsedBootstrapMethods(Set set1) {
        if (this.codeAttributeIndex_int != -1) {
            ((CodeAttributeBody) this.attributes[this.codeAttributeIndex_int]).collectUsedBootstrapMethods(set1);
        }
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
