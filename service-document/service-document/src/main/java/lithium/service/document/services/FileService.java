package lithium.service.document.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import lithium.service.document.data.entities.DocumentFile;
import lithium.service.document.data.repositories.DocumentFileRepository;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.document.data.entities.File;
import lithium.service.document.data.repositories.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;

@Slf4j
@Service
public class FileService {
	@Autowired
	private FileRepository fileRepo;

	@Autowired
	private DocumentFileRepository documentFileRepository;
	
	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	public File saveFile(byte[] data, String mimeType, String name) {

		String md5Hash = calculateMD5(data);

		//Find graphic if it is the same
		Iterable<File> graphics = fileRepo.findBySizeAndMd5Hash(data.length, md5Hash);

		File fileData = null;
		for (File tmpGraphic: graphics) {
			if (Arrays.equals(tmpGraphic.getData(), data)) {
				fileData = tmpGraphic;
			}
		}
		//No matching graphics, make new one
		if(fileData == null) {

			fileData = File.builder()
					.data(data)
					.size(data.length)
					.md5Hash(md5Hash)
					.mimeType(mimeType)
					.name(name)
					.build();
			fileData = fileRepo.save(fileData);
		} else if (!Objects.equals(fileData.getMimeType(), mimeType)) {
			fileData.setMimeType(mimeType);
			fileData = fileRepo.save(fileData);

		}
		

		return fileData;
	}
	
	public boolean isGraphicContentEqual(File g, byte[] data) {

		if(g.getSize() != data.length) return false;
		
		//String md5Hash = calculateMD5(data);
		if(g.getMd5Hash() != calculateMD5(data)) return false;
		
		if(!Arrays.equals(g.getData(), data)) return false;
		
		return true;

	}
	
	public String calculateMD5(byte[] data) {
		int maxMd5DataSize = 50 * 1024; //50kb
		byte[] md5CalcData = ArrayUtils.subarray(data, 0, data.length < maxMd5DataSize ? data.length : maxMd5DataSize);

		return Hex.encodeHexString(Md5Crypt.md5Crypt(md5CalcData, "$1$Document").getBytes());
	}

	public List<DocumentFile> findByDocumentIdAndDeletedFalse(long documentId) {
		try {
			return documentFileRepository.findByDocumentIdAndDeletedFalse(documentId);
		} catch (Exception e) {
			log.error("Wile try to find DocumentFile by DocId and DeletedFalse got an error:" + e.getMessage());
			return new ArrayList<>();
		}
	}
}
