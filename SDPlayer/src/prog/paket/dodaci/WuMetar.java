package prog.paket.dodaci;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WuMetar extends JProgressBar {

	private static final long serialVersionUID = 4575847246140798285L;

	private JPanel grayBar;
	private JPanel greenBar;
	private JPanel yellowBar;
	private JPanel redBar;

	public WuMetar getThis(){
		return this;
	}

	public WuMetar(){
		setString("");
		setStringPainted(true);
		setOrientation(SwingConstants.VERTICAL);
		setOpaque(false);
		setLayout(null);
		grayBar = new JPanel();
		greenBar = new JPanel();
		yellowBar = new JPanel();
		redBar = new JPanel();
		grayBar.setBackground(Color.LIGHT_GRAY);
		greenBar.setBackground(Color.GREEN);
		yellowBar.setBackground(Color.YELLOW);
		redBar.setBackground(Color.RED);
		add(grayBar);
		add(greenBar);
		add(yellowBar);
		add(redBar);
		addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				int value = getValue() + 1;
				if(value <= 15){
					// Bojenje delimicno sive
					redBar.setVisible(false);
					yellowBar.setVisible(false);
					greenBar.setVisible(false);
					grayBar.setBounds(2, 100 - getValue() + 1, 12, 100);
				}else if(value <= 75){
					// Bojenje sive i delimicno zelene
					redBar.setVisible(false);
					yellowBar.setVisible(false);
					greenBar.setVisible(true);
					greenBar.setBounds(2, 100 - getValue() + 1, 12, 85);
					grayBar.setBounds(2, 86, 12, 100);
				}else if(value <= 92){
					// Bojenje sive, zelene i delimicno zute
					redBar.setVisible(false);
					yellowBar.setVisible(true);
					greenBar.setVisible(true);
					yellowBar.setBounds(2, 100 - getValue() + 1, 12, 86);
					greenBar.setBounds(2, 26, 12, 85);
					grayBar.setBounds(2, 86, 12, 100);
				}else{
					// Bojenje sive, zelene, zute i delimicno crvene
					redBar.setVisible(true);
					yellowBar.setVisible(true);
					greenBar.setVisible(true);
					redBar.setBounds(2, 100 - getValue() + 1, 12, 8);
					yellowBar.setBounds(2, 9, 12, 86);
					greenBar.setBounds(2, 26, 12, 85);
					grayBar.setBounds(2, 86, 12, 100);
				}
			}
		});
	}

}
