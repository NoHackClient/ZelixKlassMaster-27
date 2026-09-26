package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ChangeObservable;
import com.zelix.klassmaster.util.NodeVisitor;
import com.zelix.klassmaster.util.ObservableModel;

import java.io.IOException;

public class ClassPropertyNode extends ChangeObservable implements NodeVisitor, EdtCallback {
    public String propertyName;
    public ClassFileComponent ownerComponent;

    public ClassFileComponent getOwnerComponent() {
        return this.ownerComponent;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    @Override
    public void handleChangeOnEdt(Object object3, Object object, Object object1, Object object2) throws ZkmException, IOException {
        this.setChanged();
        this.notifyObservers(object, object1, object2);
    }

    @Override
    public void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        new EdtCallbackInvoker(this, observableModel1, object, object1, object2);
    }

    public ClassPropertyNode(String string, ClassFileComponent classFileComponent) {
        this.propertyName = string;
        this.ownerComponent = classFileComponent;
    }
}
