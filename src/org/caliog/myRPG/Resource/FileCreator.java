package org.caliog.myRPG.Resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class FileCreator {

	public void copyFile(String dest, String src) throws FileNotFoundException, IOException {
		File destination = new File(dest);
		URL url = getClass().getResource(src);
		if (destination == null || url == null || !destination.exists())
			return;

		copyFile(url.openStream(), new FileOutputStream(dest));

	}

	public static void copyFile(InputStream in, OutputStream out) throws IOException {

		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
		in.close();
		out.close();
	}

	public static void copyURL(final File dest, final String src) throws IOException {
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(new URL(src).openStream());
			fout = new FileOutputStream(dest.getAbsolutePath());

			final byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				fout.write(data, 0, count);
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (fout != null) {
				fout.close();
			}
		}

	}
}
