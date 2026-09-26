package com.zelix.klassmaster.config;

import com.zelix.klassmaster.exceptions.AssertionFailedException;
import com.zelix.klassmaster.util.SettingsObjectInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.io.ObjectInputStream;

public class ReferenceObfIncludeStore extends PersistedStringList {
    public static ReferenceObfIncludeStore loadFromFile(final String parent) {
        final File file = new File(parent, "ZKM_OB_REF.ser");
        if (file.exists() && !file.isDirectory() && file.canRead()) {
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new SettingsObjectInputStream(new FileInputStream(file));
                final ReferenceObfIncludeStore referenceObfIncludeStore = (ReferenceObfIncludeStore) objectInputStream.readObject();
                if (referenceObfIncludeStore.l == null) {
                    referenceObfIncludeStore.l = new LinkedHashMap();
                    referenceObfIncludeStore.p = true;
                } else {
                    referenceObfIncludeStore.p = true;
                }
                final ReferenceObfIncludeStore referenceObfIncludeStore2 = referenceObfIncludeStore;
                try {
                    objectInputStream.close();
                } catch (final IOException ex) {
                }
                return referenceObfIncludeStore2;
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
        return new ReferenceObfIncludeStore();
    }

    private ReferenceObfIncludeStore() {
    }
}
