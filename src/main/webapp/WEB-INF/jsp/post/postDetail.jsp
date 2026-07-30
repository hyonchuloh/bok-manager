<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="icon" href="/images/tube.ico">
<link rel="apple-touch-icon" href="/images/tube-apple-touch-icon.png">
<link rel="apple-touch-icon-precomposed" href="/images/tube-apple-touch-icon-precomposed.png">
<title>게시물 쓰레드</title>
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<link rel="stylesheet" type="text/css" href="/css/post.css" />
<style>
    :root {
        --content-width: 720px;
    }
    body {
        width: 720px !important;
        margin: 0px auto !important;
    }
</style>
<script>
    function toggleReplyForm(seq) {
        const el = document.getElementById('replyForm-' + seq);
        el.style.display = (el.style.display === 'block') ? 'none' : 'block';
    }
    function toggleEditForm(seq) {
        const el = document.getElementById('editForm-' + seq);
        el.style.display = (el.style.display === 'block') ? 'none' : 'block';
    }
    function deletePost(seq, redirectSeq, needsPassword) {
        let userPw = '';
        if (needsPassword) {
            userPw = prompt('본인 확인을 위해 게시물 작성 시 등록한 비밀번호를 입력해주세요.');
            if (userPw === null) return;
        } else if (!confirm('정말 삭제하시겠습니까? (하위 대댓글도 함께 삭제됩니다)')) {
            return;
        }
        document.getElementById('deleteSeq').value = seq;
        document.getElementById('deleteRedirectSeq').value = redirectSeq;
        document.getElementById('deleteUserPw').value = userPw;
        document.deleteFrm.submit();
    }
</script>
</head>
<body>
    <c:if test="${authenticated}">
        <%@ include file="/WEB-INF/jsp/common/header.jsp" %>
    </c:if>
    <h1>
        <img src="/images/profile.jpg" class="h1-image" onclick="location.href='/post';"/>
        &nbsp;게시물 쓰레드
    </h1>
    <table class="h1-menu-table">
        <tr>
            <td class="h1-menu-td">
                <a href="/"><img src="/images/icons/caret-left.png" class="icon"/>홈으로</a>
                &nbsp;|&nbsp;<a href="/post">타임라인 전체보기</a>
                ${resultMsg}
            </td>
        </tr>
    </table>

    <c:forEach var="item" items="${thread}">
        <c:set var="canManage" value="${isAdmin || (empty item.password && item.userId eq sessionUserId)}"/>
        <c:set var="redirectArg" value="${item.depth == 0 ? '' : rootSeq}"/>
        <div class="post-card" style="margin-left: ${item.depth * 30}px;">
            <div class="post-meta">
                <img src="/images/icons/identification-card.png" class="icon"/><b><c:out value="${item.userId}"/></b>
                &nbsp;·&nbsp;${item.createdAt}
                <c:if test="${item.depth == 0}">&nbsp;·&nbsp;<b>원 게시물</b></c:if>
            </div>
            <div class="post-contents"><c:out value="${item.contents}"/></div>
            <div class="post-actions">
                <form method="post" action="/post-like" style="display: inline;">
                    <input type="hidden" name="seq" value="${item.seq}" />
                    <input type="hidden" name="redirectSeq" value="${rootSeq}" />
                    <button type="submit" class="link-like">♥ ${item.likeCount}</button>
                </form>
                &nbsp;|&nbsp;<a onclick="toggleReplyForm(${item.seq});"><img src="/images/icons/envelope-simple.png" class="icon"/>답글</a>
                <c:if test="${canManage || not empty item.password}">
                    &nbsp;|&nbsp;<a onclick="toggleEditForm(${item.seq});">수정</a>
                    <c:choose>
                    <c:when test="${canManage}">
                    &nbsp;|&nbsp;<a onclick="deletePost(${item.seq}, '${redirectArg}', false);">삭제</a>
                    </c:when>
                    <c:otherwise>
                    &nbsp;|&nbsp;<a onclick="deletePost(${item.seq}, '${redirectArg}', true);">삭제</a>
                    </c:otherwise>
                    </c:choose>
                </c:if>
            </div>
            <div class="reply-form" id="replyForm-${item.seq}">
                <form method="post" action="/post-reply">
                    <input type="hidden" name="parentSeq" value="${item.seq}" />
                    <input type="hidden" name="rootSeq" value="${rootSeq}" />
                    <textarea name="contents" maxlength="280" placeholder="답글을 입력하세요" required></textarea>
                    <c:if test="${!authenticated}">
                    <div class="post-compose-identity">
                        <input type="text" name="userId" class="menu-input" placeholder="닉네임" maxlength="50" required/>
                        <input type="password" name="userPw" class="menu-input" placeholder="비밀번호(수정/삭제 시 필요)" autocomplete="off" required/>
                    </div>
                    </c:if>
                    <div class="reply-form-footer">
                        <input type="submit" class="menu-input" value="답글 게시"/>
                    </div>
                </form>
            </div>
            <div class="edit-form" id="editForm-${item.seq}">
                <form method="post" action="/post-edit">
                    <input type="hidden" name="seq" value="${item.seq}" />
                    <input type="hidden" name="redirectSeq" value="${redirectArg}" />
                    <textarea name="contents"><c:out value="${item.contents}"/></textarea>
                    <div class="edit-form-footer">
                        <c:if test="${!canManage}">
                            <input type="password" name="userPw" class="menu-input" placeholder="비밀번호" autocomplete="off"/>
                        </c:if>
                        <input type="submit" class="menu-input" value="수정 완료"/>
                    </div>
                </form>
            </div>
        </div>
    </c:forEach>

    <form name="deleteFrm" method="post" action="/post-delete" style="display: none;">
        <input type="hidden" id="deleteSeq" name="seq" value="" />
        <input type="hidden" id="deleteRedirectSeq" name="redirectSeq" value="" />
        <input type="hidden" id="deleteUserPw" name="userPw" value="" />
    </form>

    <p align="center">
        <img src="/images/TheBankOfKorea.png" height="10px" />
    </p>
</body>
</html>
