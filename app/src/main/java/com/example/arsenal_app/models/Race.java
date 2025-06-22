package com.example.arsenal_app.models;

/**
 * Race object used to store all the information regarding an F1 qualifying, sprint or race event.
 */
public class Race {

    private int id;
    private String country;
    private String date;
    private String race;
    private String time;
    private String track;
    private String circuit;

    public Race(int id, String country, String date, String race, String time, String track, String circuit) {
        this.id = id;
        this.country = country;
        this.date = date;
        this.race = race;
        this.time = time;
        this.track = track;
        this.circuit = circuit;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getTime() {
        return time;
    }

    /**
     * Convert from 24h in the format xx:xx to xx:xx(AM/PM)
     * @return String in the 12h format.
     */
    public String getFormattedTime() {
        // Convert from 24h to 12h.
        String[] timeParts = time.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        if (hours < 12){
            return String.format("%02d:%02dAM", hours, minutes);
        }
        return String.format("%d:%02dPM", hours-12, minutes);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getCircuit() {
        return circuit;
    }

    public void setCircuit(String circuit) {
        this.circuit = circuit;
    }
}
