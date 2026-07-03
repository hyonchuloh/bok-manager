<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" href="/images/tube.ico">
<link rel="apple-touch-icon" href="/images/tube-apple-touch-icon.png">
<link rel="apple-touch-icon-precomposed" href="/images/tube-apple-touch-icon-precomposed.png">
<link rel="stylesheet" type="text/css" href="/css/bokwire.css" />
<link rel="stylesheet" type="text/css" href="/css/tetris.css" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1"/>
<title>오현철 과장 업무관리</title>
<style>
    body {
        width: auto;
    }
</style>
<script type="text/javascript" src="/js/pathkey.js"></script>
</head>
<body>
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
        <tr>
            <td style="border: 0px; text-align: center; padding: 0px; margin: 0px;">
                <!-- 테트리스 게임 영역 -->
                <div id="container">
                    <div id="game-panel">
                        <canvas id="game" width="240" height="400"></canvas>
                    </div>
                    <div id="info-panel">
                        <canvas id="preview" width="80" height="80"></canvas>
                        <div id="score" class="stat-item">🎯점수: 0</div>
                        <div id="lines">🧹없앤 줄: 0</div>
                        <div id="speed">⚡순간속도: 0.0회/분</div>
                        <div id="max-speed" class="stat-item">🏆최대 순간속도: 0.0회/분</div>
                        <div id="avg-speed">📈평균속도: 0.0회/분</div>
                        <div id="game-over">게임 오버</div>
                    </div>
                </div>
                <!-- 테트리스 게임 영역 -->
             </td>
        </tr>
    </table>
    <form action="/login" method="post" name="frm">
        <p style="text-align: center;">
            <input type="button" value="🔐 PASSKEY LOGIN" class="login-input" onclick="passkeyLogin('login', DEFAULT_USER_ID);"/><br/>
            <a href="javascript:void(0);" onclick="showCredentialLogin();" style="font-size: 10pt; color: gray;">ID/PASSWORD LOGIN</a>
        </p>
        <p id="credentialBlock" style="text-align: center; display: none;">
            <input type="text" name="userId" id="userId" autocomplete="off" value="${userId}" class="login-input"/><br/>
            <input type="password" name="userPw" class="login-input" onkeydown="goSubmit();"/><br/>
            <input type="button" value="🔑 LOGIN" class="login-input" onclick="document.frm.submit();"/><br/>
            <!--a href="javascript:void(0);" onclick="passkeyLogin('register');" style="font-size: 10pt; color: gray;">PASSKEY REGISTRATION</a -->
        </p>
        <p style="font-size: 10pt; text-align: center; color: gray;">
            <span onclick="playerRotate();">　　　▲　　　</span><br/>
            <span onclick="playerMove(-1);">　　　◀</span>
            <span onclick="playerHardDrop();">hc5642@me.com</span>
            <span onclick="playerMove(1);">▶　　　</span>
        </p>
    </form>

    <script>
        const canvas = document.getElementById('game');
        const context = canvas.getContext('2d');
        context.scale(20, 20);

        const previewCanvas = document.getElementById('preview');
        const previewContext = previewCanvas.getContext('2d');
        previewContext.scale(20, 20);

        function createMatrix(w, h) {
            const matrix = [];
            while (h--) matrix.push(new Array(w).fill(0));
            return matrix;
        }

        function createPiece(type) {
            const pieces = {
                T: [
                [0, 1, 0],
                [1, 1, 1],
                [0, 0, 0],
                ],
                O: [
                [2, 2],
                [2, 2],
                ],
                L: [
                [0, 0, 3],
                [3, 3, 3],
                [0, 0, 0],
                ],
                J: [
                [4, 0, 0],
                [4, 4, 4],
                [0, 0, 0],
                ],
                I: [
                [0, 5, 0, 0],
                [0, 5, 0, 0],
                [0, 5, 0, 0],
                [0, 5, 0, 0],
                ],
                S: [
                [0, 6, 6],
                [6, 6, 0],
                [0, 0, 0],
                ],
                Z: [
                [7, 7, 0],
                [0, 7, 7],
                [0, 0, 0],
                ]
            };
            return pieces[type];
        }

        const arena = createMatrix(12, 20);

        const player = {
            pos: {x: 0, y: 0},
            matrix: null,
            nextMatrix: null,
            score: 0,
            linesCleared: 0,
            hardDropInterval: 0,
            hardDropCount: 0,
            totalHardDropTime: 0,
            maxHardDropSpeed: 0,
        };

        const colors = [
            null,
            'purple',
            'yellow',
            'orange',
            'blue',
            'cyan',
            'green',
            'red',
        ];

        function drawMatrix(matrix, offset, ctx) {
            matrix.forEach((row, y) => {
                row.forEach((value, x) => {
                if (value !== 0) {
                    ctx.fillStyle = colors[value];
                    ctx.fillRect(x + offset.x, y + offset.y, 1, 1);
                }
                });
            });
        }

        function drawGhost() {
            if (!player.matrix) return;
            const ghostPos = {...player.pos};
            while (!collide(arena, {matrix: player.matrix, pos: ghostPos})) {
                ghostPos.y++;
            }
            ghostPos.y--;
            player.matrix.forEach((row, y) => {
                row.forEach((value, x) => {
                    if (value !== 0) {
                        context.fillStyle = 'rgba(255,255,255,0.2)';
                        context.fillRect(x + ghostPos.x, y + ghostPos.y, 1, 1);
                    }
                });
            });
        }

        function merge(arena, player) {
            player.matrix.forEach((row, y) => {
                row.forEach((value, x) => {
                if (value !== 0) {
                    arena[y + player.pos.y][x + player.pos.x] = value;
                }
                });
            });
        }

        function collide(arena, player) {
            const m = player.matrix;
            const o = player.pos;
            for (let y = 0; y < m.length; ++y) {
                for (let x = 0; x < m[y].length; ++x) {
                if (m[y][x] !== 0 &&
                    (arena[y + o.y] &&
                    arena[y + o.y][x + o.x]) !== 0) {
                    return true;
                    }
                }
            }
            return false;
        }

        function playerDrop() {
            if (gameOver) return;
            player.pos.y++;
            if (collide(arena, player)) {
                player.pos.y--;
                merge(arena, player);
                arenaSweep();
                playerReset();
            }
            dropCounter = 0;
        }

        function playerHardDrop() {
            if (gameOver) return;
            const hardDropTime = performance.now();
            while (!collide(arena, player)) {
                player.pos.y++;
            }
            player.pos.y--;
            merge(arena, player);
            const bonus = calculateBonus(player.matrix, player.pos);
            player.score += bonus;
            arenaSweep();
            playerReset();
            updateScore();
            dropCounter = 0;
            if (lastHardDropTime) {
                player.hardDropInterval = hardDropTime - lastHardDropTime;
            }
            const instantSpeed = player.hardDropInterval > 0
                ? 60 / (player.hardDropInterval / 1000)
                : 0;
            player.maxHardDropSpeed = Math.max(player.maxHardDropSpeed, instantSpeed);
            player.hardDropCount += 1;
            player.totalHardDropTime += player.hardDropInterval > 0 ? player.hardDropInterval : 0;
            lastHardDropTime = hardDropTime;
        }

        function calculateBonus(matrix, position) {
            let bonus = 0;
            for (let y = 0; y < matrix.length; y++) {
                for (let x = 0; x < matrix[y].length; x++) {
                if (matrix[y][x] !== 0) {
                    let currentY = position.y + y + 1;
                    while (currentY < arena.length &&
                        arena[currentY][position.x + x] === 0) {
                        bonus += 10;
                        currentY++;
                        }
                    }
                }
            }
            return bonus;
        }

        function playerMove(dir) {
            if (gameOver) return;
            player.pos.x += dir;
            if (collide(arena, player)) {
                player.pos.x -= dir;
            }
        }

        function playerReset() {
            if (gameOver) return;
            if (!player.nextMatrix) {
                player.nextMatrix = createPiece(randomPieceType());
            }
            player.matrix = player.nextMatrix;
            player.nextMatrix = createPiece(randomPieceType());
            player.pos.y = 0;
            player.pos.x = (arena[0].length / 2 | 0) - (player.matrix[0].length / 2 | 0);
            if (collide(arena, player)) {
                gameOver = true;
                if (animationFrameId) {
                    cancelAnimationFrame(animationFrameId);
                    animationFrameId = null;
                }
                player.matrix = null;
                lastHardDropTime = 0;
                dropInterval = 1000;
                if (player.score >= 10000) {
                    alert("테트리스 10,000점 이상 달성! 패스워드 초기화가 가능합니다.(단, 계정이 존재해야 합니다.)");
                }
                updateScore();
                draw();
                return;
            }
            drawNext();
        }

        function randomPieceType() {
            const pieces = 'TJLOSZI';
            return pieces[Math.floor(Math.random() * pieces.length)];
        }

        function drawNext() {
            previewContext.fillStyle = '#000';
            previewContext.fillRect(0, 0, previewCanvas.width, previewCanvas.height);
            drawMatrix(player.nextMatrix, {x: 1, y: 1}, previewContext);
        }

        function arenaSweep() {
            let rowCount = 1;
            let clearedLines = 0;
            outer: for (let y = arena.length - 1; y >= 0; --y) {
                for (let x = 0; x < arena[y].length; ++x) {
                if (arena[y][x] === 0) continue outer;
                }
                const row = arena.splice(y, 1)[0].fill(0);
                arena.unshift(row);
                ++y;
                player.score += rowCount * 100;
                clearedLines += 1;
                rowCount *= 2;
            }
            if (clearedLines > 0) {
                player.linesCleared += clearedLines;
            }
            updateScore();
        }

        function rotate(matrix) {
            for (let y = 0; y < matrix.length; ++y) {
                for (let x = 0; x < y; ++x) {
                [matrix[x][y], matrix[y][x]] = [matrix[y][x], matrix[x][y]];
                }
            }
            matrix.forEach(row => row.reverse());
        }

        function playerRotate() {
            const pos = player.pos.x;
            let offset = 1;
            // 반시계 방향 회전 (시계 방향 3번)
            rotate(player.matrix);
            rotate(player.matrix);
            rotate(player.matrix);
            while (collide(arena, player)) {
                player.pos.x += offset;
                offset = -(offset + (offset > 0 ? 1 : -1));
                if (offset > player.matrix[0].length) {
                // 회전 복구
                rotate(player.matrix);
                player.pos.x = pos;
                return;
                }
            }
        }

        function draw() {
            context.fillStyle = '#000';
            context.fillRect(0, 0, canvas.width, canvas.height);
            drawMatrix(arena, {x: 0, y: 0}, context);
            drawGhost();
            if (player.matrix) {
                drawMatrix(player.matrix, player.pos, context);
            }
        }

        let dropCounter = 0;
        let dropInterval = 1000;
        let lastTime = 0;
        let lastHardDropTime = 0;
        let animationFrameId = 0;
        let gameOver = false;

        function update(time = 0) {
            if (gameOver) return;
            const deltaTime = time - lastTime;
            lastTime = time;
            dropCounter += deltaTime;
            if (dropCounter > dropInterval) {
                playerDrop();
            }
            draw();
            animationFrameId = requestAnimationFrame(update);
        }

        function updateScore() {
            document.getElementById('score').textContent = '🎯점수: ' + player.score;
            document.getElementById('lines').textContent = '🧹없앤 줄: ' + player.linesCleared;
            const instantSpeedText = player.hardDropInterval > 0
                ? (60 / (player.hardDropInterval / 1000)).toFixed(1) + '회/분'
                : '0.0회/분';
            const maxSpeedText = player.maxHardDropSpeed > 0
                ? player.maxHardDropSpeed.toFixed(1) + '회/분'
                : '0.0회/분';
            const avgSpeedText = player.hardDropCount > 0 && player.totalHardDropTime > 0
                ? (60 / ((player.totalHardDropTime / player.hardDropCount) / 1000)).toFixed(1) + '회/분'
                : '0.0회/분';
            document.getElementById('speed').textContent = '⚡순간속도: ' + instantSpeedText;
            document.getElementById('max-speed').textContent = '🏆최대 순간속도: ' + maxSpeedText;
            document.getElementById('avg-speed').textContent = '📈평균속도: ' + avgSpeedText;
            document.getElementById('max-speed').classList.toggle('highlight-max-speed', player.maxHardDropSpeed > 100);
            document.getElementById('score').classList.toggle('highlight-score', player.score > 1000);
            document.getElementById('game-over').style.display = gameOver ? 'block' : 'none';
            if (player.score >= 500) dropInterval = 800;
            if (player.score >= 1000) dropInterval = 600;
            if (player.score >= 2000) dropInterval = 400;
            if (player.score >= 3000) dropInterval = 300;
            if (player.score >= 5000) dropInterval = 200;
        }

        document.addEventListener('keydown', event => {
        if (gameOver) return;
        if (event.key === 'ArrowLeft') {
            playerMove(-1);
        } else if (event.key === 'ArrowRight') {
            playerMove(1);
        } else if (event.key === 'ArrowDown') {
            playerDrop();
        } else if (event.key === 'ArrowUp') {
            playerRotate();
        } else if (event.code === 'Space') {
            playerHardDrop();
        }
        });

        playerReset();
        updateScore();
        update();
    </script>
</body>
</html>