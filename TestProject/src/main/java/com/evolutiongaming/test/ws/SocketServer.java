/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evolutiongaming.test.ws;

import com.evolutiongaming.test.entity.Ping;
import com.evolutiongaming.test.entity.Table;
import com.evolutiongaming.test.entity.User;
import java.io.StringReader;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author AKS513
 */
@ApplicationScoped
@ServerEndpoint(value = "/actions")
public class SocketServer {
    
    @Inject
    private UserHandler userHandler;
    
    @OnError
    public void onError(Throwable error) {
        System.out.println(error);
    }
    
    @OnMessage
    public void handleMessage(final String message, final Session session) {
        try (final JsonReader reader = Json.createReader(new StringReader(message))) {
            final JsonObject jsonMessage = reader.readObject();
            System.out.printf("Client: %s",jsonMessage);
            final String action = jsonMessage.getString("$type");
            
            if(action.equalsIgnoreCase("login")) {
                final User user = new User();
                user.setUsername(jsonMessage.getString("username"));
                user.setPassword(jsonMessage.getString("password"));
                userHandler.addSession(session);
                userHandler.loginUser(user);
            }
            if(action.equalsIgnoreCase("logoff")) {
                userHandler.logoffUser(jsonMessage.getString("username"));
                userHandler.removeSession(session);                
            }
            if(action.equalsIgnoreCase("ping")) {
                final Ping ping = new Ping();
                ping.setId(jsonMessage.getInt("seq"));
                userHandler.pingUser(ping);
            }
            if(action.equalsIgnoreCase("authorize_user")) {
                final User user = new User();
                user.setUsername(jsonMessage.getString("name"));
                userHandler.authorize(user.getUsername());
            }
            if(action.equalsIgnoreCase("add_table")) {
                final Table table = new Table();
                table.setId(jsonMessage.getInt("after_id"));
                table.setName(jsonMessage.getJsonObject("table").getString("name"));
                userHandler.addTable(table);
            }
            if(action.equalsIgnoreCase("update_table")) {
                final JsonObject tableObject = jsonMessage.getJsonObject("table");
                final Table table = userHandler.getTableById(tableObject.getInt("id"));    
                table.setName(tableObject.getString("name"));
                userHandler.editTable(table.getId());
            }
            if(action.equalsIgnoreCase("remove_table")) {
                final Table table = userHandler.getTableById(jsonMessage.getInt("id"));
                userHandler.removeTable(table.getId());
            }
        }
    }
}