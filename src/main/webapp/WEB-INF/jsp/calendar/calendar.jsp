<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="icon" href="/images/bok.ico">
<link rel="apple-touch-icon" href="/images/bok_logo.png">
<link rel="apple-touch-icon-precomposed" href="/images/bok_logo.png">
<title>한국은행 오현철 과장 업무관리 - ${name}</title>
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<script>
function saveHoliday() {
        document.frm2.calDate.value = document.getElementById("calDate").value;
        document.frm2.calData.value = document.getElementById("calData").value;
        document.frm2.submit();
}
function saveItem(key, value) {
        var filterKey = document.getElementById("filterKey").value;
        if ( filterKey != "" ) {
                alert("필터 해제 필요!");
                return;
        }
        if ( window.event.keyCode == 9 || window.event.keyCode == 186 ) {
                document.frm.key.value = key;
                document.frm.value.value = value;
                document.frm.startDay.value = document.getElementById("startDay").value;
                document.frm.submit();
        }
}
function eventFilter(name, check) {
        document.href="/manager/calender/${name}"
}
function openSearch(name, year) {
        var key = document.getElementById("searchkey").value;
        window.open('/manager/calfind?name=' + name + '&year=' + year + '&searchkey=' + key,'FEP CALENDER SEARCH','width=700, height=850');
}
function openDownload() {
        var path = document.getElementById("downloadFile").value;
        window.open('/manager/download?path=' + path,'DOWNLOAD','width=700, height=250');
}
function holidayCheck() {
        var table = document.getElementById("mainTable");
        var tds = table.getElementsByTagName("td");
        for (i=0; i<tds.length; i++) {
            if ( tds[i].innerText.indexOf("*holiday*") > 0 ) {
                tds[i].style.backgroundColor="#FFDEE9";
            }
            if ( tds[i].innerText.indexOf("*vacation*") > 0 ) {
                tds[i].style.backgroundColor="#F6FFDD";
            }
        }
        var sendMsg = "${sendMsg}";
        if ( sendMsg != "" ) {
                alert(sendMsg);
        }
}
function chgfocus(input) {
        document.getElementById("calDate").value = input;
}
</script>
</head>
<body onload="holidayCheck();">
<div style="float: right; padding-right: 10px; padding-top: 7px;font-size: 10pt; color: azure; font-weight: 100; text-align: right;">
        안녕하세요? 오늘은 ${yearInt }년 ${monthInt }월 ${dayInt }일 입니다.</br>
        <a href="/manager/callbook" style="color: azure;  font-weight: 400;">연락처</a>
         | <a href="/manager/calendar/${name}" style="color: azure; text-decoration: none; font-weight: 400;">달력</a>
         | <a href="/manager/calendar/iso20022" style="color: azure; text-decoration: none; font-weight: 400;">ISO 20022</a>
         | <a href="/logout" style="color: azure; text-decoration: none; font-weight: 400;">로그아웃</a>
</div>
<h1>
        <img src="/images/bok_logo.png" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px; cursor: pointer;" onclick="location.href='/manager/calendar/${name}';"/>
        &nbsp;${yearInt}년 ${monthInt}월 캘린더 (${name})
</h1>
        &nbsp;
        <a href="/manager/calendar/${name}?year=${yearInt }&month=${monthInt-1 }&key=&value=&filterKey=${filterKey}">이전달</a> |
        <a href="/manager/calendar/${name}?year=${yearInt }&month=${monthInt+1 }&key=&value=&filterKey=${filterKey}">다음달</a> |
        시작일자 : 
                <input type="text" id="startDay" value="${startDay}" style="width: 30px;" autocomplete="off"/>
                <input type="button" value="SUBMIT" onclick="location.href='/manager/calendar/${name}?year=${yearInt }&month=${monthInt}&startDay='+document.getElementById('startDay').value;" />
        | 달력검색 : <input type="text" id="searchkey"  style="width: 150px;" autocomplete="off"/> <input type="button" value="SEARCH" onclick="openSearch('${name}', '${yearInt}');" />
        | 필터 : 
                <input type="text" id="filterKey" style="width: 150px;" autocomplete="off" value="${filterKey}"/>
                <input type="button" value="필터" onclick="location.href='/manager/calendar/${name}?year=${yearInt}&month=${monthInt}&filterKey='+document.getElementById('filterKey').value;" />
        | 다운로드 : 
                <input type="text" id="downloadFile" style="width: 150px" value="/home/appuser/bok-manager/calendar.ohhyonchul.2024.dat" />
                <input type="button" value="다운로드" onclick="openDownload()" />
        | HOLIDAY : 
                <input type="text" id="calDate" value="CAL.${yearInt}.${monthInt}.${dayInt}" style="width: 110px;"/> 
                <input type="text" id="calData" /> 
                <input type="button" value="저장" onclick="saveHoliday()" /> • 
                
<table style="width: 100%; table-layout:fixed;" id="mainTable">
<tr>
        <th style="width: 10%;">일</th>
        <th style="width: 16%;">월</th>
        <th style="width: 16%;">화</th>
        <th style="width: 16%;">수</th>
        <th style="width: 16%;">목</th>
        <th style="width: 16%;">금</th>
        <th style="width: 10%;">토</th>
</tr>
<c:set var="isContinue" value="true"/>
<c:set var="tdColor" value="#FFFFFF" /><!-- E1F6FA -->
<c:forEach var="row" items="${dayTable}" varStatus="row_status">
<c:if test="${isContinue eq 'true'}">
        <tr>
                <c:forEach var="col" items="${row}" varStatus="cal_status">
                <c:choose>
                        <c:when test="${col > 0}">
                                <c:set var="tdColor" value="#FFFFFF" />
                                <c:if test="${cal_status.first}"><c:set var="tdColor" value="#E1F6FA" /></c:if>
                                <c:if test="${cal_status.last}"><c:set var="tdColor" value="#E1F6FA" /></c:if>
                                <td valign="top" style="font-size: 10pt; line-height: 140%; background-color: ${tdColor};" onclick="chgfocus('CAL.${yearInt}.${monthInt}.${col}')">
                                        <c:set var="tempKey">CAL.${yearInt}.${monthInt}.${col}</c:set>
                                        <span style="font-weight: 700;background-color: rgb(233, 233, 233);">${col}</span> ${calHoliday[tempKey]}
                                        <c:if test="${col == dayInt}"><span style="color: blue; font-weight: 700;"> Today</span></c:if>
                                        <br/>
                                        <c:if test="${col >= startDay}">
                                                <div contenteditable='true' onkeydown="saveItem('CAL.${yearInt}.${monthInt}.${col}', this.innerHTML);" >
                                                        ${contents[tempKey]}
                                                </div>
                                        </c:if>
                                </td>
                        </c:when>
                        <c:otherwise>
                                <td style="background-color: #F3F3F2">
                                        &nbsp;
                                </td>
                                <c:if test="${row_status.index > 1}">
                                        <c:set var="isContinue" value="false"/>
                                </c:if>
                        </c:otherwise>
                </c:choose>
                </c:forEach>
        </tr>
</c:if>
</c:forEach>
<c:set var="isContinue" value="true"/>
<c:forEach var="row" items="${dayTable2}" varStatus="row_status">
<c:if test="${isContinue eq 'true'}">
        <tr>
                <c:forEach var="col" items="${row}" varStatus="cal_status">
                <c:choose>
                        <c:when test="${col > 0}">
                                <c:set var="tdColor" value="#FFFFFF" />
                                <c:if test="${cal_status.first}"><c:set var="tdColor" value="#E1F6FA" /></c:if>
                                <c:if test="${cal_status.last}"><c:set var="tdColor" value="#E1F6FA" /></c:if>
                                <td valign="top" style="font-size: 10pt; line-height: 140%; background-color: ${tdColor};">
                                        <c:set var="tempKey">CAL.${nextYear}.${nextMonth}.${col}</c:set>
                                        <span style="font-weight: 700;background-color: rgb(233, 233, 233);">${col}</span> ${calHoliday2[tempKey]}
                                        <c:if test="${col == dayInt}"><font color="blue"> Today</font></c:if>
                                        <br/>
                                        ${contents2[tempKey]}
                                </td>
                        </c:when>
                        <c:otherwise>
                                <td style="background-color: #F3F3F2">
                                        &nbsp;
                                </td>
                                <c:if test="${row_status.index > 1}">
                                        <c:set var="isContinue" value="false"/>
                                </c:if>
                        </c:otherwise>
                </c:choose>
                </c:forEach>
        </tr>
</c:if>
</c:forEach>
</table>
<p align="center">
        <img src="/images/TheBankOfKorea.png" height="10px"/>
</p>
<form name="frm" action="/manager/calendar/${name}" method="POST">
        <input type="hidden" name="key" value="" />
        <input type="hidden" name="value" value="" />
        <input type="hidden" name="year" value="${yearInt}" />
        <input type="hidden" name="month" value="${monthInt}" />
        <input type="hidden" name="startDay" value="${startDay}"/>
</form>
<form name="frm2" action="/manager/calendar/holiday" method="POST">
        <input type="hidden" name="calDate" value="CAL.${yearInt}.${monthInt}.${dayInt}" /> 
        <input type="hidden" name="calData" /> 
        <input type="hidden" name="name" value="${name}" />
        <input type="hidden" name="year" value="${yearInt}" />
        <input type="hidden" name="month" value="${monthInt}" />
        <input type="hidden" name="startDay" value="${startDay}"/>
</form>
</body>
</html>