<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" href="/images/tube.ico">
<link rel="apple-touch-icon" href="/images/tube-apple-touch-icon.png">
<link rel="apple-touch-icon-precomposed" href="/images/tube-apple-touch-icon-precomposed.png">
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<link rel="stylesheet" type="text/css" href="/css/post.css" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1"/>
<title>오현철 과장 업무관리</title>
<style>
    :root {
        --content-width: 720px;
    }
    body {
        width: 720px !important;
        margin: 0px auto !important;
    }
    .post-compose, .post-card {
        max-width: 620px;
        margin-left: auto;
        margin-right: auto;
    }
    .post-card {
        border-radius: 12px;
    }
</style>
<script type="text/javascript" src="/js/pathkey.js"></script>
<script>
    function toggleEditForm(seq) {
        const el = document.getElementById('editForm-' + seq);
        el.style.display = (el.style.display === 'block') ? 'none' : 'block';
    }
    function toggleReplyForm(seq) {
        const el = document.getElementById('replyForm-' + seq);
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

    /* 댓글 펼치기/닫기 및 인라인 작성: 게시물 상세 페이지로 이동하지 않고 현재 화면에서 처리한다 */
    function toggleComments(seq) {
        const el = document.getElementById('comments-' + seq);
        if (el.style.display === 'block') {
            el.style.display = 'none';
            return;
        }
        el.style.display = 'block';
        if (!el.dataset.loaded) {
            loadComments(seq);
        }
    }
    function loadComments(seq) {
        const el = document.getElementById('comments-' + seq);
        el.innerHTML = '<div class="post-empty" style="padding:8px;">불러오는 중...</div>';
        fetch('/post-replies/' + seq, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
            .then(function(res) { return res.text(); })
            .then(function(html) { renderComments(seq, html); })
            .catch(function() { el.innerHTML = '<div class="post-empty" style="padding:8px;">댓글을 불러오지 못했습니다.</div>'; });
    }
    function renderComments(seq, html) {
        const el = document.getElementById('comments-' + seq);
        el.innerHTML = html;
        el.dataset.loaded = '1';
        const marker = el.querySelector('.reply-count-marker');
        const label = document.getElementById('replyCountLabel-' + seq);
        if (marker && label) {
            label.textContent = '댓글 ' + marker.getAttribute('data-count') + '개';
        }
    }
    function submitCommentForm(event, form, rootSeq) {
        event.preventDefault();
        const fd = new FormData(form);
        fetch(form.action, { method: 'POST', body: fd, headers: { 'X-Requested-With': 'XMLHttpRequest' } })
            .then(function(res) { return res.text(); })
            .then(function(html) { renderComments(rootSeq, html); })
            .catch(function() { alert('요청 처리 중 오류가 발생했습니다.'); });
        return false;
    }
    function deleteComment(seq, rootSeq, needsPassword) {
        let userPw = '';
        if (needsPassword) {
            userPw = prompt('본인 확인을 위해 게시물 작성 시 등록한 비밀번호를 입력해주세요.');
            if (userPw === null) return;
        } else if (!confirm('정말 삭제하시겠습니까? (하위 대댓글도 함께 삭제됩니다)')) {
            return;
        }
        const fd = new FormData();
        fd.append('seq', seq);
        fd.append('redirectSeq', rootSeq);
        fd.append('userPw', userPw);
        fetch('/post-delete', { method: 'POST', body: fd, headers: { 'X-Requested-With': 'XMLHttpRequest' } })
            .then(function(res) { return res.text(); })
            .then(function(html) { renderComments(rootSeq, html); });
    }
</script>
</head>
<body>
    <h1 style="text-align: center;">
        <img src="/images/profile.jpg" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px;"/>
        &nbsp;오현철 과장 업무관리
    </h1>

    <!-- 타임라인(짧은 게시물) 영역 -->
    <img src="/images/icons/sparkle.png" class="icon"/>타임라인

    <div class="post-compose">
        <form name="postFrm" method="post" action="/post-save">
            <textarea name="contents" maxlength="280" placeholder="무슨 일이 있었나요?" required></textarea>
            <div class="post-compose-footer">
                <c:if test="${!authenticated}">
                <div class="post-compose-identity">
                    <input type="text" name="userId" class="menu-input" placeholder="닉네임" maxlength="50" required/>
                    <input type="password" name="userPw" class="menu-input" placeholder="비밀번호(수정/삭제 시 필요)" autocomplete="off" required/>
                </div>
                </c:if>
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
                <div class="post-meta">
                    <img src="/images/icons/identification-card.png" class="icon"/><b><c:out value="${item.userId}"/></b>
                    &nbsp;·&nbsp;${item.createdAt}
                </div>
                <div class="post-contents"><c:out value="${item.contents}"/></div>
                <div class="post-actions">
                    <form method="post" action="/post-like" style="display: inline;">
                        <input type="hidden" name="seq" value="${item.seq}" />
                        <button type="submit" class="link-like">♥ ${item.likeCount}</button>
                    </form>
                    &nbsp;|&nbsp;<a onclick="toggleComments(${item.seq});"><img src="/images/icons/envelope-simple.png" class="icon"/><span id="replyCountLabel-${item.seq}">댓글 ${item.replyCount}개</span></a>
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
                <div class="comments-section" id="comments-${item.seq}"></div>
            </div>
        </c:forEach>
        <c:if test="${hasMorePosts}">
            <div class="post-more"><a href="/post">타임라인 전체보기</a></div>
        </c:if>
    </c:otherwise>
    </c:choose>

    <form action="/login" method="post" name="frm">
        <p style="text-align: center;">
            <button type="button" class="login-input" onclick="passkeyLogin('login', DEFAULT_USER_ID);"><img src="/images/icons/lock-key.png" class="icon"/>PASSKEY LOGIN</button><br/>
            <a href="javascript:void(0);" onclick="showCredentialLogin();" style="font-size: 10pt; color: gray;">ID/PASSWORD LOGIN</a>
        </p>
        <p id="credentialBlock" style="text-align: center; display: none;">
            <input type="text" name="userId" id="userId" autocomplete="off" value="${userId}" class="login-input"/><br/>
            <input type="password" name="userPw" class="login-input" onkeydown="goSubmit();"/><br/>
            <button type="button" class="login-input" onclick="document.frm.submit();"><img src="/images/icons/key.png" class="icon"/>LOGIN</button><br/>
            <a href="javascript:void(0);" onclick="passkeyLogin('register');" style="font-size: 10pt; color: gray;">PASSKEY REGISTRATION</a>
        </p>
        <p style="font-size: 10pt; text-align: center; color: gray;">
            hc5642@me.com
        </p>
    </form>

    <form name="deleteFrm" method="post" action="/post-delete" style="display: none;">
        <input type="hidden" id="deleteSeq" name="seq" value="" />
        <input type="hidden" id="deleteUserPw" name="userPw" value="" />
    </form>
</body>
</html>
