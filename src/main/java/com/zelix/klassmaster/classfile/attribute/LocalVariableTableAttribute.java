package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.ConstantPool;
import com.zelix.klassmaster.classfile.insn.LocalVariableList;
import com.zelix.klassmaster.classfile.insn.LocalVariableSlot;
import com.zelix.klassmaster.classfile.insn.MethodFlowAnalyzer;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ObjectPair;
import com.zelix.klassmaster.util.ObservableHolder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LocalVariableTableAttribute extends LocalVariableTableBase {
    private static String invalidAttributeMessage;
    private static long entrySize;

    public final void addMissingEntries(
            int ba, MethodFlowAnalyzer methodFlowAnalyzer, ConstantPool constantPool1, List list1, LocalVariableList localVariableList1, ListMultimap listMultimap
    ) throws ZkmException, IOException {
        if (this.valid) {
            ListMultimap listMultimap1 = new ListMultimap(ba);

            for (LocalVariableEntry localVariableEntry : this.entries) {
                listMultimap1.addValue(localVariableEntry.getLocalIndex(), localVariableEntry);
            }

            int lastSlotIndex = localVariableList1.getLastSlotIndex();
            boolean bl = false;

            for (int i = 0; i < lastSlotIndex; i++) {
                if (!listMultimap1.containsKey(i)) {
                    LocalVariableSlot localVariableSlot1 = localVariableList1.getSlotAt(i);
                    if (localVariableSlot1 != null && !localVariableSlot1.isWideSecondHalf() && localVariableSlot1.isInserted()) {
                        ObservableHolder observableHolder = new ObservableHolder();
                        ObjectPair objectPair = methodFlowAnalyzer.findLocalVariableScope(i, observableHolder);
                        String string = (String) observableHolder.getValue();
                        if ((Integer) objectPair.getFirst() > -1 && (Integer) objectPair.getSecond() > -1 && string != null) {
                            LocalVariableEntry localVariableEntry1 = new LocalVariableEntry(
                                    this,
                                    (Integer) objectPair.getFirst(),
                                    (Integer) objectPair.getSecond(),
                                    constantPool1.createUtf8Constant("a", list1),
                                    constantPool1.createUtf8Constant(string, list1),
                                    localVariableSlot1,
                                    listMultimap
                            );
                            listMultimap1.addValue(i, localVariableEntry1);
                            bl = true;
                        }
                    }
                }
            }

            if (bl) {
                ArrayList arrayList = new ArrayList(listMultimap1.getValueCount());

                for (int i = 0; i < ba; i++) {
                    if (listMultimap1.containsKey(i)) {
                        Iterator iterator = listMultimap1.getValues(i).iterator();

                        while (iterator.hasNext()) {
                            LocalVariableEntry localVariableEntry2 = (LocalVariableEntry) iterator.next();
                            arrayList.add(localVariableEntry2);
                        }
                    }
                }

                this.entries = ((com.zelix.klassmaster.classfile.attribute.LocalVariableEntry[]) (arrayList.toArray(new LocalVariableEntry[arrayList.size()])));
                this.entryCount = this.entries.length;
                this.length = this.entryCount * (int) entrySize + 2;
            }
        }
    }

    @Override
    public final LocalVariableEntry readEntry(
            ClassFileInputStream classFileInputStream, LocalVariableList localVariableList1, ListMultimap listMultimap, ListMultimap listMultimap1
    ) throws IOException {
        return new LocalVariableEntry(this, classFileInputStream, localVariableList1, listMultimap, listMultimap1);
    }

    public LocalVariableTableAttribute(
            ClassFileComponent classFileComponent,
            int ba,
            String string,
            ClassFileInputStream classFileInputStream,
            LocalVariableList localVariableList1,
            ListMultimap listMultimap,
            PrintWriter printWriter,
            ListMultimap listMultimap1
    ) throws IOException {
        super(classFileComponent, ba, string, classFileInputStream, localVariableList1, listMultimap, printWriter, listMultimap1, invalidAttributeMessage);
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
        invalidAttributeMessage = "Invalid LocalVariableTable Attribute";
        entrySize = -6826888089365905398L;
    }
}
