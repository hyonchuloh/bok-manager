<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <!-- 헤더부 시작 -->
    <div class="h1-right">
        <table class="h1-right-table">
            <tr class="h1-right-table">
                <td class="h1-right-table">
                    안녕하세요? 오늘은 <b>${yearInt}년 ${monthInt}월 ${dayInt}일</b> 입니다.
                    <br/>
                    <a href="/manager/callbook" ><img src="/images/icons/address-book.png" class="h1-nav-icon"/>연락처</a>
                    | <a href="/manager/calendar" ><img src="/images/icons/calendar.png" class="h1-nav-icon"/>전체달력</a>
                    | <a href="/manager/calendar-week"><img src="/images/icons/calendar-check.png" class="h1-nav-icon"/>평일달력</a>
                    | <a href="/manager/calendar-year"><img src="/images/icons/calendar-dots.png" class="h1-nav-icon"/>연간달력</a>
                    | <a href="/manager/board"><img src="/images/icons/note-pencil.png" class="h1-nav-icon"/>메모장</a>
                    | <a href="/admin/users"><img src="/images/icons/user-gear.png" class="h1-nav-icon"/>사용자 관리</a>
                    | <a href="/logout"><img src="/images/icons/sign-out.png" class="h1-nav-icon"/>로그아웃</a>
                </td>
                <td rowspan="2" class="h1-right-table">
                    &nbsp;&nbsp;&nbsp;<img src="/images/bok_logo.png" class="h1-right-img"/>
                </td>
            </tr>
        </table>
    </div>
