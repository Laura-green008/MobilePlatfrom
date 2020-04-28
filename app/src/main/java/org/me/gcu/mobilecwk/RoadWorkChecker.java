package org.me.gcu.mobilecwk;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class RoadWorkChecker extends AppCompatActivity implements View.OnClickListener {

    private List<RssFeedModel> mFeedModelList;
    private List<RssFeedModel> userFeedModelList;
    private EditText roadworksEditRoad;
    private Button roadworksButton;
    private String roadwoksurl="https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private Button plannedRoadworksButton;
    private Button currentRoadworksButton;
    private String userRoadworksRoad;
    private TextView roadworksErrorMessage;
    private RecyclerView roadworksRecyclerview;

    private String mFeedTitle;
    private String mFeedLink;
    private String mFeedDescription;
    private String mFeedStart;
    private String mFeedEnd;


    public static final String TAG = "MyActivity";

    Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.roadwork_checker);

        roadworksEditRoad = (EditText) findViewById(R.id.roadworksEditRoad);
        roadworksButton = (Button) findViewById(R.id.roadworkButton);
        roadworksButton.setOnClickListener(this);
        plannedRoadworksButton = (Button) findViewById(R.id.swaptoPlanned);
        plannedRoadworksButton.setOnClickListener(this);
        currentRoadworksButton = (Button) findViewById(R.id.swaptoCurrent);
        currentRoadworksButton.setOnClickListener(this);
        roadworksErrorMessage=(TextView)findViewById(R.id.roadworkErrorMessage);
        roadworksRecyclerview=(RecyclerView)findViewById(R.id.roadworksRecyclerview);
        roadworksRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        // roadworkCheckerDate=(DatePicker)findViewById(R.id.roadWorksDate);
//        if(recyclerViewState!=null)
//            roadworksRecyclerview.getLayoutManager().onRestoreInstanceState(recyclerViewState);//restore
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        recyclerViewState = roadworksRecyclerview.getLayoutManager().onSaveInstanceState();//save
//    }


    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.swaptoCurrent:
                openCurrentRoadwork1();
                break;
            case R.id.swaptoPlanned:
                openPlannedRoadwork1();
                break;
            case R.id.roadworkButton:
                userRoadworksRoad=roadworksEditRoad.getText().toString();
                roadworksErrorMessage.setText("");

                RoadWorkChecker.runRoadworksChecker runRoadworkasync = new RoadWorkChecker.runRoadworksChecker();
                runRoadworkasync.execute();
                break;
        }

    }

    public void openCurrentRoadwork1() {
        Intent openRoadwork1 = new Intent(this, RoadWorkChecker.class);
        startActivity(openRoadwork1);
    }

    public void openPlannedRoadwork1() {
        Intent openPlannedRoadwork1 = new Intent(this, PlannedRoadworkChecker.class);
        startActivity(openPlannedRoadwork1);
    }

    private class runRoadworksChecker extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {

            // urlLink = mEditText.getText().toString();
        }

        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL(roadwoksurl);
                InputStream inputStream = url.openConnection().getInputStream();

                userFeedModelList=parseFeed(inputStream);
                mFeedModelList= new ArrayList<>();
                int count= userFeedModelList.size();
                if(userRoadworksRoad != null) {
                    for (int i=0; i<count; i++){
                        if(userFeedModelList.get(i).title.contains(userRoadworksRoad)){
                            mFeedModelList.add(userFeedModelList.get(i));
                        }
                    }
                    userRoadworksRoad="";
                }
                else if(userRoadworksRoad == null){
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
            //mSwipeLayout.setRefreshing(false);

            if (success) {
//            titleIncidents.setText("Title: " + mFeedTitle);
//            descriptionIncidents.setText("Description: " + mFeedDescription);
//            linkIncidents.setText("Link: " + mFeedLink);
                // Fill RecyclerView
                if(mFeedModelList.size()==0){
                    //mFeedModelList=userFeedModelList;

                    roadworksErrorMessage.setText("The Road has no Incidents or Doesn't Exist");
                }
                else {
                    roadworksRecyclerview.setAdapter(new RecyclerAdapterRoadworks(mFeedModelList));
                }
            } else {
                Toast.makeText(RoadWorkChecker.this,
                        "Enter a valid Rss feed url",
                        Toast.LENGTH_LONG).show();
            }
        }


        public List<RssFeedModel> parseFeed(InputStream inputStream) throws XmlPullParserException,
                IOException {
            String title = null;
            String link = null;
            String description = null;
            String start = null;
            String end = null;
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

                        description=getDescription(result);
                        start= getStartDate(result);
                        end=getEndDate(result);

                    }

                    if (title != null && link != null && description != null) {

                        if(isItem) {
                            RssFeedModel item = new RssFeedModel(title, link, description, start, end);
                            items.add(item);
                        }
                        else {
                            mFeedTitle = title;
                            mFeedLink = link;
                            mFeedDescription = description;
                            mFeedStart = start;
                            mFeedEnd = end;

                        }

                        title = null;
                        link = null;
                        description = null;
                        start = null;
                        end = null;
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
        public String start;
        public String end;

        public RssFeedModel(String title, String link, String description, String start, String end) {
            this.title = title;
            this.link = link;
            this.description = description;
            this.start = start;
            this.end = end;
        }
    }

    public String getStartDate(String result){

        String startDate = "";
        String[] split = result.split("<br />");
        if (split.length > 1) {
            startDate = split[0];
        }

        String[] split1 = startDate.split(",");
        if (split1.length > 1) {
            startDate = split1[1];
        }

        startDate = startDate.replace(" - 00:00", "");
        startDate = startDate.trim();
        startDate = startDate.replace(" ", "/");
        //Date datestart = new SimpleDateFormat("dd/MMMM/yy").parse(startDate);
        //System.out.println(datestart);

        return startDate;

    }

    public String getEndDate(String result){
        String endDate="";
        String[] split=result.split("<br />");

        if(split.length>1){
            endDate=split[1];
        }

        String[] split1 = endDate.split(",");
        if(split1.length>1){
            endDate=split1[1];
        }

        endDate = endDate.replace(" - 00:00", "");
        endDate = endDate.trim();
        endDate = endDate.replace(" ", "/");



        String myFormat = "dd/MMMM/yy"; //In which you need put here
        return endDate;

    }

    public String getDescription(String result){

        String desc = result.replace("<br />","\n");

        return desc;
    }


}

