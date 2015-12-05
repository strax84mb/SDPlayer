package rs.trznica.dragan.forms;

import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import rs.trznica.dragan.forms.support.ModalResult;

public class YesNoDialog extends JDialog {

	private static final long serialVersionUID = 3225294350430564700L;

	private ModalResult modalResult = ModalResult.NO;

	public ModalResult getModalResult() {
		return modalResult;
	}

	public YesNoDialog(String text){
		setResizable(false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setModal(true);
		Box box = Box.createVerticalBox();
		getContentPane().add(box);
		// Tekst
		JLabel label = new JLabel("<html>" + text + "</html>");
		label.setBorder(new EmptyBorder(10, 10, 10, 10));
		label.setAlignmentX(CENTER_ALIGNMENT);
		label.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		box.add(label);
		// Dugmici
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalGlue());
		JButton btn = new JButton("Ne");
		btn.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				modalResult = ModalResult.NO;
				setVisible(false);
			}
		});
		panel.add(btn);
		panel.add(Box.createHorizontalStrut(10));
		btn = new JButton("Da");
		btn.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				modalResult = ModalResult.YES;
				setVisible(false);
			}
		});
		panel.add(btn);
		panel.add(Box.createHorizontalGlue());
		panel.setAlignmentX(CENTER_ALIGNMENT);
		box.add(panel);
		box.add(Box.createVerticalStrut(10));
		pack();
	}

	public void showDialog(Component comp){
		int x = comp.getLocationOnScreen().x + comp.getWidth() + 10;
		int y = comp.getLocationOnScreen().y;
		if(x + getWidth() + 10 > Toolkit.getDefaultToolkit().getScreenSize().width)
			x = Toolkit.getDefaultToolkit().getScreenSize().width - getWidth() - 10;
		if(x < 10) x = 10;
		if(y + getHeight() + 10 > Toolkit.getDefaultToolkit().getScreenSize().height)
			y = Toolkit.getDefaultToolkit().getScreenSize().height - getHeight() - 10;
		if(y < 10) y = 10;
		setLocation(comp.getLocationOnScreen().x + comp.getWidth() + 10, comp.getLocationOnScreen().y);
		setVisible(true);
	}

	public void showDialogInCenter(Component comp){
		int x = comp.getLocationOnScreen().x + (comp.getWidth() / 2) - (getWidth() / 2);
		int y = comp.getLocationOnScreen().y + (comp.getHeight() / 2) - (getHeight() / 2);
		if(x + getWidth() + 10 > Toolkit.getDefaultToolkit().getScreenSize().width)
			x = Toolkit.getDefaultToolkit().getScreenSize().width - getWidth() - 10;
		if(x < 10) x = 10;
		if(y + getHeight() + 10 > Toolkit.getDefaultToolkit().getScreenSize().height)
			y = Toolkit.getDefaultToolkit().getScreenSize().height - getHeight() - 10;
		if(y < 10) y = 10;
		setLocation(comp.getLocationOnScreen().x + comp.getWidth() + 10, comp.getLocationOnScreen().y);
		setVisible(true);
	}

}
