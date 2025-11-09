package com.bok.iso.mngr.svc;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bok.iso.mngr.dao.dto.BokManagerCallbookDto;

public interface BokManagerCallbookSvc {

    public void initTable();
    public List<BokManagerCallbookDto> selectItems();
    public List<BokManagerCallbookDto> selectItems(String searchKey);
    public BokManagerCallbookDto selectItem(int seq);
    public int insertItem(BokManagerCallbookDto dto);
    public int updateItem(BokManagerCallbookDto dto);
    public int deleteItem(int seq);
    /**
     * 업로드된 엑셀 파일을 파싱하여 DB에 벌크로 저장한다.
     * 반환값: 저장된 레코드 수
     */
    int bulkInsertFromExcel(MultipartFile file);
}