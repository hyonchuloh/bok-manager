package com.bok.iso.mngr.svc;

import java.util.List;

import com.bok.iso.mngr.dao.dto.BokManagerCallbookDto;

public interface BokManagerCallbookSvc {

    public void initTable();
    public List<BokManagerCallbookDto> selectItems();
    public List<BokManagerCallbookDto> selectItems(String searchKey);
    public BokManagerCallbookDto selectItem(int seq);
    public int insertItem(BokManagerCallbookDto dto);
    public int updateItem(BokManagerCallbookDto dto);
    public int deleteItem(int seq);

}