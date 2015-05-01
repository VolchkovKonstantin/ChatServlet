var numberChangeString = -1;
var uniqueId = function () {
    var date = Date.now();
    var random = Math.random() * Math.random();
    return Math.floor(date * random).toString();
};
var theTask = function (user, message) {
    return {
        user: user,
        message: message,
        id: uniqueId()
    };
};
var appState = {
    mainUrl: '/Chat',
    taskList: [],
    token: 'TN11EN'
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
    //connectToServer();
    /*  id = uniqueId();
     var logins = restoreLogin();
     for (var i = 0; i < logins.length; i++) {
     if(logins[i].id == id) {
     break;
     }
     }
     var login = logins[i].user;
     if (login != "") {
     document.getElementById('nameLogin').innerHTML = login;
     }
     */
    //restore();
    setInterval(function () {
        restore();
    }, 5000);

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
       /* if (event.type == 'click' && event.target.classList.contains('iconChange')) {
            changeClick(event.target.parentNode);
        }
        if (event.type == 'click' && event.target.classList.contains('iconDelete')) {
            deleteClick(event.target.parentNode);
        }
        */
    }
}
function createAllTask(allTask, fromIndex) {
    var index = fromIndex;
    var size = allTask.length;
    for (var i = 0; i < size; i++) {
        if (addFromIndex(allTask[i], index))
            index++;
    }
    var block = document.getElementsByClassName("chat");
    block[0].scrollTop = block[0].scrollHeight;
}

function addFromIndex(task, fromIndex) {
    var items = document.getElementsByClassName('items')[0];
    var bool = true;
    if (items.childElementCount <= fromIndex) {
        if (task.message == "") {
            // appState.taskList[fromIndex] = task;
            return;
        }
        else {
            var item = createItem(task);
            items.appendChild(item);
            appState.taskList[fromIndex] = task;
        }
    }
    else {
        if (task.message == "") {
            items.removeChild(items.childNodes[fromIndex]);
            appState.taskList.splice(fromIndex, 1);
            bool = false;
            return;
        }
        else {
            var item = createItem(task);
            items.replaceChild(item, items.childNodes[fromIndex]);
            appState.taskList[fromIndex] = task;
        }
    }
    return bool;
}
/*function storeChat(listToSave) {
 if (typeof(Storage) == "undefined") {
 alert('localStorage is not accessible');
 return;
 }
 localStorage.setItem("Chat", JSON.stringify(listToSave));
 }
 function storeLogin(saveLogin) {
 if (typeof(Storage) == "undefined") {
 alert('localStorage is not accessible');
 return;
 }
 localStorage.setItem("Login", JSON.stringify(savelogin));
 }
 function restoreChat() {
 if (typeof(Storage) == "undefined") {
 alert('localStorage is not accessible');
 return;
 }
 var item = localStorage.getItem("Chat");
 return item && JSON.parse(item);
 }

 function restoreLogin() {
 if (typeof(Storage) == "undefined") {
 alert('localStorage is not accessible');
 return;
 }
 var item = localStorage.getItem("Login");
 return item && JSON.parse(item);
 }
 */
function pickLogin() {
    var login = document.getElementById('inputLogin');
    document.getElementById('nameLogin').innerHTML = login.value;
    // postRquestLogin(login.value);
    //storeLogin(login.value);
    login.value = '';
    var items = document.getElementsByClassName('items') [0];
    for (var i = 0; i < items.childElementCount; i++) {
        if (document.getElementById('nameLogin').innerHTML !== items.childNodes[i].childNodes[0].textContent && items.childNodes[i].childElementCount == 4) {
            items.childNodes[i].removeChild(items.childNodes[i].childNodes[3]);
            items.childNodes[i].removeChild(items.childNodes[i].childNodes[2]);
            items.childNodes[i].classList.remove('border2');
            items.childNodes[i].classList.add('border1');
            items.childNodes[i].childNodes[0].classList.remove('fat');
        }
        else if (document.getElementById('nameLogin').innerHTML == items.childNodes[i].childNodes[0].textContent && items.childNodes[i].childElementCount != 4) {
            items.childNodes[i].childNodes[0].classList.add('fat');
            items.childNodes[i].classList.remove('border1');
            items.childNodes[i].classList.add('border2');
            var deletes = '<img class="iconDelete">';
            items.childNodes[i].innerHTML += deletes;
            deletes = '<img class="iconChange">';
            items.childNodes[i].innerHTML += deletes;
        }
    }
}
function buttonClick() {
    var message = document.getElementById('inputMessage').value;
    var user = document.getElementById('nameLogin').innerHTML;
    //   taskList = restoreChat();
    if (user.localeCompare("") == 0) {
        alert("input Login!!!")
        return;
    }

    if (!message)
        return;
    var newTask = theTask(user, message);
    if (numberChangeString == -1) {
        addTodoInternal(newTask);
        appState.taskList.push(newTask);
        addTodo(newTask, function () {
            //    output(appState);
        });
    }
    else {
        addTodoInternal(newTask);
        changeRequest(appState.taskList[numberChangeString], function () {
            numberChangeString = -1;
        });
    }

    document.getElementById('inputMessage').value = '';
    //storeChat(taskList);
}
function addTodo(task, continueWith) {
    post(appState.mainUrl, JSON.stringify(task), function () {
        //restore(continueWith);
    });
}
function addTodoInternal(task) {
    if (task.message == "") {
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
    var htmlAsText = '<div class="item border1" data-task-id="индефикатор">' +
        '<p>Логин</p><p>Сообщение</p><img class="iconDelete">' +
        '<img class="iconChange"></div>';
    temp.innerHTML = htmlAsText;
    updateItem(temp.firstChild, task);
    return temp.firstChild;
}
function updateItem(divItem, task) {
    divItem.setAttribute('data-task-id', task.id);
    divItem.childNodes[0].textContent = task.user;
    divItem.childNodes[1].textContent = task.message;
    if (document.getElementById('nameLogin').innerHTML !== task.user) {
        divItem.removeChild(divItem.childNodes[3]);
        divItem.removeChild(divItem.childNodes[2]);
    }
    else {
        divItem.childNodes[0].classList.add('fat');
        divItem.classList.remove('border1');
        divItem.classList.add('border2');
    }
}
function deleteRequest(task, continueWith) {
    deletes(appState.mainUrl + '?id=' + task.id, JSON.stringify(task), function () {
        continueWith();
    });
}
function deleteClick(item) {
    // taskList = restoreChat();
    items = document.getElementsByClassName('items')[0];
    var id = item.attributes['data-task-id'].value;
    for (var i = 0; i < appState.taskList.length; i++) {
        if (appState.taskList[i].id == id) {
            break;
        }
    }
    items.removeChild(item);
    deleteRequest(appState.taskList[i], function () {
    });
    //appState.taskList.splice(i, 1);
    //storeChat(taskList);
}
function changeRequest(task, continueWith) {
    put(appState.mainUrl + '?id=' + task.id, JSON.stringify(task), function () {
        continueWith();
    });
}
function changeClick(item) {
    document.getElementById('inputMessage').value = item.childNodes[1].textContent;
    deleteMessage(item);
    document.getElementById('inputMessage').focus();
}
function deleteMessage(item) {
    //taskList = restoreChat();
    var items = document.getElementsByClassName('items')[0];
    var id = item.attributes['data-task-id'].value;
    for (var i = 0; i < appState.taskList.length; i++) {
        if (appState.taskList[i].id == id) {
            numberChangeString = i;
            break;
        }
    }
    items.childNodes[numberChangeString].childNodes[1].textContent = '';
    appState.taskList[numberChangeString].message = "User Change message";
    changeRequest(appState.taskList[numberChangeString], function () {
    });
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
function connectToServer(){
    var req = new XMLHttpRequest();
    req.open("GET");
    req.send();
    req.onreadystatechange = function() {
        if(req.readyState == 4) {

        }
    }
}
function restore(continueWith) {//!!!!!!!!!!!!!!!!!!!!!!!!!!!!! после гет
    var url = '/Chat' + '?token=' + appState.token;
    get(url, function (responseText) {
        console.assert(responseText != null);
        var response = JSON.parse(responseText)
        appState.token = response.token;
        createAllTask(response.messages, 0);
        // output(appState);
        continueWith && continueWith();
    });

    /*  allTask = restoreChat();
     taskList = restoreChat();
     var login = restoreLogin();
     if (login != "") {
     document.getElementById('nameLogin').innerHTML = login;
     document.getElementById('onlineUser').innerHTML = login;
     }
     createAllTask(allTask, taskList);
     */
}

function output(value) {
    var output = document.getElementById('shellChat');
    document.getElementsByClassName('serverPosition')[0].outerHTML = "<div class=\"serverPosition\"><h4>Server:<img src=\"resources/css/images/redButton.png\"></h4></div>"
    output.innerText = JSON.stringify(value, null, 2);
}

//->!!!!!!!!!!!!!!!!!!!!!!!!!!!
function defaultErrorHandler(message) {
    console.error(message);
    output(message);
}

function get(url, continueWith, continueWithError) {
    ajax('GET', url, null, continueWith, continueWithError);
}

function post(url, data, continueWith, continueWithError) {
    ajax('POST', url, data, continueWith, continueWithError);
}

function put(url, data, continueWith, continueWithError) {
    ajax('PUT', url, data, continueWith, continueWithError);
}

function deletes(url, data, continueWith, continueWithError) {
    ajax('DELETE', url, data, continueWith, continueWithError);
}

function isError(text) {
    if (text == "")
        return false;

    try {
        var obj = JSON.parse(text);
    } catch (ex) {
        return true;
    }

    return !!obj.error;
}
//<-!!!!!!!!!!!!!!!!!!!!!!!
function ajax(method, url, data, continueWith, continueWithError) {
    var xhr = new XMLHttpRequest();

    continueWithError = continueWithError || defaultErrorHandler;
    xhr.open(method || 'GET', url);

    xhr.onload = function () {
        if (xhr.readyState !== 4)
            return;

        if (xhr.status != 200) {
            continueWithError('Error on the server side, response ' + xhr.status);
            return;
        }

        if (isError(xhr.responseText)) {
            continueWithError('Error on the server side, response ' + xhr.responseText);
            return;
        }

        continueWith(xhr.responseText);
    };

    xhr.ontimeout = function () {
        continueWithError('Server timed out !');
    }

    xhr.onerror = function (e) {
        var errMsg = 'Server connection error !\n' +
            '\n' +
            'Check if \n' +
            '- server is active\n' +
            '- server sends header "Access-Control-Allow-Origin:*"';

        continueWithError(errMsg);
    };

    xhr.send(data);
}
//!!!!!!!!!!!!!->
window.onerror = function (err) {
    output(err.toString());
}