package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.exceptions.InvalidEditException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.ui.component.PlatformAwareJList;
import com.zelix.klassmaster.util.ListenerRegistry;

import java.io.IOException;
import javax.swing.DefaultListModel;

public class MethodPropertiesPanel extends MemberPropertiesPanel {
    @Override
    public void applyAccessFlags(int ba) throws ZkmException, IOException {
        ClassRepository classRepository1;
        if ((ba & 1024) != 0) {
            if ((ba & 2) != 0) {
                throw new InvalidEditException("An abstract method cannot also be private");
            }

            if ((ba & 8) != 0) {
                throw new InvalidEditException("An abstract method cannot also be static");
            }

            if ((ba & 16) != 0) {
                throw new InvalidEditException("An abstract method cannot also be final");
            }

            if ((ba & 256) != 0) {
                throw new InvalidEditException("An abstract method cannot also be native");
            }

            if ((ba & 32) != 0) {
                throw new InvalidEditException("An abstract method cannot also be synchronized");
            }

            classRepository1 = super.classRepository;
        } else {
            classRepository1 = super.classRepository;
        }

        synchronized (classRepository1) {
            super.memberInfo.setAccessFlags(ba);
        }
    }

    @Override
    public void renameMember(Object object) throws ZkmException, IOException {
    }

    @Override
    public void initModifierIndices() {
        super.initModifierIndices();
        super.abstractIndex = 0;
        super.staticIndex = 1;
        super.finalIndex = 2;
        super.synchronizedIndex = 3;
        super.nativeIndex = 4;
    }

    @Override
    public void populateModifierList(PlatformAwareJList platformAwareJList) {
        DefaultListModel defaultListModel = (DefaultListModel) platformAwareJList.getModel();
        defaultListModel.addElement("abstract");
        defaultListModel.addElement("static");
        defaultListModel.addElement("final");
        defaultListModel.addElement("synchronized");
        defaultListModel.addElement("native");
    }

    public MethodPropertiesPanel(MethodInfo methodInfo1, ClassRepository classRepository1, ZkmMainWindow zkmMainWindow, ListenerRegistry listenerRegistry1) {
        super(methodInfo1, classRepository1, zkmMainWindow, listenerRegistry1);
    }

    @Override
    public int getRetainedFlagsMask() {
        return -1344;
    }
}
