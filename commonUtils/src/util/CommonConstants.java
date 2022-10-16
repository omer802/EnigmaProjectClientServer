package util;


import com.google.gson.Gson;

public class CommonConstants {
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/enigmaApp_Web_exploded";
    public final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;
    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginRequest";

    public final static Gson GSON_INSTANCE = new Gson();
    public final static int MAX_AMOUNT_ERROR_TO_SHOW = 3;

}
