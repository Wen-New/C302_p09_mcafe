package sg.edu.rp.webservices.c302_p09_mcafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ListView lvCategories;
    private ArrayList<MenuCategory> alCategories;
    private ArrayAdapter<MenuCategory> aaCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvCategories = (ListView)findViewById(R.id.listViewCategories);
        alCategories = new ArrayList<MenuCategory>();
        aaCategories = new ArrayAdapter<MenuCategory>(this, android.R.layout.simple_list_item_1, alCategories);
        lvCategories.setAdapter(aaCategories);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String loginID = prefs.getString("loginID", "");
        String apiKey = prefs.getString("apiKey", "");

        if (loginID.equalsIgnoreCase("") || apiKey.equalsIgnoreCase("")) {
            // redirect back to login screen
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        lvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MenuCategory selectedContact = alCategories.get(position);

                // TODO (7) When a contact is selected, create an Intent to View Contact Details
                // Put the following into intent:- contact_id, loginId, apikey
                Intent i = new Intent(MainActivity.this, MenuItemsByCategoryActivity.class);
                String cid = alCategories.get(position).getCategoryId();
                String description = alCategories.get(position).getDescription();
                i.putExtra("id", cid);
                i.putExtra("description", description);
                startActivity(i);
            }
        });

        // Point X – call getMenuCategories web service so that we can display list of categories
        alCategories.clear();

        // TODO (5) Refresh the main activity with the latest list of contacts by calling getListOfContacts.php
        // What is the web service URL?
        HttpRequest requestPOST = new HttpRequest("http://10.0.2.2/C302_P09_mCafe/getMenuCategories.php");
        // What is the HTTP method?
        requestPOST.setOnHttpResponseListener(myResponseListener);
        requestPOST.setMethod("POST");
        // What parameters need to be provided?
        requestPOST.addData("loginId", loginID);
        requestPOST.addData("apikey", apiKey);
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
                        alCategories.clear();
                        for (int i=0; i<jsonArray.length(); i++){
                            JSONObject jsonObj = jsonArray.getJSONObject(i);

                            String categoryId = jsonObj.getString("menu_item_category_id");
                            String description = jsonObj.getString("menu_item_category_description");

                            MenuCategory obj = new MenuCategory(categoryId, description);
                            alCategories.add(obj);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    aaCategories.notifyDataSetChanged();
                }
            };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.logout) {

            // TODO (8) Create an Intent to Create Contact
            // Put the following into intent:- loginId, apikey
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            String loginID = prefs.getString("loginID", "");
            String apiKey = prefs.getString("apiKey", "");
            editor.commit();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}

