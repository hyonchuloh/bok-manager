<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- 인라인 댓글 조각 뷰. index/타임라인 화면에서 게시물 이동 없이 AJAX로 불러와 삽입한다. --%>
<div class="reply-count-marker" data-count="${fn:length(replies)}" style="display:none;"></div>
<c:if test="${not empty resultMsg}">
    <div class="post-empty" style="padding: 4px 0;">${resultMsg}</div>
</c:if>
<c:if test="${empty replies}">
    <div class="post-empty" style="padding: 8px 0;">아직 댓글이 없습니다.</div>
</c:if>
<c:forEach var="item" items="${replies}" varStatus="rowStatus">
    <c:set var="canManage" value="${isAdmin || (empty item.password && item.userId eq sessionUserId)}"/>
    <%-- 이전/다음 댓글의 depth를 미리 살펴, 같은 레벨(형제)이 있는 구간만 선을 이어준다 --%>
    <c:set var="prevDepth" value="${rowStatus.index > 0 ? replies[rowStatus.index - 1].depth : 0}"/>
    <c:set var="nextDepth" value="${rowStatus.index + 1 < fn:length(replies) ? replies[rowStatus.index + 1].depth : 0}"/>
    <div class="comment-item">
        <c:forEach var="col" begin="1" end="${item.depth}">
            <c:choose>
                <c:when test="${col == item.depth}">
                    <%-- 아바타가 놓이는 노드 컬럼: 같은 레벨의 형제가 있는 방향으로만 선을 그린다 --%>
                    <c:set var="hasSiblingAbove" value="${prevDepth >= col}"/>
                    <c:set var="hasSiblingBelow" value="${nextDepth >= col}"/>
                    <c:choose>
                        <c:when test="${hasSiblingAbove && hasSiblingBelow}"><c:set var="nodeLineClass" value=""/></c:when>
                        <c:when test="${hasSiblingAbove}"><c:set var="nodeLineClass" value=" tree-col-node-no-below"/></c:when>
                        <c:when test="${hasSiblingBelow}"><c:set var="nodeLineClass" value=" tree-col-node-no-above"/></c:when>
                        <c:otherwise><c:set var="nodeLineClass" value=" tree-col-node-isolated"/></c:otherwise>
                    </c:choose>
                    <%-- 부모(상위 depth)의 형제가 없어 새로 갈라져 나온 댓글은 부모 tree 줄기에서
                         곡선으로 이어지는 커넥터를 덧붙인다. depth 1은 게시글의 tree-col(간격 포함 42px)에서,
                         그 외 대댓글은 바로 왼쪽 조상 컬럼(32px)에서 갈라져 나온다. --%>
                    <c:if test="${!hasSiblingAbove}">
                        <c:choose>
                            <c:when test="${item.depth == 1}">
                                <c:set var="nodeLineClass" value="${nodeLineClass} tree-col-first-link"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="nodeLineClass" value="${nodeLineClass} tree-col-parent-link"/>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                    <span class="tree-col${nodeLineClass}">
                        <c:choose>
                            <c:when test="${item.userId eq adminUserId}">
                                <img src="/images/profile.jpg" class="tree-avatar" alt=""/>
                            </c:when>
                            <c:otherwise>
                                <img src="/images/icons/ghost.png" class="tree-avatar" alt=""/>
                            </c:otherwise>
                        </c:choose>
                    </span>
                </c:when>
                <c:otherwise>
                    <span class="tree-col${nextDepth >= col ? '' : ' tree-col-stop'}"></span>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <div class="comment-body">
            <div class="post-meta">
                <b><c:out value="${item.userId}"/></b>
                &nbsp;·&nbsp;${item.createdAtFormatted}
            </div>
            <div class="post-contents"><c:out value="${item.contents}"/></div>
            <div class="post-actions">
                <form method="post" action="/post-like" onsubmit="return submitCommentForm(event, this, ${rootSeq});" style="display: inline;">
                    <input type="hidden" name="seq" value="${item.seq}" />
                    <input type="hidden" name="redirectSeq" value="${rootSeq}" />
                    <button type="submit" class="link-like"><img src="/images/icons/heart-straight.png" class="icon" alt=""/>${item.likeCount}</button>
                </form>
                <c:if test="${item.depth < 5}">
                &nbsp;|&nbsp;<a onclick="toggleReplyForm(${item.seq});">답글</a>
                </c:if>
                <c:if test="${canManage || not empty item.password}">
                    &nbsp;|&nbsp;<a onclick="toggleEditForm(${item.seq});">수정</a>
                    <c:choose>
                    <c:when test="${canManage}">
                    &nbsp;|&nbsp;<a onclick="deleteComment(${item.seq}, ${rootSeq}, false);">삭제</a>
                    </c:when>
                    <c:otherwise>
                    &nbsp;|&nbsp;<a onclick="deleteComment(${item.seq}, ${rootSeq}, true);">삭제</a>
                    </c:otherwise>
                    </c:choose>
                </c:if>
            </div>
            <c:if test="${item.depth < 5}">
            <div class="reply-form" id="replyForm-${item.seq}">
                <form method="post" action="/post-reply" onsubmit="return submitCommentForm(event, this, ${rootSeq});">
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
            </c:if>
            <div class="edit-form" id="editForm-${item.seq}">
                <form method="post" action="/post-edit" onsubmit="return submitCommentForm(event, this, ${rootSeq});">
                    <input type="hidden" name="seq" value="${item.seq}" />
                    <input type="hidden" name="redirectSeq" value="${rootSeq}" />
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
    </div>
</c:forEach>
<div class="comment-compose">
    <form method="post" action="/post-reply" onsubmit="return submitCommentForm(event, this, ${rootSeq});">
        <input type="hidden" name="parentSeq" value="${rootSeq}" />
        <input type="hidden" name="rootSeq" value="${rootSeq}" />
        <textarea name="contents" maxlength="280" placeholder="댓글을 입력하세요" required></textarea>
        <div class="comment-compose-footer">
            <c:if test="${!authenticated}">
            <div class="post-compose-identity">
                <input type="text" name="userId" class="menu-input" placeholder="닉네임" maxlength="50" required/>
                <input type="password" name="userPw" class="menu-input" placeholder="비밀번호(수정/삭제 시 필요)" autocomplete="off" required/>
            </div>
            </c:if>
            <input type="submit" class="menu-input" value="댓글 작성"/>
        </div>
    </form>
</div>
