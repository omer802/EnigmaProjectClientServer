package client.constants;

import static util.CommonConstants.FULL_SERVER_PATH;

public class ConstantsUBoat {
    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/client/javafx/UBoatMainController/UBoat.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/client/javafx/Login/UBoatLogin.fxml";
    public final static String LOGIN_PAGE_CSS_RESOURCE_LOCATION = "/client/javafx/Login/login.css";

    public final static String UPLOAD_XML_FILE = FULL_SERVER_PATH + "/upload-file";
    public final static String SEND_CODE_CONFIGURATION = FULL_SERVER_PATH + "/setCodeConfiguration";

    public final static String GENERATE_RANDOM_CODE_CONFIGURATION = FULL_SERVER_PATH + "/randomCode";
    public final static String FETCH_DICTIONARY = FULL_SERVER_PATH + "/fetchDictionary";

    public final static String MAKE_UBOAT_READY = FULL_SERVER_PATH + "/makeUBoatReady";

    public final static String RESET_CODE = FULL_SERVER_PATH + "/resetCode";
    public final static String ENCRYPT_MESSAGE = FULL_SERVER_PATH + "/encryptMessage";
    public final static String GET_SIGNED_ALLIES = FULL_SERVER_PATH + "/getSignedAllies";

    public final static int REFRESH_RATE = 500;
}
