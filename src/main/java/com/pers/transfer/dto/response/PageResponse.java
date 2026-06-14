package com.pers.transfer.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(List<T> content,
                              Metadata metadata
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                new Metadata(page.getNumber(), page.getSize(), page.getTotalElements())
        );
    }

    public record Metadata(int page, int size, long totalElements) {
    }
}
