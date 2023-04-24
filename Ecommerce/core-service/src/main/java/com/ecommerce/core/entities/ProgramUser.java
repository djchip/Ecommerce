package com.ecommerce.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

@Entity
@Table(name = "PROGRAM_USER")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "userId")
    private Integer userId;

    @Column(name = "programId")
    private Integer programId;
}
