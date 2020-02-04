package com.example.lifearound;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lifearound.PeopleFragment.OnListFragmentInteractionListener;
import com.example.lifearound.data.model.LoggedInUser;
import com.example.lifearound.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPeopleRecyclerViewAdapter extends RecyclerView.Adapter<MyPeopleRecyclerViewAdapter.ViewHolder> {

    private final List<LoggedInUser> mUsers;
    private final OnListFragmentInteractionListener mListener;

    public MyPeopleRecyclerViewAdapter(List<LoggedInUser> items, OnListFragmentInteractionListener listener) {
        mUsers = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_people, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mUsers.get(position);
        holder.mNameView.setText(mUsers.get(position).getDisplayName());//getUserId()); //
        holder.mScoreView.setText(String.valueOf(mUsers.get(position).getSocialPoints())); ////getDisplayName()

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onPeopleFragmentInteraction(holder.mItem,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mScoreView;
        public LoggedInUser mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.friend_name);
            mScoreView = (TextView) view.findViewById(R.id.friend_score);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
