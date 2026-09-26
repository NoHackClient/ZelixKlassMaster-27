package com.zelix.klassmaster.util;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;
import java.util.List;

public abstract class VisitableNode implements ObservableModel {
    public List observers;
    private static int[] staticIntArray;

    @Override
    public abstract void removeObserver(Object object);

    public static int[] getStaticIntArray() {
        return staticIntArray;
    }

    public static void setStaticIntArray(int[] ba) {
        staticIntArray = ba;
    }

    @Override
    public abstract void notifyObservers(Object object, Object object1, Object object2) throws ZkmException, IOException;

    @Override
    public final void notifyObserverWithoutArgs(NodeVisitor nodeVisitor) throws ZkmException, IOException {
        this.notifyObserver(nodeVisitor);
    }

    @Override
    public final void notifyObserver(NodeVisitor nodeVisitor) throws ZkmException, IOException {
        nodeVisitor.handleObservedChange(this, null, null, null);
    }

    @Override
    public synchronized void setChanged() {
    }

    @Override
    public final void setChangedAndNotify() throws ZkmException, IOException {
        this.setChanged();
        this.notifyObservers();
    }

    @Override
    public abstract void addObserver(NodeVisitor nodeVisitor);

    @Override
    public void notifyObservers() throws ZkmException, IOException {
        this.notifyObservers(null, null, null);
    }

    static {
        if (getStaticIntArray() != null) {
            setStaticIntArray(new int[1]);
        }
    }
}
