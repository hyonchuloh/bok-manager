package com.ohc.bok.mngr.svc;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ohc.bok.mngr.dao.BokManagerCallbookDao;
import com.ohc.bok.mngr.dao.dto.BokManagerCallbookDto;

@Service
public class BokManagerCallbookSvcImpl implements BokManagerCallbookSvc {

    private final BokManagerCallbookDao dao;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    BokManagerCallbookSvcImpl(BokManagerCallbookDao dao) {
        this.dao = dao;
    }

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
    public List<BokManagerCallbookDto> selectItems(String searchKey) {
        List<BokManagerCallbookDto> retValue = new ArrayList<BokManagerCallbookDto>();
        for ( BokManagerCallbookDto dto : dao.selectItems() ) {
            if ( dto.getExtName().contains(searchKey) || dto.getBizName().contains(searchKey) || dto.getCall().contains(searchKey) || dto.getDepName().contains(searchKey) ||
                 dto.getEmail().contains(searchKey) || dto.getName().contains(searchKey) || dto.getExt().contains(searchKey) ) {
                retValue.add(dto);
            }
        }
        return retValue;
    }
    @Override
    public int updateItem(BokManagerCallbookDto dto) {
        return dao.updateItem(dto);
    }
    @Override
    public int bulkInsertFromExcel(MultipartFile file) {
        int insertedCount = 0;
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 없습니다.");
        }

        DataFormatter formatter = new DataFormatter();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                throw new IllegalArgumentException("엑셀 시트가 없습니다.");
            }

            // 첫 행이 헤더라 가정 -> 두번째 행부터 데이터 (rowIndex 1)
            for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                // 예: 컬럼 순서: extName, depName, bizName, name, call, email, ext
                String extName = formatter.formatCellValue(row.getCell(0)).trim();
                String depName = formatter.formatCellValue(row.getCell(1)).trim();
                String bizName = formatter.formatCellValue(row.getCell(2)).trim();
                String personName = formatter.formatCellValue(row.getCell(3)).trim();
                String call = formatter.formatCellValue(row.getCell(4)).trim();
                String email = formatter.formatCellValue(row.getCell(5)).trim();
                String ext = formatter.formatCellValue(row.getCell(6)).trim();

                // 빈 행은 건너뜀
                if (extName.isEmpty() && depName.isEmpty() && bizName.isEmpty() && personName.isEmpty()) {
                    continue;
                }

                // seq는 insert 시 자동 생성하므로 0 또는 -1 사용
                BokManagerCallbookDto dto = new BokManagerCallbookDto(0, extName, depName, bizName, personName, call, email, ext);

                try {
                    int result = insertItem(dto); // 기존 insertItem 재사용
                    if (result > 0) insertedCount++;
                } catch (Exception e) {
                    logger.warn("Row {} insert failed: {}", r+1, e.getMessage());
                    // 실패한 행은 로깅만 하고 계속 진행
                }
            }
        } catch (Exception e) {
            logger.error("bulkInsertFromExcel error", e);
            throw new RuntimeException("엑셀 처리 중 오류: " + e.getMessage(), e);
        }
        logger.info("bulk insert finished, insertedCount={}", insertedCount);
        return insertedCount;
    }

    @Override
    public byte[] exportToExcel(List<BokManagerCallbookDto> list) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("연락처");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            String[] headers = { "no", "기관명", "부서명", "담당업무", "이름", "연락처", "이메일", "업무이력" };
            Row headerRow = sheet.createRow(0);
            for (int c = 0; c < headers.length; c++) {
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(headers[c]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (BokManagerCallbookDto dto : list) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getSeq());
                row.createCell(1).setCellValue(dto.getExtName());
                row.createCell(2).setCellValue(dto.getDepName());
                row.createCell(3).setCellValue(dto.getBizName());
                row.createCell(4).setCellValue(dto.getName());
                row.createCell(5).setCellValue(dto.getCall());
                row.createCell(6).setCellValue(dto.getEmail());
                row.createCell(7).setCellValue(dto.getExt());
            }

            for (int c = 0; c < headers.length; c++) {
                sheet.autoSizeColumn(c);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            logger.error("exportToExcel error", e);
            throw new RuntimeException("엑셀 생성 중 오류: " + e.getMessage(), e);
        }
    }

}
