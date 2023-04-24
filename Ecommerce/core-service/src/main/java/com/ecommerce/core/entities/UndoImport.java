package com.ecommerce.core.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "undo_import")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UndoImport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "request_object")
    private String requestObject;

    @Column(name = "revert_object")
    private String revertObject;

    @Column(name = "status")
    private Integer status;

    @Column(name = "url")
    private String url;

    @Column(name = "description")
    private String description;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "id_record")
    private Integer idRecord;

    @Column(name = "undo_id")
    private Integer undoId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Builder(builderMethodName = "undoImportBuilder")
    public UndoImport(String requestObject, String revertObject, Integer status, String url, String description, String tableName,
                      Integer idRecord, Integer undoId, String createdBy, String updatedBy, LocalDateTime createdDate, LocalDateTime updatedDate) {
        super(createdDate, updatedDate);
        this.requestObject = requestObject;
        this.revertObject = revertObject;
        this.status = status;
        this.url = url;
        this.description = description;
        this.tableName = tableName;
        this.idRecord = idRecord;
        this.undoId = undoId;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }
}
