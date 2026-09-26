package com.zelix.klassmaster.script;

import com.zelix.klassmaster.archive.DirectoryFileLister;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassLoader;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.config.IgnoreMissingReferencesSpec;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.exceptions.ZkmProcessingException;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.log.AbstractMessageLog;
import com.zelix.klassmaster.obfuscator.exclude.FixedClassesExclusionSet;
import com.zelix.klassmaster.obfuscator.trim.ClassMemberSets;
import com.zelix.klassmaster.util.LinkedSetMultimap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.SetMultiMap;
import com.zelix.klassmaster.util.SystemEnvironmentConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class ScriptEnvironment extends AbstractMessageLog {
    public FixedClassesExclusionSet fixedClassesExclusionSet;
    public String trimLogFileName;
    public boolean looseClassFilesLoaded;
    public String defaultTrimExcludeFile;
    public boolean microEditionClassesPresent;
    public String defaultParamChangesExcludeFile;
    public String defaultParamObfuscationExcludeFile;
    public boolean skipMidpPreverification;
    public IgnoreMissingReferencesSpec ignoreMissingReferencesSpec;
    public String defaultExcludeFile;
    public ClassMemberSets trimExclusionMemberSets;
    public File defaultDirectory;
    public List versionedClasspaths;
    public boolean stackMapUpdateSkipped;
    public ArrayList excludeStatements = new ArrayList();
    public ArrayList ignoreMissingReferencesStatements = new ArrayList();
    public ArrayList trimExcludeStatements = new ArrayList();
    public ArrayList unexcludeStatements = new ArrayList();
    public ArrayList trimUnexcludeStatements = new ArrayList();
    public ArrayList obfuscateReferencesIncludeStatements = new ArrayList();
    public ArrayList obfuscateReferencesExcludeStatements = new ArrayList();
    public ArrayList accessedByReflectionStatements = new ArrayList();
    public ArrayList accessedByReflectionExcludeStatements = new ArrayList();
    public ArrayList obfuscateFlowExcludeStatements = new ArrayList();
    public ArrayList obfuscateFlowUnexcludeStatements = new ArrayList();
    public ArrayList obfuscateExceptionsExcludeStatements = new ArrayList();
    public ArrayList obfuscateExceptionsUnexcludeStatements = new ArrayList();
    public ArrayList stringEncryptionExcludeStatements = new ArrayList();
    public ArrayList stringEncryptionUnexcludeStatements = new ArrayList();
    public ArrayList integerEncryptionExcludeStatements = new ArrayList();
    public ArrayList integerEncryptionUnexcludeStatements = new ArrayList();
    public ArrayList longEncryptionExcludeStatements = new ArrayList();
    public ArrayList longEncryptionUnexcludeStatements = new ArrayList();
    public ArrayList existingSerializedClassesStatements = new ArrayList();
    public ArrayList fixedClassesStatements = new ArrayList();
    public ArrayList groupingsStatements = new ArrayList();
    public ArrayList removeMethodCallsIncludeStatements = new ArrayList();
    public ArrayList removeMethodCallsExcludeStatements = new ArrayList();
    public ArrayList paramChangesIncludeStatements = new ArrayList();
    public ArrayList paramChangesExcludeStatements = new ArrayList();
    public ArrayList paramObfuscationIncludeStatements = new ArrayList();
    public ArrayList paramObfuscationExcludeStatements = new ArrayList();
    public LinkedSetMultimap classInitializationOrder = new LinkedSetMultimap();
    public ClassRepository classRepository;
    public ZkmClasspath classpath;
    public String logFileName;
    public final boolean proGuardMappingInputEnabled;

    public void addAccessedByReflectionStatement(Object object) {
        this.accessedByReflectionStatements.add(object);
    }

    public void setDefaultFileNames(String string, String string1, String string2, String string3, String string4, String string5) throws ZkmProcessingException {
        if (string5 != null) {
            this.defaultDirectory = new File(ZkmFileUtils.resolveDotRelativePath(ZkmFileUtils.normalizeSeparators(string5), (File) null));
            if (!this.defaultDirectory.exists() || !this.defaultDirectory.isDirectory()) {
                throw new ZkmProcessingException(
                        "ERROR: Default directory \"" + this.defaultDirectory.getAbsolutePath() + "\" does not exist or it is not a directory"
                );
            }

            this.defaultDirectory = new File(this.defaultDirectory.getAbsolutePath());
        } else if (HiddenOptionFlags.DEFAULT_DIR != null) {
            this.defaultDirectory = new File(ZkmFileUtils.resolveDotRelativePath(ZkmFileUtils.normalizeSeparators(HiddenOptionFlags.DEFAULT_DIR), (File) null));
            if (!this.defaultDirectory.exists() || !this.defaultDirectory.isDirectory()) {
                throw new ZkmProcessingException(
                        "ERROR: Default directory \""
                                + this.defaultDirectory.getAbsolutePath()
                                + "\" specified by \""
                                + "ZKM_DEFAULT_DIR"
                                + "\" does not exist or it is not a directory"
                );
            }
        } else {
            this.defaultDirectory = new File(SystemEnvironmentConstants.USER_DIR);
        }

        if (string != null && string.trim().length() != 0) {
            this.trimLogFileName = ZkmFileUtils.resolveDotRelativePath(ZkmFileUtils.normalizeSeparators(string), this.defaultDirectory);
        } else {
            this.trimLogFileName = "ZKM_TrimLog.txt";
        }

        if (ZkmFileUtils.isRelativePath(this.trimLogFileName)) {
            this.trimLogFileName = new File(this.defaultDirectory, this.trimLogFileName).getAbsolutePath();
        } else {
            this.trimLogFileName = new File(this.trimLogFileName).getAbsolutePath();
        }

        if (string1 != null && string1.trim().length() != 0) {
            this.defaultExcludeFile = ZkmFileUtils.resolveDotRelativePath(ZkmFileUtils.normalizeSeparators(string1), this.defaultDirectory);
        } else {
            this.defaultExcludeFile = "defaultExclude.txt";
        }

        if (ZkmFileUtils.isRelativePath(this.defaultExcludeFile)) {
            this.defaultExcludeFile = new File(this.defaultDirectory, this.defaultExcludeFile).getAbsolutePath();
        } else {
            this.defaultExcludeFile = new File(this.defaultExcludeFile).getAbsolutePath();
        }

        if (string2 != null && string2.trim().length() != 0) {
            this.defaultTrimExcludeFile = ZkmFileUtils.resolveDotRelativePath(ZkmFileUtils.normalizeSeparators(string2), this.defaultDirectory);
        } else {
            this.defaultTrimExcludeFile = "defaultTrimExclude.txt";
        }

        if (string3 != null && string3.trim().length() != 0) {
            this.defaultParamChangesExcludeFile = ZkmFileUtils.resolveDotRelativePath(ZkmFileUtils.normalizeSeparators(string3), this.defaultDirectory);
        } else {
            this.defaultParamChangesExcludeFile = "defaultMethodParameterChangesExclude.txt";
        }

        if (string4 != null && string4.trim().length() != 0) {
            this.defaultParamObfuscationExcludeFile = ZkmFileUtils.resolveDotRelativePath(ZkmFileUtils.normalizeSeparators(string4), this.defaultDirectory);
        } else {
            this.defaultParamObfuscationExcludeFile = "defaultMethodParameterObfuscationExclude.txt";
        }

        if (ZkmFileUtils.isRelativePath(this.defaultTrimExcludeFile)) {
            this.defaultTrimExcludeFile = new File(this.defaultDirectory, this.defaultTrimExcludeFile).getAbsolutePath();
        } else {
            this.defaultTrimExcludeFile = new File(this.defaultTrimExcludeFile).getAbsolutePath();
        }
    }

    public File getDefaultDirectory() {
        return this.defaultDirectory;
    }

    public void resetIntegerEncryptionExclusions() {
        this.integerEncryptionExcludeStatements = new ArrayList();
        this.integerEncryptionUnexcludeStatements = new ArrayList();
    }

    public void addRemoveMethodCallsExcludeStatement(Object object) {
        this.removeMethodCallsExcludeStatements.add(object);
    }

    public List getAccessedByReflectionStatements() {
        return this.accessedByReflectionStatements;
    }

    public void addUnexcludeStatement(Object object) {
        this.unexcludeStatements.add(object);
    }

    public void addExcludeStatement(Object object) {
        this.excludeStatements.add(object);
    }

    public boolean isStackMapUpdateSkipped() {
        return this.stackMapUpdateSkipped;
    }

    public List getStringEncryptionUnexcludeStatements() {
        return this.stringEncryptionUnexcludeStatements;
    }

    public List getParamChangesIncludeStatements() {
        return this.paramChangesIncludeStatements;
    }

    public void resetClassInitializationOrder() {
        this.classInitializationOrder = new LinkedSetMultimap();
    }

    public void addRemoveMethodCallsIncludeStatement(Object object) {
        this.removeMethodCallsIncludeStatements.add(object);
    }

    public List getFixedClassesStatements() {
        return this.fixedClassesStatements;
    }

    public void addObfuscateFlowUnexcludeStatement(Object object) {
        this.obfuscateFlowUnexcludeStatements.add(object);
    }

    public SetMultiMap getClassInitializationOrder() {
        return this.classInitializationOrder;
    }

    public void resetExclusions() {
        this.excludeStatements = new ArrayList();
        this.unexcludeStatements = new ArrayList();
    }

    public List getGroupingsStatements() {
        return this.groupingsStatements;
    }

    public void addIntegerEncryptionUnexcludeStatement(Object object) {
        this.integerEncryptionUnexcludeStatements.add(object);
    }

    public void resetParamObfuscationExclusions() {
        this.paramObfuscationIncludeStatements = new ArrayList();
        this.paramObfuscationExcludeStatements = new ArrayList();
    }

    public ScriptEnvironment(ClassRepository classRepository1, ZkmClasspath zkmClasspath, String string, PrintWriter printWriter) {
        super(true);
        this.classRepository = classRepository1;
        this.classpath = zkmClasspath;

        try {
            this.setDefaultFileNames(
                    "ZKM_TrimLog.txt",
                    "defaultExclude.txt",
                    "defaultTrimExclude.txt",
                    "defaultMethodParameterChangesExclude.txt",
                    "defaultMethodParameterObfuscationExclude.txt",
                    SystemEnvironmentConstants.USER_DIR
            );
        } catch (ZkmProcessingException zkmProcessingException) {
        }

        ScriptEnvironment scriptEnvironment2;
        String string1;
        if (string == null) {
            this.logFileName = "ZKM_log.txt";
            scriptEnvironment2 = this;
            string1 = this.logFileName;
        } else {
            this.logFileName = string;
            scriptEnvironment2 = this;
            string1 = this.logFileName;
        }

        scriptEnvironment2.validateLogFileName(string1, this.trimLogFileName);
        this.logFileName = new File(this.logFileName).getAbsolutePath();
        super.logWriter = printWriter;
        this.proGuardMappingInputEnabled = false;
    }

    public void addLongEncryptionUnexcludeStatement(Object object) {
        this.longEncryptionUnexcludeStatements.add(object);
    }

    public void addAccessedByReflectionExcludeStatement(Object object) {
        this.accessedByReflectionExcludeStatements.add(object);
    }

    public void addLongEncryptionExcludeStatement(Object object) {
        this.longEncryptionExcludeStatements.add(object);
    }

    public List getParamObfuscationIncludeStatements() {
        return this.paramObfuscationIncludeStatements;
    }

    public boolean hasMicroEditionClasses() {
        return this.microEditionClassesPresent;
    }

    public void validateLogFileName(String string, String string1) {
        if (string == null) {
            throw new ZkmRuntimeException("The Zelix KlassMaster log file name cannot be null");
        }

        boolean bl;
        if (ZkmFileUtils.caseSensitiveFileSystem) {
            if (string.equals(string1)) {
                throw new ZkmRuntimeException("The Zelix KlassMaster log file name cannot be the same as the Trim log file name. : " + ZkmFileUtils.OS_NAME);
            }

            bl = ZkmFileUtils.caseSensitiveFileSystem;
        } else {
            bl = ZkmFileUtils.caseSensitiveFileSystem;
        }

        if (!bl && string.equalsIgnoreCase(string1)) {
            throw new ZkmRuntimeException("The Zelix KlassMaster log file name cannot be the same as the Trim log file name. : " + ZkmFileUtils.OS_NAME);
        }
    }

    public List getLongEncryptionExcludeStatements() {
        return this.longEncryptionExcludeStatements;
    }

    public List getAccessedByReflectionExcludeStatements() {
        return this.accessedByReflectionExcludeStatements;
    }

    public String getDefaultParamChangesExcludeFile() {
        return this.defaultParamChangesExcludeFile;
    }

    public List getObfuscateReferencesExcludeStatements() {
        return this.obfuscateReferencesExcludeStatements;
    }

    public List getObfuscateReferencesIncludeStatements() {
        return this.obfuscateReferencesIncludeStatements;
    }

    public List getObfuscateFlowUnexcludeStatements() {
        return this.obfuscateFlowUnexcludeStatements;
    }

    public void addObfuscateFlowExcludeStatement(Object object) {
        this.obfuscateFlowExcludeStatements.add(object);
    }

    public void resetObfuscateExceptionsExclusions() {
        this.obfuscateExceptionsExcludeStatements = new ArrayList();
        this.obfuscateExceptionsUnexcludeStatements = new ArrayList();
    }

    public void resetAccessedByReflection() {
        this.accessedByReflectionStatements = new ArrayList();
        this.accessedByReflectionExcludeStatements = new ArrayList();
    }

    public ClassMemberSets getTrimExclusionMemberSets() {
        return this.trimExclusionMemberSets;
    }

    public void addTrimUnexcludeStatement(Object object) {
        this.trimUnexcludeStatements.add(object);
    }

    public void resetRemoveMethodCalls() {
        this.removeMethodCallsIncludeStatements = new ArrayList();
        this.removeMethodCallsExcludeStatements = new ArrayList();
    }

    public void resetObfuscateReferenceExclusions() {
        this.obfuscateReferencesIncludeStatements = new ArrayList();
        this.obfuscateReferencesExcludeStatements = new ArrayList();
    }

    public ZkmClasspath getClasspathForVersion(Integer integer) throws ZkmException, IOException {
        if (integer == null) {
            return this.classpath;
        }

        ClasspathClassLoader classpathClassLoader1 = this.classpath.getClassLoader();
        ZkmClasspath zkmClasspath = null;
        if (this.versionedClasspaths == null) {
            this.versionedClasspaths = new ArrayList();
            zkmClasspath = new ZkmClasspath(System.getProperty("java.class.path"), integer);
            classpathClassLoader1.addVersionedClasspath(zkmClasspath);
            this.versionedClasspaths.add(zkmClasspath);
        } else {
            int ba = this.versionedClasspaths.size();
            boolean bl = false;

            for (int i = 0; i < ba; i++) {
                ZkmClasspath zkmClasspath1 = (ZkmClasspath) this.versionedClasspaths.get(i);
                if (zkmClasspath1.getReleaseVersion().equals(integer)) {
                    zkmClasspath = zkmClasspath1;
                    bl = true;
                    break;
                }
            }

            if (!bl) {
                zkmClasspath = new ZkmClasspath(System.getProperty("java.class.path"), integer);
                classpathClassLoader1.addVersionedClasspath(zkmClasspath);
                this.versionedClasspaths.add(zkmClasspath);
            }

            if (this.versionedClasspaths.size() > 1) {
                Collections.sort(this.versionedClasspaths, (zkmClasspathx, zkmClasspath1x) -> ((com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath) zkmClasspathx).getReleaseVersion() - ((com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath) zkmClasspath1x).getReleaseVersion());
            }
        }

        return zkmClasspath;
    }

    public List getObfuscateExceptionsUnexcludeStatements() {
        return this.obfuscateExceptionsUnexcludeStatements;
    }

    public void addParamChangesIncludeStatement(Object object) {
        this.paramChangesIncludeStatements.add(object);
    }

    public FixedClassesExclusionSet getFixedClassesExclusionSet() {
        return this.fixedClassesExclusionSet;
    }

    public boolean throwsOnFatalError() {
        return super.throwOnFatalError;
    }

    public ScriptEnvironment(
            ClassRepository classRepository1,
            ZkmClasspath zkmClasspath,
            boolean bl,
            String string,
            String string1,
            String string2,
            String string3,
            String string4,
            String string5,
            String string6,
            boolean bl1,
            boolean skipMidpPreverification,
            boolean proGuardMappingInputEnabled
    ) throws ZkmException {
        super(bl);
        this.classRepository = classRepository1;
        this.classpath = zkmClasspath;
        super.throwOnFatalError = bl1;
        this.skipMidpPreverification = skipMidpPreverification;
        this.proGuardMappingInputEnabled = proGuardMappingInputEnabled;
        this.setDefaultFileNames(string1, string2, string3, string4, string5, string6);
        if (string != null && string.trim().length() != 0) {
            this.logFileName = ZkmFileUtils.resolveDotRelativePath(ZkmFileUtils.normalizeSeparators(string), this.defaultDirectory);
        } else {
            this.logFileName = "ZKM_log.txt";
        }

        if (ZkmFileUtils.isRelativePath(this.logFileName)) {
            this.logFileName = new File(this.defaultDirectory, this.logFileName).getAbsolutePath();
        } else {
            this.logFileName = new File(this.logFileName).getAbsolutePath();
        }

        try {
            File file1 = new File(this.logFileName);
            this.validateLogFileName(this.logFileName, this.trimLogFileName);
            File file2 = file1.getParentFile();
            if (!file2.exists()) {
                DirectoryFileLister.ensureDirectoryExists(file2.getAbsolutePath());
            }

            ObservableHolder observableHolder = new ObservableHolder();
            if (!ZkmFileUtils.canWriteAndReadFile(this.logFileName, observableHolder)) {
                throw new ZkmProcessingException(
                        "Log file \"" + this.logFileName + "\" could not be created, modified and read. : '" + (String) observableHolder.getValue() + "' (B)"
                );
            }

            super.logWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file1), "UTF-8"), true);
        } catch (IOException iOException) {
            throw new ZkmProcessingException("Log file \"" + this.logFileName + "\" could not be opened : " + iOException);
        }

        if (string6 != null) {
            this.logMessage("Default directory set to '" + this.defaultDirectory.getAbsolutePath() + "'" + HiddenOptionFlags.LINE_SEPARATOR);
        }
    }

    public String getDefaultExcludeFile() {
        return this.defaultExcludeFile;
    }

    public void resetTrimExclusions() {
        this.trimExcludeStatements = new ArrayList();
        this.trimUnexcludeStatements = new ArrayList();
        this.trimExclusionMemberSets = null;
    }

    public List getObfuscateExceptionsExcludeStatements() {
        return this.obfuscateExceptionsExcludeStatements;
    }

    public String getDefaultParamObfuscationExcludeFile() {
        return this.defaultParamObfuscationExcludeFile;
    }

    public void resetObfuscateFlowExclusions() {
        this.obfuscateFlowExcludeStatements = new ArrayList();
        this.obfuscateFlowUnexcludeStatements = new ArrayList();
    }

    public List getTrimUnexcludeStatements() {
        return this.trimUnexcludeStatements;
    }

    public void resetGroupings() {
        this.groupingsStatements = new ArrayList();
    }

    public List getTrimExcludeStatements() {
        return this.trimExcludeStatements;
    }

    public void addParamChangesExcludeStatement(Object object) {
        this.paramChangesExcludeStatements.add(object);
    }

    public ClassRepository getClassRepository() {
        return this.classRepository;
    }

    public void addGroupingsStatement(Object object) {
        this.groupingsStatements.add(object);
    }

    public boolean hasClassInitializationOrder() {
        return this.classInitializationOrder != null;
    }

    public String getLogFileName() {
        return this.logFileName;
    }

    public void addParamObfuscationIncludeStatement(Object object) {
        this.paramObfuscationIncludeStatements.add(object);
    }

    public void resetParamChangesExclusions() {
        this.paramChangesIncludeStatements = new ArrayList();
        this.paramChangesExcludeStatements = new ArrayList();
    }

    public List getIntegerEncryptionExcludeStatements() {
        return this.integerEncryptionExcludeStatements;
    }

    public boolean isMidpPreverificationSkipped() {
        return this.skipMidpPreverification;
    }

    public void buildIgnoreMissingReferencesSpec() {
        if (this.ignoreMissingReferencesStatements.size() > 0) {
            this.ignoreMissingReferencesSpec = new IgnoreMissingReferencesSpec(this.classRepository, this.ignoreMissingReferencesStatements, this);
        }
    }

    public void addObfuscateReferencesIncludeStatement(Object object) {
        this.obfuscateReferencesIncludeStatements.add(object);
    }

    public IgnoreMissingReferencesSpec getIgnoreMissingReferencesSpec() {
        return this.ignoreMissingReferencesSpec;
    }

    public void resetIgnoreMissingReferences() {
        this.ignoreMissingReferencesSpec = null;
        this.ignoreMissingReferencesStatements = new ArrayList();
    }

    public void setTrimExclusionMemberSets(ClassMemberSets classMemberSets) {
        this.trimExclusionMemberSets = classMemberSets;
    }

    public void addParamObfuscationExcludeStatement(Object object) {
        this.paramObfuscationExcludeStatements.add(object);
    }

    public List getExcludeStatements() {
        return this.excludeStatements;
    }

    public void setFixedClassesExclusionSet(FixedClassesExclusionSet fixedClassesExclusionSet1) {
        this.fixedClassesExclusionSet = fixedClassesExclusionSet1;
    }

    public List getStringEncryptionExcludeStatements() {
        return this.stringEncryptionExcludeStatements;
    }

    public String getTrimLogFileName() {
        return this.trimLogFileName;
    }

    public List getExistingSerializedClassesStatements() {
        return this.existingSerializedClassesStatements;
    }

    public void addObfuscateExceptionsExcludeStatement(Object object) {
        this.obfuscateExceptionsExcludeStatements.add(object);
    }

    public void addIgnoreMissingReferencesStatement(Object object) {
        this.ignoreMissingReferencesStatements.add(object);
    }

    public String getDefaultTrimExcludeFile() {
        return this.defaultTrimExcludeFile;
    }

    public List getRemoveMethodCallsExcludeStatements() {
        return this.removeMethodCallsExcludeStatements;
    }

    public void setStackMapUpdateSkipped(boolean stackMapUpdateSkipped) {
        this.stackMapUpdateSkipped = stackMapUpdateSkipped;
    }

    @Override
    public void closeLog() {
        super.closeLog();
        if (this.classpath != null) {
            this.classpath.closeJrtFileSystem();
        }
    }

    public void addStringEncryptionExcludeStatement(Object object) {
        this.stringEncryptionExcludeStatements.add(object);
    }

    public List getObfuscateFlowExcludeStatements() {
        return this.obfuscateFlowExcludeStatements;
    }

    public void addIntegerEncryptionExcludeStatement(Object object) {
        this.integerEncryptionExcludeStatements.add(object);
    }

    public void addClassInitializationOrder(SetMultiMap setMultiMap) {
        Iterator iterator = setMultiMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            this.classInitializationOrder.addValues(entry.getKey(), (Collection) entry.getValue());
        }
    }

    public List getLongEncryptionUnexcludeStatements() {
        return this.longEncryptionUnexcludeStatements;
    }

    public void setLooseClassFilesLoaded(boolean looseClassFilesLoaded) {
        this.looseClassFilesLoaded = looseClassFilesLoaded;
    }

    public List getParamChangesExcludeStatements() {
        return this.paramChangesExcludeStatements;
    }

    public void resetFixedClasses() {
        this.fixedClassesStatements = new ArrayList();
        this.fixedClassesExclusionSet = null;
    }

    public void resetExistingSerializedClasses() {
        this.existingSerializedClassesStatements = new ArrayList();
    }

    public List getUnexcludeStatements() {
        return this.unexcludeStatements;
    }

    public void addStringEncryptionUnexcludeStatement(Object object) {
        this.stringEncryptionUnexcludeStatements.add(object);
    }

    public List getRemoveMethodCallsIncludeStatements() {
        return this.removeMethodCallsIncludeStatements;
    }

    public void resetStringEncryptionExclusions() {
        this.stringEncryptionExcludeStatements = new ArrayList();
        this.stringEncryptionUnexcludeStatements = new ArrayList();
    }

    public void clearTrimExclusionMemberSets() {
        this.trimExclusionMemberSets = null;
    }

    public void addObfuscateReferencesExcludeStatement(Object object) {
        this.obfuscateReferencesExcludeStatements.add(object);
    }

    public void addTrimExcludeStatement(Object object) {
        this.trimExcludeStatements.add(object);
    }

    public boolean isProGuardMappingInputEnabled() {
        return this.proGuardMappingInputEnabled;
    }

    public void setMicroEditionClassesPresent(boolean microEditionClassesPresent) {
        this.microEditionClassesPresent = microEditionClassesPresent;
    }

    public List getParamObfuscationExcludeStatements() {
        return this.paramObfuscationExcludeStatements;
    }

    public void addExistingSerializedClassesStatement(Object object) {
        this.existingSerializedClassesStatements.add(object);
    }

    public boolean isLooseClassFilesLoaded() {
        return this.looseClassFilesLoaded;
    }

    public List getIntegerEncryptionUnexcludeStatements() {
        return this.integerEncryptionUnexcludeStatements;
    }

    public void resetLongEncryptionExclusions() {
        this.longEncryptionExcludeStatements = new ArrayList();
        this.longEncryptionUnexcludeStatements = new ArrayList();
    }

    public void addObfuscateExceptionsUnexcludeStatement(Object object) {
        this.obfuscateExceptionsUnexcludeStatements.add(object);
    }

    public void addFixedClassesStatement(Object object) {
        this.fixedClassesStatements.add(object);
    }

    public ScriptEnvironment(
            ClassRepository classRepository1,
            ZkmClasspath zkmClasspath,
            String string,
            String string1,
            String string2,
            String string3,
            String string4,
            String string5,
            String string6
    ) throws ZkmException {
        this(classRepository1, zkmClasspath, false, string, string1, string2, string3, string4, string5, string6, false, false, false);
    }
}
