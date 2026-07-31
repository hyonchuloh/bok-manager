<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
        --content-width: 340px;
    }
    body {
        width: 340px !important;
        margin: 0px auto !important;
    }
    .post-compose, .post-card {
        max-width: 340px;
        margin-left: auto;
        margin-right: auto;
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

    /* 페이지 이동 없이 다음 게시물 묶음을 AJAX로 불러와 목록 하단에 이어붙인다 */
    function loadMorePosts(offset) {
        fetch('/post-feed-more?offset=' + offset, { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
            .then(function(res) { return res.text(); })
            .then(function(html) {
                const tmp = document.createElement('div');
                tmp.innerHTML = html;
                const batch = tmp.querySelector('#feedItemsBatch');
                document.getElementById('feedList').insertAdjacentHTML('beforeend', batch.innerHTML);
                const marker = tmp.querySelector('#postMoreMarker');
                const moreWrap = document.getElementById('postMoreWrap');
                if (marker && marker.getAttribute('data-has-more') === 'true') {
                    const nextOffset = marker.getAttribute('data-next-offset');
                    moreWrap.querySelector('a').setAttribute('onclick', 'loadMorePosts(' + nextOffset + '); return false;');
                } else if (moreWrap) {
                    moreWrap.remove();
                }
            });
    }
</script>
</head>
<body>
    <h1 style="text-align: center;">
        <img src="/images/profile.jpg" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px;"/>
        &nbsp;오현철 과장 업무관리
    </h1>

    <!-- 타임라인(짧은 게시물) 영역 -->
    <img src="/images/icons/sparkle.png" class="icon"/>　　타임라인

    <c:if test="${authenticated}">
        <div class="post-compose">
            <form name="postFrm" method="post" action="/post-save">
                <textarea name="contents" maxlength="280" placeholder="무슨 일이 있었나요?" required></textarea>
                <div class="post-compose-footer">
                        <a href="/manager/calendar" >전체달력</a>
                        | <a href="/manager/calendar-week">평일달력</a>
                        | <a href="/manager/callbook">연락처</a>
                    <input type="submit" class="menu-input" value="게시하기"/>
                </div>
            </form>
        </div>
    </c:if>

    <c:choose>
    <c:when test="${empty items}">
        <div class="post-empty">아직 게시물이 없습니다. 첫 게시물을 작성해보세요!</div>
    </c:when>
    <c:otherwise>
        <div id="feedList">
            <%@ include file="/WEB-INF/jsp/post/feedItemFragment.jsp" %>
        </div>
        <c:if test="${hasMorePosts}">
            <div class="post-more" id="postMoreWrap">
                <a href="javascript:void(0);" onclick="loadMorePosts(${fn:length(items)}); return false;">더 보기</a>
            </div>
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
            <a href="javascript:void(0);" onclick="passkeyLogin('register');" style="font-size: 10pt; color: gray;">PASSKEY REGISTRATION</a> | 
            <a href="/logout" style="font-size: 10pt; color: gray;">LOGOUT</a>
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
