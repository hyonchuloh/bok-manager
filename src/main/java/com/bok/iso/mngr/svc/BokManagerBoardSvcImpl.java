package com.bok.iso.mngr.svc;

import java.util.List;

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
    public int updateItem(BokManagerBoardDto input) {
        if ( input.getSeq() == 0 ) {
            return dao.insertItem(input); // 신규 게시글인 경우, insert를 수행한다.
        } else {
            return dao.updateItem(input); // 기존 게시글인 경우, update를 수행한다.
        }
    }

    @Override
    public BokManagerBoardDto selectItem(int seq) {
        BokManagerBoardDto selectResult = dao.selectItem(seq);
        if ( selectResult == null ) {
            // If the item does not exist, create a new one with default values
            BokManagerBoardDto newItem = new BokManagerBoardDto();
            newItem.setSeq(seq);
            newItem.setContents("");
            dao.insertItem(newItem);
        }
        return dao.selectItem(seq);
    }

    @Override
    public List<BokManagerBoardDto> getListItems() {
        return dao.getListItems();
    }

    @Override
    public BokManagerBoardDto getLatestItem() {
        return dao.getLatestItem();
    }
}
