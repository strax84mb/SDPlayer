package prog.paket.mp3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Mp3InputStream extends InputStream {

	private AudioInputStream in = null;
	private AudioInputStream din = null;
	private AudioFormat decodedFormat = null;

	public Mp3InputStream(String filename) throws UnsupportedAudioFileException, IOException{
		this(new File(filename));
	}

	public Mp3InputStream(File file) throws UnsupportedAudioFileException, IOException{
		in = AudioSystem.getAudioInputStream(file);
		din = null;
		AudioFormat baseFormat = in.getFormat();
		decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
		                                            baseFormat.getSampleRate(),
		                                            //80000.0f,
		                                            16,
		                                            baseFormat.getChannels(),
		                                            baseFormat.getChannels() * 2,
		                                            baseFormat.getSampleRate(),
		                                            //80000.0f,
		                                            false);
		din = AudioSystem.getAudioInputStream(decodedFormat, in);
	}

	public AudioInputStream getStream(){
		return din;
	}

	public AudioFormat getDecodedFormat(){
		return decodedFormat;
	}

	@Override
	public int available() throws IOException {
		return din.available();
	}

	@Override
	public void close() throws IOException {
		din.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		din.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return din.markSupported();
	}

	@Override
	public int read() throws IOException {
		return din.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return din.read(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return din.read(b);
	}

	@Override
	public synchronized void reset() throws IOException {
		din.reset();
	}

	@Override
	public long skip(long n) throws IOException {
		return din.skip(n);
	}

	public void dispose() throws IOException{
		in.close();
		din.close();
	}

}
