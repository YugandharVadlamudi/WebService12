package com.example.vikas.webservice1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kiran on 22-02-2016.
 */
public class HomeActivity extends Activity {
    ProgressDialog dialog;
    TextView tvHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tvHome=(TextView)findViewById(R.id.tv_hello);
        dialog = new ProgressDialog(HomeActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        init();
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
                tvHome.setText(response.getJSONObject("user").getString("first_name"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
