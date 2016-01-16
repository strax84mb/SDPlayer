package rs.trznica.dragan.validator.struja;

import java.text.SimpleDateFormat;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import rs.trznica.dragan.dto.struja.OcitavanjeDto;
import rs.trznica.dragan.forms.support.DecimalFormater;

public class OcitavanjeValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(getClass());
	}

	private boolean validNumber(String value, int decimals) {
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
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			sdf.setLenient(false);
			sdf.parse(dto.getMesec());
		} catch(Exception e) {
			errors.reject("Obavezno je uneti mesec u formatu yyyy-MM (npr. 2015-10).");
		}
		if (!validNumber(dto.getKwNT(), 0)) {
			errors.reject("Obavezno je uneti potrošene kW niže tarife.");
		}
		if (!validNumber(dto.getKwVT(), 0)) {
			errors.reject("Obavezno je uneti potrošene kW više tarife.");
		}
		if (!validNumber(dto.getCenaNT(), 2)) {
			errors.reject("Obavezno je uneti cenu niže tarife.");
		}
		if (!validNumber(dto.getCenaVT(), 2)) {
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
