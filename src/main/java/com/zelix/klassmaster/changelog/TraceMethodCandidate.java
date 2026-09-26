package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.classfile.MethodInfo;

public class TraceMethodCandidate {
    public String methodNameAndDescriptor;
    public MethodInfo method;

    public TraceMethodCandidate(String string, MethodInfo methodInfo1) {
        this.methodNameAndDescriptor = string;
        this.method = methodInfo1;
    }
}
