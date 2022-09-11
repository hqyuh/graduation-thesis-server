package com.hqh.quizserver.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicDTO {
    private Long id;
    private String name;
    private List<TestQuizzResponseDTO> quizzResponseDTOList;
}
