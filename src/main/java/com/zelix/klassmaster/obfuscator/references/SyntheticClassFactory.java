package com.zelix.klassmaster.obfuscator.references;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.exceptions.ZkmException;

public interface SyntheticClassFactory {
    ProgramClass createSyntheticClass(String string, int ba, int bb, int bc, Object object) throws ZkmException;
}
