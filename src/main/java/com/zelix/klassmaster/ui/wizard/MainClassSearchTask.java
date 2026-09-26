package com.zelix.klassmaster.ui.wizard;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.hierarchy.ClassFileSetIndex;
import com.zelix.klassmaster.exceptions.ClassFileLoadException;
import com.zelix.klassmaster.exceptions.ZkmClassNotFoundException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.ui.component.ClassListEntry;
import com.zelix.klassmaster.ui.dialog.MessageBoxDialog;
import com.zelix.klassmaster.util.DialogCallback;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.List;
import java.util.Vector;

public class MainClassSearchTask implements Runnable {
    public final ScriptHelperWizard wizard;
    public final DialogCallback resultCallback;
    public final DialogCallback trimExcludeCallback;

    public MainClassSearchTask(ScriptHelperWizard scriptHelperWizard, DialogCallback dialogCallback1, DialogCallback dialogCallback2) {
        this.wizard = scriptHelperWizard;
        this.resultCallback = dialogCallback1;
        this.trimExcludeCallback = dialogCallback2;
    }

    @Override
    public void run() {
        try {
            try {
                this.wizard.mainWindow.showWaitCursor();
                ClassFileSetIndex classFileSetIndex = this.wizard.getClassFileIndex(this.wizard.openedLocations);
                Vector vector = null;
                List list1 = classFileSetIndex.getClassesDeclaringMethod("main([Ljava/lang/String;)V");
                List list2 = classFileSetIndex.findSubclasses("java/applet/Applet");
                if (list1 != null && list1.size() > 0) {
                    vector = new Vector(Math.max(5, list1.size()));

                    for (int i = 0; i < list1.size(); i++) {
                        vector.addElement(new ClassListEntry((ClassFileBase) list1.get(i), true));
                    }
                }

                ScriptHelperWizard scriptHelperWizard;
                if (list2 == null) {
                    scriptHelperWizard = this.wizard;
                } else if (list2.size() <= 0) {
                    scriptHelperWizard = this.wizard;
                } else {
                    if (vector == null) {
                        vector = new Vector(list2.size());
                    }

                    for (int i = 0; i < list2.size(); i++) {
                        vector.addElement(new ClassListEntry((ClassFileBase) list2.get(i), false));
                    }

                    scriptHelperWizard = this.wizard;
                }

                ScriptHelperWizard.accessSetMainClassEntries(scriptHelperWizard, vector);
                ScriptHelperWizard.accessSetRootPackage(this.wizard, classFileSetIndex.buildPackageTree());
                this.resultCallback.onDialogResult(this.trimExcludeCallback);
            } catch (ZkmClassNotFoundException zkmClassNotFoundException) {
                new MessageBoxDialog(
                        this.wizard.mainWindow,
                        "Classpath Error",
                        "Error while determining classes with main methods : Class '" + ZkmUtils.slashesToDots(zkmClassNotFoundException.getClassName()) + "' not found"
                );
                this.wizard.finishWizard();
            } catch (ClassFileLoadException classFileLoadException) {
                new MessageBoxDialog(
                        this.wizard.mainWindow, "File Error", "Error while determining classes with main methods : " + classFileLoadException.getMessage()
                );
                this.wizard.finishWizard();
            } catch (ZkmProcessingException zkmProcessingException) {
                new MessageBoxDialog(
                        this.wizard.mainWindow, "File Error", "Error while determining classes with main methods : " + zkmProcessingException.getMessage()
                );
                this.wizard.finishWizard();
            } finally {
                this.wizard.mainWindow.restoreDefaultCursor();
            }
        } catch (Throwable throwable) {
            throw ZkmUtils.sneakyThrow(throwable);
        }
    }
}
