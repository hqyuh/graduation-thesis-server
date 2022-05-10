package com.hqh.graduationthesisserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportAndSuggestionsDto {

    private Long id;
    private String type;
    private String comment;
    private Long userId;

}
