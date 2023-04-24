package com.ecommerce.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseDbObj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "status")
    private Integer status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "col_1")
    private String col1;

    @Column(name = "col_2")
    private String col2;

    @Column(name = "col_3")
    private String col3;

    @Column(name = "col_4")
    private String col4;

    @Column(name = "col_5")
    private String col5;

    @Column(name = "col_6")
    private String col6;

    @Column(name = "col_7")
    private String col7;

    @Column(name = "col_8")
    private String col8;

    @Column(name = "col_9")
    private String col9;

    @Column(name = "col_10")
    private String col10;

    @Column(name = "col_11")
    private String col11;

    @Column(name = "col_12")
    private String col12;

    @Column(name = "col_13")
    private String col13;

    @Column(name = "col_14")
    private String col14;

    @Column(name = "col_15")
    private String col15;

    @Column(name = "col_16")
    private String col16;

    @Column(name = "col_17")
    private String col17;

    @Column(name = "col_18")
    private String col18;

    @Column(name = "col_19")
    private String col19;

    @Column(name = "col_20")
    private String col20;

    @Column(name = "col_21")
    private String col21;

    @Column(name = "col_22")
    private String col22;

    @Column(name = "col_23")
    private String col23;

    @Column(name = "col_24")
    private String col24;

    @Column(name = "col_25")
    private String col25;

    @Column(name = "col_26")
    private String col26;

    @Column(name = "col_27")
    private String col27;

    @Column(name = "col_28")
    private String col28;

    @Column(name = "col_29")
    private String col29;

    @Column(name = "col_30")
    private String col30;

    @Column(name = "created_date", updatable = false, columnDefinition = "DATE")
    private LocalDateTime createdDate;

    @Column(name = "updated_date", columnDefinition = "DATE")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
