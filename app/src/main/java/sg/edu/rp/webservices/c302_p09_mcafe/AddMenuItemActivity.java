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

import org.json.JSONArray;
import org.json.JSONObject;

public class AddMenuItemActivity extends AppCompatActivity {

    Button btnAdd;
    Intent i;
    EditText etItem, etPrice;
    String cid, name, price, loginID, apiKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);

        i = getIntent();
        etItem = findViewById(R.id.etItem);
        etPrice = findViewById(R.id.etPrice);
        btnAdd = findViewById(R.id.btnAdd);
        cid = i.getStringExtra("cid");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        loginID = prefs.getString("loginID", "");
        apiKey = prefs.getString("apiKey", "");

        if (loginID.equalsIgnoreCase("") || apiKey.equalsIgnoreCase("")) {
            // redirect back to login screen
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etItem.getText().toString();
                price = etPrice.getText().toString();

                // What is the web service URL?
                HttpRequest requestPOST = new HttpRequest("http://10.0.2.2/C302_P09_mCafe/addMenuItem.php");
                // What is the HTTP method?
                requestPOST.setOnHttpResponseListener(myResponseListener);
                requestPOST.setMethod("POST");
                // What parameters need to be provided?
                requestPOST.addData("loginId", loginID);
                requestPOST.addData("apikey", apiKey);
                requestPOST.addData("categoryId", cid);
                requestPOST.addData("description", name);
                requestPOST.addData("price", price);
                requestPOST.execute();

            }
        });
    }

    private HttpRequest.OnHttpResponseListener myResponseListener =
            new HttpRequest.OnHttpResponseListener() {
                @Override
                public void onResponse(String response){
                    // process response here
                    Log.i("Result", response);

                    try{
                        JSONObject jsonObj= new JSONObject(response);
                        boolean status = jsonObj.getBoolean("status");
                        if(status == true) {
                            finish();
                        } else {
                            Toast.makeText(AddMenuItemActivity.this, "Failed", Toast.LENGTH_LONG).show();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
            };
}
