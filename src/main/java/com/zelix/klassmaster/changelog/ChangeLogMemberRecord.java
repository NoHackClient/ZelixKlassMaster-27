package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.classfile.AccessFlags;

import java.io.Serializable;

public abstract class ChangeLogMemberRecord implements Serializable {
    public final String H;
    public final String v;
    public final int Y;

    public ChangeLogMemberRecord(String string, String string1, int ba) {
        this.H = string;
        this.v = string1;
        this.Y = ba;
    }

    public String getName() {
        return this.H;
    }

    public String getType() {
        return this.v;
    }

    public boolean isPrivate() {
        return AccessFlags.isPrivate(this.Y);
    }
}
