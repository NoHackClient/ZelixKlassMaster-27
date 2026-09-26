package com.zelix.klassmaster.obfuscator.reflection;

import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.classfile.insn.StackFrameState;
import com.zelix.klassmaster.classfile.insn.StringConstantNameRef;

public class ClassDescriptorStringValue extends StringConstantNameRef {
    @Override
    public boolean equals(Object object) {
        boolean strictMergeDisabled = StackFrameState.isStrictMergeDisabled();
        if (object != null) {
            boolean bl1 = this.getClass().equals(object.getClass());
            if (strictMergeDisabled) {
                return bl1;
            }

            if (bl1) {
                ClassDescriptorStringValue classDescriptorStringValue1 = (ClassDescriptorStringValue) object;
                ClassDescriptorStringValue classDescriptorStringValue2 = this;
                if (!strictMergeDisabled) {
                    if (super.stringConstant != null) {
                        ResolvedStringConstant resolvedStringConstant;
                        if (!strictMergeDisabled) {
                            if (classDescriptorStringValue1.stringConstant == null) {
                                return false;
                            }

                            resolvedStringConstant = super.stringConstant;
                        } else {
                            resolvedStringConstant = classDescriptorStringValue1.stringConstant;
                        }

                        return resolvedStringConstant.equals(classDescriptorStringValue1.stringConstant);
                    }

                    classDescriptorStringValue2 = classDescriptorStringValue1;
                }

                return classDescriptorStringValue2.stringConstant == null;
            }
        }

        return false;
    }

    public ClassDescriptorStringValue(ResolvedStringConstant resolvedStringConstant) {
        super(resolvedStringConstant);
    }

    @Override
    public String getNormalizedName() {
        String string = null;
        if (super.stringConstant != null) {
            string = super.stringConstant.getEditableValue();
            int ba = string.lastIndexOf("[");
            if (ba > -1) {
                string = string.substring(ba + 1);
            }

            if (string.endsWith(";")) {
                string = string.substring(1, string.length() - 1);
            }
        }

        return string;
    }

    @Override
    public int hashCode() {
        boolean strictMergeDisabled = StackFrameState.isStrictMergeDisabled();
        ResolvedStringConstant resolvedStringConstant;
        if (!strictMergeDisabled) {
            if (super.stringConstant == null) {
                return 0;
            }

            resolvedStringConstant = super.stringConstant;
        } else {
            resolvedStringConstant = super.stringConstant;
        }

        return resolvedStringConstant.hashCode() + 1;
    }
}
