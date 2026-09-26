package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.hierarchy.ClassMemberLookup;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.log.MessageReporter;

import java.io.IOException;
import java.util.List;

public class JavaFxHandlerSignatureFilter implements DescriptorMatcher {
    public final ClassMemberLookup classMemberLookup;
    public final MessageReporter messageReporter;

    public JavaFxHandlerSignatureFilter(ClassMemberLookup classMemberLookup1, MessageReporter messageReporter1) {
        this.classMemberLookup = classMemberLookup1;
        this.messageReporter = messageReporter1;
    }

    @Override
    public boolean matchesDescriptor(String string) throws ZkmException, IOException {
        List list1 = ConstantPoolEntry.getParameterTypes(string);
        int ba = list1.size();
        if (ba == 0) {
            return true;
        }

        if (ba == 1) {
            String string1 = (String) list1.get(0);
            if (string1.charAt(0) == '[') {
                return false;
            }

            if (string1.charAt(0) != 'L' || string1.charAt(string1.length() - 1) != ';') {
                return false;
            }

            String string2 = string1.substring(1, string1.length() - 1);

            try {
                if (!string2.equals("javafx/event/Event")
                        && !this.classMemberLookup.isSubclass(string2, "javafx/event/Event")
                        && !string2.equals("javafx/collections/ListChangeListener")
                        && !this.classMemberLookup.implementsInterface(string2, "javafx/collections/ListChangeListener")
                        && !string2.equals("javafx/collections/MapChangeListener")
                        && !this.classMemberLookup.implementsInterface(string2, "javafx/collections/MapChangeListener")
                        && !string2.equals("javafx/collections/SetChangeListener")
                        && !this.classMemberLookup.implementsInterface(string2, "javafx/collections/SetChangeListener")) {
                    return false;
                }

                return true;
            } catch (ClassFileLoadException classFileLoadException1) {
                this.messageReporter.reportFatalError("FATAL ERROR:", classFileLoadException1.getMessage());
            }
        } else if (ba == 3) {
            String string3 = (String) list1.get(0);
            if (string3.charAt(0) == '[') {
                return false;
            }

            if (!((String) list1.get(1)).equals(list1.get(2))) {
                return false;
            }

            if (string3.charAt(0) != 'L' || string3.charAt(string3.length() - 1) != ';') {
                return false;
            }

            String string4 = string3.substring(1, string3.length() - 1);

            try {
                if (!string4.equals("javafx/beans/value/ObservableValue")
                        && !this.classMemberLookup.implementsInterface(string4, "javafx/beans/value/ObservableValue")) {
                    return false;
                }

                return true;
            } catch (ClassFileLoadException classFileLoadException) {
                this.messageReporter.reportFatalError("FATAL ERROR:", classFileLoadException.getMessage());
            }
        }

        return false;
    }
}
