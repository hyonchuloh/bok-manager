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

    boardContent = boardContent.replace(/13\.3333px/g, "inherit");
    boardContent = boardContent.replace(/13\.333333px/g, "inherit");
    boardContent = boardContent.replace(/9pt/g, "inherit");
    boardContent = boardContent.replace(/9\.5pt/g, "inherit");
    boardContent = boardContent.replace(/10pt/g, "inherit");
    boardContent = boardContent.replace(/11pt/g, "inherit");
    boardContent = boardContent.replace(/12pt/g, "inherit");
    boardContent = boardContent.replace(/inheritpx/g, "inherit");
    boardContent = boardContent.replace(/\{\{/g, "<span style='color: blue;'>");
    boardContent = boardContent.replace(/\}\}/g, "</span>");
    boardContent = boardContent.replace(/\[\[/g, "<span style='color: red;'>");
    boardContent = boardContent.replace(/\]\]/g, "</span>");
    boardContent = boardContent.replace(/\(\(/g, "<span style='color: #999999;'>");
    boardContent = boardContent.replace(/\)\)/g, "</span>");

    document.frm.title.value = boardTitle;
    document.frm.contents.value = boardContent;
    document.frm.submit();
}
function handlePaste(event) {
    event.preventDefault();
    const text = event.clipboardData.getData('text/plain');
    document.execCommand('insertText', false, text);
}
function deleteItem() {
    if (confirm("정말 삭제하시겠습니까?")) {
        document.frm.action = "/manager/board-delete";
        document.frm.submit();
    }
}
function changeStyle() {
    document.frm.font.value = document.getElementById("fontInput").value;
    document.frm.fontSize.value = document.getElementById("fontSizeInput").value;
    document.frm.lineHeight.value = document.getElementById("lineHeightInput").value;
    document.frm.letterSpacing.value = document.getElementById("letterSpacingInput").value;
    document.getElementById("latestBoardContents").style.fontFamily = document.getElementById("fontInput").value;
    document.getElementById("latestBoardContents").style.fontSize = document.getElementById("fontSizeInput").value;
    document.getElementById("latestBoardContents").style.lineHeight = document.getElementById("lineHeightInput").value;
    document.getElementById("latestBoardContents").style.letterSpacing = document.getElementById("letterSpacingInput").value;
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
        <img src="/images/profile.jpg" class="h1-image" onclick="location.href='/manager/board';"/>
        &nbsp;메모장
    </h1>
    <!-- 메뉴부 시작 -->
    <table class="h1-menu-table">
        <tr>
            <td class="h1-menu-td">
                📄 새글 작성 : <input type="button" class="menu-input" value="NEW" onclick="document.location.href='/manager/board?seq=0'"/> |
                ${resultMsg}
            </td>
        </tr>
    </table>
    <!-- 게시판 시작 -->
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
                        <th style="width: 20%; color: gray;" >${latestBoard.createdAt}</th>
                    </tr>
                    <tr>
                        <td colspan="2" style="text-align: left;">
                            🆎 폰트 변경 : <select class="menu-input" id="fontInput" onchange="changeStyle()">
                                <c:forEach var="item" items="${fontList}">
                                    <option value="${item}" ${item eq font ? 'selected' : ''}>${item}</option>
                                </c:forEach>
                                </select> | 
                            9️⃣ 폰트 사이즈 변경 : <select class="menu-input" id="fontSizeInput" onchange="changeStyle()">
                                <option value="9.5pt" ${fontSize eq '9.5pt' ? 'selected' : ''}>9.5pt</option>
                                <option value="10pt" ${fontSize eq '10pt' ? 'selected' : ''}>10pt</option>  
                                <option value="11pt" ${fontSize eq '11pt' ? 'selected' : ''}>11pt</option>
                                <option value="12pt" ${fontSize eq '12pt' ? 'selected' : ''}>12pt</option>
                                <option value="13pt" ${fontSize eq '13pt' ? 'selected' : ''}>13pt</option>
                                <option value="14pt" ${fontSize eq '14pt' ? 'selected' : ''}>14pt</option>
                            </select> | 
                            ↕️ 줄 간격 변경 : <select class="menu-input" id="lineHeightInput" onchange="changeStyle()">
                                <option value="100%" ${lineHeight eq '100%' ? 'selected' : ''}>100%</option>
                                <option value="120%" ${lineHeight eq '120%' ? 'selected' : ''}>120%</option>
                                <option value="130%" ${lineHeight eq '130%' ? 'selected' : ''}>130%</option>
                                <option value="140%" ${lineHeight eq '140%' ? 'selected' : ''}>140%</option>
                                <option value="150%" ${lineHeight eq '150%' ? 'selected' : ''}>150%</option>
                                <option value="160%" ${lineHeight eq '160%' ? 'selected' : ''}>160%</option>
                                <option value="170%" ${lineHeight eq '170%' ? 'selected' : ''}>170%</option>
                                <option value="180%" ${lineHeight eq '180%' ? 'selected' : ''}>180%</option>
                            </select> | 
                            ↔️ 글자 간격 변경 : <select class="menu-input" id="letterSpacingInput" onchange="changeStyle()">
                                <option value="0px" ${letterSpacing eq '0px' ? 'selected' : ''}>0px</option>
                                <option value="-0.5px" ${letterSpacing eq '-0.5px' ? 'selected' : ''}>-0.5px</option>
                                <option value="-1px" ${letterSpacing eq '-1px' ? 'selected' : ''}>-1px</option>
                                <option value="-1.5px" ${letterSpacing eq '-1.5px' ? 'selected' : ''}>-1.5px</option>
                            </select>
                        </td>
                        <td style="text-align: right;">
                            <input type="button" class="menu-input" value="SAVE" onclick="saveItem();"/>
                            <input type="button" class="menu-input" value="DELETE" onclick="deleteItem();"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" id="latestBoardContents" contenteditable="true" onpaste="handlePaste(event)" 
                            style="word-wrap: break-word; font-family: '${font}'; font-size: ${fontSize}; line-height: ${lineHeight}; 
                                   letter-spacing: ${letterSpacing};" onpaste="handlePaste(event);">
                            ${latestBoard.contents}
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    <form name="frm" method="post" action="/manager/board-save">
        <input type="hidden" name="seq" value="${latestBoard.seq}" />
        <input type="hidden" name="title" value="" />
        <input type="hidden" name="contents" value="" />
        <input type="hidden" name="font" value="${font}" />
        <input type="hidden" name="fontSize" value="${fontSize}" />
        <input type="hidden" name="lineHeight" value="${lineHeight}" />
        <input type="hidden" name="letterSpacing" value="${letterSpacing}" />
    </form>
</body>
</html>