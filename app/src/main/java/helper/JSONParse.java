package helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franc on 10/02/2018.
 */

public class JSONParse {

    //Declare the arrays of fields you require
    public static String[] ids;
    public static String[] messages;
    public static String[] created;

    private JSONArray messagesJson = null;


    List<Messages> Messaggi;


    private String json;

    public void ParseJSON(String json){

        this.json = json;
    }

    protected void parseJSON(){
        JSONObject jsonObject=null;

        try {

            messagesJson = new JSONArray(json);


            ids = new String[messagesJson.length()];
            messages = new String[messagesJson.length()];
            created = new String[messagesJson.length()];

            Messaggi = new ArrayList<Messages>();



            for(int i = 0; i< messagesJson.length(); i++){
                Messages movie_object =  new Messages();

                jsonObject = messagesJson.getJSONObject(i);

                ids[i] = jsonObject.getString("userID");
                messages[i] = jsonObject.getString("message2");
                created[i] = jsonObject.getString("created");

                movie_object.setUserId(ids[i]);
                movie_object.setMessage(messages[i]);
                movie_object.setMessage(created[i]);
                Messaggi.add(movie_object);



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public List<Messages> getMessaggi()
    {
        //function to return the final populated list
        return Messaggi;
    }


}
