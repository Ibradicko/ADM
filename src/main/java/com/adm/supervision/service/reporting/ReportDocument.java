package com.adm.supervision.service.reporting;

import java.util.List;

public record ReportDocument(String title, List<String> summaryLines, List<String> headers, List<List<String>> rows, String preview) {}
