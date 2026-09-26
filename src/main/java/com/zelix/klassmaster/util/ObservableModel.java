package com.zelix.klassmaster.util;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public interface ObservableModel {
    void notifyObservers(Object object, Object object1, Object object2) throws ZkmException, IOException;

    void notifyObserver(NodeVisitor nodeVisitor) throws ZkmException, IOException;

    void setChanged();

    void setChangedAndNotify() throws ZkmException, IOException;

    void addObserver(NodeVisitor nodeVisitor);

    void notifyObserverWithoutArgs(NodeVisitor nodeVisitor) throws ZkmException, IOException;

    void notifyObservers() throws ZkmException, IOException;

    void removeObserver(Object object);
}
