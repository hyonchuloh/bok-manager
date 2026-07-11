<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
