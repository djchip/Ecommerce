package com.ecommerce.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

@Entity
@Table(name = "STANDARD_USER")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "standardId")
    private Integer standardId;

    @Column(name = "userId")
    private Integer userId;

    @Column(name = "programId")
    private Integer programId;

}
