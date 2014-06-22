package prog.paket.playlist.generator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

public class GenerationReportDlg extends JFrame {

	private static final long serialVersionUID = -548917198841085205L;

	/**
	 * Create the dialog.
	 */
	public GenerationReportDlg(String text) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 550, 300);
		getContentPane().setLayout(new BorderLayout());
		{
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			{
				JLabel lblNewLabel = new JLabel(text);
				lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
				lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
				scrollPane.setViewportView(lblNewLabel);
			}
		}
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((size.width/2)-(getSize().width/2), (size.height/2)-(getSize().height/2));
		setVisible(true);
	}

}
