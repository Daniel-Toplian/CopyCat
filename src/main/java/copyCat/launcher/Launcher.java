package copyCat.launcher;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static copyCat.utils.Constants.CONFIG_PATH;

public class Launcher {

    private final Logger LOGGER = LogManager.getLogger(Launcher.class);

    public Future<Properties> init(){
        Promise<Properties> startPromise = Promise.promise();
        getConfig().onSuccess(startPromise::tryComplete).onFailure(startPromise::tryFail);
        return startPromise.future();
    }

    private Future<Properties> getConfig(){
        Promise<Properties> configPromise = Promise.promise();
        String configPath = System.getenv().getOrDefault(CONFIG_PATH, "./src/main/resources/env.properties");
        File configFile = new File(configPath);

        if (!configFile.exists()) {
            configPromise.tryFail("No config file found with path: %s".formatted(configPath));
            return configPromise.future();
        }

        try {
            LOGGER.info("Loading properties from: %s".formatted(configPath));
            Properties properties = new Properties();
            properties.load(new FileInputStream(configFile));
            configPromise.tryComplete(properties);
        } catch (IOException e) {
            configPromise.tryFail("Exception in loading properties from: %s. Error: %s".formatted(configPath, e.getMessage()));
        }
        return configPromise.future();
    }
}
