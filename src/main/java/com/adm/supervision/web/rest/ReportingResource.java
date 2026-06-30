package com.adm.supervision.web.rest;

import com.adm.supervision.service.DashboardReportingService;
import com.adm.supervision.service.dto.GenerateRapportExportRequest;
import com.adm.supervision.service.dto.RapportExportPreviewDTO;
import com.adm.supervision.service.reporting.ExportDownload;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reporting")
public class ReportingResource {

    private final DashboardReportingService dashboardReportingService;

    public ReportingResource(DashboardReportingService dashboardReportingService) {
        this.dashboardReportingService = dashboardReportingService;
    }

    @PostMapping("/exports/generate")
    @PreAuthorize("@businessAuthorizationService.canExportReporting()")
    public ResponseEntity<RapportExportPreviewDTO> generateExport(@Valid @RequestBody GenerateRapportExportRequest request) {
        return ResponseEntity.ok(dashboardReportingService.generateExport(request));
    }

    @GetMapping("/exports/{rapportExportId}/preview")
    @PreAuthorize("@businessAuthorizationService.canReadReporting()")
    public ResponseEntity<RapportExportPreviewDTO> previewExport(@PathVariable Long rapportExportId) {
        return ResponseEntity.ok(dashboardReportingService.previewExport(rapportExportId));
    }

    @GetMapping("/exports/{rapportExportId}/download")
    @PreAuthorize("@businessAuthorizationService.canReadReporting()")
    public ResponseEntity<byte[]> downloadExport(@PathVariable Long rapportExportId) {
        ExportDownload exportDownload = dashboardReportingService.downloadExport(rapportExportId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + exportDownload.fileName() + "\"")
            .header(HttpHeaders.CONTENT_TYPE, exportDownload.contentType())
            .body(exportDownload.content());
    }
}
