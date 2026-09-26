package com.zelix.klassmaster.ui;

import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.config.ChangeLogInputFile;
import com.zelix.klassmaster.config.ObfuscateOptions;
import com.zelix.klassmaster.log.MessageReporter;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.List;
import java.util.Vector;

public class GuiObfuscateTask implements Runnable {
    public final ZkmMainWindow mainWindow;
    public final ObfuscateOptions obfuscateOptions;
    public final Vector excludeStatements;
    public final Vector referenceIncludeStatements;
    public final MessageReporter messageReporter;
    public final DialogCallback dialogCallback;
    public final ScriptEnvironment scriptEnvironment;
    public final OperationStatusCallback statusCallback;

    @Override
    public void run() {
        try {
            this.mainWindow.setBusy(true);
            ClassRepository classRepository1 = ZkmMainWindow.accessClassRepository(this.mainWindow);
            ChangeLogInputFile[] changeLogInputFiles = this.obfuscateOptions.c ? this.obfuscateOptions.g : null;
            List list1 = (List) null;
            List list2 = (List) null;
            List list3 = (List) null;
            List list4 = (List) null;
            List list5 = (List) null;
            List list6 = (List) null;
            List list7 = (List) null;
            List list8 = (List) null;
            List list9 = (List) null;
            List list10 = (List) null;
            List list11 = (List) null;
            List list12 = (List) null;
            List list13 = (List) null;
            List list14 = (List) null;
            List list15 = (List) null;
            List list16 = (List) null;
            List list17 = (List) null;
            List list18 = (List) null;
            List list19 = (List) null;
            List list20 = (List) null;
            List list21 = (List) null;
            boolean bl;
            ObfuscateOptions obfuscateOptions1;
            if (this.obfuscateOptions.m == 1) {
                bl = true;
                obfuscateOptions1 = this.obfuscateOptions;
            } else {
                bl = false;
                obfuscateOptions1 = this.obfuscateOptions;
            }

            classRepository1.obfuscate(
                    changeLogInputFiles,
                    this.obfuscateOptions.e,
                    this.obfuscateOptions.h,
                    this.obfuscateOptions.f,
                    this.obfuscateOptions.v,
                    this.excludeStatements,
                    list1,
                    list2,
                    list3,
                    list4,
                    list5,
                    list6,
                    list7,
                    list8,
                    list9,
                    list10,
                    list11,
                    list12,
                    list13,
                    list14,
                    list15,
                    list16,
                    this.referenceIncludeStatements,
                    list17,
                    list18,
                    list19,
                    list20,
                    list21,
                    this.obfuscateOptions.E,
                    this.obfuscateOptions.x,
                    this.obfuscateOptions.k,
                    this.obfuscateOptions.l,
                    this.obfuscateOptions.w,
                    this.obfuscateOptions.z,
                    this.obfuscateOptions.A,
                    this.obfuscateOptions.y,
                    this.obfuscateOptions.b,
                    this.obfuscateOptions.u,
                    this.obfuscateOptions.o,
                    this.obfuscateOptions.d7,
                    this.obfuscateOptions.as,
                    this.obfuscateOptions.dZ,
                    this.obfuscateOptions.dC,
                    this.obfuscateOptions.i,
                    this.obfuscateOptions.B,
                    this.obfuscateOptions.am,
                    this.obfuscateOptions.s,
                    this.obfuscateOptions.t,
                    this.obfuscateOptions.C,
                    this.obfuscateOptions.r,
                    this.obfuscateOptions.q,
                    this.obfuscateOptions.D,
                    this.obfuscateOptions.aj,
                    bl,
                    obfuscateOptions1.ai,
                    this.obfuscateOptions.j,
                    this.obfuscateOptions.XN,
                    this.obfuscateOptions.ar,
                    this.obfuscateOptions.F,
                    this.obfuscateOptions.ay,
                    this.obfuscateOptions.dV,
                    this.obfuscateOptions.aC,
                    this.obfuscateOptions.dN,
                    this.obfuscateOptions.at,
                    this.obfuscateOptions.dv,
                    this.obfuscateOptions.dE,
                    this.obfuscateOptions.di,
                    this.obfuscateOptions.dB,
                    this.obfuscateOptions.dr,
                    this.messageReporter,
                    this.dialogCallback,
                    this.scriptEnvironment
            );
            ZkmMainWindow zkmMainWindow;
            if (this.obfuscateOptions.h != null) {
                this.obfuscateOptions.h.close();
                zkmMainWindow = this.mainWindow;
            } else {
                zkmMainWindow = this.mainWindow;
            }

            ZkmMainWindow.getSelectedItemHolder(zkmMainWindow).setChangedAndNotify();
            ProgramClass programClass1 = (ProgramClass) ZkmMainWindow.getSelectedClassHolder(this.mainWindow).getSelectedNode();
            if (programClass1 != null) {
                programClass1.refreshPropertyNodes();
            }

            if (this.statusCallback != null) {
                this.statusCallback.onDialogCancelled();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }

    public GuiObfuscateTask(
            ZkmMainWindow zkmMainWindow,
            ObfuscateOptions obfuscateOptions1,
            Vector vector,
            Vector vector1,
            MessageReporter messageReporter1,
            DialogCallback dialogCallback1,
            ScriptEnvironment scriptEnvironment1,
            OperationStatusCallback operationStatusCallback
    ) {
        this.mainWindow = zkmMainWindow;
        this.obfuscateOptions = obfuscateOptions1;
        this.excludeStatements = vector;
        this.referenceIncludeStatements = vector1;
        this.messageReporter = messageReporter1;
        this.dialogCallback = dialogCallback1;
        this.scriptEnvironment = scriptEnvironment1;
        this.statusCallback = operationStatusCallback;
    }
}
