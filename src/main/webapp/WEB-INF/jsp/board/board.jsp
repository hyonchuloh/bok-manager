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
    <div class="h1-right">
        안녕하세요? 오늘은 <b>${yearInt}년 ${monthInt}월 ${dayInt}일</b> 입니다.</br>
        <a href="/manager/callbook" class="h1-link">⭐연락처</a>
        | <a href="/manager/calendar" class="h1-link">🗓️달력</a>
        | <a href="/manager/calendar-week" class="h1-link">🗓️달력(week only)</a>
        | <a href="/manager/board" class="h1-link">📝메모장</a>
        | <a href="/admin/users" class="h1-link">👤사용자 관리</a>
        | <a href="/logout" class="h1-link">🚪로그아웃</a>
    </div>
    <h1>
        <img src="/images/profile.jpg" class="h1-image" onclick="location.href='/manager/board';"/>
        &nbsp;메모장
    </h1>
    <p class="h1-menu">
        <input type="button" class="h1-input" value="저장" onclick="saveItem();"/>
        <input type="button" class="h1-input" value="신규작성" onclick="document.location.href='/manager/board?seq=0'"/>
        ${resultMsg}
    </p>
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
                <input type="button" class="h1-input" value="저장" onclick="saveItem();"/>
            </td>
        </tr>
    </table>
</body>
<form name="frm" method="post" action="/manager/board-save">
    <input type="hidden" name="seq" value="${latestBoard.seq}" />
    <input type="hidden" name="title" value="" />
    <input type="hidden" name="contents" value="" />
</html>