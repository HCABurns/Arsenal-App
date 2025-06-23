package com.example.arsenal_app.database;

public interface APICallback<T> {
    void onSuccess(T result);
    void onError(Exception e);
}