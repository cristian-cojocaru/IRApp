package LuceneFiles;

import java.io.File;
import java.io.FileFilter;

public class Filter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
        if(pathname.getName().toLowerCase().endsWith(".txt") ||
                pathname.getName().toLowerCase().endsWith(".pdf") ||
                pathname.getName().toLowerCase().endsWith(".docx"))
            return true;
        return false;
    }

}