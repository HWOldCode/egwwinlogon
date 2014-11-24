/*
 * PS3 Media Server, for streaming any medias to your PS3.
 * Copyright (C) 2008  A.Brochard
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 2
 * of the License only.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package winapi.io;

import com.sun.jna.Platform;

import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process to create a platform specific communications pipe that provides
 * an input stream and output stream. Other processes can then transmit
 * content via this pipe.
 */
public class PipeProcess {
	private static final Logger logger = LoggerFactory.getLogger(PipeProcess.class);

	private String linuxPipeName;
	private WindowsNamedPipe mk;
	private boolean forcereconnect;

	public PipeProcess(String pipeName, String... extras) {
		this.forcereconnect = false;
		boolean in = true;

		if( extras != null && extras.length > 0 && extras[0].equals("out") ) {
			in = false;
		}

		if( extras != null ) {
			for( int i = 0; i < extras.length; i++ ) {
				if( extras[i].equals("reconnect") ) {
					this.forcereconnect = true;
				}
			}
		}

		if( Platform.isWindows() ) {
			this.mk = new WindowsNamedPipe(pipeName, this.forcereconnect, in);
		} else {
			this.linuxPipeName = getPipeName(pipeName);
		}
	}

	private static String getPipeName(String pipeName) {
		/*try {
			return configuration.getTempFolder() + "/" + pipeName;
		} catch (IOException e) {*/
			//logger.error("Pipe may not be in temporary directory", e);
			return pipeName;
		//}
	}

	public String getInputPipe() {
		if( !Platform.isWindows() ) {
			return this.linuxPipeName;
		}

		return this.mk.getPipeName();
	}

	public String getOutputPipe() {
		if( !Platform.isWindows() ) {
			return this.linuxPipeName;
		}

		return this.mk.getPipeName();
	}

	public ProcessWrapper getPipeProcess() {
		return this.mk;
	}

	public void deleteLater() {
		if( !Platform.isWindows() ) {
			File f = new File(this.linuxPipeName);
			f.deleteOnExit();
		}
	}

	public InputStream getInputStream() throws IOException {
		if( !Platform.isWindows() ) {
			//logger.trace("Opening file " + linuxPipeName + " for reading...");
			RandomAccessFile raf = new RandomAccessFile(this.linuxPipeName, "r");

			return new FileInputStream(raf.getFD());
		}

		return this.mk.getReadable();
	}

	public OutputStream getOutputStream() throws IOException {
		if( !Platform.isWindows() ) {
			//logger.trace("Opening file " + linuxPipeName + " for writing...");
			RandomAccessFile raf = new RandomAccessFile(this.linuxPipeName, "rw");

			return new FileOutputStream(raf.getFD());
		}

		return this.mk.getWritable();
	}
}
