package me.bonoj.moviebug.ui;

public class ScrollCounter {
    private int adapterSize = 0;
    private int counter = 1;
    private long timeOfLastQuery = 0;

    public void setTimeOfLastQuery(long timeOfLastQuery) {
        this.timeOfLastQuery = timeOfLastQuery;
    }

    public long getTimeOfLastQuery() {
        return timeOfLastQuery;
    }

    public void setAdapterSize(int adapterSize) {
        this.adapterSize = adapterSize;
    }

    public int getAdapterSize() { return adapterSize; }

    public int getCounter() {
        return counter;
    }

    public void incrementCounter() {
        counter++;
    }

    public void resetCounter() {
        counter = 1;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
