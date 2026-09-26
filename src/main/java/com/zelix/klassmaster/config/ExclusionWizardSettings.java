package com.zelix.klassmaster.config;

import com.zelix.klassmaster.exceptions.AssertionFailedException;
import com.zelix.klassmaster.util.SettingsObjectInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.io.ObjectInputStream;

public class ExclusionWizardSettings extends WizardAppSettings implements Serializable {
    public boolean g = false;
    public String h = null;

    public String getSelectedPackageName() {
        return this.h;
    }

    public boolean isPackageSelected() {
        return this.g;
    }

    public static ExclusionWizardSettings loadFromFile(final String parent) {
        final File file = new File(parent, "ZKM_EX.ser");
        if (file.exists() && !file.isDirectory() && file.canRead()) {
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new SettingsObjectInputStream(new FileInputStream(file));
                final ExclusionWizardSettings exclusionWizardSettings = (ExclusionWizardSettings) objectInputStream.readObject();
                if (exclusionWizardSettings.l == null) {
                    exclusionWizardSettings.l = new LinkedHashMap();
                    exclusionWizardSettings.p = true;
                } else {
                    exclusionWizardSettings.p = true;
                }
                final ExclusionWizardSettings exclusionWizardSettings2 = exclusionWizardSettings;
                try {
                    objectInputStream.close();
                } catch (final IOException ex) {
                }
                return exclusionWizardSettings2;
            } catch (final IOException ex2) {
            } catch (final ClassNotFoundException ex3) {
            } catch (final ClassCastException ex4) {
            } catch (final AssertionFailedException ex5) {
                throw ex5;
            } catch (final Throwable t) {
            } finally {
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (final IOException ex6) {
                    }
                }
            }
        }
        return new ExclusionWizardSettings();
    }

    public boolean isExactPackageName(String string) {
        return string != null && string.length() > 0 && string.indexOf("*") == -1 && string.indexOf("^") == -1 && string.charAt(string.length() - 1) == '.';
    }

    public void setPackageSelection(boolean bl, String string) {
        this.g = bl;
        this.h = string;
    }

    @Override
    public void setEntries(List list1) {
        super.l.clear();
        int ba = 0;
        String string = null;

        for (int i = 0; i < list1.size(); i++) {
            String string1 = (String) list1.get(i);
            super.l.put(string1, string1);
            if (this.isExactPackageName(string1)) {
                if (++ba == 1) {
                    string = string1;
                }
            }
        }

        boolean bl;
        if (super.f != null) {
            if (super.f.length() > 0) {
                if (super.d) {
                    if (!super.l.containsKey(super.f)) {
                        super.d = false;
                        bl = this.g;
                    } else {
                        bl = this.g;
                    }
                } else if (super.l.containsKey(super.f)) {
                    super.d = true;
                    bl = this.g;
                } else {
                    bl = this.g;
                }
            } else {
                bl = this.g;
            }
        } else {
            bl = this.g;
        }

        if (bl) {
            if (!super.l.containsKey(this.h)) {
                this.g = false;
                bl = this.g;
            } else {
                bl = this.g;
            }
        } else if (super.l.containsKey(this.h)) {
            this.g = true;
            bl = this.g;
        } else {
            bl = this.g;
        }

        if (!bl && ba > 0) {
            this.g = true;
            this.h = string;
        }
    }
}
