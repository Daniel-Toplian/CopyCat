package copyCat;

import copyCat.launcher.Launcher;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
public class Application {

    private static Properties properties = new Properties();

    public static void main(String[] args) {
        Launcher launcher = new Launcher();
        launcher.init().onSuccess(properties -> {
            Application.properties = properties;
            run(Application.class, args);
        }).onFailure(error -> {
            System.out.println("Failed to start. Error: " + error.getMessage());
            System.exit(1);
        });
    }

    public static Properties properties(){
        return properties;
    }
}
