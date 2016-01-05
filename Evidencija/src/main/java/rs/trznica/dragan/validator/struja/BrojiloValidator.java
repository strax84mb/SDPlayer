package rs.trznica.dragan.validator.struja;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import rs.trznica.dragan.entities.struja.Brojilo;

public class BrojiloValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Brojilo.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		if (!supports(obj.getClass())) {
			errors.reject("Pogrešan objekat u kodu!");
			return;
		}
		Brojilo brojilo = (Brojilo) obj;
		if (StringUtils.isEmpty(brojilo.getBroj())) {
			errors.reject("Broj brojila je obavezan za vozilo.");
		}
		if (StringUtils.isEmpty(brojilo.getEd())) {
			errors.reject("ED brojila je obavezan za vozilo.");
		}
	}

}
