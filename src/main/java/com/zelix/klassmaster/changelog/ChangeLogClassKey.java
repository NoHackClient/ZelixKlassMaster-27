package com.zelix.klassmaster.changelog;

public class ChangeLogClassKey extends ChangeLogMemberRecord implements Comparable {
    @Override
    public int hashCode() {
        return this.v == null ? this.H.hashCode() : this.H.hashCode() ^ this.v.hashCode();
    }

    public final int compareToKey(ChangeLogClassKey changeLogClassKey1) {
        return this.H.compareTo(changeLogClassKey1.H);
    }

    public ChangeLogClassKey(String string, String string1, int ba) {
        super(string, string1, ba);
    }

    @Override
    public int compareTo(Object object) {
        return this.compareToKey((ChangeLogClassKey) object);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ChangeLogClassKey)) {
            return false;
        }

        ChangeLogClassKey changeLogClassKey1 = (ChangeLogClassKey) object;
        return this.H.equals(changeLogClassKey1.H)
                && (this.v == null && changeLogClassKey1.v == null || this.v != null && changeLogClassKey1.v != null && this.v.equals(changeLogClassKey1.v));
    }
}
