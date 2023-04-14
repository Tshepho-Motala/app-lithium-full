package lithium.service.games.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.games.client.objects.supplier.GameVerticalEnum;
import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.supplier.SupplierGameMetaBetLimits;
import lithium.service.games.data.entities.supplier.SupplierGameMetaData;
import lithium.service.games.data.repositories.GameRepository;
import lithium.service.games.data.repositories.SupplierGameMetaDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SupplierGameMetaDataService {

    @Autowired
    private SupplierGameMetaDataRepository repository;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private CachingDomainClientService domainClientService;

    public List<SupplierGameMetaData> retrieve(String provider, GameVerticalEnum supplier, String domain, int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        return repository.findAllByGameVerticalNameAndGameDomainNameAndGameProviderGuidAndGameSupplierGameGuidNotNullAndGameEnabledTrue(supplier, domain, provider, paging);
    }

    public void updateGamesAndSupplierGameMetaData(lithium.service.games.client.objects.supplier.SupplierGameMetaData data, Game game) {
        ObjectMapper objectMapper = new ObjectMapper();
        SupplierGameMetaData supplierGameMetaData = objectMapper.convertValue(data, SupplierGameMetaData.class);
        SupplierGameMetaData dbData = repository.findFirstByGameId(game.getId());
        Domain domain = domainClientService.retrieveDomainFromDomainService(game.getDomain().getName());
        if(domain.getCurrency() == null) {
            supplierGameMetaData.setBetLimits(new ArrayList<>());
            log.warn("Domain does not have currency set up: domainName: " + domain.getName());
        } else {
            List<SupplierGameMetaBetLimits> limits = supplierGameMetaData.getBetLimits().stream()
                    .filter(betLimit -> domain.getCurrency().equals(betLimit.getCurrencyCode()))
                    .collect(Collectors.toList());
            supplierGameMetaData.setBetLimits(limits);
        }
        if (dbData != null) {
            updateData(supplierGameMetaData, dbData);
            repository.save(supplierGameMetaData);
        } else {
            supplierGameMetaData = repository.save(supplierGameMetaData);
            game.setSupplierGameMetaData(supplierGameMetaData);
            gameRepo.save(game);
        }
    }

    private void updateData(SupplierGameMetaData supplierGameMetaData, SupplierGameMetaData dbSupplierGameMetaData) {
        supplierGameMetaData.setId(dbSupplierGameMetaData.getId());
        if (supplierGameMetaData.getGameVertical() != null && dbSupplierGameMetaData.getGameVertical() != null)
            supplierGameMetaData.getGameVertical().setId(dbSupplierGameMetaData.getGameVertical().getId());
        if (supplierGameMetaData.getDealer() != null && dbSupplierGameMetaData.getDealer() != null)
            supplierGameMetaData.getDealer().setId(dbSupplierGameMetaData.getDealer().getId());
        if (supplierGameMetaData.getDisplay() != null && dbSupplierGameMetaData.getDisplay() != null)
            supplierGameMetaData.getDisplay().setId(dbSupplierGameMetaData.getDisplay().getId());
        if (supplierGameMetaData.getOperationHours() != null && dbSupplierGameMetaData.getOperationHours() != null)
            supplierGameMetaData.getOperationHours().setId(dbSupplierGameMetaData.getOperationHours().getId());

        // Left empty. Currently, frontend does not need this data
        supplierGameMetaData.setDescriptions(new HashSet<>());
        supplierGameMetaData.setLinks(new ArrayList<>());
    }
}
