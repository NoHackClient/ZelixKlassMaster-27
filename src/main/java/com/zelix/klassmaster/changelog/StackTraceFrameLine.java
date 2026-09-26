package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ZkmAssert;

import java.util.StringTokenizer;

public class StackTraceFrameLine implements StackTraceLineInfo {
    public String methodName;
    public MutableInt lineNumber;
    public String className;
    public String methodDescriptor;
    public String errorMessage;
    public String modulePrefix;
    public String leadingText = "";
    public boolean parsed = true;
    public String locationPrefix = "";
    public String locationSuffix = "";
    public String lineText;

    public String getMethodName() {
        return this.methodName;
    }

    public boolean parseQualifiedMethodName(String string) {
        String string1 = string;
        int ba = string1.indexOf("/");
        if (ba > -1) {
            ZkmAssert.assertTrue(ba > 0 && ba < string1.length() - 2, new String[]{"Invalid stack trace line '" + string1 + "' : " + ba + " : '" + string1 + "'"});
            this.modulePrefix = string1.substring(ba);
            string1 = string1.substring(ba + 1);
        }

        StringTokenizer stringTokenizer = new StringTokenizer(string1, ".");
        int bb = stringTokenizer.countTokens();
        if (bb > 1) {
            int bc = 0;
            StringBuilder stringBuilder = new StringBuilder();

            while (stringTokenizer.hasMoreTokens()) {
                String string2 = stringTokenizer.nextToken();
                if (bc == 0) {
                    stringBuilder.append(string2);
                } else if (bc == bb - 1) {
                    this.methodName = string2.trim();
                    int bd = this.methodName.indexOf(" ");
                    if (bd > -1) {
                        this.methodName = this.methodName.substring(0, bd);
                    }
                } else {
                    stringBuilder.append("." + string2);
                }

                bc++;
            }

            this.className = stringBuilder.toString();
            this.className = this.className.replace('/', '.');
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getClassName() {
        return this.className != null ? this.className.replace('/', '.') : null;
    }

    public StackTraceFrameLine(String string) {
        this.lineText = string.trim();
        int ba = this.findAtIndex();
        if (ba > -1) {
            this.leadingText = this.lineText.substring(0, ba);
            String string1 = this.lineText.substring(ba + "at ".length()).trim();
            int bb = string1.indexOf("(");
            if (bb > -1) {
                if (this.parseQualifiedMethodName(string1.substring(0, bb).trim())) {
                    this.parseLocation(string1.substring(bb));
                } else {
                    this.parsed = false;
                }
            } else {
                this.parsed = this.parseQualifiedMethodName(string1.trim());
            }
        } else {
            this.parsed = false;
        }
    }

    @Override
    public boolean hasLineNumber() {
        return this.lineNumber != null;
    }

    @Override
    public String getLineText() {
        return this.lineText;
    }

    public boolean hasError() {
        return this.errorMessage != null;
    }

    public void parseLocation(String string) {
        if (string.length() >= 2) {
            String string1 = string;
            int ba = string1.indexOf("(", 1);
            int bb = string1.indexOf(")");
            if (ba > bb) {
                String string2 = string1.substring(0, ba).trim();
                if (ConstantPoolEntry.isValidMethodDescriptor(string2)) {
                    this.methodDescriptor = string2;
                    string1 = string1.substring(ba);
                }
            }

            int be = string1.indexOf(":");
            int bc = string1.lastIndexOf(")");
            if (be > -1) {
                if (bc > -1) {
                    if (bc > be) {
                        String string3 = string1.substring(be + 1, bc).trim();

                        try {
                            int bd = Integer.parseInt(string3);
                            this.lineNumber = new MutableInt(bd);
                            this.locationPrefix = string1.substring(0, be + 1);
                            this.locationSuffix = string1.substring(bc);
                        } catch (NumberFormatException numberFormatException) {
                            this.locationPrefix = string1;
                        }
                    } else {
                        this.locationPrefix = string1;
                    }
                } else {
                    this.locationPrefix = string1;
                }
            } else {
                this.locationPrefix = string1;
            }
        }
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public int findAtIndex() {
        boolean bl = false;
        int ba = -1;
        int bb = -1;

        while (!bl && (bb = this.lineText.indexOf("at ", bb + 1)) > -1) {
            if (bb <= 0) {
                ba = 0;
                break;
            }

            switch (this.lineText.charAt(bb - 1)) {
                case '\t':
                case ' ':
                    bl = true;
                    ba = bb;
            }
        }

        return ba;
    }

    @Override
    public boolean isValidFrame() {
        return this.parsed && this.errorMessage == null;
    }

    public String getMethodDescriptor() {
        return this.methodDescriptor;
    }

    public void setErrorMessage(String string) {
        this.errorMessage = string;
    }

    @Override
    public int getLineNumber(Object object, Object object1) {
        return this.lineNumber != null ? this.lineNumber.getValue() : -1;
    }
}
