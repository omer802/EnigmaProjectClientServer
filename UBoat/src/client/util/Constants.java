package client.util;

import com.google.gson.Gson;

public class Constants {
    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/client/javafx/UBoatMainController/UBoat.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/client/javafx/Login/UBoatLogin.fxml";
    public final static String LOGIN_PAGE_CSS_RESOURCE_LOCATION = "/client/javafx/Login/login.css";
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";

    private final static String CONTEXT_PATH = "/enigmaApp_Web_exploded";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginRequest";
    public final static String UPLOAD_XML_FILE = FULL_SERVER_PATH + "/upload-file";
    public final static String SEND_CODE_CONFIGURATION = FULL_SERVER_PATH + "/setCodeConfiguration";

    public final static String GENERATE_RANDOM_CODE_CONFIGURATION = FULL_SERVER_PATH + "/randomCode";
    public final static String FETCH_TRIE = FULL_SERVER_PATH + "/fetchTrie";

    public final static Gson GSON_INSTANCE = new Gson();

}
