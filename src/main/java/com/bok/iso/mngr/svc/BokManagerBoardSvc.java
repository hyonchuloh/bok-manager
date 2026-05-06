package com.bok.iso.mngr.svc;

import java.util.List;

import com.bok.iso.mngr.dao.dto.BokManagerBoardDto;

public interface BokManagerBoardSvc {

    void initTable();

    int updateItem(BokManagerBoardDto input);

    BokManagerBoardDto selectItem(int seq);

    List<BokManagerBoardDto> getListItems();

    BokManagerBoardDto getLatestItem();
}
