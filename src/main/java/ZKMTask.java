



import com.zelix.ZKM;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class ZKMTask
        extends Task {
    private String scriptFileName;
    private String logFileName;
    private String trimLogFileName;
    private String defaultExcludeFileName;
    private String defaultTrimExcludeFileName;
    private String defaultMethodParameterChangesExcludeFileName;
    private String defaultMethodParameterObfuscationExcludeFileName;
    private String defaultDirectoryName;
    private boolean isVerbose = false;
    private boolean isParseOnly = false;
    public static final String TRUE = "true";
    public static final String YES = "yes";
    public static final String MISSING_SCRIPT_MSG = "Missing or empty ZKM Script file name";
    List<Sysproperty> sysproperties = new ArrayList<Sysproperty>();

    public void setScriptFileName(String scriptFileName) {
        this.scriptFileName = scriptFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public void setTrimLogFileName(String trimLogFileName) {
        this.trimLogFileName = trimLogFileName;
    }

    public void setDefaultExcludeFileName(String defaultExcludeFileName) {
        this.defaultExcludeFileName = defaultExcludeFileName;
    }

    public void setDefaultTrimExcludeFileName(String defaultTrimExcludeFileName) {
        this.defaultTrimExcludeFileName = defaultTrimExcludeFileName;
    }

    public void setDefaultMethodParameterChangesExcludeFileName(String defaultMethodParameterChangesExcludeFileName) {
        this.defaultMethodParameterChangesExcludeFileName = defaultMethodParameterChangesExcludeFileName;
    }

    public void setDefaultMethodParameterObfuscationExcludeFileName(String defaultMethodParameterObfuscationExcludeFileName) {
        this.defaultMethodParameterObfuscationExcludeFileName = defaultMethodParameterObfuscationExcludeFileName;
    }

    public void setDefaultDirectoryName(String defaultDirectoryName) {
        this.defaultDirectoryName = defaultDirectoryName;
    }

    public void setIsVerbose(String isVerboseString) {
        if (isVerboseString != null && (isVerboseString.equalsIgnoreCase(TRUE) || isVerboseString.equalsIgnoreCase(YES))) {
            this.isVerbose = true;
        }
    }

    public void setIsParseOnly(String isParseOnlyString) {
        if (isParseOnlyString != null && (isParseOnlyString.equalsIgnoreCase(TRUE) || isParseOnlyString.equalsIgnoreCase(YES))) {
            this.isParseOnly = true;
        }
    }

    public void execute() throws BuildException {
        if (this.scriptFileName == null || this.scriptFileName.length() == 0) {
            throw new BuildException(MISSING_SCRIPT_MSG);
        }
        if (this.sysproperties != null) {
            Properties systemProperties = System.getProperties();
            for (Sysproperty sysproperty : this.sysproperties) {
                systemProperties.put(sysproperty.getKey(), sysproperty.getValue());
            }
        }
        try {
            ZKM.runWithHashtable(this.scriptFileName, this.logFileName, this.trimLogFileName, this.defaultExcludeFileName, this.defaultTrimExcludeFileName, this.defaultMethodParameterChangesExcludeFileName, this.defaultMethodParameterObfuscationExcludeFileName, this.defaultDirectoryName, this.isVerbose, this.isParseOnly, this.getProject().getProperties());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BuildException(ex.toString());
        }
    }

    public Sysproperty createSysproperty() {
        Sysproperty sysproperty = new Sysproperty(this);
        this.sysproperties.add(sysproperty);
        return sysproperty;
    }

    public class Sysproperty {
        private String key;
        private String value;
        final ZKMTask this$0;

        public Sysproperty(ZKMTask this$0) {
            this.this$0 = this$0;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
}
