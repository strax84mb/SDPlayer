package rs.trznica.dragan.forms.support;

import javax.swing.JCheckBox;

import rs.trznica.dragan.entities.tankovanje.Potrosac;

public class ConsumerCheckBox extends JCheckBox {

	private static final long serialVersionUID = -4531845164011779431L;

	private Potrosac consumer;

	public ConsumerCheckBox(Potrosac consumer) {
		super(consumer.toString());
		this.consumer = consumer;
	}

	public Potrosac getConsumer() {
		return consumer;
	}
}
