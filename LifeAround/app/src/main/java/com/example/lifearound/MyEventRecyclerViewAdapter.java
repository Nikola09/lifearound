package com.example.lifearound;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lifearound.EventsFragment.OnListFragmentInteractionListener;
import com.example.lifearound.data.model.MapEvent;
import com.example.lifearound.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyEventRecyclerViewAdapter extends RecyclerView.Adapter<MyEventRecyclerViewAdapter.ViewHolder> {

    private final List<MapEvent> mEvents;
    private final OnListFragmentInteractionListener mListener;

    public MyEventRecyclerViewAdapter(List<MapEvent> items, OnListFragmentInteractionListener listener) {
        mEvents = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mEvents.get(position);
        holder.nameTextView.setText(mEvents.get(position).getName());
        holder.organizerTextView.setText("Organizer: "+mEvents.get(position).getOrganizer());
        holder.datetimeTextView.setText(mEvents.get(position).getStartTimeStr()+" - "+mEvents.get(position).getEndTimeStr());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onEventsFragmentInteraction(holder.mItem,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView nameTextView;
        public final TextView organizerTextView;
        public final TextView datetimeTextView;
        public MapEvent mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            nameTextView = (TextView) view.findViewById(R.id.event_name);
            organizerTextView = (TextView) view.findViewById(R.id.event_organizer);
            datetimeTextView = (TextView) view.findViewById(R.id.event_datetime);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + nameTextView.getText() + "'";
        }
    }
}
