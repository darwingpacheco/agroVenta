package com.example.agroventa.data;

public class DispatchPlan {
    private final String city;
    private final int currentLoadKg;
    private final int minimumLoadKg;
    private final int maxLoadKg;
    private final String dispatchMode;
    private final String statusMessage;

    public DispatchPlan(String city, int currentLoadKg, int minimumLoadKg, int maxLoadKg,
                        String dispatchMode, String statusMessage) {
        this.city = city;
        this.currentLoadKg = currentLoadKg;
        this.minimumLoadKg = minimumLoadKg;
        this.maxLoadKg = maxLoadKg;
        this.dispatchMode = dispatchMode;
        this.statusMessage = statusMessage;
    }

    public String getCity() {
        return city;
    }

    public int getCurrentLoadKg() {
        return currentLoadKg;
    }

    public int getMinimumLoadKg() {
        return minimumLoadKg;
    }

    public int getMaxLoadKg() {
        return maxLoadKg;
    }

    public String getDispatchMode() {
        return dispatchMode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}

