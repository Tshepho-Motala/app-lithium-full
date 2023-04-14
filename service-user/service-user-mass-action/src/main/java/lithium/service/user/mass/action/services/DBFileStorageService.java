package lithium.service.user.mass.action.services;

import lithium.service.user.mass.action.data.entities.DBFile;
import lithium.service.user.mass.action.data.repositories.DBFileRepository;
import lithium.service.user.mass.action.exceptions.Status400FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class DBFileStorageService {

    @Autowired
    private DBFileRepository dbFileRepository;

    public DBFile storeFile(MultipartFile file) throws Status400FileStorageException {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new Status400FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            DBFile dbFile = new DBFile(fileName, file.getContentType(), file.getBytes());

            return dbFileRepository.save(dbFile);
        } catch (IOException | Status400FileStorageException ex) {
            throw new Status400FileStorageException("Could not store file " + fileName + ". Please try again!" + ex.getMessage());
        }
    }
}