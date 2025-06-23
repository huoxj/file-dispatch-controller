package com.fdc.po;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "File")
public class File {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private String id;

    @Basic
    @Column(name = "url")
    private String url;

    @Basic
    @Column(name = "private_key")
    private String privateKey;

    @Basic
    @Column(name = "create_time")
    private Date createTime;

}
