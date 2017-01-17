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

            if ("add".equalsIgnoreCase(jsonMessage.getString("action"))) {
                final User user = new User();
                user.setName(jsonMessage.getString("name"));
                sessionHandler.addUser(user);
            }

            if ("remove".equalsIgnoreCase(jsonMessage.getString("action"))) {
                final int id = jsonMessage.getInt("id");
                sessionHandler.removeUser(id);
            }
            
            if ("ping".equalsIgnoreCase(jsonMessage.getString("action"))) {
                final int id = jsonMessage.getInt("id");
                sessionHandler.pingUser(id);
            }
        }
    }
}