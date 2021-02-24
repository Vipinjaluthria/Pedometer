package com.j4velin.pedometer;

public class LeaderBoard {
    String Photo;
    String Name;
    String Steps;
    String Time;
    String Email;

    public LeaderBoard() {
    }

    public String getPhoto() {
        return Photo;
    }

    public String getName() {
        return Name;
    }

    public String getSteps() {
        return Steps;
    }

    public String getTime() {
        return Time;
    }

    public String getEmail() {
        return Email;
    }

    public LeaderBoard(String photo,String name, String steps, String time, String email) {
        Photo = photo;
        Name = name;
        Steps = steps;
        Time = time;
        Email = email;
    }
}
