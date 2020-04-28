package org.me.gcu.mobilecwk;
//S1822006
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IncidentsChecker extends AppCompatActivity implements View.OnClickListener {

    private String urlSource = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
    //private String urlSource="https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private RecyclerView recycleviewIncidents;
    private List<RssFeedModel> mFeedModelList;
    private List<RssFeedModel> userFeedModelList;
    private String mFeedTitle;
    private String mFeedLink;
    private String mFeedDescription;
    private TextView incidentsCheckerTitle;
    private TextView enterRoadIncident;
    private EditText enteredRoadIncident;
    private Button searchIncidentsButton;
    private String userRoadinput;
    private TextView incidentsErrorMessage;



    public static final String TAG = "MyActivity";

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.incidents_checker);

        incidentsCheckerTitle=(TextView)findViewById(R.id.incidentsCheckerTitle);
        enterRoadIncident=(TextView)findViewById(R.id.enterRoadIncident);
        enteredRoadIncident=(EditText)findViewById(R.id.enteredRoadIncident);
        searchIncidentsButton=(Button)findViewById(R.id.searchIncidentsButton);
        recycleviewIncidents=(RecyclerView)findViewById(R.id.recycleviewIncidents);

        searchIncidentsButton.setOnClickListener(this);
        recycleviewIncidents.setLayoutManager(new LinearLayoutManager(this));
        incidentsErrorMessage=(TextView)findViewById(R.id.incidentsErrrorMessage);


    }
    public void onClick(View v){

        userRoadinput=enteredRoadIncident.getText().toString();
        incidentsErrorMessage.setText("");
        runIncidentsChecker runasync = new runIncidentsChecker();
        runasync.execute();

    }

private class runIncidentsChecker extends AsyncTask<Void, Void, Boolean>{

    @Override
    protected void onPreExecute() {


    }

    protected Boolean doInBackground(Void... voids) {
        try {
            URL url = new URL(urlSource);
            InputStream inputStream = url.openConnection().getInputStream();

              userFeedModelList=parseFeed(inputStream);
              mFeedModelList= new ArrayList<>();
              int count= userFeedModelList.size();
            if(userRoadinput != null) {
                for (int i=0; i<count; i++){
                    if(userFeedModelList.get(i).title.contains(userRoadinput)){
                        mFeedModelList.add(userFeedModelList.get(i));
                    }
                }
                userRoadinput="";
            }
            else if(userRoadinput == null){
                mFeedModelList=userFeedModelList;
            }

            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error", e);
        }
        return false;
    }


    protected void onPostExecute(Boolean success) {

        if (success) {

            // Fill RecyclerView
            if(mFeedModelList.size()==0){

                incidentsErrorMessage.setText("The Road has no Incidents or Doesn't Exist");
            }
            else {
                recycleviewIncidents.setAdapter(new RecyclerAdapterIncidents(mFeedModelList));
            }
        } else {
            Toast.makeText(IncidentsChecker.this,
                    "Enter a valid Rss feed url",
                    Toast.LENGTH_LONG).show();
        }
    }


    public List<RssFeedModel> parseFeed(InputStream inputStream) throws XmlPullParserException,
            IOException {
        String title = null;
        String link = null;
        String description = null;
        boolean isItem = false;
        List<RssFeedModel> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MyXmlParser", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = getDescription(result);
                }

                if (title != null && link != null && description != null) {

                    if(isItem) {
                        RssFeedModel item = new RssFeedModel(title, link, description);
                        items.add(item);
                    }
                    else {
                        mFeedTitle = title;
                        mFeedLink = link;
                        mFeedDescription = description;
                    }

                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }



    }

}
    public class RssFeedModel {

        public String title;
        public String link;
        public String description;

        public RssFeedModel(String title, String link, String description) {
            this.title = title;
            this.link = link;
            this.description = description;
        }
    }

    public String getDescription(String result){

        String desc = result.replace("<br />"," ");

        return desc;
    }




}


