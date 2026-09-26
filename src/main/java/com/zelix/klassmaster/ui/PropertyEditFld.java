package com.zelix.klassmaster.ui;

import javax.swing.JTextField;

public class PropertyEditFld extends JTextField {
    public boolean settingText;

    @Override
    public void setText(String string) {
        this.settingText = true;
        super.setText(string);
        this.settingText = false;
    }

    public boolean isSettingText() {
        return this.settingText;
    }
}
