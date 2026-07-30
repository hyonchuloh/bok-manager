<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="icon" href="/images/tube.ico">
<link rel="apple-touch-icon" href="/images/tube-apple-touch-icon.png">
<link rel="apple-touch-icon-precomposed" href="/images/tube-apple-touch-icon-precomposed.png">
<title>타임라인</title>
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
    function toggleEditForm(seq) {
        const el = document.getElementById('editForm-' + seq);
        el.style.display = (el.style.display === 'block') ? 'none' : 'block';
    }
    function deletePost(seq, needsPassword) {
        let userPw = '';
        if (needsPassword) {
            userPw = prompt('본인 확인을 위해 게시물 작성 시 등록한 비밀번호를 입력해주세요.');
            if (userPw === null) return;
        } else if (!confirm('정말 삭제하시겠습니까? (하위 대댓글도 함께 삭제됩니다)')) {
            return;
        }
        document.getElementById('deleteSeq').value = seq;
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
        &nbsp;타임라인
    </h1>
    <table class="h1-menu-table">
        <tr>
            <td class="h1-menu-td">
                <a href="/"><img src="/images/icons/caret-left.png" class="icon"/>홈으로</a>
                ${resultMsg}
            </td>
        </tr>
    </table>

    <div class="post-compose">
        <form name="postFrm" method="post" action="/post-save">
            <textarea name="contents" maxlength="280" placeholder="무슨 일이 있었나요?" required></textarea>
            <c:if test="${!authenticated}">
            <div class="post-compose-identity">
                <input type="text" name="userId" class="menu-input" placeholder="닉네임" maxlength="50" required/>
                <input type="password" name="userPw" class="menu-input" placeholder="비밀번호(수정/삭제 시 필요)" autocomplete="off" required/>
            </div>
            </c:if>
            <div class="post-compose-footer">
                <input type="submit" class="menu-input" value="게시하기"/>
            </div>
        </form>
    </div>

    <c:choose>
    <c:when test="${empty feed}">
        <div class="post-empty">아직 게시물이 없습니다. 첫 게시물을 작성해보세요!</div>
    </c:when>
    <c:otherwise>
        <c:forEach var="item" items="${feed}">
            <c:set var="canManage" value="${isAdmin || (empty item.password && item.userId eq sessionUserId)}"/>
            <div class="post-card">
                <div class="post-meta clickable" style="cursor: pointer;" onclick="location.href='/post-detail/${item.seq}';">
                    <img src="/images/icons/identification-card.png" class="icon"/><b><c:out value="${item.userId}"/></b>
                    &nbsp;·&nbsp;${item.createdAt}
                </div>
                <div class="post-contents" onclick="location.href='/post-detail/${item.seq}';" style="cursor: pointer;"><c:out value="${item.contents}"/></div>
                <div class="post-actions">
                    <form method="post" action="/post-like" style="display: inline;">
                        <input type="hidden" name="seq" value="${item.seq}" />
                        <button type="submit" class="link-like">♥ ${item.likeCount}</button>
                    </form>
                    &nbsp;|&nbsp;<a href="/post-detail/${item.seq}"><img src="/images/icons/envelope-simple.png" class="icon"/>댓글 ${item.replyCount}개</a>
                    <c:if test="${canManage || not empty item.password}">
                        &nbsp;|&nbsp;<a onclick="toggleEditForm(${item.seq});">수정</a>
                        <c:choose>
                        <c:when test="${canManage}">
                        &nbsp;|&nbsp;<a onclick="deletePost(${item.seq}, false);">삭제</a>
                        </c:when>
                        <c:otherwise>
                        &nbsp;|&nbsp;<a onclick="deletePost(${item.seq}, true);">삭제</a>
                        </c:otherwise>
                        </c:choose>
                    </c:if>
                </div>
                <div class="edit-form" id="editForm-${item.seq}">
                    <form method="post" action="/post-edit">
                        <input type="hidden" name="seq" value="${item.seq}" />
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
    </c:otherwise>
    </c:choose>

    <form name="deleteFrm" method="post" action="/post-delete" style="display: none;">
        <input type="hidden" id="deleteSeq" name="seq" value="" />
        <input type="hidden" id="deleteUserPw" name="userPw" value="" />
    </form>

    <p align="center">
        <img src="/images/TheBankOfKorea.png" height="10px" />
    </p>
</body>
</html>
