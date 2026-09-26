package com.zelix.klassmaster.util;

import com.zelix.klassmaster.archive.TempFileManager;
import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;
import java.util.TimeZone;
import java.util.Map.Entry;

public class ZkmUtils {
    private static double hashLoadFactor = 0.74;
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final int[] PRIME_CAPACITIES = new int[]{
            5,
            7,
            11,
            13,
            17,
            19,
            23,
            29,
            31,
            37,
            41,
            43,
            47,
            53,
            59,
            61,
            67,
            71,
            73,
            79,
            83,
            89,
            97,
            101,
            107,
            113,
            127,
            131,
            137,
            149,
            157,
            163,
            167,
            173,
            179,
            191,
            197,
            211,
            223,
            229,
            239,
            251,
            257,
            263,
            269,
            277,
            283,
            293,
            307,
            317,
            331,
            347,
            359,
            367,
            379,
            389,
            397,
            409,
            419,
            431,
            443,
            457,
            467,
            479,
            491,
            503,
            521,
            541,
            557,
            569,
            587,
            599,
            613,
            631,
            647,
            661,
            677,
            691,
            709,
            727,
            743,
            761,
            787,
            809,
            827,
            853,
            877,
            907,
            929,
            953,
            977,
            997,
            1019,
            1049,
            1087,
            1109,
            1151,
            1181,
            1213,
            1249,
            1277,
            1303,
            1361,
            1399,
            1427,
            1459,
            1489,
            1523,
            1559,
            1597,
            1637,
            1693,
            1733,
            1777,
            1823,
            1861,
            1901,
            1949,
            1993,
            2039,
            2081,
            2129,
            2179,
            2237,
            2287,
            2333,
            2381,
            2437,
            2503,
            2557,
            2609,
            2663,
            2719,
            2777,
            2833,
            2897,
            2957,
            3019,
            3083,
            3163,
            3229,
            3299,
            3371,
            3449,
            3527,
            3607,
            3691,
            3767,
            3847,
            3929,
            4013,
            4099,
            4201,
            4289,
            4391,
            4481,
            4583,
            4679,
            4783,
            4889,
            4987,
            5087,
            5189,
            5297,
            5407,
            5519,
            5639,
            5779,
            5897,
            6029,
            6151,
            6277,
            6421,
            6551,
            6689,
            6823,
            6961,
            7103,
            7247,
            7393,
            7541,
            7699,
            7853,
            8011,
            8179,
            8353,
            8521,
            8693,
            8867,
            9049,
            9239,
            9431,
            9623,
            9817,
            10037,
            10243,
            10453,
            10663,
            10883,
            11113,
            11351,
            11579,
            11813,
            12071,
            12323,
            12577,
            12829,
            13093,
            13367,
            13649,
            13931,
            14221,
            14519,
            14813,
            15121,
            15427,
            15737,
            16057,
            16381,
            16729,
            17077,
            17419,
            17783,
            18143,
            18517,
            18899,
            19289,
            19681,
            20089,
            20507,
            20921,
            21341,
            21773,
            22229,
            22679,
            23143,
            23609,
            24083,
            24571,
            25073,
            25577,
            26099,
            26627,
            27179,
            27733,
            28289,
            28859,
            29437,
            30029,
            30631,
            31247,
            31873,
            32531,
            33191,
            33857,
            34537,
            35251,
            35963,
            36683,
            37423,
            38177,
            38953,
            39733,
            40529,
            41341,
            42169,
            43013,
            43889,
            44771,
            45667,
            46589,
            47521,
            48473,
            49451,
            50441,
            51461,
            52501,
            53569,
            54647,
            55763,
            56891,
            58031,
            59197,
            60383,
            61603,
            62851,
            64109,
            65393,
            66701,
            68041,
            69403,
            70793,
            72211,
            73673,
            75149,
            76667,
            78203,
            79769,
            81371,
            83003,
            84673,
            86369,
            88117,
            89891,
            91691,
            93529,
            95401,
            97327,
            99277,
            101267,
            101273,
            102019,
            102829,
            103787,
            104593,
            105397,
            106279,
            107053,
            107981,
            108821,
            109579,
            110503,
            111317,
            112153,
            113023,
            113843,
            114713,
            115597,
            116371,
            117239,
            118037,
            118907,
            119783,
            120647,
            121379,
            122209,
            123059,
            123887,
            124753,
            125621,
            126443,
            127331,
            128201,
            128981,
            129769,
            130633,
            131543,
            132409,
            133213,
            134089,
            135007,
            135757,
            136547,
            137393,
            138283,
            139177,
            139991,
            140813,
            141667,
            142537,
            143477,
            144341,
            145253,
            146051,
            146933,
            147761,
            148691,
            149423,
            150247,
            151157,
            151901,
            152783,
            153611,
            154523,
            155383,
            156253,
            157177,
            157999,
            158923,
            159773,
            160649,
            161521,
            162451,
            163243,
            164113,
            165047,
            165931,
            166847,
            167641,
            168631,
            169553,
            170351,
            171179,
            172079,
            172871,
            173807,
            174653,
            175673,
            176417,
            177257,
            178223,
            179021,
            179807,
            180623,
            181711,
            182489,
            183349,
            184199,
            185077,
            185893,
            186727,
            187559,
            188519,
            189391,
            190261,
            191137,
            192029,
            192853,
            193727,
            194681,
            195493,
            196429,
            197293,
            198139,
            198971,
            199889,
            200867,
            201757,
            202621,
            203459,
            204431,
            205327,
            206191,
            207061,
            207877,
            208697,
            209569,
            210347,
            211231,
            212131,
            213133,
            214007,
            214817,
            215833,
            216779,
            217643,
            218579,
            219433,
            220217,
            221093,
            222007,
            222919,
            223757,
            224669,
            225523,
            226433,
            227377,
            228281,
            229081,
            229837,
            230719,
            231589,
            232567,
            233549,
            234463,
            235307,
            236329,
            237217,
            238171,
            239027,
            239947,
            240853,
            241679,
            242521,
            243479,
            244367,
            245209,
            246131,
            246937,
            247873,
            248701,
            249539,
            250619,
            251417,
            252283,
            253307,
            254053,
            255023,
            255869,
            256801,
            257783,
            258617,
            259547,
            260483,
            261431,
            262349,
            263267,
            264113,
            265021,
            265921,
            266837,
            267601,
            268519,
            269341,
            270269,
            271127,
            272039,
            272933,
            273941,
            274847,
            275699,
            276557,
            277513,
            278479,
            279397,
            280297,
            281159,
            281959,
            282889,
            283859,
            284723,
            285611,
            286553,
            287537,
            288559,
            289369,
            290317,
            291167,
            292091,
            293071,
            294053,
            294923,
            295879,
            296753,
            297719,
            298681,
            299623
    };

    public static long xorLongWithKey(long ba, String string) {
        char[] bb = string.toCharArray();
        long bc = 0L;

        for (int i = 0; i < 8; i++) {
            char be;
            if (i < bb.length - 1) {
                be = bb[i];
            } else {
                be = bb[i % bb.length];
            }

            if (i == 0) {
                bc |= be & 7;
            } else {
                bc |= be & 15;
            }

            bc <<= 4;
        }

        return ba ^ bc;
    }

    public static void diffSortedIntArrays(int[] ba, int[] bb, ObservableHolder observableHolder, ObservableHolder observableHolder1) throws ZkmException, IOException {
        int bc;
        if (ba.length > bb.length) {
            bc = ba.length;
        } else {
            bc = bb.length;
        }

        int[] bd = new int[bc];
        int[] be = new int[bc];
        int bf = 0;
        int bg = 0;
        int bh = 0;
        int bi = 0;

        while (bh < ba.length && bi < bb.length) {
            if (ba[bh] == bb[bi]) {
                bh++;
                bi++;
            } else if (ba[bh] > bb[bi]) {
                bd[bf++] = bb[bi];
                bi++;
            } else {
                be[bg++] = ba[bh];
                bh++;
            }
        }

        while (bh < ba.length) {
            be[bg++] = ba[bh];
            bh++;
        }

        while (bi < bb.length) {
            bd[bf++] = bb[bi];
            bi++;
        }

        int[] bj = new int[bg];
        System.arraycopy(be, 0, bj, 0, bg);
        observableHolder1.setValue(bj);
        int[] bk = new int[bf];
        System.arraycopy(bd, 0, bk, 0, bf);
        observableHolder.setValue(bk);
    }

    public static String xorString(String string, String string1) {
        char[] ba = string.toCharArray();
        char[] bb = string1.toCharArray();
        StringBuilder stringBuilder = new StringBuilder(ba.length);

        for (int i = 0; i < ba.length; i++) {
            char bd;
            if (i < bb.length - 1) {
                bd = bb[i];
            } else {
                bd = bb[i % bb.length];
            }

            stringBuilder.append((char) ((byte) ba[i] ^ (byte) bd));
        }

        return stringBuilder.toString();
    }

    public static TwoKeyMap copyTwoKeyMap(TwoKeyMap twoKeyMap) {
        return twoKeyMap.deepCopy();
    }

    public static void listProperties(Properties properties1, PrintWriter printWriter) {
        listProperties("", properties1, printWriter);
    }

    public static String getHashedSystemProperty(String string) {
        return getHashedSystemProperty(string, (String) null);
    }

    public static String saltedSha512Hex(String string) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(string);
            stringBuilder.append("xrcyorpnviag");
            return digestToHex(stringBuilder.toString(), "SHA-512");
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            String[] strings1 = new String[]{"(A)", noSuchAlgorithmException.toString()};
            ZkmAssert.assertTrue(false, strings1);
            return "";
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            String[] strings = new String[]{"(A)", unsupportedEncodingException.toString()};
            ZkmAssert.assertTrue(false, strings);
            return "";
        }
    }

    public static String escapeAndTruncate(String string, int ba) {
        return string.length() > ba ? escapeJavaString(string.substring(0, ba)) + " ...<" + (string.length() - ba) + " CHARS>" : escapeJavaString(string);
    }

    public static String slashesToDots(String string) throws IOException {
        return string.replace('/', '.');
    }

    public static String concatStrings(String[] strings) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String string : strings) {
            stringBuilder.append(string);
        }

        return stringBuilder.toString();
    }

    public static String digestToHex(String string, String string1) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest messageDigest1 = MessageDigest.getInstance(string1);
        int bc = string.length();
        messageDigest1.update(string.getBytes(), 0, bc);
        StringBuilder stringBuilder = new StringBuilder();
        byte[] ba = messageDigest1.digest();

        for (int i = 0; i < ba.length; i++) {
            String string2 = Integer.toHexString(ba[i] & 255);
            if (string2.length() == 1) {
                stringBuilder.append('0');
            }

            stringBuilder.append(string2);
        }

        return stringBuilder.toString();
    }

    public static long getRandomSeed(int ba) {
        if (HiddenOptionFlags.RANDOM_SEED != null) {
            try {
                long bc = Long.parseLong(HiddenOptionFlags.RANDOM_SEED);
                if (bc > 0L) {
                }

                long bb;
                if (bc < 0L) {
                    bb = bc + ba;
                } else {
                    bb = bc - ba;
                }

                return bb;
            } catch (Throwable throwable) {
            }
        }

        return generateRandomLong();
    }

    public static NamedSet createNamedSet(Collection collection1) {
        return HiddenOptionFlags.DETERMINISTIC_OUTPUT ? new NamedLinkedHashSet(collection1) : new NamedHashSet(collection1);
    }

    public static HashMap createHashMap() {
        return HiddenOptionFlags.DETERMINISTIC_OUTPUT ? new LinkedHashMap() : new HashMap();
    }

    public static int getHiddenOptionIndex() {
        if (HiddenOptionFlags.CANARY_PROPERTY_01 != null) {
            return 1;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_02) {
            return 2;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_03) {
            return 3;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_04 != null) {
            return 4;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_05) {
            return 5;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_06) {
            return 6;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_07 != null) {
            return 7;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_08) {
            return 8;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_09) {
            return 9;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_10 != null) {
            return 10;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_11) {
            return 11;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_12) {
            return 12;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_13 != null) {
            return 13;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_14) {
            return 14;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_15) {
            return 15;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_16 != null) {
            return 16;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_17) {
            return 17;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_18) {
            return 18;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_19 != null) {
            return 19;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_20) {
            return 20;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_21) {
            return 21;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_22 != null) {
            return 22;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_23) {
            return 23;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_24) {
            return 24;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_25 != null) {
            return 25;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_26) {
            return 26;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_27) {
            return 27;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_28 != null) {
            return 28;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_29) {
            return 29;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_30) {
            return 30;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_31 != null) {
            return 31;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_32) {
            return 32;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_33) {
            return 33;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_34 != null) {
            return 34;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_35) {
            return 35;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_36) {
            return 36;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_37 != null) {
            return 37;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_38) {
            return 38;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_39) {
            return 39;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_40 != null) {
            return 40;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_41) {
            return 41;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_42) {
            return 42;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_43 != null) {
            return 43;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_44) {
            return 44;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_45) {
            return 45;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_46 != null) {
            return 46;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_47) {
            return 47;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_48) {
            return 48;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_49 != null) {
            return 49;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_50) {
            return 50;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_51) {
            return 51;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_52 != null) {
            return 52;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_53) {
            return 53;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_54) {
            return 54;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_55 != null) {
            return 55;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_56) {
            return 56;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_57) {
            return 57;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_58 != null) {
            return 58;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_59) {
            return 59;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_60) {
            return 60;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_61 != null) {
            return 61;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_62) {
            return 62;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_63) {
            return 63;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_64 != null) {
            return 64;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_65) {
            return 65;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_66) {
            return 66;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_67 != null) {
            return 67;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_68) {
            return 68;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_69) {
            return 69;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_70 != null) {
            return 70;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_71) {
            return 71;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_72) {
            return 72;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_73 != null) {
            return 73;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_74) {
            return 74;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_75) {
            return 75;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_76 != null) {
            return 76;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_77) {
            return 77;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_78) {
            return 78;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_79 != null) {
            return 79;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_80) {
            return 80;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_81) {
            return 81;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_82 != null) {
            return 82;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_83) {
            return 83;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_84) {
            return 84;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_85 != null) {
            return 85;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_86) {
            return 86;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_87) {
            return 87;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_88 != null) {
            return 88;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_89) {
            return 89;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_90) {
            return 90;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_91 != null) {
            return 91;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_92) {
            return 92;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_93) {
            return 93;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_94 != null) {
            return 94;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_95) {
            return 95;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_96) {
            return 96;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_97 != null) {
            return 97;
        } else if (HiddenOptionFlags.CANARY_PROPERTY_98) {
            return 98;
        } else {
            return HiddenOptionFlags.CANARY_PROPERTY_99 ? 99 : 0;
        }
    }

    public static List enumerationToList(Enumeration enumeration) {
        ArrayList arrayList = new ArrayList();

        while (enumeration.hasMoreElements()) {
            arrayList.add(enumeration.nextElement());
        }

        return arrayList;
    }

    public static HashSet copyHashSet(HashSet hashSet) {
        return (HashSet) hashSet.clone();
    }

    public static boolean mergeUniqueInto(List list1, List list2) {
        boolean bl = false;
        HashSet hashSet = createHashSet(getPrimeCapacity(list1.size() + list2.size()));
        hashSet.addAll(list2);

        for (Object object : list1) {
            if (hashSet.add(object)) {
                list2.add(object);
            } else {
                bl = true;
            }
        }

        return bl;
    }

    public static HashMap createHashMap(int ba, float bb) {
        return HiddenOptionFlags.DETERMINISTIC_OUTPUT ? new LinkedHashMap(ba, bb) : new HashMap(ba, bb);
    }

    public static String toUnicodeEscape(int ba) {
        String string = Integer.toHexString(ba);
        return "\\u" + ZkmStringUtils.pad(string, 82, 4, 48);
    }

    public static HashSet createHashSet() {
        return HiddenOptionFlags.DETERMINISTIC_OUTPUT ? new LinkedHashSet() : new HashSet();
    }

    public static String escapeJavaString(String string) {
        char[] ba = string.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();

        for (char bb : ba) {
            char bc = bb;
            switch (bc) {
                case '\n':
                    stringBuilder.append("\\n");
                    break;
                case '\f':
                    stringBuilder.append("\\f");
                    break;
                case '\r':
                    stringBuilder.append("\\r");
                    break;
                case '"':
                    stringBuilder.append("\\\"");
                    break;
                case '\\':
                    stringBuilder.append("\\\\");
                    break;
                default:
                    if (bc >= ' ' && bc <= '~') {
                        stringBuilder.append(bb);
                    } else {
                        stringBuilder.append(toUnicodeEscape(bc));
                    }
            }
        }

        return stringBuilder.toString();
    }

    public static Object twoKeyMapOrSelf(Object object, Object object1, TwoKeyMap twoKeyMap) {
        if (twoKeyMap != null) {
            Object object2 = twoKeyMap.getValue(object, object1);
            if (object2 != null) {
                return object2;
            }
        }

        return object1;
    }

    public static int toUnsignedShort(int ba) {
        return ba & 65535;
    }

    public static void swapChars(char[] ba, int bb, int bc) {
        char bd = ba[bb];
        ba[bb] = ba[bc];
        ba[bc] = bd;
    }

    public static String getBracketedTimestamp() {
        return "[" + getTimestamp() + "]";
    }

    public static void swapElements(Object[] objects, int ba, int bb) {
        Object object = objects[ba];
        objects[ba] = objects[bb];
        objects[bb] = object;
    }

    public static boolean allUnique(Object[] objects) {
        HashSet hashSet = createHashSet(getPrimeCapacity(objects.length));

        for (Object object : objects) {
            if (!hashSet.add(object)) {
                return false;
            }
        }

        return true;
    }

    public static byte[] parseByteString(String string) {
        byte be = 36;
        byte bf = 36;
        ZkmAssert.assertTrue(true, new String[]{"MAX_RADIX is too small : 36 < " + 36});
        int ba = string.length();
        byte[] bb = new byte[ba / 2];
        int bc = 0;

        for (int i = 0; i < ba; i += 2) {
            bb[bc++] = (byte) Integer.parseInt(string.substring(i, i + 2), 36);
        }

        return bb;
    }

    public static String unescapeJavaString(String string) {
        StringBuilder stringBuilder = new StringBuilder(string.length());

        for (int i = 0; i < string.length(); i++) {
            char bb = string.charAt(i);
            if (bb == '\\' && i + 1 < string.length()) {
                switch (string.charAt(i + 1)) {
                    case '"':
                        stringBuilder.append('"');
                        i++;
                        break;
                    case '\'':
                        stringBuilder.append('\'');
                        i++;
                        break;
                    case '\\':
                        stringBuilder.append('\\');
                        i++;
                        break;
                    case 'n':
                        stringBuilder.append('\n');
                        i++;
                        break;
                    case 'r':
                        stringBuilder.append('\r');
                        i++;
                        break;
                    case 't':
                        stringBuilder.append('\t');
                        i++;
                        break;
                    case 'u':
                        if (i + 5 < string.length()) {
                            String string1 = string.substring(i + 2, i + 6);

                            try {
                                int bc = Integer.parseInt(string1, 16);
                                stringBuilder.append((char) bc);
                                i += 5;
                                break;
                            } catch (NumberFormatException numberFormatException) {
                            }
                        }

                        stringBuilder.append(bb);
                        break;
                    default:
                        stringBuilder.append(bb);
                }
            } else {
                stringBuilder.append(bb);
            }
        }

        return stringBuilder.toString();
    }

    public static String toHexArrayLiteral(byte[] ba) {
        if (ba != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{");

            for (int i = 0; i < ba.length; i++) {
                stringBuilder.append(byteToHex(ba[i]));
                if (i < ba.length - 1) {
                    stringBuilder.append(',');
                }
            }

            return stringBuilder.toString() + "}";
        } else {
            return "{}";
        }
    }

    public static String toQuotedListString(Collection collection1) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("'");
        int ba = collection1.size();
        int bb = 0;

        for (Iterator iterator = collection1.iterator(); iterator.hasNext(); bb++) {
            String string = (String) iterator.next();
            stringBuilder.append(string);
            if (bb < ba - 2) {
                stringBuilder.append("', '");
            } else if (bb < ba - 1) {
                stringBuilder.append("' and '");
            }
        }

        stringBuilder.append("'");
        return stringBuilder.toString();
    }

    public static Random createRandom(int ba) {
        long bb = getRandomSeed(ba);
        Random random1;
        if (HiddenOptionFlags.DETERMINISTIC_OUTPUT) {
            random1 = new Random(bb);
        } else {
            random1 = HiddenOptionFlags.USE_PLAIN_RANDOM ? new Random() : new SecureRandom();
            random1.setSeed(bb);
        }

        return random1;
    }

    public static Random createSeededRandom(long ba) {
        return new Random(ba);
    }

    public static String stackTraceToString(Throwable throwable) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        return byteArrayOutputStream.toString();
    }

    public static Object mapOrSelf(Object object, Map map1) {
        if (map1 != null) {
            Object object1 = map1.get(object);
            if (object1 != null) {
                return object1;
            }
        }

        return object;
    }

    public static ListMultimap invertMultiMap(MultiMap multiMap) {
        SetMultiMap setMultiMap = new SetMultiMap();
        Iterator iterator = multiMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Object object = entry.getKey();

            for (Object object1 : (List) entry.getValue()) {
                setMultiMap.addValue(object1, object);
            }
        }

        return setMultiMap.toListMultimap();
    }

    public static long generateRandomLong() {
        String.valueOf(System.nanoTime()).getBytes();
        return HiddenOptionFlags.USE_PLAIN_RANDOM
                ? new Random(System.nanoTime()).nextLong()
                : new SecureRandom(String.valueOf(System.nanoTime()).getBytes()).nextLong();
    }

    public static String createCommentBox(boolean bl, String[] strings) {
        String string;
        String string1;
        byte ba;
        String string2;
        String string3;
        if (bl) {
            string = "/**";
            string1 = "**/";
            ba = 42;
            string2 = "/* ";
            string3 = " */";
        } else {
            string = "///";
            string1 = "///";
            ba = 47;
            string2 = "// ";
            string3 = " //";
        }

        int bb = 0;
        int bc = 0;

        for (String string4 : strings) {
            int bd = string4.length();
            bb += bd;
            if (bd > bc) {
                bc = bd;
            }
        }

        StringBuilder stringBuilder = new StringBuilder(bb + strings.length * 6 + (bc + 6) * 2);
        stringBuilder.append(string + ZkmStringUtils.pad("", 76, bc, ba) + string1);
        stringBuilder.append(LINE_SEPARATOR);

        for (String string5 : strings) {
            stringBuilder.append(string2 + ZkmStringUtils.pad(string5, 76, bc, 32) + string3);
            stringBuilder.append(LINE_SEPARATOR);
        }

        stringBuilder.append(string + ZkmStringUtils.pad("", 76, bc, ba) + string1);
        stringBuilder.append(LINE_SEPARATOR);
        return stringBuilder.toString();
    }

    public static NamedSet copyNamedSet(NamedSet namedSet) {
        return (NamedSet) namedSet.clone();
    }

    public static String xorStringWithKey(String string, String string1) {
        char[] ba = string.toCharArray();
        char[] bb = string1.toCharArray();
        StringBuilder stringBuilder = new StringBuilder(ba.length);

        for (int i = 0; i < ba.length; i++) {
            char bd = ba[i];
            char be;
            if (i < bb.length - 1) {
                be = bb[i];
            } else {
                be = bb[i % bb.length];
            }

            stringBuilder.append((char) ((byte) bd ^ (byte) be));
        }

        return stringBuilder.toString();
    }

    public static String createCommentBox(String[] strings) {
        return createCommentBox(true, strings);
    }

    public static String getHashedSystemProperty(String string, String string1) {
        Enumeration<?> enumeration = System.getProperties().propertyNames();

        while (enumeration.hasMoreElements()) {
            String string2 = (String) enumeration.nextElement();
            String string3 = saltedSha512Hex(string2);
            if (string.equals(string3)) {
                return System.getProperty(string2);
            }
        }

        return string1;
    }

    public static byte[] decodeCustomBaseBytes(String string, String string1) {
        int ba = string.length();
        byte[] bb = new byte[ba / 2];
        int bc = 0;

        for (int i = 0; i < ba; i += 2) {
            bb[bc++] = (byte) parseCustomBase(string.substring(i, i + 2), string1);
        }

        return bb;
    }

    public static Map invertMap(Map map1) {
        HashMap hashMap = createHashMap(getPrimeCapacity(map1.size()));
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            hashMap.put(entry.getValue(), entry.getKey());
        }

        return hashMap;
    }

    public static boolean isClassAvailable() {
        try {
            Class.forName("javax.crypto.Cipher");
            return true;
        } catch (ClassNotFoundException classNotFoundException) {
            return false;
        }
    }

    public static void swapInts(int[] ba, int bb, int bc) {
        int bd = ba[bb];
        ba[bb] = ba[bc];
        ba[bc] = bd;
    }

    public static String toHexDigit(int ba) {
        switch (ba) {
            case 0:
                return "0";
            case 1:
                return "1";
            case 2:
                return "2";
            case 3:
                return "3";
            case 4:
                return "4";
            case 5:
                return "5";
            case 6:
                return "6";
            case 7:
                return "7";
            case 8:
                return "8";
            case 9:
                return "9";
            case 10:
                return "A";
            case 11:
                return "B";
            case 12:
                return "C";
            case 13:
                return "D";
            case 14:
                return "E";
            case 15:
                return "F";
            default:
                throw new RuntimeException(ba + " can't be translated to a hex digit");
        }
    }

    public static String descriptorToJavaName(String string) {
        String string1 = string;
        int ba = 0;
        if (string1.startsWith("[")) {
            ba = ZkmStringUtils.countChar(string1, "[".charAt(0));
            string1 = string1.substring(ba);
        }

        if (string1.endsWith(";")) {
            string1 = string1.substring(1, string1.length() - 1);
        } else if (string1.length() == 1) {
            string1 = ConstantPoolEntry.getPrimitiveTypeName(string1);
        }

        string1 = string1.replace('/', '.');

        for (int i = 0; i < ba; i++) {
            string1 = string1 + "[]";
        }

        return string1;
    }

    public static void shuffleInts(int[] ba, int bb, Random random1) {
        for (int i = bb; i > 1; i += -1) {
            swapInts(ba, i - 1, random1.nextInt(i));
        }
    }

    private static void swapListElements(List list1, int ba, int bb) {
        Object object = list1.get(ba);
        list1.set(ba, list1.get(bb));
        list1.set(bb, object);
    }

    public static void shuffleArray(Object[] objects, Random random1) {
        for (int i = objects.length; i > 1; i += -1) {
            swapElements(objects, i - 1, random1.nextInt(i));
        }
    }

    public static void listProperties(String string, Properties properties1, PrintWriter printWriter) {
        printWriter.println("-- Listing " + string + " properties --");
        Enumeration<Object> enumeration = properties1.keys();

        while (enumeration.hasMoreElements()) {
            Object object = enumeration.nextElement();
            Object object1 = properties1.get(object);
            printWriter.println(object.toString() + " = " + object1.toString());
        }
    }

    public static long getCurrentThreadId() {
        Thread thread = Thread.currentThread();
        if (!JavaRuntimeVersion.isRuntimeAtLeastJava19()) {
            return thread.getId();
        }

        Lookup lookup = MethodHandles.lookup();
        Class[] class1 = new Class[0];
        MethodType methodType1 = MethodType.methodType(long.class, class1);
        Class<?> class2 = thread.getClass();

        while (!class2.getName().equals("java.lang.Thread")) {
            class2 = class2.getSuperclass();
        }

        try {
            methodType1.parameterArray();
            return (long) lookup.findVirtual(class2, "threadId", methodType1).invoke(thread);
        } catch (Throwable throwable) {
            if (JavaRuntimeVersion.isRuntimeAtLeastJava19()) {
            }

            return thread.getId();
        }
    }

    public static void shuffleChars(char[] ba) {
        shuffleChars(ba, createRandom(97));
    }

    public static String toQuotedArrayString(String[] strings) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("'");

        for (int i = 0; i < strings.length; i++) {
            stringBuilder.append(strings[i]);
            if (i < strings.length - 2) {
                stringBuilder.append("', '");
            } else if (i < strings.length - 1) {
                stringBuilder.append("' and '");
            }
        }

        stringBuilder.append("'");
        return stringBuilder.toString();
    }

    public static LinkedHashSet copyLinkedHashSet(LinkedHashSet linkedHashSet) {
        return (LinkedHashSet) linkedHashSet.clone();
    }

    public static String getTimestamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        TimeZone timeZone = TimeZone.getDefault();
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(new Date());
    }

    public static int getPrimeCapacity(int ba, double bb) {
        int bc = (int) (ba / bb);
        int bd = 0;
        int be = PRIME_CAPACITIES.length - 1;
        int bf = 0;

        while (bd <= be) {
            bf = (bd + be) / 2;
            if (bc < PRIME_CAPACITIES[bf]) {
                be = bf - 1;
            } else {
                if (bc <= PRIME_CAPACITIES[bf]) {
                    return PRIME_CAPACITIES[bf];
                }

                bd = bf + 1;
            }
        }

        if (PRIME_CAPACITIES[bf] > bc) {
            return PRIME_CAPACITIES[bf];
        } else {
            return bf + 1 < PRIME_CAPACITIES.length ? PRIME_CAPACITIES[bf + 1] : bc;
        }
    }

    public static String dotsToSlashes(String string) {
        return string.replace('.', '/');
    }

    public static String md5Hex(String string) {
        try {
            return digestToHex(string, "MD5");
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            String[] strings1 = new String[]{"(B)", noSuchAlgorithmException.toString()};
            ZkmAssert.assertTrue(false, strings1);
            return "";
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            String[] strings = new String[]{"(B)", unsupportedEncodingException.toString()};
            ZkmAssert.assertTrue(false, strings);
            return "";
        }
    }

    public static NamedSet createNamedSet(String string, Collection collection1) {
        return HiddenOptionFlags.DETERMINISTIC_OUTPUT ? new NamedLinkedHashSet(string, collection1) : new NamedHashSet(string, collection1);
    }

    public static HashMap copyToHashMap(Map map1) {
        return HiddenOptionFlags.DETERMINISTIC_OUTPUT ? new LinkedHashMap(map1) : new HashMap(map1);
    }

    public static byte[] toModifiedUtf8(String string) {
        char[] ba = string.toCharArray();
        int bb = 0;
        int bc = string.length();
        byte[] bd = new byte[bc * 3];

        for (int i = 0; i < bc; i++) {
            char bf = ba[i];
            if (bf == 0) {
                bd[bb++] = -64;
                bd[bb++] = -128;
            } else if (bf < 128) {
                bd[bb++] = (byte) bf;
            } else if (bf < 2048) {
                bd[bb++] = (byte) (192 | bf >>> 6 & 31);
                bd[bb++] = (byte) (128 | bf & 63);
            } else {
                bd[bb++] = (byte) (224 | bf >>> '\f' & 15);
                bd[bb++] = (byte) (128 | bf >>> 6 & 63);
                bd[bb++] = (byte) (128 | bf & 63);
            }
        }

        byte[] bg = new byte[bb];
        System.arraycopy(bd, 0, bg, 0, bb);
        return bg;
    }

    public static DisableableMap copyDisableableMap(DisableableMap disableableMap) {
        return new DisableableMap(disableableMap);
    }

    public static String toEnglishList(Collection collection1) {
        StringBuilder stringBuilder = new StringBuilder();
        int ba = collection1.size();
        int bb = 0;

        for (Iterator iterator = collection1.iterator(); iterator.hasNext(); bb++) {
            String string = (String) iterator.next();
            stringBuilder.append(string);
            if (bb < ba - 2) {
                stringBuilder.append(", ");
            } else if (bb < ba - 1) {
                stringBuilder.append(" and ");
            }
        }

        return stringBuilder.toString();
    }

    public static Map copyMap(Map map1) {
        return copyToHashMap(map1);
    }

    public static long computeStringHash(String string) {
        char[] ba = string.toCharArray();
        long bb = 0L;
        if (ba.length > 0) {
            for (int i = 0; i < ba.length; i++) {
                bb = 31L * bb + ba[i];
            }
        }

        return bb < 0L ? bb * -1L : bb;
    }

    public static Set toUniqueSet(Object[] objects) {
        HashSet hashSet = createHashSet(getPrimeCapacity(objects.length));

        for (Object object : objects) {
            if (!hashSet.add(object)) {
                throw new IllegalArgumentException("The specified array must contain unique objects.");
            }
        }

        return hashSet;
    }

    public static void shuffleInts(int[] ba, Random random1) {
        shuffleInts(ba, ba.length, random1);
    }

    public static CountingBag copyCountingBag(CountingBag countingBag) {
        return (CountingBag) countingBag.clone();
    }

    public static boolean intArraysEqual(int[] ba, int[] bb) {
        if (ba.length != bb.length) {
            return false;
        }

        for (int i = 0; i < ba.length; i++) {
            if (ba[i] != bb[i]) {
                return false;
            }
        }

        return true;
    }

    public static String toHexString(byte[] ba) {
        if (ba == null) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < ba.length; i++) {
            stringBuilder.append(byteToHex(ba[i]));
        }

        return stringBuilder.toString();
    }

    public static ArrayList copyArrayList(ArrayList arrayList) {
        return (ArrayList) arrayList.clone();
    }

    public static String byteToHex(int ba) {
        int bb = ByteConversionUtils.toUnsignedByte(ba);
        int bc = bb / 16;
        int bd = bb % 16;
        return toHexDigit(bc) + toHexDigit(bd);
    }

    public static long parseCustomBase(String string, String string1) {
        String string2 = string;
        long ba = 0L;
        string2 = new StringBuilder(string2).reverse().toString();
        int bb = string1.length();
        long bc = 1L;

        for (char bd : string2.toCharArray()) {
            ba += string1.indexOf(bd) * bc;
            bc *= bb;
        }

        return ba;
    }

    public static int getPrimeCapacity(int ba) {
        return getPrimeCapacity(ba, hashLoadFactor);
    }

    public static void shuffleList(List list1, Random random1) {
        int ba = list1.size();
        if (ba >= 10 && !(list1 instanceof RandomAccess)) {
            Object[] objects = list1.toArray();
            shuffleArray(objects, random1);
            ListIterator listIterator = list1.listIterator();

            for (int i = 0; i < objects.length; i++) {
                listIterator.next();
                listIterator.set(objects[i]);
            }
        } else {
            for (int i = ba; i > 1; i += -1) {
                swapListElements(list1, i - 1, random1.nextInt(i));
            }
        }
    }

    public static HashSet createHashSet(int ba) {
        return HiddenOptionFlags.DETERMINISTIC_OUTPUT ? new LinkedHashSet(ba) : new HashSet(ba);
    }

    public static String toCustomBase(long ba, char[] bb) {
        long bc = ba;
        if (bc < 0L) {
            throw new IllegalArgumentException("Must be positive " + bc);
        }

        if (bc == 0L) {
            return String.valueOf(bb[0]);
        }

        int bd = bb.length;
        StringBuilder stringBuilder = new StringBuilder();

        while (bc > 0L) {
            stringBuilder.append(bb[(int) (bc % bd)]);
            bc /= bd;
        }

        return stringBuilder.reverse().toString();
    }

    public static NamedSet createNamedSet() {
        return HiddenOptionFlags.DETERMINISTIC_OUTPUT ? new NamedLinkedHashSet() : new NamedHashSet();
    }

    public static String fromModifiedUtf8(byte[] ba) {
        int bb = 0;
        int bc = ba.length;
        char[] bd = new char[bc];

        for (int i = 0; i < bc; i++) {
            int bf = 255 & ba[i];
            if (bf < 192) {
                bd[bb++] = (char) bf;
            } else if (bf < 224) {
                char bg = (char) ((char) (bf & 31) << 6);
                byte bh = ba[++i];
                bg = (char) (bg | (char) (bh & 63));
                bd[bb++] = bg;
            } else if (i < bc - 2) {
                char bj = (char) ((char) (bf & 15) << '\f');
                byte bi = ba[++i];
                bj = (char) (bj | (char) (bi & 63) << 6);
                bi = ba[++i];
                bj = (char) (bj | (char) (bi & 63));
                bd[bb++] = bj;
            }
        }

        return new String(bd, 0, bb);
    }

    public static Object multiMapOrSelf(Object object, Object object1, ReadOnlyMultiMap readOnlyMultiMap) {
        if (readOnlyMultiMap != null) {
            Object object2 = readOnlyMultiMap.getValue(object, object1);
            if (object2 != null) {
                return object2;
            }
        }

        return object1;
    }

    public static HashMap createHashMap(int ba) {
        return HiddenOptionFlags.DETERMINISTIC_OUTPUT ? new LinkedHashMap(ba) : new HashMap(ba);
    }

    public static String decodeHiddenString() {
        return xorString(
                "\t\u000e\u001bOU\t\r\u0007\u001f\u0001$\u000e\u000b\u0016\u0010\u0019\u000b\u0012\u0002\u0004]B*\u0002\u0006\\</\u000b\u0016\u0017\u0011\u0018",
                "java/lang/Object"
        );
    }

    public static List describeTempFileNames(Collection collection1) {
        ArrayList arrayList = new ArrayList(collection1.size());
        Iterator iterator = collection1.iterator();

        while (iterator.hasNext()) {
            String string = (String) iterator.next();
            if (TempFileManager.isZkmTempFilePath(string)) {
                arrayList.add(TempFileManager.getOriginalName(string) + " (" + string + ")");
            } else {
                arrayList.add(string);
            }
        }

        return arrayList;
    }

    public static String getHashedMapOrSystemProperty(Map map1, String string, String string1) {
        Iterator iterator = map1.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String string2 = saltedSha512Hex((String) entry.getKey());
            if (string.equals(string2)) {
                return (String) entry.getValue();
            }
        }

        Enumeration<?> enumeration = System.getProperties().propertyNames();

        while (enumeration.hasMoreElements()) {
            String string3 = (String) enumeration.nextElement();
            String string4 = saltedSha512Hex(string3);
            if (string.equals(string4)) {
                return System.getProperty(string3);
            }
        }

        return string1;
    }

    public static Object[] copyArray(Object[] objects) {
        return objects.clone();
    }

    public static HashSet createHashSetFrom(Collection collection1) {
        return HiddenOptionFlags.DETERMINISTIC_OUTPUT ? new LinkedHashSet(collection1) : new HashSet(collection1);
    }

    public static void shuffleChars(char[] ba, Random random1) {
        for (int i = ba.length; i > 1; i += -1) {
            swapChars(ba, i - 1, random1.nextInt(i));
        }
    }

    public static NamedSet createNamedSetFromMap(String string, Map map1) {
        return HiddenOptionFlags.DETERMINISTIC_OUTPUT ? new NamedLinkedHashSet(string, map1) : new NamedHashSet(string, map1);
    }

    public static boolean arraysEqual(Object[] objects, Object[] objects1) {
        if (objects.length != objects1.length) {
            return false;
        }

        for (int i = 0; i < objects.length; i++) {
            if (!objects[i].equals(objects1[i])) {
                return false;
            }
        }

        return true;
    }

    public static String getHashedProperty(String string, String string1, Properties properties1) {
        if (properties1 == null) {
            return string1;
        }

        Enumeration<?> enumeration = properties1.propertyNames();

        while (enumeration.hasMoreElements()) {
            String string2 = (String) enumeration.nextElement();
            String string3 = saltedSha512Hex(string2);
            if (string.equals(string3)) {
                return properties1.getProperty(string2);
            }
        }

        return string1;
    }

    public static int getModifiedUtf8Length(String string) {
        return toModifiedUtf8(string).length;
    }

    public static <T extends Throwable> T sneakyThrow(Throwable throwable) throws T {
        throw (T) throwable;
    }

    public static <T extends Throwable> void mayThrow() throws T {
    }

    private ZkmUtils() {
    }
}
