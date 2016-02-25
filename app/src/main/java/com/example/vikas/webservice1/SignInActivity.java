package com.example.vikas.webservice1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class SignInActivity extends Activity {
    EditText edEmail, edPassword;
    Button bvSignIn;
    double latitude, longitude;
    ConnectivityManager cm;

    HashMap<String, String> deviseH, locationH, userH;
    JSONObject main, userJson, deviseJson, locationJson;
    TextView forgotPassword;
    MainActivity l;
    AlertDialog.Builder alertDialog;
    private String DateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        init();
        deviceInformation();
        locatinInformation();
    }

    private void userInformation() {
        userH.put("login_type", "email");
        userH.put("email", edEmail.getText().toString());
        userH.put("source", "android");
        userH.put("password", edPassword.getText().toString());
    }

    private void locatinInformation() {
        locationH.put("latitude", utils.latitude);
        locationH.put("longitude", utils.longititude);
        locationH.put("accuracy", utils.accracy);
        locationH.put("provider", utils.provider);
        Log.d("latitude", String.valueOf(latitude));
        Log.d("longitute", String.valueOf(longitude));
    }

    private void deviceInformation() {
        TimeZone tz = TimeZone.getDefault();
        Log.d("timeZone", "" + tz.getID());
        Log.d("osVersion", "" + Build.VERSION.RELEASE);
        Log.d("MName", "" + Build.MANUFACTURER.toString());
        Log.d("Manufacture", "" + Build.MODEL.toString());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTime = sdf.format(c.getTime());
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
        Date date = null;
        String output = null;
        try {
            date = sdf.parse(DateTime);
            output = outputformat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("dateTime", "" + output);
        Log.d("deviseKey", Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID));
        deviseH.put("time_zone", tz.getID());
        deviseH.put("device_os", "Android");
        deviseH.put("device_os_version", Build.VERSION.RELEASE);
        deviseH.put("device_gcm_token", "android");
        deviseH.put("device_model", Build.MODEL.toString());
        deviseH.put("date_time",output);
        deviseH.put("device_manufacture", Build.MANUFACTURER.toString());
        deviseH.put("device_key", Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID));
    }

    private void init() {
        edEmail = (EditText) findViewById(R.id.edit_text_email_signin);
        edPassword = (EditText) findViewById(R.id.edit_text_password_signin);
        bvSignIn = (Button) findViewById(R.id.btn_signin);
forgotPassword=(TextView)findViewById(R.id.tv_forgot_password);
        deviseH = new HashMap<>();
        userH = new HashMap<>();
        locationH = new HashMap<>();
        bvSignIn.setOnClickListener(SignIn);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForgotPasswordActivity.class));
            }
        });
        alertDialog = new AlertDialog.Builder(this);
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    View.OnClickListener SignIn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (!utils.hasContent(edEmail)) {
                    Toast.makeText(getApplicationContext(), "Enter Email id", Toast.LENGTH_SHORT).show();
                    edEmail.requestFocus();
                } else if (!utils.hasContent(edPassword)) {
                    Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                    edPassword.requestFocus();
                } else if (!utils.passwordLength(edPassword)) {
                    Toast.makeText(getApplicationContext(), "Password min Characters 8", Toast.LENGTH_SHORT).show();
                    edPassword.requestFocus();
                } else if (!utils.hasEmailPattern(edEmail)) {
                    Toast.makeText(getApplicationContext(), "Email Pattern didn't match", Toast.LENGTH_SHORT).show();
                    edEmail.requestFocus();
                } else {
                    userData();
                }

            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void userData() {
        try {
            userInformation();
            userJson = new JSONObject(userH);
            deviseJson = new JSONObject(deviseH);
            locationJson = new JSONObject(locationH);
            main = new JSONObject();
            main.put("device", deviseJson);
            main.put("location", locationJson);
            main.put("user", userJson);
            Log.d("main", "" + main);
            JsonObjectRequest SingInRequst = new JsonObjectRequest(Request.Method.POST, utils.urlLogin, main, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        if (response.getBoolean("valid")) {
                            if (response.getString("user") != null) {
                                JSONObject user = response.getJSONObject("user");
                                utils.user_access_token = user.getString("user_access_token");
                                utils.app_authorization_access_token = user.getString("app_authorization_access_token");
//                                startActivity(new Intent(SignInActivity.this, ProfileActivity.class));
                                startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "" + error.toString(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("App-Authorization", "mDFyks1RqmB1rt3M|57Fpv84uy5HGZCuT9IN6J9RTdgAo24pt-_NW509nN-9");
                    return header;
                }
            };
            Volley.newRequestQueue(getApplicationContext()).add(SingInRequst);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
