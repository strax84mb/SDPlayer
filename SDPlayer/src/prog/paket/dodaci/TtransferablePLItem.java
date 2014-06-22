package prog.paket.dodaci;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TtransferablePLItem implements Transferable {

	public static DataFlavor plItemsFlavor = new DataFlavor(ListJItem.class, "Playlist item");

	protected static DataFlavor[] supportedFlavors = {plItemsFlavor};

	private ListJItem items[];

	public TtransferablePLItem(ListJItem items[]){}
	@Override
	public Object getTransferData(DataFlavor arg0)
			throws UnsupportedFlavorException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
