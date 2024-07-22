package com.bok.iso.mngr.dao.dto;

public class BokManagerCallbookDto {

    private int seq;
    private String extName; 
    private String depName; 
    private String bizName;
    private String name;
    private String call;
    private String email;
    private String ext;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getExtName() {
        return extName;
    }

    public String getDepName() {
        return depName;
    }

    public String getBizName() {
        return bizName;
    }

    public String getName() {
        return name;
    }

    public String getCall() {
        return call;
    }

    public String getEmail() {
        return email;
    }

    public String getExt() {
        return ext;
    }

    public void setExtName(String extName) {
        this.extName = extName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    

}
