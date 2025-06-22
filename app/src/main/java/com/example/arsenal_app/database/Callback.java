package com.example.arsenal_app.database;

public interface Callback<T> {
    void onSuccess(T result);
    void onError(Exception e);
}