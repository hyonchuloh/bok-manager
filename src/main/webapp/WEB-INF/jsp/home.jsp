
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" href="/images/tube.ico">
<link rel="apple-touch-icon" href="/images/tube-apple-touch-icon.png">
<link rel="apple-touch-icon-precomposed" href="/images/tube-apple-touch-icon-precomposed.png">
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1"/>
<title>오현철 과장 업무관리</title>
</head>
<body>
    <h1 align="center">
        <img src="/images/profile.jpg" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px;"/>
        &nbsp;오현철 과장 업무관리
    </h1>
    <p style="text-align: center;">
        <button type="button" class="login-input" onclick="location.href='/manager/calendar';"><img src="/images/icons/calendar.png" class="icon"/>달력</button><br/>
        <button type="button" class="login-input" onclick="location.href='/manager/calendar-week';"><img src="/images/icons/calendar-check.png" class="icon"/>달력(Week Only)</button><br/>
        <button type="button" class="login-input" onclick="location.href='/manager/callbook';"><img src="/images/icons/address-book.png" class="icon"/>연락처</button><br/>
        <button type="button" class="login-input" onclick="location.href='/logout';"><img src="/images/icons/sign-out.png" class="icon"/>로그아웃</button><br/>
        <button type="button" class="login-input" onclick="location.href='/admin/users';"><img src="/images/icons/user-gear.png" class="icon"/>사용자 관리</button><br/>
    </p>   
</body>
</html>