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
    
    public void addSession(final Session session) {     
        sessions.add(session);
        
        users.stream().map(user -> createAddMessage(user)).forEach(addMessage -> 
            sendToSession(session, addMessage)
        ); 
    }
    
    public void removeSession(final Session session) {   
        sessions.remove(session);
    }
    
    public List<User> getUsers() {     
        return new ArrayList<>(users);   
    }  
    
    public void addUser(final User user) {  
        user.setId(userId);
        users.add(user);
        userId++;
        final JsonObject addMessage = createAddMessage(user);
        sendToAllConnectedSessions(addMessage);
    }   
    
    public void removeUser(final int id) { 
        final User user = getUserById(id).get();
        users.remove(user);
        
        final JsonObject removeMessage = removeMessage(id);
        sendToAllConnectedSessions(removeMessage);
    }   
    
    public void pingUser(final int id) {
        final User user = getUserById(id).get();
        final JsonObject pingMessage = pingMessage(user);
        sendToAllConnectedSessions(pingMessage);
    }
    
    private Optional<User> getUserById(final int id) {       
        return users.stream().filter(user -> user.getId() == id).findFirst();
    }  
    
    private JsonObject createAddMessage(final User user) {    
        final JsonProvider provider = JsonProvider.provider();
        final JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "add").add("id", user.getId())
                .add("name", user.getName()).build();
        return addMessage; 
    } 
    
    private JsonObject removeMessage(final int id) {    
        final JsonProvider provider = JsonProvider.provider();
        final JsonObject removeMessage = provider.createObjectBuilder()
                .add("action", "remove").add("id", id).build();
        return removeMessage; 
    }
    
    private JsonObject pingMessage(final User user) {    
        final JsonProvider provider = JsonProvider.provider();
        final JsonObject pingMessage = provider.createObjectBuilder()
                .add("action", "ping").add("id", user.getId()).build();
        return pingMessage; 
    }
    
    private void sendToAllConnectedSessions(final JsonObject message) { 
        sessions.stream().forEach(session -> 
            sendToSession(session, message)
        );
    }  
    
    private void sendToSession(final Session session, final JsonObject message) { 
        try {
            session.getBasicRemote().sendText(String.format("%s", message));
        } catch (IOException ex) {
            sessions.remove(session);
        }
    }
}