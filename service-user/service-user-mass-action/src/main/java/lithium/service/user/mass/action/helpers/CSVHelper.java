package lithium.service.user.mass.action.helpers;

import lithium.service.user.mass.action.data.entities.FileData;
import lithium.service.user.mass.action.exceptions.Status422DataValidationError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class CSVHelper {

    private static boolean isCSV(MultipartFile file) throws IOException {
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        final Optional<String> first = fileReader.lines().skip(1).findFirst();
        if (!first.isPresent()) return true;
        final String s = first.get();
        return !s.contains(";");// The file might also just have a single column;
    }

    public static int validateCSV(MultipartFile file) throws Status422DataValidationError, IOException {
        return fileToFileData(file).size();
    }

    public static List<FileData> fileToFileData(MultipartFile file) throws Status422DataValidationError, IOException {
        if (isCSV(file)) {
            return csvToFileData(file.getInputStream());
        }
        return scsvToFileData(file.getInputStream());
    }

    public static List<FileData> csvToFileData(InputStream is) throws Status422DataValidationError {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            List<FileData> fileDataList = new ArrayList<>();

            Set<Long> bucket = new HashSet<>();
            long recordNumber = 0;
            for (CSVRecord csvRecord : csvRecords) {
                if (csvRecord.size() == 0) { continue;}
                if (csvRecord.get(0) != null && !csvRecord.get(0).isEmpty()) {
                    if (csvRecord.get(0).contains(";")) throw new Exception("Not CSV, checking SCSV");
                    long playerId = Long.parseLong(csvRecord.get(0));
                    double amount = (csvRecord.size() >= 2 && csvRecord.get(1) != null && !csvRecord.get(1).isEmpty()) ? Double.parseDouble(csvRecord.get(1)) : 0; //If amount is not set, default to 0 (default is applied in job)
                    FileData fileData = FileData.builder()
                            .rowNumber(++recordNumber)
                            .uploadedPlayerId(playerId)
                            .amount(amount)
                            .duplicate(isDuplicatePlayer(bucket, playerId))
                            .build();
                    fileDataList.add(fileData);
                }
            }

            return fileDataList;
        } catch (Exception e) {
            log.debug("Failed to parse CSV file with comma as a delimiter: " + e.getMessage());
            throw new Status422DataValidationError("Failed to parse CSV file with comma as a delimiter: " + e.getMessage());
        }
    }

    public static List<FileData> scsvToFileData(InputStream is) throws Status422DataValidationError {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim().withAllowMissingColumnNames());
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            List<FileData> fileDataList = new ArrayList<>();

            Set<Long> bucket = new HashSet<>();
            long recordNumber = 0;
            for (CSVRecord csvRecord : csvRecords) {
                if(csvRecord.size() == 0) { continue;}
                if(csvRecord.get(0) != null && !csvRecord.get(0).isEmpty()) {
                    long playerId = Long.parseLong(csvRecord.get(0));
                    double amount = (csvRecord.size() >= 2 && csvRecord.get(1) != null && !csvRecord.get(1).isEmpty()) ? Double.parseDouble(csvRecord.get(1)) : 0; //If amount is not set, default to 0 (default is applied in job)
                    FileData fileData = FileData.builder()
                            .rowNumber(++recordNumber)
                            .uploadedPlayerId(playerId)
                            .amount(amount)
                            .duplicate(isDuplicatePlayer(bucket, playerId))
                            .build();
                    fileDataList.add(fileData);
                }
            }

            return fileDataList;
        } catch (Exception e) {
            log.debug("Failed to parse CSV file with semicolon as a delimiter: " + e.getMessage());
            throw new Status422DataValidationError("Failed to parse CSV file with semicolon as a delimiter: " + e.getMessage());
        }
    }

    private static boolean isDuplicatePlayer(Set<Long> bucket, long playerId) {
       long beforeCount = bucket.size();
       bucket.add(playerId);
       long afterCount = bucket.size();
        return afterCount == beforeCount;
    }
}
