package rs.trznica.dragan.validator.tankovanje;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import rs.trznica.dragan.dto.tankovanje.PotrosacDto;

public class PotrosacValidator implements Validator {

	public boolean supports(Class<?> clazz) {
		return PotrosacDto.class.equals(clazz);
	}

	public void validate(Object obj, Errors errors) {
		if (!supports(obj.getClass())) {
			errors.reject("Pogrešan objekat u kodu!");
			return;
		}
		PotrosacDto dto = (PotrosacDto)obj;
		if (StringUtils.isEmpty(dto.getTip())) {
			errors.reject("Tip / naziv je obavezan za vozilo.");
		} else if (dto.getTip().length() > 15) {
			errors.reject("Tip / naziv ne može imati više od 15 karaktera.");
		}
		if (dto.getVozilo()) {
			if (StringUtils.isEmpty(dto.getMarka())) {
				errors.reject("Marka je obavezna za vozilo.");
			} else if (dto.getMarka().length() > 15) {
				errors.reject("Marka ne može imati više od 15 karaktera.");
			}
			if (StringUtils.isEmpty(dto.getRegOznaka())) {
				errors.reject("Registracija je obavezna za vozilo.");
			} else if (dto.getRegOznaka().length() > 11) {
				errors.reject("Registracija ne može imati više od 11 karaktera.");
			}
		}
	}
}
