package com.zelix.klassmaster.script;

import com.zelix.klassmaster.util.ZkmStringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ProcessOutputPump extends Thread {
    public InputStream inputStream;
    public PrintWriter logWriter;
    public String linePrefix;
    public boolean isErrorStream;

    public ProcessOutputPump(InputStream inputStream1, PrintWriter printWriter, int ba, boolean isErrorStream) {
        this.inputStream = inputStream1;
        this.logWriter = printWriter;
        if (isErrorStream) {
            this.linePrefix = ZkmStringUtils.pad("ERROR: ", 82, ba, 32);
        } else {
            this.linePrefix = ZkmStringUtils.pad("", 76, ba, 32);
        }

        this.isErrorStream = isErrorStream;
    }

    @Override
    public void run() {
        boolean bl = false;

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(this.inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String string;
            while ((string = bufferedReader.readLine()) != null) {
                bl = true;
                this.logWriter.println(this.linePrefix + string);
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        if (bl) {
            System.out.println(this.linePrefix + "Output generated and written to log.");
        }
    }
}
