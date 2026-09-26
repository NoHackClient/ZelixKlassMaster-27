package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.ClassPropertyNode;
import com.zelix.klassmaster.ui.component.EdtCallback;
import com.zelix.klassmaster.ui.component.EdtCallbackInvoker;
import com.zelix.klassmaster.ui.component.PlatformAwareJList;
import com.zelix.klassmaster.ui.component.SelectionHolder;
import com.zelix.klassmaster.util.NodeVisitor;
import com.zelix.klassmaster.util.ObservableModel;
import com.zelix.klassmaster.util.VisitableNode;

import java.awt.Container;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.DefaultListModel;

public class ClassPropertiesListView implements NodeVisitor, EdtCallback {
    public Object lastSelectedNode;
    public SelectionHolder selectionHolder;
    public PlatformAwareJList propertiesList;
    public DefaultListModel listModel;

    @Override
    public void handleObservedChange(ObservableModel observableModel1, Object object, Object object1, Object object2) throws ZkmException, IOException {
        new EdtCallbackInvoker(this, observableModel1, object, object1, object2);
    }

    public ClassPropertiesListView(SelectionHolder selectionHolder1, PlatformAwareJList platformAwareJList) {
        this.selectionHolder = selectionHolder1;
        this.propertiesList = platformAwareJList;
        this.listModel = (DefaultListModel) platformAwareJList.getModel();
        this.listModel.addElement("no properties available");
        this.propertiesList.setEnabled(false);
        selectionHolder1.addObserver(this);
        this.relayoutList();
    }

    @Override
    public void handleChangeOnEdt(Object object, Object object1, Object object2, Object object3) throws ZkmException, IOException {
        VisitableNode visitableNode = ((SelectionHolder) object).getSelectedNode();
        if (visitableNode != null) {
            if (visitableNode != this.lastSelectedNode) {
                if (visitableNode instanceof ProgramClass) {
                    this.listModel = new DefaultListModel();
                    Enumeration enumeration = ((ProgramClass) visitableNode).createPropertyNodes();

                    while (enumeration.hasMoreElements()) {
                        ClassPropertyNode classPropertyNode = (ClassPropertyNode) enumeration.nextElement();
                        this.listModel.addElement(classPropertyNode.getPropertyName());
                    }

                    this.propertiesList.setModel(this.listModel);
                    this.propertiesList.setEnabled(true);
                }

                this.relayoutList();
            }
        } else {
            this.listModel = new DefaultListModel();
            this.listModel.addElement("no properties available");
            this.propertiesList.setModel(this.listModel);
            this.propertiesList.setEnabled(false);
            this.relayoutList();
        }

        this.lastSelectedNode = visitableNode;
    }

    public void relayoutList() {
        Container container1 = this.propertiesList.getParent();
        container1.removeAll();
        container1.add(this.propertiesList, "Center");
    }
}
