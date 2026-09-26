package com.zelix.klassmaster.proguard.mapping.parser;

import com.zelix.klassmaster.util.ZkmAssert;

import java.io.Serializable;

public class ProGuardMappingToken implements Serializable {
    public ProGuardMappingToken U;
    public int m;
    public ProGuardMappingToken r;
    public int M;
    public int V;
    public int b;
    public String L;
    public int C;
    private static final String c = " NULL >";

    public static ProGuardMappingToken newToken(int ba, String string) {
        switch (ba) {
            default:
                return new ProGuardMappingToken(ba, string);
        }
    }

    public ProGuardMappingToken() {
    }

    @Override
    public String toString() {
        return this.L == null ? "<" + ZkmAssert.getSimpleClassName(this) + c : this.L;
    }

    public ProGuardMappingToken(int ba, String string) {
        this.m = ba;
        this.L = string;
    }
}
