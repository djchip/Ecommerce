package com.ecommerce.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "EMAIL_CONFIG")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

public class EmailConfig{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "HOST")
    private String host;

    @Column(name = "PORT")
    private String port;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;
}
