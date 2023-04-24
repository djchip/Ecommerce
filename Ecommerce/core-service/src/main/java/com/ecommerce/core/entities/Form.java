package com.ecommerce.core.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "FORM")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "description")
    private String description;

    @Column(name = "description_en")
    private String descriptionEn;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "upload_time")
    private Date uploadTime;

    @Column(name = "deleted")
    private Integer deleted;

    @Column(name = "year_of_application")
    private Integer yearOfApplication;

    @Column(name = "status")
    private Integer status;

    @Column(name = "path_file")
    private String pathFile;

    @Column(name = "num_title")
    private Integer numTitle;

    @Column(name = "form_key")
    private Integer formKey;

    @Column(name = "start_title")
    private Integer startTitle;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(
            name = "form_unit",
            joinColumns = @JoinColumn(name = "form_id", insertable = true, updatable = true),
            inverseJoinColumns = @JoinColumn(name = "unit_id", insertable = true, updatable = true))
    List<Unit> units;
}
