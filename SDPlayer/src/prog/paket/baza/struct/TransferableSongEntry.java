package prog.paket.baza.struct;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TransferableSongEntry implements Transferable {

	public static DataFlavor songEntryFlavor = new DataFlavor(SongEntry.class, "Song entry");

	protected static DataFlavor[] supportedFlavors = {songEntryFlavor};

	SongEntry[] entries;

	public TransferableSongEntry(SongEntry entries[]){
		this.entries = entries;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return entries;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if(flavor.equals(songEntryFlavor))
			return true;
		else return false;
	}

}
