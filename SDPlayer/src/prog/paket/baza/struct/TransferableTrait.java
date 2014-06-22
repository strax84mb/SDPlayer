package prog.paket.baza.struct;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TransferableTrait implements Transferable {

	protected static DataFlavor traitMenuFlavor = new DataFlavor(TraitMenu.class, "SongTraitMenu");

	protected static DataFlavor[] supportedFlavors = {traitMenuFlavor};

	TraitMenu trait;

	public TransferableTrait(TraitMenu trait){
		this.trait = trait;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return trait;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if(flavor.equals(traitMenuFlavor))
			return true;
		else return false;
	}

}
