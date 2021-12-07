package model;

import javax.persistence.*;

@Entity
@Table(name = "attachment")
public class Attachment {
    @Id
    @Column (name = "id")
    @SequenceGenerator(name = "generator", sequenceName = "sequenceofattachments", schema = "senla", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    private Integer id;

    private String name;
    private String docType;

    @Lob
    private byte[] data;

    public Attachment() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocType() {
        return docType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }
}
