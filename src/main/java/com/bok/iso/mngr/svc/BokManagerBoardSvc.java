package com.bok.iso.mngr.svc;

import com.bok.iso.mngr.dao.dto.BokManagerBoardDto;
import java.util.List;

public interface BokManagerBoardSvc {

    void initTable();

    int insertItem(BokManagerBoardDto input);

    int deleteItem(int seq);

    int updateItem(BokManagerBoardDto input);

    List<BokManagerBoardDto> selectList();

    BokManagerBoardDto selectItem(int seq);
}
