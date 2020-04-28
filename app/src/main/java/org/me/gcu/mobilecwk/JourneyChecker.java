package org.me.gcu.mobilecwk;
//S1822006
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class JourneyChecker extends AppCompatActivity implements View.OnClickListener {

    private String apiKey="AIzaSyDq2L517naec3Pwrl8eFtVipyNXt6ZsgQw";

    String start = "geelong";
    String finish = "melbourne";
    String region = "au";

    Button jounreyButton;





    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.journey_checker);
        jounreyButton=(Button)findViewById(R.id.journeyCheckButton);
        jounreyButton.setOnClickListener(this);


    }

    public void onClick(View v){
        System.out.println(calculateRoute(start,finish,region));

    }

    private static String calculateRoute(String start, String finish, String region){
        String result = "";
        String urlString = "https://maps.googleapis.com/maps/api/directions/json?sensor=false&origin="+start+"&destination="+finish+"&key=AIzaSyDq2L517naec3Pwrl8eFtVipyNXt6ZsgQw";
        System.out.println(urlString);

        try{
            URL urlGoogleDirService = new URL(urlString);

            HttpURLConnection urlGoogleDirCon = (HttpURLConnection)urlGoogleDirService.openConnection();

            urlGoogleDirCon.setAllowUserInteraction( false );
            urlGoogleDirCon.setDoInput( true );
            urlGoogleDirCon.setDoOutput( false );
            urlGoogleDirCon.setUseCaches( true );
            urlGoogleDirCon.setRequestMethod("GET");
            urlGoogleDirCon.connect();

            DocumentBuilderFactory factoryDir = DocumentBuilderFactory.newInstance();
            DocumentBuilder parserDirInfo = factoryDir.newDocumentBuilder();
            Document docDir = parserDirInfo.parse(urlGoogleDirCon.getInputStream());

            urlGoogleDirCon.disconnect();
            result = docDir.toString();


            return result;
        }

        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }

    };
}
