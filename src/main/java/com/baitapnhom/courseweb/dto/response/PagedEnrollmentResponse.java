package com.baitapnhom.courseweb.dto.response;

import java.util.List;

public class PagedEnrollmentResponse {
    private List<EnrollmentResponse> content;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;

    public PagedEnrollmentResponse(List<EnrollmentResponse> content, 
                                  int totalPages, long totalElements, 
                                  int currentPage, int pageSize) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    // Getters
    public List<EnrollmentResponse> getContent() { return content; }
    public int getTotalPages() { return totalPages; }
    public long getTotalElements() { return totalElements; }
    public int getCurrentPage() { return currentPage; }
    public int getPageSize() { return pageSize; }
}