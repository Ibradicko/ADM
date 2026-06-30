package com.adm.supervision.service.dto;

public class RapportExportPreviewDTO {

    private RapportExportDTO export;
    private String preview;

    public RapportExportDTO getExport() {
        return export;
    }

    public void setExport(RapportExportDTO export) {
        this.export = export;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }
}
