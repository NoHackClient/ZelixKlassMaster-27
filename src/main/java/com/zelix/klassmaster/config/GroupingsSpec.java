package com.zelix.klassmaster.config;

import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.script.parser.ast.ASTComplexAnnotationSpecifier;
import com.zelix.klassmaster.script.parser.ast.ASTGroupingsStatement;
import com.zelix.klassmaster.script.parser.ast.ASTRenameFilterParameter;
import com.zelix.klassmaster.util.ReadOnlyMultiMap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GroupingsSpec implements ClassHierarchyQuery {
    public TwoKeyMap parametersByGroup = new TwoKeyMap();
    public TwoKeyMap classesByGroup = new TwoKeyMap();
    public ClassRepository classRepository;
    public List groupingsStatements;
    public ScriptEnvironment scriptEnvironment;

    public final void resolveGroupClasses() throws ZkmException, IOException {
        ArrayList arrayList = new ArrayList();
        Enumeration enumeration = this.parametersByGroup.keys();

        while (enumeration.hasMoreElements()) {
            String string = (String) enumeration.nextElement();
            arrayList.add(string);
        }

        Collections.sort(arrayList);

        for (int i = 0; i < arrayList.size(); i++) {
            String string1 = (String) arrayList.get(i);
            Iterator iterator = this.parametersByGroup.getInnerMap(string1).keySet().iterator();

            while (iterator.hasNext()) {
                ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) iterator.next();
                if (!aSTRenameFilterParameter.isClassSpecifier()) {
                    this.scriptEnvironment
                            .logWarning(
                                    "Parameter '" + aSTRenameFilterParameter + "' in 'groupings' statement is not a class exclusion parameter. It will be ignored.", true
                            );
                } else if (aSTRenameFilterParameter.hasLinkClassName()) {
                    this.scriptEnvironment
                            .logWarning("Parameter '" + aSTRenameFilterParameter + "' in 'groupings' statement cannot use the '<link>' syntax. It will be ignored.", true);
                } else {
                    if (aSTRenameFilterParameter.isClassPlusSpec()) {
                        this.scriptEnvironment
                                .logWarning(
                                        "Parameter '" + aSTRenameFilterParameter + "' in 'groupings' statement cannot use the '" + "+" + "' tag. The tag will be ignored.",
                                        true
                                );
                    }

                    HashSet hashSet = ZkmUtils.createHashSet();
                    aSTRenameFilterParameter.collectGroupingClasses(this, hashSet);
                    if (hashSet.size() > 0) {
                        Iterator iterator1 = hashSet.iterator();

                        while (iterator1.hasNext()) {
                            ProgramClass programClass1 = (ProgramClass) iterator1.next();
                            this.classesByGroup.putValue(string1, programClass1, programClass1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public final boolean implementsAnnotatedInterface(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        return this.classRepository.implementsAnnotatedInterface(string, string1, aSTComplexAnnotationSpecifier);
    }

    public GroupingsSpec(ClassRepository classRepository1, List list1, ScriptEnvironment scriptEnvironment1) throws ZkmException, IOException {
        this.classRepository = classRepository1;
        this.groupingsStatements = list1;
        this.scriptEnvironment = scriptEnvironment1;
        if (classRepository1.hasProgramClasses()) {
            this.collectGroupingParameters();
            this.resolveGroupClasses();
        }
    }

    @Override
    public final boolean isHierarchyMarked() {
        return this.classRepository.isHierarchyMarked();
    }

    @Override
    public final boolean extendsAnnotatedClassByOriginalName(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        return this.classRepository.extendsAnnotatedClassByOriginalName(string, string1, aSTComplexAnnotationSpecifier);
    }

    @Override
    public String getOriginalClassName(Object object) {
        String string = (String) object;
        return this.classRepository.getOriginalClassName(string);
    }

    public Enumeration enumerateClasses() {
        return this.classRepository.enumerateProgramClasses();
    }

    @Override
    public final boolean extendsAnnotatedClass(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        return this.classRepository.extendsAnnotatedClass(string, string1, aSTComplexAnnotationSpecifier);
    }

    @Override
    public final boolean isHierarchySealed() {
        return this.classRepository.isHierarchySealed();
    }

    @Override
    public final boolean isObjectMethodSignature(Object object) {
        MethodSignature methodSignature1 = (MethodSignature) object;
        return this.classRepository.isObjectMethodSignature(methodSignature1);
    }

    @Override
    public Set getClassAnnotations(String string, Integer integer, boolean bl) throws ZkmException, IOException {
        Boolean boolean1 = bl;
        Integer integer1 = integer;
        String string1 = string;
        return this.getClassAnnotations(true, string1, integer1, boolean1);
    }

    @Override
    public Set getClassAnnotations(Object object, String string, Integer integer, boolean bl) throws ZkmException, IOException {
        return this.classRepository.getClassAnnotations(string, integer, bl);
    }

    @Override
    public final boolean implementsAnnotatedInterfaceByOriginalName(String string, String string1, ASTComplexAnnotationSpecifier aSTComplexAnnotationSpecifier) throws ZkmException, IOException {
        return this.classRepository.implementsAnnotatedInterfaceByOriginalName(string, string1, aSTComplexAnnotationSpecifier);
    }

    @Override
    public final boolean implementsInterfaceByOriginalName(String string, String string1) throws ZkmException, IOException {
        return this.classRepository.implementsInterfaceByOriginalName(string, string1);
    }

    @Override
    public final boolean isSubclassByOriginalName(String string, String string1) throws ZkmException, IOException {
        return this.classRepository.isSubclassByOriginalName(string, string1);
    }

    public ReadOnlyMultiMap getGroupedClasses() {
        return new ReadOnlyMultiMap(this.classesByGroup);
    }

    @Override
    public final boolean hasOriginalNameCaches() {
        return this.classRepository.hasOriginalNameCaches();
    }

    @Override
    public ClassResolver getClassResolver() {
        return this.classRepository.getClassResolver();
    }

    public boolean isLoggingEnabled() {
        return this.scriptEnvironment.isVerbose();
    }

    public void printGroupingParameters() {
        PrintWriter printWriter = this.scriptEnvironment.getLogWriter();
        ArrayList arrayList = new ArrayList();
        Enumeration enumeration = this.parametersByGroup.keys();

        while (enumeration.hasMoreElements()) {
            String string = (String) enumeration.nextElement();
            arrayList.add(string);
        }

        Collections.sort(arrayList);
        printWriter.println("\tGroupings parameters:");

        for (int i = 0; i < arrayList.size(); i++) {
            String string1 = (String) arrayList.get(i);
            printWriter.println("\t\t\"" + string1 + "\"");
            Iterator iterator = this.parametersByGroup.getInnerMap(string1).keySet().iterator();

            while (iterator.hasNext()) {
                printWriter.println("\t\t\t\"" + ((ASTRenameFilterParameter) iterator.next()).toScriptText() + "\"");
            }
        }
    }

    @Override
    public final boolean implementsInterface(String string, String string1) throws ZkmException, IOException {
        return this.classRepository.implementsInterface(string, string1);
    }

    public PrintWriter getLogWriter() {
        return this.scriptEnvironment.getLogWriter();
    }

    public final void collectGroupingParameters() {
        int ba = 0;
        int bb = this.groupingsStatements.size();

        for (int i = 0; i < bb; i++) {
            ArrayList arrayList = ((ASTGroupingsStatement) this.groupingsStatements.get(i)).getGroupings();

            for (int j = 0; j < arrayList.size(); j++) {
                String string = "GROUPING_" + ba++;
                ArrayList arrayList1 = (ArrayList) arrayList.get(j);

                for (int k = 0; k < arrayList1.size(); k++) {
                    ASTRenameFilterParameter aSTRenameFilterParameter = (ASTRenameFilterParameter) arrayList1.get(k);
                    this.parametersByGroup.putValue(string, aSTRenameFilterParameter, aSTRenameFilterParameter);
                }
            }
        }
    }

    @Override
    public final boolean isSubclass(String string, String string1) throws ZkmException, IOException {
        return this.classRepository.isSubclass(string, string1);
    }
}
