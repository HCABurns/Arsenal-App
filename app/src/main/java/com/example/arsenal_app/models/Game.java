package com.example.arsenal_app.models;

public class Game {

    private String id;
    private String opponent;
    private String date;
    private String time;

    private String competition;
    private String stadium;
    private String badge_base64;

    public Game(String id, String opponent, String date, String time, String competition, String stadium, String badge_base64) {
        this.id = id;
        this.opponent = opponent;
        this.date = date;
        this.time = time;
        this.competition = competition;
        this.stadium = stadium;
        this.badge_base64 = badge_base64;
    }

    public Game(){

    }

    public static void main(String[] args) {
        Game game = new Game();
        game.stadium = "awd";
        System.out.println(game);
    }

}
