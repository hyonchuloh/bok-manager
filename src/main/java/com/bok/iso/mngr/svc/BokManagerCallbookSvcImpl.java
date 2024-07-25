package com.bok.iso.mngr.svc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bok.iso.mngr.dao.BokManagerCallbookDao;
import com.bok.iso.mngr.dao.dto.BokManagerCallbookDto;

@Service
public class BokManagerCallbookSvcImpl implements BokManagerCallbookSvc {

    @Autowired
    private BokManagerCallbookDao dao;

    @Override
    public int deleteItem(int seq) {
        return dao.deleteItem(seq);
    }
    @Override
    public void initTable() {
        dao.initTable();
    }
    @Override
    public int insertItem(BokManagerCallbookDto dto) {
        return dao.insertItem(dto);
    }
    @Override
    public BokManagerCallbookDto selectItem(int seq) {
        return dao.selectItem(seq);
    }
    @Override
    public List<BokManagerCallbookDto> selectItems() {
        return dao.selectItems();
    }
    @Override
    public int updateItem(BokManagerCallbookDto dto) {
        return dao.updateItem(dto);
    }

}
