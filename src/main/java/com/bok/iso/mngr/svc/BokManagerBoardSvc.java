package com.bok.iso.mngr.svc;

import com.bok.iso.mngr.dao.dto.BokManagerBoardDto;

public interface BokManagerBoardSvc {

    void initTable();

    int insertItem(BokManagerBoardDto input);

    int updateItem(BokManagerBoardDto input);

    BokManagerBoardDto selectItem(int seq);
}
