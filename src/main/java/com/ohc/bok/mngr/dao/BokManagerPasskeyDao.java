package com.ohc.bok.mngr.dao;

import java.util.List;

import com.ohc.bok.mngr.dao.dto.BokManagerPasskeyDto;

public interface BokManagerPasskeyDao {

    public void initTable();
    public int insertPasskey(BokManagerPasskeyDto passkey);
    public BokManagerPasskeyDto selectByCredentialId(String credentialId);
    public List<BokManagerPasskeyDto> selectByUserId(String userId);
    public List<BokManagerPasskeyDto> selectAllPasskeys();
    public int deleteByCredentialId(String credentialId);
}
