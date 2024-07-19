<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
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
    <li>아 이 디 : <input type="text" name="userId" autocomplete="off"/></li>
    <li>비밀번호 : <input type="password" name="userPw" /></li>
    <c:if test="${not empty message}">
        <li>${message}</li>
    </c:if>
</ul>
<input type="submit" name="로그인" />
</form>
</body>
</html>