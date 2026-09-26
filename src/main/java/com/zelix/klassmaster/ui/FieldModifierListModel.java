package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.classfile.FieldInfo;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.exceptions.InvalidEditException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.PlatformAwareJList;
import com.zelix.klassmaster.util.ListenerRegistry;

import java.io.IOException;
import javax.swing.DefaultListModel;

public class FieldModifierListModel extends MemberPropertiesPanel {
    @Override
    public void initModifierIndices() {
        super.initModifierIndices();
        super.staticIndex = 0;
        super.finalIndex = 1;
        super.volatileIndex = 2;
        super.transientIndex = 3;
    }

    public FieldModifierListModel(FieldInfo fieldInfo, ClassRepository classRepository1, ZkmMainWindow zkmMainWindow, ListenerRegistry listenerRegistry1) {
        super(fieldInfo, classRepository1, zkmMainWindow, listenerRegistry1);
    }

    @Override
    public void applyAccessFlags(int ba) throws ZkmException, IOException {
        if ((ba & 16) != 0 && (ba & 64) != 0) {
            throw new InvalidEditException("An field cannot be final and volatile");
        }

        if (!super.memberInfo.getOwningClass().isInterface() || (ba & 8) != 0 && (ba & 16) != 0 && (ba & 1) != 0) {
            synchronized (super.classRepository) {
                super.memberInfo.setAccessFlags(ba);
            }
        } else {
            throw new InvalidEditException("Fields in Interfaces must be public, static and final");
        }
    }

    @Override
    public void renameMember(Object object) throws ZkmException, IOException {
        String string = (String) object;
        synchronized (super.classRepository) {
            super.classRepository.renameField((FieldInfo) super.memberInfo, string);
        }
    }

    @Override
    public int getRetainedFlagsMask() {
        return -224;
    }

    @Override
    public void populateModifierList(PlatformAwareJList platformAwareJList) {
        DefaultListModel defaultListModel = (DefaultListModel) platformAwareJList.getModel();
        defaultListModel.addElement("static");
        defaultListModel.addElement("final");
        defaultListModel.addElement("volatile");
        defaultListModel.addElement("transient");
    }
}
