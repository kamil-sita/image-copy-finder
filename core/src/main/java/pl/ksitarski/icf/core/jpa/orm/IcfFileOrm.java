package pl.ksitarski.icf.core.jpa.orm;

import pl.ksitarski.icf.core.accessing.ImageInDatabase;
import pl.ksitarski.icf.core.accessing.ImageInDatabaseImpl;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(
        name = "icf_file",
        indexes = {
                @Index(columnList = "r", name = "r_index_asd"),
                @Index(columnList = "g", name = "g_index_asd"),
                @Index(columnList = "b", name = "b_index_asd"),
                @Index(columnList = "statistics_present", name = "statistics_present_index_asd")
        }

)
public class IcfFileOrm {
    protected IcfFileOrm() {
    }

    public IcfFileOrm(String filePath, IcfDbOrm icfDbOrm) {
        this.filePath = filePath;
        this.icfDbOrm = icfDbOrm;
        this.statisticsPresent = false;
    }

    public ImageInDatabase toImageInDatabase() {
        return new ImageInDatabaseImpl(
                getIcfDbOrm(),
                this
        );
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "icf_file_id", nullable = false)
    private Long id;

    @Column(name = "hue", nullable = true)
    private Double hue;

    @Column(name = "saturation", nullable = true)
    private Double saturation;

    @Column(name = "value", nullable = true)
    private Double value;

    @Column(name = "r", nullable = true)
    private Integer r;

    @Column(name = "g", nullable = true)
    private Integer g;

    @Column(name = "b", nullable = true)
    private Integer b;

    @Column(name = "width_height_ratio", nullable = true)
    private Double widthHeightRatio;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "icf_db_id_r", nullable = false)
    private IcfDbOrm icfDbOrm;

    @OneToMany(mappedBy = "icfFileOrm")
    private List<IcfFileOptOrm> fileOptOrmList;

    @Column(name = "width", nullable = true)
    private Integer width;

    @Column(name = "height", nullable = true)
    private Integer height;

    @Column(name = "statistics_present", nullable = false)
    private Boolean statisticsPresent;

    //setters


    public IcfFileOrm setHue(Double hue) {
        this.hue = hue;
        return this;
    }

    public IcfFileOrm setSaturation(Double saturation) {
        this.saturation = saturation;
        return this;
    }

    public IcfFileOrm setValue(Double value) {
        this.value = value;
        return this;
    }

    public IcfFileOrm setR(Integer r) {
        this.r = r;
        return this;
    }

    public IcfFileOrm setG(Integer g) {
        this.g = g;
        return this;
    }

    public IcfFileOrm setB(Integer b) {
        this.b = b;
        return this;
    }

    public IcfFileOrm setWidthHeightRatio(Double widthHeightRatio) {
        this.widthHeightRatio = widthHeightRatio;
        return this;
    }

    public IcfFileOrm setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public IcfFileOrm setHeight(Integer height) {
        this.height = height;
        return this;
    }

    public IcfFileOrm setStatisticsPresent(Boolean statisticsPresent) {
        this.statisticsPresent = statisticsPresent;
        return this;
    }

    //getters
    public Long getId() {
        return id;
    }

    public Double getHue() {
        return hue;
    }

    public Double getSaturation() {
        return saturation;
    }

    public Double getValue() {
        return value;
    }

    public Double getWidthHeightRatio() {
        return widthHeightRatio;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public String getFilePath() {
        return filePath;
    }

    public IcfDbOrm getIcfDbOrm() {
        return icfDbOrm;
    }

    public List<IcfFileOptOrm> getFileOptOrmList() {
        return fileOptOrmList;
    }

    public Integer getR() {
        return r;
    }

    public Integer getG() {
        return g;
    }

    public Integer getB() {
        return b;
    }

    public Boolean getStatisticsPresent() {
        return statisticsPresent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IcfFileOrm fileOrm = (IcfFileOrm) o;

        return id != null ? id.equals(fileOrm.id) : fileOrm.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
