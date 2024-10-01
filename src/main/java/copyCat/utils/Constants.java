package copyCat.utils;

public class Constants {

    // --- urls ---
    public static final String BASE_ROUTE = "/api/v1/";

    // --- config ---
    public static final String SERVER_PORT = "server.port";
    public static final String RECOVERY_TYPE = "recovery.type";
    public static final String RECOVERY_FILE_PATH = "recovery.file.path";
    public static final String MONGO_RECOVERY = "mongodb";
    public static final String FILE_RECOVERY = "file";

    // --- mongodb ---
    public static final String MONGODB_PREFIX = "spring.data.mongodb.";
    public static final String MONGODB_USERNAME = MONGODB_PREFIX + "username";
    public static final String MONGODB_PASSWORD = MONGODB_PREFIX + "password" ;
    public static final String MONGODB_PORT = MONGODB_PREFIX + "port";
    public static final String MONGODB_HOST = MONGODB_PREFIX + "host";
    public static final String MONGODB_DATABASE = MONGODB_PREFIX + "database";
    public static final String MONGODB_AUTH_DATABASE = MONGODB_PREFIX + "authentication-database";

    // --- messages ---
    public static final String API_NOT_FOUND = "API Mock not found for URL: %s";
}
