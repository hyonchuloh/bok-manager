<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>연락처 엑셀 업로드</title>
    <link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
    <style>
        .result.success { background:#e6ffed; border:1px solid #4caf50; color:#116633 }
        .result.error { background:#ffecec; border:1px solid #d32f2f; color:#6d0b0b }
    </style>
</head>
<body>
<h2>
    <img src="/images/profile.jpg" style="border-radius: 70%; width: 40px; padding: 0px; margin: 0px;"/>
	&nbsp;연락처 엑셀 업로드
</h2>
<ul>
    <li>엑셀 파일(.xlsx)을 선택하여 업로드 하세요. (form field name: <code>file</code>)</li>

    <form id="uploadForm" method="post" enctype="multipart/form-data">
        <li>
            <input type="file" name="file" id="fileInput" accept=".xlsx,.xls" required /></li>
        <li><button type="submit">업로드</button></li>
        <li><button type="button" id="closeBtn">닫기</button></li>
        <li><button type="button" onclick="location.href='/etc/sample.xlsx'">sample.xlsx 다운로드</button></li>
    </form>
</ul>

<div id="result" class="result"></div>

<script>
    const form = document.getElementById('uploadForm');
    const resultEl = document.getElementById('result');
    const closeBtn = document.getElementById('closeBtn');

    closeBtn.addEventListener('click', () => {
        // If opened as a popup window, close it. Otherwise, just go back.
        if (window.opener) {
            window.close();
        } else {
            history.back();
        }
    });

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        resultEl.style.display = 'none';
        resultEl.textContent = '';

        const fileInput = document.getElementById('fileInput');
        if (!fileInput.files || fileInput.files.length === 0) {
            showError('업로드할 파일을 선택하세요.');
            return;
        }

        const fd = new FormData();
        fd.append('file', fileInput.files[0]);
        const nameVal = document.getElementById('nameInput').value;
        if (nameVal) fd.append('name', nameVal);

        try {
            const resp = await fetch('/manager/callbook/upload', {
                method: 'POST',
                body: fd,
                credentials: 'same-origin'
            });

            // fetch follows redirects; response.url will be final URL after redirect.
            const finalUrl = resp.url || '';
            // try to extract resultMsg query parameter from finalUrl
            const u = new URL(finalUrl, window.location.origin);
            const msg = u.searchParams.get('resultMsg');
            if (msg) {
                showSuccess(decodeURIComponent(msg));
            } else {
                // If no message, show a generic success notice
                showSuccess('업로드가 완료되었습니다.');
            }
        } catch (err) {
            showError('업로드 중 오류가 발생했습니다: ' + (err.message || err));
        }
    });

    function showSuccess(text) {
        resultEl.className = 'result success';
        resultEl.style.display = 'block';
        resultEl.textContent = text;
    }
    function showError(text) {
        resultEl.className = 'result error';
        resultEl.style.display = 'block';
        resultEl.textContent = text;
    }
</script>
</body>
</html>
