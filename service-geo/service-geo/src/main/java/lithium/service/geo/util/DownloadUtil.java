package lithium.service.geo.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DownloadUtil {
	
	public static final int COMPRESSION_TYPE_NONE = 0;
	public static final int COMPRESSION_TYPE_ZIP = 1;
	public static final int COMPRESSION_TYPE_GZIP = 2;
	public static final int COMPRESSION_TYPE_TAR_GZIP = 3;

	public static boolean isTooOld(String local, long maxAgeInMillis) {
		log.debug("Checking age of " + local);
		File file = new File(local);
		if (file.exists()) {
			if (file.lastModified() > (new Date().getTime() - maxAgeInMillis)) {
				log.info(file + " exists and is not older than " + maxAgeInMillis + " milliseconds");

				return false;
			} else {
				log.info(file + " exists but is too old");
			}
		}

		return true;
	}

	public static boolean download(String url, String local, int compressionType) throws Exception {
		File file = new File(local);
		File tmpFile = new File(local + ".download");
		if (tmpFile.exists()) tmpFile.delete();

		log.info("Downloading from " + url + " into " + tmpFile);
		FileUtils.copyURLToFile(new URL(url), tmpFile);
		log.info("Downloaded from " + url + " into " + tmpFile);

		if (compressionType == COMPRESSION_TYPE_ZIP) return extractZip(tmpFile.getPath(), local);
		if (compressionType == COMPRESSION_TYPE_GZIP) return extractGzip(tmpFile.getPath(), local);
		if (compressionType== COMPRESSION_TYPE_TAR_GZIP) return extractTarGzip(tmpFile.getPath(), local);

		if (file.exists()) file.delete();

		FileUtils.moveFile(tmpFile, file);

		return true;
	}

	public static boolean cleanUp(String local) {
		log.debug("Cleaning up " + local);
		File file = new File(local);

		if (file.exists()) file.delete();

		return true;
	}

	public static boolean extractTarGzip(String from, String to) throws Exception {
		File file = new File(to);
		File tmpFile = new File(from);
		File tmpFileUncompressed = new File(file + ".uncompressed");

		FileInputStream fis = new FileInputStream(tmpFile);
		GZIPInputStream gzipInputStream = new GZIPInputStream(new BufferedInputStream(fis));

		//Due to the new increment process - we now use an underscore
		String dbName = to.substring(to.lastIndexOf("_") + 1);

		try (TarArchiveInputStream tis = new TarArchiveInputStream(gzipInputStream)) {
			TarArchiveEntry tarEntry;

			while ((tarEntry = tis.getNextTarEntry()) != null) {

				if (tarEntry.isDirectory() || !tarEntry.getName().contains(dbName)) continue;

				FileOutputStream fos = new FileOutputStream(tmpFileUncompressed);
				IOUtils.copy(tis, fos);
				fos.close();

				if (file.exists()) file.delete();

				FileUtils.copyFile(tmpFileUncompressed, file);
			}
		}

		//Clean up after ourselves
		if (tmpFile.exists()) tmpFile.delete();
		if(tmpFileUncompressed.exists()) tmpFileUncompressed.delete();

		return true;
	}

	public static boolean extractZip(String from, String to) throws Exception {
		File tmpFile = new File(from);

		if (!tmpFile.exists()) {
			log.error(tmpFile + " does not exist");
		}
		
		log.info("Extracting " + tmpFile);
		
		byte[] buffer = new byte[1024];
		
		ZipInputStream zis = new ZipInputStream(new FileInputStream(tmpFile));
		try {
			ZipEntry z = zis.getNextEntry(); 
			if (z == null) {
				log.error("Was expecting a zip entry from " + tmpFile);
				return false;
			}
			File outFile = new File(to);
			if (outFile.exists()) {
				log.info(outFile + " exists. Deleting.");
				outFile.delete();
			}
			
			log.info("Storing " + z.getName() + " from " + tmpFile + " into " + outFile);
			FileOutputStream fos = new FileOutputStream(outFile);
			
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
	
			fos.close();
		} finally {
			zis.close();
		}

		return true;
	}
	
	public static boolean extractGzip(String from, String to) throws Exception {
		File tmpFile = new File(from);
		File tmpFileUncompressed = new File(to + ".uncompressed");
		File file = new File(to);
		GZIPInputStream gis = new GZIPInputStream(new FileInputStream(tmpFile));
		FileUtils.copyInputStreamToFile(gis, tmpFileUncompressed);
		if (file.exists()) file.delete();
		FileUtils.moveFile(tmpFileUncompressed, file);
		tmpFile.delete();
		return true;
	}
	
}
