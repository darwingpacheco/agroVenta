package com.example.agroventa;

import android.os.CountDownTimer;

public class SessionManager {
    private static SessionManager instance;
    private static final long SESSION_DURATION = 5 * 60 * 1000; // 5 minutos en milisegundos
    private long remainingTime = SESSION_DURATION;
    private boolean isSessionActive = false;
    private CountDownTimer sessionTimer;
    private boolean isLogin;
    private boolean expiredTime;
    private boolean clickNoLogin;
    private SessionListener listener;

    private SessionManager() {
        isSessionActive = false;
        isLogin = false;
        expiredTime = false;
        clickNoLogin = false;
        remainingTime = 0;
    }

    // Obtener la instancia única
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void startSession(SessionListener listener) {
        this.listener = listener;
        isSessionActive = true;
        if (sessionTimer != null) {
            sessionTimer.cancel();
        }

        sessionTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;
                if (listener != null) {
                    listener.onSessionTick(remainingTime);
                }
            }

            @Override
            public void onFinish() {
                isSessionActive = false;
                remainingTime = SESSION_DURATION;
                if (listener != null) {
                    listener.onSessionExpired();
                }
            }
        };
        sessionTimer.start();
    }

    public void stopSession() {
        isSessionActive = false;
        if (sessionTimer != null) {
            sessionTimer.cancel();
        }
        remainingTime = SESSION_DURATION;
    }

    public boolean isSessionActive() {
        return isSessionActive;
    }

    public void setSessionActive(boolean sessionActive) {
        isSessionActive = sessionActive;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(long remainingTime) {
        this.remainingTime = remainingTime;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public boolean isExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(boolean expiredTime) {
        this.expiredTime = expiredTime;
    }

    // Restablecer sesión
    public void resetSession() {
        isSessionActive = false;
        isLogin = false;
        expiredTime = false;
        remainingTime = 0;
    }

    public boolean isClickNoLogin() {
        return clickNoLogin;
    }

    public void setClickNoLogin(boolean clickNoLogin) {
        this.clickNoLogin = clickNoLogin;
    }
}

