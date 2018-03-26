package LuceneFiles;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class StartIndexing {
    private static void deleteContentFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteContentFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static void main(String[] args) throws TikaException, SAXException {
        Indexer indexer;
        deleteContentFolder(new File(LuceneConstants.indexDir));
        try {
            indexer = new Indexer(LuceneConstants.indexDir);
            int numIndexed;
            long startTime = System.currentTimeMillis();
            numIndexed = indexer.createIndex(LuceneConstants.dataDir, new Filter());
            long endTime = System.currentTimeMillis();
            indexer.close();
            System.out.println(numIndexed+" Files indexed, time taken: " +(endTime-startTime)+" ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
