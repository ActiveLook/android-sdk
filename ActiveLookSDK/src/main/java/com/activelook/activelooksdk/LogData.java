package com.activelook.activelooksdk;

public class LogData {

    public LogData(String message, LogTypeMessage type) {
        this.message = message;
        this.type = type;
    }

    public String message = "";
    public LogTypeMessage type = LogTypeMessage.TYPE_NONE;

}


