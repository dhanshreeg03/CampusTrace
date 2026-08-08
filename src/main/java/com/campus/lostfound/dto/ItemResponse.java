package com.campus.lostfound.dto;

import com.campus.lostfound.model.Item;

public class ItemResponse {
    private int id;
    private String name;
    private String description;
    private String location;
    private String date;
    private String type;
    private String status;
    private String contact;

    public ItemResponse() {}

    public ItemResponse(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.location = item.getLocation();
        this.date = item.getDate() != null ? item.getDate().toString() : null;
        this.type = item.getType() != null ? item.getType().name() : null;
        this.status = item.getStatus() != null ? item.getStatus().name() : null;
        this.contact = item.getContact();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
