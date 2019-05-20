package com.vishnumandole.todo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vishnumandole.todo.assignment.models.Event;

import java.util.Map;

public class EventDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_POST_KEY = "post_key";
    public static final String EXTRA_EVENT_UID = "event_uid";
    private static final String TAG = "EventDetailActivity";
    private DatabaseReference mEventReference;
    private DatabaseReference mEventUserEventsReference;
    private ValueEventListener mEventListener;
    private String mEventKey, event_uid;
    private TextView mAuthorView,mDateView;
    private EditText mTitleView;
    private EditText mBodyView;
    private Button mUpdateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        // Get post key from intent
        mEventKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        event_uid = getIntent().getStringExtra(EXTRA_EVENT_UID);
        if (mEventKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }
        // Initialize Database
        mEventReference = FirebaseDatabase.getInstance().getReference().child("events").child(mEventKey);
        mEventUserEventsReference = FirebaseDatabase.getInstance().getReference().child("user-events").child(getUid()).child(mEventKey);


        // Initialize Views
        mAuthorView = findViewById(R.id.postAuthor);
        mTitleView = findViewById(R.id.postTitle);
        mBodyView = findViewById(R.id.postBody);
        mDateView = findViewById(R.id.date);
        mUpdateButton = findViewById(R.id.buttonEventComment);
        mUpdateButton.setOnClickListener(this);

        if (!event_uid.equals(getUid())) {
            mTitleView.setEnabled(false);
            mBodyView.setEnabled(false);
            mUpdateButton.setVisibility(View.GONE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Event object and use the values to update the UI
                Event post = dataSnapshot.getValue(Event.class);
                // [START_EXCLUDE]
                mAuthorView.setText(post.author);
                mTitleView.setText(post.title);
                mBodyView.setText(post.body);
                mDateView.setText(post.date);
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Event failed, log a message
                Log.w(TAG, "loadEvent:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(EventDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mEventReference.addValueEventListener(postListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mEventListener = postListener;

    }

    @Override
    public void onStop() {
        super.onStop();
        // Remove post value event listener
        if (mEventListener != null) {
            mEventReference.removeEventListener(mEventListener);
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonEventComment) {


            // Title is required
            if (TextUtils.isEmpty(mTitleView.getText().toString())) {
                mTitleView.setError("REQUIRED");
                return;
            }

            // Body is required
            if (TextUtils.isEmpty(mBodyView.getText().toString())) {
                mBodyView.setError("REQUIRED");
                return;
            }

            updateEvent(getUid(), mAuthorView.getText().toString(), mTitleView.getText().toString(), mBodyView.getText().toString(),mDateView.getText().toString());
        }
    }

    // [START write_fan_out]
    private void updateEvent(String userId, String username, String title, String body, String date) {
        Event post = new Event(userId, username, title, body,date);
        Map<String, Object> postValues = post.toMap();
        mEventReference.updateChildren(postValues);
        mEventUserEventsReference.updateChildren(postValues, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Toast.makeText(EventDetailActivity.this, "Event Updated Succesfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


}
