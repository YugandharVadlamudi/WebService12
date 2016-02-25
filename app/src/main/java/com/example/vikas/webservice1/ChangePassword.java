package com.example.vikas.webservice1;

import android.app.Activity;
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

public class ChangePassword extends Activity {


    EditText currentPassword,newPassword,confirmPassword;
    Button changePassword;
    HashMap<String,String> userPasswordUpdate, deviceKey;
    JSONObject userPasswordUpadateJson,deviceKeyPasswordJson,mainJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        currentPassword= (EditText) findViewById(R.id.et_profile_current_password);
        newPassword= (EditText) findViewById(R.id.et_profile_new_password);
        confirmPassword= (EditText) findViewById(R.id.et_profile_confirm_password);
        changePassword= (Button) findViewById(R.id.btn_change_password);
        userPasswordUpdate=new HashMap<>();
        deviceKey=new HashMap<>();
        mainJson=new JSONObject();
        changePassword.setOnClickListener(passwordUpdate);
    }

    View.OnClickListener passwordUpdate=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            userDeatails();
            updateDetails();
        }
    };
    private void updateDetails(){

        userPasswordUpdate.put("new_password", newPassword.getText().toString());
        userPasswordUpdate.put("old_password",currentPassword.getText().toString());
        userPasswordUpdate.put("login_type","email");
        userPasswordUpdate.put("user_access_token",utils.user_access_token);
        deviceKey.put("device_key", Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID));
        userPasswordUpadateJson=new JSONObject(userPasswordUpdate);
        deviceKeyPasswordJson=new JSONObject(deviceKey);

        try {
            mainJson.put("user",userPasswordUpadateJson);
            mainJson.put("device",deviceKeyPasswordJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void userDeatails(){

        JsonObjectRequest passwordChange=new JsonObjectRequest(Request.Method.PUT, utils.urlChangePassword, mainJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("valid")){
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
        Volley.newRequestQueue(getApplicationContext()).add(passwordChange);

    }
}
