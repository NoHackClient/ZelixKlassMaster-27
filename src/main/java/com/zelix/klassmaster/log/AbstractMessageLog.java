package com.zelix.klassmaster.log;

import com.zelix.klassmaster.config.HiddenOptionFlags;
import com.zelix.klassmaster.exceptions.ZkmRuntimeException;
import com.zelix.klassmaster.util.SyncIndexedSet;
import com.zelix.klassmaster.util.ZkmUtils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractMessageLog {
    public int seriousErrorCount;
    public SyncIndexedSet loggedMessages;
    public PrintWriter logWriter;
    public int messageCount;
    public int warningCount;
    public int errorCount;
    public int pendingMarkerCount;
    public boolean throwOnFatalError;
    public SyncIndexedSet seriousErrors;
    public boolean verbose;

    public int getPendingMarkerCount() {
        return this.pendingMarkerCount;
    }

    public String getLoggedMessagesText() {
        if (this.loggedMessages == null) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        int ba = 0;

        for (Iterator iterator = this.loggedMessages.iterator(); iterator.hasNext(); ba++) {
            if (ba > 0) {
                stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            }

            stringBuilder.append((String) iterator.next());
        }

        return stringBuilder.toString();
    }

    public final void logWarning(String string) {
        this.logWarning(string, false);
    }

    public void logMessage(String string) {
        this.logMessage(string, false);
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    public final void logWarning(String string, boolean bl) {
        String string1 = (bl ? "\t" : "") + "WARNING:" + " " + string + this.takeMessageSuffix();
        if (this.loggedMessages == null) {
            this.loggedMessages = new SyncIndexedSet();
        }

        if (this.loggedMessages.add(string1)) {
            this.warningCount++;
            this.logWriter.println(string1);
        }
    }

    public final void logLine(String string) {
        String string1 = "\t" + string;
        this.logWriter.println(string1);
    }

    public List getLoggedMessages() {
        int ba = this.loggedMessages == null ? 0 : this.loggedMessages.size();
        ArrayList arrayList = new ArrayList(ba);
        if (this.loggedMessages != null) {
            Iterator iterator = this.loggedMessages.iterator();

            while (iterator.hasNext()) {
                String string = (String) iterator.next();
                arrayList.add(string);
            }
        }

        return arrayList;
    }

    public final void logMessage(String string, boolean bl) {
        String string1 = (bl ? "\t" : "") + "MESSAGE:" + " " + string + this.takeMessageSuffix();
        if (this.loggedMessages == null) {
            this.loggedMessages = new SyncIndexedSet();
        }

        if (this.loggedMessages.add(string1)) {
            this.messageCount++;
            this.logWriter.println(string1);
        }
    }

    public void clearLoggedMessages() {
        this.loggedMessages = null;
        this.seriousErrors = null;
    }

    public final void logFatalError(String string) {
        String string1 = "FATAL ERROR: " + string + this.takeMessageSuffix();
        System.err.println(string1);
        this.logWriter.println(string1);
        this.logWriter.close();
        if (this.throwOnFatalError) {
            throw new ZkmRuntimeException(string1);
        }

        System.exit(1);
    }

    public int getSeriousErrorCount() {
        return this.seriousErrorCount;
    }

    public PrintWriter getLogWriter() {
        return this.logWriter;
    }

    public void incrementPendingMarkerCount() {
        this.pendingMarkerCount++;
    }

    public AbstractMessageLog(boolean verbose) {
        this.verbose = verbose;
    }

    public String getSeriousErrorsText() {
        if (this.seriousErrors == null) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        int ba = 0;

        for (Iterator iterator = this.seriousErrors.iterator(); iterator.hasNext(); ba++) {
            if (ba > 0) {
                stringBuilder.append(HiddenOptionFlags.LINE_SEPARATOR);
            }

            stringBuilder.append((String) iterator.next());
        }

        return stringBuilder.toString();
    }

    public void closeLog() {
        this.logWriter.close();
    }

    public int getWarningCount() {
        return this.warningCount;
    }

    public final void logError(String string) {
        this.logError(string, false);
    }

    public int decrementPendingMarkerCount() {
        return this.pendingMarkerCount--;
    }

    public int getErrorCount() {
        return this.errorCount;
    }

    public final void logError(String string, boolean bl) {
        String string1 = (bl ? "\t" : "") + "ERROR:" + " " + string + this.takeMessageSuffix();
        if (this.loggedMessages == null) {
            this.loggedMessages = new SyncIndexedSet();
        }

        if (this.loggedMessages.add(string1)) {
            this.logWriter.println(string1);
            this.errorCount++;
        }
    }

    public String takeMessageSuffix() {
        if (this.getPendingMarkerCount() > 0) {
            this.decrementPendingMarkerCount();
            return HiddenOptionFlags.DOT_MESSAGE_SUFFIX ? " ." : " ";
        } else {
            return "";
        }
    }

    public final void logSeriousError(String string) {
        String string1 = "" + "ERROR:" + " " + string + this.takeMessageSuffix();
        if (this.loggedMessages == null) {
            this.loggedMessages = new SyncIndexedSet();
        }

        if (this.seriousErrors == null) {
            this.seriousErrors = new SyncIndexedSet();
        }

        if (this.loggedMessages.add(string1)) {
            this.logWriter.println(string1);
            this.seriousErrors.add(string1);
            this.seriousErrorCount++;
        }
    }

    public static String buildGeneratedByHeader(String string) {
        return ZkmUtils.createCommentBox(new String[]{"Generated by Zelix KlassMaster 27.0.0 " + string + " " + ZkmUtils.getTimestamp()});
    }

    public final void logSeriousError_v(String string) {
        this.logSeriousError(string);
    }

    public int getMessageCount() {
        return this.messageCount;
    }
}
