package com.ecommerce.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

@Entity
@Table(name = "PROOF_STANDARD")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProofsStandard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "PROOF_ID")
    private Integer proofId;

    @Column(name = "STANDARD_ID")
    private Integer standardId;
}
