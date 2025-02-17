package guru.qa.rococo.data.entity.museum;

import guru.qa.rococo.model.rest.GeoJson;
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
@Table(name = "geo")
public class GeoEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(nullable = false)
    private String city;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity country;

    public static GeoEntity fromJson(GeoJson json, CountryEntity country) {
        GeoEntity entity = new GeoEntity();
        entity.setId(null);
        entity.setCity(json.city());
        entity.setCountry(country);
        return entity;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof HibernateProxy) {
            o = ((HibernateProxy) o).getHibernateLazyInitializer().getImplementation();
        }
        if (getClass() != o.getClass()) return false;
        GeoEntity that = (GeoEntity) o;
        return Objects.equals(city, that.city) && Objects.equals(country.getId(), that.country.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(id);
    }
}