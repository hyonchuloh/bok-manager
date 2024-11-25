package com.bok.iso.mngr.dao;

import java.util.List;

import com.bok.iso.mngr.dao.dto.BokManagerBoardDto;

public interface BokManagerBoardDao {

    public void initTable();
    public int insertItem(BokManagerBoardDto input);
    public int deleteItem(int seq);
    public int updateItem(BokManagerBoardDto input);
    public List<BokManagerBoardDto> selectList();
    public BokManagerBoardDto selectItem(int seq);

}
