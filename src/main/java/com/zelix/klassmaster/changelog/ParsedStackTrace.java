package com.zelix.klassmaster.changelog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class ParsedStackTrace {
    public String text;
    public List frameLines;

    public ParsedStackTrace(String string) throws StackTraceTranslateException {
        this.text = string;
        BufferedReader bufferedReader = new BufferedReader(new StringReader(this.text));
        this.frameLines = new ArrayList();

        try {
            String string1;
            while ((string1 = bufferedReader.readLine()) != null) {
                if (string1.trim().length() > 0) {
                    this.frameLines.add(new StackTraceFrameLine(string1));
                }
            }
        } catch (IOException iOException) {
            throw new StackTraceTranslateException(iOException.toString());
        }
    }

    public int getLineCount() {
        return this.frameLines.size();
    }

    public Enumeration enumerateReversed() {
        ArrayList arrayList = new ArrayList(this.frameLines);
        Collections.reverse(arrayList);
        return Collections.enumeration(arrayList);
    }
}
