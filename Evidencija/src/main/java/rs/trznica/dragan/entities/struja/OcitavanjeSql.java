package rs.trznica.dragan.entities.struja;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "Ocitavanje")
public class OcitavanjeSql {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "brojilo_id", nullable = false)
    private Long brojiloId;

    @Column(name = "brojilo_vrsta", nullable = false)
    @Enumerated(EnumType.STRING)
    private VrstaBrojila brojiloVrsta;

    @Column(name = "brojilo_broj", nullable = false, length = 12)
    private String brojiloBroj;

    @Column(name = "brojilo_ed", nullable = false, length = 12)
    private String brojiloED;

    @Column(name = "mesec", nullable = false, length = 7)
    private String mesec;

    @Column(name = "kw_vt")
    @Builder.Default
    private Long kwVT = 0L;

    @Column(name = "kw_nt", nullable = false)
    @Builder.Default
    private Long kwNT = 0L;

    @Column(name = "cena_vt")
    @Builder.Default
    private Long cenaVT = 0L;

    @Column(name = "cena_nt", nullable = false)
    @Builder.Default
    private Long cenaNT = 0L;

    @Column(name = "pristup", nullable = false)
    @Builder.Default
    private Long pristup = 0L;

    @Column(name = "podsticaj", nullable = false)
    @Builder.Default
    private Long podsticaj = 0L;

    @Column(name = "kw_reaktivna")
    @Builder.Default
    private Long kwReaktivna = 0L;

    @Column(name = "cena_reaktivna")
    @Builder.Default
    private Long cenaReaktivna = 0L;
}
