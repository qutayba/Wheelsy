package site.qutayba.wheelsy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import site.qutayba.wheelsy.R;
import site.qutayba.wheelsy.databinding.TripsListRowBinding;
import site.qutayba.wheelsy.models.Trip;
import site.qutayba.wheelsy.utilities.OnItemClickListener;

public class TripsDataAdapter extends RecyclerView.Adapter<TripsDataAdapter.TripViewHolder> {

    private ArrayList<Trip> trips;
    private final int WARNINING_LIMIT = 100;
    private final int DANGER_LIMIT = 180;
    private OnItemClickListener<Trip> itemClickListener;

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TripsListRowBinding listItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.trips_list_row, parent, false);
        return new TripViewHolder(listItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        final Trip currentTrip = trips.get(position);
        holder.itemView.setTrip(currentTrip);
        holder.itemView.setIndex(position);
        holder.itemView.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null)
                    itemClickListener.onItemClick(currentTrip);
            }
        });
        setKmViewColor(holder.itemView, currentTrip);
    }

    @Override
    public int getItemCount() {
        if(trips == null)
            return 0;

        return trips.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setTrips(ArrayList<Trip> trips) {
        this.trips = trips;
        notifyDataSetChanged();
    }

    private void setKmViewColor(TripsListRowBinding itemView, Trip trip) {

        int background = R.drawable.row_circle_view;
        int textCOlor = R.color.colorPrimary;

        if(trip.getDistance() > WARNINING_LIMIT) {
            background = R.drawable.row_circle_warning_view;
            textCOlor = R.color.colorWarning;
        }

        if(trip.getDistance() > DANGER_LIMIT) {
            background = R.drawable.row_circle_danger_view;
            textCOlor = R.color.colorAncient;
        }

        itemView.kmView.setBackground(ContextCompat.getDrawable(itemView.getRoot().getContext(), background));
        itemView.kmViewText.setTextColor(ContextCompat.getColor(itemView.getRoot().getContext(), textCOlor));
    }

    class TripViewHolder extends RecyclerView.ViewHolder {

        private TripsListRowBinding itemView;


        public TripViewHolder(@NonNull TripsListRowBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;
        }

        public TripsListRowBinding getItemView() {
            return itemView;
        }
    }


}
