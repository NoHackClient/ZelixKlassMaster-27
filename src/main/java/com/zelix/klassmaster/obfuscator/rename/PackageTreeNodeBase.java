package com.zelix.klassmaster.obfuscator.rename;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class PackageTreeNodeBase implements Comparable {
    public Set children = new LinkedHashSet();
    public boolean archiveOnly = true;

    public abstract boolean isRoot();

    public void collectPath(Object object) {
    }

    public void collectAllPaths(ArrayList arrayList) {
        this.collectPath(arrayList);
        Iterator iterator = this.children.iterator();

        while (iterator.hasNext()) {
            ((PackageTreeNode) iterator.next()).collectAllPaths(arrayList);
        }
    }

    public boolean isArchiveOnly() {
        return this.archiveOnly;
    }

    public abstract String getPackagePath();

    public void indexAllNodes(Map map1) {
        this.indexInto(map1);
        Iterator iterator = this.children.iterator();

        while (iterator.hasNext()) {
            ((PackageTreeNode) iterator.next()).indexAllNodes(map1);
        }
    }

    public final int comparePathIgnoreCase(PackageTreeNodeBase packageTreeNodeBase1) {
        return this.getPackagePath().toLowerCase().compareTo(packageTreeNodeBase1.getPackagePath().toLowerCase());
    }

    @Override
    public int compareTo(Object object) {
        return this.comparePathIgnoreCase((PackageTreeNodeBase) object);
    }

    public abstract void indexInto(Object object);

    public void setArchiveOnly(boolean archiveOnly) {
        this.archiveOnly = archiveOnly;
    }

    public final void addPackagePath(int ba, String[] strings, Map map1) {
        int bb = ba;
        if (bb < strings.length) {
            String string = strings[bb];
            Iterator iterator = this.children.iterator();

            while (iterator.hasNext()) {
                PackageTreeNode packageTreeNode = (PackageTreeNode) iterator.next();
                if (string.equals(packageTreeNode.getSimpleName())) {
                    packageTreeNode.addPackagePath(++bb, strings, map1);
                    return;
                }
            }

            String string1 = this.getPackagePath();
            String string2 = (string1.length() > 0 ? string1 + "/" : "") + string;
            boolean bl = false;
            if (map1 != null && map1.containsKey(string2)) {
                bl = (Boolean) map1.get(string2);
            }

            PackageTreeNode packageTreeNode1 = new PackageTreeNode(this, string, bl);
            this.children.add(packageTreeNode1);
            packageTreeNode1.addPackagePath(++bb, strings, map1);
        }
    }
}
