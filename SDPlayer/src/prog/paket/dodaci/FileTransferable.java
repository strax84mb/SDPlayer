package prog.paket.dodaci;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileTransferable implements Transferable {

	private DataFlavor flavors[] = new DataFlavor[]{DataFlavor.javaFileListFlavor};

	private List<File> list;

	public FileTransferable(List<File> list){
		this.list = list;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if(isDataFlavorSupported(flavor))
			return list;
		else return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.javaFileListFlavor.equals(flavor);
	}

}
