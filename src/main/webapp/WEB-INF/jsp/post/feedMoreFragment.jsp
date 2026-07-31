<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- index의 "더 보기" AJAX 응답: 게시물 카드 묶음 + 다음 페이지 존재 여부/오프셋 마커 --%>
<div id="feedItemsBatch"><%@ include file="/WEB-INF/jsp/post/feedItemFragment.jsp" %></div>
<div id="postMoreMarker" data-has-more="${hasMore}" data-next-offset="${nextOffset}" style="display:none;"></div>
