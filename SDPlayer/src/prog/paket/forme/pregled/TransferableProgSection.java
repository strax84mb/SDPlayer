package prog.paket.forme.pregled;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import prog.paket.forme.reklame.ScheduledItem;
import prog.paket.playlist.generator.struct.ProgSection;

public class TransferableProgSection implements Transferable {

	public static DataFlavor progSectionFlavor = new DataFlavor(ScheduledItem.class, "Scheduled item");

	protected static DataFlavor[] supportedFlavors = {progSectionFlavor};

	private ProgSection section;

	public TransferableProgSection(ProgSection section){
		this.section = section;
	}

	@Override
	public Object getTransferData(DataFlavor arg0)
			throws UnsupportedFlavorException, IOException {
		return section;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if(flavor.equals(progSectionFlavor))
			return true;
		else return false;
	}

}
