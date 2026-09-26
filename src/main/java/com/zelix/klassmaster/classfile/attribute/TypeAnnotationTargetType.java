package com.zelix.klassmaster.classfile.attribute;

public enum TypeAnnotationTargetType {
    CLASS_TYPE_PARAMETER(0),
    METHOD_TYPE_PARAMETER(1),
    CLASS_EXTENDS(16),
    CLASS_TYPE_PARAMETER_BOUND(17),
    METHOD_TYPE_PARAMETER_BOUND(18),
    FIELD(19),
    METHOD_RETURN(20),
    METHOD_RECEIVER(21),
    METHOD_FORMAL_PARAMETER(22),
    THROWS(23),
    LOCAL_VARIABLE(64),
    RESOURCE_VARIABLE(65),
    EXCEPTION_PARAMETER(66),
    INSTANCEOF(67),
    NEW(68),
    METHOD_REFERENCE_0(69),
    METHOD_REFERENCE_1(70),
    CAST(71),
    CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT(72),
    METHOD_INVOCATION_TYPE_ARGUMENT(73),
    CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT(74),
    METHOD_REFERENCE_TYPE_ARGUMENT(75);

    public static final TypeAnnotationTargetType[] VALUES = new TypeAnnotationTargetType[]{
            CLASS_TYPE_PARAMETER,
            METHOD_TYPE_PARAMETER,
            CLASS_EXTENDS,
            CLASS_TYPE_PARAMETER_BOUND,
            METHOD_TYPE_PARAMETER_BOUND,
            FIELD,
            METHOD_RETURN,
            METHOD_RECEIVER,
            METHOD_FORMAL_PARAMETER,
            THROWS,
            LOCAL_VARIABLE,
            RESOURCE_VARIABLE,
            EXCEPTION_PARAMETER,
            INSTANCEOF,
            NEW,
            METHOD_REFERENCE_0,
            METHOD_REFERENCE_1,
            CAST,
            CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT,
            METHOD_INVOCATION_TYPE_ARGUMENT,
            CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT,
            METHOD_REFERENCE_TYPE_ARGUMENT
    };
    public final int code;

    public static TypeAnnotationTargetType[] getValues() {
        return VALUES.clone();
    }

    public int getCode() {
        return this.code;
    }

    TypeAnnotationTargetType(int code) {
        this.code = code;
    }
}
