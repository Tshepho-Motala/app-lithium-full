package lithium.service.kyc.provider.onfido.service;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lithium.service.kyc.provider.onfido.objects.CheckAddressContent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class AddressMatchServiceTest {
    @InjectMocks
    private AddressMatchService matchService;

    @Test
    public void strictMatchShouldSuccess() throws Exception {
        List<CheckAddressContent> data = readData("address-to-match-strict.csv");
        log.info("Read addresses for check: " + data.size());
        for (int i = 1; i <= data.size(); i++) {
            CheckAddressContent content  = data.get(i-1);
            boolean match = matchService.strictMatch("Test2", content.getExtractedAddress(), content.getAddressLine1(), content.getCity(), content.getPostalCode());
            log.info("Match result " + i + " of " + data.size() + " (" + content.getResultId() + "): " + match);
            assertTrue(match);
        }
    }

    @Test
    public void strictMatchShouldFail() throws Exception {
        List<CheckAddressContent> data = readData("address-to-match-ignore-symbols.csv");
        log.info("Read addresses for check: " + data.size());
        for (int i = 1; i <= data.size(); i++) {
            CheckAddressContent content  = data.get(i-1);
            boolean match = matchService.strictMatch("Test3", content.getExtractedAddress(), content.getAddressLine1(), content.getCity(), content.getPostalCode());
            log.info("Match result " + i + " of " + data.size() + " (" + content.getResultId() + "): " + match);
            assertFalse(match);
        }
    }

    @Test
    public void checkListOfAddressesForEnhancing() throws Exception {
        List<CheckAddressContent> data = readData("address-to-match-ignore-symbols.csv");
        log.info("Read addresses for check: " + data.size());
        for (int i = 1; i <= data.size(); i++) {
            CheckAddressContent content  = data.get(i-1);
            boolean match = matchService.matchWithSymbolsIgnore("Test1", content.getExtractedAddress(), content.getAddressLine1(), content.getCity(), content.getPostalCode(), "\s", "\\.", "-", ",");
            log.info("Match result " + i + " of " + data.size() + " (" + content.getResultId() + "): " + match);
            assertTrue(match);
        }
    }

    private List<CheckAddressContent> readData(String fileName) throws IOException {
        File resource = new ClassPathResource(fileName).getFile();
        HeaderColumnNameMappingStrategy mappingStrategy = new HeaderColumnNameMappingStrategy();
        mappingStrategy. setType(CheckAddressContent.class);
        List list = new CsvToBeanBuilder(new CSVReader(Files.newBufferedReader(resource.toPath())))
                .withSeparator(',')
                .withMappingStrategy(mappingStrategy)
                .build()
                .parse();
        list.sort(Comparator.comparing(CheckAddressContent::getResultId));
        return list;
    }


}