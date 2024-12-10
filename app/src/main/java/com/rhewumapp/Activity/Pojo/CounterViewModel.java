package com.rhewumapp.Activity.Pojo;

import androidx.lifecycle.ViewModel;

public class CounterViewModel extends ViewModel {
    private int counter = 0;

    public int getCounter() {
        return counter;
    }

    public void incrementCounter() {
        counter++;
    }
}
