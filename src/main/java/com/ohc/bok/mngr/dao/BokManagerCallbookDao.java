package com.ohc.bok.mngr.dao;

import java.util.List;

import com.ohc.bok.mngr.dao.dto.BokManagerCallbookDto;

public interface BokManagerCallbookDao {

    public void initTable();
    public List<BokManagerCallbookDto> selectItems();
    public BokManagerCallbookDto selectItem(int seq);
    public int insertItem(BokManagerCallbookDto dto);
    public int updateItem(BokManagerCallbookDto dto);
    public int deleteItem(int seq);

}
