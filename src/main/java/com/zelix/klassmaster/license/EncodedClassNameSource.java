package com.zelix.klassmaster.license;

public abstract class EncodedClassNameSource {
    static {
        System.getProperty("line.separator", "\n");
    }

    public String getEncodedClassName(int ba) {
        switch (ba) {
            case 0:
                return "e0d25164a7ccbd2433f1cb80afec3e59a77523ec5";
            case 1:
                return "7523ece68726c92a0ef8140c8d759d89d23f662bb";
            case 2:
                return "726c92a0ef8140c8d759d89d26u1wmj2qm6u1wmj2";
            case 3:
                return "6u1wmj2qm19fba69865eaa72e0d25164ae0d25164";
            default:
                return "b9dc1eb133f34650ce3f662bbc916f628269219fb";
        }
    }
}
