package com.zelix.klassmaster.license;

public class LicenseDetails extends TemplateTextBase {
    private final String[] licenseFields;

    public LicenseDetails() {
        String[] strings = new String[]{
                "License No.:                                         ",
                "none                                                 ",
                "License Type:                                        ",
                "unlimited                                            ",
                "Licensee:                                            ",
                "dev                                                  ",
                "                                                     ",
                "dev@b.com",
                "Not used                                             ",
                "Not used                                             "
        };
        this.licenseFields = strings;
    }

    public String getEncodedEvaluationPeriod() {
        return "WHa6SUKKUK";
    }

    public String getEncodedGracePeriod() {
        return "VlW2eUeA0";
    }

    public String getLicenseField(Object object, Object object1) {
        return this.licenseFields[(Integer) object].trim();
    }

    public String getLicenseToken() {
        return "58#\\u0001\\u00031*\\u0001<\\u000erm";
    }

    public String getEncodedExpiryTime() {
        return "f3dgFIbD6N1QU";
    }

    public String getRawLicenseField(int ba) {
        return this.licenseFields[ba];
    }
}
