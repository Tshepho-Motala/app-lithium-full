package lithium.service.report.player.trans.data.repositories.specifications;

import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Matcher;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.report.player.trans.data.entities.PlayerTransaction;
import lithium.service.report.player.trans.data.entities.PlayerTransaction_;
public class PlayerTransactionSpecification {
	public static Specification<PlayerTransaction> find(
		final String userGuid,
		final Date dateStart,
		final Date dateEnd,
		final Long queryCriteriaId,
		final String search
	) {
		return new Specification<PlayerTransaction>() {
			@Override
			public Predicate toPredicate(Root<PlayerTransaction> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Long searchNumber = null;
				BigDecimal amount = null;
				try {
					amount = new BigDecimal(search.replaceAll(Matcher.quoteReplacement("$"), "").replaceAll(",","").replaceAll(" ", ""));
					searchNumber = Long.parseLong(search);
				} catch (Exception e) {
					//No need to throw this exception
				}
				
				
				return cb.and(	
								cb.equal(root.get(PlayerTransaction_.queryCriteria), queryCriteriaId),
								cb.equal(root.get(PlayerTransaction_.userGuid), userGuid),
								cb.between(root.get(PlayerTransaction_.tranEntryDate), dateStart, dateEnd),
								cb.or(
										searchNumber != null ? cb.equal(root.get(PlayerTransaction_.tranId), searchNumber) : cb.or(),
										amount != null ? cb.equal(root.get(PlayerTransaction_.tranEntryAmount), amount.movePointRight(2).longValue()) : cb.or(),
										cb.like(cb.upper(root.get(PlayerTransaction_.externalTranId)), search.toUpperCase() + "%"),
										cb.like(cb.upper(root.get(PlayerTransaction_.tranEntryAccountType)), search.toUpperCase() + "%"),
										cb.like(cb.upper(root.get(PlayerTransaction_.tranEntryAccountCode)), search.toUpperCase() + "%"),
										cb.like(cb.upper(root.get(PlayerTransaction_.providerGuid)), "%" + search.toUpperCase() + "%"),
										cb.like(cb.upper(root.get(PlayerTransaction_.gameName)), search.toUpperCase() + "%"),
										cb.like(cb.upper(root.get(PlayerTransaction_.bonusName)), search.toUpperCase() + "%"),
										cb.like(cb.upper(root.get(PlayerTransaction_.bonusCode)), search.toUpperCase() + "%")
										
								)
							);
			}
		};
	}
}

//CB always true .and() always false .or()
//http://stackoverflow.com/questions/14675229/jpa-criteria-api-how-to-express-literal-true-and-literal-false