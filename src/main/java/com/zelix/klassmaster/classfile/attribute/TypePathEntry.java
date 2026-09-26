package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.ClassFileComponent;
import com.zelix.klassmaster.classfile.ClassFileInputStream;
import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;

import java.io.DataOutputStream;
import java.io.IOException;

public class TypePathEntry extends ClassFileComponent {
    public boolean valid = true;
    public TypePathKind kind;
    public int typeArgumentIndex;

    public boolean isValid() {
        return this.valid;
    }

    public TypePathEntry(ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream) throws IOException {
        super(classFileComponent);
        switch (classFileInputStream.readUnsignedByte()) {
            case 0:
                this.kind = TypePathKind.ARRAY;
                break;
            case 1:
                this.kind = TypePathKind.NESTED;
                break;
            case 2:
                this.kind = TypePathKind.WILDCARD;
                break;
            case 3:
                this.kind = TypePathKind.TYPE;
                break;
            default:
                this.valid = false;
        }

        this.typeArgumentIndex = classFileInputStream.readUnsignedByte();
        if (this.kind != TypePathKind.TYPE && this.typeArgumentIndex != 0) {
            this.valid = false;
        } else {
            this.valid = true;
        }
    }

    public boolean isTypeArgumentStep() {
        switch (SignatureKindSwitchMap.TYPE_PATH_KIND_SWITCH[this.kind.ordinal()]) {
            case 1:
            case 2:
                return false;
            case 3:
            case 4:
                return true;
            default:
                return false;
        }
    }

    public void writeTo(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeByte(this.kind.getCode());
        dataOutputStream.writeByte(this.typeArgumentIndex);
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
    }

    public int getEntrySize() {
        return 2;
    }
}
