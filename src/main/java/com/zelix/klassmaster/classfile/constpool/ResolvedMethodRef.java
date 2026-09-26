package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.AbstractMethodInfo;
import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassFile;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.MemberNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.obfuscator.reflection.ClassNameArgTranslator;
import com.zelix.klassmaster.obfuscator.reflection.ClassNameDepth2Translator;
import com.zelix.klassmaster.obfuscator.reflection.FieldLookupTemplate;
import com.zelix.klassmaster.obfuscator.reflection.FindFieldNameTranslator;
import com.zelix.klassmaster.obfuscator.reflection.FindMethodNameTranslator;
import com.zelix.klassmaster.obfuscator.reflection.FindSpecialHelperTemplate;
import com.zelix.klassmaster.obfuscator.reflection.GetMethodReflectionStub;
import com.zelix.klassmaster.obfuscator.reflection.InvocationKind;
import com.zelix.klassmaster.obfuscator.reflection.LoadClassReflectionHelper;
import com.zelix.klassmaster.obfuscator.reflection.RefFieldUpdaterNameTemplate;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionApiMethod;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionLookupTemplate;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionParamType;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionTargetKind;
import com.zelix.klassmaster.obfuscator.reflection.ReflectionTargetScope;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public abstract class ResolvedMethodRef extends ResolvedMemberRef {
    public static ReflectionLookupTemplate CLASS_NAME_ARG_TEMPLATE = new ClassNameArgTranslator();
    public static ReflectionLookupTemplate LOAD_CLASS_TEMPLATE = new LoadClassReflectionHelper();
    public static ReflectionLookupTemplate CLASS_NAME_DEPTH2_TEMPLATE = new ClassNameDepth2Translator();
    public static ReflectionLookupTemplate GET_METHOD_TEMPLATE = new GetMethodReflectionStub();
    public static ReflectionLookupTemplate FIND_METHOD_NAME_TEMPLATE = new FindMethodNameTranslator();
    public static ReflectionLookupTemplate FIND_SPECIAL_TEMPLATE = new FindSpecialHelperTemplate();
    public static ReflectionLookupTemplate FIELD_LOOKUP_TEMPLATE = new FieldLookupTemplate();
    public static ReflectionLookupTemplate FIND_FIELD_NAME_TEMPLATE = new FindFieldNameTranslator();
    public static ReflectionLookupTemplate REF_FIELD_UPDATER_TEMPLATE = new RefFieldUpdaterNameTemplate();
    public static ReflectionApiMethod LAMBDA_GET_FUNCTIONAL_METHOD_NAME = new ReflectionApiMethod(
            0, InvocationKind.INSTANCE, ReflectionTargetKind.FUNCTIONAL_INTERFACE_METHOD_TYPE, "getFunctionalInterfaceMethodName()Ljava/lang/String;"
    );
    public static ReflectionApiMethod GET_NAME = new ReflectionApiMethod(
            0, InvocationKind.INSTANCE, ReflectionTargetKind.OBJECT_NAME_TYPE, "getName()Ljava/lang/String;"
    );
    public static ReflectionApiMethod CLASS_FOR_NAME = new ReflectionApiMethod(
            1,
            InvocationKind.STATIC,
            ReflectionTargetKind.CLASS_TYPE,
            false,
            "forName(Ljava/lang/String;)Ljava/lang/Class;",
            CLASS_NAME_ARG_TEMPLATE,
            ReflectionTargetScope.ALL_CLASSES
    );
    public static ReflectionApiMethod CLASS_FOR_NAME_MODULE;
    public static ReflectionApiMethod CLASS_FOR_NAME_LOADER;
    public static ReflectionApiMethod CLASS_GET_FIELD;
    public static ReflectionApiMethod CLASS_GET_METHOD;
    public static ReflectionApiMethod LOADER_LOAD_CLASS;
    public static ReflectionApiMethod LOADER_HANDLER_LOAD_CLASS_URL;
    public static ReflectionApiMethod LOAD_CLASS_CODEBASE;
    public static ReflectionApiMethod LOADER_LOAD_CLASS_RESOLVE;
    public static ReflectionApiMethod RMI_LOAD_CLASS;
    public static ReflectionApiMethod RMI_LOAD_CLASS_URL;
    public static ReflectionApiMethod RMI_LOAD_CLASS_CODEBASE;
    public static ReflectionApiMethod RMI_LOAD_CLASS_CODEBASE_LOADER;
    public static ReflectionApiMethod CLASS_GET_DECLARED_FIELD;
    public static ReflectionApiMethod CLASS_GET_DECLARED_METHOD;
    public static ReflectionApiMethod LOADER_DEFINE_CLASS;
    public static ReflectionApiMethod LOADER_DEFINE_CLASS_DOMAIN;
    public static ReflectionApiMethod FIND_CLASS;
    public static ReflectionApiMethod LOADER_FIND_SYSTEM_CLASS;
    public static ReflectionApiMethod LOADER_FIND_LOADED_CLASS;
    public static ReflectionApiMethod EVENT_SET_DESCRIPTOR_INIT4;
    public static ReflectionApiMethod EVENT_SET_DESCRIPTOR_INIT6;
    public static ReflectionApiMethod EVENT_SET_DESCRIPTOR_INIT7;
    public static ReflectionApiMethod GET_BUNDLE;
    public static ReflectionApiMethod GET_BUNDLE_LOCALE;
    public static ReflectionApiMethod GET_BUNDLE_LOADER;
    public static ReflectionApiMethod BEANS_INSTANTIATE;
    public static ReflectionApiMethod BEANS_INSTANTIATE_CONTEXT;
    public static ReflectionApiMethod BEANS_INSTANTIATE_INITIALIZER;
    public static ReflectionApiMethod BEAN_CONTEXT_INSTANTIATE_CHILD;
    public static ReflectionApiMethod CLASS_NEW_INSTANCE;
    public static ReflectionApiMethod CLASS_GET_CONSTRUCTOR;
    public static ReflectionApiMethod CLASS_GET_CONSTRUCTORS;
    public static ReflectionApiMethod PACKAGE_GET_PACKAGE;
    public static ReflectionApiMethod LOADER_GET_PACKAGE;
    public static ReflectionApiMethod LOOKUP_FIND_STATIC;
    public static ReflectionApiMethod LOOKUP_FIND_VIRTUAL;
    public static ReflectionApiMethod LOOKUP_FIND_SPECIAL;
    public static ReflectionApiMethod LOOKUP_FIND_CONSTRUCTOR;
    public static ReflectionApiMethod LOOKUP_FIND_GETTER;
    public static ReflectionApiMethod LOOKUP_FIND_SETTER;
    public static ReflectionApiMethod LOOKUP_FIND_STATIC_GETTER;
    public static ReflectionApiMethod LOOKUP_FIND_STATIC_SETTER;
    public static ReflectionApiMethod LOOKUP_FIND_VAR_HANDLE;
    public static ReflectionApiMethod LOOKUP_FIND_STATIC_VAR_HANDLE;
    public static ReflectionApiMethod LOOKUP_BIND;
    public static ReflectionApiMethod REFERENCE_FIELD_UPDATER_NEW;
    public static ReflectionApiMethod INTEGER_FIELD_UPDATER_NEW;
    public static ReflectionApiMethod LONG_FIELD_UPDATER_NEW;
    public static ReflectionApiMethod METHOD_TYPE_RETURN;
    public static ReflectionApiMethod METHOD_TYPE_RETURN_PARAM;
    public static ReflectionApiMethod METHOD_TYPE_RETURN_PARAMS;
    public static ReflectionApiMethod METHOD_TYPE_RETURN_PARAM_PARAMS;
    public static ReflectionApiMethod METHOD_TYPE_RETURN_TYPE;
    public static TwoKeyMap REFLECTION_METHODS_BY_SIGNATURE;
    public static HashSet SUBTYPE_MATCHED_SIGNATURES;

    public static AbstractMethodInfo findMethodInClass(ClassFileBase classFileBase, MethodSignature methodSignature1, ClassMemberLookup classMemberLookup1) {
        return classFileBase.isProgramClass() && !classFileBase.isVersionedVariant()
                ? classMemberLookup1.findDeclaredMethod((ProgramClass) classFileBase, methodSignature1)
                : classFileBase.findMethod(methodSignature1);
    }

    public ResolvedMethodRef(
            AbstractConstantPool abstractConstantPool,
            ResolvedClassConstant resolvedClassConstant,
            ResolvedNameAndType resolvedNameAndType,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1
    ) throws ZkmException, IOException {
        super(abstractConstantPool, resolvedClassConstant, resolvedNameAndType, classMemberLookup1, classResolver1, true);
    }


    static {
        CLASS_FOR_NAME.addParamDetail(0, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        CLASS_FOR_NAME_MODULE = new ReflectionApiMethod(
                2,
                InvocationKind.STATIC,
                ReflectionTargetKind.CLASS_TYPE,
                false,
                "forName(Ljava/lang/Module;Ljava/lang/String;)Ljava/lang/Class;",
                CLASS_NAME_ARG_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        CLASS_FOR_NAME_MODULE.addParamDetail(1, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        CLASS_FOR_NAME_LOADER = new ReflectionApiMethod(
                3,
                InvocationKind.STATIC,
                ReflectionTargetKind.CLASS_TYPE,
                false,
                "forName(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;",
                CLASS_NAME_DEPTH2_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        CLASS_FOR_NAME_LOADER.addParamDetail(0, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        CLASS_GET_FIELD = new ReflectionApiMethod(
                1,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.FIELD_TYPE,
                true,
                "getField(Ljava/lang/String;)Ljava/lang/reflect/Field;",
                FIELD_LOOKUP_TEMPLATE,
                ReflectionTargetScope.ALL_PUBLIC_FIELDS
        );
        CLASS_GET_FIELD.addParamDetail(0, ReflectionParamType.FIELD_NAME_PARAM_TYPE, false);
        CLASS_GET_METHOD = new ReflectionApiMethod(
                2,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.METHOD_TYPE,
                true,
                "getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
                GET_METHOD_TEMPLATE,
                ReflectionTargetScope.ALL_PUBLIC_METHODS
        );
        CLASS_GET_METHOD.addParamDetail(0, ReflectionParamType.METHOD_NAME_PARAM_TYPE, false);
        CLASS_GET_METHOD.addParamDetail(1, ReflectionParamType.CLASS_PARAM_TYPE, true);
        LOADER_LOAD_CLASS = new ReflectionApiMethod(
                1,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.CLASS_TYPE,
                false,
                "loadClass(Ljava/lang/String;)Ljava/lang/Class;",
                CLASS_NAME_ARG_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        LOADER_LOAD_CLASS.addParamDetail(0, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        LOADER_HANDLER_LOAD_CLASS_URL = new ReflectionApiMethod(
                2,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.CLASS_TYPE,
                false,
                "loadClass(Ljava/net/URL;Ljava/lang/String;)Ljava/lang/Class;",
                CLASS_NAME_ARG_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        LOADER_HANDLER_LOAD_CLASS_URL.addParamDetail(1, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        LOAD_CLASS_CODEBASE = new ReflectionApiMethod(
                2, InvocationKind.INSTANCE, ReflectionTargetKind.CLASS_TYPE, "loadClass(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Class;"
        );
        LOAD_CLASS_CODEBASE.addParamDetail(1, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        LOADER_LOAD_CLASS_RESOLVE = new ReflectionApiMethod(
                2,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.CLASS_TYPE,
                false,
                "loadClass(Ljava/lang/String;Z)Ljava/lang/Class;",
                LOAD_CLASS_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        LOADER_LOAD_CLASS_RESOLVE.addParamDetail(0, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        RMI_LOAD_CLASS = new ReflectionApiMethod(
                1,
                InvocationKind.STATIC,
                ReflectionTargetKind.CLASS_TYPE,
                false,
                "loadClass(Ljava/lang/String;)Ljava/lang/Class;",
                CLASS_NAME_ARG_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        RMI_LOAD_CLASS.addParamDetail(0, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        RMI_LOAD_CLASS_URL = new ReflectionApiMethod(
                2,
                InvocationKind.STATIC,
                ReflectionTargetKind.CLASS_TYPE,
                false,
                "loadClass(Ljava/net/URL;Ljava/lang/String;)Ljava/lang/Class;",
                CLASS_NAME_ARG_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        RMI_LOAD_CLASS_URL.addParamDetail(1, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        RMI_LOAD_CLASS_CODEBASE = new ReflectionApiMethod(
                2,
                InvocationKind.STATIC,
                ReflectionTargetKind.CLASS_TYPE,
                false,
                "loadClass(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Class;",
                CLASS_NAME_ARG_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        RMI_LOAD_CLASS_CODEBASE.addParamDetail(1, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        RMI_LOAD_CLASS_CODEBASE_LOADER = new ReflectionApiMethod(
                2,
                InvocationKind.STATIC,
                ReflectionTargetKind.CLASS_TYPE,
                false,
                "loadClass(Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/Class;",
                LOAD_CLASS_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        RMI_LOAD_CLASS_CODEBASE_LOADER.addParamDetail(1, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        CLASS_GET_DECLARED_FIELD = new ReflectionApiMethod(
                1,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.FIELD_TYPE,
                true,
                "getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;",
                FIELD_LOOKUP_TEMPLATE,
                ReflectionTargetScope.ALL_FIELDS_OF_CLASS
        );
        CLASS_GET_DECLARED_FIELD.addParamDetail(0, ReflectionParamType.FIELD_NAME_PARAM_TYPE, false);
        CLASS_GET_DECLARED_METHOD = new ReflectionApiMethod(
                2,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.METHOD_TYPE,
                true,
                "getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
                GET_METHOD_TEMPLATE,
                ReflectionTargetScope.ALL_METHODS_OF_CLASS
        );
        CLASS_GET_DECLARED_METHOD.addParamDetail(0, ReflectionParamType.METHOD_NAME_PARAM_TYPE, false);
        CLASS_GET_DECLARED_METHOD.addParamDetail(1, ReflectionParamType.CLASS_PARAM_TYPE, true);
        LOADER_DEFINE_CLASS = new ReflectionApiMethod(
                4, InvocationKind.INSTANCE, ReflectionTargetKind.CLASS_TYPE, "defineClass(Ljava/lang/String;[BII)Ljava/lang/Class;"
        );
        LOADER_DEFINE_CLASS.addParamDetail(0, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        LOADER_DEFINE_CLASS_DOMAIN = new ReflectionApiMethod(
                5, InvocationKind.INSTANCE, ReflectionTargetKind.CLASS_TYPE, "defineClass(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)Ljava/lang/Class;"
        );
        LOADER_DEFINE_CLASS_DOMAIN.addParamDetail(0, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        FIND_CLASS = new ReflectionApiMethod(
                1,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.CLASS_TYPE,
                false,
                "findClass(Ljava/lang/String;)Ljava/lang/Class;",
                CLASS_NAME_ARG_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        FIND_CLASS.addParamDetail(0, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        LOADER_FIND_SYSTEM_CLASS = new ReflectionApiMethod(
                1,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.CLASS_TYPE,
                false,
                "findSystemClass(Ljava/lang/String;)Ljava/lang/Class;",
                CLASS_NAME_ARG_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        LOADER_FIND_SYSTEM_CLASS.addParamDetail(0, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        LOADER_FIND_LOADED_CLASS = new ReflectionApiMethod(
                1,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.CLASS_TYPE,
                false,
                "findLoadedClass(Ljava/lang/String;)Ljava/lang/Class;",
                CLASS_NAME_ARG_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        LOADER_FIND_LOADED_CLASS.addParamDetail(0, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        EVENT_SET_DESCRIPTOR_INIT4 = new ReflectionApiMethod(
                4, InvocationKind.CONSTRUCTOR, ReflectionTargetKind.OBJECT_TYPE, "<init>(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;Ljava/lang/String;)V"
        );
        EVENT_SET_DESCRIPTOR_INIT4.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE, false);
        EVENT_SET_DESCRIPTOR_INIT4.addParamDetail(2, ReflectionParamType.CLASS_PARAM_TYPE, false);
        EVENT_SET_DESCRIPTOR_INIT4.addParamDetail(3, ReflectionParamType.METHOD_NAME_PARAM_TYPE, false);
        EVENT_SET_DESCRIPTOR_INIT6 = new ReflectionApiMethod(
                6,
                InvocationKind.CONSTRUCTOR,
                ReflectionTargetKind.OBJECT_TYPE,
                "<init>(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"
        );
        EVENT_SET_DESCRIPTOR_INIT6.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE, false);
        EVENT_SET_DESCRIPTOR_INIT6.addParamDetail(2, ReflectionParamType.CLASS_PARAM_TYPE, false);
        EVENT_SET_DESCRIPTOR_INIT6.addParamDetail(3, ReflectionParamType.METHOD_NAME_PARAM_TYPE, true);
        EVENT_SET_DESCRIPTOR_INIT6.addParamDetail(4, ReflectionParamType.METHOD_NAME_PARAM_TYPE, false);
        EVENT_SET_DESCRIPTOR_INIT6.addParamDetail(5, ReflectionParamType.METHOD_NAME_PARAM_TYPE, false);
        EVENT_SET_DESCRIPTOR_INIT7 = new ReflectionApiMethod(
                7,
                InvocationKind.CONSTRUCTOR,
                ReflectionTargetKind.OBJECT_TYPE,
                "<init>(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"
        );
        EVENT_SET_DESCRIPTOR_INIT7.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE, false);
        EVENT_SET_DESCRIPTOR_INIT7.addParamDetail(2, ReflectionParamType.CLASS_PARAM_TYPE, false);
        EVENT_SET_DESCRIPTOR_INIT7.addParamDetail(3, ReflectionParamType.METHOD_NAME_PARAM_TYPE, true);
        EVENT_SET_DESCRIPTOR_INIT7.addParamDetail(4, ReflectionParamType.METHOD_NAME_PARAM_TYPE, false);
        EVENT_SET_DESCRIPTOR_INIT7.addParamDetail(5, ReflectionParamType.METHOD_NAME_PARAM_TYPE, false);
        EVENT_SET_DESCRIPTOR_INIT7.addParamDetail(6, ReflectionParamType.METHOD_NAME_PARAM_TYPE, false);
        GET_BUNDLE = new ReflectionApiMethod(
                1,
                InvocationKind.STATIC,
                ReflectionTargetKind.BUNDLE_TYPE,
                false,
                "getBundle(Ljava/lang/String;)Ljava/util/ResourceBundle;",
                CLASS_NAME_ARG_TEMPLATE,
                ReflectionTargetScope.ALL_LIST_RESOUCE_BUNDLE
        );
        GET_BUNDLE.addParamDetail(0, ReflectionParamType.CLASS_OR_PROPERTIES_NAME_PARAM_TYPE, false);
        GET_BUNDLE_LOCALE = new ReflectionApiMethod(
                2,
                InvocationKind.STATIC,
                ReflectionTargetKind.BUNDLE_TYPE,
                false,
                "getBundle(Ljava/lang/String;Ljava/util/Locale;)Ljava/util/ResourceBundle;",
                LOAD_CLASS_TEMPLATE,
                ReflectionTargetScope.ALL_LIST_RESOUCE_BUNDLE
        );
        GET_BUNDLE_LOCALE.addParamDetail(0, ReflectionParamType.CLASS_OR_PROPERTIES_NAME_PARAM_TYPE, false);
        GET_BUNDLE_LOADER = new ReflectionApiMethod(
                3,
                InvocationKind.STATIC,
                ReflectionTargetKind.BUNDLE_TYPE,
                false,
                "getBundle(Ljava/lang/String;Ljava/util/Locale;Ljava/lang/ClassLoader;)Ljava/util/ResourceBundle;",
                CLASS_NAME_DEPTH2_TEMPLATE,
                ReflectionTargetScope.ALL_LIST_RESOUCE_BUNDLE
        );
        GET_BUNDLE_LOADER.addParamDetail(0, ReflectionParamType.CLASS_OR_PROPERTIES_NAME_PARAM_TYPE, false);
        BEANS_INSTANTIATE = new ReflectionApiMethod(
                2,
                InvocationKind.STATIC,
                ReflectionTargetKind.DEFAULT_CONSTRUCTOR_CALL_TYPE,
                false,
                "instantiate(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Object;",
                CLASS_NAME_ARG_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        BEANS_INSTANTIATE.addParamDetail(1, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        BEANS_INSTANTIATE_CONTEXT = new ReflectionApiMethod(
                3,
                InvocationKind.STATIC,
                ReflectionTargetKind.DEFAULT_CONSTRUCTOR_CALL_TYPE,
                false,
                "instantiate(Ljava/lang/ClassLoader;Ljava/lang/String;Ljava/beans/beancontext/BeanContext;)Ljava/lang/Object;",
                LOAD_CLASS_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        BEANS_INSTANTIATE_CONTEXT.addParamDetail(1, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        BEANS_INSTANTIATE_INITIALIZER = new ReflectionApiMethod(
                4,
                InvocationKind.STATIC,
                ReflectionTargetKind.DEFAULT_CONSTRUCTOR_CALL_TYPE,
                false,
                "instantiate(Ljava/lang/ClassLoader;Ljava/lang/String;Ljava/beans/beancontext/BeanContext;Ljava/beans/AppletInitializer;)Ljava/lang/Object;",
                CLASS_NAME_DEPTH2_TEMPLATE,
                ReflectionTargetScope.ALL_LIST_RESOUCE_BUNDLE
        );
        BEANS_INSTANTIATE_INITIALIZER.addParamDetail(1, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        BEAN_CONTEXT_INSTANTIATE_CHILD = new ReflectionApiMethod(
                1,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.DEFAULT_CONSTRUCTOR_CALL_TYPE,
                false,
                "instantiateChild(Ljava/lang/String;)Ljava/lang/Object;",
                CLASS_NAME_ARG_TEMPLATE,
                ReflectionTargetScope.ALL_CLASSES
        );
        BEAN_CONTEXT_INSTANTIATE_CHILD.addParamDetail(0, ReflectionParamType.CLASS_NAME_PARAM_TYPE, false);
        CLASS_NEW_INSTANCE = new ReflectionApiMethod(
                0, InvocationKind.INSTANCE, ReflectionTargetKind.DEFAULT_CONSTRUCTOR_CALL_TYPE, true, true, "newInstance()Ljava/lang/Object;"
        );
        CLASS_GET_CONSTRUCTOR = new ReflectionApiMethod(
                0,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.SPECIFIC_CONSTRUCTOR_CALL_TYPE,
                true,
                true,
                "getConstructor([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;"
        );
        CLASS_GET_CONSTRUCTOR.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE, true);
        CLASS_GET_CONSTRUCTORS = new ReflectionApiMethod(
                1, InvocationKind.INSTANCE, ReflectionTargetKind.ALL_CONSTRUCTORS_CALL_TYPE, true, true, "getConstructors()[Ljava/lang/reflect/Constructor;"
        );
        PACKAGE_GET_PACKAGE = new ReflectionApiMethod(
                1, InvocationKind.STATIC, ReflectionTargetKind.PACKAGE_TYPE, "getPackage(Ljava/lang/String;)Ljava/lang/Package;"
        );
        PACKAGE_GET_PACKAGE.addParamDetail(0, ReflectionParamType.PACKAGE_NAME_PARAM_TYPE, false);
        LOADER_GET_PACKAGE = new ReflectionApiMethod(
                1, InvocationKind.INSTANCE, ReflectionTargetKind.PACKAGE_TYPE, "getPackage(Ljava/lang/String;)Ljava/lang/Package;"
        );
        LOADER_GET_PACKAGE.addParamDetail(0, ReflectionParamType.PACKAGE_NAME_PARAM_TYPE, false);
        LOOKUP_FIND_STATIC = new ReflectionApiMethod(
                3,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.METHOD_HANDLE_TYPE,
                false,
                true,
                "findStatic(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;",
                FIND_METHOD_NAME_TEMPLATE,
                ReflectionTargetScope.ALL_STATIC_METHODS_ACCESSIBLE_FROM_CLASS
        );
        LOOKUP_FIND_STATIC.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE);
        LOOKUP_FIND_STATIC.addParamDetail(1, ReflectionParamType.METHOD_NAME_PARAM_TYPE, false);
        LOOKUP_FIND_STATIC.addParamDetail(2, ReflectionParamType.METHOD_TYPE_PARAM_TYPE, false);
        LOOKUP_FIND_VIRTUAL = new ReflectionApiMethod(
                3,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.METHOD_HANDLE_TYPE,
                false,
                true,
                "findVirtual(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;",
                FIND_METHOD_NAME_TEMPLATE,
                ReflectionTargetScope.ALL_VIRTUAL_METHODS_ACCESSIBLE_FROM_CLASS
        );
        LOOKUP_FIND_VIRTUAL.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE);
        LOOKUP_FIND_VIRTUAL.addParamDetail(1, ReflectionParamType.METHOD_NAME_PARAM_TYPE, false);
        LOOKUP_FIND_VIRTUAL.addParamDetail(2, ReflectionParamType.METHOD_TYPE_PARAM_TYPE, false);
        LOOKUP_FIND_SPECIAL = new ReflectionApiMethod(
                4,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.METHOD_HANDLE_TYPE,
                false,
                true,
                "findSpecial(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                FIND_SPECIAL_TEMPLATE,
                ReflectionTargetScope.ALL_VIRTUAL_METHODS_ACCESSIBLE_FROM_CLASS
        );
        LOOKUP_FIND_SPECIAL.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE, false);
        LOOKUP_FIND_SPECIAL.addParamDetail(1, ReflectionParamType.METHOD_NAME_PARAM_TYPE, false);
        LOOKUP_FIND_SPECIAL.addParamDetail(2, ReflectionParamType.METHOD_TYPE_PARAM_TYPE, false);
        LOOKUP_FIND_SPECIAL.addParamDetail(3, ReflectionParamType.CLASS_PARAM_TYPE);
        LOOKUP_FIND_CONSTRUCTOR = new ReflectionApiMethod(
                2,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.METHOD_HANDLE_TYPE,
                false,
                true,
                "findConstructor(Ljava/lang/Class;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;"
        );
        LOOKUP_FIND_CONSTRUCTOR.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE);
        LOOKUP_FIND_CONSTRUCTOR.addParamDetail(1, ReflectionParamType.METHOD_TYPE_PARAM_TYPE, false);
        LOOKUP_FIND_GETTER = new ReflectionApiMethod(
                3,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.METHOD_HANDLE_TYPE,
                false,
                true,
                "findGetter(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                FIND_FIELD_NAME_TEMPLATE,
                ReflectionTargetScope.ALL_INSTANCE_FIELDS_ACCESSIBLE_FROM_CLASS
        );
        LOOKUP_FIND_GETTER.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE);
        LOOKUP_FIND_GETTER.addParamDetail(1, ReflectionParamType.FIELD_NAME_PARAM_TYPE, false);
        LOOKUP_FIND_GETTER.addParamDetail(2, ReflectionParamType.CLASS_PARAM_TYPE, false);
        LOOKUP_FIND_SETTER = new ReflectionApiMethod(
                3,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.METHOD_HANDLE_TYPE,
                false,
                true,
                "findSetter(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                FIND_FIELD_NAME_TEMPLATE,
                ReflectionTargetScope.ALL_INSTANCE_FIELDS_ACCESSIBLE_FROM_CLASS
        );
        LOOKUP_FIND_SETTER.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE);
        LOOKUP_FIND_SETTER.addParamDetail(1, ReflectionParamType.FIELD_NAME_PARAM_TYPE, false);
        LOOKUP_FIND_SETTER.addParamDetail(2, ReflectionParamType.CLASS_PARAM_TYPE, false);
        LOOKUP_FIND_STATIC_GETTER = new ReflectionApiMethod(
                3,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.METHOD_HANDLE_TYPE,
                false,
                true,
                "findStaticGetter(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                FIND_FIELD_NAME_TEMPLATE,
                ReflectionTargetScope.ALL_STATIC_FIELDS_ACCESSIBLE_FROM_CLASS
        );
        LOOKUP_FIND_STATIC_GETTER.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE);
        LOOKUP_FIND_STATIC_GETTER.addParamDetail(1, ReflectionParamType.FIELD_NAME_PARAM_TYPE, false);
        LOOKUP_FIND_STATIC_GETTER.addParamDetail(2, ReflectionParamType.CLASS_PARAM_TYPE, false);
        LOOKUP_FIND_STATIC_SETTER = new ReflectionApiMethod(
                3,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.METHOD_HANDLE_TYPE,
                false,
                true,
                "findStaticSetter(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                FIND_FIELD_NAME_TEMPLATE,
                ReflectionTargetScope.ALL_STATIC_FIELDS_ACCESSIBLE_FROM_CLASS
        );
        LOOKUP_FIND_STATIC_SETTER.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE);
        LOOKUP_FIND_STATIC_SETTER.addParamDetail(1, ReflectionParamType.FIELD_NAME_PARAM_TYPE, false);
        LOOKUP_FIND_STATIC_SETTER.addParamDetail(2, ReflectionParamType.CLASS_PARAM_TYPE, false);
        LOOKUP_FIND_VAR_HANDLE = new ReflectionApiMethod(
                3,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.METHOD_HANDLE_TYPE,
                false,
                true,
                "findVarHandle(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/VarHandle;",
                FIND_FIELD_NAME_TEMPLATE,
                ReflectionTargetScope.ALL_INSTANCE_FIELDS_ACCESSIBLE_FROM_CLASS
        );
        LOOKUP_FIND_VAR_HANDLE.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE);
        LOOKUP_FIND_VAR_HANDLE.addParamDetail(1, ReflectionParamType.FIELD_NAME_PARAM_TYPE, false);
        LOOKUP_FIND_VAR_HANDLE.addParamDetail(2, ReflectionParamType.CLASS_PARAM_TYPE, false);
        LOOKUP_FIND_STATIC_VAR_HANDLE = new ReflectionApiMethod(
                3,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.METHOD_HANDLE_TYPE,
                false,
                true,
                "findStaticVarHandle(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/VarHandle;",
                FIND_FIELD_NAME_TEMPLATE,
                ReflectionTargetScope.ALL_STATIC_FIELDS_ACCESSIBLE_FROM_CLASS
        );
        LOOKUP_FIND_STATIC_VAR_HANDLE.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE);
        LOOKUP_FIND_STATIC_VAR_HANDLE.addParamDetail(1, ReflectionParamType.FIELD_NAME_PARAM_TYPE, false);
        LOOKUP_FIND_STATIC_VAR_HANDLE.addParamDetail(2, ReflectionParamType.CLASS_PARAM_TYPE, false);
        LOOKUP_BIND = new ReflectionApiMethod(
                3,
                InvocationKind.INSTANCE,
                ReflectionTargetKind.METHOD_HANDLE_TYPE,
                false,
                true,
                "bind(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;",
                FIND_METHOD_NAME_TEMPLATE,
                ReflectionTargetScope.ALL_VIRTUAL_METHODS_ACCESSIBLE_FROM_CLASS
        );
        LOOKUP_BIND.addParamDetail(0, ReflectionParamType.OBJECT_PARAM_TYPE, false);
        LOOKUP_BIND.addParamDetail(1, ReflectionParamType.METHOD_NAME_PARAM_TYPE, false);
        LOOKUP_BIND.addParamDetail(2, ReflectionParamType.METHOD_TYPE_PARAM_TYPE, false);
        REFERENCE_FIELD_UPDATER_NEW = new ReflectionApiMethod(
                3,
                InvocationKind.STATIC,
                ReflectionTargetKind.FIELD_TYPE,
                false,
                true,
                "newUpdater(Ljava/lang/Class;Ljava/lang/Class;Ljava/lang/String;)Ljava/util/concurrent/atomic/AtomicReferenceFieldUpdater;",
                REF_FIELD_UPDATER_TEMPLATE,
                ReflectionTargetScope.ALL_INSTANCE_FIELDS_OF_CLASS
        );
        REFERENCE_FIELD_UPDATER_NEW.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE);
        ReflectionApiMethod reflectionApiMethod = REFERENCE_FIELD_UPDATER_NEW;
        ReflectionParamType reflectionParamType = ReflectionParamType.CLASS_PARAM_TYPE;
        Boolean boolean3 = true;
        Boolean boolean2 = false;
        Boolean boolean1 = false;
        reflectionApiMethod.addParamDetail(1, reflectionParamType, boolean1, boolean2, boolean3);
        REFERENCE_FIELD_UPDATER_NEW.addParamDetail(2, ReflectionParamType.FIELD_NAME_PARAM_TYPE, false);
        INTEGER_FIELD_UPDATER_NEW = new ReflectionApiMethod(
                2,
                InvocationKind.STATIC,
                ReflectionTargetKind.FIELD_TYPE,
                false,
                true,
                "newUpdater(Ljava/lang/Class;Ljava/lang/String;)Ljava/util/concurrent/atomic/AtomicIntegerFieldUpdater;",
                FIELD_LOOKUP_TEMPLATE,
                ReflectionTargetScope.ALL_INSTANCE_FIELDS_OF_CLASS
        );
        INTEGER_FIELD_UPDATER_NEW.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE);
        INTEGER_FIELD_UPDATER_NEW.addParamDetail(1, ReflectionParamType.FIELD_NAME_PARAM_TYPE, false);
        LONG_FIELD_UPDATER_NEW = new ReflectionApiMethod(
                2,
                InvocationKind.STATIC,
                ReflectionTargetKind.FIELD_TYPE,
                false,
                true,
                "newUpdater(Ljava/lang/Class;Ljava/lang/String;)Ljava/util/concurrent/atomic/AtomicLongFieldUpdater;",
                FIELD_LOOKUP_TEMPLATE,
                ReflectionTargetScope.ALL_INSTANCE_FIELDS_OF_CLASS
        );
        LONG_FIELD_UPDATER_NEW.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE);
        LONG_FIELD_UPDATER_NEW.addParamDetail(1, ReflectionParamType.FIELD_NAME_PARAM_TYPE, false);
        METHOD_TYPE_RETURN = new ReflectionApiMethod(
                1, InvocationKind.STATIC, ReflectionTargetKind.METHOD_TYPE_TYPE, false, false, "methodType(Ljava/lang/Class;)Ljava/lang/invoke/MethodType;"
        );
        METHOD_TYPE_RETURN.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE, false);
        METHOD_TYPE_RETURN_PARAM = new ReflectionApiMethod(
                2,
                InvocationKind.STATIC,
                ReflectionTargetKind.METHOD_TYPE_TYPE,
                false,
                false,
                "methodType(Ljava/lang/Class;Ljava/lang/Class;)Ljava/lang/invoke/MethodType;"
        );
        METHOD_TYPE_RETURN_PARAM.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE, false);
        METHOD_TYPE_RETURN_PARAM.addParamDetail(1, ReflectionParamType.CLASS_PARAM_TYPE, false);
        METHOD_TYPE_RETURN_PARAMS = new ReflectionApiMethod(
                2,
                InvocationKind.STATIC,
                ReflectionTargetKind.METHOD_TYPE_TYPE,
                false,
                false,
                "methodType(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;"
        );
        METHOD_TYPE_RETURN_PARAMS.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE, false);
        METHOD_TYPE_RETURN_PARAMS.addParamDetail(1, ReflectionParamType.CLASS_PARAM_TYPE, true);
        METHOD_TYPE_RETURN_PARAM_PARAMS = new ReflectionApiMethod(
                3,
                InvocationKind.STATIC,
                ReflectionTargetKind.METHOD_TYPE_TYPE,
                false,
                false,
                "methodType(Ljava/lang/Class;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;"
        );
        METHOD_TYPE_RETURN_PARAM_PARAMS.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE, false);
        METHOD_TYPE_RETURN_PARAM_PARAMS.addParamDetail(1, ReflectionParamType.CLASS_PARAM_TYPE, false);
        METHOD_TYPE_RETURN_PARAM_PARAMS.addParamDetail(2, ReflectionParamType.CLASS_PARAM_TYPE, true);
        METHOD_TYPE_RETURN_TYPE = new ReflectionApiMethod(
                2,
                InvocationKind.STATIC,
                ReflectionTargetKind.METHOD_TYPE_TYPE,
                false,
                false,
                "methodType(Ljava/lang/Class;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodType;",
                null,
                ReflectionTargetScope.NONE
        );
        METHOD_TYPE_RETURN_TYPE.addParamDetail(0, ReflectionParamType.CLASS_PARAM_TYPE, false);
        METHOD_TYPE_RETURN_TYPE.addParamDetail(1, ReflectionParamType.METHOD_TYPE_PARAM_TYPE, false);
        REFLECTION_METHODS_BY_SIGNATURE = new TwoKeyMap(21, 5);
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "getFunctionalInterfaceMethodName()Ljava/lang/String;", "java/lang/invoke/SerializedLambda", LAMBDA_GET_FUNCTIONAL_METHOD_NAME
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue("getName()Ljava/lang/String;", "java/lang/Class", GET_NAME);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("getName()Ljava/lang/String;", "java/lang/Package", GET_NAME);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("getName()Ljava/lang/String;", "java/lang/reflect/Field", GET_NAME);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("getName()Ljava/lang/String;", "java/lang/reflect/Method", GET_NAME);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("getName()Ljava/lang/String;", "java/lang/reflect/Constructor", GET_NAME);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("getName()Ljava/lang/String;", "java/lang/reflect/Member", GET_NAME);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("forName(Ljava/lang/String;)Ljava/lang/Class;", "java/lang/Class", CLASS_FOR_NAME);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("forName(Ljava/lang/Module;Ljava/lang/String;)Ljava/lang/Class;", "java/lang/Class", CLASS_FOR_NAME_MODULE);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("forName(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;", "java/lang/Class", CLASS_FOR_NAME_LOADER);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("getField(Ljava/lang/String;)Ljava/lang/reflect/Field;", "java/lang/Class", CLASS_GET_FIELD);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", "java/lang/Class", CLASS_GET_METHOD);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;", "java/lang/Class", CLASS_GET_DECLARED_FIELD);
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", "java/lang/Class", CLASS_GET_DECLARED_METHOD
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue("loadClass(Ljava/lang/String;)Ljava/lang/Class;", "java/lang/ClassLoader", LOADER_LOAD_CLASS);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("loadClass(Ljava/lang/String;Z)Ljava/lang/Class;", "java/lang/ClassLoader", LOADER_LOAD_CLASS_RESOLVE);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("defineClass(Ljava/lang/String;[BII)Ljava/lang/Class;", "java/lang/ClassLoader", LOADER_DEFINE_CLASS);
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "defineClass(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)Ljava/lang/Class;", "java/lang/ClassLoader", LOADER_DEFINE_CLASS_DOMAIN
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue("findClass(Ljava/lang/String;)Ljava/lang/Class;", "java/lang/ClassLoader", FIND_CLASS);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("findClass(Ljava/lang/String;)Ljava/lang/Class;", "java/lang/invoke/MethodHandles$Lookup", FIND_CLASS);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("findSystemClass(Ljava/lang/String;)Ljava/lang/Class;", "java/lang/ClassLoader", LOADER_FIND_SYSTEM_CLASS);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("findLoadedClass(Ljava/lang/String;)Ljava/lang/Class;", "java/lang/ClassLoader", LOADER_FIND_LOADED_CLASS);
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "<init>(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;Ljava/lang/String;)V", "java/beans/EventSetDescriptor", EVENT_SET_DESCRIPTOR_INIT4
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "<init>(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                "java/beans/EventSetDescriptor",
                EVENT_SET_DESCRIPTOR_INIT6
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "<init>(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                "java/beans/EventSetDescriptor",
                EVENT_SET_DESCRIPTOR_INIT7
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue("loadClass(Ljava/lang/String;)Ljava/lang/Class;", "java/rmi/server/RMIClassLoader", RMI_LOAD_CLASS);
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "loadClass(Ljava/net/URL;Ljava/lang/String;)Ljava/lang/Class;", "java/rmi/server/RMIClassLoader", RMI_LOAD_CLASS_URL
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "loadClass(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Class;", "java/rmi/server/RMIClassLoader", RMI_LOAD_CLASS_CODEBASE
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "loadClass(Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/Class;",
                "java/rmi/server/RMIClassLoader",
                RMI_LOAD_CLASS_CODEBASE_LOADER
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue("loadClass(Ljava/lang/String;)Ljava/lang/Class;", "java/rmi/server/LoaderHandler", LOADER_LOAD_CLASS);
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "loadClass(Ljava/net/URL;Ljava/lang/String;)Ljava/lang/Class;", "java/rmi/server/LoaderHandler", LOADER_HANDLER_LOAD_CLASS_URL
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue("getBundle(Ljava/lang/String;)Ljava/util/ResourceBundle;", "java/util/ResourceBundle", GET_BUNDLE);
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "getBundle(Ljava/lang/String;Ljava/util/Locale;)Ljava/util/ResourceBundle;", "java/util/ResourceBundle", GET_BUNDLE_LOCALE
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "getBundle(Ljava/lang/String;Ljava/util/Locale;Ljava/lang/ClassLoader;)Ljava/util/ResourceBundle;", "java/util/ResourceBundle", GET_BUNDLE_LOADER
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "instantiate(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Object;", "java/beans/Beans", BEANS_INSTANTIATE
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "instantiate(Ljava/lang/ClassLoader;Ljava/lang/String;Ljava/beans/beancontext/BeanContext;)Ljava/lang/Object;",
                "java/beans/Beans",
                BEANS_INSTANTIATE_CONTEXT
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "instantiate(Ljava/lang/ClassLoader;Ljava/lang/String;Ljava/beans/beancontext/BeanContext;Ljava/beans/AppletInitializer;)Ljava/lang/Object;",
                "java/beans/Beans",
                BEANS_INSTANTIATE_INITIALIZER
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "instantiateChild(Ljava/lang/String;)Ljava/lang/Object;", "java/beans/beancontext/BeanContext", BEAN_CONTEXT_INSTANTIATE_CHILD
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue("newInstance()Ljava/lang/Object;", "java/lang/Class", CLASS_NEW_INSTANCE);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("getConstructor([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;", "java/lang/Class", CLASS_GET_CONSTRUCTOR);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("getConstructors()[Ljava/lang/reflect/Constructor;", "java/lang/Class", CLASS_GET_CONSTRUCTORS);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("getPackage(Ljava/lang/String;)Ljava/lang/Package;", "java/lang/Package", PACKAGE_GET_PACKAGE);
        REFLECTION_METHODS_BY_SIGNATURE.putValue("getPackage(Ljava/lang/String;)Ljava/lang/Package;", "java/lang/ClassLoader", LOADER_GET_PACKAGE);
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "findStatic(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;",
                "java/lang/invoke/MethodHandles$Lookup",
                LOOKUP_FIND_STATIC
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "findVirtual(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;",
                "java/lang/invoke/MethodHandles$Lookup",
                LOOKUP_FIND_VIRTUAL
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "findSpecial(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                "java/lang/invoke/MethodHandles$Lookup",
                LOOKUP_FIND_SPECIAL
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "findConstructor(Ljava/lang/Class;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;",
                "java/lang/invoke/MethodHandles$Lookup",
                LOOKUP_FIND_CONSTRUCTOR
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "findGetter(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                "java/lang/invoke/MethodHandles$Lookup",
                LOOKUP_FIND_GETTER
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "findSetter(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                "java/lang/invoke/MethodHandles$Lookup",
                LOOKUP_FIND_SETTER
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "findStaticGetter(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                "java/lang/invoke/MethodHandles$Lookup",
                LOOKUP_FIND_STATIC_GETTER
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "findStaticSetter(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;",
                "java/lang/invoke/MethodHandles$Lookup",
                LOOKUP_FIND_STATIC_SETTER
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "findVarHandle(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/VarHandle;",
                "java/lang/invoke/MethodHandles$Lookup",
                LOOKUP_FIND_VAR_HANDLE
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "findStaticVarHandle(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/VarHandle;",
                "java/lang/invoke/MethodHandles$Lookup",
                LOOKUP_FIND_STATIC_VAR_HANDLE
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "bind(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;",
                "java/lang/invoke/MethodHandles$Lookup",
                LOOKUP_BIND
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "newUpdater(Ljava/lang/Class;Ljava/lang/Class;Ljava/lang/String;)Ljava/util/concurrent/atomic/AtomicReferenceFieldUpdater;",
                "java/util/concurrent/atomic/AtomicReferenceFieldUpdater",
                REFERENCE_FIELD_UPDATER_NEW
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "newUpdater(Ljava/lang/Class;Ljava/lang/String;)Ljava/util/concurrent/atomic/AtomicIntegerFieldUpdater;",
                "java/util/concurrent/atomic/AtomicIntegerFieldUpdater",
                INTEGER_FIELD_UPDATER_NEW
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "newUpdater(Ljava/lang/Class;Ljava/lang/String;)Ljava/util/concurrent/atomic/AtomicLongFieldUpdater;",
                "java/util/concurrent/atomic/AtomicLongFieldUpdater",
                LONG_FIELD_UPDATER_NEW
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue("methodType(Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", "java/lang/invoke/MethodType", METHOD_TYPE_RETURN);
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "methodType(Ljava/lang/Class;Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", "java/lang/invoke/MethodType", METHOD_TYPE_RETURN_PARAM
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "methodType(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", "java/lang/invoke/MethodType", METHOD_TYPE_RETURN_PARAMS
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "methodType(Ljava/lang/Class;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;",
                "java/lang/invoke/MethodType",
                METHOD_TYPE_RETURN_PARAM_PARAMS
        );
        REFLECTION_METHODS_BY_SIGNATURE.putValue(
                "methodType(Ljava/lang/Class;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodType;", "java/lang/invoke/MethodType", METHOD_TYPE_RETURN_TYPE
        );
        SUBTYPE_MATCHED_SIGNATURES = ZkmUtils.createHashSet(31);
        SUBTYPE_MATCHED_SIGNATURES.add("getPackage(Ljava/lang/String;)Ljava/lang/Package;");
        SUBTYPE_MATCHED_SIGNATURES.add("<init>(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;Ljava/lang/String;)V");
        SUBTYPE_MATCHED_SIGNATURES.add("<init>(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
        SUBTYPE_MATCHED_SIGNATURES.add(
                "<init>(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"
        );
        SUBTYPE_MATCHED_SIGNATURES.add("defineClass(Ljava/lang/String;[BII)Ljava/lang/Class;");
        SUBTYPE_MATCHED_SIGNATURES.add("defineClass(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)Ljava/lang/Class;");
        SUBTYPE_MATCHED_SIGNATURES.add("findLoadedClass(Ljava/lang/String;)Ljava/lang/Class;");
        SUBTYPE_MATCHED_SIGNATURES.add("findClass(Ljava/lang/String;)Ljava/lang/Class;");
        SUBTYPE_MATCHED_SIGNATURES.add("findSystemClass(Ljava/lang/String;)Ljava/lang/Class;");
        SUBTYPE_MATCHED_SIGNATURES.add("loadClass(Ljava/lang/String;)Ljava/lang/Class;");
        SUBTYPE_MATCHED_SIGNATURES.add("loadClass(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Class;");
        SUBTYPE_MATCHED_SIGNATURES.add("loadClass(Ljava/lang/String;Z)Ljava/lang/Class;");
        SUBTYPE_MATCHED_SIGNATURES.add("loadClass(Ljava/net/URL;Ljava/lang/String;)Ljava/lang/Class;");
        SUBTYPE_MATCHED_SIGNATURES.add("getBundle(Ljava/lang/String;)Ljava/util/ResourceBundle;");
        SUBTYPE_MATCHED_SIGNATURES.add("getBundle(Ljava/lang/String;Ljava/util/Locale;)Ljava/util/ResourceBundle;");
        SUBTYPE_MATCHED_SIGNATURES.add("getBundle(Ljava/lang/String;Ljava/util/Locale;Ljava/lang/ClassLoader;)Ljava/util/ResourceBundle;");
        SUBTYPE_MATCHED_SIGNATURES.add("instantiate(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Object;");
        SUBTYPE_MATCHED_SIGNATURES.add("instantiate(Ljava/lang/ClassLoader;Ljava/lang/String;Ljava/beans/beancontext/BeanContext;)Ljava/lang/Object;");
        SUBTYPE_MATCHED_SIGNATURES.add(
                "instantiate(Ljava/lang/ClassLoader;Ljava/lang/String;Ljava/beans/beancontext/BeanContext;Ljava/beans/AppletInitializer;)Ljava/lang/Object;"
        );
        SUBTYPE_MATCHED_SIGNATURES.add("instantiateChild(Ljava/lang/String;)Ljava/lang/Object;");
    }


    public final int getArgumentSlotCount() {
        List list1 = ConstantPoolEntry.getParameterTypes(this.nameAndType.getDescriptor());
        int ba = list1.size();
        int bb = ba;

        for (int i = 0; i < ba; i++) {
            String string = (String) list1.get(i);
            if (string.equals("J") || string.equals("D")) {
                bb++;
            }
        }

        return bb;
    }

    public int getReturnValueCount() {
        return ConstantPoolEntry.getReturnDescriptor(this.nameAndType.getDescriptor()).equals("V") ? 0 : 1;
    }

    public AbstractMethodInfo lookupMethod(
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            ObservableHolder observableHolder,
            ObservableHolder observableHolder1,
            ObservableHolder observableHolder2,
            IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException {
        String string = this.getMemberName();
        String string1 = this.getDescriptor();
        MethodSignature methodSignature1 = new MethodSignature(string, string1);
        AbstractMethodInfo abstractMethodInfo = null;
        String string2 = this.getReferencedClassName();
        String string3 = string2;
        ClassFileBase classFileBase = this.getOwnerClass();
        Integer integer = classFileBase.hasReleaseVersion() ? classFileBase.getReleaseVersion() : null;
        if (MethodSignature.isSignaturePolymorphic(string2, string, classMemberLookup1) && this.getOwnerClass().supportsJava7()) {
            ClasspathClassFile classpathClassFile = (ClasspathClassFile) classResolver1.getVersionedClass(
                    string2, integer, "looking for class '" + string2 + "' which is referenced in class '" + this.getOwnerFilePath() + "'"
            );
            return classpathClassFile.getOrCreateMethod(methodSignature1);
        }

        ClassFileBase classFileBase1 = null;
        HashSet hashSet = null;
        if (string3.startsWith("[")) {
            string3 = "java/lang/Object";
        }

        String string4 = "looking for method '"
                + methodSignature1.formatDeclaration((Map) null)
                + "' in class '"
                + ZkmUtils.slashesToDots(string2)
                + "' which is referenced in class '"
                + this.getOwnerFilePath()
                + "'";
        ClassFileBase classFileBase2 = classResolver1.getVersionedClass(string3, integer, string4, ignoreMissingReferencesSpec1);

        while (abstractMethodInfo == null && classFileBase2 != null) {
            abstractMethodInfo = findMethodInClass(classFileBase2, methodSignature1, classMemberLookup1);
            if (abstractMethodInfo == null) {
                if (classFileBase1 == null) {
                    classFileBase1 = classFileBase2;
                    hashSet = ZkmUtils.createHashSet();
                }

                hashSet.add(classFileBase2);
            }

            string3 = classFileBase2.getSuperclassName();
            if (string3 == null) {
                break;
            }

            Integer integer1 = classFileBase2.hasReleaseVersion() ? classFileBase2.getReleaseVersion() : integer;
            classFileBase2 = classResolver1.getVersionedClass(string3, integer1, string4, ignoreMissingReferencesSpec1);
        }

        if (abstractMethodInfo == null) {
            classFileBase2 = classFileBase1;

            while (abstractMethodInfo == null && classFileBase2 != null) {
                abstractMethodInfo = findMethodInSuperInterfaces(
                        classFileBase2, methodSignature1, integer, classMemberLookup1, classResolver1, hashSet, string4, ignoreMissingReferencesSpec1
                );
                if (abstractMethodInfo == null) {
                    string3 = classFileBase2.getSuperclassName();
                    if (string3 == null) {
                        break;
                    }

                    Integer integer2 = classFileBase2.hasReleaseVersion() ? classFileBase2.getReleaseVersion() : integer;
                    classFileBase2 = classResolver1.getVersionedClass(string3, integer2, string4, ignoreMissingReferencesSpec1);
                }
            }
        }

        observableHolder.setValue(string3);
        observableHolder1.setValue(classFileBase1);
        observableHolder2.setValue(hashSet);
        return abstractMethodInfo;
    }

    public List getDescriptorProgramClasses() {
        long ba = 84140148338121L;
        ba = 115200824007016L ^ ba;
        long bb = ba ^ 23945335188930L;
        String string = this.nameAndType.getDescriptor();
        return ConstantPoolEntry.getReferencedProgramClasses(string);
    }

    public ReflectionApiMethod findReflectionApiMethod(ClassMemberLookup classMemberLookup1) throws ZkmException, IOException {
        String string = this.getSignatureString();
        String string1 = this.getReferencedClassName();
        Map map1 = REFLECTION_METHODS_BY_SIGNATURE.getInnerMap(string);
        if (map1 == null) {
            return null;
        }

        if (map1.containsKey(string1)) {
            return (ReflectionApiMethod) map1.get(string1);
        }

        if (!SUBTYPE_MATCHED_SIGNATURES.contains(string)) {
            return null;
        }

        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string2 = (String) entry.getKey();
            if (!string2.equals("java/rmi/server/LoaderHandler") && !string2.equals("java/beans/beancontext/BeanContext")) {
                if (classMemberLookup1.isSubclass(string1, string2)) {
                    return (ReflectionApiMethod) entry.getValue();
                }
            } else if (classMemberLookup1.implementsInterface(string1, string2)) {
                return (ReflectionApiMethod) entry.getValue();
            }
        }

        return null;
    }

    public final boolean isConstructor() {
        return this.nameAndType.nameUtf8.getValue().equals("<init>");
    }

    @Override
    public final void resolveMember(ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1) throws ZkmException, IOException {
        MethodSignature methodSignature1 = new MethodSignature(this.getMemberName(), this.getDescriptor());
        ObservableHolder observableHolder = new ObservableHolder();
        ObservableHolder observableHolder1 = new ObservableHolder();
        this.resolvedMember = this.lookupMethod(
                classMemberLookup1, classResolver1, new ObservableHolder(), observableHolder, observableHolder1, ignoreMissingReferencesSpec1
        );
        ClassFileBase classFileBase = (ClassFileBase) observableHolder.getValue();
        Set set1 = (Set) observableHolder1.getValue();
        if (this.resolvedMember == null) {
            if (classFileBase != null) {
                ObservableHolder observableHolder2 = new ObservableHolder();
                if ((!HiddenOptionFlags.IGNORE_MISSING_MEMBERS || classFileBase.isProgramClass())
                        && (
                        ignoreMissingReferencesSpec1 == null
                                || !ignoreMissingReferencesSpec1.isMissingMethodIgnored(classFileBase, this.getMemberName(), this.getDescriptor(), observableHolder2)
                )) {
                    throw new MemberNotFoundException(
                            "Could not find method '"
                                    + methodSignature1.toDisplayString()
                                    + "' in class '"
                                    + classFileBase.getDisplayLocationName()
                                    + "' ("
                                    + classFileBase.getDottedClassName()
                                    + ") or in its hierarchy. Such a reference occurs in class '"
                                    + this.getOwnerLocationDescription()
                                    + "'. Please check the classpath and reopen your classes (A)."
                    );
                }

                if (!observableHolder2.isValueNull() && ignoreMissingReferencesSpec1 != null) {
                    ignoreMissingReferencesSpec1.getScriptEnvironment()
                            .logMessage(
                                    "Method '"
                                            + methodSignature1.toDisplayString()
                                            + "' in class '"
                                            + classFileBase.getDottedClassName()
                                            + "' could not be found but it has been specified as able to be ignored. : '"
                                            + (String) observableHolder2.getValue()
                                            + "'",
                                    true
                            );
                }
            }
        } else if (set1 != null && this.resolvedMember.isStatic()) {
            Iterator iterator = set1.iterator();

            while (iterator.hasNext()) {
                ClassFileBase classFileBase1 = (ClassFileBase) iterator.next();
                if (classFileBase1.isProgramClass()) {
                    ((ProgramClass) classFileBase1).addInheritedMethodReferrer((AbstractMethodInfo) this.resolvedMember, classFileBase);
                }
            }
        }
    }

    public final String getStackReturnType() {
        return getStackReturnType(this.nameAndType);
    }

    public final List getStackParameterTypes() {
        return getStackParameterTypes(this.nameAndType);
    }

    public static AbstractMethodInfo findMethodInSuperInterfaces(
            ClassFileBase classFileBase,
            MethodSignature methodSignature1,
            Integer integer,
            ClassMemberLookup classMemberLookup1,
            ClassResolver classResolver1,
            HashSet hashSet,
            String string,
            IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException {
        Integer integer1 = classFileBase.hasReleaseVersion() ? classFileBase.getReleaseVersion() : integer;
        String[] strings = classFileBase.getInterfaceNames();

        for (int i = 0; i < strings.length; i++) {
            ClassFileBase classFileBase1 = classResolver1.getVersionedClass(strings[i], integer1, string, ignoreMissingReferencesSpec1);
            if (classFileBase1 != null) {
                AbstractMethodInfo abstractMethodInfo = findMethodInClass(classFileBase1, methodSignature1, classMemberLookup1);
                if (abstractMethodInfo != null) {
                    return abstractMethodInfo;
                }

                if (hashSet.add(classFileBase1)) {
                    abstractMethodInfo = findMethodInSuperInterfaces(
                            classFileBase1, methodSignature1, integer, classMemberLookup1, classResolver1, hashSet, string, ignoreMissingReferencesSpec1
                    );
                    if (abstractMethodInfo != null) {
                        return abstractMethodInfo;
                    }
                }
            }
        }

        return null;
    }

    public ResolvedMethodRef(
            ConstantMemberRef constantMemberRef, ResolvedClassConstant resolvedClassConstant, ResolvedNameAndType resolvedNameAndType, ListMultimap listMultimap
    ) {
        super(constantMemberRef, resolvedClassConstant, resolvedNameAndType, listMultimap);
    }

    public List getParameterTypes() {
        return ConstantPoolEntry.getParameterTypes(this.nameAndType.getDescriptor());
    }

    public ReflectionApiMethod getExactReflectionApiMethod() {
        String string = this.getSignatureString();
        String string1 = this.getReferencedClassName();
        return (ReflectionApiMethod) REFLECTION_METHODS_BY_SIGNATURE.getValue(string, string1);
    }

    public ResolvedMethodRef(
            AbstractConstantPool abstractConstantPool,
            ResolvedClassConstant resolvedClassConstant,
            ResolvedNameAndType resolvedNameAndType,
            AbstractMethodInfo abstractMethodInfo
    ) {
        super(abstractConstantPool, resolvedClassConstant, resolvedNameAndType, abstractMethodInfo);
    }

    public String getSignatureString() {
        return this.getMemberName() + this.getDescriptor();
    }
}
