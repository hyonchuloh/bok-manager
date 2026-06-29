// 로그인 페이지 진입 시 자동 패스키 인증에 사용할 기본 사용자 아이디
const DEFAULT_USER_ID = 'ohhyonchul';

async function goSubmit() {
    if (window.event.keyCode == 13 ) {
        document.frm.submit();
    }
}

// ID/PW 입력 영역을 표시하고 아이디 입력란에 포커스
function showCredentialLogin() {
    document.getElementById('credentialBlock').style.display = 'block';
    document.getElementById('userId').focus();
}

async function passkeyLogin(mode, userIdOverride) {
    const userId = userIdOverride || document.getElementById('userId').value;
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