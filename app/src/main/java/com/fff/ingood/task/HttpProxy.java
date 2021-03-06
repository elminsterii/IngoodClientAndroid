package com.fff.ingood.task;

/**
 * Created by ElminsterII on 2018/6/11.
 */

public class HttpProxy {

    //private static final String SERVER_URL = "https://hangouttw.appspot.com";
    private static final String SERVER_URL = "https://ingoodtw.appspot.com";
    public static final String HTTP_POST_API_REGISTER = SERVER_URL + "/register";
    public static final String HTTP_POST_API_UNREGISTER = SERVER_URL + "/unregister";
    public static final String HTTP_POST_API_LOGIN = SERVER_URL + "/login";
    public static final String HTTP_POST_API_LOGOUT = SERVER_URL + "/logout";
    public static final String HTTP_POST_API_VERIFY_EMAIL = SERVER_URL + "/verifyemail";
    public static final String HTTP_POST_API_PERSON_QUERY = SERVER_URL + "/queryperson";
    public static final String HTTP_POST_API_PERSON_UPDATE = SERVER_URL + "/updateperson";
    public static final String HTTP_POST_API_PERSON_CHECK_EXIST = SERVER_URL + "/checkpersonexist";
    public static final String HTTP_POST_API_PERSON_SAVE_ACTIVITY = SERVER_URL + "/saveactivity";
    public static final String HTTP_API_PERSON_ICON_ACCESS = SERVER_URL + "/accesspersonicon";
    public static final String HTTP_POST_API_PERSON_ICON_DELETE = SERVER_URL + "/deletepersonicon";
    public static final String HTTP_POST_API_PERSON_TEMP_PASSWORD = SERVER_URL + "/temppassword";
    public static final String HTTP_POST_API_ACTIVITY_CREATE = SERVER_URL + "/createactivity";
    public static final String HTTP_POST_API_ACTIVITY_DELETE = SERVER_URL + "/deleteactivity";
    public static final String HTTP_POST_API_ACTIVITY_QUERY = SERVER_URL + "/queryactivity";
    public static final String HTTP_POST_API_ACTIVITY_UPDATE = SERVER_URL + "/updateactivity";
    public static final String HTTP_POST_API_ACTIVITY_REPUBLISH = SERVER_URL + "/republishactivity";
    public static final String HTTP_POST_API_ACTIVITY_QUERY_ID_BY = SERVER_URL + "/queryactivityidby";
    public static final String HTTP_POST_API_ACTIVITY_DEEM = SERVER_URL + "/deemactivity";
    public static final String HTTP_POST_API_ACTIVITY_ATTEND = SERVER_URL + "/attendactivity";
    public static final String HTTP_POST_API_ACTIVITY_OFFER_TOOK = SERVER_URL + "/offertookactivity";
    public static final String HTTP_API_ACTIVITY_IMAGE_ACCESS = SERVER_URL + "/accessactivityimage";
    public static final String HTTP_POST_API_ACTIVITY_IMAGE_DELETE = SERVER_URL + "/deleteactivityimage";
    public static final String HTTP_POST_API_COMMENT_CREATE = SERVER_URL + "/createcomment";
    public static final String HTTP_POST_API_COMMENT_QUERY = SERVER_URL + "/querycomment";
    public static final String HTTP_POST_API_COMMENT_QUERY_ID_BY = SERVER_URL + "/querycommentidby";
    public static final String HTTP_POST_API_COMMENT_UPDATE = SERVER_URL + "/updatecomment";
    public static final String HTTP_POST_API_COMMENT_DELETE = SERVER_URL + "/deletecomment";


    public static final int HTTP_POST_TIMEOUT = 10;
}
