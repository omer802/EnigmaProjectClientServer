package client.constants;

import static util.CommonConstants.FULL_SERVER_PATH;

public class AlliesConstants {
    public final static String GET_CONTESTS = FULL_SERVER_PATH + "/contestsDataRequest";

    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/client/javafx/allies/allies.fxml";

    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/client/javafx/Login/AlliesLogin.fxml";
    public final static String LOGIN_PAGE_CSS_RESOURCE_LOCATION = "/client/javafx/Login/login.css";
    public final static int REFRESH_RATE = 500;


    public static final String UPDATE_ALLIE_CHOSEN_CONTEST = FULL_SERVER_PATH + "/updateAllieContest" ;
    public static final String GET_PARTICIPANT_ALLIES = FULL_SERVER_PATH + "/getSignedAllies" ;

    public static final String GET_SIGNED_AGENTS = FULL_SERVER_PATH + "/getAllAgentsSignedToAllie";

    public static final String UPDATE_MISSION_SIZE = FULL_SERVER_PATH + "/updateAllieMissionSize";



}
