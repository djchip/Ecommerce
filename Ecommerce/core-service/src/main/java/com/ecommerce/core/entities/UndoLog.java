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
@Table(name = "undo_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UndoLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "action")
    private String action;

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

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Builder(builderMethodName = "undoLogBuilder")
    public UndoLog(String action, String requestObject, String revertObject, int status,
                   String url, String description, String tableName, Integer idRecord,
                   String createdBy, String updatedBy,
                   LocalDateTime createdDate, LocalDateTime updatedDate) {
        super(createdDate, updatedDate);
        this.action = action;
        this.requestObject = requestObject;
        this.revertObject = revertObject;
        this.status = status;
        this.url = url;
        this.description = description;
        this.tableName = tableName;
        this.idRecord = idRecord;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }
}
