package com.ecommerce.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

@Entity
@Table(name = "SOFTWARE_VERSION")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SoftwareVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "VERSION")
    private String version;

    @Column(name = "CHANGE_LOGS")
    private String changeLogs;

    @Column(name = "LASTEST_VERSION")
    private Integer lastestVersion;

}