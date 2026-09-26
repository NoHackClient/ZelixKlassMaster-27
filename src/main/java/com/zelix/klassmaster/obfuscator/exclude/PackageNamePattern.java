package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.util.ZkmStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class PackageNamePattern implements PackagePatternSpec {
    public String packageName;

    @Override
    public boolean hasDotSuffix() {
        return false;
    }

    @Override
    public String getSpecText() {
        return this.packageName.replace('/', '.') + '.';
    }

    public PackageNamePattern(String string) {
        this.packageName = string;
    }

    @Override
    public List getNameSegments() {
        ArrayList arrayList = new ArrayList();
        StringTokenizer stringTokenizer = new StringTokenizer("/");

        while (stringTokenizer.hasMoreTokens()) {
            arrayList.add(stringTokenizer.nextToken());
        }

        return arrayList;
    }

    @Override
    public String getNamePattern() {
        return this.getSpecText();
    }

    @Override
    public boolean matchesName(String string) {
        return ZkmStringUtils.matchesWildcard(string, this.packageName);
    }

    @Override
    public boolean isLiteralName() {
        return this.packageName.indexOf("*") == -1;
    }

    @Override
    public boolean hasCaretTag() {
        return false;
    }

    @Override
    public double computeSpecificity() {
        int ba = this.packageName.lastIndexOf("*");
        if (this.packageName.equals("*")) {
            return 1.0;
        } else {
            return ba > -1 ? 0.5 : 0.1;
        }
    }
}
