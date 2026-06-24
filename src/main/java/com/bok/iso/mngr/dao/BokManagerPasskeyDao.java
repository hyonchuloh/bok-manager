package com.bok.iso.mngr.dao;

import java.util.List;

import com.bok.iso.mngr.dao.dto.BokManagerPasskeyDto;

public interface BokManagerPasskeyDao {

    public void initTable();
    public int insertPasskey(BokManagerPasskeyDto passkey);
    public BokManagerPasskeyDto selectByCredentialId(String credentialId);
    public List<BokManagerPasskeyDto> selectByUserId(String userId);
}
