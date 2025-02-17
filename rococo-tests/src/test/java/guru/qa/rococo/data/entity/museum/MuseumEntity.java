package guru.qa.rococo.data.entity.museum;

import guru.qa.rococo.model.rest.MuseumJson;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "museum")
public class MuseumEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "bytea", nullable = false)
    private byte[] photo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "geo_id", nullable = false)
    private GeoEntity geo;

    public static MuseumEntity fromJson(MuseumJson json, GeoEntity geo) {
        MuseumEntity entity = new MuseumEntity();
        entity.setId(json.id());
        entity.setTitle(json.title());
        entity.setDescription(json.description());
        entity.setPhoto(json.photo().getBytes());
        entity.setGeo(geo);
        return entity;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        MuseumEntity that = (MuseumEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(id);
    }
}