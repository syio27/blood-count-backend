package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.response.GameToExport;
import com.pja.bloodcount.model.Game;
import com.pja.bloodcount.repository.GameRepository;
import com.pja.bloodcount.service.contract.ExportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ExportServiceImpl implements ExportService {
    private final GameRepository gameRepository;

    @Override
    public byte[] exportData() throws IOException {

        List<Game> allGames = gameRepository.findAll();
        List<Game> completedGames = allGames.stream()
                .filter(game -> game.isCompleted() && game.getUser().isStudent())
                .toList();

        List<GameToExport> gamesToExport = new ArrayList<>();
        completedGames.forEach(game -> {
            GameToExport gameToExport = GameToExport
                    .builder()
                    .id(game.getId())
                    .language(game.getLanguage())
                    .userEmail(game.getUser().getEmail())
                    .userGroup(game.getUser().getGroup().getGroupNumber())
                    .startTime(game.getStartTime())
                    .endTime(game.getEndTime())
                    .status(game.getStatus())
                    .score(game.getScore())
                    .testDuration(game.getTestDuration())
                    .patientInfo("Gender: " + game.getPatient().getGender() + ", age: " + game.getPatient().getAge())
                    .playedCaseId("Case number: " + game.getCaseDetails().getAnActualCaseId())
                    .caseInfo("Anemia type: " + game.getCaseDetails().getAnemiaType() + ", diagnosis: " + game.getCaseDetails().getDiagnosis())
                    .build();

            gamesToExport.add(gameToExport);
        });

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Games Sheet");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("GAME_ID");
        headerRow.createCell(1).setCellValue("LANGUAGE");
        headerRow.createCell(2).setCellValue("USER EMAIL");
        headerRow.createCell(3).setCellValue("USER GROUP");
        headerRow.createCell(4).setCellValue("START_TIME");
        headerRow.createCell(5).setCellValue("END_TIME");
        headerRow.createCell(6).setCellValue("ESTIMATED_END_TIME");
        headerRow.createCell(7).setCellValue("STATUS");
        headerRow.createCell(8).setCellValue("SCORE");
        headerRow.createCell(9).setCellValue("TEST_DURATION");
        headerRow.createCell(10).setCellValue("PATIENT_INFO");
        headerRow.createCell(11).setCellValue("PLAYED_CASE_NUMBER");
        headerRow.createCell(12).setCellValue("CASE_DETAILS");

        for (int i = 0; i < gamesToExport.size(); i++) {
            GameToExport gameToExport = gamesToExport.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(gameToExport.getId());
            row.createCell(1).setCellValue(gameToExport.getLanguage().name());
            row.createCell(2).setCellValue(gameToExport.getUserEmail());
            row.createCell(3).setCellValue(gameToExport.getUserGroup());
            row.createCell(4).setCellValue(gameToExport.getStartTime() != null ? gameToExport.getStartTime().toString() : null);
            row.createCell(5).setCellValue(gameToExport.getEndTime() != null ? gameToExport.getEndTime().toString() : null);
            row.createCell(6).setCellValue(gameToExport.getEstimatedEndTime() != null ? gameToExport.getEstimatedEndTime().toString() : null);
            row.createCell(7).setCellValue(gameToExport.getStatus().name());
            row.createCell(8).setCellValue(gameToExport.getScore());
            row.createCell(9).setCellValue(gameToExport.getTestDuration());
            row.createCell(10).setCellValue(gameToExport.getPatientInfo());
            row.createCell(11).setCellValue(gameToExport.getPlayedCaseId());
            row.createCell(12).setCellValue(gameToExport.getCaseInfo());
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        byte[] bytes = bos.toByteArray();
        bos.close();
        workbook.close();
        return bytes;
    }
}
