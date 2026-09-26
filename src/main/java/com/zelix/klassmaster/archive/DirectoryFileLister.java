package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.ui.component.DirectoryFilenameFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DirectoryFileLister {
    public File rootDirectory;
    private static final String CREATE_DIR_ERROR_PREFIX = "Couldn't create directory '";

    public List listSubdirectories() {
        ArrayList arrayList = new ArrayList();
        this.collectSubdirectories(this.rootDirectory, arrayList);
        return arrayList;
    }

    public void collectSubdirectories(File file1, List list1) {
        String[] strings = file1.list(new DirectoryFilenameFilter());
        int ba = strings != null ? strings.length : 0;

        for (int i = 0; i < ba; i++) {
            File file2 = new File(file1, strings[i]);
            list1.add(file2.getAbsolutePath());
            this.collectSubdirectories(file2, list1);
        }
    }

    public DirectoryFileLister(File file1) {
        this.rootDirectory = file1;
    }

    public static void ensureDirectoryExists(String string) throws IOException {
        if (string != null) {
            File file1 = new File(string);
            if (!file1.exists() && !file1.mkdirs()) {
                throw new IOException(CREATE_DIR_ERROR_PREFIX + string + "'");
            }
        }
    }
}
