package com.ecommerce.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "action_log")
@FieldNameConstants
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "USER")
    private String user;

    @Column(name = "IP")
    private String ip;

    @Column(name = "ACTION_TIME", updatable = false)
    private LocalDateTime actionTime;

    @PrePersist
    protected  void onCreate(){
        this.actionTime = LocalDateTime.now();
    }

    @Column(name = "METHOD")
    private String method;

    @Column(name = "URL")
    private String url;

    @Column(name = "REQUEST")
    private String request;

    @Column(name = "RESPONSE")
    private String response;
}
