package com.ecommerce.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "software_log")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SoftwareLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "ID")
    private Integer id;
    @Column(name = "Error")
    private String error;
    @Column(name = "AmendingContent")
    private String amendingcontent;
    @Column(name = "Version")
    private String version;
    @Column(name = "created_date")
    private Date errorlogtime;
    @Column(name = "updated_date")
    private Date successfulrevisiontime;
    @Column(name = "CREATED_BY")
    private String createdBy;
    @Column(name = "UPDATED_BY")
    private String updatedBy;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "UNDO_STATUS")
    private Integer undoStatus;

    @Column(name = "DELETED")
    private Integer deleted;


//    @PrePersist
//    protected void onCreate() {
//        this.errorlogtime = LocalDateTime.now();
////        this.successfulrevisiontime = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        this.successfulrevisiontime = LocalDateTime.now();
//    }
}
