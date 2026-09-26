package com.zelix.klassmaster.util;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ChangeObservable extends ObservableSupport {
    public static final String JAVA_CLASS_PATH = System.getProperty("java.class.path");
    public NodeVisitor[] visitorSnapshot;
    public boolean changed = false;

    @Override
    public final void notifyObservers(Object object, Object object1, Object object2) throws ZkmException, IOException {
        int ba;
        synchronized (this) {
            if (!this.hasChanged() || this.observers == null) {
                return;
            }

            ba = this.observers.size();
            if (this.visitorSnapshot == null) {
                this.visitorSnapshot = new NodeVisitor[ba];
            } else if (ba > this.visitorSnapshot.length) {
                this.visitorSnapshot = new NodeVisitor[ba];
            }

            this.visitorSnapshot = ((com.zelix.klassmaster.util.NodeVisitor[]) (this.observers.toArray(this.visitorSnapshot)));
            this.clearChanged();
        }

        for (int i = ba - 1; i >= 0; i += -1) {
            if (this.visitorSnapshot[i] != null) {
                this.visitorSnapshot[i].handleObservedChange(this, object, object1, object2);
            }
        }
    }

    public synchronized void clearChanged() {
        this.changed = false;
    }

    public synchronized boolean hasChanged() {
        return this.changed;
    }

    @Override
    public synchronized void setChanged() {
        this.changed = true;
    }
}
