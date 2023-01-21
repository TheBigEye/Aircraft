package minicraft.core.io;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.saveload.Save;

public class FileHandler extends Game {
	private FileHandler() {}

	public static final int REPLACE_EXISTING = 0;
	public static final int RENAME_COPY = 1;
	public static final int SKIP = 2;

	private static final String OS = System.getProperty("os.name").toLowerCase();
	private static final String localGameDir;
	private static final String systemGameDir;
	private static final String systemTempDir;

	static {
		String local = "playminicraft/mods/Aircraft";

		if (OS.contains("windows")) { // Windows filesystem
			systemGameDir = System.getenv("APPDATA");
			systemTempDir = System.getenv("TEMP");

		} else {
			systemGameDir = System.getProperty("user.home");
			systemTempDir = System.getProperty("java.io.tmpdir");
			if (!OS.contains("mac")) { // Linux and Mac filesystem
				local = "." + local;
			}
		}

		localGameDir = "/" + local;

		if (Game.debug) {
			Logger.debug("OS name: {}", OS);
			Logger.debug("System game dir: {}", systemGameDir);
			Logger.debug("System temp dir: {}", systemTempDir);
		}
	}

	public static String getSystemGameDir() {
		return systemGameDir;
	}

	public static String getLocalGameDir() {
		return localGameDir;
	}

	public static void determineGameDir(String saveDir) {
		gameDir = saveDir + localGameDir;

		if (debug) {
			Logger.debug("Determined game directory: {}",  gameDir);
		}

		File testFile = new File(gameDir);
		testFile.mkdirs();

		File oldFolder = new File(saveDir + "/.playminicraft/mods/Aircraft");
		if (oldFolder.exists() && !oldFolder.equals(testFile)) {
			try {
				copyFolderContents(oldFolder.toPath(), testFile.toPath(), RENAME_COPY, true);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}

		if (OS.contains("mac")) {
			oldFolder = new File(saveDir + "/.playminicraft");
			if (oldFolder.exists()) {
				try {
					copyFolderContents(oldFolder.toPath(), testFile.toPath(), RENAME_COPY, true);
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	private static void deleteFolder(File top) {
		if (top == null) {
			return;
		}
		if (top.isDirectory()) {
			File[] subfiles = top.listFiles();

			if (subfiles != null) {
				for (File subfile : subfiles) {
					deleteFolder(subfile);
				}
			}
		}
		top.delete();
	}

	public static void copyFolderContents(Path origFolder, Path newFolder, int ifExisting, boolean deleteOriginal) throws IOException {

		// I can determine the local folder structure with origFolder.relativize(file), then use newFolder.resolve(relative).
		Logger.info("Copying contents of folder {} to new folder {} ...", origFolder, newFolder);

		Files.walkFileTree(origFolder, new FileVisitor<Path>() {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
				String newFilename = newFolder.resolve(origFolder.relativize(file)).toString();

				if (new File(newFilename).exists()) {
					if (ifExisting == SKIP) {
						return FileVisitResult.CONTINUE;

					} else if (ifExisting == RENAME_COPY) {
						newFilename = newFilename.substring(0, newFilename.lastIndexOf("."));
						StringBuilder newFilenameBuilder = new StringBuilder(newFilename);
						do {
						    newFilenameBuilder.append("(Old)");
						    newFilename = newFilenameBuilder.toString();
						} while (new File(newFilename).exists());
						newFilename += Save.extension;
					}
				}

				Path newFile = new File(newFilename).toPath();

				if (Game.debug) {
					System.out.println("Visiting file " + file + "; translating to " + newFile);
				}

				try {
					Files.copy(file, newFile, StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return FileVisitResult.CONTINUE;
			}

			public FileVisitResult preVisitDirectory(Path p, BasicFileAttributes bfa) {
				return FileVisitResult.CONTINUE;
			}

			public FileVisitResult postVisitDirectory(Path p, IOException ex) {
				return FileVisitResult.CONTINUE;
			}

			public FileVisitResult visitFileFailed(Path p, IOException ex) {
				return FileVisitResult.CONTINUE;
			}
		});

		if (deleteOriginal) {
			deleteFolder(origFolder.toFile());
		}
	}
	
	
	/**
	 * Remove all the natives files downloaded
	 */
	public static void cleanNativesFiles() {
	    // Remove these native libraries
	    String[] folderNames = {
	    		"java-discord"
	    };
	    
	    File tempDirFile = new File(systemTempDir);
	    File[] nativeFolders = tempDirFile.listFiles(new FilenameFilter() {
	        @Override
	        public boolean accept(File dir, String name) {
	            for (String folderName : folderNames) {
	                if (name.startsWith(folderName)) {
	                    return true;
	                }
	            }
	            return false;
	        }
	    });

	    // Delete the native folders found
	    for (File nativeFolder : nativeFolders) {
	        deleteFolder(nativeFolder);
	    }
	}
}