package com.bok.iso.mngr.dao.dto;

public class BokManagerBoardDto {

    private int seq;
    private String title;
    private int categoryIndex; // default=1
    private String contents;
    private String createUserId;
    private int attachIndex; // default=0
    private long updateTime;
    private long createTime;
    private boolean deleteYn;
    public int getSeq() {
        return seq;
    }
    public String getTitle() {
        return title;
    }
    public int getCategoryIndex() {
        return categoryIndex;
    }
    public String getContents() {
        return contents;
    }
    public String getCreateUserId() {
        return createUserId;
    }
    public int getAttachIndex() {
        return attachIndex;
    }
    public long getUpdateTime() {
        return updateTime;
    }
    public long getCreateTime() {
        return createTime;
    }
    public void setSeq(int seq) {
        this.seq = seq;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setCategoryIndex(int categoryIndex) {
        this.categoryIndex = categoryIndex;
    }
    public void setContents(String contents) {
        this.contents = contents;
    }
    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }
    public void setAttachIndex(int attachIndex) {
        this.attachIndex = attachIndex;
    }
    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    public boolean isDeleteYn() {
        return deleteYn;
    }
    public void setDeleteYn(boolean deelteYn) {
        this.deleteYn = deelteYn;
    }

    
}
