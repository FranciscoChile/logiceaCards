package com.logicea.model;

public enum Status {
    TODO("To Do"), INPROGRESS("In progress"), DONE("Done");

    private final String status;

    private Status (String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

}
