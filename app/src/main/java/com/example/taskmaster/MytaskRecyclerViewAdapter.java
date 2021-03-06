package com.example.taskmaster;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmaster.taskFragment.OnListFragmentInteractionListener;
import com.example.taskmaster.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MytaskRecyclerViewAdapter extends RecyclerView.Adapter<MytaskRecyclerViewAdapter.ViewHolder> {

    static final String TAG = "crystal.ViewAdapter";
    private final List<Task> mValues;
    private final OnListFragmentInteractionListener mListener;
    Context mContext;

    public MytaskRecyclerViewAdapter(List<Task> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    // creates a new row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);
        return new ViewHolder(view);
    }

    // Given the holder and the position index, fill in that view with the right data for that position
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).getTitle());


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "it was clicked!");
                Context context = v.getContext();

                if (context.getClass().getName().equals("com.example.taskmaster.AllTasksActivity")) {

                    String description = holder.mItem.body;
                    CharSequence taskDetails = "Detail: " + description;
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, taskDetails, duration);
                    toast.show();
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);

                } else if (context.getClass().getName().equals("com.example.taskmaster.MainActivity")) {

                    Intent goToDetailPage = new Intent(mContext, TaskDetailActivity.class);
                    goToDetailPage.putExtra("title", holder.mItem.title);
                    goToDetailPage.putExtra("body", holder.mItem.body);
                    goToDetailPage.putExtra("state", holder.mItem.state);
                    goToDetailPage.putExtra("image", holder.mItem.image);

                    context.startActivity(goToDetailPage);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public Task mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }
}
