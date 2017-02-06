/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evolutiongaming.test.ws;

import com.evolutiongaming.test.entity.Ping;
import com.evolutiongaming.test.entity.Table;
import com.evolutiongaming.test.entity.User;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

/**
 *
 * @author AKS513
 */
@ApplicationScoped
public class UserHandler {
    private final Set<Session> sessions = new HashSet<>();
    private final Set<User> users = new HashSet<>();
    private final Set<Table> tables = new HashSet<>();

    public void addSession(final Session session) {
        sessions.add(session);
    }

    public void removeSession(final Session session) {
        sessions.remove(session);
        sendToAllConnectedSessions(logoffMessage());
    }

    public void loginUser(final User user) {        
        sendToAllConnectedSessions(loginMessage(user));
    }

    public void logoffUser(final String username) {
        users.remove(users.stream().filter(
            user -> user.getUsername().equalsIgnoreCase(username)
        ).findFirst().get());
    }
    
    public void pingUser(final Ping ping) {
        sendToAllConnectedSessions(pingMessage(ping));
    }
    
    public void authorize(final String username) {
       if(!username.equalsIgnoreCase("admin"))
           sendToAllConnectedSessions(authorizeMessage());
    }
    
    public void addTable(final Table table) {
        tables.add(table);        
        sendToAllConnectedSessions(createTableMessage(table));
    }
    
    public Table getTableById(final int id) {
        return tables.stream().filter(table -> table.getId() == id).findFirst().get();
    }
    
    public void editTable(final int id) {
        sendToAllConnectedSessions(editTableMessage(getTableById(id))); 
    }
    
    public void removeTable(final int id) {
        sendToAllConnectedSessions(removeTableMessage(getTableById(id)));
    }

    private JsonObject loginMessage(final User user) {
        if (!user.getPassword().isEmpty()
                && (user.getUsername().equalsIgnoreCase("admin") || user.getUsername().equalsIgnoreCase("user"))) {
            users.add(user);

            return JsonProvider.provider().createObjectBuilder()
                    .add("$type", "login_successful").add("user_type", user.getUsername()).build();
        } else {
            return JsonProvider.provider().createObjectBuilder()
                    .add("$type", "login_failed").build();
        }
    }
    
    private JsonObject logoffMessage() {
        return JsonProvider.provider().createObjectBuilder()
            .add("$type", "logoff").build();
    }
    
    private JsonObject pingMessage(final Ping ping) {        
        return JsonProvider.provider().createObjectBuilder()
            .add("$type", "pong").add("seq", ping.getId()).build();
    }
    
    private JsonObject authorizeMessage() {
        return JsonProvider.provider().createObjectBuilder()
            .add("$type", "not_authorized").build();
    }
    
    private JsonObject createTableMessage(final Table table) {
        return JsonProvider.provider().createObjectBuilder()
            .add("$type", "table_added").add("id", table.getId()).add("name", table.getName()).build();
    }
    
    private JsonObject editTableMessage(final Table table) {
        return JsonProvider.provider().createObjectBuilder()
            .add("$type", "table_updated").add("id", table.getId()).add("name", table.getName()).build();
    }
    
    private JsonObject removeTableMessage(final Table table) {
        return JsonProvider.provider().createObjectBuilder()
            .add("$type", "table_removed").add("id", table.getId()).build();
    }

    private void sendToSession(final Session session, final JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            System.out.println(String.format("%s error: %s", UserHandler.class.getName(), ex));
            sessions.remove(session);
        }
    }

    private void sendToAllConnectedSessions(final JsonObject message) {
        System.out.printf("Server: %s", message);
        sessions.stream().forEach(session -> sendToSession(session, message));
    }    
}
