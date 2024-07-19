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
    public List<BokManagerCallbookDto> selectItems() {
        return dao.selectItems();
    }

}
