package com.example.vikas.webservice1;

import android.app.Activity;
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

/**
 * Created by Kiran on 22-02-2016.
 */
public class ForgotPasswordActivity extends Activity {
    EditText edFPEmail;
    Button sentPassword;
    HashMap<String, String> userHash, deviseHash;
    JSONObject userJson, deviseJson, mainJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        init();
    }

    private void init() {
        edFPEmail = (EditText) findViewById(R.id.ed_fp_email);
        sentPassword = (Button) findViewById(R.id.bt_sent_passoword);
        userHash = new HashMap<>();
        deviseHash = new HashMap<>();
        mainJson = new JSONObject();
        sentPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObjectRequest forgotPasswordRequst = new JsonObjectRequest(Request.Method.POST, utils.urlForgotPassword, userdeviseDetails(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("valid"))
                            {
                                Toast.makeText(getApplicationContext(),""+response.getString("message"),Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),""+response.getString("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("erroris",""+error.getMessage());
                    Toast.makeText(getApplicationContext(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String,String>hashHeader=new HashMap<String, String>();
                        hashHeader.put("App-Authorization","mDFyks1RqmB1rt3M|57Fpv84uy5HGZCuT9IN6J9RTdgAo24pt-_NW509nN-9");
                        return hashHeader;
                    }
                };
                Volley.newRequestQueue(getApplicationContext()).add(forgotPasswordRequst);
            }
        });
    }
    public JSONObject userdeviseDetails() {
        userHash.put("email", edFPEmail.getText().toString());
        userHash.put("source", "android");
        deviseHash.put("device_key", Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID));
        userJson = new JSONObject(userHash);
        deviseJson = new JSONObject(deviseHash);
        try {
            mainJson.put("user", userJson);
            mainJson.put("device", deviseJson);
            Log.d("forgortjson",""+mainJson);
            return mainJson;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
