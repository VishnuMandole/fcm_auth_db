package com.vishnumandole.todo.assignment.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Event {
    public String uid;
    public String author;
    public String title;
    public String body;
    public String date;
    public Map<String, Boolean> stars = new HashMap<>();

    public Event() {
     }

    public Event(String uid, String author, String title, String body, String date) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.date = date;
    }

     @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("date", date);
        result.put("stars", stars);

        return result;
    }

}
