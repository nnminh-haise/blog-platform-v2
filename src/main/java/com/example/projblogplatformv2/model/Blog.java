package com.example.projblogplatformv2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "blogs")
public class Blog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @DateTimeFormat(pattern = "yyyy/MM/dd")
    @Column(name = "publish_at")
    private Date publishAt;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "attachment", nullable = false)
    private String attachment;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "hidden_status", nullable = false)
    private Boolean hiddenStatus = false;

    @OneToMany(mappedBy = "blog", fetch = FetchType.EAGER)
    private Collection<CategoryDetail> categoryDetails;
}
