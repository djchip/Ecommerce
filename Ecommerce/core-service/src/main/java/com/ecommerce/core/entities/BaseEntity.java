package com.ecommerce.core.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 6865928648644949247L;

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
