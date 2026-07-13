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
    :root {
        --content-width: 1536px;
    }
    body {
        width: 1536px !important;
        margin: 0px auto !important;
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
function deletePasskey(credentialId) {
    if ( confirm("정말로 삭제하시겠습니까? ["+credentialId+"]") ) {
        document.passkeyFrm.credentialId.value = credentialId;
        document.passkeyFrm.action = "/admin/users-passkey-delete";
        document.passkeyFrm.submit();
    }
}
</script>
</head>
<body>
    <%@ include file="/WEB-INF/jsp/common/header.jsp" %>
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
        <th style="width: 50px;"><img src="/images/icons/folder.png" class="icon"/>구분</th>
        <th style="width: 150px;"><img src="/images/icons/identification-card.png" class="icon"/>사용자ID</th>
        <th style="width: 150px;"><img src="/images/icons/key.png" class="icon"/>사용자PW</th>
        <th style="width: 150px;"><img src="/images/icons/envelope-simple.png" class="icon"/>이메일</th>
        <th style="width: 150px;"><img src="/images/icons/floppy-disk.png" class="icon"/>저장/삭제</th>
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

    <!-- 패스키 등록 현황 -->
    <table class="h1-menu-table">
        <tr>
            <td class="h1-menu-td">
                <img src="/images/icons/lock-key.png" class="icon"/>패스키 등록 현황
            </td>
        </tr>
    </table>
    <table style="table-layout:fixed; width: 100%; font-family: 'd2coding'; margin-top: 10px;" id="passkeyTable">
        <tr>
            <th style="width: 80px;">#</th>
            <th style="width: 180px;">사용자ID</th>
            <th>Credential ID</th>
            <th style="width: 120px;">Sign Count</th>
            <th style="width: 120px;">액션</th>
        </tr>
        <c:forEach var="passkey" items="${passkeyList}" varStatus="passkey_status">
        <tr>
            <th>${passkey_status.index+1}</th>
            <td style="font-weight: 700;">${passkey.userId}</td>
            <td style="word-break: break-all;">${passkey.credentialId}</td>
            <td style="text-align: center;">${passkey.signCount}</td>
            <td style="text-align: center;">
                <input type="button" class="menu-input" value="삭제" onclick="deletePasskey('${passkey.credentialId}')" />
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
    <form name="passkeyFrm" action="/admin/users-passkey-delete" method="POST">
        <input type="hidden" name="credentialId" />
    </form>
    <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
    <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
    <br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
</body>
</html>