package sg.edu.rp.webservices.c302_p09_mcafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etLoginID, etPassword;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginID = (EditText)findViewById(R.id.editTextLoginID);
        etPassword = (EditText)findViewById(R.id.editTextPassword);
        btnSubmit = (Button)findViewById(R.id.buttonSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etLoginID.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.equalsIgnoreCase("")) {
                    Toast.makeText(LoginActivity.this, "Login failed. Please enter username.", Toast.LENGTH_LONG).show();

                } else if (password.equalsIgnoreCase("")) {
                    Toast.makeText(LoginActivity.this, "Login failed. Please enter password.", Toast.LENGTH_LONG).show();

                } else {
                    // proceed to authenticate user
                    // What is the web service URL?
                    HttpRequest requestPOST = new HttpRequest("http://10.0.2.2/C302_P09_mCafe/doLogin.php");
                    // What is the HTTP method?
                    requestPOST.setOnHttpResponseListener(myResponseListener);
                    requestPOST.setMethod("POST");
                    // What parameters need to be provided?
                    requestPOST.addData("username", etLoginID.getText().toString());
                    requestPOST.addData("password", etPassword.getText().toString());
                    requestPOST.execute();

// Point X – call doLogin web service to authenticate use



                }
            }
        });
    }

    // If the user can log in, extract the id and API Key from the JSON object, set them into Intent and start MainActivity Intent.
    // If the user cannot log in, display a toast to inform user that login has failed.
    private HttpRequest.OnHttpResponseListener myResponseListener =
            new HttpRequest.OnHttpResponseListener() {
                @Override
                public void onResponse(String response){
                    // process response here
                    Log.i("Result", response);

                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("authenticated") == true) {
                            String id = jsonObject.getString("id");
                            String apikey = jsonObject.getString("apikey");

                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("loginID", id);
                            editor.putString("apiKey", apikey);
                            editor.commit();

                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.putExtra("id", id);
                            i.putExtra("apikey", apikey);
                            startActivity(i);
                        } else {
                            Toast.makeText(LoginActivity.this,"Login has failed", Toast.LENGTH_LONG).show();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }


                }
            };
}
