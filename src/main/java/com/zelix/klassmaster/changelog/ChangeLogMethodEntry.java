package com.zelix.klassmaster.changelog;

import com.zelix.ZKMChangeLog;

public class ChangeLogMethodEntry {
    public final ZKMChangeLog changeLog;
    public String returnType;
    public String methodName;
    public String[] argumentTypes;

    public String getSignatureString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.returnType);
        stringBuilder.append(' ');
        stringBuilder.append(this.methodName);
        stringBuilder.append('(');
        int ba = 0;
        int bb = 0;

        for (String[] strings = this.argumentTypes; bb < strings.length; strings = this.argumentTypes) {
            stringBuilder.append(this.argumentTypes[ba]);
            if (ba < this.argumentTypes.length - 1) {
                stringBuilder.append(",");
            }

            bb = ++ba;
        }

        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    public ChangeLogMethodEntry(ZKMChangeLog zKMChangeLog, String string, String string1, String[] strings) {
        this.changeLog = zKMChangeLog;
        this.returnType = string;
        this.methodName = string1;
        this.argumentTypes = strings;
    }
}
