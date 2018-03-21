package LuceneFiles;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import RoAnalyzer.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer{

    private IndexWriter writer;

    Indexer(String indexDirectoryPath) throws IOException {
//        //this directory will contain the indexes
        Analyzer analyzer = new RoAnalyzer();
        Directory directory = FSDirectory.open(Paths.get(indexDirectoryPath));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(directory, config);
    }


    private Document getDocument(File file) throws IOException {
        Document document = new Document();

        Field contentField = new Field(LuceneConstants.CONTENTS, new FileReader(file), TextField.TYPE_NOT_STORED);
        Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(), TextField.TYPE_STORED);
        Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(),TextField.TYPE_STORED );

        document.add(contentField);
        document.add(fileNameField);
        document.add(filePathField);

        return document;
    }

    private void indexFile(File file) throws IOException {
        System.out.println("Indexing "+file.getCanonicalPath());
        Document document = getDocument(file);
        writer.addDocument(document);
    }

    public int createIndex(String dataDirPath, FileFilter filter)
            throws IOException {
        //get all files in the data directory
        File[] files = new File(dataDirPath).listFiles();

        for (File file : files) {
            if(!file.isDirectory()
                    && !file.isHidden()
                    && file.exists()
                    && file.canRead()
                    && filter.accept(file)
                    ){
                indexFile(file);
            }
        }
        return writer.numDocs();
    }

    public void close() throws IOException {
        writer.close();
    }
}