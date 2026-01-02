<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="icon" href="/images/bok.ico">
<link rel="apple-touch-icon" href="/images/bok_logo.png">
<link rel="apple-touch-icon-precomposed" href="/images/bok_logo.png">
<title>게시판 목록</title>
<link rel="stylesheet" type="text/css" href="/css/bokwire-week.css" />
<script>
function saveItem(boardSeq) { 
    boardElement = document.getElementById("board" + boardSeq);
    boardContent = boardElement ? boardElement.innerHTML : "";

    document.frm.categoryIndex.value = boardSeq;
    document.frm.contents.value = boardContent;
    document.frm.submit();
}
function saveItemOnTabeKey(boardSeq) {
    if (event.keyCode == 9) { // Tab key
        event.preventDefault(); // 기본 탭 동작 방지
        document.frm.categoryIndex.value = boardSeq;
        document.frm.contents.value = document.getElementById("board" + boardSeq).innerHTML
        document.frm.submit();
    }
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
        | <a href="/logout" style="color: azure; text-decoration: none; font-weight: 400;">로그아웃</a>
    </div>
    <h1>
        <img src="/images/profile.jpg" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px; cursor: pointer;" onclick="location.href='/manager/board';"/>
        &nbsp;메모장
    </h1>
    <ul>
        <li>
            ${resultMsg}
        </li>
    </ul>
    <table border="1" style="width: 100%; height: 100%; table-layout:fixed; font-size:10pt; font-family: 'd2coding';" id="boardTable">
        <tr>
            <th>메모1 <input type="button" value="저장" onclick="saveItem(1);"/></th>
            <th>메모2 <input type="button" value="저장" onclick="saveItem(2);"/></th>
            <th>메모3 <input type="button" value="저장" onclick="saveItem(3);"/></th>
        </tr>
        <tr style="vertical-align: top;">
            <td style="word-wrap:break-word;">
                <div contenteditable='true' id="board1" onkeydown="saveItemOnTabeKey(1);" onpaste="handlePaste(event);">
                    ${board1}
                </div>
            </td>
            <td style="word-wrap:break-word;">
                <div contenteditable='true' id="board2" onkeydown="saveItemOnTabeKey(2);" onpaste="handlePaste(event);">
                    ${board2}
                </div>
            </td>
            <td style="word-wrap:break-word;">
                <div contenteditable='true' id="board3" onkeydown="saveItemOnTabeKey(3);" onpaste="handlePaste(event);">
                    ${board3}
                </div>
            </td>
    </table>
    <input type="textarea" width="1536px" height="30px"/>
</body>
<form name="frm" method="post" action="/manager/board-save">
    <input type="hidden" name="categoryIndex" value="" />
    <input type="hidden" name="contents" value="" />
</html>