package Start;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueryController {
    @RequestMapping(value="/search", method=RequestMethod.GET)
    public void queryMethod(@RequestParam String query){
        System.out.println("you want to search = " + query);
    }
}
