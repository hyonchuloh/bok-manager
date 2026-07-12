package com.ohc.bok.mngr.svc;

import java.util.List;

import com.ohc.bok.mngr.dao.dto.BokManagerBoardDto;

public interface BokManagerBoardSvc {

    void initTable();

    int updateItem(BokManagerBoardDto input);

    BokManagerBoardDto selectItem(int seq);

    List<BokManagerBoardDto> getListItems();

    BokManagerBoardDto getLatestItem();

    int deleteItem(int seq);
}
