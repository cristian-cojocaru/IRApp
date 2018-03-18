package Start;

import LuceneFiles.LuceneTester;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class QueryController {
    @RequestMapping(value="/search", method=RequestMethod.GET)
    public void queryMethod(@RequestParam String query){
        System.out.println("you want to search = " + query);
        LuceneTester tester;
        try {
            tester = new LuceneTester();
            tester.createIndex();
            tester.search(query);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
