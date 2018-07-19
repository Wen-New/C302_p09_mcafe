package sg.edu.rp.webservices.c302_p09_mcafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import sg.edu.rp.webservices.c302_p09_mcafe.HttpRequest;
import sg.edu.rp.webservices.c302_p09_mcafe.R;

public class MenuItemsByCategoryActivity extends AppCompatActivity {

    private static final String TAG = "MenuItemsByCategoryActivity";

    private ListView lvItems;
    private ArrayList<MenuItem> alItems;
    private ArrayAdapter<MenuItem> aaItems;
    Intent i;
    String loginID, apiKey, cid, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_items_by_category);

        i = getIntent();
        cid = i.getStringExtra("id");
        description = i.getStringExtra("description");

        lvItems = (ListView)findViewById(R.id.listViewItems);
        alItems = new ArrayList<MenuItem>();
        aaItems = new ArrayAdapter<MenuItem>(this, android.R.layout.simple_list_item_1, alItems);
        alItems.clear();
        lvItems.setAdapter(aaItems);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        loginID = prefs.getString("loginID", "");
        apiKey = prefs.getString("apiKey", "");

        if (loginID.equalsIgnoreCase("") || apiKey.equalsIgnoreCase("")) {
            // redirect back to login screen
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        // Point X – call getMenuCategories web service so that we can display list of categories


    }

    @Override
    protected void onResume() {
        super.onResume();

        alItems.clear();
        // TODO (5) Refresh the main activity with the latest list of contacts by calling getListOfContacts.php
        // What is the web service URL?
        HttpRequest requestPOST = new HttpRequest("http://10.0.2.2/C302_P09_mCafe/getMenuItemsByCategory.php");
        // What is the HTTP method?
        requestPOST.setOnHttpResponseListener(myResponseListener);
        requestPOST.setMethod("POST");
        // What parameters need to be provided?
        requestPOST.addData("loginId", loginID);
        requestPOST.addData("apikey", apiKey);
        requestPOST.addData("categoryId", cid);
        requestPOST.execute();
    }

    private HttpRequest.OnHttpResponseListener myResponseListener =
            new HttpRequest.OnHttpResponseListener() {
                @Override
                public void onResponse(String response){
                    // process response here
                    Log.i("Result", response);

                    try{
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i=0; i<jsonArray.length(); i++){
                            JSONObject jsonObj = jsonArray.getJSONObject(i);

                            String itemId = jsonObj.getString("menu_item_id");
                            String categoryId = jsonObj.getString("menu_item_category_id");
                            String description = jsonObj.getString("menu_item_description");
                            double price = jsonObj.getDouble("menu_item_unit_price");

                            MenuItem obj = new MenuItem(itemId, categoryId, description, price);
                            alItems.add(obj);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    aaItems.notifyDataSetChanged();
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.add) {

            // TODO (8) Create an Intent to Create Contact
            // Put the following into intent:- loginId, apikey
            Intent i = new Intent(MenuItemsByCategoryActivity.this, AddMenuItemActivity.class);
            i.putExtra("cid", cid);
            startActivity(i);
        }
        if (id == R.id.logout) {

            // TODO (8) Create an Intent to Create Contact
            // Put the following into intent:- loginId, apikey
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            String loginID = prefs.getString("loginID", "");
            String apiKey = prefs.getString("apiKey", "");
            editor.commit();
            Intent i = new Intent(MenuItemsByCategoryActivity.this, LoginActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}

