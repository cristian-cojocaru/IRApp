package LuceneFiles;

import RoAnalyzer.RoAnalyzer;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.file.Paths;

public class Indexer{

    private IndexWriter writer;


    public String getContentDocx(File file) throws IOException, TikaException, SAXException {

        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(file);
        ParseContext pcontext = new ParseContext();

        OOXMLParser msofficeparser = new OOXMLParser ();
        msofficeparser.parse(inputstream, handler, metadata,pcontext);

        return handler.toString();
    }

    public String getContentPdf(File file) throws IOException, TikaException, SAXException {

        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(file);
        ParseContext pcontext = new ParseContext();

        //parsing the document using PDF parser
        PDFParser pdfparser = new PDFParser();
        pdfparser.parse(inputstream, handler, metadata,pcontext);

        return handler.toString();
    }

    Indexer(String indexDirectoryPath) throws IOException {
        Analyzer analyzer = new RoAnalyzer(LuceneConstants.customSW);
        Directory directory = FSDirectory.open(Paths.get(indexDirectoryPath));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(directory, config);
    }

    private Document getDocument(File file) throws IOException, TikaException, SAXException {
        Document document = new Document();
        Field contentField;
        switch (FilenameUtils.getExtension(file.getName())){
            case "txt":
                System.out.println("txt");
                contentField = new Field(LuceneConstants.CONTENTS, new FileReader(file), TextField.TYPE_NOT_STORED);
                document.add(contentField);
                break;
            case "pdf": {
                contentField = new Field(LuceneConstants.CONTENTS, new StringReader(getContentPdf(file)), TextField.TYPE_NOT_STORED);
                document.add(contentField);
            }break;
            case "docx": {
                contentField = new Field(LuceneConstants.CONTENTS, new StringReader(getContentDocx(file)), TextField.TYPE_NOT_STORED);
                document.add(contentField);
            }
        }

        Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(), TextField.TYPE_STORED);
        Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(),TextField.TYPE_STORED );
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