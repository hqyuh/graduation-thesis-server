package com.hqh.quizserver.helper.quizz;

import com.hqh.quizserver.entity.Question;
import com.hqh.quizserver.entity.TestQuizz;
import com.hqh.quizserver.entity.UserMark;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.hqh.quizserver.constant.FileConstant.*;

public class ExcelHelper {

    public static final int COLUMN_INDEX_QUESTION = 0;
    public static final int COLUMN_INDEX_ANSWER_A = 1;
    public static final int COLUMN_INDEX_ANSWER_B = 2;
    public static final int COLUMN_INDEX_ANSWER_C = 3;
    public static final int COLUMN_INDEX_ANSWER_D = 4;
    public static final int COLUMN_INDEX_CORRECT_RESULT = 5;
    public static final int COLUMN_INDEX_CORRECT_ESSAY = 6;
    public static final int COLUMN_INDEX_MARK = 7;
    public static final int COLUMN_INDEX_TYPE = 8;

    public static ByteArrayInputStream quizzesToExcel(TestQuizz quizz) {

        try(XSSFWorkbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet(quizz.getTestName().toUpperCase());
            XSSFRow headerRow = sheet.createRow(0);

            // set font
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 12);
            font.setColor(IndexedColors.BLACK.index);

            XSSFCellStyle style = workbook.createCellStyle();
            style.setFont(font);

            for (int i = 0; i < HEADER_QUIZZ.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(HEADER_QUIZZ[i]);
                cell.setCellStyle(style);
            }

            int rowIdx = 1;
            for (int i = 0; i < quizz.getQuestions().size(); i++) {
                Row row = sheet.createRow(rowIdx++);
                Question question = quizz.getQuestions().get(i);
                row.createCell(1).setCellValue(question.getId());
                row.createCell(2).setCellValue(question.getTopicQuestion());
                row.createCell(3).setCellValue(question.getAnswerA());
                row.createCell(4).setCellValue(question.getAnswerB());
                row.createCell(5).setCellValue(question.getAnswerC());
                row.createCell(6).setCellValue(question.getAnswerD());
                row.createCell(7).setCellValue(question.getCorrectResult());
                row.createCell(8).setCellValue(question.getCorrectEssay());
                row.createCell(9).setCellValue(question.getMark());
                row.createCell(10).setCellValue(question.getType());
            }
            workbook.write(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException exception) {
            throw new RuntimeException("Fail to import data to Excel file: " + exception.getMessage());
        }

    }

    public static boolean hasExcelFormat(MultipartFile multipartFile) {
        return TYPE.equals(multipartFile.getContentType());
    }

    private static Object getCellValue(XSSFCell cell) {
        CellType cellType = cell.getCellType();
        Object cellValue = null;

        switch (cellType) {
            case BOOLEAN:
                cellValue = cell.getBooleanCellValue();
                break;
            case NUMERIC:
                cellValue = cell.getNumericCellValue();
                break;
            case STRING:
                cellValue = cell.getStringCellValue().isBlank() ? null : cell.getStringCellValue(); // temp
                break;
            case _NONE:
            case BLANK:
            case ERROR:
            default:
                break;
        }
        return cellValue;
    }

    public static List<Question> importFromExcel(InputStream inputStream, TestQuizz testQuizz, String username) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(workbook.getSheetName(0));
            Iterator<Row> rows = sheet.iterator();
            List<Question> questions = new ArrayList<>();

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if (currentRow.getRowNum() == 0) {
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();
                Question question = new Question();
                int cellIdx = 0;

                while (cellsInRow.hasNext()) {
                    XSSFCell currentCell = (XSSFCell) cellsInRow.next();

                    switch (cellIdx) {
                        case COLUMN_INDEX_QUESTION:
                            question.setTopicQuestion((String) getCellValue(currentCell));
                            break;
                        case COLUMN_INDEX_ANSWER_A:
                            question.setAnswerA((String) getCellValue(currentCell));
                            break;
                        case COLUMN_INDEX_ANSWER_B:
                            question.setAnswerB((String) getCellValue(currentCell));
                            break;
                        case COLUMN_INDEX_ANSWER_C:
                            question.setAnswerC((String) getCellValue(currentCell));
                            break;
                        case COLUMN_INDEX_ANSWER_D:
                            question.setAnswerD((String) getCellValue(currentCell));
                            break;
                        case COLUMN_INDEX_CORRECT_RESULT:
                            question.setCorrectResult((String) getCellValue(currentCell));
                            break;
                        case COLUMN_INDEX_CORRECT_ESSAY:
                            question.setCorrectEssay((String) getCellValue(currentCell));
                            break;
                        case COLUMN_INDEX_MARK:
                            question.setMark((Double) getCellValue(currentCell));
                            break;
                        case COLUMN_INDEX_TYPE:
                            question.setType((String) getCellValue(currentCell));
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                question.setTestQuizz(testQuizz);
                question.setMilestones(1);
                question.setDateCreated(Instant.now());
                question.setCreatedAt(new Date());
                question.setCreatedBy(username);
                question.setUpdatedAt(new Date());
                question.setUpdatedBy(username);
                questions.add(question);
            }
            workbook.close();

            return questions;
        } catch (Exception exception) {
            throw new RuntimeException("Fail to parse Excel file: " + exception.getMessage());
        }
    }

    public static ByteArrayInputStream userMarkToExcel(List<UserMark> userMarks) {
        try(XSSFWorkbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("Mark");
            XSSFRow headerRow = sheet.createRow(0);

            for (int i = 0; i < HEADER_MARK.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADER_MARK[i]);
            }

            String fullName = userMarks.get(0).getUser().getFullName();
            int rowIdx = 1;
            for (UserMark mark : userMarks) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(1).setCellValue(mark.getId());
                row.createCell(2).setCellValue(fullName);
                row.createCell(3).setCellValue(mark.getUser().getUsername());
                row.createCell(4).setCellValue(mark.getMark());
                row.createCell(5).setCellValue(mark.getTestQuizz().getTestName());
                row.createCell(6).setCellValue(mark.getCompletedDate().toString().substring(0, 10));
            }

            workbook.write(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException exception) {
            throw new RuntimeException("Fail to import data to Excel file: " + exception.getMessage());
        }
    }

}
