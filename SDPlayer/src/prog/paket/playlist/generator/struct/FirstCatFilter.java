package prog.paket.playlist.generator.struct;

import java.io.File;
import java.io.FileFilter;

public class FirstCatFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		if(pathname.getName().endsWith("-1.sec"))
			return true;
		else return false;
	}

}
