package org.caliog.myRPG.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.caliog.myRPG.Manager;
import org.caliog.myRPG.myConfig;
import org.caliog.myRPG.Resource.FileCreator;

public class DataFolder {

	public static void backup() throws IOException {
		int max = myConfig.getMaxBackups();
		File dir = new File(FilePath.backup);
		long a = dir.lastModified();
		File delete = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HHmmss");
		Date date = new Date();
		int counter = dir.listFiles().length + 1;
		while (dir.listFiles().length >= max) {
			counter--;
			if (counter <= 0) {
				Manager.plugin.getLogger().warning("Could not delete oldest backup folder!");
				break;
			}
			for (File f : dir.listFiles())
				if (f.lastModified() < a) {
					a = f.lastModified();
					delete = f;
				}

			if (delete != null)
				deleteDirectory(delete);

		}

		File srcFolder = new File(FilePath.data);
		File destFolder = new File(FilePath.backup + "Data " + dateFormat.format(date));
		if (srcFolder.exists()) {
			copyFolder(srcFolder, destFolder);
			Manager.plugin.getLogger().log(Level.INFO, "Created backup of all data files!");
		}
	}

	public static void copyFolder(File src, File dest) throws IOException {
		if (src.getName().equals(new File(FilePath.backup).getName()))
			return;
		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
			}
			String[] files = src.list();
			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				copyFolder(srcFile, destFile);
			}
		} else {
			copyFile(src, dest);
		}
	}

	public static void copyFile(File src, File dest) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dest);
		FileCreator.copyFile(in, out);
	}

	public static Runnable backupTask() {
		return new Runnable() {

			@Override
			public void run() {
				try {
					DataFolder.backup();
				} catch (IOException e) {
					Manager.plugin.getLogger().log(Level.WARNING, "Backup failed!");

				}
			}
		};
	}

	public static boolean deleteDirectory(File directory) throws IOException {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
		}
		return (directory.delete());
	}

}
