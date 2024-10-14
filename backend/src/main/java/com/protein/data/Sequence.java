package com.protein.data;

import javax.persistence.*;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Entity
public class Sequence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer indexNumber;
    private String proteins;

    @Column(length = 1000)
    private String accessions; // 存储为分号分隔的字符串

    @Column(length = 5000)
    private String sequence;

    @Column(length = 5000)
    private String annotations; // 存储为逗号分隔的字符串

    @Column(length = 5000)
    private String interpros; // 存储为逗号分隔的字符串

    private Integer orgs;

    // 标准的 Getter 和 Setter 方法

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(Integer indexNumber) {
        this.indexNumber = indexNumber;
    }

    public String getProteins() {
        return proteins;
    }

    public void setProteins(String proteins) {
        this.proteins = proteins;
    }

    public String getAccessions() {
        return accessions;
    }

    public void setAccessions(String accessions) {
        this.accessions = accessions;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getAnnotations() {
        return annotations;
    }

    public void setAnnotations(String annotations) {
        this.annotations = annotations;
    }

    public String getInterpros() {
        return interpros;
    }

    public void setInterpros(String interpros) {
        this.interpros = interpros;
    }

    public Integer getOrgs() {
        return orgs;
    }

    public void setOrgs(Integer orgs) {
        this.orgs = orgs;
    }

    // 基于列表的 Getter 和 Setter 方法

    public List<String> getAccessionsList() {
        if (this.accessions != null && !this.accessions.isEmpty()) {
            return Arrays.asList(this.accessions.split(";\\s*"));
        }
        return null;
    }

    public void setAccessionsList(List<String> accessionsList) {
        if (accessionsList != null && !accessionsList.isEmpty()) {
            this.accessions = accessionsList.stream().collect(Collectors.joining("; "));
        } else {
            this.accessions = null;
        }
    }

    public List<String> getAnnotationsList() {
        if (this.annotations != null && !this.annotations.isEmpty()) {
            return Arrays.asList(this.annotations.split(",\\s*"));
        }
        return null;
    }

    public void setAnnotationsList(List<String> annotationsList) {
        if (annotationsList != null && !annotationsList.isEmpty()) {
            this.annotations = annotationsList.stream().collect(Collectors.joining(", "));
        } else {
            this.annotations = null;
        }
    }

    public List<String> getInterprosList() {
        if (this.interpros != null && !this.interpros.isEmpty()) {
            return Arrays.asList(this.interpros.split(",\\s*"));
        }
        return null;
    }

    public void setInterprosList(List<String> interprosList) {
        if (interprosList != null && !interprosList.isEmpty()) {
            this.interpros = interprosList.stream().collect(Collectors.joining(", "));
        } else {
            this.interpros = null;
        }
    }
}