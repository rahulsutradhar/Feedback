package com.emplaceme.feedback.Extras;

/**
 * Created by developer on 11/01/16.
 */
public interface LinkDetails {

    public interface URL {

        public static final String URL_SERVER = "192.168.0.113";

        public static final String URL_OUTLET_LOGIN = "http://"+URL_SERVER+"/emplace/public/api/client/login";
        public static final String URL_EXISTING_OUTLET = "http://"+URL_SERVER+"/emplace/public/api/client";
        public static final String URL_EXISTING_USER = "http://"+URL_SERVER+"/emplace/public/api/client/check_existing_user";
        public static final String URL_FEEDBACK = "http://"+URL_SERVER+"/emplace/public/api/client/feedback";

    }
}
