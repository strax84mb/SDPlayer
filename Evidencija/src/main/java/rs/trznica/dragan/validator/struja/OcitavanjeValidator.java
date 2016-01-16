package rs.trznica.dragan.validator.struja;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import rs.trznica.dragan.dto.struja.OcitavanjeDto;
import rs.trznica.dragan.entities.struja.VrstaBrojila;
import rs.trznica.dragan.forms.support.DecimalFormater;

public class OcitavanjeValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(OcitavanjeDto.class);
	}

	private boolean validNumber(String value, int decimals) {
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		try {
			DecimalFormater.parseToLong(value, decimals);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void validate(Object obj, Errors errors) {
		if (!supports(obj.getClass())) {
			errors.reject("Pogrešan objekat u kodu!");
			return;
		}
		OcitavanjeDto dto = (OcitavanjeDto) obj;
		if (dto.getBrojilo() == null) {
			errors.reject("Obavezno je izabrati brojilo.");
		}
		try {
			if (!dto.getMesec().matches("^[0-9]{4}-[0-9]{2}$")) {
				throw new Exception();
			}
			Integer.valueOf(dto.getMesec().substring(0, 4));
			if (Integer.valueOf(dto.getMesec().substring(5)) > 12) {
				throw new Exception();
			}
		} catch(Exception e) {
			errors.reject("Obavezno je uneti mesec u formatu yyyy-MM (npr. 2015-10).");
		}
		if (!validNumber(dto.getKwNT(), 0)) {
			errors.reject("Obavezno je uneti potrošene kW niže tarife.");
		}
		if (!VrstaBrojila.SIR_POT_JED.equals(dto.getBrojilo().getVrstaBrojila()) && !validNumber(dto.getKwVT(), 0)) {
			errors.reject("Obavezno je uneti potrošene kW više tarife.");
		}
		if (!validNumber(dto.getCenaNT(), 2)) {
			errors.reject("Obavezno je uneti cenu niže tarife.");
		}
		if (!VrstaBrojila.SIR_POT_JED.equals(dto.getBrojilo().getVrstaBrojila()) && !validNumber(dto.getCenaVT(), 2)) {
			errors.reject("Obavezno je uneti cenu više tarife.");
		}
		if (!validNumber(dto.getPristup(), 2)) {
			errors.reject("Obavezno je uneti pristup.");
		}
		if (!validNumber(dto.getPodsticaj(), 2)) {
			errors.reject("Obavezno je uneti podsticaj.");
		}
		if (!validNumber(dto.getKwReaktivna(), 0)) {
			errors.reject("Obavezno je uneti reaktivne kW.");
		}
		if (!validNumber(dto.getCenaKW(), 3)) {
			errors.reject("Obavezno je uneti cenu reaktivnog kW.");
		}
	}

}
