package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.classfile.MethodInfo;

public class FrameMethodCandidate {
    public boolean callTraceBreak = false;
    public ResolvedStackTraceLine stackTraceLine;
    public MethodInfo method;
    public int hashValue;

    public boolean hasCallTraceBreak() {
        return this.callTraceBreak;
    }

    public ResolvedStackTraceLine getStackTraceLine() {
        return this.stackTraceLine;
    }

    public MethodInfo getMethod() {
        return this.method;
    }

    @Override
    public int hashCode() {
        return this.hashValue;
    }

    @Override
    public boolean equals(Object object) {
        com.zelix.klassmaster.changelog.FrameMethodCandidate frameMethodCandidate3 = null;
        String[] strings = StackTraceTranslator.getOpaqueStrings();
        int bb = ((object instanceof FrameMethodCandidate) ? 1 : 0);
        if (strings != null) {
            if ((bb != 0)) {
                FrameMethodCandidate frameMethodCandidate1 = (FrameMethodCandidate) object;
                bb = this.hashValue;
                if (strings != null) {
                    label85:
                    if (this.hashValue == frameMethodCandidate1.hashValue) {
                        label95:
                        {
                            label107:
                            {
                                FrameMethodCandidate frameMethodCandidate2 = this;
                                if (strings != null) {
                                    if (this.stackTraceLine == null) {
                                        frameMethodCandidate3 = frameMethodCandidate1;
                                        if (strings == null) {
                                            break label95;
                                        }

                                        if (frameMethodCandidate1.stackTraceLine == null) {
                                            break label107;
                                        }
                                    }

                                    frameMethodCandidate2 = this;
                                }

                                if (strings != null) {
                                    if (frameMethodCandidate2.stackTraceLine == null) {
                                        break label85;
                                    }

                                    frameMethodCandidate2 = frameMethodCandidate1;
                                }

                                if (strings != null) {
                                    if (frameMethodCandidate2.stackTraceLine == null) {
                                        break label85;
                                    }

                                    frameMethodCandidate2 = this;
                                }

                                byte ba = ((byte) ((frameMethodCandidate2.stackTraceLine.getLineText().equals(frameMethodCandidate1.stackTraceLine.getLineText())) ? 1 : 0));
                                if (strings == null) {
                                    return ba != 0;
                                }

                                if (ba == 0) {
                                    break label85;
                                }
                            }

                            frameMethodCandidate3 = this;
                        }

                        label66:
                        if (strings != null) {
                            if (frameMethodCandidate3.method == null) {
                                frameMethodCandidate3 = frameMethodCandidate1;
                                if (strings == null) {
                                    break label66;
                                }

                                if (frameMethodCandidate1.method == null) {
                                    return true;
                                }
                            }

                            frameMethodCandidate3 = this;
                        }

                        if (strings != null) {
                            if (frameMethodCandidate3.method == null) {
                                return false;
                            }

                            frameMethodCandidate3 = frameMethodCandidate1;
                        }

                        if (strings != null) {
                            if (frameMethodCandidate3.method == null) {
                                return false;
                            }

                            frameMethodCandidate3 = this;
                        }

                        boolean bl = frameMethodCandidate3.method.isSameMethod(frameMethodCandidate1.method);
                        if (strings == null) {
                            return bl;
                        }

                        if (bl) {
                            return true;
                        }

                        return false;
                    }

                    bb = 0;
                }

                return bb != 0;
            }

            bb = 0;
        }

        return bb != 0;
    }

    public FrameMethodCandidate(ResolvedStackTraceLine resolvedStackTraceLine, MethodInfo methodInfo1, boolean callTraceBreak) {
        this.stackTraceLine = resolvedStackTraceLine;
        this.method = methodInfo1;
        this.callTraceBreak = callTraceBreak;
        this.hashValue = resolvedStackTraceLine.getLineText().hashCode()
                ^ methodInfo1.getSourceNameAndDescriptor().hashCode()
                ^ methodInfo1.getClassName().hashCode();
    }

    public FrameMethodCandidate(ResolvedStackTraceLine resolvedStackTraceLine) {
        this.stackTraceLine = resolvedStackTraceLine;
        this.method = null;
        this.callTraceBreak = false;
        this.hashValue = resolvedStackTraceLine.getLineText().hashCode();
    }
}
