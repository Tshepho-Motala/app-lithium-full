package lithium.tokens;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IO {
	public static String compressString(String srcTxt) throws IOException {
		log.debug(srcTxt);
		ByteArrayOutputStream rstBao = new ByteArrayOutputStream();
		GZIPOutputStream zos = new GZIPOutputStream(rstBao);
		zos.write(srcTxt.getBytes());
		IOUtils.closeQuietly(zos);
		
		byte[] bytes = rstBao.toByteArray();
		log.debug(new String(bytes));
		String encoded = Base64.encodeBase64String(bytes);
		log.debug(encoded);
		return encoded;
	}
	
	public static String uncompressString(String zippedBase64Str) throws IOException {
		log.debug(zippedBase64Str);
		String result = null;
		byte[] bytes = Base64.decodeBase64(zippedBase64Str);
		GZIPInputStream zi = null;
		try {
			zi = new GZIPInputStream(new ByteArrayInputStream(bytes));
			result = IOUtils.toString(zi);
		} finally {
			IOUtils.closeQuietly(zi);
		}
		return result;
	}
}