/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


window.onload = init;
var socket = new WebSocket("ws://localhost:8080/WebSocketTestApp/test");
socket.onmessage = onMessage;

function init() {
    hideForm();
}

function onMessage(event) {
   var user = JSON.parse(event.data);
    if (user.$type === "login") {
        printUserElement(user);
    }
    if (user.$type === "remove") {
        var username = document.getElementById("currentUser");    
        username.innerHTML = '';
        user.parentNode.removeChild(user);
    }
}

function hideForm() {
    document.getElementById("content").style.display = "none";
    document.getElementById("logging").style.display = "none";
    document.getElementById("addUserForm").style.display = "none";
    document.getElementById("currentUser").style.display = "none";
    document.getElementById("loginButton").style.display = '';
}

function showForm() {
    document.getElementById("addUserForm").style.display = '';
    document.getElementById("loginButton").style.display = "none";
    document.getElementById("currentUser").style.display = "none";
    document.getElementById("content").style.display = "none";
    document.getElementById("logging").style.display = "none";
}

function formSubmit() {
    var form = document.getElementById("addUserForm");
    var username = form.elements["username"].value;
    var password = form.elements["passwordname"].value;
    
    hideForm();
    document.getElementById("addUserForm").reset();
    login(username, password);
    document.getElementById("currentUser").style.display = '';
    document.getElementById("loginButton").style.display = "none";
    document.getElementById("content").style.display = '';
    document.getElementById("logging").style.display = '';
}

function login(username, password) {
    var userAction = {
        $type: "login",
        username: username,
        password: password
    };
    socket.send(JSON.stringify(userAction));
}

function printUserElement(user) {
    var username = document.getElementById("currentUser");    
    username.innerHTML = user.username;
       
    var logoff = document.createElement("span");
    logoff.setAttribute("class", "removeUser");
    logoff.innerHTML = "<p/><p/><div class=\"button\">\n\
        <a href=\"#\" onClick=logoff(" + user.id + ")>Log off</a>\n\
    </div>";
    username.appendChild(logoff);
    
    var ping = document.createElement("span");
    ping.setAttribute("class", "removeUser");
    ping.innerHTML = "<div class=\"button\">\n\
        <a href=\"#\" onClick=pingUser(" + user.username + ")Ping user</a>\n\
    </div>";
    username.appendChild(ping);
    
    if(user.username === "admin") {        
        var addTable = document.createElement("span");
        addTable.setAttribute("class", "removeUser");
        addTable.innerHTML = "<div class=\"button\">\n\
            <a href=\"#\">Add table</a>\n\
        </div>";
        username.appendChild(addTable);
        
        document.getElementById("content").style.display = "none";
    } else {
        var content = document.getElementById("content");
        var subscribe = document.createElement("span");
        subscribe.setAttribute("class", "removeUser");
        subscribe.innerHTML = "<p/><p/><h3>Table name\n\
        <div class=\"button\">\n\
            <a href=\"#\">Subscribe</a>\n\
        </div>\n\
        <div class=\"button\">\n\
            <a href=\"#\">Unsubscribe</a>\n\
        </div></h3>";
        content.appendChild(subscribe);
    }
}

function logoff(id) {
    var userAction = {
        $type: "remove",
        id: id
    };
    document.getElementById("loginButton").style.display = '';
    document.getElementById("content").innerHTML = '';
    document.getElementById("content").style.display = "none";
    document.getElementById("logging").style.display = "none";
    socket.send(JSON.stringify(userAction));
}