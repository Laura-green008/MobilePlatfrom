package org.me.gcu.mobilecwk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import static java.time.temporal.ChronoUnit.DAYS;

public class RecyclerAdapterPlannedRoadworks extends RecyclerView.Adapter<RecyclerAdapterPlannedRoadworks.FeedModelViewHolder> {

    private List<PlannedRoadworkChecker.RssFeedModel> mRssFeedModels;

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

    public RecyclerAdapterPlannedRoadworks(List<PlannedRoadworkChecker.RssFeedModel> rssFeedModels) {

        mRssFeedModels = rssFeedModels;

    }

    @Override
    public RecyclerAdapterPlannedRoadworks.FeedModelViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.incidents_rows, parent, false);
        FeedModelViewHolder holder = new FeedModelViewHolder(v);
        return holder;
    }

    //@Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {

        final PlannedRoadworkChecker.RssFeedModel rssFeedModel = mRssFeedModels.get(position);
        ((TextView) holder.rssFeedView.findViewById(R.id.incidentsTitleRow)).setText(rssFeedModel.title);
        ((TextView) holder.rssFeedView.findViewById(R.id.incidentsDescriptionRow))
                .setText(rssFeedModel.description);
        ((TextView) holder.rssFeedView.findViewById(R.id.incidentsLinkRow)).setText(rssFeedModel.link);

        makeStartDate(rssFeedModel.start);
        makeEndDate(rssFeedModel.end);
        System.out.println(startDate);
        System.out.println(endDate);
        System.out.println(today);

        if(startDate != null) {
            Long days = DAYS.between(startDate, endDate);
            Long third = days/3;
            System.out.println(days/3);
            if (today.isBefore(startDate) ||DAYS.between(today, endDate) < third|| days<4 || DAYS.between(today,endDate)<4) {
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
