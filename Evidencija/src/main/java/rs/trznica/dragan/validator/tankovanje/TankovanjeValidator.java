package rs.trznica.dragan.validator.tankovanje;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import rs.trznica.dragan.dto.tankovanje.TankovanjeDto;
import rs.trznica.dragan.forms.support.DecimalFormater;

public class TankovanjeValidator implements Validator {

	public boolean supports(Class<?> clazz) {
		return TankovanjeDto.class.equals(clazz);
	}

	public void validate(Object obj, Errors err) {
		if (!supports(obj.getClass())) {
			err.reject("Pogrešan objekat u kodu!");
			return;
		}
		TankovanjeDto dto = (TankovanjeDto) obj;
		if (dto.getPotrosacId() == null) {
			err.reject("Obavezno je izabrati potrošač.");
		}
		if (dto.getDatum() == null) {
			err.reject("Obavezno je uneti datum tankovanja.");
		}
		if (StringUtils.isEmpty(dto.getMesec())) {
			err.reject("Obavezno je uneti oznaku meseca za koji se tankovanje evidentira.");
		} else  if (!dto.getMesec().matches("^[0-9]{4}-[0-9]{2}$")) {
			err.reject("Oznaka meseca mora biti u formatu godina-mesec (npr. 2015-08).");
		}
		try {
			Double num = DecimalFormater.parseToDouble(dto.getKolicina());
			if (num < 0) {
				throw new NumberFormatException();
			}
		} catch (Exception e) {
			err.reject("Količina mora biti veća od nule.");
		}
		try {
			Double num = DecimalFormater.parseToDouble(dto.getJedCena());
			if (num < 0) {
				throw new NumberFormatException();
			}
		} catch (Exception e) {
			err.reject("Cena litre mora biti veća od nule.");
		}
		try {
			Long num = Long.valueOf(dto.getKilometraza());
			if (num <= 0) {
				throw new NumberFormatException();
			}
		} catch (Exception e) {
			err.reject("Kilometraža mora biti ceo pozitivan broj.");
		}
	}

}
