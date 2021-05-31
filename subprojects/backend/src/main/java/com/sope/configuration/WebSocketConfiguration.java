package com.sope.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sope.websocket.WebSocketMessageHandler;

@Configuration
@EnableWebSocket
@ComponentScan(basePackages = { "com.sope.websocket" })

public class WebSocketConfiguration implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(websocketMessageHandler(), "/directchat").addInterceptors(new HttpSessionHandshakeInterceptor()).setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler websocketMessageHandler() {
        return new WebSocketMessageHandler();
    }

    @Bean
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        gsonBuilder.setLenient();
        return gsonBuilder.create();
    }

}
