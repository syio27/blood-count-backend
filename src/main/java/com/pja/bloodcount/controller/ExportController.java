package com.pja.bloodcount.controller;

import com.pja.bloodcount.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
public class ExportController {

    private final ExportService service;

    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROOT') or hasRole('SUPERVISOR')")
    public ResponseEntity<byte[]> exportGameStats() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename("ExcelFile.xlsx").build());
        return new ResponseEntity<>(service.exportData(), headers, HttpStatus.CREATED);
    }
}
