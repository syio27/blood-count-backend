package com.pja.bloodcount.service.contract;

import java.io.IOException;

public interface ExportService {

    byte[] exportData() throws IOException;
}
