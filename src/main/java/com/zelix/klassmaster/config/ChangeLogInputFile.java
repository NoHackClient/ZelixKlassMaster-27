package com.zelix.klassmaster.config;

import java.io.Reader;
import java.io.Serializable;

public class ChangeLogInputFile implements Serializable {
    public transient Reader b;
    public final String a;

    public Reader getReader() {
        return this.b;
    }

    public String getFileName() {
        return this.a;
    }

    public ChangeLogInputFile(String string) {
        this.a = string;
    }

    public void setReader(Reader reader1) {
        this.b = reader1;
    }
}
