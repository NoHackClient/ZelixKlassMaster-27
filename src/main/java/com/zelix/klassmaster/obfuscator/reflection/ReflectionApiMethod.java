package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.classfile.constpool.ResolvedMethodRef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ReflectionApiMethod {
    public List paramDetails = new ArrayList();
    private final int parameterCount;
    public final InvocationKind invocationKind;
    public final ReflectionTargetKind targetKind;
    public final boolean receiverIsTargetClass;
    public final boolean reportable;
    public final ReflectionLookupTemplate lookupTemplate;
    public final ReflectionTargetScope targetScope;
    public final String methodSignature;

    public boolean isFieldLookup() {
        return this.targetKind == ReflectionTargetKind.FIELD_TYPE;
    }

    public List getParamDetails() {
        return Collections.unmodifiableList(this.paramDetails);
    }

    public boolean isConstructorLookup() {
        switch (ReflectionScopeSwitchMap.TARGET_KIND_SWITCH[this.targetKind.ordinal()]) {
            case 1:
            case 2:
            case 3:
                return true;
            case 4:
                if (this.getMethodName().equals("findConstructor")) {
                    return true;
                }

                return false;
            default:
                return false;
        }
    }

    public boolean isMethodHandleLookup() {
        return this.targetKind == ReflectionTargetKind.METHOD_HANDLE_TYPE;
    }

    public boolean isHandlingEnabled(boolean bl, boolean bl1, boolean bl2) {
        switch (ReflectionScopeSwitchMap.TARGET_SCOPE_SWITCH[this.targetScope.ordinal()]) {
            case 1:
            case 2:
                return bl;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return bl1;
            case 8:
            case 9:
            case 10:
            case 11:
                return bl2;
            case 12:
                return false;
            default:
                return false;
        }
    }

    public int getArgumentOffset() {
        return this.invocationKind.getArgumentOffset();
    }

    public boolean hasMethodNameParam() {
        Iterator iterator = this.paramDetails.iterator();

        while (iterator.hasNext()) {
            if (((ReflectionParamDetail) iterator.next()).isMethodNameParam()) {
                return true;
            }
        }

        return false;
    }

    public boolean isMethodTypeLookup() {
        return this.targetKind == ReflectionTargetKind.METHOD_TYPE_TYPE;
    }

    public String getMethodName() {
        return this.methodSignature.substring(0, this.methodSignature.indexOf(40));
    }

    public boolean isReceiverTargetClass() {
        return this.receiverIsTargetClass;
    }

    public void addParamDetail(int ba, ReflectionParamType reflectionParamType, boolean bl) {
        Boolean boolean3 = false;
        Boolean boolean2 = false;
        Boolean boolean1 = bl;
        this.addParamDetail(ba, reflectionParamType, boolean1, boolean2, boolean3);
    }

    public void addParamDetail(int ba, ReflectionParamType reflectionParamType, Boolean boolean1, Boolean boolean2, Boolean boolean3) {
        List list1;
        if (this.paramDetails.size() > 0) {
            ReflectionParamDetail reflectionParamDetail = (ReflectionParamDetail) this.paramDetails.get(this.paramDetails.size() - 1);
            if (reflectionParamDetail.getPosition() >= ba) {
                throw new IllegalArgumentException(
                        "Parameter details must be added in ascending position order. " + reflectionParamDetail.getPosition() + " > " + ba
                );
            }

            list1 = this.paramDetails;
        } else {
            list1 = this.paramDetails;
        }

        list1.add(new ReflectionParamDetail(ba, reflectionParamType, boolean1, boolean2, boolean3, null));
    }

    public boolean hasLookupTemplate() {
        return this.lookupTemplate != null;
    }

    public static int getFieldTypeParamIndex(ReflectionApiMethod reflectionApiMethod) {
        for (int i = 0; i < reflectionApiMethod.getParamDetailCount(); i++) {
            if (reflectionApiMethod.getParamDetail(i).isFieldTypeParam()) {
                return i;
            }
        }

        return -1;
    }

    public ReflectionLookupTemplate getLookupTemplate() {
        return this.lookupTemplate;
    }

    public static int getMemberNameParamIndex(ReflectionApiMethod reflectionApiMethod) {
        for (int i = 0; i < reflectionApiMethod.getParamDetailCount(); i++) {
            ReflectionParamDetail reflectionParamDetail = reflectionApiMethod.getParamDetail(i);
            if (reflectionParamDetail.isFieldNameParam() || reflectionParamDetail.isMethodNameParam()) {
                return i;
            }
        }

        return -1;
    }

    public String getMethodSignature() {
        return this.methodSignature;
    }

    public boolean isSpecificConstructorLookup() {
        return this.targetKind == ReflectionTargetKind.SPECIFIC_CONSTRUCTOR_CALL_TYPE;
    }

    public ReflectionApiMethod(
            int parameterCount,
            InvocationKind invocationKind1,
            ReflectionTargetKind reflectionTargetKind,
            boolean receiverIsTargetClass,
            boolean reportable,
            String string,
            ReflectionLookupTemplate reflectionLookupTemplate,
            ReflectionTargetScope reflectionTargetScope
    ) {
        this.parameterCount = parameterCount;
        this.invocationKind = invocationKind1;
        this.targetKind = reflectionTargetKind;
        this.receiverIsTargetClass = receiverIsTargetClass;
        this.reportable = reportable;
        this.lookupTemplate = reflectionLookupTemplate;
        this.targetScope = reflectionTargetScope;
        this.methodSignature = string;
    }

    public static int getTargetClassArgIndex(ReflectionApiMethod reflectionApiMethod) {
        if (reflectionApiMethod.isReceiverTargetClass()) {
            return reflectionApiMethod.getParamDetailCount();
        }

        for (int i = 0; i < reflectionApiMethod.getParamDetailCount(); i++) {
            if (reflectionApiMethod.getParamDetail(i).isTargetClassParam()) {
                return i;
            }
        }

        return -1;
    }

    public static String getPrimitiveFieldTypeDescriptor(ReflectionApiMethod reflectionApiMethod) {
        if (reflectionApiMethod == ResolvedMethodRef.INTEGER_FIELD_UPDATER_NEW) {
            return "I";
        } else {
            return reflectionApiMethod == ResolvedMethodRef.LONG_FIELD_UPDATER_NEW ? "J" : null;
        }
    }

    public ReflectionApiMethod(int ba, InvocationKind invocationKind1, ReflectionTargetKind reflectionTargetKind, String string) {
        this(ba, invocationKind1, reflectionTargetKind, false, true, string);
    }

    public void addParamDetail(int ba, ReflectionParamType reflectionParamType) {
        Boolean boolean3 = false;
        Boolean boolean2 = true;
        Boolean boolean1 = false;
        this.addParamDetail(ba, reflectionParamType, boolean1, boolean2, boolean3);
    }

    public ReflectionApiMethod(
            int ba,
            InvocationKind invocationKind1,
            ReflectionTargetKind reflectionTargetKind,
            boolean bl,
            String string,
            ReflectionLookupTemplate reflectionLookupTemplate,
            ReflectionTargetScope reflectionTargetScope
    ) {
        this(ba, invocationKind1, reflectionTargetKind, bl, true, string, reflectionLookupTemplate, reflectionTargetScope);
    }

    public static boolean isPrimitiveFieldUpdater(ReflectionApiMethod reflectionApiMethod) {
        return reflectionApiMethod == ResolvedMethodRef.INTEGER_FIELD_UPDATER_NEW || reflectionApiMethod == ResolvedMethodRef.LONG_FIELD_UPDATER_NEW;
    }

    public boolean isFunctionalInterfaceLookup() {
        return this.targetKind == ReflectionTargetKind.FUNCTIONAL_INTERFACE_METHOD_TYPE;
    }

    public ReflectionTargetScope getTargetScope() {
        return this.targetScope;
    }

    public int getParamDetailCount() {
        return this.paramDetails.size();
    }

    public boolean isDefaultConstructorCall() {
        return this.targetKind == ReflectionTargetKind.DEFAULT_CONSTRUCTOR_CALL_TYPE;
    }

    public boolean isReportable() {
        return this.reportable;
    }

    public boolean isMethodLookup() {
        return this.targetKind == ReflectionTargetKind.METHOD_TYPE;
    }

    public ReflectionApiMethod(int ba, InvocationKind invocationKind1, ReflectionTargetKind reflectionTargetKind, boolean bl, boolean bl1, String string) {
        this(ba, invocationKind1, reflectionTargetKind, bl, bl1, string, null, ReflectionTargetScope.NONE);
    }

    public boolean isObjectNameLookup() {
        return this.targetKind == ReflectionTargetKind.OBJECT_NAME_TYPE;
    }

    public ReflectionParamDetail getParamDetail(int ba) {
        return (ReflectionParamDetail) this.paramDetails.get(ba);
    }

    public boolean isAllConstructorsLookup() {
        return this.targetKind == ReflectionTargetKind.ALL_CONSTRUCTORS_CALL_TYPE;
    }

    public boolean hasFieldNameParam() {
        Iterator iterator = this.paramDetails.iterator();

        while (iterator.hasNext()) {
            if (((ReflectionParamDetail) iterator.next()).isFieldNameParam()) {
                return true;
            }
        }

        return false;
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
