package prog.paket.playlist.generator.struct;

import java.io.File;

public class FileItem {

	private File file;

	public File getFile(){
		return file;
	}

	public FileItem(){}

	public FileItem(File file){
		this.file = file;
	}

	@Override
	public String toString() {
		if(file == null) return "";
		return file.getName();
	}

}
