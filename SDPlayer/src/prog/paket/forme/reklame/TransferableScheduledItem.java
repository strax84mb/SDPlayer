package prog.paket.forme.reklame;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;


public class TransferableScheduledItem implements Transferable {

	public static DataFlavor scheduledItemListFlavor = new DataFlavor(ScheduledItem.class, "Scheduled item");

	protected static DataFlavor[] supportedFlavors = {scheduledItemListFlavor};

	int[] indexList;

	public TransferableScheduledItem(int[] indexList){
		this.indexList = indexList;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return indexList;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if(flavor.equals(scheduledItemListFlavor))
			return true;
		else return false;
	}

}
