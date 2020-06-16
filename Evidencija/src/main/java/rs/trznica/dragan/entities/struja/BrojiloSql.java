package rs.trznica.dragan.entities.struja;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

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
@Entity(name = "Brojilo")
public class BrojiloSql {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "broj", nullable = false, length = 12)
    private String broj;

    @Column(name = "ed", nullable = false, length = 12)
    private String ed;

    @Column(name = "opis", length = 255)
    private String opis;

    @Column(name = "u_funkciji", nullable = false)
    private Boolean uFunkciji = true;

    @Column(name = "vrsta_brojila", nullable = false)
    @Enumerated(EnumType.STRING)
    private VrstaBrojila vrstaBrojila;

    @Override
    public String toString() {
        return (StringUtils.isEmpty(broj) || StringUtils.isEmpty(ed)) ? "Greska" :
                new StringBuilder(ed).append(" - ").append(broj).append(" - ").append(vrstaBrojila.getAbrev()).toString();
    }
}
