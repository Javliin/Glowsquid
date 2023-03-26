package me.javlin.glowsquid.network.interceptor;

import com.github.ffalcinelli.jdivert.exceptions.WinDivertException;

import java.net.UnknownHostException;

public interface IInterceptor {
    void start() throws WinDivertException, UnknownHostException;
    void stop();
}
