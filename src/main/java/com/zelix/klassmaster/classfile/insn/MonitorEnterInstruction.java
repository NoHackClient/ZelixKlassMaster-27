package com.zelix.klassmaster.classfile.insn;

public class MonitorEnterInstruction extends MonitorInstruction {
    @Override
    public void updateHeldMonitors(StackFrameState stackFrameState) {
        stackFrameState.pushMonitorEnter(this);
    }

    public MonitorEnterInstruction() {
        super(194);
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
