package com.vishnumandole.todo.assignment.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class EventFragment extends EventListFragment {

    public EventFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my events
        return databaseReference.child("user-events").child(getUid());
    }
}
