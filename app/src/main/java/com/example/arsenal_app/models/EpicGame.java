package com.example.arsenal_app.models;

public class EpicGame{

        private String name;
        private String time;
        private String cover_base64;
        private String availability;

        public EpicGame(){}

        public String getAvailability() {
                return availability;
        }

        public void setAvailability(String availability) {
                this.availability = availability;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getTime() {
                return time;
        }

        public void setTime(String time) {
                this.time = time;
        }

        public String getCover_base64() {
                return cover_base64;
        }

        public void setCover_base64(String cover_base64) {
                this.cover_base64 = cover_base64;
        }

        @Override
        public String toString() {
                return "EpicGame{" +
                        "name='" + name + '\'' +
                        ", time='" + time + '\'' +
                        ", cover_base64='" + cover_base64 + '\'' +
                        '}';
        }
}
