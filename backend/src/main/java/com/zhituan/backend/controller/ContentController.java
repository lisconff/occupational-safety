package com.zhituan.backend.controller;

import com.zhituan.backend.common.api.ApiResponse;
import com.zhituan.backend.domain.model.content.LegalCase;
import com.zhituan.backend.service.ContentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/cases/featured")
    public ApiResponse<List<LegalCase>> listHomepageFeaturedCases() {
        return ApiResponse.ok(contentService.listHomepageFeaturedCases());
    }

    @GetMapping("/cases/{caseId}")
    public ApiResponse<LegalCase> getCaseDetail(@PathVariable String caseId) {
        return ApiResponse.ok(contentService.getCaseDetail(caseId));
    }
}
