package com.kutuphane.observer;

import java.util.ArrayList;
import java.util.List;

public class ObservableData {

    private List<DataObserver> observers = new ArrayList<>();

    public void addObserver(DataObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(DataObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        // Observer'lara haber veriyor
        for (DataObserver observer : observers) {
            observer.updateData();
        }
    }
}