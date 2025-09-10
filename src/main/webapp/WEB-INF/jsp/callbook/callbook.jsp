<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="icon" href="/images/profile.ico">
<link rel="apple-touch-icon" href="/images/profile.jsp">
<link rel="apple-touch-icon-precomposed" href="/images/profile.jsp">
<title>한국은행 오현철 과장 업무관리 - ${name}</title>
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<style>
td { letter-spacing: 0px; }
</style>
<script>
function insertItem() {
    document.frm.extName.value = document.getElementById("new_extName").innerText;
    document.frm.depName.value = document.getElementById("new_depName").innerText;
    document.frm.bizName.value = document.getElementById("new_bizName").innerText;
    document.frm.name.value = document.getElementById("new_name").innerText;
    document.frm.call.value = document.getElementById("new_call").innerText;
    document.frm.email.value = document.getElementById("new_email").innerText;
    document.frm.ext.value = document.getElementById("new_ext").innerText;
    document.frm.searchKey.value = document.searchFrm.searchKey.value;
    document.frm.submit();
}
function editItem(itemSeq) {
    document.frm.seq.value = itemSeq;
    document.frm.extName.value = document.getElementById("edit_" + itemSeq +"_extName").innerHTML;
    document.frm.depName.value = document.getElementById("edit_" + itemSeq +"_depName").innerHTML;
    document.frm.bizName.value = document.getElementById("edit_" + itemSeq +"_bizName").innerHTML;
    document.frm.name.value = document.getElementById("edit_" + itemSeq +"_name").innerHTML;
    document.frm.call.value = document.getElementById("edit_" + itemSeq +"_call").innerHTML;
    document.frm.email.value = document.getElementById("edit_" + itemSeq +"_email").innerHTML;
    document.frm.ext.value = document.getElementById("edit_" + itemSeq +"_ext").innerHTML;
    document.frm.searchKey.value = document.searchFrm.searchKey.value;
    document.frm.submit();
}
function deleteItem(itemSeq) {
    if ( confirm("정말로 삭제하시겠습니까?") ) {
        document.frm.seq.value = itemSeq;
        document.frm.action = "/manager/callbook-delete";
        document.frm.submit();
    }
}
</script>
</head>
<body>
<div style="float: right; padding-right: 10px; padding-top: 7px;font-size: 10pt; color: azure; font-weight: 100; text-align: right;">
        안녕하세요? 오늘은 ${yearInt }년 ${monthInt }월 ${dayInt }일 입니다.</br>
        <a href="/manager/callbook/${name}" style="color: azure;  font-weight: 400;">연락처</a>
         | <a href="/manager/calendar/${name}" style="color: azure; text-decoration: none; font-weight: 400;">달력</a>
         | <a href="/manager/calendar-week/${name}" style="color: azure; text-decoration: none; font-weight: 400;">달력(week only)</a>
         | <a href="/manager/board/list/${name}" style="color: azure; text-decoration: none; font-weight: 400;">블로그</a>
         | <a href="/manager/calendar/iso20022" style="color: azure; text-decoration: none; font-weight: 400;">ISO 20022</a>
         | <a href="/logout" style="color: azure; text-decoration: none; font-weight: 400;">로그아웃</a>
</div>
<h1>
    <img src="/images/profile.jpg" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px; cursor: pointer;" onclick="location.href='/manager/callbook/${name}'" >
    &nbsp;연락처
</h1>
<form name="searchFrm" method="get" action="/manager/callbook/${name}">
    검색 : <input type="text" value="${searchKey}" name="searchKey" /> <input type="submit" value="검색" /> 
    ${resultMsg}
</form>
<table style="width: 100%; table-layout:fixed;" id="mainTable">
<tr>
    <th style="width: 50px;">no</th>
    <th style="width: 10%;">기관명</th>
    <th style="width: 10%;">부서명</th>
    <th style="width: 10%;">담당업무</th>
    <th style="width: 10%;">이름</th>
    <th style="width: 10%;">연락처</th>
    <th style="width: 15%;">이메일</th>
    <th>업무이력</th>
    <th style="width: 150px;">저장/삭제</th>
</tr>
<tr>
    <td style="text-align:center;">신규</td>
    <td contenteditable='true' id="new_extName"> </td>
    <td contenteditable='true' id="new_depName"> </td>
    <td contenteditable='true' id="new_bizName"> </td>
    <td contenteditable='true' id="new_name"> </td>
    <td contenteditable='true' id="new_call"> </td>
    <td contenteditable='true' id="new_email"> </td>
    <td contenteditable='true' id="new_ext"> </td>
    <td style="text-align:center;"><input type="button" value="저장" onclick="insertItem();"/></td>
</tr>
<c:forEach var="row" items="${list}" varStatus="cal_status">
<tr>
    <td id="edit_${row.seq}_seq"  style="text-align:center; width: 50px;">${row.seq}</td>
    <td contenteditable='true' id="edit_${row.seq}_extName" style="font-weight: 700;">${row.extName}</td>
    <td contenteditable='true' id="edit_${row.seq}_depName">${row.depName}</td>
    <td contenteditable='true' id="edit_${row.seq}_bizName">${row.bizName}</td>
    <td contenteditable='true' id="edit_${row.seq}_name" style="font-weight: 700;">${row.name}</td>
    <td contenteditable='true' id="edit_${row.seq}_call">${row.call}</td>
    <td contenteditable='true' id="edit_${row.seq}_email">${row.email}</td>
    <td contenteditable='true' id="edit_${row.seq}_ext">${row.ext}</td>
    <td style="text-align: center;"><input type="button" value="수정" onclick="editItem('${row.seq}')"/>
        <input type="button" value="삭제" onclick="deleteItem('${row.seq}')"/></td>
</tr>
</c:forEach>
</table>
<p style="text-align: center;">
        <img src="/images/TheBankOfKorea.png" height="10px"/>
</p>
<form name="frm" action="/manager/callbook" method="POST">
    <input type="hidden" name="seq" value="0"/>
    <input type="hidden" name="extName" />
    <input type="hidden" name="depName" />
    <input type="hidden" name="bizName" />
    <input type="hidden" name="name" />
    <input type="hidden" name="call" />
    <input type="hidden" name="email" />
    <input type="hidden" name="ext" />
    <input type="hidden" name="searchKey" />
</form>
</body>
</html>