package com.zelix.klassmaster.classfile.insn;

public interface IntCounter {
    int getValue();

    int addAndGet(int ba);

    int incrementAndGet();

    int getAndIncrement();
}
