/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evolutiongaming.ws;

import com.evolutiongaming.entity.User;

import java.io.IOException;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import javax.json.JsonObject;
import javax.json.spi.JsonProvider;

import javax.websocket.Session;

/**
 *
 * @author aks513
 */
@ApplicationScoped
public class UserSessionHandler {
    private int userId = 0;
    private final Set<Session> sessions = new HashSet<>();
    private final Set<User> users = new HashSet<>();
    
    private int pingId = 0;
        
    public void addSession(final Session session) {     
        sessions.add(session);
        
        users.stream().map(user -> loginMessage(user)).forEach(addMessage -> 
            sendToSession(session, addMessage)
        ); 
    }
    
    public void removeSession(final Session session) {   
        sessions.remove(session);
    }
    
    public void addUser(final User user) {  
        user.setId(userId);
        users.add(user);
        userId++;
        sendToAllConnectedSessions(loginMessage(user));
    }
    
    public void removeUser(final int id) { 
        final User user = getUserById(id).get();
        users.remove(user);
        
        final JsonObject removeMessage = removeMessage(id);
        sendToAllConnectedSessions(removeMessage);
    }
      
    private JsonObject loginMessage(final User user) {    
        final JsonProvider provider = JsonProvider.provider();
        final JsonObject addMessage = provider.createObjectBuilder()
                .add("$type", "login").add("id", user.getId())
                .add("username", user.getName()).add("password", user.getPassword())
                .build();
        return addMessage; 
    }
    
    private JsonObject removeMessage(final int id) {    
        final JsonProvider provider = JsonProvider.provider();
        final JsonObject removeMessage = provider.createObjectBuilder()
                .add("$type", "remove").add("id", id).build();
        return removeMessage; 
    }
    
    private Optional<User> getUserById(final int id) {       
        return users.stream().filter(user -> user.getId() == id).findFirst();
    }
    
    private void sendToSession(final Session session, final JsonObject message) { 
        try {
            session.getBasicRemote().sendText(String.format("%s", message));
        } catch (IOException ex) {
            sessions.remove(session);
        }
    }
    
    private void sendToAllConnectedSessions(final JsonObject message) { 
        sessions.stream().forEach(session -> 
            sendToSession(session, message)
        );
    }
}