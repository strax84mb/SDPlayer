package rs.trznica.dragan.entities.tankovanje;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.trznica.dragan.entities.support.GorivoType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Potrosac {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(unique = true, length = 11)
	private String regOznaka;

	@Column(length = 15, nullable = true)
	private String marka;

	@Column(length = 15, nullable = true)
	private String tip;

	@Column(nullable = false)
	private Boolean vozilo = false;

	@Column(nullable = false)
	private Boolean teretnjak = false;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private GorivoType gorivo = GorivoType.BMB;

	@Column(nullable = false)
	private Boolean aktivan = true;

	@Column(nullable = false, name = "meri_km")
	private Boolean meriKm = true;
	
	@Column(nullable = true, name = "podrucje")
	private String podrucje;
	
	@Column(nullable = false, name = "broj_sedista")
	private Integer brojSedista = 0;

	@Column(nullable = false, name = "snaga_motora")
	private Integer snagaMotora = 0;
	
	@Column(nullable = false, name = "tezina")
	private Integer tezina = 0;
	
	@Column(nullable = false, name = "nosivost")
	private Integer nosivost = 0;
	
	@Column(nullable = true, name = "vozaci")
	private String vozaci;

	@Override
	public String toString() {
		return (Boolean.TRUE.equals(vozilo)) ? id + " - " + regOznaka + " - " + marka + " " + tip : id + " - " + tip ;
	}

}
