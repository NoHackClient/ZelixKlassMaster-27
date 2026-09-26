package com.zelix.klassmaster.classfile;

import com.zelix.klassmaster.classfile.attribute.ConstantValueAttribute;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.util.ArrayEnumeration;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

public class SerialVersionUidCalculator implements Serializable {
    public static final String l = System.getProperty("SERIAL_VER_ALGORITHM", "1.5");
    public static final Comparator U = new FieldNameComparator();
    public static final Comparator g = new MethodSignatureComparator();

    public static long computeSerialVersionUid(ProgramClass programClass1, ClassMemberLookup classMemberLookup1) throws ZkmException, IOException {
        String string = programClass1.getClassName();
        if (!classMemberLookup1.isSubclass(string, "java/lang/Enum")
                && !classMemberLookup1.isSubclass(string, "java/lang/reflect/Proxy")
                && !string.equals("java/lang/Enum")
                && (
                classMemberLookup1.implementsInterface(string, "java/io/Serializable")
                        || classMemberLookup1.implementsInterface(string, "java/io/Externalizable")
                        || string.equals("java/io/Externalizable")
                        || string.equals("java/io/Serializable")
        )) {
            FieldInfo fieldInfo = classMemberLookup1.findField(string, "serialVersionUID", "J");
            if (fieldInfo != null) {
                ConstantValueAttribute constantValueAttribute = fieldInfo.getConstantValueAttribute();
                if (constantValueAttribute != null) {
                    return constantValueAttribute.getLongValue();
                }
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
            long ba = 0L;

            try {
                MessageDigest messageDigest1 = MessageDigest.getInstance("SHA-1");
                DigestOutputStream digestOutputStream = new DigestOutputStream(byteArrayOutputStream, messageDigest1);
                DataOutputStream dataOutputStream = new DataOutputStream(digestOutputStream);
                dataOutputStream.writeUTF(programClass1.getDottedClassName());
                int innerClassAccessFlags = programClass1.getInnerClassAccessFlags();
                String string2;
                if (innerClassAccessFlags == -1) {
                    innerClassAccessFlags = programClass1.getAccessFlags();
                    string2 = l;
                } else {
                    string2 = l;
                }

                if (!string2.equals("1.4")) {
                    innerClassAccessFlags &= 1553;
                }

                if (programClass1.isInterface()) {
                    if (programClass1.getMethodCount() > 0) {
                        innerClassAccessFlags |= 1024;
                        dataOutputStream.writeInt(innerClassAccessFlags);
                    } else {
                        innerClassAccessFlags &= -1025;
                        dataOutputStream.writeInt(innerClassAccessFlags);
                    }
                } else {
                    dataOutputStream.writeInt(innerClassAccessFlags);
                }

                String[] strings = programClass1.getInterfaceNames();
                Arrays.sort(strings);

                for (int i = 0; i < strings.length; i++) {
                    String string1 = strings[i].replace('/', '.');
                    dataOutputStream.writeUTF(string1);
                }

                ArrayEnumeration arrayEnumeration = programClass1.enumerateFields();
                arrayEnumeration.sortElements(U);

                for (boolean bl = arrayEnumeration.hasMoreElements(); bl; bl = arrayEnumeration.hasMoreElements()) {
                    FieldInfo fieldInfo1 = (FieldInfo) arrayEnumeration.nextElement();
                    if (!fieldInfo1.isStrictlyPrivate() || !fieldInfo1.isStatic() && !fieldInfo1.isTransient()) {
                        dataOutputStream.writeUTF(fieldInfo1.getSourceName());
                        int accessFlags = fieldInfo1.getAccessFlags();
                        if (!l.equals("1.4")) {
                            accessFlags &= 223;
                            dataOutputStream.writeInt(accessFlags);
                        } else {
                            dataOutputStream.writeInt(accessFlags);
                        }

                        dataOutputStream.writeUTF(fieldInfo1.getDescriptor());
                    }
                }

                if (classMemberLookup1.declaresMethod(MethodSignature.STATIC_INITIALIZER, programClass1)) {
                    dataOutputStream.writeUTF("<clinit>");
                    dataOutputStream.writeInt(8);
                    dataOutputStream.writeUTF("()V");
                }

                ArrayEnumeration arrayEnumeration1 = programClass1.enumerateMethods();
                arrayEnumeration1.sortElements(g);
                Vector vector1 = new Vector();
                Vector vector = new Vector();

                for (boolean bl1 = arrayEnumeration1.hasMoreElements(); bl1; bl1 = arrayEnumeration1.hasMoreElements()) {
                    MethodInfo methodInfo1 = (MethodInfo) arrayEnumeration1.nextElement();
                    if (!methodInfo1.isStrictlyPrivate()) {
                        if (methodInfo1.isConstructor()) {
                            vector1.addElement(methodInfo1);
                        } else if (!methodInfo1.isStaticInitializer()) {
                            vector.addElement(methodInfo1);
                        }
                    }
                }

                for (int i = 0; i < vector1.size(); i++) {
                    MethodInfo methodInfo2 = (MethodInfo) vector1.elementAt(i);
                    dataOutputStream.writeUTF(methodInfo2.getJvmName());
                    int be = methodInfo2.getAccessFlags();
                    if (!l.equals("1.4")) {
                        be &= 3391;
                        dataOutputStream.writeInt(be);
                    } else {
                        dataOutputStream.writeInt(be);
                    }

                    dataOutputStream.writeUTF(methodInfo2.getDescriptor().replace('/', '.'));
                }

                for (int i = 0; i < vector.size(); i++) {
                    MethodInfo methodInfo3 = (MethodInfo) vector.elementAt(i);
                    dataOutputStream.writeUTF(methodInfo3.getJvmName());
                    int bk = methodInfo3.getAccessFlags();
                    if (!l.equals("1.4")) {
                        bk &= 3391;
                        dataOutputStream.writeInt(bk);
                    } else {
                        dataOutputStream.writeInt(bk);
                    }

                    dataOutputStream.writeUTF(methodInfo3.getDescriptor().replace('/', '.'));
                }

                dataOutputStream.flush();
                byte[] bi = messageDigest1.digest();
                int bj = 0;
                int bm = 0;
                byte bn = 8;

                while (true) {
                    int bf = bi.length;
                    if (bm >= Math.min(8, bf)) {
                        break;
                    }

                    ba += (long) (bi[bj] & 0xFF) << bj * 8;
                    bm = ++bj;
                    bn = 8;
                }
            } catch (IOException iOException) {
                ba = 0L;
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                throw new ZkmProcessingException(noSuchAlgorithmException.toString());
            }

            return ba;
        } else {
            return 0L;
        }
    }

    private SerialVersionUidCalculator() {
    }
}
