package com.ecommerce.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "exhibition_file")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExhibitionFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Expose
    private Integer id;

    @Column(name = "file_name")
    @Expose
    private String fileName;

    @Column(name = "file_content")
    @Expose
    private String fileContent;

    @Column(name = "file_path")
    @Expose
    private String filePath;

    @JsonIgnore
    @ManyToMany(mappedBy = "listExhFile")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Set<Proof> likes;

}
