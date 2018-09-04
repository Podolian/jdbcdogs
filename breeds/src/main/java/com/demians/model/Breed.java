package com.demians.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@Builder
public class Breed {
    private Long id;
    private String name;
    private Integer averageWeight;
    private String origin;
    private String recomendedNickname;
}
