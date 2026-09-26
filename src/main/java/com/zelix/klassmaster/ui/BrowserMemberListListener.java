package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.classfile.MemberInfo;
import com.zelix.klassmaster.ui.component.MemberListNode;
import com.zelix.klassmaster.util.ZkmUtils;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class BrowserMemberListListener implements ListSelectionListener {
    public ZkmMainWindow mainWindow;
    public MemberListNode memberListNode;

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        try {
            if (!listSelectionEvent.getValueIsAdjusting()) {
                int selectedIndex = ((JList) listSelectionEvent.getSource()).getSelectedIndex();
                if (selectedIndex > -1) {
                    MemberInfo memberInfo1 = this.memberListNode.getMember(selectedIndex);
                    this.mainWindow.showMemberProperties(memberInfo1);
                }
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public BrowserMemberListListener(MemberListNode memberListNode1, ZkmMainWindow zkmMainWindow) {
        this.mainWindow = zkmMainWindow;
        this.memberListNode = memberListNode1;
    }
}
