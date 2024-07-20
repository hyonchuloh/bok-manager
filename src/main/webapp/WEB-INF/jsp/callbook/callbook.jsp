<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="icon" href="/images/bok.ico">
<title>Calendar - ${name}</title>
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
</head>
<body>
<h1>
	연락처
	<div style="float: right; padding-right: 3px; padding-top:0px;font-size: 10pt; color: azure; font-weight: 100;">
		안녕하세요? | <a href="/logout" style="color: azure;">로그아웃</a> 
	</div>
</h1>
	&nbsp;
<table style="width: 100%; table-layout:fixed;" border="1" id="mainTable">
<tr>
    <th>no</th>
    <th>기관명</th>
    <th>부서명</th>
    <th>담당업무</th>
    <th>이름</th>
    <th>연락처</th>
    <th>이메일</th>
    <th>기타</th>
    <th>저장</th>
</tr>
<tr>
    <td> </td>
    <td contenteditable='true'> </td>
    <td contenteditable='true'> </td>
    <td contenteditable='true'> </td>
    <td contenteditable='true'> </td>
    <td contenteditable='true'> </td>
    <td contenteditable='true'> </td>
    <td><input type="button" value="신규" onclick/></td>
</tr>
<c:forEach var="col" items="${row}" varStatus="cal_status">
<tr>
    <td>${col.no}</td>
    <td contenteditable='true' >${col.no}</td>
    <td contenteditable='true' >${col.no}</td>
    <td contenteditable='true' >${col.no}</td>
    <td contenteditable='true' >${col.no}</td>
    <td contenteditable='true' >${col.no}</td>
    <td contenteditable='true' >${col.no}</td>
    <td><input type="button" value="변경" /></td>
</tr>
</c:forEach>
</table>
<form action="/calendar" method="POST">
    <input type="hidden" name="seq" />
    <input type="hidden" name="seq" />
    <input type="hidden" name="seq" />
    <input type="hidden" name="seq" />
    <input type="hidden" name="seq" />
    <input type="hidden" name="seq" />
    <input type="hidden" name="seq" />
    <input type="hidden" name="seq" />
    <input type="hidden" name="seq" />
</form>
</body>
</html>