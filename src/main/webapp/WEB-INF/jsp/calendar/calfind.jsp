<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>FEP CalFinder</title>
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<style>
tr:hover {
	background: #E6EBF5 !important;
}
</style>
</head>
<body>
<div style="float: right; padding-right: 0px; padding-top:0px;">
	<img src="/images/image02.png" height="30px" />
</div>
<h2 style="color: #004888;">
	<img src="/images/profile.jpg" style="width: 30px ;border-radius: 70%; overflow: hidden; border:1px solid #CCC"/>
	&nbsp;캘린더 검색
</h2>
<form name="frm" action="calfind">
<ul>
	<li>
		사용자명 <input type="text" name="name" value="${name}" style="width: 80px;" />
		년도검색 <input type="text" name="year" value="${year}" style="width: 80px;" />
		검색어 <input type="text" name="searchkey" value="${searchkey}" style="width: 150px;" />
		<input type="submit" value="SUBMIT" />
	</li>
</ul>
</form>
<table style="width: 100%; table-layout:fixed;" border=1>
<tr>
	<th style="with: 100px;">key</th>
	<th>value</th>
</tr>
<c:forEach var="row" items="${result}">
<tr>
	<td>${row.key}</td>
	<td>${row.value}</td>
</tr>
</c:forEach>
</table>
<p align="center">
	<img src="/images/image04.png" height="10px"/>
</p>
</body>
</html>