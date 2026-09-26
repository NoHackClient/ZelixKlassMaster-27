package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.util.ObjectTriple;

public class HelperClassTriple implements LookupClassOption {
    public ObjectTriple helperClasses;
    public final ObjectTriple helperClassNames;

    public String getFirstClassName() {
        return (String) this.helperClassNames.getFirst();
    }

    public HelperClassTriple(ObjectTriple objectTriple) {
        this.helperClassNames = objectTriple;
    }

    public void setHelperClass(ProgramClass programClass1, int ba) {
        switch (ba) {
            case 0:
                this.helperClasses = new ObjectTriple(programClass1, null, null);
                break;
            case 1:
                this.helperClasses.setSecond(programClass1);
                break;
            case 2:
                this.helperClasses.setThird(programClass1);
        }
    }

    public String getThirdClassName() {
        return (String) this.helperClassNames.getThird();
    }

    public int indexOfClassName(String string) {
        if (((String) this.helperClassNames.getFirst()).equals(string)) {
            return 0;
        } else {
            return ((String) this.helperClassNames.getSecond()).equals(string) ? 1 : 2;
        }
    }

    @Override
    public boolean shouldMapClassesToLookupClass() {
        return false;
    }

    public String getSecondClassName() {
        return (String) this.helperClassNames.getSecond();
    }
}
