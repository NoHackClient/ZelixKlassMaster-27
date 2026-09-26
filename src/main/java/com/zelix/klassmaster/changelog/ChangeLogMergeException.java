package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.exceptions.ZkmException;

public class ChangeLogMergeException extends ZkmException {
    public ChangeLogMergeException(String string) {
        super(string);
    }
}
