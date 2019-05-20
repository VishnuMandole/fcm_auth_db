package com.vishnumandole.todo.assignment.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.vishnumandole.todo.R;
import com.vishnumandole.todo.assignment.models.Event;

public class EventViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;

    public TextView dateView;
    public TextView bodyView;

    public EventViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.postTitle);
        authorView = itemView.findViewById(R.id.postAuthor);
        dateView = itemView.findViewById(R.id.date);
        bodyView = itemView.findViewById(R.id.postBody);
    }

    public void bindToEvent(Event post) {
        titleView.setText(post.title);
        authorView.setText(post.author);
        bodyView.setText(post.body);
        dateView.setText(post.date);
    }
}
