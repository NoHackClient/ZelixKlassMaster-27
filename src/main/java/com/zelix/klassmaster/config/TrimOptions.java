package com.zelix.klassmaster.config;

import com.zelix.klassmaster.exceptions.AssertionFailedException;
import com.zelix.klassmaster.util.SettingsObjectInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.ObjectInputStream;

public class TrimOptions implements Serializable {
    public boolean a = false;
    public boolean b = true;
    public boolean d = false;
    public boolean c = false;
    public boolean e = false;
    public boolean f = true;

    public TrimOptions(final String parent) {
        this.a = false;
        this.b = true;
        this.d = false;
        this.c = false;
        this.e = false;
        this.f = true;
        final File file = new File(parent, "ZKM_TO.ser");
        if (file.exists() && !file.isDirectory() && file.canRead()) {
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new SettingsObjectInputStream(new FileInputStream(file));
                final TrimOptions trimOptions = (TrimOptions) objectInputStream.readObject();
                this.a = trimOptions.a;
                this.b = trimOptions.b;
                this.c = trimOptions.c;
                this.d = trimOptions.d;
                this.e = trimOptions.e;
                this.f = trimOptions.f;
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

    public TrimOptions() {
    }

    public void saveToFile(final String parent) {
        final File file = new File(parent, "ZKM_TO.ser");
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
}
