/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evolutiongaming.ws;

import com.evolutiongaming.entity.User;
import java.io.StringReader;

import javax.enterprise.context.ApplicationScoped;

import javax.inject.Inject;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author aks513
 */
@ApplicationScoped
@ServerEndpoint(value = "/test")
public class WebSocketServer {

    @Inject
    private UserSessionHandler sessionHandler;

    @OnOpen
    public void open(final Session session) {
        sessionHandler.addSession(session);
    }

    @OnClose
    public void close(final Session session) {
        sessionHandler.removeSession(session);
    }

    @OnMessage
    public void handleMessage(final String message, final Session session) {
        try (final JsonReader reader = Json.createReader(new StringReader(message))) {
            final JsonObject jsonMessage = reader.readObject();
            
            if (jsonMessage.getString("$type").equalsIgnoreCase("login")) {
                final User user = new User();
                user.setName(jsonMessage.getString("username"));
                user.setPassword(jsonMessage.getString("password"));
                sessionHandler.addUser(user);
            }
            
            if (jsonMessage.getString("$type").equalsIgnoreCase("remove")) {
                final int id = jsonMessage.getInt("id");
                sessionHandler.removeUser(id);
            }
            
            if (jsonMessage.getString("$type").equalsIgnoreCase("ping")) {
                
            }
        }
    }
}