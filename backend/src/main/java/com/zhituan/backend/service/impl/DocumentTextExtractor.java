package com.zhituan.backend.service.impl;

import com.zhituan.backend.common.exception.BusinessException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Component
public class DocumentTextExtractor {

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

    public ExtractResult extract(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请上传文件");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件过大，最大支持 10MB");
        }

        String filename = file.getOriginalFilename();
        String ext = extensionOf(filename);
        if (!StringUtils.hasText(ext)) {
            throw new BusinessException("不支持的文件类型，请上传 pdf/doc/docx/txt 文件");
        }

        try {
            byte[] bytes = file.getBytes();
            String text = switch (ext) {
                case "txt" -> extractTxt(bytes);
                case "pdf" -> extractPdf(bytes);
                case "docx" -> extractDocx(bytes);
                case "doc" -> extractDoc(bytes);
                default -> throw new BusinessException("不支持的文件类型: " + ext);
            };

            if (!StringUtils.hasText(text)) {
                throw new BusinessException("未提取到有效文本，请检查文件内容");
            }

            return new ExtractResult(filename, ext, normalizeText(text));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("文件解析失败: " + e.getMessage());
        }
    }

    private String extensionOf(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private String extractTxt(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private String extractPdf(byte[] bytes) throws IOException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(bytes))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractDocx(byte[] bytes) throws IOException {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(bytes));
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    private String extractDoc(byte[] bytes) throws IOException {
        try (HWPFDocument document = new HWPFDocument(new ByteArrayInputStream(bytes));
             WordExtractor extractor = new WordExtractor(document)) {
            return extractor.getText();
        }
    }

    private String normalizeText(String text) {
        String normalized = text.replace("\r\n", "\n").replace("\r", "\n");
        if (normalized.length() > 20000) {
            normalized = normalized.substring(0, 20000);
        }
        return normalized.trim();
    }

    public record ExtractResult(String fileName, String fileType, String text) {
    }
}
