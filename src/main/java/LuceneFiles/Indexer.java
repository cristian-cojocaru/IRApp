package LuceneFiles;

import RoAnalyzer.RoAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;

public class Indexer{

    private IndexWriter writer;

    Indexer(String indexDirectoryPath) throws IOException {
        Analyzer analyzer = new RoAnalyzer(LuceneConstants.customSW);
        Directory directory = FSDirectory.open(Paths.get(indexDirectoryPath));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(directory, config);
    }

    private String extractContent(File file) throws IOException, TikaException {
        return new Tika().parseToString(file);
    }

    private String getImportantText(int floor, int ceil, String text){
        String[] parts = text.split(" ");
        if(ceil > parts.length)
            ceil = parts.length;
        if(floor > ceil)
            return null;

        StringBuilder stringBuilder = new StringBuilder();
        for(int i=floor;i<ceil;i++){
            stringBuilder.append(parts[i] + " ");
        }
        return stringBuilder.toString();
    }

    private Document getDocument(File file) throws IOException, TikaException, SAXException {
        Document document = new Document();

        Field contentField = new Field(LuceneConstants.CONTENTS, extractContent(file), TextField.TYPE_STORED);
        Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(), TextField.TYPE_STORED);
        Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(),TextField.TYPE_STORED );

        String text;
        int floor = 0, ceil = 10;
        if((text = getImportantText(floor, ceil, extractContent(file)))!=null) {
            Field importantField = new Field(LuceneConstants.IMPORTANT_TEXT, text, TextField.TYPE_NOT_STORED);
            System.out.println(importantField.stringValue());
            document.add(importantField);
        }

        document.add(contentField);
        document.add(fileNameField);
        document.add(filePathField);

        return document;
    }

    private void indexFile(File file) throws IOException, TikaException, SAXException {
        System.out.println("Indexing "+file.getCanonicalPath());
        Document document = getDocument(file);
        writer.addDocument(document);
    }

    public int createIndex(String dataDirPath, FileFilter filter) throws IOException, TikaException, SAXException {
        File[] files = new File(dataDirPath).listFiles();
        for (File file : files) {
            if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)){
                indexFile(file);
            }
        }
        return writer.numDocs();
    }

    public void close() throws IOException {
        writer.close();
    }
}