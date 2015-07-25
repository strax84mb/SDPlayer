package rs.trznica.dragan.forms;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.springframework.stereotype.Component;

@Component
public class ApplicationFrame extends JFrame {

	private static final long serialVersionUID = 2278856270790456663L;

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public ApplicationFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}

}
