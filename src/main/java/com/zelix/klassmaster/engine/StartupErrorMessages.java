package com.zelix.klassmaster.engine;

public abstract class StartupErrorMessages {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    public static final String PATH_SEPARATOR = System.getProperty("path.separator");
    public static final String JVM_VERSION_ERROR = "ERROR: Zelix KlassMaster 27.0.0 can process earlier bytecode but it requires a Java 8 (JDK 1.8) or better JVM to run. Your JVM is version '"
            + System.getProperty("java.vm.version")
            + "'.";
    public static final String MESSAGE_DIGEST_MISSING_ERROR = "ERROR: Zelix KlassMaster 27.0.0 requires java.security.MessageDigest to be in its runtime classpath. : \""
            + System.getProperty("java.class.path")
            + "\"";

    protected StartupErrorMessages() {
    }
}
