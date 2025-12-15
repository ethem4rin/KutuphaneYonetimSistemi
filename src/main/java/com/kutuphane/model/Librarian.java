package com.kutuphane.model;

public class Librarian extends User {

    public Librarian(int id, String username, String password, String fullName) {
        super(id, username, password, fullName, "PERSONEL");
    }
}