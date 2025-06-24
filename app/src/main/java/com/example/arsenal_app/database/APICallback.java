package com.example.arsenal_app.database;

public interface APICallback<T> {
    void onSuccess(T usid);
    void onError(Exception e);
}