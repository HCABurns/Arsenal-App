package com.example.arsenal_app.models;

public class Game {

    private String opponent;
    private String date;
    private String time;

    private String competition;

    public Game(String opponent, String date, String time, String competition) {
        this.opponent = opponent;
        this.date = date;
        this.time = time;
        this.competition = competition;
    }

    public Game(){

    }

}
