package com.adm.supervision.service.reporting;

import com.adm.supervision.config.ApplicationProperties;
import com.adm.supervision.domain.enumeration.FormatExport;
import com.adm.supervision.service.BusinessValidationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

@Service
public class ReportFileService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT;
    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final ApplicationProperties applicationProperties;

    public ReportFileService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public void writeReport(String fileName, FormatExport format, ReportDocument document) {
        Path filePath = resolveStoragePath(fileName);
        try {
            Files.createDirectories(filePath.getParent());
            if (format == FormatExport.EXCEL) {
                Files.write(filePath, buildWorkbook(document));
            } else {
                Files.write(filePath, buildPdf(document));
            }
        } catch (IOException e) {
            throw new BusinessValidationException("rapportExport", "fileWriteError", "Impossible de generer le fichier exporte");
        }
    }

    public void writePreview(String fileName, String preview) {
        Path previewPath = resolveStoragePath(previewFileName(fileName));
        try {
            Files.createDirectories(previewPath.getParent());
            Files.writeString(previewPath, preview, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BusinessValidationException("rapportExport", "previewWriteError", "Impossible de sauvegarder l'apercu du rapport");
        }
    }

    public String loadPreview(String fileName) {
        Path previewPath = resolveExistingStoragePath(previewFileName(fileName));
        try {
            return Files.readString(previewPath, StandardCharsets.UTF_8);
        } catch (RuntimeException | IOException e) {
            return "Apercu indisponible pour ce rapport";
        }
    }

    public ExportDownload download(String fileName, FormatExport format) {
        Path filePath = resolveExistingStoragePath(fileName);
        try {
            return new ExportDownload(Files.readAllBytes(filePath), filePath.getFileName().toString(), contentTypeFor(format));
        } catch (IOException e) {
            throw new BusinessValidationException("rapportExport", "fileReadError", "Impossible de lire le fichier exporte");
        }
    }

    private Path resolveStoragePath(String fileName) {
        Path storageRoot = Path.of(applicationProperties.getReporting().getStoragePath()).toAbsolutePath().normalize();
        Path resolved = storageRoot.resolve(fileName).normalize();
        if (!resolved.startsWith(storageRoot)) {
            throw new BusinessValidationException("rapportExport", "invalidFilePath", "Chemin de rapport invalide");
        }
        return resolved;
    }

    private Path resolveExistingStoragePath(String fileName) {
        Path filePath = resolveStoragePath(fileName);
        if (!Files.exists(filePath)) {
            throw new BusinessValidationException("rapportExport", "fileNotFound", "Fichier exporte introuvable");
        }
        return filePath;
    }

    private String previewFileName(String fileName) {
        int extensionIndex = fileName.lastIndexOf('.');
        if (extensionIndex < 0) {
            return fileName + ".preview.txt";
        }
        return fileName.substring(0, extensionIndex) + ".preview.txt";
    }

    private String contentTypeFor(FormatExport format) {
        return format == FormatExport.EXCEL ? EXCEL_CONTENT_TYPE : PDF_CONTENT_TYPE;
    }

    private byte[] buildPdf(ReportDocument document) throws IOException {
        try (PDDocument pdf = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            new PdfReportRenderer(pdf, document).render();
            pdf.save(output);
            return output.toByteArray();
        }
    }

    private byte[] buildWorkbook(ReportDocument document) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(output, StandardCharsets.UTF_8)) {
            writeZipEntry(zip, "[Content_Types].xml", buildContentTypesXml());
            writeZipEntry(zip, "_rels/.rels", buildPackageRelationshipsXml());
            writeZipEntry(zip, "docProps/core.xml", buildCorePropertiesXml());
            writeZipEntry(zip, "docProps/app.xml", buildAppPropertiesXml());
            writeZipEntry(zip, "xl/workbook.xml", buildWorkbookXml());
            writeZipEntry(zip, "xl/_rels/workbook.xml.rels", buildWorkbookRelationshipsXml());
            writeZipEntry(zip, "xl/worksheets/sheet1.xml", buildWorksheetXml(document));
        }
        return output.toByteArray();
    }

    private String buildContentTypesXml() {
        return (
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
            "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">" +
            "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>" +
            "<Default Extension=\"xml\" ContentType=\"application/xml\"/>" +
            "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>" +
            "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>" +
            "<Override PartName=\"/docProps/core.xml\" ContentType=\"application/vnd.openxmlformats-package.core-properties+xml\"/>" +
            "<Override PartName=\"/docProps/app.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.extended-properties+xml\"/>" +
            "</Types>"
        );
    }

    private String buildPackageRelationshipsXml() {
        return (
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
            "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">" +
            "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>" +
            "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties\" Target=\"docProps/core.xml\"/>" +
            "<Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties\" Target=\"docProps/app.xml\"/>" +
            "</Relationships>"
        );
    }

    private String buildCorePropertiesXml() {
        String now = DATE_TIME_FORMATTER.format(Instant.now());
        return (
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
            "<cp:coreProperties xmlns:cp=\"http://schemas.openxmlformats.org/package/2006/metadata/core-properties\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<dc:title>ADM Reporting Export</dc:title>" +
            "<dc:creator>ADM Backend</dc:creator>" +
            "<cp:lastModifiedBy>ADM Backend</cp:lastModifiedBy>" +
            "<dcterms:created xsi:type=\"dcterms:W3CDTF\">" +
            now +
            "</dcterms:created>" +
            "<dcterms:modified xsi:type=\"dcterms:W3CDTF\">" +
            now +
            "</dcterms:modified>" +
            "</cp:coreProperties>"
        );
    }

    private String buildAppPropertiesXml() {
        return (
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
            "<Properties xmlns=\"http://schemas.openxmlformats.org/officeDocument/2006/extended-properties\" xmlns:vt=\"http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes\">" +
            "<Application>ADM Backend</Application>" +
            "</Properties>"
        );
    }

    private String buildWorkbookXml() {
        return (
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
            "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">" +
            "<sheets><sheet name=\"Report\" sheetId=\"1\" r:id=\"rId1\"/></sheets>" +
            "</workbook>"
        );
    }

    private String buildWorkbookRelationshipsXml() {
        return (
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
            "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">" +
            "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>" +
            "</Relationships>"
        );
    }

    private String buildWorksheetXml(ReportDocument document) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of(document.title()));
        rows.addAll(document.summaryLines().stream().map(List::of).toList());
        rows.add(List.of(""));
        rows.add(document.headers());
        rows.addAll(document.rows());

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        xml.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>");
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            xml.append("<row r=\"").append(rowIndex + 1).append("\">");
            List<String> row = rows.get(rowIndex);
            for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                String reference = excelColumnName(columnIndex) + (rowIndex + 1);
                xml
                    .append("<c r=\"")
                    .append(reference)
                    .append("\" t=\"inlineStr\"><is><t>")
                    .append(escapeXml(row.get(columnIndex)))
                    .append("</t></is></c>");
            }
            xml.append("</row>");
        }
        xml.append("</sheetData></worksheet>");
        return xml.toString();
    }

    private void writeZipEntry(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private String excelColumnName(int index) {
        StringBuilder builder = new StringBuilder();
        int current = index;
        do {
            builder.insert(0, (char) ('A' + (current % 26)));
            current = current / 26 - 1;
        } while (current >= 0);
        return builder.toString();
    }

    private String escapeXml(String value) {
        String safeValue = value == null ? "" : value;
        return safeValue.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }
}
