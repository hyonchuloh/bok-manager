<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" href="/images/tube.ico">
<link rel="apple-touch-icon" href="/images/tube-apple-touch-icon.png">
<link rel="apple-touch-icon-precomposed" href="/images/tube-apple-touch-icon-precomposed.png">
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1"/>
<title>오현철 과장 업무관리</title>
<style>
body {
    width: auto;
}
</style>
<script>
    async function goSubmit() {
        if (window.event.keyCode == 13 ) {
            document.frm.submit();
        }
    }

    async function passkeyLogin(mode) {
        const userId = document.getElementById('userId').value;
        if (!userId) {
            alert('먼저 사용자 아이디를 입력하세요.');
            return;
        }

        const response = await fetch('/login/passkey/options', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ userId, mode })
        });

        const options = await response.json();
        if (options.error) {
            alert(options.error);
            return;
        }

        const publicKey = {
            ...options,
            challenge: Uint8Array.from(atob(options.challenge.replace(/-/g, '+').replace(/_/g, '/')), c => c.charCodeAt(0)),
            user: options.user ? {
                ...options.user,
                id: Uint8Array.from(atob(options.user.id.replace(/-/g, '+').replace(/_/g, '/')), c => c.charCodeAt(0))
            } : undefined,
            allowCredentials: options.allowCredentials ? options.allowCredentials.map(cred => ({
                ...cred,
                id: Uint8Array.from(atob(cred.id.replace(/-/g, '+').replace(/_/g, '/')), c => c.charCodeAt(0))
            })) : undefined,
        };

        let credential;
        try {
            if (mode === 'register') {
                credential = await navigator.credentials.create({ publicKey });
            } else {
                credential = await navigator.credentials.get({ publicKey });
            }
        } catch (e) {
            alert('Passkey 동작이 실패했습니다: ' + e.message);
            return;
        }

        const body = {
            id: credential.id,
            type: credential.type,
            response: {
                clientDataJSON: arrayBufferToBase64Url(credential.response.clientDataJSON),
                authenticatorData: credential.response.authenticatorData ? arrayBufferToBase64Url(credential.response.authenticatorData) : undefined,
                signature: credential.response.signature ? arrayBufferToBase64Url(credential.response.signature) : undefined,
                attestationObject: credential.response.attestationObject ? arrayBufferToBase64Url(credential.response.attestationObject) : undefined
            }
        };

        const verifyUrl = mode === 'register' ? '/login/passkey/register-verify' : '/login/passkey/assert';
        const verifyResponse = await fetch(verifyUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const verifyResult = await verifyResponse.json();
        if (verifyResult.success) {
            window.location.href = '/manager/calendar';
        } else {
            alert(verifyResult.error || 'Passkey 인증에 실패했습니다.');
        }
    }

    function arrayBufferToBase64Url(buffer) {
        const bytes = new Uint8Array(buffer);
        let binary = '';
        bytes.forEach(b => binary += String.fromCharCode(b));
        return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
    }
</script>
</head>
<body onload="document.getElementById('userId').focus();">
    <h1 style="text-align: center;">
        <img src="/images/profile.jpg" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px;"/>
        &nbsp;오현철 과장 업무관리
    </h1>
    <table align="center" style="border: 0px;">
        <tr>
            <td style="text-align: left; font-size: 20px; color: black; 
                       line-height: 1.5em; border-left:#CCCCCC 3px solid; 
                       border-right:0px; border-top: 0px; border-bottom: 0px; 
                       padding-left:15px; ">
                ${message}
            </td>
        </tr>
    </table>
    <form action="/login" method="post" name="frm">
        <p style="text-align: center;">
            <input type="text" name="userId" id="userId" autocomplete="off" value="${userId}" class="login-input"/><br/>
            <input type="password" name="userPw" class="login-input" onkeydown="goSubmit();"/><br/>
            <input type="button" value="로그인" class="login-input" onclick="document.frm.submit();"/><br/>
            <input type="button" value="Apple Passkey 로그인" class="login-input" onclick="passkeyLogin('login');"/><br/>
            <a href="javascript:void(0);" onclick="passkeyLogin('register');" style="font-size: 10pt; color: gray;">Apple Passkey 등록</a>
            <p style="font-size: 10pt; text-align: center; color: gray;">hc5642@me.com</p>
        </p>
    </form>
</body>
</html>