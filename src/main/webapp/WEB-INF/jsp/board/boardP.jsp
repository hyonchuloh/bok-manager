<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>한국은행 오현철 과장 업무관리</title>
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
</head>
<body>
	<h2>
		<img src="/images/profile.jpg" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px;"/>
		&nbsp;${resultMsg}
	</h2>
    <table style="width: 100%; table-layout:fixed;">
		<tr style="cursor: pointer;" onclick="location.href='/manager/board/${board.seq}'">
            <td style="text-align: center;">${board.seq}</td>
            <td style="text-align: left;">${board.title}</td>
            <td style="color: gray; text-align: center;">${board.createdAt}</td>
        </tr>
        <tr>
            <td colspan="3" id="latestBoardContents" contenteditable="true" onpaste="handlePaste(event)" style="word-wrap: break-word;">
                ${board.contents}
            </td>
        </tr>
	</table>
	<p align="center">
			<img src="/images/TheBankOfKorea.png" height="10px"/>
	</p>
</body>
</html>