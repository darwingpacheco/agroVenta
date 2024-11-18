package com.example.agroventa;

public interface SessionListener {
    void onSessionTick(long remainingTime); // Se llama en cada tick del temporizador
    void onSessionExpired(); // Se llama cuando la sesión expira
}

