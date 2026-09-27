package com.zelix;

import com.zelix.klassmaster.archive.DirectoryFileLister;
import com.zelix.klassmaster.archive.ZkmFileUtils;
import com.zelix.klassmaster.changelog.ChangeLogClassKey;
import com.zelix.klassmaster.changelog.ChangeLogConvertConstants;
import com.zelix.klassmaster.changelog.ChangeLogMapping;
import com.zelix.klassmaster.changelog.ChangeLogMemberKey;
import com.zelix.klassmaster.changelog.NewNameMapping;
import com.zelix.klassmaster.changelog.parser.ChangeLogNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogParseException;
import com.zelix.klassmaster.changelog.parser.ChangeLogParser;
import com.zelix.klassmaster.changelog.parser.ChangeLogSimpleNode;
import com.zelix.klassmaster.changelog.parser.ChangeLogTokenMgrError;
import com.zelix.klassmaster.classfile.MethodInfo;
import com.zelix.klassmaster.classfile.MethodSignature;
import com.zelix.klassmaster.classfile.ProgramClass;
import com.zelix.klassmaster.classfile.attribute.CodeAttributeBody;
import com.zelix.klassmaster.classfile.hierarchy.ClassRepository;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassLoader;
import com.zelix.klassmaster.classfile.hierarchy.ClasspathClassResolver;
import com.zelix.klassmaster.classfile.hierarchy.ZkmClasspath;
import com.zelix.klassmaster.engine.ZkmApiBase;
import com.zelix.klassmaster.exceptions.ClassLookupException;
import com.zelix.klassmaster.script.ScriptEnvironment;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ObservableHolder;
import com.zelix.klassmaster.util.ZkmStringUtils;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class ZKMChangeLogConvert extends ChangeLogConvertConstants {
    public int baselineErrorCount;
    public ScriptEnvironment scriptEnvironment;
    public int baselineWarningCount;
    public ChangeLogMapping changeLogMapping;
    public int baselineMessageCount;
    public int baselineFatalErrorCount;
    public final File changeLogFile;
    public final File outputFile;
    public final ClasspathClassResolver classResolver;

    public ZKMChangeLogConvert(String string, String string1, String string2) throws Exception {
        this.changeLogFile = new File(string);
        this.outputFile = new File(string1);
        if (string2 != null && string2.length() > 0) {
            ZkmApiBase.isZkmJarInClasspath(string2);
            ZkmClasspath zkmClasspath = new ZkmClasspath(string2);
            zkmClasspath.setClassLoader(new ClasspathClassLoader(zkmClasspath, ZkmFileUtils.caseSensitiveFileSystem));
            zkmClasspath.locateRuntimeClasses(new ObservableHolder(), new ObservableHolder());
            this.classResolver = new ClasspathClassResolver(zkmClasspath, ZkmFileUtils.caseSensitiveFileSystem);
        } else {
            this.classResolver = null;
        }

        if (!this.changeLogFile.exists() || this.changeLogFile.isDirectory()) {
            System.out.println("File '" + this.changeLogFile.getAbsolutePath() + "' doesn't exist or is a directory");
        } else if (this.outputFile.isDirectory()) {
            System.out.println("File '" + this.outputFile.getAbsolutePath() + "' is a directory");
        } else {
            ObservableHolder observableHolder = new ObservableHolder();
            this.convertChangeLog(observableHolder);
            if (observableHolder.isValueNull()) {
                System.out.println("Converted contents of '" + this.changeLogFile.getAbsolutePath() + "' written to '" + this.outputFile.getAbsolutePath() + "'");
            } else {
                System.out.println("ERROR: " + (String) observableHolder.getValue());
            }
        }
    }

    public void reportNewLogErrors() {
        boolean bl = false;
        boolean bl1 = false;
        if (this.baselineFatalErrorCount < this.scriptEnvironment.getSeriousErrorCount() || this.baselineErrorCount < this.scriptEnvironment.getErrorCount()) {
            bl = true;
            bl1 = true;
        }

        if (this.baselineWarningCount < this.scriptEnvironment.getWarningCount() || this.baselineMessageCount < this.scriptEnvironment.getMessageCount()) {
            bl1 = true;
        }

        if (bl1) {
            System.err.println(this.scriptEnvironment.getLoggedMessagesText());
        }

        if (bl) {
            System.exit(1);
        }
    }

    public void recordLogCounts() {
        this.baselineMessageCount = this.scriptEnvironment.getMessageCount();
        this.baselineWarningCount = this.scriptEnvironment.getWarningCount();
        this.baselineErrorCount = this.scriptEnvironment.getErrorCount();
        this.baselineFatalErrorCount = this.scriptEnvironment.getSeriousErrorCount();
    }

    
    
    public void convertChangeLog(ObservableHolder observableHolder) throws Exception {
        observableHolder.setValue(null);
        this.scriptEnvironment = new ScriptEnvironment(
                (ClassRepository) null, (ZkmClasspath) null, "ZKM_ConvertLog.txt", (String) null, (String) null, (String) null, (String) null, (String) null, (String) null
        );
        this.scriptEnvironment.clearLoggedMessages();
        this.recordLogCounts();
        this.parseChangeLog();
        this.reportNewLogErrors();
        Map map1 = this.changeLogMapping.getClassMappings();
        PrintWriter printWriter = null;
        boolean bl = false ;

        try {
            bl = true;
            File file1 = this.outputFile.getAbsoluteFile().getParentFile();
            if (file1 != null) {
                DirectoryFileLister.ensureDirectoryExists(file1.getAbsolutePath());
            }

            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(this.outputFile)));
            if (map1 != null) {
                StringBuilder stringBuilder = new StringBuilder();
                Iterator iterator = new TreeSet(map1.keySet()).iterator();

                while (iterator.hasNext()) {
                    String string = (String) iterator.next();
                    String string1 = (String) map1.get(string);
                    stringBuilder.setLength(0);
                    stringBuilder.append(string);
                    stringBuilder.append(" -> ");
                    stringBuilder.append(string1);
                    stringBuilder.append(':');
                    printWriter.println(stringBuilder.toString());
                    ListMultimap listMultimap = this.changeLogMapping.copyRawFieldMappings(string);
                    if (listMultimap != null) {
                        TreeSet treeSet = new TreeSet(listMultimap.keySet());
                        Iterator iterator1 = treeSet.iterator();

                        while (iterator1.hasNext()) {
                            ChangeLogClassKey changeLogClassKey = (ChangeLogClassKey) iterator1.next();
                            stringBuilder.setLength(0);
                            stringBuilder.append("    ");
                            if (changeLogClassKey.getType() != null) {
                                stringBuilder.append(changeLogClassKey.getType());
                                stringBuilder.append(' ');
                            }

                            stringBuilder.append(changeLogClassKey.getName());
                            stringBuilder.append(" -> ");
                            List list1 = listMultimap.getValues(changeLogClassKey);
                            stringBuilder.append((String) list1.get(0));
                            printWriter.println(stringBuilder.toString());
                        }
                    }

                    ProgramClass programClass1;
                    ChangeLogMapping changeLogMapping1;
                    programClass1 = null;
                    label128:
                    if (this.classResolver != null) {
                        try {
                            programClass1 = this.classResolver.loadProgramClass(ZkmUtils.dotsToSlashes(string1));
                        } catch (ClassLookupException classLookupException) {
                            this.scriptEnvironment.logError(classLookupException.getMessage());
                            changeLogMapping1 = this.changeLogMapping;
                            break label128;
                        }

                        changeLogMapping1 = this.changeLogMapping;
                    } else {
                        changeLogMapping1 = this.changeLogMapping;
                    }

                    ListMultimap listMultimap1 = changeLogMapping1.copyRawMethodMappings(string);
                    if (listMultimap1 != null) {
                        TreeSet treeSet1 = new TreeSet(listMultimap1.keySet());
                        Iterator iterator2 = treeSet1.iterator();

                        while (iterator2.hasNext()) {
                            ChangeLogMemberKey changeLogMemberKey = (ChangeLogMemberKey) iterator2.next();
                            stringBuilder.setLength(0);
                            stringBuilder.append("    ");
                            if (changeLogMemberKey.getType() != null) {
                                NewNameMapping newNameMapping = (NewNameMapping) listMultimap1.getValues(changeLogMemberKey).get(0);
                                if (programClass1 != null) {
                                    String string2 = MethodSignature.parseParameterList(newNameMapping.getEffectiveParameterTypes());
                                    String string3 = MethodSignature.javaTypeToDescriptor(changeLogMemberKey.getType(), map1);
                                    MethodSignature methodSignature1 = new MethodSignature(newNameMapping.getNewName(), string2, string3);
                                    MethodInfo methodInfo1 = programClass1.findMethodBySignature(methodSignature1);
                                    if (methodInfo1 != null) {
                                        CodeAttributeBody codeAttributeBody = methodInfo1.getCodeAttribute();
                                        if (codeAttributeBody != null) {
                                            int[] lineNumbers = codeAttributeBody.getLineNumbers();
                                            if (lineNumbers.length > 0) {
                                                stringBuilder.append(lineNumbers[0]);
                                                stringBuilder.append(':');
                                                stringBuilder.append(lineNumbers[lineNumbers.length - 1]);
                                                stringBuilder.append(':');
                                            }
                                        }
                                    } else {
                                        this.scriptEnvironment
                                                .logError(
                                                        "Method '"
                                                                + methodSignature1
                                                                + "' not found in class '"
                                                                + programClass1.getDottedClassName()
                                                                + "'. Line numbers could not be included in mapping."
                                                );
                                    }
                                }

                                stringBuilder.append(changeLogMemberKey.getType());
                                stringBuilder.append(' ');
                                stringBuilder.append(changeLogMemberKey.getName());
                                stringBuilder.append('(');
                                String string4 = ZkmStringUtils.replaceAll(changeLogMemberKey.getParameterTypes(), ", ", ",");
                                stringBuilder.append(string4);
                                stringBuilder.append(')');
                                stringBuilder.append(" -> ");
                                stringBuilder.append(newNameMapping.getNewName());
                                printWriter.println(stringBuilder.toString());
                            }
                        }
                    }
                }

                bl = false;
            } else {
                observableHolder.setValue("'" + this.changeLogFile.getAbsolutePath() + "' contains no class mappings.");
                bl = false;
            }
        } finally {
            if (bl) {
                if (printWriter != null) {
                    printWriter.close();
                }
            }
        }

        printWriter.close();
    }

    
    
    public void parseChangeLog() throws Exception {
        BufferedReader bufferedReader = null;
        ChangeLogSimpleNode changeLogSimpleNode = null;
        boolean bl = false ;

        try {
            bl = true;
            this.changeLogMapping = new ChangeLogMapping(this.changeLogFile.getAbsolutePath(), this.scriptEnvironment);
            ChangeLogParser changeLogParser = ChangeLogParser.instance;
            String string = ChangeLogMapping.readChangeLogEncoding(this.changeLogFile);
            if (string != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.changeLogFile), string));
            } else {
                bufferedReader = new BufferedReader(new FileReader(this.changeLogFile));
            }

            if (changeLogParser == null) {
                new ChangeLogParser(bufferedReader);
            } else {
                ChangeLogParser.ReInit(bufferedReader);
            }

            changeLogSimpleNode = ChangeLogParser.Input();
            ChangeLogMapping changeLogMapping1 = this.changeLogMapping;
            changeLogSimpleNode.interpret((ChangeLogNode) null, 30872, 34067, 41973, changeLogMapping1);
            ChangeLogMapping changeLogMapping2;
            if (changeLogSimpleNode.jjtGetNumChildren() == 0) {
                System.out.println("ERROR: '" + this.changeLogFile.getAbsolutePath() + "' is empty.");
                System.exit(1);
                changeLogMapping2 = this.changeLogMapping;
            } else {
                this.changeLogMapping.derivePackageMappingsFromClasses();
                changeLogMapping2 = this.changeLogMapping;
            }

            changeLogMapping2.markParsingComplete();
            bl = false;
        } catch (FileNotFoundException fileNotFoundException) {
            throw new Exception("File Error : Couldn't open file \"" + this.changeLogFile.getAbsolutePath() + "\"");
        } catch (ChangeLogTokenMgrError changeLogTokenMgrError) {
            throw new Exception("Lexical error while reading \"" + this.changeLogFile.getAbsolutePath() + "\" : \"" + changeLogTokenMgrError.getMessage() + "\"");
        } catch (ChangeLogParseException changeLogParseException) {
            throw new Exception("Parse error while reading \"" + this.changeLogFile.getAbsolutePath() + "\" : \"" + changeLogParseException.getMessage() + "\"");
        } finally {
            if (bl) {
                if (changeLogSimpleNode != null) {
                    changeLogSimpleNode.dump();
                }

                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException iOException) {
                    }
                }
            }
        }

        if (changeLogSimpleNode != null) {
            changeLogSimpleNode.dump();
        }

        try {
            bufferedReader.close();
        } catch (IOException iOException1) {
        }
    }

    public static void main(String[] strings) {
        try {
            if (strings.length != 2 && strings.length != 3) {
                System.out.println("Usage: java com.zelix.ZKMChangeLogConvert <changeLogFileName> <outputFileName> [obfuscatedByteCodeClasspath]");
                System.exit(1);
            }

            String string3 = strings[0];
            String string4 = strings[1];
            String string = strings.length == 3 ? strings[2] : null;
            String string1 = string4;
            String string2 = string3;
            new ZKMChangeLogConvert(string2, string1, string);
        } catch (Throwable throwable) {
            throw ZkmUtils.<RuntimeException>sneakyThrow(throwable);
        }
    }
}
