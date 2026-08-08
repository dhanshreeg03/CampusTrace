package com.campus.lostfound.dto;

public class CreateItemRequest {
    private String name;
    private String description;
    private String location;
    private String date; // String format to allow validation and parsing
    private String type; // String format to allow validation (LOST/FOUND)
    private String contact;

    public CreateItemRequest() {}

    public CreateItemRequest(String name, String description, String location, String date, String type, String contact) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.date = date;
        this.type = type;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
