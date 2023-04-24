package com.ecommerce.core.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "NOTIFICATIONS")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

public class Notifications{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private Integer id;

    @Column(name = "NOTIFI_TYPE")
    private Integer notifiType;

    @Column(name = "USERNAME")
    private String username;
    
    @Column(name = "CONTENTS")
    private String contents;

    @Column(name = "created_date", updatable = false, columnDefinition = "DATE")
	private LocalDateTime createdDate;
    
    @Column(name = "STATUS")
    private Integer status;

	@PrePersist
	protected void onCreate() {
		this.createdDate = LocalDateTime.now();
	}
}
