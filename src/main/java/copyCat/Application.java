package copyCat;

import copyCat.launcher.Launcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);
    private static Properties properties = new Properties();

    public static void main(String[] args) {
        Launcher launcher = new Launcher();
        launcher.init().onSuccess(properties -> {
            Application.properties = properties;
            run(Application.class, args);
        }).onFailure(error -> {
            LOGGER.error("Failed to start. Error: {}", error.getMessage());
            System.exit(1);
        });
    }

    public static Properties properties(){
        return properties;
    }
}
