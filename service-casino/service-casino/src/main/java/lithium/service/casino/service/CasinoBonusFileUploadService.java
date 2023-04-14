package lithium.service.casino.service;

import lithium.exceptions.ErrorCodeException;
import lithium.metrics.LithiumMetricsService;
import lithium.metrics.TimeThisMethod;
import lithium.service.casino.client.data.BonusAllocatev2;
import lithium.service.casino.data.entities.BonusFileRunDetail;
import lithium.service.casino.data.entities.BonusFileUpload;
import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.User;
import lithium.service.casino.data.repositories.BonusFileRunDetailRepository;
import lithium.service.casino.data.repositories.BonusFileUploadRepository;
import lithium.service.casino.data.repositories.UserRepository;
import lithium.service.casino.exceptions.Status412InvalidCustomFreeMoneyAmountException;
import lithium.service.casino.exceptions.Status421InvalidFileDataException;
import lithium.service.casino.exceptions.Status422InvalidParameterProvidedException;
import lithium.service.casino.exceptions.Status510GeneralCasinoExecutionException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.tokens.LithiumTokenUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CasinoBonusFileUploadService {

	protected static final String PLAYER_BONUS_HISTORY_ID = "player_bonus_history_id";
	
	private static final String SYSTEM_USER = "system";
	private static final String DEPOSIT = "CASHIER_DEPOSIT";
	private static final String BONUS_REVISION_ID = "bonus_revision_id";

	@Value("${lithium.service.casino.service.xprequiredforhourlybonus:5020}")
	@Setter
	private Long xpRequiredForHourlyBonus;

	@Setter @Autowired private LithiumMetricsService metrics;
	@Setter @Autowired private CasinoTriggerBonusService casinoTriggerBonusService;
	@Setter @Autowired private BonusFileUploadRepository bonusFileUploadRepository;
	@Setter @Autowired private BonusFileRunDetailRepository bonusFileRunDetailRepository;
	@Setter @Autowired private UserRepository userRepository;
	@Setter @Autowired private CasinoBonusService casinoBonusService;
	@Autowired LimitInternalSystemService limitInternalSystemService;

	@TimeThisMethod //Not sure this is going to be useful since it will be multiple bonuses so it will go over 1 second and error
	public Map<String, String> processTriggerBonusFileUpload(String bonusCode, Long revisionId, MultipartFile csvfile, LithiumTokenUtil util
	) throws Status421InvalidFileDataException, Status510GeneralCasinoExecutionException {
		//Lookup stuff
		User author = userRepository.save(userRepository.findOrCreateByGuid(util.guid(), () -> new User()));
		BonusRevision revision = null;
		if (revisionId != null) {
			revision = casinoBonusService.findBonusRevisionById(revisionId);
		}
		//Persist file
		byte[] csvFileData = null;
		try {
			csvFileData = csvfile.getBytes();
		} catch (IOException e) {
			log.error("Unable to read CSV file data for bonus allocation: " + bonusCode + " author: " + util.guid(),  e);
			throw new Status421InvalidFileDataException(e.getMessage());
		}
		BonusFileUpload bonusFileUpload = BonusFileUpload.builder()
				.author(author)
				.bonusRevision(revision)
				.file(csvFileData)
				.fileType(csvfile.getContentType())
				.size(csvfile.getSize())
				.build();
		bonusFileUpload = bonusFileUploadRepository.save(bonusFileUpload);

		//Run through the records and process
		List<List<String>> records = new ArrayList<>();
		Map<String, String> resultMap = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(csvFileData)))) {
			String line;
			while ((line = br.readLine()) != null) {
				//TODO: If there is a need, we can run these executions in parallel (see accounting summary job)
				if (line.trim().isEmpty()) continue;
				line = line.replaceAll("[^a-zA-Z\\/_0-9,\\.]", "");
				Double customFreeMoneyAmount = null;
				try {
					//Line format [playerguid], [optional custom amount]
					log.debug("CSV processing bonus allocation - bonus code:" + bonusCode + " line: " + line);
					String[] values = line.split(",");
					if (values.length >= 2) {
						try {
							customFreeMoneyAmount = Double.parseDouble(values[1]);
						} catch (NumberFormatException | NullPointerException e) {
							log.debug("Invalid custom free money amount for csv file: " + line);
							throw new Status412InvalidCustomFreeMoneyAmountException(values[1]);
						}
					}

					//Check if the user in the current can be granted a bonus
					limitInternalSystemService.checkPromotionsAllowed(values[0]);

					casinoTriggerBonusService.processTriggerOrTokenBonusWithCustomMoney(
							BonusAllocatev2.builder()
									.bonusCode(bonusCode)
									.playerGuid(values[0])
									.customAmountDecimal(customFreeMoneyAmount)
									.bonusRevisionId(revisionId)
									.build(), revision.getBonusType(), null);


					resultMap.put(values[0], "Success");
					bonusFileRunDetailRepository.save(BonusFileRunDetail.builder()
							.bonusFileUpload(bonusFileUpload)
							.lineData(line)
							.success(true)
							.build());
				} catch (ErrorCodeException ex) {
					log.warn("Unable to process line from bonus add CSV file: " + line);
					bonusFileRunDetailRepository.save(BonusFileRunDetail.builder()
							.bonusFileUpload(bonusFileUpload)
							.lineData(line)
							.success(false)
							.errorMessage(ex.getMessage())
							.build());
					resultMap.put(line, "failed: " + ex.getMessage());
					bonusFileUpload.setHadSomeErrors(true);
				}
			}
			return resultMap;
		} catch (Exception ex) {
			log.error("Unexpected error in csv file read: " + ex.getMessage(), ex);
			bonusFileUpload.setHadSomeErrors(true);
			throw new Status510GeneralCasinoExecutionException(ex.getMessage());
		} finally {
			bonusFileUpload.setComplete(true);
			bonusFileUpload.setCompletionDate(DateTime.now().toDate());
			bonusFileUploadRepository.save(bonusFileUpload);
		}
	}

	public Page<BonusFileUpload> findCsvFileList(DataTableRequest request, BonusRevision bonusRevision) {
		Page<BonusFileUpload> bonusRevisionList = bonusFileUploadRepository.findByBonusRevision(bonusRevision, request.getPageRequest());
		bonusRevisionList.forEach(data -> {
			data.setFile(null); //Reduce size footprint in data transfer
		});
		return bonusRevisionList;
	}


	public void getCsvFile(String bonusFileUploadId, HttpServletResponse response
	) throws Status421InvalidFileDataException,
			Status422InvalidParameterProvidedException,
			Status510GeneralCasinoExecutionException {
		OutputStream outputStream = null;

		try {
			outputStream = response.getOutputStream();
		} catch (IOException e) {
			log.error("Error in getting output stream on csv file download request: " + e.getMessage());
			throw new Status510GeneralCasinoExecutionException(e.getMessage());
		}
		Long fileId = null;
		try {
			fileId = Long.parseLong(bonusFileUploadId);
		} catch (NumberFormatException | NullPointerException e) {
			log.warn("Unable to parse provided csv id to long: " + bonusFileUploadId);
			throw new Status422InvalidParameterProvidedException("bonusFileUploadId="+bonusFileUploadId);
		}
		BonusFileUpload bonusFileUpload = bonusFileUploadRepository.findOne(fileId);
		if (bonusFileUpload == null) throw new Status421InvalidFileDataException("No matching csv id: " + bonusFileUploadId);

		String fileName =  bonusFileUpload.getBonusRevision().getBonusName()+"_"+bonusFileUpload.getCreationDate()+ ".csv";
		String mimeType = bonusFileUpload.getFileType();
		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("attachment; x-filename=\"" + fileName +"\""));
		response.setHeader("x-filename", String.format(fileName));
		Pageable pageRequest = PageRequest.of(0, 10);
		Page<BonusFileRunDetail> pageResult = null;

		do {
			pageResult = bonusFileRunDetailRepository.findByBonusFileUpload(bonusFileUpload, pageRequest);
			StringBuilder dataBuffer = new StringBuilder();
			for (BonusFileRunDetail result: pageResult.getContent()) {
				dataBuffer.append(result.getErrorMessage() != null ? result.getErrorMessage() : "Success");
				dataBuffer.append(",");
				dataBuffer.append(result.getLineData());
				dataBuffer.append("\r\n");
			}
			try {
				outputStream.write(dataBuffer.toString().getBytes());
			} catch (IOException | NullPointerException e) {
				log.warn("Unable to write csv bytes to output stream: " + e.getMessage());
				throw new Status510GeneralCasinoExecutionException(e.getMessage());
			}
			pageRequest = pageRequest.next();
		} while (!pageResult.isLast());
		try {
			outputStream.flush();
			outputStream.close();
		} catch (IOException | NullPointerException e) {
			log.warn("Unable to flush and close output stream: " + e.getMessage());
			throw new Status510GeneralCasinoExecutionException(e.getMessage());
		}
	}
}
