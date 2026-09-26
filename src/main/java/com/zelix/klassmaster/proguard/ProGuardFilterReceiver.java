package com.zelix.klassmaster.proguard;

public interface ProGuardFilterReceiver {
    boolean addArchiveFilter(Object object);

    boolean addFileFilter(Object object);
}
