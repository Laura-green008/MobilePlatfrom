package org.me.gcu.mobilecwk;
//S1822006
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.DECADES;

public class RecyclerAdapterRoadworks extends RecyclerView.Adapter<RecyclerAdapterRoadworks.FeedModelViewHolder> {

private List<RoadWorkChecker.RssFeedModel> mRssFeedModels;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate today = LocalDate.now();

public static class FeedModelViewHolder extends RecyclerView.ViewHolder {
    private View rssFeedView;

    public FeedModelViewHolder(View v) {
        super(v);
        rssFeedView = v;
    }
}

    public RecyclerAdapterRoadworks(List<RoadWorkChecker.RssFeedModel> rssFeedModels) {

        mRssFeedModels = rssFeedModels;

    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.incidents_rows, parent, false);
        FeedModelViewHolder holder = new FeedModelViewHolder(v);
        return holder;
    }

    //@Override
    public void onBindViewHolder (FeedModelViewHolder holder, int position) {
        final RoadWorkChecker.RssFeedModel rssFeedModel = mRssFeedModels.get(position);
        ((TextView)holder.rssFeedView.findViewById(R.id.incidentsTitleRow)).setText(rssFeedModel.title);
        ((TextView)holder.rssFeedView.findViewById(R.id.incidentsDescriptionRow))
                .setText(rssFeedModel.description);
        ((TextView)holder.rssFeedView.findViewById(R.id.incidentsLinkRow)).setText(rssFeedModel.link);

        makeStartDate(rssFeedModel.start);
        makeEndDate(rssFeedModel.end);

        System.out.println("start date is"+startDate);
        System.out.println("end date is"+endDate);
        System.out.println("today is"+today);
//        int daysBetween=DAYS.between(startDate, endDate);
//        double daysBetween=DAYS.between(startDate, endDate).doubleValue();

        if(startDate != null) {
            System.out.println("Days between today and end date"+DAYS.between(today, endDate));
            System.out.println("Days between start and today"+DAYS.between(startDate,today));
            //System.out.println(today.isBefore(startDate));
            System.out.println(DAYS.between(startDate,endDate));

        }

        if(startDate != null) {
            Long days = DAYS.between(startDate, endDate);
            Long third = days/3;
            System.out.println("Days between"+days);
            System.out.println("Thirds"+third);
            System.out.println("Two Thirds"+third*2);
            System.out.println("Days between today and end date"+DAYS.between(today, endDate));

            if (today.isBefore(startDate) ||DAYS.between(today, endDate) < third|| days<4) {
                ((TextView) holder.rssFeedView.findViewById(R.id.incidentsTitleRow)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.green, 0, 0, 0);
            }  else if ( DAYS.between(today, endDate) > third*2 && third*2 > 4) {
                ((TextView) holder.rssFeedView.findViewById(R.id.incidentsTitleRow)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.red, 0, 0, 0);
            }
            else {
//                if (DAYS.between(today, endDate) > 7) {
                ((TextView) holder.rssFeedView.findViewById(R.id.incidentsTitleRow)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.yellow, 0, 0, 0);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mRssFeedModels.size();
    }


    public void makeStartDate(String start) {
        try {
            Date startDate1 = new SimpleDateFormat("dd/MMMM/yyyy").parse(start);
            startDate = startDate1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void makeEndDate(String end) {
        try {
            Date endDate1 = new SimpleDateFormat("dd/MMMM/yyyy").parse(end);
            endDate = endDate1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}