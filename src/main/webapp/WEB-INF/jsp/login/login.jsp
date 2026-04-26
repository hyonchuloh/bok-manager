<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" href="/images/tube.ico">
<link rel="apple-touch-icon" href="/images/tube-apple-touch-icon.png">
<link rel="apple-touch-icon-precomposed" href="/images/tube-apple-touch-icon-precomposed.png">
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1"/>
<title>오현철 과장 업무관리</title>
<style>
body {
    width: auto;
}
</style>
<script>
    function goSubmit() {
        if (window.event.keyCode == 13 ) {
            document.frm.submit();
        }
    }
</script>
</head>
<body onload="document.getElementById('userId').focus();">
    <h1 style="text-align: center;">
        <img src="/images/profile.jpg" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px;"/>
        &nbsp;오현철 과장 업무관리
    </h1>
    <table align="center" style="border: 0px;">
        <tr>
            <td style="text-align: left; font-size: 16pt; color: black; 
                       line-height: 1.5em; border-left:#CCCCCC 3px solid; 
                       border-right:0px; border-top: 0px; border-bottom: 0px; 
                       padding-left:15px; ">
                ${message}
            </td>
        </tr>
    </table>
    <form action="/login" method="post" name="frm">
        <p style="text-align: center;">
            <input type="text" name="userId" id="userId" autocomplete="off" value="${userId}" class="login-input"/><br/>
            <input type="password" name="userPw" class="login-input" onkeydown="goSubmit();"/><br/>
            <input type="button" value="로그인" class="login-input" onclick="document.frm.submit();"/><br/>
            <p style="font-size: 10pt; text-align: center; color: gray;">hc5642@me.com</p>
        </p>
    </form>
</body>
</html>