package com.zelix.klassmaster.util;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public class ObservableHolder extends ChangeObservable {
    public Object value;

    public ObservableHolder(Object object) throws ZkmException, IOException {
        this.setValue(object);
    }

    public Object getValue() {
        return this.value;
    }

    public boolean isValueNull() {
        return this.value == null;
    }

    public void setValue(Object object) throws ZkmException, IOException {
        this.value = object;
        this.setChanged();
        this.notifyObservers(object, null, null);
    }

    public ObservableHolder() throws ZkmException, IOException {
        this.setValue(null);
    }

    public ObservableHolder clearValue() throws ZkmException, IOException {
        this.setValue(null);
        return this;
    }
}
