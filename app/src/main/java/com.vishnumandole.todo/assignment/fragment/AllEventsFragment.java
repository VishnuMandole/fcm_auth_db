package com.vishnumandole.todo.assignment.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;


public class AllEventsFragment extends EventListFragment{

    public AllEventsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query recentEventsQuery = databaseReference.child("events")
                .limitToFirst(100);
        return recentEventsQuery;
    }

}
