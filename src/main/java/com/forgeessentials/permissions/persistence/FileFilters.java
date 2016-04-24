package com.forgeessentials.permissions.persistence;

import java.io.File;
import java.io.FileFilter;

public final class FileFilters {

	public static class Directory implements FileFilter {
		@Override
		public boolean accept(File file) {
			return file.isDirectory();
		}
	}

	public static class Extension implements FileFilter {

		private String ext;

		public Extension(String ext) {
			this.ext = ext.toLowerCase();
		}

		@Override
		public boolean accept(File file) {
			return !file.isDirectory() && file.getName().toLowerCase().endsWith(ext);
		}
	}

	private FileFilters() {
	}

}
