package Start;

import LuceneFiles.LuceneConstants;
import LuceneFiles.Searcher;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.apache.lucene.search.ScoreDoc;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import static java.lang.Math.log10;

@Controller
public class QueryController {
    @RequestMapping(value="/search", method=RequestMethod.GET)
    public String queryMethod(Model model, @RequestParam String query) {
        List<Map<String, String>> files = new ArrayList<Map<String, String>>();
        Searcher searcher;

        try {
            searcher = new Searcher(LuceneConstants.indexDir);
            long startTime = System.currentTimeMillis();
            TopDocs hits = searcher.search(query);
            long endTime = System.currentTimeMillis();

            System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));

            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc);
                Map<String, String> map = new HashMap<>();
                map.put("name", "File name: " + doc.get(LuceneConstants.FILE_NAME));
                map.put("content", doc.get(LuceneConstants.CONTENTS));
                DecimalFormat df = new DecimalFormat("##.###");
                map.put("score", "Score: " + df.format(scoreDoc.score * 100));
                System.out.println(doc.get(LuceneConstants.FILE_NAME) + " " + scoreDoc.score * 100);
                files.add(map);
            }

            model.addAttribute("files", files);
            searcher.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return "index";
    }
}
