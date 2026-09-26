package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.classfile.MethodSignature;

public interface MainMethodConstants {
    MethodSignature MAIN_METHOD_SIGNATURE = new MethodSignature("main([Ljava/lang/String;)V");
}
