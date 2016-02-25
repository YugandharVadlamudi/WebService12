package com.example.vikas.webservice1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ProfileActivity extends Activity {
    EditText edFirstName, edLastName, edEmail;
    Button btUpdateProfile,btChangePassword;
    ProgressDialog dialog;
    HashMap<String, String> userDetailsUpdate, deviseKeyH;
    JSONObject userDetailsUpdateJson, deviseKeyJson, mainJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        edEmail = (EditText) findViewById(R.id.edit_text_profile_email);
        edFirstName = (EditText) findViewById(R.id.edit_text_profile_first_name);
        edLastName = (EditText) findViewById(R.id.edit_text_profile_last_name);
        btUpdateProfile = (Button) findViewById(R.id.btn_done);
        btChangePassword=(Button)findViewById(R.id.bt_change_password);
        edEmail.setEnabled(false);
        userDetailsUpdate = new HashMap<>();
        deviseKeyH = new HashMap<>();
        mainJson = new JSONObject();
        /*
        * dialog spinner show
        * */
        dialog = new ProgressDialog(ProfileActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        init();
        btUpdateProfile.setOnClickListener(profileUpdate);
        btChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),changePasswordActivity.class));
            }
        });
    }

    public void init() {
        JsonObjectRequest profileRequest = new JsonObjectRequest(Request.Method.GET, utils.urlProfile, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("valid")) {
                        Log.d("response_Value", "" + response);
                        userProfile(response);
                    } else {
                        Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> profileHeader = new HashMap<>();
                profileHeader.put("User-Authorization", utils.app_authorization_access_token);
                profileHeader.put("App-Authorization", "mDFyks1RqmB1rt3M|57Fpv84uy5HGZCuT9IN6J9RTdgAo24pt-_NW509nN-9");
                return profileHeader;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(profileRequest);
    }
    private void userProfile(JSONObject response) {
        try {
            if (response.getJSONObject("user") != null) {
                dialog.dismiss();
                Log.d("userdetails_", "" + response.getJSONObject("user").getString("first_name"));
                edEmail.setText(response.getJSONObject("user").getString("email"));
                edLastName.setText(response.getJSONObject("user").getString("last_name"));
                edFirstName.setText(response.getJSONObject("user").getString("first_name"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener profileUpdate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            deviseUserDetails();
            updateDetails();
        }
    };

    private void updateDetails() {
        JsonObjectRequest profileUpdate = new JsonObjectRequest(Request.Method.PUT, utils.urlProfileUpdate, mainJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("valid")) {
                        Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                        dialog = new ProgressDialog(ProfileActivity.this);
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setMessage("Loading");
                        dialog.setIndeterminate(true);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        init();
                    } else {
                        Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), error.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getApplicationContext(), error.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(), error.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError)
                {
                    Toast.makeText(getApplicationContext(), error.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(), error.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> profileHeaderUpdate = new HashMap<>();
                profileHeaderUpdate.put("User-Authorization", utils.app_authorization_access_token);
                profileHeaderUpdate.put("App-Authorization", "mDFyks1RqmB1rt3M|57Fpv84uy5HGZCuT9IN6J9RTdgAo24pt-_NW509nN-9");
                return profileHeaderUpdate;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(profileUpdate);

    }

    private void deviseUserDetails() {
        userDetailsUpdate.put("profile_picture_url", "");
        userDetailsUpdate.put("profile_picture", "");
        userDetailsUpdate.put("source", "android");
        userDetailsUpdate.put("first_name", edFirstName.getText().toString());
        userDetailsUpdate.put("last_name", edLastName.getText().toString());
        deviseKeyH.put("device_key", Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID));
        userDetailsUpdateJson = new JSONObject(userDetailsUpdate);
        deviseKeyJson = new JSONObject(deviseKeyH);
        try {
            mainJson.put("user", userDetailsUpdateJson);
            mainJson.put("device", deviseKeyJson);
            Log.d("mainJson", "" + mainJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
