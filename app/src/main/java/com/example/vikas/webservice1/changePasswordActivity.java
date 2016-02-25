package com.example.vikas.webservice1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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


public class changePasswordActivity extends Activity {
    EditText etOldPasswrord, etNewPassword, etConfirmPassword;
    Button btChangePassword, Logout;
    HashMap<String, String> UserHash, deviseHash;
    JSONObject userJSON, deviseJSON, mainJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        init();
    }

    private void init() {
        etOldPasswrord = (EditText) findViewById(R.id.et_profile_current_password);
        etNewPassword = (EditText) findViewById(R.id.et_profile_new_password);
        etConfirmPassword = (EditText) findViewById(R.id.et_profile_confirm_password);
        btChangePassword = (Button) findViewById(R.id.btn_change_password);
         Logout=(Button)findViewById(R.id.bv_logout);
        UserHash = new HashMap<>();
        deviseHash = new HashMap<>();
        mainJSON = new JSONObject();
        btChangePassword.setOnClickListener(ChangePassword);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new AlertDialog.Builder(changePasswordActivity.this)
                       .setMessage("Are you want to exit")
                       .setCancelable(true)
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               utils.app_authorization_access_token=null;
                               utils.user_access_token=null;
                               startActivity(new Intent(getApplicationContext(),MainActivity.class));
                           }
                       })
                       .setNegativeButton("No", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                           }
                       }).show();
            }
        });
    }

    View.OnClickListener ChangePassword = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserData();
            JsonObjectRequest changePassworRequest = new JsonObjectRequest(Request.Method.PUT, utils.urlChangePassword, mainJSON, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("response", "" + response);
                    try {
                        if (response.getBoolean("valid")) {
                            Toast.makeText(getApplicationContext(), "" + response.getString("message"), Toast.LENGTH_SHORT).show();
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
                    Log.d("erroris", "" + error);
                    Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headerHash = new HashMap<>();
                    headerHash.put("User-Authorization", utils.app_authorization_access_token);
                    headerHash.put("App-Authorization", "mDFyks1RqmB1rt3M|57Fpv84uy5HGZCuT9IN6J9RTdgAo24pt-_NW509nN-9");
                    return headerHash;
                }
            };
            Volley.newRequestQueue(getApplicationContext()).add(changePassworRequest);
        }
    };

    public void UserData() {
        UserHash.put("new_password", etNewPassword.getText().toString());
        UserHash.put("source", "android");
        UserHash.put("old_password", etOldPasswrord.getText().toString());
        UserHash.put("login_type", "email");
        UserHash.put("user_access_token", utils.user_access_token);
        deviseHash.put("device_key", Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID));
        userJSON = new JSONObject(UserHash);
        deviseJSON = new JSONObject(deviseHash);
        try {
            mainJSON.put("user", userJSON);
            mainJSON.put("device", deviseJSON);
            Log.d("mainJson", "" + mainJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
