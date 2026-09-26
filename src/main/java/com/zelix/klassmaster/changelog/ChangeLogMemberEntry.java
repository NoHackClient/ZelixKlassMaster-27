package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;

public class ChangeLogMemberEntry implements Comparable {
    public final String className;
    public String fieldName;
    public String fieldDescriptor;
    public String setterMethodName;
    public String getterMethodName;
    public String altGetterMethodName;
    public boolean alternateVariant;
    public String packageName;
    public String fieldTypeName;
    public String newClassName;
    public String newFieldName;
    public String newSetterMethodName;
    public String newGetterMethodName;
    public String newAltGetterMethodName;

    public boolean hasIncompleteMapping() {
        return this.newFieldName != null && this.setterMethodName == null
                || this.newSetterMethodName != null && this.getterMethodName == null
                || this.newGetterMethodName != null && this.altGetterMethodName == null
                || this.newAltGetterMethodName != null;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ChangeLogMemberEntry)) {
            return false;
        }

        ChangeLogMemberEntry changeLogMemberEntry1 = (ChangeLogMemberEntry) object;
        return this.className.equals(changeLogMemberEntry1.className)
                && (
                this.fieldName == null && changeLogMemberEntry1.fieldName == null && this == object
                        || this.fieldName != null && changeLogMemberEntry1.fieldName != null && this.fieldName.equals(changeLogMemberEntry1.fieldName)
        );
    }

    public void setAlternateVariant(boolean alternateVariant) {
        this.alternateVariant = alternateVariant;
    }

    public String getInternalClassName() {
        return ZkmUtils.dotsToSlashes(this.className);
    }

    public String getNewFieldName() {
        return this.newFieldName;
    }

    public String getFieldTypeName() {
        return this.fieldTypeName;
    }

    public void setGetterMethodName(String string) {
        this.getterMethodName = string;
    }

    public String getNewAltGetterMethodName() {
        return this.newAltGetterMethodName;
    }

    public String getClassName() {
        return this.className;
    }

    public int compareEntries(ChangeLogMemberEntry changeLogMemberEntry1) {
        if (this.className.equals(changeLogMemberEntry1.className)) {
            if (this.fieldName != null) {
                return changeLogMemberEntry1.fieldName != null ? this.fieldName.compareTo(changeLogMemberEntry1.fieldName) : -1;
            } else {
                return changeLogMemberEntry1.fieldName != null ? 1 : 0;
            }
        } else {
            return this.className.compareTo(changeLogMemberEntry1.className);
        }
    }

    public void setNewSetterMethodName(String string) {
        this.newSetterMethodName = string;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getSetterMethodName() {
        return this.setterMethodName;
    }

    public ChangeLogMemberEntry(
            String string,
            String string1,
            String string2,
            String string3,
            String string4,
            String string5,
            boolean alternateVariant,
            String string6,
            ChangeLogMapping changeLogMapping1,
            String string7,
            ObservableHolder observableHolder
    ) throws ZkmException, IOException {
        observableHolder.clearValue();
        this.className = string;
        this.fieldName = string1;
        this.fieldDescriptor = string2;
        this.setterMethodName = string3;
        this.getterMethodName = string4;
        this.altGetterMethodName = string5;
        this.alternateVariant = alternateVariant;
        this.packageName = string6;
        if (string2 != null) {
            this.fieldTypeName = ConstantPoolEntry.descriptorToJavaType(string2);
        }

        if (changeLogMapping1.hasClassMapping(string)) {
            this.newClassName = changeLogMapping1.getNewClassName(string);
            if (this.newClassName == null) {
                this.newClassName = string;
            }
        } else {
            observableHolder.setValue("Class '" + string + "' appears in a '" + string7 + "' line but it is not present in the main body of the change log.");
        }

        if (string1 != null) {
            if (changeLogMapping1.containsFieldMapping(string, string1, this.fieldTypeName)) {
                this.newFieldName = changeLogMapping1.getNewFieldName(string, string1, this.fieldTypeName);
                if (this.newFieldName == null) {
                    this.newFieldName = string1;
                }
            }

            if (string3 != null) {
                String[] strings = new String[]{this.fieldTypeName};
                if (changeLogMapping1.containsMethodMapping(string, string3, strings, "void")) {
                    this.newSetterMethodName = changeLogMapping1.getNewMethodName(string, string3, strings, "void");
                    if (this.newSetterMethodName == null) {
                        this.newSetterMethodName = string3;
                    }
                } else {
                    observableHolder.setValue(
                            "Method 'void "
                                    + string3
                                    + '('
                                    + this.fieldTypeName
                                    + ")' in class '"
                                    + string
                                    + "' appears in a '"
                                    + string7
                                    + "' line but it is not present in the main body of the change log. (1)"
                    );
                }
            }

            String[] strings1 = new String[0];
            if (string4 != null) {
                if (changeLogMapping1.containsMethodMapping(string, string4, strings1, this.fieldTypeName)) {
                    this.newGetterMethodName = changeLogMapping1.getNewMethodName(string, string4, strings1, this.fieldTypeName);
                    if (this.newGetterMethodName == null) {
                        this.newGetterMethodName = string4;
                    }
                } else {
                    observableHolder.setValue(
                            "Method '"
                                    + this.fieldTypeName
                                    + " "
                                    + string4
                                    + "()' in class '"
                                    + string
                                    + "' appears in a '"
                                    + string7
                                    + "' line but it is not present in the main body of the change log. (2)"
                    );
                }
            }

            if (string5 != null) {
                if (changeLogMapping1.containsMethodMapping(string, string5, strings1, this.fieldTypeName)) {
                    this.newAltGetterMethodName = changeLogMapping1.getNewMethodName(string, string5, strings1, this.fieldTypeName);
                    if (this.newAltGetterMethodName == null) {
                        this.newAltGetterMethodName = string5;
                    }
                } else {
                    observableHolder.setValue(
                            "Method '"
                                    + this.fieldTypeName
                                    + " "
                                    + string5
                                    + "()' in class '"
                                    + string
                                    + "' appears in a '"
                                    + string7
                                    + "' line but it is not present in the main body of the change log. (3)"
                    );
                }
            }
        }
    }

    public boolean isTraceBackEntry() {
        return this.packageName == null;
    }

    public boolean isAlternateVariant() {
        return this.alternateVariant;
    }

    public String getAltGetterMethodName() {
        return this.altGetterMethodName;
    }

    public String getNewSetterMethodName() {
        return this.newSetterMethodName;
    }

    public String getNewGetterMethodName() {
        return this.newGetterMethodName;
    }

    public void setNewGetterMethodName(String string) {
        this.newGetterMethodName = string;
    }

    public boolean hasNoField() {
        return this.fieldName == null;
    }

    public String getArrayElementClassName() {
        return this.fieldDescriptor.substring(2, this.fieldDescriptor.length() - 1);
    }

    public String getNewClassName() {
        return this.newClassName;
    }

    @Override
    public int hashCode() {
        return this.fieldName != null ? this.className.hashCode() ^ this.fieldName.hashCode() : System.identityHashCode(this);
    }

    public void setAltGetterMethodName(String string) {
        this.altGetterMethodName = string;
    }

    @Override
    public int compareTo(Object object) {
        return this.compareEntries((ChangeLogMemberEntry) object);
    }

    public boolean hasCustomReferenceType() {
        return this.fieldDescriptor != null && this.fieldDescriptor.endsWith(";") && !this.fieldDescriptor.endsWith("Ljava/lang/String;");
    }

    public boolean hasSetterMethod() {
        return this.setterMethodName != null;
    }

    public void setNewAltGetterMethodName(String string) {
        this.newAltGetterMethodName = string;
    }

    public String getGetterMethodName() {
        return this.getterMethodName;
    }

    public void setSetterMethodName(String string) {
        this.setterMethodName = string;
    }

    public String getFieldDescriptor() {
        return this.fieldDescriptor;
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
