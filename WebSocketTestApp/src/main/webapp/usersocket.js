/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


window.onload = init;
var socket = new WebSocket("ws://localhost:8080/WebSocketTestAppDemo/test");
socket.onmessage = onMessage;

function onMessage(event) {
    var user = JSON.parse(event.data);
    if (user.action === "add") {
        printUserElement(user);
    }
    if (user.action === "remove") {
        document.getElementById(user.id).remove();
        user.parentNode.removeChild(user);
    }
    if (user.action === "ping") {
        pingUserElement(user);
    }
}

function addUser(name) {
    var userAction = {
        action: "add",
        name: name
    };
    socket.send(JSON.stringify(userAction));
}

function removeUser(element) {
    var userAction = {
        action: "remove",
        id: element
    };
    document.getElementById("loginButton").style.display = '';
    document.getElementById("logging").style.display = "none";
    socket.send(JSON.stringify(userAction));
}

function pingUser(element) {
    var userAction = {
        action: "ping",
        id: element
    };
    socket.send(JSON.stringify(userAction));
}

function printUserElement(user) {
    var content = document.getElementById("content");
    
    var userDiv = document.createElement("div");
    userDiv.setAttribute("id", user.id);
    content.appendChild(userDiv);

    var userName = document.createElement("span");
    userName.setAttribute("class", "userName");
    userName.innerHTML = user.name +"("+user.id+")"+ "\t";
    userDiv.appendChild(userName);

    var removeUser = document.createElement("span");
    removeUser.setAttribute("class", "removeUser");
    removeUser.innerHTML = "<div class=\"button\">\n\
        <a href=\"#\" onClick=removeUser(" + user.id + ")>Log off</a>\n\
    </div>";
    userDiv.appendChild(removeUser);
    
    var pingUser = document.createElement("span");
    pingUser.setAttribute("class", "removeUser");
    pingUser.innerHTML = "<div class=\"button\">\n\
        <a href=\"#\" onClick=pingUser(" + user.id + ")>Ping user</a>\n\
    </div>";
    userDiv.appendChild(pingUser);
}

function pingUserElement(user) {
    var content = document.getElementById("logging");
    
    var headerText = document.createElement("p");
    headerText.innerHTML = "User "+user.id+": System -> Pong";
    content.appendChild(headerText);        
}

function showForm() {
    document.getElementById("addUserForm").style.display = '';
}

function hideForm() {
    document.getElementById("addUserForm").style.display = "none";
}

function formSubmit() {
    var form = document.getElementById("addUserForm");
    var name = form.elements["user_name"].value;
    hideForm();
    document.getElementById("addUserForm").reset();
    addUser(name);
    document.getElementById("loginButton").style.display = "none";
    document.getElementById("logging").style.display = '';
}

function init() {
    hideForm();
    document.getElementById("logging").style.display = "none";
}