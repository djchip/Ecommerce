package com.ecommerce.core.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import com.ecommerce.core.dto.ProofDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

//@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "proof")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NamedNativeQuery(
        name = "fts_content_proof",
        query = "SELECT p.ID, p.PROOF_NAME, p.PROOF_CODE, d.name as named, c.name, p.DESCRIPTION, p.DOCUMENT_TYPE, p.RELEASE_BY" +
                " FROM proof p LEFT JOIN directory d ON p.STANDARD_ID = d.id LEFT JOIN criteria c ON p.CRITERIA_ID = c.id LEFT JOIN exhibition_file f ON p.EXH_FILE = f.id" +
                " WHERE MATCH(f.file_content) AGAINST (?1) OR MATCH(p.PROOF_NAME, p.PROOF_CODE) AGAINST (?1)",
        resultSetMapping = "fts_proof_dto"
)
@SqlResultSetMapping(
        name = "fts_proof_dto",
        classes = @ConstructorResult(
                targetClass = ProofDTO.class,
                columns = {
                        @ColumnResult(name = "id"),
                        @ColumnResult(name = "proof_name"),
                        @ColumnResult(name = "proof_code"),
                        @ColumnResult(name = "named"),
                        @ColumnResult(name = "name"),
                        @ColumnResult(name = "description"),
                        @ColumnResult(name = "document_type"),
                        @ColumnResult(name = "release_by")
                }
        )
)
public class Proof {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Expose
    private Integer id;

    @Column(name = "PROOF_NAME")
    @Expose
    private String proofName;

    @Column(name = "PROOF_NAME_EN")
    @Expose
    private String proofNameEn;

    @Column(name = "PROOF_CODE")
    @Expose
    private String proofCode;

    @Column(name = "OLD_PROOF_CODE")
    @Expose
    private String oldProofCode;

    @Column(name = "STANDARD_ID")
    @Expose
    private Integer standardId;

    @Column(name = "CRITERIA_ID")
    @Expose
    private Integer criteriaId;

    @Column(name = "DOCUMENT_TYPE")
    @Expose
    private Integer documentType;

    @Column(name = "NUMBER_SIGN")
    @Expose
    private String numberSign;

    @Column(name = "RELEASE_DATE")
    @Expose
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime releaseDate;

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

    @Column(name = "NOTE")
    @Expose
    private String note;

    @Column(name = "NOTE_EN")
    @Expose
    private String noteEn;

    @Column(name = "CREATED_BY")
    @Expose
    private String createdBy;

    @Column(name = "UPDATED_BY")
    @Expose
    private String updatedBy;

    @Column(name = "PROGRAM_ID")
    @Expose
    private Integer programId;

    @Column(name = "ORDER_STAND")
    @Expose
    private Integer orderOfStandard;

    @Column(name = "ORDER_CRI")
    @Expose
    private Integer orderOfCriteria;

    @Column(name = "EXH_FILE")
    @Expose
    private Integer exhFile;

    @Column(name = "UPLOADED_DATE", columnDefinition = "DATE")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime uploadedDate;

    @Column(name = "STATUS")
    @Expose
    private Integer status;

    @Column(name = "UNDO_STATUS")
    @Expose
    private Integer undoStatus;

    @Column(name = "DELETED")
    @Expose
    private Integer deleted;

    @Expose
    @Column(name = "created_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdDate;

    @Expose
    @Column(name = "updated_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date updatedDate;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(
            name = "proofs_unit",
            joinColumns = @JoinColumn(name = "proofs_id", insertable = true, updatable = true),
            inverseJoinColumns = @JoinColumn(name = "units_id", insertable = true, updatable = true))
    @Expose
    List<Unit> unit;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(
            name = "proof_exh_file",
            joinColumns = @JoinColumn(name = "proof_id", insertable = true, updatable = true),
            inverseJoinColumns = @JoinColumn(name = "exh_file_id", insertable = true, updatable = true))
    @Expose
    List<ExhibitionFile> listExhFile;
}
