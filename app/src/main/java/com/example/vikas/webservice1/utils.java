package com.example.vikas.webservice1;

import android.widget.EditText;

/**
 * Created by Kiran on 16-02-2016.
 */
public class utils {
    final static String EMAILPATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static String android_id;
    public static String urlRegistration = "http://app.byji.com/byji_std/v1/dev/api/v1/appuser/register";
    public static String urlLogin = "http://app.byji.com/byji_std/v1/dev/api/v1/appuser/login";
    public static String urlProfile = "http://app.byji.com/byji_std/v1/dev/api/v1/appuser/profile_details?source=android&device_key=354865078151188";
    public static String urlProfileUpdate = " http://app.byji.com/byji_std/v1/dev/api/v1/appuser/update_profile";
    public static String urlChangePassword = " http://app.byji.com/byji_std/v1/dev/api/v1/appuser/change_password";
    public static String urlForgotPassword = "http://app.byji.com/byji_std/v1/dev/api/v1/appuser/forgot_password";
    public static String urlSocialLogin = "http://app.byji.com/byji_std/v1/dev/api/v1/appuser/social_login";
    public static String latitude = "13.6236614";
    public static String longititude = "79.4312654";
    public static String accracy = "20.0";
    public static String provider = "network";
    public static String user_access_token;
    public static String app_authorization_access_token;
    public static String firstname, lastname, email, facebookid, propic;
    public static String helloworld_program;

    public static boolean hasContent(EditText ed) {
        if (ed.getText().toString().trim().length() > 0) {
            return true;
        }
        return false;
    }

    public static boolean hasEmailPattern(EditText ed) {
        if (ed.getText().toString().matches(EMAILPATTERN)) {
            return true;
        }
        return false;
    }

    public static boolean matchpassword(EditText password, EditText changePassword) {
        if (password.getText().toString().equals(changePassword.getText().toString())) {
            return true;
        }
        return false;
    }

    public static boolean passwordLength(EditText ed) {
        if (ed.getText().toString().length() >= 8) {
            return true;
        }
        return false;
    }

    public static char[] md5Password() {
        return null;
    }

}

