package com.zelix;

import com.zelix.klassmaster.ant.ProGuardTaskPath;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.engine.ZkmApiBase;
import com.zelix.klassmaster.util.ObservableHolder;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public class ZKM_PG_Task extends Task {
    public File configurationFile;
    public StringBuilder proGuardOptions = new StringBuilder();

    public void addText(String string) {
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
        }

        this.proGuardOptions.append(this.getProject().replaceProperties(string));
    }

    public void setRenamesourcefileattribute(String string) {
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
        }

        this.proGuardOptions.append("-renamesourcefileattribute");
        if (string != null) {
            this.proGuardOptions.append(' ');
            this.proGuardOptions.append('"');
            this.proGuardOptions.append(string);
            this.proGuardOptions.append('"');
        }
    }

    public void setFlattenpackagehierarchy(String string) {
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
        }

        this.proGuardOptions.append("-flattenpackagehierarchy");
        if (string != null) {
            this.proGuardOptions.append(' ');
            this.proGuardOptions.append('"');
            this.proGuardOptions.append(string);
            this.proGuardOptions.append('"');
        }
    }

    public void setMergeinterfacesaggressively(boolean bl) {
        if (bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-mergeinterfacesaggressively");
        }
    }

    public void setOptimizationpasses(int ba) {
        StringBuilder stringBuilder;
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
            stringBuilder = this.proGuardOptions;
        } else {
            stringBuilder = this.proGuardOptions;
        }

        stringBuilder.append("-optimizationpasses");
        this.proGuardOptions.append(' ');
        this.proGuardOptions.append(ba);
    }

    public void setConfiguration(File file1) {
        this.configurationFile = file1;
    }

    public void setPackageobfuscationdictionary(File file1) {
        StringBuilder stringBuilder;
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
            stringBuilder = this.proGuardOptions;
        } else {
            stringBuilder = this.proGuardOptions;
        }

        stringBuilder.append("-packageobfuscationdictionary");
        this.proGuardOptions.append(' ');
        this.proGuardOptions.append('"');
        this.proGuardOptions.append(file1.getAbsolutePath());
        this.proGuardOptions.append('"');
    }

    public void setVerbose(boolean bl) {
        if (bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-verbose");
        }
    }

    public void setMicroedition(boolean bl) {
        if (!bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-microedition");
        }
    }

    public void addConfiguredOutjar(ProGuardTaskPath proGuardTaskPath) {
        StringBuilder stringBuilder;
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
            stringBuilder = this.proGuardOptions;
        } else {
            stringBuilder = this.proGuardOptions;
        }

        stringBuilder.append("-outjars");
        this.proGuardOptions.append(' ');
        this.proGuardOptions.append(proGuardTaskPath.getFileName());
    }

    public void setPrintseeds(File file1) {
        StringBuilder stringBuilder;
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
            stringBuilder = this.proGuardOptions;
        } else {
            stringBuilder = this.proGuardOptions;
        }

        stringBuilder.append("-printseeds");
        this.proGuardOptions.append(' ');
        this.proGuardOptions.append('"');
        this.proGuardOptions.append(file1.getAbsolutePath());
        this.proGuardOptions.append('"');
    }

    public void setNote(boolean bl) {
        if (!bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-dontnote");
        }
    }

    public void setWarn(boolean bl) {
        if (!bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-dontwarn");
        }
    }

    public void setDump(File file1) {
        StringBuilder stringBuilder;
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
            stringBuilder = this.proGuardOptions;
        } else {
            stringBuilder = this.proGuardOptions;
        }

        stringBuilder.append("-dump");
        this.proGuardOptions.append(' ');
        this.proGuardOptions.append('"');
        this.proGuardOptions.append(file1.getAbsolutePath());
        this.proGuardOptions.append('"');
    }

    public void setPreverify(boolean bl) {
        if (!bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-dontpreverify");
        }
    }

    public void setAllowaccessmodification(boolean bl) {
        if (bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-allowaccessmodification");
        }
    }

    public void setSkipnonpubliclibraryclassmembers(boolean bl) {
        if (!bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-dontskipnonpubliclibraryclasses");
        }
    }

    public void setDefaultpackage(String string) {
        this.setRepackageclasses(string);
    }

    public void setObfuscate(boolean bl) {
        if (!bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-dontobfuscate");
        }
    }

    public void setObfuscationdictionary(File file1) {
        StringBuilder stringBuilder;
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
            stringBuilder = this.proGuardOptions;
        } else {
            stringBuilder = this.proGuardOptions;
        }

        stringBuilder.append("-obfuscationdictionary");
        this.proGuardOptions.append(' ');
        this.proGuardOptions.append('"');
        this.proGuardOptions.append(file1.getAbsolutePath());
        this.proGuardOptions.append('"');
    }

    public void setUsemixedcaseclassnames(boolean bl) {
        if (!bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-dontusemixedcaseclassnames");
        }
    }

    public void setPrintconfiguration(File file1) {
        StringBuilder stringBuilder;
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
            stringBuilder = this.proGuardOptions;
        } else {
            stringBuilder = this.proGuardOptions;
        }

        stringBuilder.append("-printconfiguration");
        this.proGuardOptions.append(' ');
        this.proGuardOptions.append('"');
        this.proGuardOptions.append(file1.getAbsolutePath());
        this.proGuardOptions.append('"');
    }

    public void setForceprocessing(boolean bl) {
        if (bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-forceprocessing");
        }
    }

    public void setPrintmapping(File file1) {
        StringBuilder stringBuilder;
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
            stringBuilder = this.proGuardOptions;
        } else {
            stringBuilder = this.proGuardOptions;
        }

        stringBuilder.append("-printmapping");
        this.proGuardOptions.append(' ');
        this.proGuardOptions.append('"');
        this.proGuardOptions.append(file1.getAbsolutePath());
        this.proGuardOptions.append('"');
    }

    public void setTarget(String string) {
        StringBuilder stringBuilder;
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
            stringBuilder = this.proGuardOptions;
        } else {
            stringBuilder = this.proGuardOptions;
        }

        stringBuilder.append("-target");
        this.proGuardOptions.append(' ');
        this.proGuardOptions.append(string);
    }

    public void setOptimize(boolean bl) {
        if (!bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-dontoptimize");
        }
    }

    public void addConfiguredInjar(ProGuardTaskPath proGuardTaskPath) {
        StringBuilder stringBuilder;
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
            stringBuilder = this.proGuardOptions;
        } else {
            stringBuilder = this.proGuardOptions;
        }

        stringBuilder.append("-injars");
        this.proGuardOptions.append(' ');
        this.proGuardOptions.append(proGuardTaskPath.getFileName());
    }

    public void setSkipnonpubliclibraryclasses(boolean bl) {
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
        }

        if (bl) {
            this.proGuardOptions.append("-skipnonpubliclibraryclasses");
        } else {
            this.proGuardOptions.append("-dontskipnonpubliclibraryclasses");
        }
    }

    public void setShrink(boolean bl) {
        if (!bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-dontshrink");
        }
    }

    public void setApplymapping(File file1) {
        StringBuilder stringBuilder;
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
            stringBuilder = this.proGuardOptions;
        } else {
            stringBuilder = this.proGuardOptions;
        }

        stringBuilder.append("-applymapping");
        this.proGuardOptions.append(' ');
        this.proGuardOptions.append('"');
        this.proGuardOptions.append(file1.getAbsolutePath());
        this.proGuardOptions.append('"');
    }

    public void setIgnorewarnings(boolean bl) {
        if (bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-ignorewarnings");
        }
    }

    public void execute() {
        if (this.configurationFile == null && this.proGuardOptions.length() == 0) {
            throw new BuildException("Missing or empty ProGuard configuration");
        }

        try {
            String string3;
            Project project;
            if (this.proGuardOptions.length() > 0) {
                String string1;
                if (this.configurationFile == null) {
                    string1 = this.proGuardOptions.toString();
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(this.proGuardOptions.toString());
                    stringBuilder.append(ZkmFileUtils.LINE_SEPARATOR);
                    stringBuilder.append(ZkmFileUtils.readFileAsString(this.configurationFile));
                    string1 = stringBuilder.toString();
                }

                ObservableHolder observableHolder = new ObservableHolder();
                String string = ZkmFileUtils.writeStringToTempFile(string1, observableHolder).getAbsolutePath();
                if (!observableHolder.isValueNull()) {
                    throw new BuildException((String) observableHolder.getValue());
                }

                string3 = string;
                project = this.getProject();
            } else {
                String string2 = this.configurationFile.getAbsolutePath();
                string3 = string2;
                project = this.getProject();
            }

            ZkmApiBase.run(string3, project.getProperties());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BuildException(exception.toString());
        }
    }

    public void setUseuniqueclassmembernames(boolean bl) {
        if (bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-useuniqueclassmembernames");
        }
    }

    public void setKeepparameternames(boolean bl) {
        if (bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-keepparameternames");
        }
    }

    public void setPrintusage(File file1) {
        StringBuilder stringBuilder;
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
            stringBuilder = this.proGuardOptions;
        } else {
            stringBuilder = this.proGuardOptions;
        }

        stringBuilder.append("-printusage");
        this.proGuardOptions.append(' ');
        this.proGuardOptions.append('"');
        this.proGuardOptions.append(file1.getAbsolutePath());
        this.proGuardOptions.append('"');
    }

    public void setClassobfuscationdictionary(File file1) {
        StringBuilder stringBuilder;
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
            stringBuilder = this.proGuardOptions;
        } else {
            stringBuilder = this.proGuardOptions;
        }

        stringBuilder.append("-classobfuscationdictionary");
        this.proGuardOptions.append(' ');
        this.proGuardOptions.append('"');
        this.proGuardOptions.append(file1.getAbsolutePath());
        this.proGuardOptions.append('"');
    }

    public void setRepackageclasses(String string) {
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
        }

        this.proGuardOptions.append("-repackageclasses");
        if (string != null) {
            this.proGuardOptions.append(' ');
            this.proGuardOptions.append('"');
            this.proGuardOptions.append(string);
            this.proGuardOptions.append('"');
        }
    }

    public void addConfiguredLibraryjar(ProGuardTaskPath proGuardTaskPath) {
        StringBuilder stringBuilder;
        if (this.proGuardOptions.length() > 0) {
            this.proGuardOptions.append(' ');
            stringBuilder = this.proGuardOptions;
        } else {
            stringBuilder = this.proGuardOptions;
        }

        stringBuilder.append("-libraryjars");
        this.proGuardOptions.append(' ');
        this.proGuardOptions.append(proGuardTaskPath.getFileName());
    }

    public void setAndroid(boolean bl) {
        if (!bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-android");
        }
    }

    public void setOverloadaggressively(boolean bl) {
        if (bl) {
            if (this.proGuardOptions.length() > 0) {
                this.proGuardOptions.append(' ');
            }

            this.proGuardOptions.append("-overloadaggressively");
        }
    }
}
