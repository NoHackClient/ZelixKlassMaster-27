package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.util.ObjectTreeNode;

public class SignatureClassTypeSegment {
    public ObjectTreeNode treeNode;
    private SignatureClassTypeSegment innerSegment;
    private String prefix;
    private String className;
    private boolean innerClassSuffix;

    public SignatureClassTypeSegment getInnerSegment() {
        return this.innerSegment;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public SignatureClassTypeSegment(String string, String string1, boolean innerClassSuffix) {
        this.prefix = string;
        this.className = string1;
        this.innerClassSuffix = innerClassSuffix;
    }

    public void setInnerSegment(SignatureClassTypeSegment signatureClassTypeSegment1) {
        this.innerSegment = signatureClassTypeSegment1;
    }

    public void setTreeNode(ObjectTreeNode objectTreeNode) {
        this.treeNode = objectTreeNode;
    }

    public void setClassName(String string) {
        this.className = string;
    }

    public void setPrefix(String string) {
        this.prefix = string;
    }

    public String getClassName() {
        return this.className;
    }

    public String getSignatureText() {
        return (this.prefix == null ? "" : this.prefix) + (this.className == null ? "" : this.className);
    }

    public void setInnerClassSuffix() {
        this.innerClassSuffix = false;
    }

    public boolean isInnerClassSuffix() {
        return this.innerClassSuffix;
    }

    public boolean startsInnerClassChain() {
        return this.innerSegment != null && !this.innerClassSuffix;
    }

    public SignatureClassTypeSegment(String string) {
        this.prefix = string;
        this.className = null;
    }
}
