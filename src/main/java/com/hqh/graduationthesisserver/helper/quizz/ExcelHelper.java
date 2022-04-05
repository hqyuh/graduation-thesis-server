package com.hqh.graduationthesisserver.helper.quizz;

import com.hqh.graduationthesisserver.domain.Question;
import com.hqh.graduationthesisserver.domain.TestQuizz;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.hqh.graduationthesisserver.constant.FileConstant.*;

public class ExcelHelper {

    public static ByteArrayInputStream quizzesToExcel(TestQuizz quizz) {

        try(XSSFWorkbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("Quizzes");
            XSSFRow headerRow = sheet.createRow(0);


            for (int i = 0; i < HEADER.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADER[i]);
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

}
