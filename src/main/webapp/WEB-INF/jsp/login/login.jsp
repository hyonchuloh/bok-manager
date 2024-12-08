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
<div style="float: left; padding-left: 10px; padding-top: 9px;">
    <img src="/images/bok_logo.png" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px;"/>
</div>
<h1>&nbsp;&nbsp;IT전략국 오현철 과장 업무관리</h1>

<form action="/login" method="post">
    <p style="text-align: center;">
        <span style="font-size: 15pt;">로그인 하세요!</span><br/><br/>
        <input type="text" name="userId" autocomplete="off" value="${userId}" class="login-input"/><br/>
        <input type="password" name="userPw" class="login-input"/><br/>
        <input type="submit" name="로그인" class="login-input"/><br/>
        <c:if test="${not empty message}">
            ${message}<br/>
        </c:if>
        (문의) hyonchul.oh@bok.or.kr
    </p>
</form>
<p align="center">
    <img src="/images/TheBankOfKorea.png" height="10px"/>
</p>
</body>
</html>