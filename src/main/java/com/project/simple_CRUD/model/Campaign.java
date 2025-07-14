package com.project.simple_CRUD.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private List<String> keyWords;
    @Column(nullable = false)
    private BigDecimal bidAmount;

    @Column(nullable = false)
    private double radius;

    @Column(nullable = false)
    private String town;

    @Column(nullable = false)
    private Boolean status;

    @Column(nullable = false)
    private BigDecimal campaignFund;

}
