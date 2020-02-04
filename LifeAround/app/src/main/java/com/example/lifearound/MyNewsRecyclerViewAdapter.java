package com.example.lifearound;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lifearound.NewsFragment.OnListFragmentInteractionListener;
import com.example.lifearound.data.model.Notification;
import com.example.lifearound.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder> {

    private final List<Notification> mNotifications;
    private final OnListFragmentInteractionListener mListener;

    public MyNewsRecyclerViewAdapter(List<Notification> items, OnListFragmentInteractionListener listener) {
        mNotifications = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mNotifications.get(position);
        holder.mMsgView.setText(mNotifications.get(position).message);
        holder.mTimeView.setText(mNotifications.get(position).getTimeStr());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onNewsFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mMsgView;
        public final TextView mTimeView;
        public Notification mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mMsgView = (TextView) view.findViewById(R.id.notf_message);   ////
            mTimeView = (TextView) view.findViewById(R.id.notf_time);  ////
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMsgView.getText() + "'";
        }
    }
}
