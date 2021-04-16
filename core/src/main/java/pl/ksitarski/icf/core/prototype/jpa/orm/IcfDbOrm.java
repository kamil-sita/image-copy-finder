package pl.ksitarski.icf.core.prototype.jpa.orm;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "icf_db")
public class IcfDbOrm {
    protected IcfDbOrm() {
    }

    public IcfDbOrm(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "icf_db_id", nullable = false)
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "icfDbOrm")
    private List<IcfFileOrm> fileOrmList;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<IcfFileOrm> getFileOrmList() {
        return fileOrmList;
    }
}
