package LuceneFiles;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

    private IndexWriter writer;

    public Indexer(String indexDirectoryPath) throws IOException {
//        //this directory will contain the indexes
        Analyzer analyzer = new StandardAnalyzer();

        Directory directory = FSDirectory.open(Paths.get(indexDirectoryPath));

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(directory, config);
    }

    public void close() throws CorruptIndexException, IOException {
        writer.close();
    }

    private Document getDocument(File file) throws IOException {
        Document document = new Document();
        //IndexableFieldType indexableFile = (IndexableFieldType) new FileReader(file);
        IndexableFieldType indexableFile = new IndexableFieldType() {
            @Override
            public boolean stored() {
                return true;
            }

            @Override
            public boolean tokenized() {
                return false;
            }

            @Override
            public boolean storeTermVectors() {
                return false;
            }

            @Override
            public boolean storeTermVectorOffsets() {
                return false;
            }

            @Override
            public boolean storeTermVectorPositions() {
                return false;
            }

            @Override
            public boolean storeTermVectorPayloads() {
                return false;
            }

            @Override
            public boolean omitNorms() {
                return false;
            }

            @Override
            public IndexOptions indexOptions() {
                return null;
            }

            @Override
            public DocValuesType docValuesType() {
                return null;
            }

            @Override
            public int pointDimensionCount() {
                return 0;
            }

            @Override
            public int pointNumBytes() {
                return 0;
            }
        };
        //index file contents
        Field contentField = new Field(LuceneConstants.CONTENTS, "contentName" , indexableFile);
        //index file name
        Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(),  indexableFile  );
        //index file path
        Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(), indexableFile );

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
}