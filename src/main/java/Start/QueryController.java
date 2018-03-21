package Start;

import LuceneFiles.LuceneConstants;
import LuceneFiles.Searcher;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class QueryController {
    @RequestMapping(value="/search", method=RequestMethod.GET)
    public void queryMethod(@RequestParam String query){
        System.out.println("\nyou want to search = " + query);

        //start searching the key
        Searcher searcher;
        try {
            searcher = new Searcher(LuceneConstants.indexDir);
            long startTime = System.currentTimeMillis();
            TopDocs hits = searcher.search(query);
            long endTime = System.currentTimeMillis();

            System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc);
                System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
                //System.out.println("File content : " + doc.get(LuceneConstants.CONTENTS));
            }
            searcher.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
