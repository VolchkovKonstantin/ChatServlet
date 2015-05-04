<%--
  Created by IntelliJ IDEA.
  User: Xaker
  Date: 04.05.2015
  Time: 20:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:directive.page isErrorPage="true" />
<html>
<head>
    <title>Error!!!!!</title>
</head>
<link rel="stylesheet" href="resources/css/style.css" type="text/css">
<script src="resources/javascrypt/game.js"></script>
<body>
<% switch (response.getStatus()) {
  case 404: %>
<div class="error"><h3>Error 404 not found!! sorry;(((</h3></div>
<embed  src="/resources/css/images/badday/1.jpg"></embed>
<embed  src="/resources/css/images/badday/2.jpg"></embed>
<embed  src="/resources/css/images/badday/3.jpg"></embed>
<embed  src="/resources/css/images/badday/4.png"></embed>
<embed  src="/resources/css/images/badday/5.jpg"></embed>
<embed  src="/resources/css/images/badday/6.jpg"></embed>
<embed  src="/resources/css/images/badday/7.jpg"></embed>
<embed  src="/resources/css/images/badday/8.jpg"></embed>
<embed  src="/resources/css/images/badday/9.jpg"></embed>
<embed  src="/resources/css/images/badday/10.jpg"></embed>
<%  break;
  case 400: %>
<div class="error"><h3>Error 400:Bad Request!!</h3> <h2>So you can reload this page, but first you can play in brilliant game</h2></div>
<button onclick='runGame()'>Run Game</button>
<% break;
  case 500: %>
<div class="error"><h3>Error 500:Internal Server Error!!</h3><h2>OOOH Sorry it's our problem please update page's.We promise we'll fixed this problem</h2></div>
<div class = "center"><embed  src="/resources/css/images/work13.gif"></embed></div>
<%break;}%>
</body>
</html>