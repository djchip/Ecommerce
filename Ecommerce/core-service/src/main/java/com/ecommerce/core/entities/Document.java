package com.ecommerce.core.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "DOCUMENT")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Expose
    private Integer id;

    @Column(name = "DOCUMENT_NUMBER")
    @Expose
    private String documentNumber;

    @Column(name = "DOCUMENT_NAME")
    @Expose
    private String documentName;

    @Column(name = "DOCUMENT_NAME_EN")
    @Expose
    private String documentNameEn;

    @Column(name = "DOCUMENT_TYPE")
    @Expose
    private Integer documentType;

    @Column(name = "RELEASE_DATE")
    @JsonFormat(pattern="dd-MM-yyyy")
    @Expose
    private Date releaseDate;

    @Column(name = "SIGNER")
    @Expose
    private String signer;

    @Column(name = "FIELD")
    @Expose
    private Integer field;

    @Column(name = "RELEASE_BY")
    @Expose
    private Integer releaseBy;

    @Column(name = "DESCRIPTION")
    @Expose
    private String description;

    @Column(name = "DESCRIPTION_EN")
    @Expose
    private String descriptionEn;

    @Column(name = "CREATED_BY")
    @Expose
    private String createdBy;

    @Column(name = "UPDATED_BY")
    @Expose
    private String updatedBy;

    @Column(name = "UNDO_STATUS")
    @Expose
    private Integer undoStatus;

    @Column(name = "DELETED")
    @Expose
    private Integer delete;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(
            name = "document_unit",
            joinColumns = @JoinColumn(name = "document_id", insertable = true, updatable = true),
            inverseJoinColumns = @JoinColumn(name = "unit_id", insertable = true, updatable = true))
    @Expose
    List<Unit> unit;
}
