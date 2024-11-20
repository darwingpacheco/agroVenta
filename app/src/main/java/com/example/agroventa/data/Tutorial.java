package com.example.agroventa.data;

public class Tutorial {
    private String title;
    private String videoUrl;

    public Tutorial(String title, String videoUrl) {
        this.title = title;
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}

