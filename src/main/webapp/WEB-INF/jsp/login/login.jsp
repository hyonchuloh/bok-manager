<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" href="/images/bok.ico">
<link rel="apple-touch-icon" href="/images/bok_logo.png">
<link rel="apple-touch-icon-precomposed" href="/images/bok_logo.png">
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<title>오현철 과장 업무관리</title>
</head>
<body onload="document.getElementById('userId').focus();">
<h1 align="center">
    <img src="/images/profile.jpg" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px;"/>
    &nbsp;오현철 과장 업무관리
</h1>

<form action="/login" method="post">
    <p style="font-size: 15pt; text-align: center">
        로그인 하세요!
    </p>
    <p style="text-align: center;">
        <input type="text" name="userId" id="userId" autocomplete="off" value="${userId}" class="login-input"/><br/>
        <input type="password" name="userPw" class="login-input"/><br/>
        <input type="submit" name="로그인" class="login-input"/><br/>
        <c:if test="${not empty message}">
            ${message}<br/>
        </c:if>
        (문의) hc5642@me.com
    </p>
</form>
</body>
</html>