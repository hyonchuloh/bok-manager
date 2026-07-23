package com.ohc.bok.mngr.svc;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ohc.bok.mngr.dao.dto.BokManagerCallbookDto;

public interface BokManagerCallbookSvc {

    /**
     * 핵심 기능: 전화번호부 테이블 초기화(앱 기동 시 1회).
     * 호출 URI: 없음 (BokManagerApplication 기동 시 @PostConstruct에서 호출)
     */
    public void initTable();

    /**
     * 핵심 기능: 전화번호부 전체 목록 조회.
     * 호출 URI: GET /manager/callbook (searchKey 미지정 시)
     */
    public List<BokManagerCallbookDto> selectItems();

    /**
     * 핵심 기능: 검색어로 전화번호부 목록 조회.
     * 호출 URI: GET /manager/callbook (searchKey 지정 시)
     */
    public List<BokManagerCallbookDto> selectItems(String searchKey);

    /**
     * 핵심 기능: 전화번호부 단건 조회.
     * 호출 URI: 없음 (현재 컨트롤러에서 호출되지 않음)
     */
    public BokManagerCallbookDto selectItem(int seq);

    /**
     * 핵심 기능: 전화번호부 신규 등록.
     * 호출 URI: POST /manager/callbook-save (seq가 0 이하인 경우)
     */
    public int insertItem(BokManagerCallbookDto dto);

    /**
     * 핵심 기능: 전화번호부 수정.
     * 호출 URI: POST /manager/callbook-save (seq가 지정된 경우)
     */
    public int updateItem(BokManagerCallbookDto dto);

    /**
     * 핵심 기능: 전화번호부 삭제.
     * 호출 URI: POST /manager/callbook-delete
     */
    public int deleteItem(int seq);

    /**
     * 핵심 기능: 업로드된 엑셀 파일을 파싱하여 DB에 벌크로 저장한다.
     * 반환값: 저장된 레코드 수
     * 호출 URI: POST /manager/callbook/upload
     */
    int bulkInsertFromExcel(MultipartFile file);

    /**
     * 핵심 기능: 전화번호부 목록을 엑셀 파일(바이트 배열)로 변환한다.
     * 호출 URI: GET /manager/callbook/download
     */
    byte[] exportToExcel(List<BokManagerCallbookDto> list);
}
