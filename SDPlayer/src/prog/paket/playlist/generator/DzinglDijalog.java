package prog.paket.playlist.generator;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import prog.paket.dodaci.ContentEvent;
import prog.paket.dodaci.ContentListener;
import prog.paket.dodaci.DzinglButton;
import prog.paket.dodaci.ListJItem;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import prog.paket.dodaci.JPLayList;

public class DzinglDijalog extends JFrame implements ActionListener {

	private static final long serialVersionUID = -6315124667901851180L;

	private JPanel contentPane;
	public JPanel dzinglPanel;
	public JPanel volPanel;
	public Component horizontalGlue;
	public Component horizontalGlue_1;
	public JLabel lblJacinaMaske;
	public JSlider volSlider;
	public Component horizontalStrut;
	public Component horizontalStrut_1;
	public JLabel lblPercent;
	public JScrollPane scrollPane;
	public JPLayList idDzinglList;

	private void saveButtonInfo(BufferedWriter writer, ListJItem item) throws Exception {
		if(item == null){
			writer.write("null");
		}else{
			writer.write(String.valueOf(item.duration));
			writer.newLine();
			writer.write(item.fileName);
			writer.newLine();
			writer.write(item.fullPath);
		}
		writer.newLine();
	}

	public void saveBindings(){
		try{
			FileWriter fw = new FileWriter("dzingl.conf");
			BufferedWriter writer = new BufferedWriter(fw);
			for(int i=0;i<40;i++){
				saveButtonInfo(writer, ((DzinglButton)dzinglPanel.getComponent(i)).getSoundFile());
			}
			writer.close();
			fw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private ListJItem loadButtonInfo(BufferedReader reader) throws Exception {
		String temp = reader.readLine();
		if(temp.equals("null")) return null;
		ListJItem ret = new ListJItem();
		ret.duration = Long.parseLong(temp);
		ret.fileName = reader.readLine();
		ret.fullPath = reader.readLine();
		ret.crossfade = false;
		return ret;
	}

	private void loadBindings(){
		try{
			FileReader fr = new FileReader("dzingl.conf");
			BufferedReader reader = new BufferedReader(fr);
			for(int i=0;i<40;i++){
				try{
					((DzinglButton)dzinglPanel.getComponent(i)).setSoundFile(
							(ListJItem)loadButtonInfo(reader));
				}catch(EOFException eofe){
					break;
				}
			}
			reader.close();
			fr.close();
		}catch(Exception e){}
	}

	private JPanel makeButtonPanel(DzinglTransferHandler trHandler){
		JPanel ret = new JPanel(new GridLayout(8, 5, 0, 0));
		DzinglButton button;
		for(int i=0;i<40;i++){
			button = new DzinglButton();
			ret.add(button);
			button.setText("Mesto za masku");
			button.setTransferHandler(trHandler);
			button.setMinimumSize(new Dimension(55, 35));
		}
		return ret;
	}

	/**
	 * Create the frame.
	 */
	public DzinglDijalog() {
		addComponentListener(new ThisComponentListener());
		setBounds(100, 100, 630, 459);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		DzinglTransferHandler trHandler = new DzinglTransferHandler();
		contentPane.setLayout(new BorderLayout(0, 0));
		
		dzinglPanel = makeButtonPanel(trHandler);
		contentPane.add(new JScrollPane(dzinglPanel));
		
		volPanel = new JPanel();
		volPanel.setBorder(new EmptyBorder(5, 0, 10, 0));
		contentPane.add(volPanel, BorderLayout.NORTH);
		volPanel.setLayout(new BoxLayout(volPanel, BoxLayout.X_AXIS));
		
		horizontalGlue = Box.createHorizontalGlue();
		volPanel.add(horizontalGlue);
		
		lblJacinaMaske = new JLabel("Ja\u010Dina maske");
		volPanel.add(lblJacinaMaske);
		
		horizontalStrut = Box.createHorizontalStrut(10);
		volPanel.add(horizontalStrut);
		
		volSlider = new JSlider();
		volSlider.setMinimum(40);
		volSlider.setMaximum(99);
		volSlider.setValue(70);
		volSlider.addChangeListener(new SliderChangeListener());
		volPanel.add(volSlider);
		
		horizontalStrut_1 = Box.createHorizontalStrut(5);
		volPanel.add(horizontalStrut_1);
		
		lblPercent = new JLabel("New label");
		volPanel.add(lblPercent);
		
		horizontalGlue_1 = Box.createHorizontalGlue();
		volPanel.add(horizontalGlue_1);
		
		scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(6, 150));
		scrollPane.setMinimumSize(new Dimension(25, 150));
		contentPane.add(scrollPane, BorderLayout.SOUTH);
		
		idDzinglList = new JPLayList("idDzinglList");
		idDzinglList.addContentListener(new IDListContentListener());
		scrollPane.setViewportView(idDzinglList);
		
		for(int i=0;i<40;i++)
			((DzinglButton)dzinglPanel.getComponent(i)).addActionListener(this);
		
		loadBindings();
	}

	public void loadIDJingles(){
		try{
			idDzinglList.getListModel().removeAllElements();
			FileInputStream fis = new FileInputStream("id_dzingl.conf");
			ObjectInputStream ois = new ObjectInputStream(fis);
			int len = ois.readInt();
			for(int i=0;i<len;i++){
				ListJItem item = new ListJItem(ois.readUTF());
				idDzinglList.getListModel().addElement(item);
			}
			ois.close();
			fis.close();
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}

	private void saveIDJingles(){
		try{
			DefaultListModel<ListJItem> model = idDzinglList.getListModel();
			int len = model.size();
			FileOutputStream fos = new FileOutputStream("id_dzingl.conf", false);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeInt(len);
			for(int i=0;i<len;i++)
				oos.writeUTF(model.getElementAt(i).fullPath);
			oos.close();
			fos.close();
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}

	private class DzinglTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 2904988127046660653L;

		@Override
		public boolean canImport(TransferSupport support) {
			if(!support.isDrop()) return false;
			return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
		}

		@Override
		public boolean importData(TransferSupport support) {
			try{
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>)support.getTransferable().getTransferData(
						DataFlavor.javaFileListFlavor);
				DzinglButton dzingl = (DzinglButton)support.getComponent();
				dzingl.setSoundFile(files.get(0));
				saveBindings();
				return true;
			}catch(Exception e){
				return false;
			}
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DzinglButton dzingl = (DzinglButton)e.getSource();
		if(dzingl.getSoundFile() == null) return;
		PlayerWin.getInstance().playSFX(dzingl.getSoundFile().fullPath);
	}

	private class SliderChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
			lblPercent.setText(String.valueOf(volSlider.getValue()) + " %");
			if(!volSlider.getValueIsAdjusting()){
				double val = volSlider.getValue();
				val /= 100.0;
				PlayerWin.getInstance().player.createDzingleFadeFun(val);
			}
		}
	}
	private class ThisComponentListener extends ComponentAdapter {
		@Override
		public void componentShown(ComponentEvent ce) {
			lblPercent.setText(String.valueOf(volSlider.getValue()) + " %");
			if(idDzinglList.getListModel().size() == 0) loadIDJingles();
		}
	}
	private class IDListContentListener implements ContentListener {
		@Override
		public void contentChanged(ContentEvent event) {
			saveIDJingles();
		}
	}
}
