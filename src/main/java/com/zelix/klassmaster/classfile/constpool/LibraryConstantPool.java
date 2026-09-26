package com.zelix.klassmaster.classfile.constpool;

import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.classfile.hierarchy.ClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassFile;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ClassFileFormatException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class LibraryConstantPool extends AbstractConstantPool {
    @Override
    public synchronized int appendEntries(List list1) {
        int ba = super.appendEntries(list1);
        int bb = list1.size();

        for (int i = 0; i < bb; i++) {
            this.registerEntry((ConstantPoolEntry) list1.get(i));
        }

        return ba;
    }

    public LibraryConstantPool(
            ClassFileInputStream classFileInputStream,
            ClasspathClassFile classpathClassFile,
            ListMultimap listMultimap,
            ListMultimap listMultimap1,
            ListMultimap listMultimap2,
            ListMultimap listMultimap3
    ) throws ClassFileFormatException, IOException {
        super(classpathClassFile);
        int ba = classFileInputStream.readUnsignedShort();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList1 = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        this.entries = new ConstantPoolEntry[ba];
        this.utf8Constants = new ArrayList(Math.max(5, (int) (ba * 1.5)));
        this.nameAndTypes = new ArrayList(Math.max(5, (int) (ba * 0.2)));
        this.classConstants = new ArrayList(Math.max(5, (int) (ba * 0.09)));
        super.classesByName = ZkmUtils.createHashMap(Math.max(5, (int) (ba * 0.09)));
        this.stringConstants = new ArrayList(Math.max(5, (int) (ba * 0.05)));
        super.integerConstants = new ArrayList(Math.max(5, (int) (ba * 0.01)));
        this.methodRefs = new ArrayList(Math.max(5, (int) (ba * 0.15)));
        super.interfaceMethodRefs = new ArrayList(Math.max(5, (int) (ba * 0.15)));
        this.fieldRefs = new ArrayList(Math.max(5, (int) (ba * 0.15)));
        this.entries[0] = ConstantNullEntry.getSharedInstance(this);
        int bb = 1;

        while (bb < ba) {
            ConstantPoolEntry constantPoolEntry = ConstantPoolEntry.readLibraryEntry(bb, classFileInputStream, this);
            if (constantPoolEntry.isUnresolved()) {
                if (constantPoolEntry instanceof ConstantClass) {
                    arrayList.add(constantPoolEntry);
                } else if (constantPoolEntry instanceof ConstantNameAndType) {
                    arrayList1.add(constantPoolEntry);
                } else {
                    arrayList2.add(constantPoolEntry);
                }
            }

            this.entries[bb++] = constantPoolEntry;
            if (constantPoolEntry.getSlotCount() == 2) {
                this.entries[bb++] = ConstantNullEntry.getSharedInstance(this);
            }
        }

        this.resolveConstants(arrayList, listMultimap, listMultimap1, listMultimap2, listMultimap3);
        this.resolveConstants(arrayList1, listMultimap, listMultimap1, listMultimap2, listMultimap3);
        this.resolveConstants(arrayList2, listMultimap, listMultimap1, listMultimap2, listMultimap3);
    }

    @Override
    public void applyPackageRenames(Object object) {
        HashMap hashMap = (HashMap) object;

        for (int i = 1; i < this.entries.length; i++) {
            if (this.entries[i] instanceof ResolvedPackageConstant) {
                ((ResolvedPackageConstant) this.entries[i]).remapName(hashMap);
            }
        }
    }

    public void resolveConstants(List list1, ListMultimap listMultimap, ListMultimap listMultimap1, ListMultimap listMultimap2, ListMultimap listMultimap3) throws ClassFileFormatException {
        int ba = 0;

        boolean bl;
        do {
            bl = false;
            ListIterator listIterator = list1.listIterator();

            while (listIterator.hasNext()) {
                ConstantPoolEntry constantPoolEntry = (ConstantPoolEntry) listIterator.next();
                StringWriter stringWriter = new StringWriter();
                new PrintWriter(stringWriter);
                ConstantPoolEntry constantPoolEntry1 = ((ResolvableConstant) constantPoolEntry).resolve(listMultimap, listMultimap1, listMultimap2, listMultimap3);
                if (constantPoolEntry1 == null) {
                    bl = true;
                } else {
                    listIterator.remove();
                    this.entries[constantPoolEntry.getIndex()] = constantPoolEntry1;
                    if (!constantPoolEntry1.isWellFormed()) {
                        throw new ZkmRuntimeException(stringWriter.toString());
                    }

                    this.registerEntry(constantPoolEntry1);
                }
            }

            if (++ba > 3) {
                throw new ClassFileFormatException(this.getClassLocationDescription() + " : " + " Unknown problem validating constant pool (B).");
            }
        } while (bl);
    }

    public void registerEntry(ConstantPoolEntry constantPoolEntry) {
        ConstantPoolTag constantPoolTag = constantPoolEntry.getTag();
        switch (ConstantTypeSwitchMap.TAG_SWITCH_TABLE[constantPoolTag.ordinal()]) {
            case 1:
                this.classConstants.add((ResolvedClassConstant) constantPoolEntry);
                super.classesByName.put(((ResolvedClassConstant) constantPoolEntry).getClassName(), (ResolvedClassConstant) constantPoolEntry);
                break;
            case 2:
                this.nameAndTypes.add((ResolvedNameAndType) constantPoolEntry);
                break;
            case 3:
                this.methodRefs.add((ResolvedMethodRefConstant) constantPoolEntry);
                break;
            case 4:
                super.interfaceMethodRefs.add((ResolvedInterfaceMethodRef) constantPoolEntry);
                break;
            case 5:
                this.fieldRefs.add((ResolvedFieldRef) constantPoolEntry);
                break;
            case 6:
                this.utf8Constants.add((ConstantUtf8) constantPoolEntry);
                break;
            case 7:
                this.stringConstants.add((ResolvedStringConstant) constantPoolEntry);
                break;
            case 8:
                super.integerConstants.add((ConstantInteger) constantPoolEntry);
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            default:
                break;
            case 14:
                super.invokeDynamics.add((ResolvedInvokeDynamic) constantPoolEntry);
        }
    }

    public void resolveMemberRefTargets(
            ClassMemberLookup classMemberLookup1, ClassResolver classResolver1, IgnoreMissingReferencesSpec ignoreMissingReferencesSpec1
    ) throws ZkmException, IOException {
        for (int i = 1; i < this.entries.length; i++) {
            if (this.entries[i] instanceof ResolvedMemberRef) {
                ((ResolvedMemberRef) this.entries[i]).resolveMember(classMemberLookup1, classResolver1, ignoreMissingReferencesSpec1);
            }
        }
    }

    @Override
    public void renameThisClass(Object object) {
        String string = (String) object;
        String string1 = this.getClassName();

        for (ConstantPoolEntry constantPoolEntry : this.entries) {
            if (constantPoolEntry instanceof ResolvedClassConstant) {
                ResolvedClassConstant resolvedClassConstant = (ResolvedClassConstant) constantPoolEntry;
                if (resolvedClassConstant.getClassName().equals(string1)) {
                    resolvedClassConstant.setClassName(string);
                }
            }
        }
    }

    @Override
    public boolean isProgramPool() {
        return false;
    }

    @Override
    public final void applyClassRenames(Object object, Object object1) throws ZkmException, IOException {
        HashMap hashMap = (HashMap) object;

        for (int i = 1; i < this.entries.length; i++) {
            if (!(this.entries[i] instanceof ConstantNullEntry)) {
                if (this.entries[i] instanceof ResolvedClassConstant) {
                    ((ResolvedClassConstant) this.entries[i]).remapName(hashMap);
                } else if (this.entries[i] instanceof ResolvedNameAndType) {
                    ((ResolvedNameAndType) this.entries[i]).remapDescriptor(hashMap);
                } else if (this.entries[i] instanceof ResolvedMethodType) {
                    ((ResolvedMethodType) this.entries[i]).remapDescriptor(hashMap);
                }
            }
        }
    }
}
