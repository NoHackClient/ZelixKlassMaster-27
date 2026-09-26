package com.zelix.klassmaster.classfile.insn;

public class MonitorExitInstruction extends MonitorInstruction {
    public MonitorExitInstruction() {
        super(195);
    }

    @Override
    public void updateHeldMonitors(StackFrameState stackFrameState) {
        stackFrameState.popMatchingMonitorEnter(this);
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
