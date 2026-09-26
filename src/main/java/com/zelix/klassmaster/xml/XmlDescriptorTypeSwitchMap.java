package com.zelix.klassmaster.xml;

public class XmlDescriptorTypeSwitchMap {
    public static final int[] CONFIG_FILE_TYPE_SWITCH = new int[XmlConfigFileType.getValues().length];

    static {
        try {
            CONFIG_FILE_TYPE_SWITCH[XmlConfigFileType.EJB_JAR.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError12) {
        }

        try {
            CONFIG_FILE_TYPE_SWITCH[XmlConfigFileType.APPLICATION.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError11) {
        }

        try {
            CONFIG_FILE_TYPE_SWITCH[XmlConfigFileType.CLIENT.ordinal()] = 3;
        } catch (NoSuchFieldError noSuchFieldError10) {
        }

        try {
            CONFIG_FILE_TYPE_SWITCH[XmlConfigFileType.WEB.ordinal()] = 4;
        } catch (NoSuchFieldError noSuchFieldError9) {
        }

        try {
            CONFIG_FILE_TYPE_SWITCH[XmlConfigFileType.SPRING.ordinal()] = 5;
        } catch (NoSuchFieldError noSuchFieldError8) {
        }

        try {
            CONFIG_FILE_TYPE_SWITCH[XmlConfigFileType.HIBERNATE.ordinal()] = 6;
        } catch (NoSuchFieldError noSuchFieldError7) {
        }

        try {
            CONFIG_FILE_TYPE_SWITCH[XmlConfigFileType.ANDROID.ordinal()] = 7;
        } catch (NoSuchFieldError noSuchFieldError6) {
        }

        try {
            CONFIG_FILE_TYPE_SWITCH[XmlConfigFileType.IBATIS.ordinal()] = 8;
        } catch (NoSuchFieldError noSuchFieldError5) {
        }

        try {
            CONFIG_FILE_TYPE_SWITCH[XmlConfigFileType.TAGLIB.ordinal()] = 9;
        } catch (NoSuchFieldError noSuchFieldError4) {
        }

        try {
            CONFIG_FILE_TYPE_SWITCH[XmlConfigFileType.FXML.ordinal()] = 10;
        } catch (NoSuchFieldError noSuchFieldError3) {
        }

        try {
            CONFIG_FILE_TYPE_SWITCH[XmlConfigFileType.E4XMI.ordinal()] = 11;
        } catch (NoSuchFieldError noSuchFieldError2) {
        }

        try {
            CONFIG_FILE_TYPE_SWITCH[XmlConfigFileType.BLUEPRINT.ordinal()] = 12;
        } catch (NoSuchFieldError noSuchFieldError1) {
        }

        try {
            CONFIG_FILE_TYPE_SWITCH[XmlConfigFileType.GENERIC.ordinal()] = 13;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
    }

    private XmlDescriptorTypeSwitchMap() {
    }
}
