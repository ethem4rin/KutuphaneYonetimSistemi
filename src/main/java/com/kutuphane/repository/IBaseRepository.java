package com.kutuphane.repository;

import java.util.List;

public interface IBaseRepository<T> {

    T save(T entity);
    T update(T entity);
    void delete(int id);
    T findById(int id);
    List<T> findAll();
}