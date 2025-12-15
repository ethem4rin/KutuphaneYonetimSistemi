package com.kutuphane.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Tüm Repository sınıflarının uygulayacağı temel CRUD işlemlerini tanımlayan Abstract Sınıf.
 */
public abstract class BaseRepository<T> {

    // Alt sınıfların DB bağlantı mekanizmasını implemente etmesini zorlar
    protected abstract Connection getOpenConnection() throws SQLException;

    public abstract T save(T entity);
    public abstract T update(T entity);
    public abstract void delete(int id);
    public abstract T findById(int id);
    public abstract List<T> findAll();
}