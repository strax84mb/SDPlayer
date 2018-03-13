package rs.trznica.dragan.validator.tankovanje;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import rs.trznica.dragan.dto.tankovanje.PutniNalogDto;

public class PutniNalogValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return PutniNalogDto.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors err) {
		if (!supports(obj.getClass())) {
			err.reject("Pogre\u0161an objekat u kodu!");
			return;
		}
		PutniNalogDto dto = (PutniNalogDto) obj;

		if (dto.getVozilo() == null) {
			err.reject("Mora\u0161 izabrati vozilo.");
		}
		if (StringUtils.isEmpty(dto.getVozac())) {
			err.reject("Mora\u0161 uneti voza\u010Da.");
		}
		if (StringUtils.isEmpty(dto.getRedniBroj())) {
			err.reject("Obavezno je uneti redni broj.");
		} else {
			try {
				if (Integer.valueOf(dto.getRedniBroj()) < 0) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				err.reject("Redni broj mora biti ceo pozitivan broj ili nula.");
			}
		}
		if (StringUtils.isEmpty(dto.getRelacija())) {
			err.reject("Mora\u0161 uneti relaciju.");
		}
		if (dto.getDatum() != null) {
			err.reject("Mora\u0161 uneti datum.");
		}
		if (StringUtils.isEmpty(dto.getVrstaPrevoza())) {
			err.reject("");
		}
		if (!dto.getVozilo().getTeretnjak()) {
			if (StringUtils.isEmpty(dto.getKorisnik())) {
				err.reject("Mora\u0161 uneti korisnika prevoza.");
			}
		}
		if (StringUtils.isEmpty(dto.getRegOznaka())) {
			err.reject("Mora\u0161 uneti radnu organizaciju.");
		}
		if (StringUtils.isEmpty(dto.getAdresaGaraze())) {
			err.reject("Mora\u0161 uneti adresu gara\u017Ee.");
		}
		if (StringUtils.isEmpty(dto.getMesto())) {
			err.reject("Mora\u0161 uneti mesto.");
		}
	}

}
