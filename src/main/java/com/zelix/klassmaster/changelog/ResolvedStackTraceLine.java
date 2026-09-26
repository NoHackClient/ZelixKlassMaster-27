package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassResolver;
import com.zelix.klassmaster.exceptions.ClassLookupException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResolvedStackTraceLine implements StackTraceLineInfo {
    public List methodCandidates = new ArrayList();
    public StackTraceFrameLine frameLine;
    public ProgramClass programClass;

    @Override
    public boolean isValidFrame() {
        return this.frameLine.isValidFrame();
    }

    @Override
    public boolean hasLineNumber() {
        return this.frameLine.hasLineNumber();
    }

    public void findCandidateMethods(ProgramClass programClass1, boolean bl) throws StackTraceTranslateException {
        List list1 = programClass1.findMethodsByName(this.frameLine.getMethodName());
        StackTraceFrameLine stackTraceFrameLine1 = this.frameLine;
        Long long1 = 121982683753216L;
        if (stackTraceFrameLine1.getLineNumber(0, long1) > -1) {
            stackTraceFrameLine1 = this.frameLine;
            Long long2 = 121982683753216L;
            int ba = stackTraceFrameLine1.getLineNumber(0, long2);
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                if (!((MethodInfo) iterator.next()).hasLineNumber(ba)) {
                    iterator.remove();
                }
            }
        }

        Iterator iterator1 = list1.iterator();

        while (iterator1.hasNext()) {
            MethodInfo methodInfo1 = (MethodInfo) iterator1.next();
            this.methodCandidates.add(new TraceMethodCandidate(methodInfo1.getNameAndDescriptor(), methodInfo1));
        }

        if (this.methodCandidates.size() == 0) {
            if (bl) {
                stackTraceFrameLine1 = this.frameLine;
                StringBuilder stringBuilder2 = new StringBuilder()
                        .append("Class '")
                        .append(programClass1.getDottedClassName())
                        .append("' contains no matching method '")
                        .append(this.frameLine.getMethodName())
                        .append("'");
                StackTraceFrameLine stackTraceFrameLine = this.frameLine;
                Long long3 = 121982683753216L;
                String string2;
                if (stackTraceFrameLine.getLineNumber(0, long3) > -1) {
                    StringBuilder stringBuilder3 = new StringBuilder().append(" with line number ");
                    StackTraceFrameLine stackTraceFrameLine4 = this.frameLine;
                    Long long4 = 121982683753216L;
                    string2 = stringBuilder3.append(stackTraceFrameLine4.getLineNumber(0, long4)).append(".").toString();
                } else {
                    string2 = ".";
                }

                stackTraceFrameLine1.setErrorMessage(stringBuilder2.append(string2).append(" The classpath may be incorrect.").toString());
            } else {
                StringBuilder stringBuilder = new StringBuilder()
                        .append("Error analyzing '")
                        .append(this.frameLine.getLineText())
                        .append("'. Class '")
                        .append(programClass1.getDottedClassName())
                        .append("' contains no matching method '")
                        .append(this.frameLine.getMethodName())
                        .append("'");
                StackTraceFrameLine stackTraceFrameLine2 = this.frameLine;
                Long long5 = 121982683753216L;
                String string1;
                if (stackTraceFrameLine2.getLineNumber(0, long5) > -1) {
                    StringBuilder stringBuilder1 = new StringBuilder().append(" with line number ");
                    StackTraceFrameLine stackTraceFrameLine3 = this.frameLine;
                    Long long6 = 121982683753216L;
                    string1 = stringBuilder1.append(stackTraceFrameLine3.getLineNumber(0, long6)).append(".").toString();
                } else {
                    string1 = ".";
                }

                String string = stringBuilder.append(string1).append(" Please check that you have specified the correct obfuscated bytecode.").toString();
                throw new StackTraceTranslateException(string);
            }
        }
    }

    @Override
    public int getLineNumber(Object object, Object object1) {
        long ba = (Long) object1;
        long bd = (long) ((Integer) object).intValue() << 48;
        int bb = (int) (((bd | ba << 16 >>> 16) ^ 0L) >>> 48);
        long bc = ((bd | ba << 16 >>> 16) ^ 0L) << 16 >>> 16;
        StackTraceFrameLine stackTraceFrameLine = this.frameLine;
        short be = (short) bb;
        Long long1 = bc;
        return stackTraceFrameLine.getLineNumber(Integer.valueOf(be), long1);
    }

    public ResolvedStackTraceLine(StackTraceFrameLine stackTraceFrameLine, ClasspathClassResolver classpathClassResolver, boolean bl) throws ZkmException, IOException {
        this.frameLine = stackTraceFrameLine;
        if (classpathClassResolver != null && this.isValidFrame()) {
            String string = stackTraceFrameLine.getClassName();
            String string1 = stackTraceFrameLine.getMethodDescriptor();

            try {
                this.programClass = classpathClassResolver.loadProgramClass(ZkmUtils.dotsToSlashes(string));
            } catch (TraceClassNotFoundException traceClassNotFoundException) {
                if (bl) {
                    stackTraceFrameLine.setErrorMessage("Class '" + string + "' not found in the change log or the obfuscated bytecode classpath.");
                    return;
                }

                throw new StackTraceTranslateException(
                        "Class '"
                                + string
                                + "' is in the change log but NOT in the obfuscated bytecode classpath. The obfuscated bytecode classpath is incorrect or incomplete."
                );
            } catch (ClassLookupException classLookupException) {
                throw new StackTraceTranslateException(classLookupException.getMessage());
            }

            if (string1 != null) {
                MethodSignature methodSignature1 = new MethodSignature(stackTraceFrameLine.getMethodName(), ZkmUtils.dotsToSlashes(string1));
                MethodInfo methodInfo1 = this.programClass.findMethodBySignature(methodSignature1);
                if (methodInfo1 == null) {
                    if (bl) {
                        stackTraceFrameLine.setErrorMessage("Method '" + methodSignature1.formatSignature() + "' in class '" + string + "' not found.");
                        return;
                    }

                    throw new StackTraceTranslateException(
                            "Error analyzing '"
                                    + stackTraceFrameLine.getLineText()
                                    + "'. Class '"
                                    + this.programClass.getDottedClassName()
                                    + "' contains no matching method '"
                                    + methodSignature1.formatSignature()
                                    + "'. Obfuscated bytecode classpath is incorrect."
                    );
                }

                TraceMethodCandidate traceMethodCandidate = new TraceMethodCandidate(
                        stackTraceFrameLine.getMethodName() + ZkmUtils.dotsToSlashes(string1), methodInfo1
                );
                this.methodCandidates.add(traceMethodCandidate);
            } else {
                this.findCandidateMethods(this.programClass, bl);
            }
        }
    }

    public ProgramClass getProgramClass() {
        return this.programClass;
    }

    public StackTraceFrameLine getFrameLine() {
        return this.frameLine;
    }

    @Override
    public String getClassName() {
        if (this.programClass != null) {
            return this.programClass.getDottedClassName();
        } else {
            return this.frameLine.getClassName() != null ? this.frameLine.getClassName() : null;
        }
    }

    @Override
    public String getLineText() {
        return this.frameLine.getLineText();
    }

    public List getMethodCandidates() {
        return this.methodCandidates;
    }
}
