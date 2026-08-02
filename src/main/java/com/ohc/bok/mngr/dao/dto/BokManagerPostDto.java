package com.ohc.bok.mngr.dao.dto;

public class BokManagerPostDto {

    private int seq;
    private Integer parentSeq;
    private String userId;
    private String contents;
    private java.time.LocalDateTime createdAt;
    /* 비로그인(익명) 작성 시에만 채워지는 게시물별 비밀번호 해시. 로그인 세션으로 작성한 경우 null. */
    private String password;
    private int likeCount;
    /* 작성 시 "info" 체크박스를 선택했는지 여부 */
    private boolean infoFlag;

    /* 조회 시에만 채워지는 부가 정보(DB 컬럼 아님) */
    private int depth;
    private int replyCount;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public Integer getParentSeq() {
        return parentSeq;
    }

    public void setParentSeq(Integer parentSeq) {
        this.parentSeq = parentSeq;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAtFormatted() {
        return createdAt == null ? null
                : createdAt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isInfoFlag() {
        return infoFlag;
    }

    public void setInfoFlag(boolean infoFlag) {
        this.infoFlag = infoFlag;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }
}
