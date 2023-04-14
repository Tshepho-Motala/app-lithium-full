package lithium.service.user.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import lithium.service.user.data.entities.Address;

public interface AddressRepository extends PagingAndSortingRepository<Address, Long>, JpaSpecificationExecutor<Address> {
  Page<Address> findByCountryCodeAndCountryNot(String countryCode, String country, Pageable page);
  Page<Address> findByAdminLevel1CodeAndAdminLevel1Not(String adminLevel1Code,String adminLevel1, Pageable page);
  Page<Address> findByCityCodeAndCityNot(String cityCode, String city, Pageable page);

  default Address findOne(Long id) {
    return findById(id).orElse(null);
  }
}
