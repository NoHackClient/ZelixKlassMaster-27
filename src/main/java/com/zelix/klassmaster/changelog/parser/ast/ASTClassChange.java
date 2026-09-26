package com.zelix.klassmaster.changelog.parser.ast;

import com.zelix.klassmaster.changelog.AbstractChangeLog;
import com.zelix.klassmaster.changelog.ChangeLogClassKey;
import com.zelix.klassmaster.changelog.ChangeLogMemberKey;
import com.zelix.klassmaster.changelog.ClassNameNodeSetter;
import com.zelix.klassmaster.changelog.NewNameMapping;
import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.util.ListMultimap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.IOException;
import java.util.Map;

public class ASTClassChange extends ChangeLogEntryNode implements ClassNameNodeSetter {
    public ListMultimap methodChanges;
    public int childCount;
    public String oldClassName;
    private Map lineNumberChanges;
    public String newClassName;
    public String sourceName;
    public ListMultimap fieldChanges;

    public void setSourceName(String string) {
        this.sourceName = string;
    }

    public void addFieldChange(Object object, String string, String string1, Integer integer) {
        if (this.fieldChanges == null) {
            int ba = ZkmUtils.getPrimeCapacity(this.childCount);
            this.fieldChanges = new ListMultimap(ba);
        }

        this.fieldChanges.addValue(new ChangeLogClassKey(string, string1, integer), object);
    }

    @Override
    public void beginEntry(Object object, Object object1) {
        AbstractChangeLog abstractChangeLog = (AbstractChangeLog) object;
        this.childCount = (Integer) object1;
        abstractChangeLog.incrementClassCount();
    }

    @Override
    public void acceptClassName(Object object) {
        this.oldClassName = (String) object;
    }

    public void addLineNumberChange(Integer integer, Integer integer1) {
        if (this.lineNumberChanges == null) {
            this.lineNumberChanges = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(this.childCount));
        }

        this.lineNumberChanges.put(integer, integer1);
    }

    @Override
    public void applyToChangeLog(Object object) throws ZkmException, IOException {
        AbstractChangeLog abstractChangeLog = (AbstractChangeLog) object;
        AbstractChangeLog abstractChangeLog1;
        String string1;
        if (this.newClassName == null) {
            this.newClassName = this.oldClassName;
            abstractChangeLog1 = abstractChangeLog;
            string1 = this.newClassName;
        } else {
            abstractChangeLog1 = abstractChangeLog;
            string1 = this.newClassName;
        }

        abstractChangeLog1.addClassMapping(string1, this.oldClassName);
        if (this.sourceName != null) {
            abstractChangeLog.addSourceNameMapping(this.oldClassName, this.sourceName);
        }

        if (this.fieldChanges != null) {
            abstractChangeLog.addFieldMappings(this.oldClassName, this.fieldChanges);
        }

        if (this.methodChanges != null) {
            boolean bl = false;
            int ba = 27634284;
            char bb = '\ue1fb';
            ListMultimap listMultimap = this.methodChanges;
            String string = this.oldClassName;
            abstractChangeLog.addMethodMappings(string, listMultimap);
        }

        if (this.lineNumberChanges != null) {
            abstractChangeLog.addLineNumberMappings(this.oldClassName, this.lineNumberChanges);
        }
    }

    public void addMethodChange(
            String string,
            String string1,
            String string2,
            Integer integer,
            String string3,
            String string4,
            boolean bl,
            String string5,
            String string6,
            Boolean boolean1
    ) {
        String string8 = string1;
        String string7 = string4;
        if (this.methodChanges == null) {
            this.methodChanges = new ListMultimap(Math.min(40, this.childCount));
        }

        if (string8 == null) {
            string8 = "";
        }

        if (string7 == null) {
            string7 = "";
        }

        boolean bl1 = string5 != null && string5.length() > 0 || bl;
        this.methodChanges
                .addValue(new ChangeLogMemberKey(string, string8, string2, integer), new NewNameMapping(string3, string7, bl, string5, string6, bl1, boolean1));
    }

    public ASTClassChange() {
        super(7);
    }

    public void setNewClassName(String string) {
        this.newClassName = string;
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
