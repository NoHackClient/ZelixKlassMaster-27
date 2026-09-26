package com.zelix.klassmaster.obfuscator.reflection;

public class ReflectionParamDetail {
    public final int position;
    public final ReflectionParamType paramType;
    public final boolean arrayParam;
    public final boolean targetClassParam;
    public final boolean fieldTypeParam;

    private ReflectionParamDetail(int position, ReflectionParamType reflectionParamType, boolean arrayParam, boolean targetClassParam, boolean fieldTypeParam) {
        this.position = position;
        this.paramType = reflectionParamType;
        this.arrayParam = arrayParam;
        this.targetClassParam = targetClassParam;
        this.fieldTypeParam = fieldTypeParam;
    }

    public ReflectionParamDetail(
            int ba, ReflectionParamType reflectionParamType, boolean bl, boolean bl1, boolean bl2, ReflectionScopeSwitchMap reflectionScopeSwitchMap
    ) {
        this(ba, reflectionParamType, bl, bl1, bl2);
    }

    public boolean isClassOrPropertiesNameParam() {
        return this.paramType == ReflectionParamType.CLASS_OR_PROPERTIES_NAME_PARAM_TYPE;
    }

    public ReflectionParamType getParamType() {
        return this.paramType;
    }

    public boolean isArrayParam() {
        return this.arrayParam;
    }

    public boolean isFieldTypeParam() {
        return this.fieldTypeParam;
    }

    public boolean isFieldNameParam() {
        return this.paramType == ReflectionParamType.FIELD_NAME_PARAM_TYPE;
    }

    public boolean isMethodNameParam() {
        return this.paramType == ReflectionParamType.METHOD_NAME_PARAM_TYPE;
    }

    public boolean isTargetClassParam() {
        return this.targetClassParam;
    }

    public boolean isPackageNameParam() {
        return this.paramType == ReflectionParamType.PACKAGE_NAME_PARAM_TYPE;
    }

    public boolean isClassParam() {
        return this.paramType == ReflectionParamType.CLASS_PARAM_TYPE;
    }

    public int getPosition() {
        return this.position;
    }

    public boolean isClassNameParam() {
        return this.paramType == ReflectionParamType.CLASS_NAME_PARAM_TYPE;
    }
}
