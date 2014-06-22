package prog.paket.playlist.generator.struct;

import java.io.File;
import java.io.FileFilter;

public class RekFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		if(pathname.getName().endsWith(".rek"))
			return true;
		else return false;
	}

}
