package com.bok.iso.mngr.svc;

import java.util.List;

import com.bok.iso.mngr.dao.dto.BokManagerCallbookDto;

public interface BokManagerCallbookSvc {

    public List<BokManagerCallbookDto> selectItems();

}