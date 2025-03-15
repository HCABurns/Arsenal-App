package com.example.arsenal_app.models;

public class Game {

    private String id;
    private String opponent;
    private String date;
    private String time;

    private String competition;
    private String stadium;
    private String badge_base64;

    public Game(String opponent, String date, String time, String competition, String stadium, String badge_base64) {
        this.opponent = opponent;
        this.date = date;
        this.time = time;
        this.competition = competition;
        this.stadium = stadium;
        this.badge_base64 = badge_base64;
    }

    public Game(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public String getBadge_base64() {
        return badge_base64;
    }

    public void setBadge_base64(String badge_base64) {
        this.badge_base64 = badge_base64;
    }

    @Override
    public String toString() {
        return "Game{" +
                "opponent='" + opponent + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", competition='" + competition + '\'' +
                ", stadium='" + stadium + '\'' +
                ", badge_base64='" + badge_base64 + '\'' +
                '}';
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.stadium = "awd";
        System.out.println(game);
    }

}
