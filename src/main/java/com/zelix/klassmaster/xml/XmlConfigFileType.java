package com.zelix.klassmaster.xml;

public enum XmlConfigFileType {
    EJB_JAR,
    APPLICATION,
    CLIENT,
    WEB,
    SPRING,
    BLUEPRINT,
    HIBERNATE,
    ANDROID,
    IBATIS,
    TAGLIB,
    FXML,
    E4XMI,
    GENERIC;

    public static final XmlConfigFileType[] VALUES = new XmlConfigFileType[]{
            EJB_JAR, APPLICATION, CLIENT, WEB, SPRING, BLUEPRINT, HIBERNATE, ANDROID, IBATIS, TAGLIB, FXML, E4XMI, GENERIC
    };

    public static XmlConfigFileType[] getValues() {
        return VALUES.clone();
    }
}
