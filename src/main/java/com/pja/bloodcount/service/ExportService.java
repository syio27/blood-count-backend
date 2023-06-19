package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.response.GameToExport;
import com.pja.bloodcount.model.Game;
import com.pja.bloodcount.model.enums.Status;
import com.pja.bloodcount.repository.GameCaseDetailsRepository;
import com.pja.bloodcount.repository.GameRepository;
import com.pja.bloodcount.repository.PatientRepository;
import com.pja.bloodcount.repository.UserRepository;
import com.pja.bloodcount.validation.UserValidator;
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
public class ExportService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final PatientRepository patientRepository;
    private final GameCaseDetailsRepository caseDetailsRepository;
    private final UserValidator userValidator;

    public byte[] exportData() throws IOException {

        List<Game> allGames = gameRepository.findAll();
        List<Game> completedGames = allGames.stream().filter(game -> game.getStatus().equals(Status.COMPLETED)).toList();

        List<GameToExport> gamesToExport = new ArrayList<>();
        completedGames.forEach(game -> {
            GameToExport gameToExport = GameToExport
                    .builder()
                    .id(game.getId())
                    .userEmail(game.getUser().getEmail())
                    .userGroup(game.getUser().getGroup().getGroupNumber())
                    .startTime(game.getStartTime())
                    .endTime(game.getEndTime())
                    .estimatedEndTime(game.getEstimatedEndTime())
                    .status(game.getStatus())
                    .score(game.getScore())
                    .testDuration(game.getTestDuration())
                    .patientInfo("Gender: " + game.getPatient().getGender() + ", age: " + game.getPatient().getAge())
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
        headerRow.createCell(1).setCellValue("USER EMAIL");
        headerRow.createCell(2).setCellValue("USER GROUP");
        headerRow.createCell(3).setCellValue("START_TIME");
        headerRow.createCell(4).setCellValue("END_TIME");
        headerRow.createCell(5).setCellValue("ESTIMATED_END_TIME");
        headerRow.createCell(6).setCellValue("STATUS");
        headerRow.createCell(7).setCellValue("SCORE");
        headerRow.createCell(8).setCellValue("TEST_DURATION");
        headerRow.createCell(9).setCellValue("PATIENT_INFO");
        headerRow.createCell(10).setCellValue("CASE_DETAILS");

        for (int i = 0; i < gamesToExport.size(); i++) {
            GameToExport gameToExport = gamesToExport.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(gameToExport.getId());
            row.createCell(1).setCellValue(gameToExport.getUserEmail());
            row.createCell(2).setCellValue(gameToExport.getUserGroup());
            row.createCell(3).setCellValue(gameToExport.getStartTime() != null ? gameToExport.getStartTime().toString() : null);
            row.createCell(4).setCellValue(gameToExport.getEndTime() != null ? gameToExport.getEndTime().toString() : null);
            row.createCell(5).setCellValue(gameToExport.getEstimatedEndTime() != null ? gameToExport.getEstimatedEndTime().toString() : null);
            row.createCell(6).setCellValue(gameToExport.getStatus().name());
            row.createCell(7).setCellValue(gameToExport.getScore());
            row.createCell(8).setCellValue(gameToExport.getTestDuration());
            row.createCell(9).setCellValue(gameToExport.getPatientInfo());
            row.createCell(10).setCellValue(gameToExport.getCaseInfo());
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        byte[] bytes = bos.toByteArray();
        bos.close();
        workbook.close();
        return bytes;
    }
}
