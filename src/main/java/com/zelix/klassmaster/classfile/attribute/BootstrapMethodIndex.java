package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BootstrapMethodIndex {
    public static final String ARG_PLACEHOLDER = String.valueOf('\u0001');
    public Map infoByEntry = ZkmUtils.createHashMap();
    public static final String CONST_PLACEHOLDER = String.valueOf('\u0002');
    private final ProgramClass programClass;
    public final BootstrapMethodsAttribute bootstrapMethodsAttribute;

    public void expandRecipeConstants() {
        Iterator iterator = this.infoByEntry.entrySet().iterator();

        while (iterator.hasNext()) {
            BootstrapMethodInfo bootstrapMethodInfo = (BootstrapMethodInfo) ((Entry) iterator.next()).getValue();
            StringBuilder stringBuilder = new StringBuilder();
            List list1 = bootstrapMethodInfo.getRecipeTokens();
            ConstantPoolEntry[] constantPoolEntrys = bootstrapMethodInfo.getConstantArguments();
            int ba = 0;

            for (int i = 0; i < list1.size(); i++) {
                String string = (String) list1.get(i);
                stringBuilder.append(ARG_PLACEHOLDER);
                if (!string.equals(ARG_PLACEHOLDER)) {
                    if (string.equals(CONST_PLACEHOLDER)) {
                        ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) constantPoolEntrys[ba];
                        ConcatRecipePart concatRecipePart = new ConcatRecipePart(i, resolvedStringConstant.getValueString(), resolvedStringConstant);
                        bootstrapMethodInfo.addRecipePart(concatRecipePart);
                        ba++;
                    } else {
                        ConcatRecipePart concatRecipePart1 = new ConcatRecipePart(i, string);
                        bootstrapMethodInfo.addRecipePart(concatRecipePart1);
                    }
                }
            }

            bootstrapMethodInfo.setRewrittenRecipe(stringBuilder.toString());
        }
    }

    public void applyRewrittenRecipes() throws ZkmException, IOException {
        boolean bl = false;
        Iterator iterator = this.infoByEntry.entrySet().iterator();

        while (iterator.hasNext()) {
            BootstrapMethodInfo bootstrapMethodInfo = (BootstrapMethodInfo) ((Entry) iterator.next()).getValue();
            bootstrapMethodInfo.getRecipeConstant().setValueFromString(bootstrapMethodInfo.getRewrittenRecipe());
            if (bootstrapMethodInfo.getConstantArguments().length > 0) {
                bl = true;
                bootstrapMethodInfo.getBootstrapEntry().truncateArguments();
            }
        }

        if (bl) {
            this.bootstrapMethodsAttribute.getLength();
        }
    }

    public boolean isEmpty() {
        return this.infoByEntry.isEmpty();
    }

    public BootstrapMethodIndex(ProgramClass programClass1, BootstrapMethodsAttribute bootstrapMethodsAttribute1) {
        this.programClass = programClass1;
        this.bootstrapMethodsAttribute = bootstrapMethodsAttribute1;
    }

    public BootstrapMethodInfo getInfo(Object object) {
        return (BootstrapMethodInfo) this.infoByEntry.get(object);
    }

    public boolean hasInfo(Object object) {
        return this.infoByEntry.containsKey(object);
    }

    public static List tokenizeRecipe(String string, MutableInt mutableInt) {
        ArrayList arrayList = new ArrayList();
        int ba = 0;
        int bb = 0;
        int bc = 0;

        int bd;
        do {
            boolean bl = false;
            int be = string.indexOf(1, bc);
            int bf = string.indexOf(2, bc);
            if (be > -1 && bf == -1) {
                bd = be;
                bl = true;
            } else if (be == -1 && bf > -1) {
                bd = bf;
                bb++;
            } else if (be <= -1 || bf <= -1) {
                bd = -1;
            } else if (be < bf) {
                bl = true;
                bd = be;
            } else {
                bd = bf;
                bb++;
            }

            if (bd > -1) {
                ba++;
                if (bd > bc) {
                    arrayList.add(string.substring(bc, bd));
                    bb++;
                }

                arrayList.add(bl ? ARG_PLACEHOLDER : CONST_PLACEHOLDER);
                bc = bd + 1;
            }
        } while (bd > -1);

        if (bc < string.length()) {
            arrayList.add(string.substring(bc));
            bb++;
        }

        if (bb > 0) {
            mutableInt.setValue(ba);
            return arrayList;
        } else {
            return null;
        }
    }

    public void registerConcatBootstrap(BootstrapMethodEntry bootstrapMethodEntry, List list1, int ba) {
        this.infoByEntry.put(bootstrapMethodEntry, new BootstrapMethodInfo(bootstrapMethodEntry, list1, ba));
    }
}
