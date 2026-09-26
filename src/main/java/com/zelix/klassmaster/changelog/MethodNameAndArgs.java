package com.zelix.klassmaster.changelog;

public class MethodNameAndArgs implements Comparable {
    private static final String UNKNOWN_ARGS = "unknown";
    public String cachedDescription;
    public String methodName;
    public String[] argumentTypes;

    public String getMethodName() {
        return this.methodName;
    }

    public MethodNameAndArgs(String string, String[] strings) {
        this.methodName = string;
        this.argumentTypes = strings;
    }

    @Override
    public int compareTo(Object object) {
        return this.compareByDescription((MethodNameAndArgs) object);
    }

    public int compareByDescription(MethodNameAndArgs methodNameAndArgs1) {
        return this.getDescription().compareTo(methodNameAndArgs1.getDescription());
    }

    public String[] getArgumentTypes() {
        return this.argumentTypes;
    }

    public String getDescription() {
        if (this.cachedDescription == null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(this.methodName + "(");
            if (this.argumentTypes != null) {
                int ba = 0;
                int bb = 0;

                for (String[] strings = this.argumentTypes; bb < strings.length; strings = this.argumentTypes) {
                    stringBuffer.append(this.argumentTypes[ba]);
                    if (ba < this.argumentTypes.length - 1) {
                        stringBuffer.append(",");
                    }

                    bb = ++ba;
                }
            } else {
                stringBuffer.append(UNKNOWN_ARGS);
            }

            stringBuffer.append(")");
            this.cachedDescription = stringBuffer.toString();
        }

        return this.cachedDescription;
    }
}
