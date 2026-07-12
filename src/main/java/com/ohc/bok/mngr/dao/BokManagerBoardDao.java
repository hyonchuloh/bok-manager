package com.ohc.bok.mngr.dao;


import java.util.List;

import com.ohc.bok.mngr.dao.dto.BokManagerBoardDto;

public interface BokManagerBoardDao {

    public void initTable();
    public int insertItem(BokManagerBoardDto input);
    public int updateItem(BokManagerBoardDto input);
    public BokManagerBoardDto selectItem(int seq);
    public List<BokManagerBoardDto> getListItems();
    public BokManagerBoardDto getLatestItem();
    public int deleteItem(int seq);

}
