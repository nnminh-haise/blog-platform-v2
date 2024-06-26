package com.example.projblogplatformv2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBlogDto {
    private String title;

    private String description;

    private String attachment;
}
