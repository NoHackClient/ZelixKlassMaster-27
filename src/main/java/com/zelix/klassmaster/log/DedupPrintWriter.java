package com.zelix.klassmaster.log;

import com.zelix.klassmaster.util.ZkmUtils;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;

public class DedupPrintWriter extends PrintWriter {
    public HashMap printedLines = ZkmUtils.createHashMap();

    @Override
    public void println(String string) {
        if (this.printedLines.put(string, string) == null) {
            super.println(string);
        }
    }

    public DedupPrintWriter(Writer writer1) {
        super(writer1, true);
    }

    public DedupPrintWriter(OutputStream outputStream) {
        super(outputStream);
    }
}
