/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var socket = new WebSocket("ws://localhost:8080/TestProject/actions");
socket.onmessage = onMessage;
window.onload = init;
var pingSeq = 0;
var tableSeq = 0;

/*Init page*/
function  init() {
    document.getElementById("userName").innerHTML = '';
    document.getElementById("loginUserForm").style.display = '';
    document.getElementById("content").style.display = "none";
    document.getElementById("pingDiv").style.display = "none";
    document.getElementById("addTableForm").style.display = "none";
    document.getElementById("tables").style.display = "none";
}

/*Web socket initialization*/
function onMessage(event) {
    var object = JSON.parse(event.data);      
    
    if(object.$type === "login_successful") {
        document.getElementById("userName").innerHTML = object.user_type;
        document.getElementById("loginUserForm").style.display = "none";
        document.getElementById("content").style.display = '';  
        document.getElementById("pingDiv").style.display = '';
        document.getElementById("tables").style.display = '';
        
        var userAction = {
            $type: "authorize_user",
            name: document.getElementById("userName").innerHTML
        };
        socket.send(JSON.stringify(userAction));
    }
    if(object.$type === "not_authorized") {
        document.getElementById("addTableBtn").style.display = "none";
    }
    if(object.$type === "login_failed") {
        alert('Incorrect username or password');
        init();
    }
    if(object.$type === "logoff") {
        init();
    }
    if(object.$type === "pong") {
        document.getElementById("pingDiv").innerHTML +=
            "<br/>" + object.seq + ": " + document.getElementById("userName").innerHTML + "-> "+ object.$type;
    }    
    if(object.$type === "table_added") {
        var content = document.getElementById("tables");
    
        var tableDiv = document.createElement("div");
        tableDiv.setAttribute("id", object.id);
        tableDiv.setAttribute("class", "table");
        tableDiv.style.backgroundColor = setElementColor();
        content.appendChild(tableDiv);
    
        var tableName = document.createElement("span");
        tableName.setAttribute("class", "tableName");
        tableName.innerHTML = object.name;
        tableDiv.appendChild(tableName);
        
        if(document.getElementById("userName").innerHTML === "admin") {
            var tableOption = document.createElement("span");
            tableOption.setAttribute("class", "removeTable");
            tableOption.innerHTML = "\
                <a href=\"#\" OnClick=editTableForm(" + object.id + ")>Edit</a>\n\
                <a href=\"#\" OnClick=removeTableElement(" + object.id + ")>Remove</a>\n\
            ";
            tableDiv.appendChild(tableOption);
        }
    }
    if(object.$type === "table_updated") {
        var element = document.getElementById(object.id);
        element.children[0].innerHTML = object.name;
        element.children[1].innerHTML = "\
            <a href=\"#\" OnClick=editTableForm(" + object.id + ")>Edit</a>\n\
            <a href=\"#\" OnClick=removeTableElement(" + object.id + ")>Remove</a>\n\
        ";
    }
    if(object.$type === "table_removed") {
        document.getElementById(object.id).remove();
    }
}

/*Login user*/
function login() {
    var form = document.getElementById("loginUserForm");
    
    var userAction = {
        $type: "login",
        username: form.elements["user_name"].value,
        password: form.elements["password_name"].value
    };
    socket.send(JSON.stringify(userAction));    
}

/*Logoff user*/
function logoff() {    
    var userAction = {
        $type: "logoff",
        username: document.getElementById("userName").innerHTML
    };
    socket.send(JSON.stringify(userAction));    
    init();
}

/*Ping user*/
function pingUser() {    
    pingSeq++;
    
    var userAction = {
        $type: "ping",
        seq: pingSeq
    };
    socket.send(JSON.stringify(userAction));    
}

/*Add table*/
function  addTable() {
    document.getElementById("addTableBtn").style.display = "none";
    document.getElementById("addTableForm").style.display = '';
    
}

function  insertTable() {
    var element = document.getElementById("addTableForm").elements["table_name"];
    
    if(element.value.length === 0)
        alert('Table name could not be empty');
    else {
        tableSeq++;
        
        var userAction = {
            $type: "add_table",
            after_id: tableSeq,
            table: {
                name: element.value
            }
        };
        
        socket.send(JSON.stringify(userAction)); 
        document.getElementById("addTableBtn").style.display = '';
        document.getElementById("addTableForm").style.display = "none";
        
    }
}

function setElementColor() {
    var array = new Array("#5eb85e","#0f90d1","#db524d","#c2a00c");
    return array[Math.floor(Math.random()*array.length)];
}

/*Edit table*/
function editTableForm(id) {
    var element = document.getElementById(id);
    element.children[1].innerHTML = "\
        <input type=\"text\" name=\"table_edit\" id=\"table_edit\" value=\""+element.children[0].innerHTML+"\">\n\
        <a href=\"#\" OnClick=editTableElement(" + id + ")>Edit</a>\n\
    ";
}

function editTableElement(id) {
    var userAction = {
        $type: "update_table",
        table: {
            id: id,
            name: document.getElementById("table_edit").value
        }
    };
    socket.send(JSON.stringify(userAction));     
}

/*Remove table*/
function removeTableElement(id) {
    var userAction = {
        $type: "remove_table",
        id: id
    };
    socket.send(JSON.stringify(userAction));
}