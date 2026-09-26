package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.classfile.MethodInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class StackTraceCallPath {
    public List frameCandidates = new ArrayList();

    public void addMethodFrame(ResolvedStackTraceLine resolvedStackTraceLine, MethodInfo methodInfo1, boolean bl) {
        FrameMethodCandidate frameMethodCandidate = new FrameMethodCandidate(resolvedStackTraceLine, methodInfo1, bl);
        this.frameCandidates.add(frameMethodCandidate);
    }

    public void addUnresolvedFrame(ResolvedStackTraceLine resolvedStackTraceLine) {
        this.addUnresolvedFrame_v(resolvedStackTraceLine);
    }

    public Enumeration enumerateFrames() {
        return Collections.enumeration(this.frameCandidates);
    }

    @Override
    public Object clone() {
        StackTraceCallPath stackTraceCallPath1 = new StackTraceCallPath();
        stackTraceCallPath1.frameCandidates = new ArrayList(this.frameCandidates);
        return stackTraceCallPath1;
    }

    public void addUnresolvedFrame_v(ResolvedStackTraceLine resolvedStackTraceLine) {
        FrameMethodCandidate frameMethodCandidate = new FrameMethodCandidate(resolvedStackTraceLine);
        this.frameCandidates.add(frameMethodCandidate);
    }

    public MethodInfo getLastMethod() {
        return this.frameCandidates.size() > 0 ? ((FrameMethodCandidate) this.frameCandidates.get(this.frameCandidates.size() - 1)).getMethod() : null;
    }

    public void addMethodFrame(ResolvedStackTraceLine resolvedStackTraceLine, MethodInfo methodInfo1) {
        this.addMethodFrame(resolvedStackTraceLine, methodInfo1, false);
    }
}
