package com.bok.iso.mngr.dao;


import com.bok.iso.mngr.dao.dto.BokManagerBoardDto;

public interface BokManagerBoardDao {

    public void initTable();
    public int insertItem(BokManagerBoardDto input);
    public int updateItem(BokManagerBoardDto input);
    public BokManagerBoardDto selectItem(int seq);

}
