<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" href="/images/bok.ico">
<title>Calendar - ${name}</title>
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
</head>
<body>
<h1>Hello, World!</h1>
<form action="/login" method="post">
<ul>
    <li>ID : <input type="text" name="userId" autocomplete="off"/></li>
    <li>비밀번호 : <input type="password" name="userPw" /></li>
    <li>${message}</li>
</ul>
<input type="submit" name="로그인" />
</form>
</body>
</html>