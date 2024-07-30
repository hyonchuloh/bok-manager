<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="icon" href="/images/bok.ico">
<title>Calendar - ${name}</title>
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
    document.frm.submit();
}
function editItem(itemSeq) {
    document.frm.seq.value = itemSeq;
    document.frm.extName.value = document.getElementById("edit_" + itemSeq +"_extName").innerText;
    document.frm.depName.value = document.getElementById("edit_" + itemSeq +"_depName").innerText;
    document.frm.bizName.value = document.getElementById("edit_" + itemSeq +"_bizName").innerText;
    document.frm.name.value = document.getElementById("edit_" + itemSeq +"_name").innerText;
    document.frm.call.value = document.getElementById("edit_" + itemSeq +"_call").innerText;
    document.frm.email.value = document.getElementById("edit_" + itemSeq +"_email").innerText;
    document.frm.ext.value = document.getElementById("edit_" + itemSeq +"_ext").innerText;
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
<h1>
	연락처
	<div style="float: right; padding-right: 3px; padding-top:0px;font-size: 10pt; color: azure; font-weight: 100;">
		안녕하세요? | <a href="/logout" style="color: azure;">로그아웃</a> 
	</div>
</h1>
	&nbsp; ${resultMsg}
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
    <th>저장/삭제</th>
</tr>
<tr>
    <td>신규</td>
    <td contenteditable='true' id="new_extName"> </td>
    <td contenteditable='true' id="new_depName"> </td>
    <td contenteditable='true' id="new_bizName"> </td>
    <td contenteditable='true' id="new_name"> </td>
    <td contenteditable='true' id="new_call"> </td>
    <td contenteditable='true' id="new_email"> </td>
    <td contenteditable='true' id="new_ext"> </td>
    <td><input type="button" value="저장" onclick="insertItem();"/></td>
</tr>
<c:forEach var="row" items="${list}" varStatus="cal_status">
<tr>
    <td id="edit_${row.seq}_seq">${row.seq}</td>
    <td contenteditable='true' id="edit_${row.seq}_extName">${row.extName}</td>
    <td contenteditable='true' id="edit_${row.seq}_depName">${row.depName}</td>
    <td contenteditable='true' id="edit_${row.seq}_bizName">${row.bizName}</td>
    <td contenteditable='true' id="edit_${row.seq}_name">${row.name}</td>
    <td contenteditable='true' id="edit_${row.seq}_call">${row.call}</td>
    <td contenteditable='true' id="edit_${row.seq}_email">${row.email}</td>
    <td contenteditable='true' id="edit_${row.seq}_ext">${row.ext}</td>
    <td><input type="button" value="수정" onclick="editItem('${row.seq}')"/>
        <input type="button" value="삭제" onclick="deleteItem('${row.seq}')"/></td>
</tr>
</c:forEach>
</table>
<p align="center">
	<img src="/images/TheBankOfKorea.png" height="10px"/>
</p>
<form name="frm" action="/manager/callbook" method="POST">
    <input type="text" name="seq" value="0"/>
    <input type="text" name="extName" />
    <input type="text" name="depName" />
    <input type="text" name="bizName" />
    <input type="text" name="name" />
    <input type="text" name="call" />
    <input type="text" name="email" />
    <input type="text" name="ext" />
</form>
</body>
</html>