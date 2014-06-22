package prog.paket.dodaci;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TransferableListJItem implements Transferable {

	public static DataFlavor jItemListFlavor = new DataFlavor(ListJItem.class, "List J. item");

	protected static DataFlavor[] supportedFlavors = {jItemListFlavor};

	private PLItemsPackage pack;

	public TransferableListJItem(int[] indexList, JPLayList list){
		pack = new PLItemsPackage(indexList, list);
	}

	public TransferableListJItem(PLItemsPackage pack){
		this.pack = pack;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if(flavor.equals(jItemListFlavor))
			return true;
		else return false;
	}

	@Override
	public Object getTransferData(DataFlavor arg0)
			throws UnsupportedFlavorException, IOException {
		return pack;
	}

}
