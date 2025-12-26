<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" hrref="/images/profile.ico">
<link rel="apple-touch-icon" href="/images/profile.jpg">
<link rel="apple-touch-icon-precomposed" href="/images/profile.jpg">
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<style>
body {
    width: auto;
    background-image: url('/images/background3.png');
    background-repeat: no-repeat;
    background-size: cover;
    background-attachment: fixed;
    background-position: center;
}
</style>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1"/>
<title>오현철 과장 업무관리</title>
</head>
<body onload="document.getElementById('userId').focus();">
<h1 align="center">
    <img src="/images/profile.jpg" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px;"/>
    &nbsp;오현철 과장 업무관리
</h1>

<form action="/login" method="post" name="frm">
    <p style="font-size: 15pt; text-align: center; color: white;">
        로그인 하세요!
    </p>
    <p style="text-align: center;">
        <input type="text" name="userId" id="userId" autocomplete="off" value="${userId}" class="login-input"/><br/>
        <input type="password" name="userPw" class="login-input"/><br/>
        <input type="button" value="로그인" class="login-input" onclick="document.frm.submit();"/><br/>
        <c:if test="${not empty message}">
            ${message}<br/>
        </c:if>
        <p style="font-size: 10pt; text-align: center; color: gray;">hc5642@me.com</p>
    </p>
</form>
</body>
</html>