package com.vishnumandole.todo.assignment.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.vishnumandole.todo.EventDetailActivity;
import com.vishnumandole.todo.R;
import com.vishnumandole.todo.assignment.models.Event;
import com.vishnumandole.todo.assignment.viewholder.EventViewHolder;

public abstract class EventListFragment extends Fragment {

    private static final String TAG = "EventListFragment";
    AlertDialog dialog = null;
     private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Event, EventViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public EventListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = rootView.findViewById(R.id.messagesList);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(postsQuery, Event.class)
                .build();
        mAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(options) {

            @Override
            public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new EventViewHolder(inflater.inflate(R.layout.item_post, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(EventViewHolder viewHolder, int position, final Event model) {
                final DatabaseReference postRef = getRef(position);
                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch EventDetailActivity
                        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                        intent.putExtra(EventDetailActivity.EXTRA_POST_KEY, postKey);
                        intent.putExtra(EventDetailActivity.EXTRA_EVENT_UID, model.uid);
                        startActivity(intent);
                    }
                });

                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (model.uid.equals(getUid())) {
                            showDeleteEventDialog(postKey);
                        }
                        return true;
                    }
                });
                // Bind Event to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToEvent(model);

            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

    private void showDeleteEventDialog(final String postKey) {
        CharSequence message = getString(R.string.remove_confirmation_box_content);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        dialog = builder.setMessage(message).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Event Removed", Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference().child("events").child(postKey).removeValue();
                FirebaseDatabase.getInstance().getReference().child("user-events").child(getUid()).child(postKey).removeValue();

            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog.cancel();
            }
        }).setIcon(android.R.drawable.ic_dialog_alert).create();
        dialog.setCancelable(true);
        dialog.show();
        return;
    }

}
