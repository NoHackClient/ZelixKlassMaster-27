package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.util.ObjectPair;

import java.util.List;
import java.util.StringTokenizer;

public class ParsedLineStackFrame extends StackTraceLine {
    public ParsedLineStackFrame(StackTraceFrameLine stackTraceFrameLine, LoadedChangeLog loadedChangeLog) throws StackTraceTranslateException {
        super.frameLine = stackTraceFrameLine;
        if (stackTraceFrameLine.isValidFrame()) {
            this.resolveClassAndLine(stackTraceFrameLine, loadedChangeLog);
            if (super.translated) {
                String string = stackTraceFrameLine.getMethodName();
                String string1 = stackTraceFrameLine.getMethodDescriptor();
                if (string1 != null) {
                    List list1 = ConstantPoolEntry.getParameterJavaTypes(string1);
                    StringBuilder stringBuilder = new StringBuilder();
                    String[] strings = this.translateArgumentTypes(list1, stringBuilder, loadedChangeLog);
                    String string2 = ConstantPoolEntry.descriptorToJavaType(string1);
                    String string3 = null;
                    if (string2 != null && string2.length() > 0) {
                        string3 = this.translateTypeName(string2, loadedChangeLog);
                    }

                    if (string.equals("<init>")) {
                        int ba = super.className.lastIndexOf(".");
                        String string4;
                        if (ba == -1) {
                            string4 = super.className;
                        } else {
                            string4 = super.className.substring(ba + 1);
                        }

                        super.methodCandidates = new MethodNameAndArgs[1];
                        super.methodCandidates[0] = new MethodNameAndArgs(string4, strings);
                    } else if (string.equals("<clinit>")) {
                        String string8 = "static";
                        super.methodCandidates = new MethodNameAndArgs[1];
                        super.methodCandidates[0] = new MethodNameAndArgs(string8, strings);
                    } else {
                        String[] strings2 = loadedChangeLog.findOriginalMethodNames(super.className, string, string3, stringBuilder.toString());
                        if (strings2 != null && strings2.length != 0) {
                            super.methodCandidates = new MethodNameAndArgs[strings2.length];

                            for (int i = 0; i < strings2.length; i++) {
                                super.methodCandidates[i] = new MethodNameAndArgs(strings2[i], strings);
                            }
                        } else {
                            String string9 = string;
                            super.methodCandidates = new MethodNameAndArgs[1];
                            super.methodCandidates[0] = new MethodNameAndArgs(string9, strings);
                        }
                    }
                } else if (string.equals("<init>")) {
                    super.methodCandidates = new MethodNameAndArgs[1];
                    int bc = super.className.lastIndexOf(".");
                    String string5;
                    if (bc == -1) {
                        string5 = super.className;
                    } else {
                        string5 = super.className.substring(bc + 1);
                    }

                    super.methodCandidates[0] = new MethodNameAndArgs(string5, null);
                } else if (string.equals("<clinit>")) {
                    super.methodCandidates = new MethodNameAndArgs[1];
                    String string6 = "static";
                    super.methodCandidates[0] = new MethodNameAndArgs(string6, StackTraceLine.EMPTY_STRING_ARRAY);
                } else {
                    List list2 = loadedChangeLog.findOriginalMethods(super.className, string);
                    if (list2 != null) {
                        super.methodCandidates = new MethodNameAndArgs[list2.size()];

                        for (int i = 0; i < list2.size(); i++) {
                            ObjectPair objectPair = (ObjectPair) list2.get(i);
                            String[] strings1 = StackTraceLine.EMPTY_STRING_ARRAY;
                            String string10 = (String) objectPair.getSecond();
                            MethodNameAndArgs[] methodNameAndArgs;
                            if (string10 == null) {
                                methodNameAndArgs = super.methodCandidates;
                            } else {
                                StringTokenizer stringTokenizer = new StringTokenizer(string10, ", ");
                                strings1 = new String[stringTokenizer.countTokens()];
                                int be = 0;

                                while (stringTokenizer.hasMoreTokens()) {
                                    strings1[be++] = stringTokenizer.nextToken();
                                }

                                methodNameAndArgs = super.methodCandidates;
                            }

                            methodNameAndArgs[i] = new MethodNameAndArgs((String) objectPair.getFirst(), strings1);
                        }
                    } else {
                        super.methodCandidates = new MethodNameAndArgs[1];
                        String string7 = stackTraceFrameLine.getMethodDescriptor();
                        if (string7 != null) {
                            List list3 = ConstantPoolEntry.getParameterJavaTypes(string7);
                            super.methodCandidates[0] = new MethodNameAndArgs(string, ((java.lang.String[]) (list3.toArray(new String[list3.size()]))));
                        } else {
                            super.methodCandidates[0] = new MethodNameAndArgs(string, null);
                        }
                    }
                }
            }
        }
    }
}
