package util;


import com.google.gson.Gson;

public class CommonConstants {

    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/enigmaApp_Web_exploded";
    public final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;
    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginRequest";
    public final static String GET_CANDIDATE = FULL_SERVER_PATH + "/getCandidate";
    public final static String GET_CONTEST_STATUS = FULL_SERVER_PATH + "/getContestStatus";

    public final static Gson GSON_INSTANCE = new Gson();
    public final static int MAX_AMOUNT_ERROR_TO_SHOW = 3;
    public final static int REFRESH_RATE = 500;
    public final static String MAKE_CLIENT_READY = FULL_SERVER_PATH + "/makeClientReady";
    public final static String UPDATE_AGENT_PROGRESS = FULL_SERVER_PATH + "/updateAgentProgress";
    public final static String GET_AGENT_AND_DM_PROGRESS = FULL_SERVER_PATH + "/getAgentsAndDMProgress";
    public final static String GET_WINNER = FULL_SERVER_PATH + "/getWinnerServlet";



}
