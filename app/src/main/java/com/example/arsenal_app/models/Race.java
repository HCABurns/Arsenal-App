package com.example.arsenal_app.models;

public class Race {

    public int id;
    public String country;
    public String date;
    public String race;
    public String time;

    public Race(int id, String country, String date, String race, String time) {
        this.id = id;
        this.country = country;
        this.date = date;
        this.race = race;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Race{" +
                "id=" + id +
                ", country='" + country + '\'' +
                ", date='" + date + '\'' +
                ", name='" + race + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
