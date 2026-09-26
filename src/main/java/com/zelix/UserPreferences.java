package com.zelix;

import com.zelix.klassmaster.exceptions.AssertionFailedException;
import com.zelix.klassmaster.util.IntPair;
import com.zelix.klassmaster.util.SerializableDimension;
import com.zelix.klassmaster.util.SettingsObjectInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.ObjectInputStream;

public class UserPreferences implements Serializable {
    public String i;
    public String o;
    public boolean u;
    public String b;
    public String a;
    public String g;
    public String c;
    public String h;
    public String d;
    public String e;
    public boolean f = true;
    public int j = 1;
    public boolean k = true;
    public boolean l = true;
    public IntPair m = new IntPair(0, 0);
    public SerializableDimension n = new SerializableDimension(400, 300);

    public synchronized void setScriptSaveDirectory(String string) {
        this.h = string;
    }

    public synchronized void saveToFile(final String parent, final String child) {
        final File file = new File(parent, child);
        if (file.exists() && (file.isDirectory() || !file.canWrite())) {
            return;
        }
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(this);
            try {
                objectOutputStream.close();
            } catch (final IOException ex) {
            }
        } catch (final IOException ex2) {
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (final IOException ex3) {
                }
            }
        }
    }

    public synchronized void setHelpWindowSize(int ba, int bb) {
        this.n = new SerializableDimension(ba, bb);
    }

    public synchronized String getLastSavePath() {
        return this.g;
    }

    public synchronized boolean isOpenNestedArchives() {
        return this.u;
    }

    public synchronized void savePreferences() {
        this.saveToFile(this.b, this.a);
    }

    public synchronized SerializableDimension getHelpWindowSize() {
        return this.n;
    }

    public synchronized void setProGuardConfigDirectory(String string) {
        this.o = string;
    }

    public UserPreferences() {
    }

    public synchronized void setOpenNestedArchives(boolean bl) {
        this.u = bl;
    }

    public synchronized int getIntSetting() {
        return this.j;
    }

    public synchronized void setBuildHelperSelected(boolean bl) {
        this.k = bl;
    }

    public synchronized String getScriptSaveDirectory() {
        return this.h;
    }

    public synchronized String getOpenClassesDirectory() {
        return this.c;
    }

    public synchronized void setIntSetting(int ba) {
        this.j = ba;
    }

    public synchronized String getProGuardConfigDirectory() {
        return this.o;
    }

    public synchronized void setChangeLogDirectory(String string) {
        this.d = string;
    }

    public synchronized String getChangeLogDirectory() {
        return this.d;
    }

    public synchronized boolean isCrossPlatformLookAndFeel() {
        return this.l;
    }

    public synchronized void setHelpWindowLocation(int ba, int bb) {
        this.m = new IntPair(ba, bb);
    }

    public synchronized boolean isUseBytecodeClasspath() {
        return this.f;
    }

    public synchronized boolean isBuildHelperSelected() {
        return this.k;
    }

    public synchronized void setCrossPlatformLookAndFeel(boolean bl) {
        this.l = bl;
    }

    public synchronized String getBytecodeClasspath() {
        return this.e;
    }

    public synchronized void setBytecodeClasspath(String string) {
        this.e = string;
    }

    public synchronized void setLastSavePath(String string) {
        this.g = string;
    }

    public synchronized String getClasspath() {
        return this.i;
    }

    public synchronized void setOpenClassesDirectory(String string) {
        this.c = string;
    }

    public synchronized void setUseBytecodeClasspath(boolean bl) {
        this.f = bl;
    }

    public synchronized void setClasspath(String string) {
        this.i = string;
    }

    public synchronized IntPair getHelpWindowLocation() {
        return this.m;
    }

    public UserPreferences(final String s) {
        this.f = true;
        this.j = 1;
        this.k = true;
        this.l = true;
        this.m = new IntPair(0, 0);
        this.n = new SerializableDimension(400, 300);
        this.a = "ZKM_O.ser";
        this.b = s;
        final File file = new File(s, "ZKM_O.ser");
        if (file.exists() && !file.isDirectory() && file.canRead()) {
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new SettingsObjectInputStream(new FileInputStream(file));
                final UserPreferences userPreferences = (UserPreferences) objectInputStream.readObject();
                this.u = userPreferences.u;
                this.c = userPreferences.c;
                this.d = userPreferences.d;
                this.e = userPreferences.e;
                this.f = userPreferences.f;
                this.g = userPreferences.g;
                this.o = userPreferences.o;
                this.h = userPreferences.h;
                this.i = userPreferences.i;
                this.j = userPreferences.j;
                this.k = userPreferences.k;
                this.l = userPreferences.l;
                this.m = userPreferences.m;
                this.n = userPreferences.n;
                try {
                    objectInputStream.close();
                } catch (final IOException ex) {
                }
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
    }
}
