package com.ecommerce.core.entities;

import com.google.gson.annotations.Expose;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "assessment")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Assessment{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Expose
    private Integer id;

    @Column(name = "name")
    @Expose
    private String name;

    @Column(name = "name_en")
    @Expose
    private String nameEn;

    @Column(name = "description")
    @Expose
    private String description;

    @Column(name = "description_en")
    @Expose
    private String descriptionEn;

    @Column(name = "file")
    @Expose
    private String file;

    @Column(name = "created_by")
    @Expose
    private String createdBy;

    @Column(name = "updated_by")
    @Expose
    private String updatedBy;

    @Column(name = "evaluated")
    @Expose
    private  Integer evaluated;

    @Column(name = "program_id")
    @Expose
    private Integer programId;

    @Column(name = "directory_id")
    @Expose
    private Integer directoryId;
    @Column(name = "criteria_id")
    @Expose
    private Integer criteriaId;


    @Column(name = "deleted")
    @Expose
    private Integer delete;

    @Column(name = "undoStatus")
    @Expose
    private Integer undoStatus;

    @Column(name = "created_date")
//    @JsonFormat(pattern="dd-MM-yyyy")
    @Expose
    private Date createdDate;


    @Column(name = "updated_date")
//    @JsonFormat(pattern="dd-MM-yyyy")
    @Expose
    private Date updatedDate;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(
            name = "assessment_proof",
            joinColumns = @JoinColumn(name = "assessment_id", insertable = true, updatable = true),
            inverseJoinColumns = @JoinColumn(name = "proof_id", insertable = true, updatable = true))
    @Expose
    List<Proof> proofs;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(
            name = "assessment_experts",
            joinColumns = @JoinColumn(name = "assessment_id", insertable = true, updatable = true),
            inverseJoinColumns = @JoinColumn(name = "user_id", insertable = true, updatable = true))
    @Expose
    List<UserInfo> user;

    @Column(name = "temp")
    @Expose
    private Integer temp;

    @Column(name = "order_ass")
    @Expose
    private Integer orderAss;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(
            name = "assessment_viewers",
            joinColumns = @JoinColumn(name = "assessment_id", insertable = true, updatable = true),
            inverseJoinColumns = @JoinColumn(name = "user_id", insertable = true, updatable = true))
    @Expose
    List<UserInfo> viewers;

    @Column(name = "content")
    @Expose
    private String content;

    @Column(name = "comment")
    @Expose
    private String comment;


    @Column(name = "reporttype")
    @Expose
    private Integer reportType;
}
