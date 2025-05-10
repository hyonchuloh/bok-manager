<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="icon" href="/images/bok.ico">
<link rel="apple-touch-icon" href="/images/bok_logo.png">
<link rel="apple-touch-icon-precomposed" href="/images/bok_logo.png">
<title>게시판 목록 - ${name}</title>
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
</head>
<body>
    <div style="float: right; padding-right: 10px; padding-top: 7px;font-size: 10pt; color: azure; font-weight: 100; text-align: right;">
        안녕하세요? 오늘은 ${yearInt }년 ${monthInt }월 ${dayInt }일 입니다.</br>
        <a href="/manager/callbook/${name}" style="color: azure;  font-weight: 400;">연락처</a>
         | <a href="/manager/calendar/${name}" style="color: azure; text-decoration: none; font-weight: 400;">달력</a>
         | <a href="/manager/board/list/${name}" style="color: azure; text-decoration: none; font-weight: 400;">블로그</a>
         | <a href="/manager/calendar/iso20022" style="color: azure; text-decoration: none; font-weight: 400;">ISO 20022</a>
         | <a href="/logout" style="color: azure; text-decoration: none; font-weight: 400;">로그아웃</a>
    </div>
    <h1>
        <img src="/images/bok_logo.png" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px; cursor: pointer;" onclick="location.href='/manager/calendar/${name}';"/>
        &nbsp;게시판 목록
    </h1>
    <table border="1" style="width: 100%; table-layout:fixed;" id="boardTable">
        <thead>
            <tr>
                <th>번호</th>
                <th>제목</th>
                <th>작성자</th>
                <th>작성일</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="item" items="${boardList}">
                <tr>
                    <td>${item.id}</td>
                    <td>${item.title}</td>
                    <td>${item.author}</td>
                    <td>${item.createdDate}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>