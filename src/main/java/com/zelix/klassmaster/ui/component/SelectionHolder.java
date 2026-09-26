package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.NodeVisitor;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ObservableModel;
import com.zelix.klassmaster.util.VisitableNode;

import java.io.IOException;

public class SelectionHolder extends ObservableHolder implements NodeVisitor {
    public void selectNode(VisitableNode visitableNode) throws ZkmException, IOException {
        this.setSelectedNode(visitableNode);
    }

    @Override
    public void setValue(Object object) throws ZkmException, IOException {
        this.selectNode((VisitableNode) object);
    }

    @Override
    public void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        this.setChanged();
        this.notifyObservers(object, object1, object2);
    }

    public void setSelectedNode(VisitableNode visitableNode) throws ZkmException, IOException {
        if (visitableNode != this.value) {
            if (this.value != null) {
                ((VisitableNode) this.value).removeObserver(this);
            }

            super.setValue(visitableNode);
            if (visitableNode != null) {
                visitableNode.addObserver(this);
            }
        }
    }

    public SelectionHolder() throws ZkmException, IOException {
    }

    public VisitableNode getSelectedNode() {
        return (VisitableNode) super.getValue();
    }
}
