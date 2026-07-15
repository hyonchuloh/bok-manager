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
<script src="/js/markdown.js"></script>
<script>
function saveItem() {
    boardTitleElement = document.getElementById("latestBoardTitle");
    boardContentElement = document.getElementById("latestBoardContents");
    boardTitle = boardTitleElement ? boardTitleElement.innerText : "";
    boardContent = boardContentElement ? toPlainText(getMarkdownRawContent("latestBoardContents") ?? boardContentElement.innerHTML) : "";

    document.frm.title.value = boardTitle;
    document.frm.contents.value = boardContent;
    document.frm.secret.value = document.getElementById("secretCheck").checked ? "true" : "false";
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
</head>
<body>
    <%@ include file="/WEB-INF/jsp/common/header.jsp" %>
    <h1>
        <img src="/images/profile.jpg" class="h1-image" onclick="location.href='/manager/board';"/>
        &nbsp;메모장
    </h1>
    <!-- 메뉴부 시작 -->
    <table class="h1-menu-table">
        <tr>
            <td class="h1-menu-td">
                <img src="/images/icons/file-text.png" class="icon"/> 새글 작성 : <input type="button" class="menu-input" value="NEW" onclick="document.location.href='/manager/board?seq=0'"/>
                ${resultMsg}
            </td>
        </tr>
    </table>
    <!-- 게시판 시작 -->
    <table style="width: 100%; height: 100%; border: 0px solid black;" id="mainTable">
        <tr style="vertical-align: top;">
            <!-- 좌측 게시판 내용 -->
            <td style="width: 50%; border: 0px solid black;">
                <table style="width: 100%">
                    <tr>
                        <th style="width: 10%;">${latestBoard.seq}</th>
                        <th style="width: 70%;" id="latestBoardTitle" contenteditable="${!boardLocked}">${latestBoard.title}</th>
                        <th style="width: 20%; color: gray;" >${latestBoard.createdAt}</th>
                    </tr>
                    <c:choose>
                    <c:when test="${boardLocked}">
                    <tr>
                        <td colspan="3" style="text-align: left;">
                            <form name="unlockFrm" method="post" action="/manager/board-unlock" style="display: inline;">
                                <input type="hidden" name="seq" value="${latestBoard.seq}" />
                                <img src="/images/icons/lock-key.png" class="icon"/> 비밀글입니다. 비밀번호 :
                                <input type="password" class="menu-input" name="password" autocomplete="off" />
                                <input type="submit" class="menu-input" value="열람" />
                            </form>
                            <input type="button" class="menu-input" value="DELETE" onclick="deleteItem();"/>
                            <c:if test="${not empty unlockError}"><span style="color: red;">${unlockError}</span></c:if>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" style="text-align: left; color: gray;">비밀번호를 입력하면 내용을 확인할 수 있습니다.</td>
                    </tr>
                    </c:when>
                    <c:otherwise>
                    <tr>
                        <td colspan="3" style="text-align: left;">
                            <img src="/images/icons/text-aa.png" class="icon"/> 폰트 : <select class="menu-input" id="fontInput" onchange="changeStyle()">
                                <c:forEach var="item" items="${fontList}">
                                    <option value="${item}" ${item eq font ? 'selected' : ''}>${item}</option>
                                </c:forEach>
                                </select> |
                            <img src="/images/icons/text-h.png" class="icon"/> 사이즈 : <select class="menu-input" id="fontSizeInput" onchange="changeStyle()">
                                <option value="9.5pt" ${fontSize eq '9.5pt' ? 'selected' : ''}>9.5pt</option>
                                <option value="10pt" ${fontSize eq '10pt' ? 'selected' : ''}>10pt</option>
                                <option value="11pt" ${fontSize eq '11pt' ? 'selected' : ''}>11pt</option>
                                <option value="12pt" ${fontSize eq '12pt' ? 'selected' : ''}>12pt</option>
                                <option value="13pt" ${fontSize eq '13pt' ? 'selected' : ''}>13pt</option>
                                <option value="14pt" ${fontSize eq '14pt' ? 'selected' : ''}>14pt</option>
                            </select> |
                            <img src="/images/icons/arrows-vertical.png" class="icon"/> 줄 간격 : <select class="menu-input" id="lineHeightInput" onchange="changeStyle()">
                                <option value="100%" ${lineHeight eq '100%' ? 'selected' : ''}>100%</option>
                                <option value="120%" ${lineHeight eq '120%' ? 'selected' : ''}>120%</option>
                                <option value="130%" ${lineHeight eq '130%' ? 'selected' : ''}>130%</option>
                                <option value="140%" ${lineHeight eq '140%' ? 'selected' : ''}>140%</option>
                                <option value="150%" ${lineHeight eq '150%' ? 'selected' : ''}>150%</option>
                                <option value="160%" ${lineHeight eq '160%' ? 'selected' : ''}>160%</option>
                                <option value="170%" ${lineHeight eq '170%' ? 'selected' : ''}>170%</option>
                                <option value="180%" ${lineHeight eq '180%' ? 'selected' : ''}>180%</option>
                            </select> |
                            <img src="/images/icons/arrows-horizontal.png" class="icon"/> 글자 간격 : <select class="menu-input" id="letterSpacingInput" onchange="changeStyle()">
                                <option value="0px" ${letterSpacing eq '0px' ? 'selected' : ''}>0px</option>
                                <option value="-0.5px" ${letterSpacing eq '-0.5px' ? 'selected' : ''}>-0.5px</option>
                                <option value="-1px" ${letterSpacing eq '-1px' ? 'selected' : ''}>-1px</option>
                                <option value="-1.5px" ${letterSpacing eq '-1.5px' ? 'selected' : ''}>-1.5px</option>
                            </select>
                            <label style="font-size: 9pt;"><input type="checkbox" id="secretCheck" ${latestBoard.secret ? 'checked' : ''} /><img src="/images/icons/lock-simple.png" class="icon"/>비밀글</label>
                            <input type="button" class="menu-input" value="SAVE" onclick="saveItem();"/>
                            <input type="button" class="menu-input" value="DELETE" onclick="deleteItem();"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" id="latestBoardContents" contenteditable="true" onpaste="handlePaste(event)" onkeydown="handleEditableEnter(event)"
                            style="word-wrap: break-word; white-space: pre-wrap; font-family: '${font}'; font-size: ${fontSize}; line-height: ${lineHeight};
                                   letter-spacing: ${letterSpacing};" onpaste="handlePaste(event);">${latestBoard.contents}</td>
                    </tr>
                    </c:otherwise>
                    </c:choose>
                </table>
                <c:if test="${!boardLocked}">
                <input type="button" class="menu-input" value="SAVE" onclick="saveItem();"/>
                <input type="button" class="menu-input" value="DELETE" onclick="deleteItem();"/>
                </c:if>
            </td>
            <!-- 우측 게시판 목록 -->
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
                            <td style="text-align: left;"><c:if test="${board.secret}"><img src="/images/icons/lock-simple.png" class="icon"/></c:if> ${board.title}</td>
                            <td style="color: gray; text-align: center;">${board.createdAt}</td>
                        </tr>
                    </c:forEach>
                </table>
            </td>
        </tr>
    </table>
    <form name="frm" method="post" action="/manager/board-save">
        <input type="hidden" name="seq" value="${latestBoard.seq}" />
        <input type="hidden" name="title" value="" />
        <input type="hidden" name="contents" value="" />
        <input type="hidden" name="secret" value="" />
        <input type="hidden" name="font" value="${font}" />
        <input type="hidden" name="fontSize" value="${fontSize}" />
        <input type="hidden" name="lineHeight" value="${lineHeight}" />
        <input type="hidden" name="letterSpacing" value="${letterSpacing}" />
    </form>
    <p align="center">
        <img src="/images/TheBankOfKorea.png" height="10px" />
    </p>
    <script>
        applyMarkdownRendering('latestBoardContents');
        enableMarkdownEditToggle('latestBoardContents');
    </script>
</body>
</html>