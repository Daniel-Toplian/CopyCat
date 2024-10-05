package copyCat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
public class Application {

    @Autowired
    private ConfigurableEnvironment env;
    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        Application app = run(Application.class, args).getBean(Application.class);
        app.startupMessage();
    }

    private void startupMessage() {
        StringBuilder propertiesLog = new StringBuilder();
        propertiesLog.append("\nStarting CopyCat application with properties:\n");

        env.getPropertySources().stream().filter(source -> source.getName().contains("application.properties")).findFirst().ifPresent(source -> {
            if (source.getSource() instanceof java.util.Map) {
                for (Object key : ((java.util.Map<?, ?>) source.getSource()).keySet()) {
                    propertiesLog.append(key).append("=")
                            .append(env.getProperty(key.toString())).append("\n");
                }
            }
        });

        LOGGER.info(propertiesLog.toString());
        LOGGER.info("#######################################################");
        LOGGER.info("      CopyCat is up and running on port: {}", env.getProperty("server.port"));
        LOGGER.info("#######################################################");
    }
}
