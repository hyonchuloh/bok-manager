
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" href="/images/bok.ico">
<title>Calendar - ${name}</title>
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
</head>
<body>
    
    <h1>
        <!-- img src="/images/profile.jpg" style="width: 40px ;border-radius: 100%; overflow: hidden;"/-->
         Hello, ${userId} World!
        <div style="float: right; padding-right: 3px; padding-top:0px;">
            <a href="/logout" style="font-size: 10pt; color: azure; font-weight: 100;">로그아웃</a> 
            <!--img src="/images/image02.png" style="width: 40px ;border-radius: 100%; overflow: hidden;" /-->
        </div>
    </h1>
    <ul>
        <li><a href="/manager/calendar/ohhyonchul">달력</a></li>
        <li><a href="/manager/callbook">연락처</a></li>
        <li><a href="/logout">로그아웃</a></li>
    </ul>   
</body>
</html>