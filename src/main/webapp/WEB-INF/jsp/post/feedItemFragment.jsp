<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- index의 게시물 카드 목록. 최초 렌더링과 "더 보기" AJAX 응답이 이 조각을 함께 사용한다. --%>
<c:forEach var="item" items="${items}">
    <c:set var="canManage" value="${isAdmin || (empty item.password && item.userId eq sessionUserId)}"/>
    <div class="post-card">
        <div class="tree-col">
            <c:choose>
                <c:when test="${item.infoFlag}">
                    <img src="/images/icons/robot.png" class="tree-avatar" alt=""/>
                </c:when>
                <c:when test="${item.userId eq adminUserId}">
                    <img src="/images/profile.jpg" class="tree-avatar" alt=""/>
                </c:when>
                <c:otherwise>
                    <img src="/images/icons/ghost.png" class="tree-avatar" alt=""/>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="post-body">
            <c:if test="${!item.infoFlag}">
            <div class="post-meta">
                ${fn:replace(item.createdAt, 'T', ' ')}
            </div>
            </c:if>
            <div class="post-contents"><c:out value="${item.contents}"/></div>
            <div class="post-actions">
                <c:if test="${!item.infoFlag}">
                <form method="post" action="/post-like" style="display: inline;">
                    <input type="hidden" name="seq" value="${item.seq}" />
                    <button type="submit" class="link-like"><img src="/images/icons/heart-straight.png" class="icon" alt=""/>${item.likeCount}</button>
                </form>
                &nbsp;|&nbsp;<a onclick="toggleComments(${item.seq});"><span id="replyCountLabel-${item.seq}">댓글 ${item.replyCount}개</span></a>
                </c:if>
                <c:if test="${canManage || not empty item.password}">
                    <c:if test="${!item.infoFlag}">&nbsp;|&nbsp;</c:if>
                    <a onclick="toggleEditForm(${item.seq});">수정</a>
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
            <c:if test="${!item.infoFlag}">
            <div class="comments-section" id="comments-${item.seq}"></div>
            </c:if>
        </div>
    </div>
</c:forEach>
