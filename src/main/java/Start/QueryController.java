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
import java.util.List;

@Controller
public class QueryController {
    @RequestMapping(value="/search", method=RequestMethod.GET)
    public String queryMethod(Model model, @RequestParam String query){
        System.out.println("\nyou want to search = " + query);
        List<String> filesList = new ArrayList<>();
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
                filesList.add(doc.get(LuceneConstants.FILE_PATH));
            }
            model.addAttribute("fileName", filesList);
            searcher.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "index";
    }
}
