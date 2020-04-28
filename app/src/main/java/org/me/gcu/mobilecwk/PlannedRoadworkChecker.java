package org.me.gcu.mobilecwk;
//S1822006
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlannedRoadworkChecker extends AppCompatActivity implements View.OnClickListener {
    private List<RssFeedModel> mFeedModelList;
    private List<RssFeedModel> userFeedModelList;

    private Button plannedRoadworksButtonPlanned;
    private Button currentRoadworksButtonPlanned;
    private Button plannedRoadworkSearch;

    private EditText plannedRoadworksEditRoad;
    private EditText plannedRoadworksDate;

    private TextView plannedRoadworksErrorMessage;
    private RecyclerView plannedRoadworksRecyclerview;

    private String userPlannedRoadworksRoad;
    private String userPlannedRoadworksDate;
    private String roadwoksurl="https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";

    private String userDateSet;
    private String startDateRoadworks;
    private String endDateRoadworks;
    private String mFeedTitle;
    private String mFeedLink;
    private String mFeedDescription;
    private String mFeedStart;
    private String mFeedEnd;

    private Date plannedRoadworksStart;
    private Date plannedRoadworksEnd;
    private Date enteredDate;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate today= LocalDate.now();



    final Calendar myCalendar = Calendar.getInstance();

    //EditText edittext= (EditText) findViewById(R.id.Birthday);
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Date usingDate=myCalendar.getTime();
            System.out.println(usingDate);
            updateLabel();
        }

    };

    public static final String TAG = "MyActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planned_roadwork_checker);

        plannedRoadworksErrorMessage=(TextView)findViewById(R.id.plannedRoadworkErrorMessage);
        //plannedRoadworksErrorMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green);
        //plannedRoadworksErrorMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green,0,0,0);

        plannedRoadworksEditRoad=(EditText)findViewById(R.id.plannedRoadworksEditRoad);
        plannedRoadworksDate=(EditText)findViewById(R.id.plannedRoadworksDateEdit);
        plannedRoadworksDate.setOnClickListener(this);

        plannedRoadworksButtonPlanned=(Button)findViewById(R.id.plannedSwaptoPlanned);
        plannedRoadworksButtonPlanned.setOnClickListener(this);
        currentRoadworksButtonPlanned=(Button)findViewById(R.id.plannedSwaptoCurrent);
        currentRoadworksButtonPlanned.setOnClickListener(this);
        plannedRoadworkSearch=(Button)findViewById(R.id.plannedRoadworkButton);
        plannedRoadworkSearch.setOnClickListener(this);

        plannedRoadworksRecyclerview=(RecyclerView)findViewById(R.id.plannedRoadworksRecyclerview);
        plannedRoadworksRecyclerview.setLayoutManager(new LinearLayoutManager(this));
    }
    public void onClick(View v){
        switch (v.getId()){

            case R.id.plannedSwaptoCurrent:
                openPlannedCurrentRoadwork();
                break;
            case R.id.plannedSwaptoPlanned:
                openPlannedPlannedRoadwork();
                break;
            case R.id.plannedRoadworkButton:
                userPlannedRoadworksRoad=plannedRoadworksEditRoad.getText().toString();
                userPlannedRoadworksDate=plannedRoadworksDate.getText().toString();
                plannedRoadworksErrorMessage.setText("");
                //userPlannedRoadworksDate="";

                PlannedRoadworkChecker.runPlannedRoadworksChecker runPlannedAsyn = new PlannedRoadworkChecker.runPlannedRoadworksChecker();
                runPlannedAsyn.execute();
                break;
            case R.id.plannedRoadworksDateEdit:
                new DatePickerDialog(this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

        }

    }

    public void openPlannedCurrentRoadwork(){
        Intent openRoadwork =new Intent(this, RoadWorkChecker.class);
        startActivity(openRoadwork);
    }

    public void openPlannedPlannedRoadwork(){
        Intent openPlannedRoadwork =new Intent(this, PlannedRoadworkChecker.class);
        startActivity(openPlannedRoadwork);
    }

    private class runPlannedRoadworksChecker extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {


        }

        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL(roadwoksurl);
                InputStream inputStream = url.openConnection().getInputStream();

                userFeedModelList=parseFeed(inputStream);
                mFeedModelList= new ArrayList<>();

                int count= userFeedModelList.size();

                if((!userPlannedRoadworksRoad.equals("")) & (userPlannedRoadworksDate.equals("")) ) {
                    for (int i=0; i<count; i++){
                        if(userFeedModelList.get(i).title.contains(userPlannedRoadworksRoad)){
                            mFeedModelList.add(userFeedModelList.get(i));

                            //findDates(userFeedModelList.get(i).description);

                        }
                    }
                    userPlannedRoadworksDate="";
                    userPlannedRoadworksDate="";
                }
                else if((!userPlannedRoadworksDate.equals("")) & (userPlannedRoadworksRoad.equals(""))){

                    for (int i=0; i<count; i++){

                        if(userPlannedRoadworksDate.compareTo(userFeedModelList.get(i).start)>=0 && userPlannedRoadworksDate.compareTo(userFeedModelList.get(i).end)<=0){

                            mFeedModelList.add(userFeedModelList.get(i));
                        }
                        System.out.println(userFeedModelList.get(i).start);
                    }
                    System.out.println(userPlannedRoadworksDate);
//                    System.out.println();
                    userPlannedRoadworksDate="";
                    userPlannedRoadworksDate="";
                }
                else if ((!userPlannedRoadworksRoad.equals("")) & (!userPlannedRoadworksDate.equals(""))){
                    for (int i=0; i<count; i++){
                        if(userPlannedRoadworksDate.compareTo(userFeedModelList.get(i).start)>=0 && userPlannedRoadworksDate.compareTo(userFeedModelList.get(i).end)<=0 && userFeedModelList.get(i).title.contains(userPlannedRoadworksRoad)){
                            mFeedModelList.add(userFeedModelList.get(i));

                        }
                    }
                    userPlannedRoadworksDate="";
                    userPlannedRoadworksDate="";
                }
                else{
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


        @SuppressLint("NewApi")
        protected void onPostExecute(Boolean success) {


            if (success) {

                // Fill RecyclerView
                if(mFeedModelList.size()==0){


                    plannedRoadworksErrorMessage.setText("The Road has no Roadworks or Doesn't Exist");
                }
                else {
                    plannedRoadworksRecyclerview.setAdapter(new RecyclerAdapterPlannedRoadworks(mFeedModelList));
                }
            } else {
                Toast.makeText(PlannedRoadworkChecker.this,
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

    private void updateLabel() {
        String myFormat = "dd/MMMM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        //DateFormat df = new SimpleDateFormat(myFormat);
       // Date userDate=myCalendar.getTime();
        //System.out.print(userDate);

        plannedRoadworksDate.setText(sdf.format(myCalendar.getTime()));
        //enteredDate=sdf.format((myCalendar.getTime()));
        //userPlannedRoadworksDate=plannedRoadworksDate.getText().toString();
        //userPlannedRoadworksDate=sdf.format(myCalendar.getTime());
//        userDate=sdf.format(myCalendar.getTime().toString());
//        System.out.print(userPlannedRoadworksDate);
        //userPlannedRoadworksDate = userPlannedRoadworksRoad.replace("null", "");
        //System.out.println(userPlannedRoadworksDate);
        //System.out.println(userDate);
        //System.out.println(plannedRoadworksDate);
    }

    public void makeStringStartDate(String startDate){
        try {
            plannedRoadworksStart = new SimpleDateFormat("dd/MMMM/yyyy").parse(startDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void makeStringEndDates(String endDate){
        try {
            plannedRoadworksEnd = new SimpleDateFormat("dd/MMMM/yyyy").parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void makeStartDate(String start) {
        try {
            Date startDate1 = new SimpleDateFormat("dd/MMMM/yyyy").parse(start);
            startDate = startDate1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void makeEndDate(String end){
        try {
            Date endDate1 = new SimpleDateFormat("dd/MMMM/yyyy").parse(end);
            endDate = endDate1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

}

