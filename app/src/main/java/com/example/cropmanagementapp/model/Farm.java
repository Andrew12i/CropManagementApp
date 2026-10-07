package com.example.cropmanagementapp.model;

public class Farm {

    private long id;
    private String name;
    private String location;

    public Farm() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}