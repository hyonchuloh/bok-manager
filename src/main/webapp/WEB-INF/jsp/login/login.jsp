<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" href="/images/bok.ico">
<link rel="apple-touch-icon" href="/images/bok_logo.png">
<link rel="apple-touch-icon-precomposed" href="/images/bok_logo.png">
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
</head>
<body>
<h1>Hello, World!</h1>

<form action="/login" method="post">
    <p style="text-align: center;">
        <span style="font-size: 15pt;">로그인하세요!</span><br/>
        <input type="text" name="userId" autocomplete="off" value="${userId}" class="login-input"/><br/>
        <input type="password" name="userPw" class="login-input" value="password"/><br/>
        <input type="submit" name="로그인" class="login-input"/><br/>
        <c:if test="${not empty message}">
            ${message}<br/>
        </c:if>
    </p>
</form>
</body>
</html>