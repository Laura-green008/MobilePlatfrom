package org.me.gcu.mobilecwk;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button jounreyPlannerstartButton;
    private Button roadWorksButton;
    private Button incidentsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            // restore your state here
        }
//        jounreyPlannerstartButton = (Button)findViewById(R.id.jounreyPlannerStartButton);
//        jounreyPlannerstartButton.setOnClickListener(this);
        roadWorksButton=(Button)findViewById(R.id.roadWorksButton);
        roadWorksButton.setOnClickListener(this);
        incidentsButton=(Button)findViewById(R.id.incidentsButton);
        incidentsButton.setOnClickListener(this);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save your state here
    }

    public void onClick(View v)
    { switch (v.getId()) {

//        case R.id.jounreyPlannerStartButton:
//            // do your code
//            openJourneyChecker();
//
//            break;

        case R.id.roadWorksButton:
            // do your code
            openRoadworkChecker();

            break;

        case R.id.incidentsButton:
            // do your code
            //startProgress();
            openIncidentsChecker();
            break;

        default:
            break;
    }

    }
    public void openRoadworkChecker(){
        Intent openRoadwork =new Intent(this, RoadWorkChecker.class);
        startActivity(openRoadwork);
    }

    public void openJourneyChecker (){
        Intent openJourneyChecker=new Intent(this, JourneyChecker.class);
        startActivity((openJourneyChecker));
    }

    public void openIncidentsChecker(){
        Intent openIncidentsChecker=new Intent(this, IncidentsChecker.class);
        startActivity((openIncidentsChecker));

    }
}
