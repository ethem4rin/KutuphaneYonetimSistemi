package com.kutuphane.model;

public abstract class BasePerson {

    private int id;
    private String username;
    private String password;
    private String fullName;
    private String role;

    public BasePerson(int id, String username, String password, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public BasePerson() { }

    public abstract String getAuthorizationLevel();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    protected void setRole(String role) { this.role = role; }
}