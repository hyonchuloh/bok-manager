package com.bok.iso.mngr.svc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bok.iso.mngr.dao.BokManagerBoardDao;
import com.bok.iso.mngr.dao.dto.BokManagerBoardDto;

@Service
public class BokManagerBoardSvcImpl implements BokManagerBoardSvc {

    @Autowired
    private BokManagerBoardDao dao;

    @Override
    public void initTable() {
        dao.initTable();
    }

    @Override
    public int insertItem(BokManagerBoardDto input) {
        return dao.insertItem(input);
    }

    @Override
    public int updateItem(BokManagerBoardDto input) {
        return dao.updateItem(input);
    }

    @Override
    public BokManagerBoardDto selectItem(int seq) {
        BokManagerBoardDto selectResult = dao.selectItem(seq);
        if ( selectResult == null ) {
            // If the item does not exist, create a new one with default values
            BokManagerBoardDto newItem = new BokManagerBoardDto();
            newItem.setCategoryIndex(seq);
            newItem.setContents("");
            dao.insertItem(newItem);
        }
        return dao.selectItem(seq);
    }
}
