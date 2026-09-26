package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.util.MutableInt;

import java.util.List;

public class ExtraParamLayoutGenerator {
    public static final char[][] PARAM_TYPE_LAYOUTS = new char[][]{
            {'J'},
            {'I', 'I'},
            {'B', 'J'},
            {'J', 'B'},
            {'S', 'J'},
            {'J', 'S'},
            {'C', 'J'},
            {'J', 'C'},
            {'I', 'J'},
            {'J', 'I'},
            {'I', 'I', 'B'},
            {'I', 'B', 'I'},
            {'B', 'I', 'I'},
            {'S', 'S', 'I'},
            {'S', 'I', 'S'},
            {'S', 'I', 'I'},
            {'S', 'I', 'C'},
            {'S', 'C', 'I'},
            {'I', 'S', 'S'},
            {'I', 'S', 'I'},
            {'I', 'S', 'C'},
            {'I', 'I', 'S'},
            {'I', 'I', 'I'},
            {'I', 'I', 'C'},
            {'I', 'C', 'S'},
            {'I', 'C', 'I'},
            {'I', 'C', 'C'},
            {'C', 'S', 'I'},
            {'C', 'I', 'S'},
            {'C', 'I', 'I'},
            {'C', 'I', 'C'},
            {'C', 'C', 'I'}
    };

    public static boolean isFullyShiftedLeft(ChangedMethodDescriptor changedMethodDescriptor) {
        if (changedMethodDescriptor != null) {
            int ba = changedMethodDescriptor.getAddedParamCount() - 1;
            return changedMethodDescriptor.getLastParam().getIndex() == ba;
        } else {
            return false;
        }
    }

    public static ChangedMethodDescriptor nextLayoutShiftingLeft(
            List list1, String string, char[] ba, ChangedMethodDescriptor changedMethodDescriptor, boolean bl, boolean bl1
    ) {
        int bb = list1.size();
        int bc = ba.length;
        int[] bd = new int[bc];
        if (!bl1) {
            bd = changedMethodDescriptor.getParamIndices();

            for (int i = 0; i < bc; i++) {
                if (bd[i] != i) {
                    bd[i]--;

                    for (int j = i - 1; j >= 0; j += -1) {
                        bd[j] = bd[j + 1] - 1;
                    }
                    break;
                }
            }
        } else {
            for (int i = 0; i < bc; i++) {
                bd[i] = bb + i - (bl ? 1 : 0);
            }
        }

        return new ChangedMethodDescriptor(list1, string, ba, bd);
    }

    public ChangedMethodDescriptor nextLayout(boolean bl, List list1, String string, char[] ba, ChangedMethodDescriptor changedMethodDescriptor, boolean bl1) {
        boolean bl2 = changedMethodDescriptor == null;
        boolean bl3;
        if (!HiddenOptionFlags.ALWAYS_REGENERATE_PARAM_LAYOUT && !bl) {
            bl3 = isFullyShiftedLeft(changedMethodDescriptor);
        } else {
            bl3 = isFullyShiftedRight(list1, changedMethodDescriptor, bl1);
        }

        if (bl3) {
            return null;
        }

        ChangedMethodDescriptor changedMethodDescriptor1;
        if (!HiddenOptionFlags.ALWAYS_REGENERATE_PARAM_LAYOUT && !bl) {
            changedMethodDescriptor1 = nextLayoutShiftingLeft(list1, string, ba, changedMethodDescriptor, bl1, bl2);
        } else {
            changedMethodDescriptor1 = nextLayoutShiftingRight(list1, string, ba, changedMethodDescriptor, bl1, bl2);
        }

        return changedMethodDescriptor1;
    }

    public static boolean isFullyShiftedRight(List list1, ChangedMethodDescriptor changedMethodDescriptor, boolean bl) {
        return changedMethodDescriptor != null ? changedMethodDescriptor.getFirstParam().getIndex() >= list1.size() - (bl ? 1 : 0) : false;
    }

    public static ChangedMethodDescriptor nextLayoutShiftingRight(
            List list1, String string, char[] ba, ChangedMethodDescriptor changedMethodDescriptor, boolean bl, boolean bl1
    ) {
        int bb = list1.size();
        int bc = ba.length;
        int[] bd = new int[bc];
        if (!bl1) {
            bd = changedMethodDescriptor.getParamIndices();
            int be = bb + bc;

            for (int i = bc - 1; i >= 0; i += -1) {
                if (bd[i] < be - bc + i - (bl ? 1 : 0)) {
                    bd[i]++;

                    for (int j = i + 1; j < bc; j++) {
                        bd[j] = bd[j - 1] + 1;
                    }
                    break;
                }
            }
        } else {
            int bh = 0;

            while (bh < bc) {
                bd[bh] = bh++;
            }
        }

        return new ChangedMethodDescriptor(list1, string, ba, bd);
    }

    public ChangedMethodDescriptor nextLayoutWithTypes(
            boolean bl, List list1, String string, MutableInt mutableInt, ChangedMethodDescriptor changedMethodDescriptor, boolean bl1, char[][] ba
    ) {
        char[] bb = ba[mutableInt.getValue()];
        boolean bl2 = changedMethodDescriptor == null;
        boolean bl3;
        if (!HiddenOptionFlags.ALWAYS_REGENERATE_PARAM_LAYOUT && !bl) {
            bl3 = isFullyShiftedLeft(changedMethodDescriptor);
        } else {
            bl3 = isFullyShiftedRight(list1, changedMethodDescriptor, bl1);
        }

        boolean bl4;
        if (bl3) {
            mutableInt.incrementAndGet();
            if (mutableInt.getValue() >= ba.length) {
                return null;
            }

            bb = ba[mutableInt.getValue()];
            bl2 = true;
            bl4 = HiddenOptionFlags.ALWAYS_REGENERATE_PARAM_LAYOUT;
        } else {
            bl4 = HiddenOptionFlags.ALWAYS_REGENERATE_PARAM_LAYOUT;
        }

        ChangedMethodDescriptor changedMethodDescriptor1;
        if (!bl4 && !bl) {
            changedMethodDescriptor1 = nextLayoutShiftingLeft(list1, string, bb, changedMethodDescriptor, bl1, bl2);
        } else {
            changedMethodDescriptor1 = nextLayoutShiftingRight(list1, string, bb, changedMethodDescriptor, bl1, bl2);
        }

        return changedMethodDescriptor1;
    }
}
