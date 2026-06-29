<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" href="/images/tube.ico">
<link rel="apple-touch-icon" href="/images/tube-apple-touch-icon.png">
<link rel="apple-touch-icon-precomposed" href="/images/tube-apple-touch-icon-precomposed.png">
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<title>오현철 과장 업무관리</title>
<style>
    /* 그리드 행에 마우스 오버 시 연한 노랑 배경 적용 (헤더 행 제외) */
    #mainTable tr:not(:first-child):hover {
        background-color: #FFF9C4; /* 연한 노랑 */
        transition: background-color 0.15s ease;
    }
    body {
        width: 1536px !important;
    }
</style>
<script>
function insertItem() {
    document.frm.userId.value = document.getElementById("new_userId").innerText;
    document.frm.userPw.value = document.getElementById("new_userPw").innerText;
    document.frm.email.value = document.getElementById("new_email").innerText;
    document.frm.action = "/admin/users-insert";
    document.frm.submit();
}
function editItem(userId) {
    document.frm.userId.value = userId;
    document.frm.userPw.value = document.getElementById("edit_" + userId +"_userPw").innerText;
    document.frm.email.value = document.getElementById("edit_" + userId +"_email").innerText;
    document.frm.submit();
}
function deleteItem(userId) {
    if ( confirm("정말로 삭제하시겠습니까? ["+userId+"]") ) {
        document.frm.userId.value = userId;
        document.frm.action = "/admin/users-delete";
        document.frm.searchKey.value = document.searchFrm.searchKey.value;
        document.frm.submit();
    }
}
</script>
</head>
<body>
    <!-- 헤더부 시작 -->
    <div class="h1-right">
    <table class="h1-right-table">
        <tr class="h1-right-table">
            <td class="h1-right-table">
                안녕하세요? 오늘은 <b>${yearInt}년 ${monthInt}월 ${dayInt}일</b> 입니다.
                <br/>
                <a href="/manager/callbook" >⭐연락처</a>
                | <a href="/manager/calendar" >🗓️달력</a>
                | <a href="/manager/calendar-week">🗓️달력(week only)</a>
                | <a href="/manager/board">📝메모장</a>
                | <a href="/admin/users">👤사용자 관리</a>
                | <a href="/logout">🚪로그아웃</a>
            </td>
            <td rowspan="2" class="h1-right-table">
                &nbsp;&nbsp;&nbsp;<img src="/images/bok_logo.png" class="h1-right-img"/>
            </td>
        </tr>
    </table>
    </div>
    <h1>
        <img src="/images/profile.jpg" class="h1-image" onclick="location.href='/manager/calendar'" >
        &nbsp;사용자 관리
    </h1>
    <!-- 메뉴부 시작 -->
    <table class="h1-menu-table">
        <tr>
            <td class="h1-menu-td">
                ${message}
            </td>
        </tr>
    </table>
    <!-- 메인부 시작 -->
    <table style="table-layout:fixed; width: 100%; font-family: 'd2coding';" id="mainTable">
    <tr>
        <th style="width: 50px;">🗂️구분</th>
        <th style="width: 150px;">🆔사용자ID</th>
        <th style="width: 150px;">🔑사용자PW</th>
        <th style="width: 150px;">✉️이메일</th>
        <th style="width: 150px;">💾저장/삭제</th>
    </tr>
    <tr>
        <th style="width: 50px;">신규</th>
        <td contenteditable='true' id="new_userId"> </td>
        <td contenteditable='true' id="new_userPw"> </td>
        <td contenteditable='true' id="new_email"> </td>
        <td style="text-align:center;"><input type="button" class="menu-input" value="저장" onclick="insertItem();"/></td>
    </tr>
    <c:forEach var="row" items="${list}" varStatus="users_status">
    <tr>
        <th style="width: 50px;">${users_status.index+1}</th>
        <td style="font-weight: 700;">${row.userId}</td>
        <td contenteditable='true' id="edit_${row.userId}_userPw">${row.userPw}</td>
        <td contenteditable='true' id="edit_${row.userId}_email">${row.email}</td>
        <td style="text-align: center;">
            <input type="button" class="menu-input" value="수정" onclick="editItem('${row.userId}')"/>
            <input type="button" class="menu-input" value="삭제" onclick="deleteItem('${row.userId}')"/>
        </td>
    </tr>
    </c:forEach>
    </table>
    <p style="text-align: center;">
            <img src="/images/TheBankOfKorea.png" height="10px"/>
    </p>
    <form name="frm" action="/admin/users-edit" method="POST">
        <input type="hidden" name="userId" />
        <input type="hidden" name="userPw" />
        <input type="hidden" name="email" />
    </form>
    <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
    <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
    <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
</body>
</html>