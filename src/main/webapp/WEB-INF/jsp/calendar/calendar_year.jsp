<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="icon" href="/images/tube.ico">
<link rel="apple-touch-icon" href="/images/tube-apple-touch-icon.png">
<link rel="apple-touch-icon-precomposed" href="/images/tube-apple-touch-icon-precomposed.png">
<title>한국은행 오현철 과장 업무관리 - ${name}</title>
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<script>
function holidayCheck() {
    var tables = document.getElementsByClassName("year-table");
    for (var t = 0; t < tables.length; t++) {
        var tds = tables[t].getElementsByTagName("td");
        for (var i = 0; i < tds.length; i++) {
            if (tds[i].innerText.indexOf("*holiday*") > 0) {
                tds[i].style.backgroundColor = "#FFDEE9";
            }
            if (tds[i].innerText.indexOf("*vacation*") > 0) {
                tds[i].style.backgroundColor = "#F6FFDD";
            }
        }
    }
}
</script>
<style>
    .year-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 10px;
    }
    .year-month-title {
        text-align: center;
        font-weight: 700;
        margin: 0px 0px 3px 0px;
    }
    .year-table {
        width: 100%;
        table-layout: fixed;
    }
    .year-table th, .year-table td {
        font-size: 8pt;
        padding: 2px;
        vertical-align: top;
    }
    .year-memo {
        font-size: 8pt;
        max-height: 50px;
        overflow-y: auto;
        white-space: pre-wrap;
    }
</style>
</head>
<body onload="holidayCheck();">
    <%@ include file="/WEB-INF/jsp/common/header.jsp" %>
    <h1>
        <img src="/images/profile.jpg" class="h1-image" onclick="location.href='/manager/calendar';"/>
        &nbsp;${yearInt}년 캘린더 전체보기 (읽기전용) (${name})
    </h1>
    <!-- 메뉴부 시작 -->
    <table class="h1-menu-table">
        <tr>
            <td class="h1-menu-td">
                <a href="/manager/calendar-year?year=${yearInt - 1}">⬅️ 이전년도</a>
                <a href="/manager/calendar-year">📌 올해</a>
                <a href="/manager/calendar-year?year=${yearInt + 1}">➡️ 다음년도</a>
            </td>
        </tr>
    </table>
    <!-- 내용부 시작 -->
    <div class="year-grid">
    <c:forEach var="m" begin="1" end="12">
        <div>
            <p class="year-month-title"><a href="/manager/calendar?year=${yearInt}&month=${m}&startDay=1">${m}월</a></p>
            <table class="year-table">
                <tr>
                    <th style="width: 14%;">일</th>
                    <th style="width: 14%;">월</th>
                    <th style="width: 14%;">화</th>
                    <th style="width: 14%;">수</th>
                    <th style="width: 14%;">목</th>
                    <th style="width: 15%;">금</th>
                    <th style="width: 15%;">토</th>
                </tr>
                <c:set var="isContinue" value="true"/>
                <c:forEach var="row" items="${monthDayTables[m]}" varStatus="row_status">
                <c:if test="${isContinue eq 'true'}">
                    <tr>
                        <c:forEach var="col" items="${row}" varStatus="cal_status">
                            <c:choose>
                                <c:when test="${col > 0}">
                                    <c:set var="tempKey">CAL.${yearInt}.${m}.${col}</c:set>
                                    <td>
                                        <span style="font-weight: 700;">${col}</span>
                                        <c:if test="${yearInt == currentYear and m == currentMonth and col == dayInt}">
                                            <span style="color: blue; font-weight: 700;"> Today</span>
                                        </c:if>
                                        <br/>
                                        ${monthHolidays[m][tempKey]}
                                        <div class="year-memo">${contents[tempKey]}</div>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td style="background-color: #CCCCCC">&nbsp;</td>
                                    <c:if test="${row_status.index > 1}">
                                        <c:set var="isContinue" value="false" />
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </tr>
                </c:if>
                </c:forEach>
            </table>
        </div>
    </c:forEach>
    </div>
    <p align="center">
        <img src="/images/TheBankOfKorea.png" height="10px" />
    </p>
</body>
</html>
