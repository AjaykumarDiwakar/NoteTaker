package com.notetaker.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoviePageResponse {

    private List<MovieDto> movieDtos;
    private Integer pageNumber;
    private Integer pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
}
