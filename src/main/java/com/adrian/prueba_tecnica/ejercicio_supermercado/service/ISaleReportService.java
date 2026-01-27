package com.adrian.prueba_tecnica.ejercicio_supermercado.service;

import java.time.LocalDate;

public interface ISaleReportService {
    byte[] generateExcelIdBranch(Long branchId);

    byte[] generateExcelIdBranchDate(Long branchId, LocalDate startDate, LocalDate endDate);

    byte[] getBranches();
}
