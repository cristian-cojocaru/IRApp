
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"Start"})
public class Index {

    public static void main(String[] args) {
        SpringApplication.run(Index.class, args);
    }
}