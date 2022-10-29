package contants;

import static util.CommonConstants.FULL_SERVER_PATH;

public class AgentConstants {
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/client/agentLogin/agentLogin.fxml";
    public final static String LOGIN_PAGE_CSS_RESOURCE_LOCATION = "/client/agentLogin/login.css";
    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/client/agentMainPage/agentMainPage.fxml";

    public static final String GET_POSSIBLES_ALLIES = FULL_SERVER_PATH + "/getAllAllies";
    public static final String GET_CHOSEN_CONTEST = FULL_SERVER_PATH + "/allieContest";
    public static final String GET_ENIGMA_AND_DICTIONARY_FROM_ALLIE = FULL_SERVER_PATH + "/fetchDictionaryAndMachine";
    public static final String GET_MISSIONS = FULL_SERVER_PATH +  "/fetchMissionsRequest";

    public static final String UPDATE_CANDIDATE = FULL_SERVER_PATH +  "/updateCandidate";



}
