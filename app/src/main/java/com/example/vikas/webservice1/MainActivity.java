package com.example.vikas.webservice1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.internal.Utility;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    TextView tvsignup, tvsignin;
    Button Facebook;
    ProgressDialog progress;
    CallbackManager callbackManager;
    HashMap<String, String> deviseHas, userHash, locationH;
    JSONObject userJSON, deviseJSON, mainJSON, locationJSON;
    String DateTime;
    ProfileTracker profileTracker;
    AccessToken accessToken;
    AccessTokenTracker accessTokenTracker;
    GraphResponse response_one;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvsignup = (TextView) findViewById(R.id.tv_signup);
        tvsignin = (TextView) findViewById(R.id.tv_signin);
        Facebook = (Button) findViewById(R.id.btn_fb_signin);
        deviseHas = new HashMap<>();
        userHash = new HashMap<>();
        locationH = new HashMap<>();
        mainJSON = new JSONObject();
        progress = new ProgressDialog(MainActivity.this);
        progress.setMessage(getResources().getString(R.string.please_wait_facebooklogin));
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                accessToken = currentAccessToken;
            }
        };
        accessTokenTracker.startTracking();
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Profile.fetchProfileForCurrentAccessToken();
                profileTracker.stopTracking();
            }
        };
        profileTracker.startTracking();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (loginResult != null)
                    fetchDetails();
            }

            @Override
            public void onCancel() {
                Log.e("facebook onCancel", "");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("facebook error", error.toString());
            }
        });

        tvsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        tvsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
        Facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends", "email"));
            }
        });
    }

    private void fetchDetails() {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.e("object", object + "");
                Log.e("response", response + "");
                if (object != null) {
                    try {
                        utils.propic = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");
                        utils.facebookid = response.getJSONObject().getString("id");
                        utils.lastname = response.getJSONObject().getString("last_name");
                        utils.firstname = response.getJSONObject().getString("id");
                        utils.email = response.getJSONObject().getString("email");
                        socialRequst(loginDetilas());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,picture,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private JSONObject loginDetilas() {
        try {
            userHash.put("profile_picture_url", utils.propic);
            userHash.put("profile_picture", "");
            userHash.put("social_auth_id", utils.facebookid);
            userHash.put("source", "android");
            userHash.put("login_type", "google");
            userHash.put("last_name", utils.lastname);
            userHash.put("email", utils.email);
            userHash.put("first_name", utils.firstname);
            userJSON = new JSONObject(userHash);
            deviseJSON = new JSONObject(deviseData());
            locationJSON = new JSONObject(locationData());
            mainJSON.put("device", deviseJSON);
            mainJSON.put("user", userJSON);
            mainJSON.put("location", locationJSON);
            Log.d("mainJson", "" + mainJSON.toString());
            return mainJSON;
        } catch (JSONException e) {
            Log.d("error", e.getMessage());
        }
        return null;
    }

    private HashMap locationData() {
        locationH.put("latitude", utils.latitude);
        locationH.put("longitude", utils.longititude);
        locationH.put("accuracy", utils.accracy);
        locationH.put("provider", utils.provider);
        return locationH;
    }

    public HashMap deviseData() {
        TimeZone tz = TimeZone.getDefault();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTime = sdf.format(c.getTime());
        DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
        Date date = null;
        String output = null;
        try {
            date = sdf.parse(DateTime);
            output = outputformat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        deviseHas.put("time_zone", "" + tz.getID());
        deviseHas.put("device_os", "Android");
        deviseHas.put("device_os_version", Build.VERSION.RELEASE);
        deviseHas.put("device_gcm_token", "android");
        deviseHas.put("device_model", Build.MODEL.toString());
        deviseHas.put("date_time", output);
        deviseHas.put("device_manufacture", Build.MANUFACTURER.toString());
        deviseHas.put("device_key", Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID));
        return deviseHas;
    }

    public void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void socialRequst(JSONObject main) {
        JsonObjectRequest socialRequsetDo = new JsonObjectRequest(Request.Method.POST, utils.urlSocialLogin, main, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("valid")) {
                        if (response.getString("user") != null) {
                            utils.user_access_token = response.getJSONObject("user").getString("user_access_token");
                            utils.app_authorization_access_token = response.getJSONObject("user").getString("app_authorization_access_token");
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                        final EditText email = new EditText(MainActivity.this);
                        alertBuilder.setTitle("enter your email id");
                        alertBuilder.setMessage("" + response.getString("message"));
                        alertBuilder.setView(email);
                        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                utils.email = email.getText().toString();
                                socialRequst(loginDetilas());
                            }
                        });
                        alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertBuilder.show();

                    }
                } catch (
                        JSONException e
                        )

                {
                    e.printStackTrace();
                }
            }
        }

                , new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        )

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> hashHeader = new HashMap<>();
                hashHeader.put("App-Authorization", "mDFyks1RqmB1rt3M|57Fpv84uy5HGZCuT9IN6J9RTdgAo24pt-_NW509nN-9");
                return hashHeader;
            }
        };
        Volley.newRequestQueue(this).
                add(socialRequsetDo);
    }
}
