package com.j4velin.pedometer;

public class LeaderBoard {
    String Name;
    String Steps;
    String Time;

    public LeaderBoard() {
    }


    public String getSteps() {
        return Steps;
    }

    public String getTime() {
        return Time;
    }

    public String getName() {
        return Name;
    }

    public LeaderBoard(String name, String steps, String time) {
        Name = name;
        Steps = steps;
        Time = time;
    }
}
