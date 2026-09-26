package com.zelix.klassmaster.config;

public abstract class WizardAppSettings extends PersistedStringList {
    public transient int e;
    public String f;
    public boolean c;
    public boolean d = false;

    public boolean isMainClassApplication() {
        return this.c;
    }

    public String getMainClassName() {
        return this.f;
    }

    public int getApplicationType() {
        return this.e;
    }

    public void setMainClass(boolean bl, String string, boolean bl1) {
        this.d = bl;
        if (this.d) {
            this.f = string;
            this.c = bl1;
        } else {
            this.f = null;
        }
    }

    public void setApplicationType(int ba) {
        this.e = ba;
    }

    public boolean hasMainClass() {
        return this.d;
    }
}
