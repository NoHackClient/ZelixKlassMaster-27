package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.classfile.constpool.UsedConstantsCollector;
import com.zelix.klassmaster.util.MutableInt;
import com.zelix.klassmaster.util.VisitableNode;

import java.io.IOException;

public class AccessFlags extends ClassFileComponent {
    private int flags;

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public final void makePublic() {
        this.flags = makePublic(this.flags);
    }

    public static int makePrivate(int ba) {
        int bb = ba;
        bb &= -8;
        return bb | 2;
    }

    public static boolean isEnum(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 16384) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public final boolean isSynthetic() {
        return isSynthetic(this.flags);
    }

    public static boolean isInterface(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 512) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public static int setVolatileFlag(int ba, boolean bl) {
        int bb = ba;
        if (bl) {
            bb |= 64;
        } else {
            bb &= -65;
        }

        return bb;
    }

    static {
        new MutableInt(1);
        new MutableInt(2);
        new MutableInt(3);
        new MutableInt(4);
    }

    public final void markVarargs() {
        this.flags = setVarargsFlag(this.flags, true);
    }

    public final int getFlags() {
        return this.flags;
    }

    public static int addStatic(int ba) {
        return setStaticFlag(ba, true);
    }

    public static int addPublic(int ba) {
        int bb = ba;
        return bb | 1;
    }

    public static boolean isAnnotation(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 8192) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public final void makeProtected() {
        this.flags = makeProtected(this.flags);
    }

    public static int addEnum(int ba) {
        return setEnumFlag(ba, true);
    }

    public static int setVarargsFlag(int ba, boolean bl) {
        int bb = ba;
        if (bl) {
            bb |= 128;
        } else {
            bb &= -129;
        }

        return bb;
    }

    public final boolean isInterface() {
        return isInterface(this.flags);
    }

    public final void setSynthetic() {
        this.flags = setSyntheticFlag(this.flags, false);
    }

    public static int addTransient(int ba) {
        return setTransientFlag(ba, true);
    }

    public final boolean isStatic() {
        return isStatic(this.flags);
    }

    public static boolean isVarargs(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 128) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public static int addSynthetic(int ba) {
        return setSyntheticFlag(ba, true);
    }

    public static int makePackagePrivate(int ba) {
        int bb = ba;
        return bb & -8;
    }

    public final boolean isBridge() {
        return isBridge(this.flags);
    }

    public static int addAnnotation(int ba) {
        return setAnnotationFlag(ba, true);
    }

    @Override
    public void collectUsedConstants(char ba, int bb, UsedConstantsCollector usedConstantsCollector, char bc) {
    }

    public static int setTransientFlag(int ba, boolean bl) {
        int bb = ba;
        if (bl) {
            bb |= 128;
        } else {
            bb &= -129;
        }

        return bb;
    }

    public static int setAbstractFlag(int ba, boolean bl) {
        int bb = ba;
        if (bl) {
            bb |= 1024;
        } else {
            bb &= -1025;
        }

        return bb;
    }

    public final void makePrivate() {
        this.flags = makePrivate(this.flags);
    }

    public static boolean isAbstract(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 1024) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public final void setStatic() {
        this.flags = setStaticFlag(this.flags, true);
    }

    public static String formatModifiers(int ba, int bb, boolean bc, boolean bd) {
        VisitableNode[] visitableNodes1;
        StringBuilder stringBuilder;
        int bg;
        label398:
        {
            label399:
            {
                VisitableNode[] visitableNodes2 = ClassFileComponent.getFlowGuardNodes();
                stringBuilder = new StringBuilder();
                visitableNodes1 = visitableNodes2;
                bg = ba;
                if (visitableNodes1 != null) {
                    if (isPublic(ba)) {
                        StringBuilder stringBuilder1;
                        String string13;
                        if ((bd ? 1 : 0) != 0) {
                            stringBuilder.append("!");
                            stringBuilder1 = stringBuilder;
                            string13 = "public";
                        } else {
                            stringBuilder1 = stringBuilder;
                            string13 = "public";
                        }

                        stringBuilder1.append(string13);
                        stringBuilder.append(" ");
                    }

                    bg = ba;
                    if (visitableNodes1 == null) {
                        break label399;
                    }

                    bg = ((isProtected(ba)) ? 1 : 0);
                }

                if (bg != 0) {
                    StringBuilder stringBuilder2;
                    String string;
                    if ((bd ? 1 : 0) != 0) {
                        stringBuilder.append("!");
                        stringBuilder2 = stringBuilder;
                        string = "protected";
                    } else {
                        stringBuilder2 = stringBuilder;
                        string = "protected";
                    }

                    stringBuilder2.append(string);
                    stringBuilder.append(" ");
                }

                bg = ba;
                if (visitableNodes1 == null) {
                    break label398;
                }

                bg = ((isPrivate(ba)) ? 1 : 0);
            }

            if (bg != 0) {
                StringBuilder stringBuilder3;
                String string1;
                if ((bd ? 1 : 0) != 0) {
                    stringBuilder.append("!");
                    stringBuilder3 = stringBuilder;
                    string1 = "private";
                } else {
                    stringBuilder3 = stringBuilder;
                    string1 = "private";
                }

                stringBuilder3.append(string1);
                stringBuilder.append(" ");
            }

            bg = bb;
        }

        label383:
        {
            label382:
            {
                if (visitableNodes1 != null) {
                    if (bg != 1) {
                        bg = bb;
                        if (visitableNodes1 == null) {
                            break label383;
                        }

                        if (bb != 3) {
                            break label382;
                        }
                    }

                    bg = ba;
                    if (visitableNodes1 == null) {
                        break label383;
                    }

                    bg = ((isAbstract(ba)) ? 1 : 0);
                }

                if (bg != 0) {
                    StringBuilder stringBuilder4;
                    String string2;
                    if ((bd ? 1 : 0) != 0) {
                        stringBuilder.append("!");
                        stringBuilder4 = stringBuilder;
                        string2 = "abstract";
                    } else {
                        stringBuilder4 = stringBuilder;
                        string2 = "abstract";
                    }

                    stringBuilder4.append(string2);
                    stringBuilder.append(" ");
                }
            }

            bg = bb;
        }

        label402:
        {
            label403:
            {
                label368:
                {
                    if (visitableNodes1 != null) {
                        if (bg != 2) {
                            bg = bb;
                            if (visitableNodes1 == null) {
                                break label403;
                            }

                            if (bb != 3) {
                                break label368;
                            }
                        }

                        bg = ba;
                        if (visitableNodes1 == null) {
                            break label403;
                        }

                        bg = ((isStatic(ba)) ? 1 : 0);
                    }

                    if (bg != 0) {
                        StringBuilder stringBuilder5;
                        String string3;
                        if ((bd ? 1 : 0) != 0) {
                            stringBuilder.append("!");
                            stringBuilder5 = stringBuilder;
                            string3 = "static";
                        } else {
                            stringBuilder5 = stringBuilder;
                            string3 = "static";
                        }

                        stringBuilder5.append(string3);
                        stringBuilder.append(" ");
                    }
                }

                bg = ba;
                if (visitableNodes1 == null) {
                    break label402;
                }

                bg = ((isFinal(ba)) ? 1 : 0);
            }

            if (bg != 0) {
                StringBuilder stringBuilder6;
                String string4;
                if ((bd ? 1 : 0) != 0) {
                    stringBuilder.append("!");
                    stringBuilder6 = stringBuilder;
                    string4 = "final";
                } else {
                    stringBuilder6 = stringBuilder;
                    string4 = "final";
                }

                stringBuilder6.append(string4);
                stringBuilder.append(" ");
            }

            bg = bb;
        }

        label353:
        {
            label405:
            {
                if (visitableNodes1 != null) {
                    if (bg == 3) {
                        bg = ba;
                        if (visitableNodes1 == null) {
                            break label353;
                        }

                        if (!isSynchronized(ba)) {
                            break label405;
                        }

                        if ((bd ? 1 : 0) != 0) {
                            stringBuilder.append("!");
                        }

                        stringBuilder.append("synchronized");
                        stringBuilder.append(" ");
                        if (visitableNodes1 != null) {
                            break label405;
                        }
                    }

                    bg = bb;
                }

                if (visitableNodes1 == null) {
                    break label353;
                }

                if (bg == 1) {
                    bg = (bc ? 1 : 0);
                    if (visitableNodes1 == null) {
                        break label353;
                    }

                    if ((bc ? 1 : 0) != 0) {
                        bg = ba;
                        if (visitableNodes1 == null) {
                            break label353;
                        }

                        if (isSuper(ba)) {
                            StringBuilder stringBuilder7;
                            String string5;
                            if ((bd ? 1 : 0) != 0) {
                                stringBuilder.append("!");
                                stringBuilder7 = stringBuilder;
                                string5 = "super";
                            } else {
                                stringBuilder7 = stringBuilder;
                                string5 = "super";
                            }

                            stringBuilder7.append(string5);
                            stringBuilder.append(" ");
                        }
                    }
                }
            }

            bg = bb;
        }

        label331:
        {
            label407:
            {
                if (visitableNodes1 != null) {
                    if (bg == 3) {
                        bg = ba;
                        if (visitableNodes1 == null) {
                            break label331;
                        }

                        int be = ba;
                        if (!isNative(be)) {
                            break label407;
                        }

                        if ((bd ? 1 : 0) != 0) {
                            stringBuilder.append("!");
                        }

                        stringBuilder.append("native");
                        stringBuilder.append(" ");
                        if (visitableNodes1 != null) {
                            break label407;
                        }
                    }

                    bg = bb;
                }

                if (visitableNodes1 != null) {
                    if (bg != 1) {
                        bg = bb;
                        if (visitableNodes1 == null) {
                            break label331;
                        }

                        if (bb != 2) {
                            break label407;
                        }
                    }

                    bg = ba;
                    if (visitableNodes1 == null) {
                        break label331;
                    }

                    bg = ((isEnum(ba)) ? 1 : 0);
                }

                if (bg != 0) {
                    StringBuilder stringBuilder8;
                    String string6;
                    if ((bd ? 1 : 0) != 0) {
                        stringBuilder.append("!");
                        stringBuilder8 = stringBuilder;
                        string6 = "enum";
                    } else {
                        stringBuilder8 = stringBuilder;
                        string6 = "enum";
                    }

                    stringBuilder8.append(string6);
                    stringBuilder.append(" ");
                }
            }

            bg = bb;
        }

        label307:
        if (visitableNodes1 != null) {
            if (bg == 1) {
                bg = ba;
                if (visitableNodes1 == null) {
                    break label307;
                }

                if (isModule(ba)) {
                    StringBuilder stringBuilder9;
                    String string7;
                    if ((bd ? 1 : 0) != 0) {
                        stringBuilder.append("!");
                        stringBuilder9 = stringBuilder;
                        string7 = "module";
                    } else {
                        stringBuilder9 = stringBuilder;
                        string7 = "module";
                    }

                    stringBuilder9.append(string7);
                    stringBuilder.append(" ");
                }
            }

            bg = bb;
        }

        label300:
        {
            label410:
            {
                if (visitableNodes1 != null) {
                    if (bg == 2) {
                        bg = ba;
                        if (visitableNodes1 == null) {
                            break label300;
                        }

                        if (!isVolatile(ba)) {
                            break label410;
                        }

                        if ((bd ? 1 : 0) != 0) {
                            stringBuilder.append("!");
                        }

                        stringBuilder.append("volatile");
                        stringBuilder.append(" ");
                        if (visitableNodes1 != null) {
                            break label410;
                        }
                    }

                    bg = bb;
                }

                if (visitableNodes1 == null) {
                    break label300;
                }

                if (bg == 3) {
                    bg = ba;
                    if (visitableNodes1 == null) {
                        break label300;
                    }

                    if (isBridge(ba)) {
                        StringBuilder stringBuilder10;
                        String string8;
                        if ((bd ? 1 : 0) != 0) {
                            stringBuilder.append("!");
                            stringBuilder10 = stringBuilder;
                            string8 = "bridge";
                        } else {
                            stringBuilder10 = stringBuilder;
                            string8 = "bridge";
                        }

                        stringBuilder10.append(string8);
                        stringBuilder.append(" ");
                    }
                }
            }

            bg = bb;
        }

        label281:
        {
            label412:
            {
                if (visitableNodes1 != null) {
                    if (bg == 2) {
                        bg = ba;
                        if (visitableNodes1 == null) {
                            break label281;
                        }

                        if (!isTransient(ba)) {
                            break label412;
                        }

                        if ((bd ? 1 : 0) != 0) {
                            stringBuilder.append("!");
                        }

                        stringBuilder.append("transient");
                        stringBuilder.append(" ");
                        if (visitableNodes1 != null) {
                            break label412;
                        }
                    }

                    bg = bb;
                }

                if (visitableNodes1 == null) {
                    break label281;
                }

                if (bg == 3) {
                    bg = (bc ? 1 : 0);
                    if (visitableNodes1 == null) {
                        break label281;
                    }

                    if ((bc ? 1 : 0) != 0) {
                        bg = ba;
                        if (visitableNodes1 == null) {
                            break label281;
                        }

                        if (isVarargs(ba)) {
                            StringBuilder stringBuilder11;
                            String string9;
                            if ((bd ? 1 : 0) != 0) {
                                stringBuilder.append("!");
                                stringBuilder11 = stringBuilder;
                                string9 = "varargs";
                            } else {
                                stringBuilder11 = stringBuilder;
                                string9 = "varargs";
                            }

                            stringBuilder11.append(string9);
                            stringBuilder.append(" ");
                        }
                    }
                }
            }

            bg = (bc ? 1 : 0);
        }

        label259:
        if (visitableNodes1 != null) {
            if (bg != 0) {
                bg = ba;
                if (visitableNodes1 == null) {
                    break label259;
                }

                int bf = ba;
                if (isInterface(bf)) {
                    StringBuilder stringBuilder12;
                    String string10;
                    if ((bd ? 1 : 0) != 0) {
                        stringBuilder.append("!");
                        stringBuilder12 = stringBuilder;
                        string10 = "interface";
                    } else {
                        stringBuilder12 = stringBuilder;
                        string10 = "interface";
                    }

                    stringBuilder12.append(string10);
                    stringBuilder.append(" ");
                }
            }

            bg = bb;
        }

        label415:
        {
            label416:
            {
                label417:
                {
                    if (visitableNodes1 != null) {
                        if (bg == 1) {
                            bg = ba;
                            if (visitableNodes1 == null) {
                                break label416;
                            }

                            if (!isAnnotation(ba)) {
                                break label417;
                            }

                            if ((bd ? 1 : 0) != 0) {
                                stringBuilder.append("!");
                            }

                            stringBuilder.append("annotation");
                            stringBuilder.append(" ");
                            if (visitableNodes1 != null) {
                                break label417;
                            }
                        }

                        bg = bb;
                    }

                    if (visitableNodes1 == null) {
                        break label416;
                    }

                    if (bg == 3) {
                        bg = (bc ? 1 : 0);
                        if (visitableNodes1 == null) {
                            break label416;
                        }

                        if ((bc ? 1 : 0) != 0) {
                            bg = ba;
                            if (visitableNodes1 == null) {
                                break label416;
                            }

                            if (isStrict(ba)) {
                                StringBuilder stringBuilder13;
                                String string11;
                                if ((bd ? 1 : 0) != 0) {
                                    stringBuilder.append("!");
                                    stringBuilder13 = stringBuilder;
                                    string11 = "strict";
                                } else {
                                    stringBuilder13 = stringBuilder;
                                    string11 = "strict";
                                }

                                stringBuilder13.append(string11);
                                stringBuilder.append(" ");
                            }
                        }
                    }
                }

                bg = ba;
                if (visitableNodes1 == null) {
                    break label415;
                }

                bg = ((isSynthetic(ba)) ? 1 : 0);
            }

            if (bg == 0) {
                return stringBuilder.toString();
            }

            bg = (bd ? 1 : 0);
        }

        StringBuilder stringBuilder14;
        String string12;
        if (bg != 0) {
            stringBuilder.append("!");
            stringBuilder14 = stringBuilder;
            string12 = "synthetic";
        } else {
            stringBuilder14 = stringBuilder;
            string12 = "synthetic";
        }

        stringBuilder14.append(string12);
        stringBuilder.append(" ");
        return stringBuilder.toString();
    }

    public static String formatModifiers(int ba, int bb, boolean bl) {
        return formatModifiers(ba, bb, bl, false);
    }

    public static boolean isModule(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 32768) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public final boolean isSynchronized() {
        return isSynchronized(this.flags);
    }

    public static boolean isTransient(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 128) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public static int setFinalFlag(int ba, boolean bl) {
        int bb = ba;
        if (bl) {
            bb |= 16;
        } else {
            bb &= -17;
        }

        return bb;
    }

    public static int makeProtected(int ba) {
        int bb = ba;
        bb &= -8;
        return bb | 4;
    }

    public final boolean isPrivate() {
        return isPrivate(this.flags);
    }

    public static int setSynchronizedFlag(int ba, boolean bl) {
        int bb = ba;
        if (bl) {
            bb |= 32;
        } else {
            bb &= -33;
        }

        return bb;
    }

    public static boolean isVolatile(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 64) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public static int setNativeFlag(int ba, boolean bl) {
        int bb = ba;
        if (bl) {
            bb |= 256;
        } else {
            bb &= -257;
        }

        return bb;
    }

    public static int addBridge(int ba) {
        return setBridgeFlag(ba, true);
    }

    @Override
    public Object clone() {
        return new AccessFlags(this.getParent(), this.flags);
    }

    public static boolean isSynthetic(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 4096) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public final boolean isAbstract() {
        return isAbstract(this.flags);
    }

    public static int addVolatile(int ba) {
        return setVolatileFlag(ba, true);
    }

    public final boolean isModule() {
        return isModule(this.flags);
    }

    public static boolean isProtected(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 4) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public static boolean isNative(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 256) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public static boolean isFinal(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 16) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public final void setBridge() {
        this.flags = setBridgeFlag(this.flags, false);
    }

    public static boolean isSynchronized(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 32) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public static int addNative(int ba) {
        return setNativeFlag(ba, true);
    }

    public static int addAbstract(int ba) {
        return setAbstractFlag(ba, true);
    }

    public static boolean isPublic(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 1) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public final void setAnnotation() {
        this.flags = setAnnotationFlag(this.flags, false);
    }

    public static int setBridgeFlag(int ba, boolean bl) {
        int bb = ba;
        if (bl) {
            bb |= 64;
        } else {
            bb &= -65;
        }

        return bb;
    }

    public final boolean isVolatile() {
        return isVolatile(this.flags);
    }

    public AccessFlags(ClassFileComponent classFileComponent, ClassFileInputStream classFileInputStream) throws IOException {
        super(classFileComponent);
        this.flags = classFileInputStream.readUnsignedShort();
    }

    public static int addFinal(int ba) {
        return setFinalFlag(ba, true);
    }

    public static boolean isStrict(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 2048) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public static boolean isSuper(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 32) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public final void setEnum() {
        this.flags = setEnumFlag(this.flags, false);
    }

    public static int setInterfaceFlag(int ba, boolean bl) {
        int bb = ba;
        if (bl) {
            bb |= 512;
        } else {
            bb &= -513;
        }

        return bb;
    }

    public static int setSyntheticFlag(int ba, boolean bl) {
        int bb = ba;
        if (bl) {
            bb |= 4096;
        } else {
            bb &= -4097;
        }

        return bb;
    }

    public static int setStaticFlag(int ba, boolean bl) {
        int bb = ba;
        if (bl) {
            bb |= 8;
        } else {
            bb &= -9;
        }

        return bb;
    }

    public int getVisibilityRank() {
        if (this.isPublic()) {
            return 4;
        } else if (this.isProtected()) {
            return 3;
        } else {
            return this.isPrivate() ? 1 : 2;
        }
    }

    public final boolean isAnnotation() {
        return isAnnotation(this.flags);
    }

    public static int addInterface(int ba) {
        return setInterfaceFlag(ba, true);
    }

    public static int makePublic(int ba) {
        int bb = ba;
        bb &= -8;
        return bb | 1;
    }

    public final boolean isNative() {
        return isNative(this.flags);
    }

    public static int setAnnotationFlag(int ba, boolean bl) {
        int bb = ba;
        if (bl) {
            bb |= 8192;
        } else {
            bb &= -8193;
        }

        return bb;
    }

    public final boolean isPackagePrivate() {
        return isPackagePrivate(this.flags);
    }

    public final boolean isPublic() {
        return isPublic(this.flags);
    }

    public final void makePackagePrivate() {
        this.flags = makePackagePrivate(this.flags);
    }

    public final String formatModifiers(boolean bl) {
        return formatModifiers(this.flags, this.getParentKind(), bl);
    }

    public static int setEnumFlag(int ba, boolean bl) {
        int bb = ba;
        if (bl) {
            bb |= 16384;
        } else {
            bb &= -16385;
        }

        return bb;
    }

    public static boolean isBridge(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 64) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public static boolean isPrivate(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 2) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public final boolean isProtected() {
        return isProtected(this.flags);
    }

    public final void setFinal(boolean bl) {
        this.flags = setFinalFlag(this.flags, bl);
    }

    public final void setVarargs() {
        this.flags = setVarargsFlag(this.flags, false);
    }

    public final boolean isFinal() {
        return isFinal(this.flags);
    }

    public static int addSynchronized(int ba) {
        return setSynchronizedFlag(ba, true);
    }

    public String getModifierString() {
        return this.formatModifiers(false);
    }

    public final boolean isSuper() {
        return isSuper(this.flags);
    }

    public AccessFlags(ClassFileComponent classFileComponent, int flags) {
        super(classFileComponent);
        this.flags = flags;
    }

    public static int addProtected(int ba) {
        int bb = ba;
        return bb | 4;
    }

    public static boolean isStatic(int ba) {
        VisitableNode[] visitableNodes1 = ClassFileComponent.getFlowGuardNodes();
        int bb = ba;
        if (visitableNodes1 != null) {
            bb = (ba & 8) != 0 ? 1 : 0;
        }

        return bb != 0;
    }

    public final boolean isEnum() {
        return isEnum(this.flags);
    }

    public final boolean isVarargs() {
        return isVarargs(this.flags);
    }

    public static boolean isPackagePrivate(int ba) {
        return (ba & 7) == 0;
    }

    public AccessFlags(ClassFileComponent classFileComponent) {
        super(classFileComponent);
        this.flags = 0;
    }

    public final boolean isTransient() {
        return isTransient(this.flags);
    }

    public static int addPrivate(int ba) {
        int bb = ba;
        return bb | 2;
    }
}
