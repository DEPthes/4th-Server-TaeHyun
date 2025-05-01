package com.hooby.listener;

public interface ServerListener {
    default void onInit() {}
    default void onDestroy() {}
}