package Start;

import LuceneFiles.LuceneConstants;
import LuceneFiles.Searcher;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class QueryController {
    @RequestMapping(value="/search", method=RequestMethod.GET)
    public String queryMethod(Model model, @RequestParam String query){
        List<Map<String, String>> files = new ArrayList<Map<String, String>>();
//        List<String> filesList = new ArrayList<>();
//        List<String> filesContent = new ArrayList<>();
        Searcher searcher;
        try {
            searcher = new Searcher(LuceneConstants.indexDir);
            long startTime = System.currentTimeMillis();
            TopDocs hits = searcher.search(query);
            long endTime = System.currentTimeMillis();

            System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc);
//                filesList.add(doc.get(LuceneConstants.FILE_NAME) + " ===> "+ doc.get(LuceneConstants.FILE_PATH));
//                filesContent.add(doc.get(LuceneConstants.CONTENTS));
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", doc.get(LuceneConstants.FILE_NAME));
                map.put("content", doc.get(LuceneConstants.CONTENTS));
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
