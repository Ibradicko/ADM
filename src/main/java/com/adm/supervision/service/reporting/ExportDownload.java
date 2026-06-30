package com.adm.supervision.service.reporting;

public record ExportDownload(byte[] content, String fileName, String contentType) {}
