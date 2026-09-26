package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.constpool.LoadableConstant;
import com.zelix.klassmaster.classfile.constpool.NumericConstantEntry;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.IntegerCache;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ClassConstantsNode extends ListClassProperty {
    public NumericConstantEntry[] constants;
    public SetMultiMap duplicateStrings;
    public int constantCount;
    public Map canonicalStrings;
    private static final String ENTRY_NOT_FOUND_MSG = "Entry not found in ";

    public ClassConstantsNode(ClassFileComponent classFileComponent, NumericConstantEntry[] numericConstantEntrys, IntegerCache integerCache1) {
        super("constants", classFileComponent, integerCache1);
        this.initConstants(numericConstantEntrys);
    }

    public int getConstantIndex(LoadableConstant loadableConstant) {
        if (super.indexMap == null) {
            this.canonicalStrings = ZkmUtils.createHashMap();
            Iterator iterator = this.duplicateStrings.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) entry.getKey();
                Iterator iterator1 = ((Set) entry.getValue()).iterator();

                while (iterator1.hasNext()) {
                    ResolvedStringConstant resolvedStringConstant1 = (ResolvedStringConstant) iterator1.next();
                    this.canonicalStrings.put(resolvedStringConstant1, resolvedStringConstant);
                }
            }

            super.indexMap = ZkmUtils.createHashMap((int) (this.constantCount * 1.5));
            int ba = 0;
            int bb = 0;

            for (int i = this.constantCount; bb < i; i = this.constantCount) {
                super.indexMap.put(this.constants[ba], super.integerCache.valueOf(ba));
                bb = ++ba;
            }
        }

        Object object = super.indexMap
                .get(
                        loadableConstant instanceof ResolvedStringConstant
                                ? ZkmUtils.mapOrSelf((ResolvedStringConstant) loadableConstant, this.canonicalStrings)
                                : loadableConstant
                );
        if (object != null) {
            return (Integer) object;
        } else {
            throw new IllegalArgumentException(ENTRY_NOT_FOUND_MSG + this.getClass().getName());
        }
    }

    public String nextConstantText() {
        return this.constants[super.cursor++].getEditableValue();
    }

    public boolean hasNextConstant() {
        return super.cursor < this.constantCount;
    }

    public void initConstants(NumericConstantEntry[] numericConstantEntrys) {
        this.duplicateStrings = new SetMultiMap();
        ArrayList arrayList = new ArrayList();

        for (NumericConstantEntry numericConstantEntry : numericConstantEntrys) {
            if (numericConstantEntry instanceof ResolvedStringConstant) {
                ResolvedStringConstant resolvedStringConstant = (ResolvedStringConstant) numericConstantEntry;
                ResolvedStringConstant resolvedStringConstant1 = resolvedStringConstant.getSourceConstant();
                if (resolvedStringConstant1 != null) {
                    this.duplicateStrings.addValue(resolvedStringConstant1, resolvedStringConstant);
                } else {
                    arrayList.add(resolvedStringConstant);
                }
            } else {
                arrayList.add(numericConstantEntry);
            }
        }

        this.constantCount = arrayList.size();
        this.constants = new NumericConstantEntry[this.constantCount];
        this.constants = ((com.zelix.klassmaster.classfile.constpool.NumericConstantEntry[]) (arrayList.toArray(this.constants)));
        super.indexMap = null;
        this.resetCursor();
    }

    public int getConstantCount() {
        return this.constantCount;
    }

    public NumericConstantEntry getConstant(int ba) {
        return this.constants[ba];
    }

    public SetMultiMap getDuplicateStrings() {
        return this.duplicateStrings;
    }

    public void updateConstants(NumericConstantEntry[] numericConstantEntrys) throws ZkmException, IOException {
        this.initConstants(numericConstantEntrys);
        this.setChanged();
        this.notifyObservers();
    }
}
