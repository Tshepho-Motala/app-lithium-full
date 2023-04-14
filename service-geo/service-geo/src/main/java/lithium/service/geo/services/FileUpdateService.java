package lithium.service.geo.services;

import java.io.File;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.geo.data.entities.FileUpdate;
import lithium.service.geo.data.repositories.FileUpdateRepository;

@Service
public class FileUpdateService {

	@Autowired FileUpdateRepository fileUpdateRepository;

	public boolean hasChanged(String name, File file) {
		Date lastModified = new Date(file.lastModified());
		
		FileUpdate fileUpdate = fileUpdateRepository.findByName(name);
		
		if (fileUpdate == null) {	
			fileUpdate = FileUpdate.builder()
					.updateDate(lastModified)
					.name(name)
					.build();
		} else if (fileUpdate.getUpdateDate().compareTo(lastModified) >= 0) {
			return false;
		}
		
		fileUpdate.setUpdateDate(lastModified);
		fileUpdateRepository.save(fileUpdate);
		
		return true;
	}
}
