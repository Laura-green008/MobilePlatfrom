package org.me.gcu.mobilecwk;
//S1822006
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapterIncidents extends RecyclerView.Adapter<RecyclerAdapterIncidents.FeedModelViewHolder> {

private List<IncidentsChecker.RssFeedModel> mRssFeedModels;

public static class FeedModelViewHolder extends RecyclerView.ViewHolder {
    private View rssFeedView;

    public FeedModelViewHolder(View v) {
        super(v);
        rssFeedView = v;
    }
}

    public RecyclerAdapterIncidents(List<IncidentsChecker.RssFeedModel> rssFeedModels) {

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
final IncidentsChecker.RssFeedModel rssFeedModel = mRssFeedModels.get(position);
        ((TextView)holder.rssFeedView.findViewById(R.id.incidentsTitleRow)).setText(rssFeedModel.title);
        ((TextView)holder.rssFeedView.findViewById(R.id.incidentsDescriptionRow))
        .setText(rssFeedModel.description);
        ((TextView)holder.rssFeedView.findViewById(R.id.incidentsLinkRow)).setText(rssFeedModel.link);
        }

@Override
public int getItemCount() {
        return mRssFeedModels.size();
        }
        }