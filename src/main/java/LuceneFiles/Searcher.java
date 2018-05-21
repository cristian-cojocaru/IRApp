package LuceneFiles;

import RoAnalyzer.RoAnalyzer;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.Token;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;
import org.tartarus.snowball.ext.RomanianStemmer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.log10;

public class Searcher {
    private IndexReader indexReader;
    private IndexSearcher indexSearcher;
    private MultiFieldQueryParser queryParser;

    public Searcher(String indexDirectoryPath) throws IOException {
        Analyzer analyzer = new RoAnalyzer(LuceneConstants.customSW);
        Directory directory =  FSDirectory.open(Paths.get(indexDirectoryPath));
        indexReader = DirectoryReader.open(directory);
        indexSearcher = new IndexSearcher(indexReader);
        queryParser = new MultiFieldQueryParser(new String[] {LuceneConstants.CONTENTS, "importantText"}, analyzer);
    }

    public TopDocs search( String searchQuery) throws IOException, ParseException {
        Query query = queryParser.parse(searchQuery);
        TopDocs topDocs = indexSearcher.search(query, LuceneConstants.MAX_SEARCH);

        for (int i = 0; i < topDocs.scoreDocs.length; ++i) {
            int docId = topDocs.scoreDocs[i].doc;
            System.out.println("\n" + getDocument(topDocs.scoreDocs[i]).getField(LuceneConstants.FILE_NAME).stringValue());
            System.out.println();
            String[] splittedQuery = searchQuery.split(" ");
            RomanianStemmer tokenizer = new RomanianStemmer();

            List<String> wordsRoot = new ArrayList<>();
            for(String word : splittedQuery){
                tokenizer.setCurrent(word);
                tokenizer.stem();
                wordsRoot.add(tokenizer.getCurrent());
            }

            for (String word : wordsRoot) {
                TFIDF(docId, word, indexReader);
            }
        }
        return topDocs;
    }


    private void TFIDF(int docId, String queryTerm, IndexReader reader) throws IOException {
        Fields fields = MultiFields.getFields(reader);
        for (String field : fields) {
            if (field.equals(LuceneConstants.CONTENTS)) {
                Terms terms = fields.terms(field);
                TermsEnum termsEnum = terms.iterator();
                while (termsEnum.next() != null) {
                    if (termsEnum.term().utf8ToString().equals(queryTerm)) {
                        int freq = currentDocFreq(docId, termsEnum.term(), reader);
                        double idf = Math.log10(reader.numDocs() / termsEnum.docFreq());
                        double tf = freq == 0 ? 0 : 1 + Math.log10(freq);
                        System.out.println("\twordRoot = "+ queryTerm + "   TF = " + tf + " IDF = " + idf);
                    }
                }
            }
        }
    }

    private int currentDocFreq(int docID, BytesRef term, IndexReader reader) throws IOException {
        PostingsEnum docEnum = MultiFields.getTermDocsEnum(reader, LuceneConstants.CONTENTS, term);
        int doc = PostingsEnum.NO_MORE_DOCS;
        while ((doc = docEnum.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
            if (docEnum.docID() == docID) {
                return docEnum.freq();
            }
        }
        return 0;
    }

    public Document getDocument(ScoreDoc scoreDoc) throws IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }

    public void close() throws IOException {
        this.indexReader.close();
    }
}
