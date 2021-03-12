package pl.ksitarski.icf.core.jpa.orm;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "icf_file_opt")
public class IcfFileOptOrm {

    protected IcfFileOptOrm() {
    }

    public IcfFileOptOrm(byte[] data, String optimizationType, IcfFileOrm icfFileOrm) {
        this.data = data;
        this.optimizationType = optimizationType;
        this.icfFileOrm = icfFileOrm;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "icf_file_opt_id", nullable = false)
    private Long id;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "data", nullable = false)
    private byte[] data;

    @Column(name = "opt_type", nullable = false)
    private String optimizationType;

    @ManyToOne
    @JoinColumn(name = "icf_file_id_r", nullable = false)
    private IcfFileOrm icfFileOrm;

    public Long getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public String getOptimizationType() {
        return optimizationType;
    }

    public IcfFileOrm getIcfFileOrm() {
        return icfFileOrm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IcfFileOptOrm that = (IcfFileOptOrm) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
