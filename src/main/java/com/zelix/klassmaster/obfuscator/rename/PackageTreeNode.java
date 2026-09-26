package com.zelix.klassmaster.obfuscator.rename;

import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.obfuscator.exclude.NameExclusionSet;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.TwoKeyMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PackageTreeNode extends PackageTreeNodeBase {
    public boolean requireLowerCaseStart;
    public final PackageTreeNodeBase parent;
    public final String simpleName;

    public PackageTreeNode(PackageTreeNodeBase packageTreeNodeBase, String string, boolean bl) {
        this.parent = packageTreeNodeBase;
        this.simpleName = string;
        if (!bl) {
            this.setArchiveOnly(false);
        }
    }

    public void collectRetainedPackageNames(Map map1, NameExclusionSet nameExclusionSet, ChangeLogMapping changeLogMapping1, String string) throws IOException {
        Iterator iterator = super.children.iterator();

        while (iterator.hasNext()) {
            String string1 = string;
            PackageTreeNode packageTreeNode1 = (PackageTreeNode) iterator.next();
            String string2 = packageTreeNode1.getPackagePath();
            if (changeLogMapping1 != null && changeLogMapping1.hasPackageMapping(string2)) {
                String string3 = changeLogMapping1.getNewPackageName(string2);
                if (string3 != null) {
                    string1 = string3;
                } else {
                    string1 = string2;
                }
            } else if (nameExclusionSet.isPackageExcluded(string2)) {
                string1 = string2;
            }

            map1.put(string2, string1);
            packageTreeNode1.collectRetainedPackageNames(map1, nameExclusionSet, changeLogMapping1, string1);
        }
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public void indexInto(Object object) {
        String string = this.getPackagePath();
        ((Map) object).put(string, this);
    }

    public PackageTreeNode getTopLevelAncestor() {
        return this.parent.isRoot() ? this : ((PackageTreeNode) this.parent).getTopLevelAncestor();
    }

    public void setRequireLowerCaseStart() {
        this.requireLowerCaseStart = true;
    }

    public void assignNewNamesRecursively(
            NameExclusionSet nameExclusionSet,
            ChangeLogMapping changeLogMapping1,
            PackageNameGeneratorBase packageNameGeneratorBase,
            Map map1,
            ListMultimap listMultimap,
            TwoKeyMap twoKeyMap,
            ListMultimap listMultimap1,
            boolean bl
    ) throws IOException {
        HashSet hashSet = ZkmUtils.createHashSet();
        List list1 = listMultimap1.getValues(this.parent.getPackagePath());
        if (list1 != null) {
            for (int i = 0; i < list1.size(); i++) {
                ProgramClass programClass1 = (ProgramClass) list1.get(i);
                if (nameExclusionSet.isClassExcluded(programClass1)) {
                    hashSet.add(programClass1.getSimpleName());
                }
            }
        }

        PackageTreeNode[] packageTreeNodes;
        label32:
        {
            this.assignNewName(nameExclusionSet, changeLogMapping1, packageNameGeneratorBase, map1, listMultimap, twoKeyMap, hashSet);
            packageTreeNodes = ((com.zelix.klassmaster.obfuscator.rename.PackageTreeNode[]) (super.children.toArray(new PackageTreeNode[super.children.size()])));
            List list2;
            if (!bl) {
                if (!HiddenOptionFlags.RANDOMIZE_OBFUSCATION) {
                    Arrays.sort(packageTreeNodes);
                    break label32;
                }

                list2 = Arrays.asList(packageTreeNodes);
            } else {
                list2 = Arrays.asList(packageTreeNodes);
            }

            ZkmUtils.shuffleList(list2, ZkmUtils.createRandom(16));
        }

        for (int i = 0; i < packageTreeNodes.length; i++) {
            packageTreeNodes[i]
                    .assignNewNamesRecursively(nameExclusionSet, changeLogMapping1, packageNameGeneratorBase, map1, listMultimap, twoKeyMap, listMultimap1, bl);
        }
    }

    @Override
    public void collectPath(Object object) {
        String string = this.getPackagePath();
        ((ArrayList) object).add(string);
    }

    public void assignNewName(
            NameExclusionSet nameExclusionSet,
            ChangeLogMapping changeLogMapping1,
            PackageNameGeneratorBase packageNameGeneratorBase,
            Map map1,
            ListMultimap listMultimap,
            TwoKeyMap twoKeyMap,
            Set set1
    ) throws IOException {
        String string = this.getPackagePath();
        boolean bl = this.isTopLevel() && this.requireLowerCaseStart;
        if (!map1.containsKey(string)) {
            String string1 = (String) map1.get(this.parent.getPackagePath());
            if (string1 == null) {
                string1 = this.parent.getPackagePath();
            }

            String string2 = packageNameGeneratorBase.generatePackageName(
                    string1, this.isArchiveOnly(), bl, listMultimap, twoKeyMap, set1, changeLogMapping1, nameExclusionSet
            );
            String string3 = (string1.length() > 0 ? string1 + "/" : "") + string2;
            map1.put(string, string3);
            listMultimap.addValue(string3, string);
        }
    }

    public boolean isTopLevel() {
        return this.parent.isRoot();
    }

    public String getSimpleName() {
        return this.simpleName;
    }

    @Override
    public String getPackagePath() {
        String string = this.parent == null ? null : this.parent.getPackagePath();
        String string1;
        if (string != null) {
            if (string.length() > 0) {
                return string + "/" + this.simpleName;
            }

            string1 = this.simpleName;
        } else {
            string1 = this.simpleName;
        }

        return string1;
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
