package com.bok.iso.mngr.dao;

import java.util.List;

import com.bok.iso.mngr.dao.dto.BokManagerCallbookDto;

public interface BokManagerCallbookDao {

    public List<BokManagerCallbookDto> selectItems();

}
