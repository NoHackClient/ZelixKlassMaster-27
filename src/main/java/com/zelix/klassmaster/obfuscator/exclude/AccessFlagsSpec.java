package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.classfile.AccessFlags;

public class AccessFlagsSpec {
    public int memberKind;
    private boolean packageRequired;
    private boolean packageForbidden;
    private int requiredFlags;
    private int forbiddenFlags;

    public final boolean isStaticForbidden() {
        return AccessFlags.isStatic(this.forbiddenFlags);
    }

    public final void forbidProtected() {
        this.forbiddenFlags = AccessFlags.addProtected(this.forbiddenFlags);
    }

    public final void setStaticRequired(boolean bl) {
        this.requiredFlags = AccessFlags.setStaticFlag(this.requiredFlags, bl);
    }

    public final void setNativeForbidden(boolean bl) {
        this.forbiddenFlags = AccessFlags.setNativeFlag(this.forbiddenFlags, bl);
    }

    public final void setVolatileRequired(boolean bl) {
        this.requiredFlags = AccessFlags.setVolatileFlag(this.requiredFlags, bl);
    }

    public final boolean isNativeForbidden() {
        return AccessFlags.isNative(this.forbiddenFlags);
    }

    public final void clearRequiredAccess() {
        this.requiredFlags = AccessFlags.makePackagePrivate(this.requiredFlags);
        this.packageRequired = false;
    }

    public final boolean isTransientForbidden() {
        return AccessFlags.isTransient(this.forbiddenFlags);
    }

    public final void setVolatileForbidden(boolean bl) {
        this.forbiddenFlags = AccessFlags.setVolatileFlag(this.forbiddenFlags, bl);
    }

    public final void forbidPrivate() {
        this.forbiddenFlags = AccessFlags.addPrivate(this.forbiddenFlags);
    }

    public final void setSynchronizedForbidden(boolean bl) {
        this.forbiddenFlags = AccessFlags.setSynchronizedFlag(this.forbiddenFlags, bl);
    }

    public final boolean isStaticRequired() {
        return AccessFlags.isStatic(this.requiredFlags);
    }

    @Override
    public Object clone() {
        return new AccessFlagsSpec(this);
    }

    public final void forbidPackage() {
        this.packageForbidden = true;
    }

    public final boolean isAbstractForbidden() {
        return AccessFlags.isAbstract(this.forbiddenFlags);
    }

    public final void forbidInterface() {
        this.forbiddenFlags = AccessFlags.addInterface(this.forbiddenFlags);
    }

    public final void requirePublic() {
        this.requiredFlags = AccessFlags.makePublic(this.requiredFlags);
        this.packageRequired = false;
    }

    public final void setSynchronizedRequired(boolean bl) {
        this.requiredFlags = AccessFlags.setSynchronizedFlag(this.requiredFlags, bl);
    }

    public final boolean isPackageForbidden() {
        return this.packageForbidden;
    }

    public final boolean isNativeRequired() {
        return AccessFlags.isNative(this.requiredFlags);
    }

    public final boolean isProtectedRequired() {
        return AccessFlags.isProtected(this.requiredFlags);
    }

    public final boolean isSynchronizedRequired() {
        return AccessFlags.isSynchronized(this.requiredFlags);
    }

    public final void forbidEnum() {
        this.forbiddenFlags = AccessFlags.addEnum(this.forbiddenFlags);
    }

    public final void setTransientForbidden(boolean bl) {
        this.forbiddenFlags = AccessFlags.setTransientFlag(this.forbiddenFlags, bl);
    }

    public final void setTransientRequired(boolean bl) {
        this.requiredFlags = AccessFlags.setTransientFlag(this.requiredFlags, bl);
    }

    public final void forbidAnnotation() {
        this.forbiddenFlags = AccessFlags.addAnnotation(this.forbiddenFlags);
    }

    public final void forbidVolatile() {
        long ba = 70103916668044L;
        ba = 96617610827666L ^ ba;
        long bb = ba ^ 57132155778872L;
        int forbiddenFlags = this.forbiddenFlags;
        this.forbiddenFlags = AccessFlags.addBridge(forbiddenFlags);
    }

    public final boolean isVolatileRequired() {
        return AccessFlags.isVolatile(this.requiredFlags);
    }

    public final boolean isVolatileForbidden() {
        return AccessFlags.isVolatile(this.forbiddenFlags);
    }

    public final void forbidNative() {
        this.forbiddenFlags = AccessFlags.addNative(this.forbiddenFlags);
    }

    public final void setFinalRequired(boolean bl) {
        this.requiredFlags = AccessFlags.setFinalFlag(this.requiredFlags, bl);
    }

    public boolean isContradictory() {
        return (this.requiredFlags & this.forbiddenFlags) != 0 || this.packageRequired && this.packageForbidden;
    }

    public final void setNativeRequired(boolean bl) {
        this.requiredFlags = AccessFlags.setNativeFlag(this.requiredFlags, bl);
    }

    public final boolean isFinalRequired() {
        return AccessFlags.isFinal(this.requiredFlags);
    }

    public final void requireFinal() {
        this.requiredFlags = AccessFlags.addFinal(this.requiredFlags);
    }

    public final void requireProtected() {
        this.requiredFlags = AccessFlags.makeProtected(this.requiredFlags);
        this.packageRequired = false;
    }

    public final void requireSynthetic() {
        this.requiredFlags = AccessFlags.addSynthetic(this.requiredFlags);
    }

    public final void requireInterface() {
        this.requiredFlags = AccessFlags.addInterface(this.requiredFlags);
    }

    public final void requireSynchronized() {
        long ba = 69119499534881L;
        ba = 96617610827666L ^ ba;
        long bb = ba ^ 125665473366682L;
        int requiredFlags = this.requiredFlags;
        this.requiredFlags = AccessFlags.addSynchronized(requiredFlags);
    }

    public final void requirePrivate() {
        this.requiredFlags = AccessFlags.makePrivate(this.requiredFlags);
        this.packageRequired = false;
    }

    public final void setInterfaceRequired(boolean bl) {
        this.requiredFlags = AccessFlags.setInterfaceFlag(this.requiredFlags, bl);
    }

    public final void setInterfaceForbidden(boolean bl) {
        this.forbiddenFlags = AccessFlags.setInterfaceFlag(this.forbiddenFlags, bl);
    }

    public final void forbidTransient() {
        long ba = 17435564844073L;
        ba = 96617610827666L ^ ba;
        long bb = ba ^ 36692230699008L;
        int forbiddenFlags = this.forbiddenFlags;
        this.forbiddenFlags = AccessFlags.addTransient(forbiddenFlags);
    }

    public final void setFinalForbidden(boolean bl) {
        this.forbiddenFlags = AccessFlags.setFinalFlag(this.forbiddenFlags, bl);
    }

    public final void forbidBridge() {
        this.forbiddenFlags = AccessFlags.addVolatile(this.forbiddenFlags);
    }

    public final void forbidPublic() {
        this.forbiddenFlags = AccessFlags.addPublic(this.forbiddenFlags);
    }

    public final void forbidAbstract() {
        this.forbiddenFlags = AccessFlags.addAbstract(this.forbiddenFlags);
    }

    public final boolean isFinalForbidden() {
        return AccessFlags.isFinal(this.forbiddenFlags);
    }

    public final void requireTransient() {
        long ba = 115929878650718L;
        ba = 96617610827666L ^ ba;
        long bb = ba ^ 79115931222903L;
        int requiredFlags = this.requiredFlags;
        this.requiredFlags = AccessFlags.addTransient(requiredFlags);
    }

    public final boolean isPackageRequired() {
        return this.packageRequired;
    }

    public final void forbidSynthetic() {
        this.forbiddenFlags = AccessFlags.addSynthetic(this.forbiddenFlags);
    }

    public final void forbidStatic() {
        long ba = 113055165470325L;
        ba = 96617610827666L ^ ba;
        long bb = ba ^ 34003110179768L;
        Integer integer = this.forbiddenFlags;
        this.forbiddenFlags = AccessFlags.addStatic(integer);
    }

    public final void forbidFinal() {
        this.forbiddenFlags = AccessFlags.addFinal(this.forbiddenFlags);
    }

    public final void requireEnum() {
        this.requiredFlags = AccessFlags.addEnum(this.requiredFlags);
    }

    public final boolean isAbstractRequired() {
        return AccessFlags.isAbstract(this.requiredFlags);
    }

    private AccessFlagsSpec(AccessFlagsSpec accessFlagsSpec2) {
        this.memberKind = accessFlagsSpec2.memberKind;
        this.packageRequired = accessFlagsSpec2.packageRequired;
        this.packageForbidden = accessFlagsSpec2.packageForbidden;
        this.requiredFlags = accessFlagsSpec2.requiredFlags;
        this.forbiddenFlags = accessFlagsSpec2.forbiddenFlags;
    }

    public final void requireNative() {
        this.requiredFlags = AccessFlags.addNative(this.requiredFlags);
    }

    public final void requireStatic() {
        this.requiredFlags = AccessFlags.addStatic(this.requiredFlags);
    }

    public final boolean isTransientRequired() {
        return AccessFlags.isTransient(this.requiredFlags);
    }

    public final boolean isPrivateRequired() {
        return AccessFlags.isPrivate(this.requiredFlags);
    }

    public final void setAbstractForbidden(boolean bl) {
        this.forbiddenFlags = AccessFlags.setAbstractFlag(this.forbiddenFlags, bl);
    }

    public final boolean isInterfaceForbidden() {
        return AccessFlags.isInterface(this.forbiddenFlags);
    }

    public final String toSpecString() {
        String string = AccessFlags.formatModifiers(this.requiredFlags, this.memberKind, true, false);
        String string1 = AccessFlags.formatModifiers(this.forbiddenFlags, this.memberKind, true, true);
        if (string.length() > 0 && string1.length() > 0) {
            return string + string1;
        } else if (string.length() > 0) {
            return string;
        } else {
            return string1.length() > 0 ? string1 : "";
        }
    }

    public final void setStaticForbidden(boolean bl) {
        this.forbiddenFlags = AccessFlags.setStaticFlag(this.forbiddenFlags, bl);
    }

    public final boolean isInterfaceRequired() {
        return AccessFlags.isInterface(this.requiredFlags);
    }

    public boolean matches(int ba) {
        return (this.requiredFlags & ba) == this.requiredFlags
                && (this.forbiddenFlags & ba) == 0
                && (!this.packageRequired || AccessFlags.isPackagePrivate(ba))
                && (!this.packageForbidden || !AccessFlags.isPackagePrivate(ba));
    }

    public final void requireBridge() {
        long ba = 58371896757886L;
        ba = 96617610827666L ^ ba;
        long bb = ba ^ 62820590722506L;
        int requiredFlags = this.requiredFlags;
        this.requiredFlags = AccessFlags.addBridge(requiredFlags);
    }

    public final void requirePackage() {
        this.requiredFlags = AccessFlags.makePackagePrivate(this.requiredFlags);
        this.packageRequired = true;
    }

    public final void forbidSynchronized() {
        long ba = 32292620972247L;
        ba = 96617610827666L ^ ba;
        long bb = ba ^ 89925087311980L;
        int forbiddenFlags = this.forbiddenFlags;
        this.forbiddenFlags = AccessFlags.addSynchronized(forbiddenFlags);
    }

    public final boolean isSynchronizedForbidden() {
        return AccessFlags.isSynchronized(this.forbiddenFlags);
    }

    public final void requireVolatile() {
        this.requiredFlags = AccessFlags.addVolatile(this.requiredFlags);
    }

    public final void requireAnnotation() {
        this.requiredFlags = AccessFlags.addAnnotation(this.requiredFlags);
    }

    public final void setAbstractRequired(boolean bl) {
        this.requiredFlags = AccessFlags.setAbstractFlag(this.requiredFlags, bl);
    }

    public AccessFlagsSpec(int memberKind) {
        this.memberKind = memberKind;
    }

    public final void requireAbstract() {
        this.requiredFlags = AccessFlags.addAbstract(this.requiredFlags);
    }

    public final boolean isPublicRequired() {
        return AccessFlags.isPublic(this.requiredFlags);
    }
}
