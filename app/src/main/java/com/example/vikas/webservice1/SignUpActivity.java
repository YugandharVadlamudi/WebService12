package com.example.vikas.webservice1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends Activity {


    EditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword;
    Button btSignUp;
    ConnectivityManager cm;
    HashMap<String, String> detailsHashMap, deviceKey;
    JSONObject userJson, devicekeyJson, mainJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        init();
    }

    private void init() {

        etFirstName = (EditText) findViewById(R.id.et_First_name);
        etLastName = (EditText) findViewById(R.id.et_Last_name);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        btSignUp = (Button) findViewById(R.id.btn_sign_up);
        btSignUp.setOnClickListener(signUp);
        detailsHashMap = new HashMap<>();
        deviceKey = new HashMap<>();
    }

    View.OnClickListener signUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (!utils.hasContent(etFirstName)) {
                    Toast.makeText(getApplicationContext(), "enter First Name", Toast.LENGTH_SHORT).show();
                    etFirstName.requestFocus();
                } else if (!utils.hasContent(etLastName)) {
                    Toast.makeText(getApplicationContext(), "enter Last Name", Toast.LENGTH_SHORT).show();
                    etLastName.requestFocus();
                } else if (!utils.hasContent(etEmail)) {
                    Toast.makeText(getApplicationContext(), "enter Email Address", Toast.LENGTH_SHORT).show();
                    etEmail.requestFocus();
                } else if (!utils.hasContent(etPassword)) {
                    Toast.makeText(getApplicationContext(), "enter enter Password", Toast.LENGTH_SHORT).show();
                    etPassword.requestFocus();
                } else if (!utils.hasContent(etConfirmPassword)) {
                    Toast.makeText(getApplicationContext(), "enter Confirm Password", Toast.LENGTH_SHORT).show();
                    etConfirmPassword.requestFocus();
                } else if (!utils.hasEmailPattern(etEmail)) {
                    Toast.makeText(getApplicationContext(), "enter Valid Email Address", Toast.LENGTH_SHORT).show();
                    etEmail.requestFocus();
                } else if (!utils.matchpassword(etPassword, etConfirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Password and Confirm password didn't match", Toast.LENGTH_SHORT).show();
                    etPassword.requestFocus();
                } else if (!utils.passwordLength(etPassword)) {
                    Toast.makeText(getApplicationContext(), "Password min 8 characters", Toast.LENGTH_SHORT).show();
                    etPassword.requestFocus();
                } else if (!utils.passwordLength(etConfirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Password min 8 characters", Toast.LENGTH_SHORT).show();
                    etConfirmPassword.requestFocus();
                } else {

                    registartion();
                }
            } else {
                Toast.makeText(getApplicationContext(), "no InternetConnection", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void registartion() {

        detailsHashMap.put("email", etEmail.getText().toString());
        detailsHashMap.put("password", etPassword.getText().toString());
        detailsHashMap.put("source", "android");
        detailsHashMap.put("login_type", "email");
        detailsHashMap.put("first_name", etFirstName.getText().toString());
        detailsHashMap.put("last_name", etLastName.getText().toString());
        deviceKey.put("device_key", Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID));
        userJson = new JSONObject(detailsHashMap);
        devicekeyJson = new JSONObject(deviceKey);
        mainJson = new JSONObject();

        try {
            mainJson.put("user", userJson);
            mainJson.put("device", devicekeyJson);

            JsonObjectRequest signupRequest = new JsonObjectRequest(Request.Method.POST, utils.urlRegistration, mainJson, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getBoolean("valid")) {
                            Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("App-Authorization", "mDFyks1RqmB1rt3M|57Fpv84uy5HGZCuT9IN6J9RTdgAo24pt-_NW509nN-9");
                    return header;
                }
            };
            Volley.newRequestQueue(getApplicationContext()).add(signupRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

