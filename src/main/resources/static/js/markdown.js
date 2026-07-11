// 게시판 본문에 사용된 표준 Markdown 문법을 HTML로 변환해서 화면에 표시하기 위한 경량 파서

// contenteditable에서 줄바꿈은 보통 <div>/<br> 형태로 저장되므로, 정규식이
// 줄 단위(^, $, multiline)로 동작할 수 있도록 먼저 순수 개행 문자로 정규화한다.
function normalizeContentEditableHtml(html) {
    return html
        .replace(/<div><br\s*\/?><\/div>/gi, '\n')
        .replace(/<\/div>\s*<div>/gi, '\n')
        .replace(/<div>/gi, '')
        .replace(/<\/div>/gi, '\n')
        .replace(/<br\s*\/?>/gi, '\n');
}

function renderMarkdown(rawHtml) {
    if (!rawHtml) return '';
    let html = normalizeContentEditableHtml(rawHtml);

    // 코드 블록 ```...```
    html = html.replace(/```([\s\S]*?)```/g, (m, code) => '<pre><code>' + code.trim() + '</code></pre>');

    // 커스텀 강조 마크업: {{...}} 파랑, [[...]] 빨강, ((...)) 회색
    html = html.replace(/\{\{([\s\S]+?)\}\}\}/g, '<span style="color: blue;">$1</span>');
    html = html.replace(/\[\[([\s\S]+?)\]\]\]/g, '<span style="color: red;">$1</span>');
    html = html.replace(/\(\(([\s\S]+?)\)\)/g, '<span style="color: #999999;">$1</span>');

    // 제목
    html = html.replace(/^###### (.*)$/gm, '<h6>$1</h6>');
    html = html.replace(/^##### (.*)$/gm, '<h5>$1</h5>');
    html = html.replace(/^#### (.*)$/gm, '<h4>$1</h4>');
    html = html.replace(/^### (.*)$/gm, '<h3>$1</h3>');
    html = html.replace(/^## (.*)$/gm, '<h2>$1</h2>');
    html = html.replace(/^# (.*)$/gm, '<h1>$1</h1>');

    // 인용문
    html = html.replace(/^&gt; ?(.*)$/gm, '<blockquote>$1</blockquote>');
    html = html.replace(/^> ?(.*)$/gm, '<blockquote>$1</blockquote>');

    // 수평선
    html = html.replace(/^(-{3,}|\*{3,})$/gm, '<hr/>');

    // 굵게, 기울임, 취소선, 인라인 코드, 링크
    html = html.replace(/\*\*(.+?)\*\*|__(.+?)__/g, (m, a, b) => '<b>' + (a || b) + '</b>');
    html = html.replace(/(?<!\*)\*([^*\n]+)\*(?!\*)|(?<!_)_([^_\n]+)_(?!_)/g, (m, a, b) => '<i>' + (a || b) + '</i>');
    html = html.replace(/~~(.+?)~~/g, '<s>$1</s>');
    html = html.replace(/`([^`]+?)`/g, '<code>$1</code>');
    html = html.replace(/\[(.+?)\]\((.+?)\)/g, '<a href="$2" target="_blank" rel="noopener">$1</a>');

    // 남은 개행은 줄바꿈으로 표시
    html = html.replace(/\n/g, '<br/>');

    return html;
}

// 렌더링 직전의 원본(수정 가능한 Markdown 원문)을 요소별로 보관해 둔다.
const markdownRawContentCache = {};

// id로 지정된 요소의 현재 내용을 Markdown으로 해석해서 다시 그려준다.
// 렌더링 전 내용은 markdownRawContentCache에 저장해 두어 편집 시 복원할 수 있게 한다.
function applyMarkdownRendering(elementId) {
    const el = document.getElementById(elementId);
    if (!el) return;
    markdownRawContentCache[elementId] = el.innerHTML;
    el.innerHTML = renderMarkdown(el.innerHTML);
}

// 저장된 원본(Markdown) 내용을 반환한다. 아직 렌더링된 적이 없으면 null.
function getMarkdownRawContent(elementId) {
    return Object.prototype.hasOwnProperty.call(markdownRawContentCache, elementId)
        ? markdownRawContentCache[elementId]
        : null;
}

// contenteditable에서 Enter 입력 시 브라우저가 <div>/<p>로 줄을 나누지 않고,
// textarea처럼 순수 개행 문자(\n)를 직접 삽입하도록 강제한다.
function handleEditableEnter(event) {
    if (event.key !== 'Enter') return;
    event.preventDefault();
    document.execCommand('insertText', false, '\n');
}

// contenteditable 원본(HTML)을 저장용 순수 텍스트로 변환한다.
// 줄바꿈은 유지하되 그 외 태그는 모두 제거하고 HTML 엔티티는 원래 문자로 되돌린다.
function toPlainText(html) {
    if (!html) return '';
    let text = normalizeContentEditableHtml(html).replace(/<[^>]+>/g, '');
    const decoder = document.createElement('textarea');
    decoder.innerHTML = text;
    // JSP 마크업 들여쓰기 등으로 인해 붙는 시작/끝의 불필요한 공백·개행을 제거한다.
    return decoder.value.trim();
}

// 커서가 요소 안으로 들어오면(focus) 렌더링되지 않은 원문을 보여주고,
// 포커스가 빠져나가면(blur) 다시 Markdown을 렌더링해서 보여준다.
function enableMarkdownEditToggle(elementId) {
    const el = document.getElementById(elementId);
    if (!el) return;
    el.addEventListener('focus', function () {
        const raw = getMarkdownRawContent(elementId);
        if (raw !== null) {
            el.innerHTML = raw;
        }
    });
    el.addEventListener('blur', function () {
        applyMarkdownRendering(elementId);
    });
}
