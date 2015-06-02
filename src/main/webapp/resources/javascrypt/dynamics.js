var numberChangeString = -1;
var uniqueId = function () {
    var date = Date.now();
    var random = Math.random() * Math.random();
    return Math.floor(date * random).toString();
};
var theTask = function (user, message, id) {
    return {
        user: user,
        message: message,
        id: id
    };
};
var appState = {
    mainUrl: '/Chat',
    taskList: []
};
/*var Users = function (id,user){
 return {
 id: id,
 user: user
 }
 };
 */
function run() {
    var Container = document.getElementsByClassName('container')[0];
    Container.addEventListener('click', delegateEvent);
    var login = restoreLogin();
    if (login != "") {
        document.getElementById('nameLogin').innerHTML = login;
    }
    poll(true);
}
function delegateEvent(event) {
    if (event.type == 'click' && (event.target.classList.contains('btn-sent') || event.target.classList.contains('btn-my'))) {
        buttonClick();
    }
    if (document.getElementById('inputMessage').value != "" && numberChangeString != -1) {
        if (event.type == 'click' && event.target.classList.contains('input')) {
            return;
        }
        alert('Please finish input');
        document.getElementById('inputMessage').focus();
        return;
    }
    else {
        if (event.type == 'click' && event.target.classList.contains('btn-info')) {
            pickLogin();
        }
        if (event.type == 'click' && event.target.classList.contains('iconChange')) {
            changeClick(event.target.parentNode);
        }
        if (event.type == 'click' && event.target.classList.contains('iconDelete')) {
            deleteClick(event.target.parentNode);
        }
    }
}
function createAllTask(allTask) {
    var size = allTask.length;
    for (var i = 0; i < size; i++) {
        if (allTask[i].request == "POST") {
            var newTask = theTask(allTask[i].user, allTask[i].message, allTask[i].id);
            addTodoInternal(newTask);
        }
        if (allTask[i].request == "DELETE") {
            var message = document.getElementById(allTask[i].id.toString());
            var items = document.getElementsByClassName('items') [0];//deleteItem(message,allTask[i]);
            for (var j = 0; j < items.childNodes.length; j++) {
                if (items.childNodes[j].id === allTask[i].id.toString()) {
                    break;
                }
            }
            message.parentNode.removeChild(message);
            appState.taskList.splice(j, 1);
        }
        if (allTask[i].request == "PUT") {
            var message = document.getElementById(allTask[i].id.toString());
            changeItem(message, allTask[i]);
        }
    }
}
function restoreLogin() {
    if(typeof(Storage) == "undefined") {
        alert('localStorage is not accessible');
        return;
    }
    var item = localStorage.getItem("Login");
    return item && JSON.parse(item);
}
function storeLogin(login) {
    if(typeof(Storage) == "undefined") {
        alert('localStorage is not accessible');
        return;
    }
    localStorage.setItem("Login", JSON.stringify(login));
}
function pickLogin() {
    var login = document.getElementById('inputLogin');
    document.getElementById('nameLogin').innerHTML = login.value;
    // postRquestLogin(login.value);
    storeLogin(login.value);
    login.value = '';
    var items = document.getElementsByClassName('items') [0];
    for (var i = 0; i < items.childNodes.length; i++) {
        updateItem(items.childNodes[i]);
    }
    var block = document.getElementsByClassName("chat");
    block[0].scrollTop = block[0].scrollHeight;
}
function buttonClick() {
    var message = document.getElementById('inputMessage').value;
    var user = document.getElementById('nameLogin').innerHTML;
    if (user.localeCompare("") == 0) {
        alert("input Login!!!")
        return;
    }

    if (!message)
        return;
    if (numberChangeString == -1) {
        var newTask = theTask(user, message, uniqueId());
        //addTodoInternal(newTask);
        addTodo(newTask, function () {
        });
    }
    else {
        //addTodoInternal(newTask);
        var block = appState.taskList[numberChangeString];
        block.message = message;
        changeRequest(block, function () {
            numberChangeString = -1;
        });
    }

    document.getElementById('inputMessage').value = '';
}
function addTodo(task) {
    post(JSON.stringify(task));
}
function addTodoInternal(task) {
    if (task.message == "") {
        return;
    }
    if (task.message =="DeleteMessage"){
        return;
    }
    var items = document.getElementsByClassName('items')[0];
    if (numberChangeString != -1) {
        items.childNodes[numberChangeString].childNodes[1].innerHTML = task.message;
        appState.taskList[numberChangeString].message = task.message;
    }
    else {
        var item = createItem(task);
        items.appendChild(item);
        appState.taskList.push(task);
        var block = document.getElementsByClassName("chat");
        block[0].scrollTop = block[0].scrollHeight;
    }
}
function createItem(task) {
    var temp = document.createElement('div');
    var htmlAsText = '<div class="item border1" id=' + task.id + '><p>' + task.user + '</p><p>' + task.message + '</p></div>';
    temp.innerHTML = htmlAsText;
    updateItem(temp.firstChild);
    return temp.firstChild;
}
function changeItem(divItem, task) {
    divItem.childNodes[0].textContent = task.user;
    divItem.childNodes[1].textContent = task.message;
}
function updateItem(divItem) {
    if (document.getElementById('nameLogin').innerHTML !== divItem.childNodes[0].innerText && divItem.childNodes.length > 2) {
        divItem.removeChild(divItem.childNodes[3]);
        divItem.removeChild(divItem.childNodes[2]);
        divItem.classList.remove('border2');
        divItem.classList.add('border1');
        divItem.childNodes[0].classList.remove('fat');
    }
    else if (document.getElementById('nameLogin').innerHTML == divItem.childNodes[0].innerText && divItem.childNodes.length < 3) {
        divItem.childNodes[0].classList.add('fat');
        divItem.classList.remove('border1');
        divItem.classList.add('border2');
        var deletes = '<img class="iconDelete">';
        divItem.innerHTML += deletes;
        deletes = '<img class="iconChange">';
        divItem.innerHTML += deletes;
    }
}
function deleteRequest(task) {
    deletes(JSON.stringify(task));
}
function deleteClick(item) {
    items = document.getElementsByClassName('items')[0];
    var id = item.attributes['id'].value;
    for (var i = 0; i < items.childNodes.length; i++) {
        if (appState.taskList[i].id.toString() === id) {
            break;
        }
    }
    deleteRequest(appState.taskList[i]);
}
function changeRequest(task) {
    put(JSON.stringify(task));
}
function changeClick(item) {
    document.getElementById('inputMessage').value = item.childNodes[1].textContent;
    deleteMessage(item);
    document.getElementById('inputMessage').focus();
}
function deleteMessage(item) {
    var items = document.getElementsByClassName('items')[0];
    var id = item.attributes['id'].value;
    for (var i = 0; i < items.childNodes.length; i++) {
        if (appState.taskList[i].id.toString() === id) {
            numberChangeString = i;
            break;
        }
    }
    items.childNodes[numberChangeString].childNodes[1].textContent = '';
}
function isEnter() {
    if (event.keyCode == 13) {
        pickLogin();
    }
}
function isShiftEnter() {
    if (event.keyCode == 13 && event.shiftKey == false) {
        buttonClick();
        event.preventDefault();
    }
}
function output(value) {
    var output = document.getElementById('shellChat');
    document.getElementsByClassName('serverPosition')[0].outerHTML = "<div class=\"serverPosition\"><h4>Server:<img src=\"resources/css/images/redButton.png\"></h4></div>"
    output.innerText = JSON.stringify(value, null, 2);
}

function post(data) {
    ajax('POST', data);
}

function put(data) {
    ajax('PUT', data);
}

function deletes(data) {
    ajax('DELETE', data);
}

function ajax(method, data) {
    $.ajax({
        url: appState.mainUrl,
        type: method,
        data: data,
        success: function (temp) {
        },
        error: function () {
        }
    });
}
window.onerror = function (err) {
    output(err.toString());
}
var flag = true;
function poll(isFirst) {
    $.ajax({
        url: appState.mainUrl + "?flag=" + isFirst,
        type: 'GET',
        dataType: 'json',
        success: function(responseText){
            flag = true;
            var messages = responseText.messages;
            createAllTask(messages);
        },
        error: function(responseText){
            if (flag) {
                flag = false;
                appState.taskList = [];
            }
            output(responseText);
        },
        complete: function() {
            setTimeout(function(){poll(!flag)},1000);
        }
    });
}
