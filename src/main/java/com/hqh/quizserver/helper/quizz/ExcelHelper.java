package com.hqh.quizserver.helper.quizz;

import com.hqh.quizserver.domain.Question;
import com.hqh.quizserver.domain.TestQuizz;
import com.hqh.quizserver.domain.UserMark;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.hqh.quizserver.constant.FileConstant.*;

public class ExcelHelper {

    public static final String SHEET_QUIZZES = "Quizzes";

    /***
     * export file excel quiz
     *
     * @param quizz
     * @return
     */
    public static ByteArrayInputStream quizzesToExcel(TestQuizz quizz) {

        try(XSSFWorkbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet(SHEET_QUIZZES);
            XSSFRow headerRow = sheet.createRow(0);


            for (int i = 0; i < HEADER_QUIZZ.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADER_QUIZZ[i]);
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
                row.createCell(8).setCellValue(question.getMark());
            }

            workbook.write(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException exception) {
            throw new RuntimeException(FAIL_TO_IMPORT_DATA_TO_EXCEL_FILE + exception.getMessage());
        }

    }

    public static boolean hasExcelFormat(MultipartFile multipartFile) {
        return TYPE.equals(multipartFile.getContentType());
    }

    /***
     *
     * @param inputStream
     * @param quizzId
     * @return questions
     */
    public static List<Question> importFromExcel(InputStream inputStream, TestQuizz quizzId) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(SHEET_QUIZZES);
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
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 0:
                            question.setTopicQuestion(currentCell.getStringCellValue());
                            break;
                        case 1:
                            question.setAnswerA(currentCell.getStringCellValue());
                            break;
                        case 2:
                            question.setAnswerB(currentCell.getStringCellValue());
                            break;
                        case 3:
                            question.setAnswerC(currentCell.getStringCellValue());
                            break;
                        case 4:
                            question.setAnswerD(currentCell.getStringCellValue());
                            break;
                        case 5:
                            question.setCorrectResult(currentCell.getStringCellValue());
                            break;
                        case 6:
                            question.setMark((float) currentCell.getNumericCellValue());
                            break;
                        case 7:
                            question.setType(currentCell.getStringCellValue());
                            break;
                        case 8:
                            question.setCorrectEssay(currentCell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                question.setTestQuizz(quizzId);
                question.setMilestones(1);
                question.setDateCreated(Instant.now());
                questions.add(question);
            }
            workbook.close();

            return questions;
        } catch (Exception exception) {
            throw new RuntimeException(FAIL_TO_PARSE_EXCEL_FILE + exception.getMessage());
        }
    }

    /***
     *
     * @param userMarks
     * @return
     */
    public static ByteArrayInputStream userMarkToExcel(List<UserMark> userMarks) {
        try(XSSFWorkbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("Mark");
            XSSFRow headerRow = sheet.createRow(0);

            for (int i = 0; i < HEADER_MARK.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADER_MARK[i]);
            }

            int rowIdx = 1;
            for (UserMark mark : userMarks) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(1).setCellValue(mark.getId());
                String fullName = mark.getUser().getFirstName() + " " + mark.getUser().getLastName();
                row.createCell(2).setCellValue(fullName);
                row.createCell(3).setCellValue(mark.getUser().getUsername());
                row.createCell(4).setCellValue(mark.getMark());
                row.createCell(5).setCellValue(mark.getTestQuizz().getTestName());
                row.createCell(6).setCellValue(mark.getCompletedDate().toString().substring(0, 10));
            }

            workbook.write(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException exception) {
            throw new RuntimeException(FAIL_TO_IMPORT_DATA_TO_EXCEL_FILE + exception.getMessage());
        }
    }

}
