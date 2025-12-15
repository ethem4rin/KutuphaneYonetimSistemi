package com.kutuphane.model;

public class User extends BasePerson {

    public User(int id, String username, String password, String fullName, String role) {
        super(id, username, password, fullName, role);
    }

    public User() {
        super();
    }

    @Override
    public String getAuthorizationLevel() {
        return "DEFAULT";
    }

    public boolean canManagePersonnel() {
        return "ADMIN".equals(getRole()) || "PERSONEL".equals(getRole());
    }
}