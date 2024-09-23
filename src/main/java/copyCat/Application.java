package copyCat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
public class Application {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        String port = run(Application.class, args).getEnvironment().getProperty("server.port");
        LOGGER.info("#######################################################");
        LOGGER.info("      CopyCat is up and running on port: {}", port);
        LOGGER.info("#######################################################");
    }
}
