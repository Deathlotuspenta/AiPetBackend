import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EnvRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        System.out.println("WX_APPID=" + System.getenv("WX_APPID"));
    }
}