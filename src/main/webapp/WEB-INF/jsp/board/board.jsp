<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="icon" href="/images/tube.ico">
<link rel="apple-touch-icon" href="/images/tube-apple-touch-icon.png">
<link rel="apple-touch-icon-precomposed" href="/images/tube-apple-touch-icon-precomposed.png">
<title>게시판 목록</title>
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<script>
function saveItem() { 
    boardTitleElement = document.getElementById("latestBoardTitle");
    boardContentElement = document.getElementById("latestBoardContents");
    boardTitle = boardTitleElement ? boardTitleElement.innerText : "";
    boardContent = boardContentElement ? boardContentElement.innerHTML : "";

    document.frm.title.value = boardTitle;
    document.frm.contents.value = boardContent;
    document.frm.submit();
}
function handlePaste(event) {
    event.preventDefault();
    const text = event.clipboardData.getData('text/plain');
    document.execCommand('insertText', false, text);
}
</script>
</head>
<body>
    <div style="float: right; padding-right: 10px; padding-top: 7px;font-size: 10pt; color: azure; font-weight: 100; text-align: right;">
        안녕하세요? 오늘은 <b>${yearInt }년 ${monthInt }월 ${dayInt }일</b> 입니다.</br>
        <a href="/manager/callbook" style="color: azure; text-decoration: none; font-weight: 400;">연락처</a>
        | <a href="/manager/calendar" style="color: azure; text-decoration: none; font-weight: 400;">달력</a>
        | <a href="/manager/calendar-week" style="color: azure; text-decoration: none; font-weight: 400;">달력(week only)</a>
        | <a href="/manager/board" style="color: azure; text-decoration: none; font-weight: 400;">메모장</a>
        | <a href="/admin/users" style="color: azure; text-decoration: none; font-weight: 400;">사용자 관리</a>
        | <a href="/logout" style="color: azure; text-decoration: none; font-weight: 400;">로그아웃</a>
    </div>
    <h1>
        <img src="/images/profile.jpg" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px; cursor: pointer;" onclick="location.href='/manager/board';"/>
        &nbsp;메모장
    </h1>
    <ul>
        <li>
            <input type="button" value="저장" onclick="saveItem();"/>
            <input type="button" value="신규작성" onclick="document.location.href='/manager/board?seq=0'"/>
            ${resultMsg}
        </li>
    </ul>
    <table style="width: 100%; height: 100%; border: 0px solid black;">
        <tr style="vertical-align: top;">
            <!-- 좌측 게시판 목록 -->
            <td style="width: 50%; border: 0px solid black;">
                <table style="width: 100%">
                    <thead>
                        <tr>
                            <th style="width: 10%;">번호</th>
                            <th style="width: 70%;">제목</th>
                            <th style="width: 20%;">작성일</th>
                        </tr>
                    </thead>
                    <c:forEach var="board" items="${boardList}">
                    <tr style="cursor: pointer;" onclick="location.href='/manager/board?seq=${board.seq}'">
                        <td style="text-align: center;">${board.seq}</td>
                        <td style="text-align: left;">${board.title}</td>
                        <td style="color: gray; text-align: center;">${board.createdAt}</td>
                    </tr>
                    </c:forEach>
                </table>
            </td>
            <!-- 우측 게시판 내용 -->
            <td style="width: 50%; border: 0px solid black;">
                <table style="width: 100%">
                    <tr>
                        <th style="width: 10%;">${latestBoard.seq}</th>
                        <th style="width: 70%;" id="latestBoardTitle" contenteditable="true">${latestBoard.title}</th>
                        <th style="width: 20%; color: gray; " >${latestBoard.createdAt}</th>
                    </tr>
                    <tr>
                        <td colspan="3" id="latestBoardContents" contenteditable="true" onpaste="handlePaste(event)" style="word-wrap: break-word;">
                            ${latestBoard.contents}
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
<form name="frm" method="post" action="/manager/board-save">
    <input type="hidden" name="seq" value="${latestBoard.seq}" />
    <input type="hidden" name="title" value="" />
    <input type="hidden" name="contents" value="" />
</html>