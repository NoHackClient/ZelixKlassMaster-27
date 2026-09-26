package com.zelix.klassmaster.obfuscator.reflection;

public class DigestCollisionException extends Exception {
    private static String flowToken;

    public static String getFlowToken() {
        return flowToken;
    }

    public static void setFlowToken() {
        flowToken = "sGEJjc";
    }

    static {
        if (getFlowToken() == null) {
            setFlowToken();
        }
    }
}
