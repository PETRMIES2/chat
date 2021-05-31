package com.sope.websocket;

@FunctionalInterface
public interface VarArgFunction<R, T> {

    R apply(T... args);
}