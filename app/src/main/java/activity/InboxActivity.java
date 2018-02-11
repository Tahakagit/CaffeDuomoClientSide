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
import com.example.franc.caffduomo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import helper.JSONParse;
import helper.Messages;
import helper.MyInboxAdapter;
import helper.SQLiteHandler;
import helper.SessionManager;

/**
 * Created by franc on 03/02/2018.
 */

public class InboxActivity extends AppCompatActivity {
    ProgressDialog pDialog;


    private SQLiteHandler db;
    private SessionManager session;
    private static final String TAG = RegisterActivity.class.getSimpleName();
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
        // TODO GET CURRENTLY LOGGED USER INFORMATION FROM SHARED PREFERENCES

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

        getMyMessages();
        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg(idText.getText().toString(), msgText.getText().toString());
                idText.setText("");
                msgText.setText("");
            }
        });

    }

    private void getMyMessages(){
        String id = session.getLoggedID();
        Log.d(TAG, "Logged ID  " + id);
        // todo create volley GET to retrievemsgbyid.php
        // todo pass id to volley
    }
    private void sendMsg(final String userId, final String msg){
        // Tag used to cancel the request
        String tag_string_req = "req_login";


        pDialog.setMessage("Invio Messaggio ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SENDMSG, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Message send response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
/*
                        JSONObject messages = jObj.getJSONObject("apps");
*/

                        // todo sistemare qua SHARED PREFERENCES
                        JSONParse pj = new JSONParse();
                        pj.ParseJSON(response);
                        listOfMessages = pj.getMessaggi();
                        inboxAdapter.notifyDataSetChanged();

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
                Log.e(TAG, "Login Error: " + error.getMessage());
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
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    // START LOCATIONS RECYCLERVIEW
    public void startInboxRecyclerView(){
        inboxAdapter = new MyInboxAdapter(listOfMessages);
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