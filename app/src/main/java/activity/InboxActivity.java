package activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.franc.caffduomo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import helper.Messages;
import helper.MyInboxAdapter;
import helper.SessionManager;

/**
 * Created by franc on 03/02/2018.
 */

public class InboxActivity extends AppCompatActivity {
    ProgressDialog pDialog;


    private SessionManager session;
    static MyInboxAdapter inboxAdapter;
    static List<Messages> listOfMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_inbox);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        TextView txtName = (TextView) findViewById(R.id.name);
        TextView txtEmail = (TextView) findViewById(R.id.email);
        Button btnLogout = (Button) findViewById(R.id.id_logout_btn);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
/*
        startInboxRecyclerView();
*/
        startNavDrawer();

        // SqLite database handler
/*
        db = new SQLiteHandler(getApplicationContext());
*/

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        startInputForm();
        getMyMessages(session.getLoggedID());
/*
        startInboxRecyclerView(getMyMessages(session.getLoggedID()));
*/


    }

    //NAVIGATION DRAWER
    public void startNavDrawer(){
        final DrawerLayout mDrawerLayout;
/*
        final Intent creaConto = new Intent(this, BattleActivity.class);
*/

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            switch (menuItem.getItemId())
                            {
                                case R.id.action_category_1:
/*
                                    startActivity(creaConto);
*/
                                    break;
                                case R.id.action_category_2:
                                    //tabLayout.getTabAt(1).select();
                                    break;
                                case R.id.action_category_3:
                                    //tabLayout.getTabAt(2).select();
                            }

                            mDrawerLayout.closeDrawers();
                            return true;
                        }
                    });
        }


    }

    public void startInputForm(){

        final EditText idText = findViewById(R.id.id_send_message_id);
        final EditText msgText = findViewById(R.id.id_send_message_msg);
        Button sendMsgButton = findViewById(R.id.id_send_message_button);

        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg(idText.getText().toString(), msgText.getText().toString());
                idText.setText("");
                msgText.setText("");
            }
        });

    }

    /**
     * Get all messages FROM messages where id_user ==  session.getLoggedId()
     * @param userId session.getLoggedID()
     */
    private void getMyMessages(final String userId){
        String id = session.getLoggedID();
        Log.d(this.getClass().getName(), "Logged ID  " + id);
        String tag_request = "req_send_msg";

        pDialog.setMessage("Caricamento messaggi ...");
        showDialog();


        listOfMessages = new ArrayList<>();
        // todo complete retrievemsdgbyid.php in eclipse

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_RETRIEVE_MSG,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(this.getClass().getName(), "Message send response: " + response.toString());
                        hideDialog();

                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                //adding the product to product list
                                listOfMessages.add(new Messages(
                                        product.getString("id"),
                                        product.getString("message"),
                                        product.getString("date")
                                ));
                            }

                            //creating adapter object and setting it to recyclerview
/*
                            MyInboxAdapter adapter = new MyInboxAdapter(MainActivity.this, productList);
*/
                            startInboxRecyclerView(listOfMessages);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("idUser", userId);
                return params;
            }

        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    /**
     * Send message to remote mysql server
     * @param userId Recipient id todo will be ussername
     * @param msg Message body
     */
    private void sendMsg(final String userId, final String msg){
        // Tag used to cancel the request
        String tag_send_msg_request = "req_send_msg";


        pDialog.setMessage("Invio Messaggio ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SENDMSG, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(this.getClass().getName(), "Message send response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Messaggio Inviato!", Toast.LENGTH_LONG).show();



                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(this.getClass().getName(), "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("idUser", userId);
                params.put("message", msg);

                return params;
            }

        };

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_send_msg_request);

    }

    // START LOCATIONS RECYCLERVIEW
    public void startInboxRecyclerView(List<Messages> messages){
        inboxAdapter = new MyInboxAdapter(messages);
        RecyclerView list;
        list = (RecyclerView)findViewById(R.id.id_rv_inbox);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(inboxAdapter);

    }


    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }


    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

/*
        db.deleteUsers();
*/

        // Launching the login activity
        Intent intent = new Intent(InboxActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}