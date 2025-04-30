
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" href="/images/bok.ico">
<title>오현철 과장 업무관리</title>
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1"/>
</head>
<body>
    <h1 align="center">
        <img src="/images/profile.jsp" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px;"/>
        &nbsp;오현철 과장 업무관리
    </h1>
    <p style="text-align: center;">
        <input type="button" value="달력" class="login-input" onclick="location.href='/manager/calendar/ohhyonchul';"/><br/>
        <input type="button" value="ISO 20022" class="login-input" onclick="location.href='/manager/calendar/iso20022';"/><br/>
        <input type="button" value="연락처" class="login-input" onclick="location.href='/manager/callbook';"/><br/>
        <input type="button" value="로그아웃" class="login-input" onclick="location.href='/logout';"/><br/>
    </p>   
</body>
</html>