package com.bok.iso.mngr.svc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bok.iso.mngr.dao.BokManagerBoardDao;
import com.bok.iso.mngr.dao.dto.BokManagerBoardDto;
import java.util.List;

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
    public int deleteItem(int seq) {
        return dao.deleteItem(seq);
    }

    @Override
    public int updateItem(BokManagerBoardDto input) {
        return dao.updateItem(input);
    }

    @Override
    public List<BokManagerBoardDto> selectList() {
        return dao.selectList();
    }

    @Override
    public BokManagerBoardDto selectItem(int seq) {
        return dao.selectItem(seq);
    }
}
