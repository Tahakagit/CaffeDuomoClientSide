package helper;

/**
 * Created by franc on 10/02/2018.
 */

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.franc.caffduomo.R;

import java.util.List;


/**
 * Created by franc on 23/01/2018.
 */

public class MyInboxAdapter extends RecyclerView.Adapter<MyInboxAdapter.ViewHolder> {
    private List<Messages> mDataset;
    Activity activity;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mTextView;
        public TextView mCardView;
        public TextView enemyHealth;

        public boolean isSelected = false;
        CardView cv;

        public ViewHolder(View v) {
            super(v);
            cv = (CardView)itemView.findViewById(R.id.cv_inbox);
            mCardView = (TextView)itemView.findViewById(R.id.message_body);


            mTextView = v;
        }


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyInboxAdapter(List<Messages> myDataset) {
        mDataset = myDataset;
    }


    public  void UpdateAdapter(List<Messages> newDataset){

        mDataset = newDataset;
        notifyDataSetChanged();
    }
    // Create new views (invoked by the layout manager)
    @Override
    public MyInboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_inbox, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
/*
        holder.mCardView.setText(mDataset.get(position).getLocationName());
*/


    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
