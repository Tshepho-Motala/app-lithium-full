package lithium.service.translate.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.entities.Language_;
import lithium.service.translate.data.entities.TranslationKey;
import lithium.service.translate.data.entities.TranslationKey_;
import lithium.service.translate.data.entities.TranslationValue;
import lithium.service.translate.data.entities.TranslationValueDefault;
import lithium.service.translate.data.entities.TranslationValueDefault_;
import lithium.service.translate.data.entities.TranslationValueRevision;
import lithium.service.translate.data.entities.TranslationValueRevision_;
import lithium.service.translate.data.entities.TranslationValue_;

public class TranslationKeySpecification {

	public static Predicate byLanguage(final String locale2, Root<TranslationKey> root, CriteriaBuilder cb, String search) {
		Join<TranslationKey, TranslationValue> joinValues = root.join(TranslationKey_.values, JoinType.INNER);
		Join<TranslationValue, Language> joinLanguage = joinValues.join(TranslationValue_.language, JoinType.INNER);
		Join<TranslationValue, TranslationValueDefault> joinValueDefault = joinValues.join(TranslationValue_.defaultValue, JoinType.LEFT);
		Join<TranslationValue, TranslationValueRevision> joinValueCurrent = joinValues.join(TranslationValue_.current, JoinType.LEFT);
		
		Predicate p = cb.equal(joinLanguage.get(Language_.locale2), locale2);
		if ((search != null) && (search.length() > 2)) {
			Predicate[] ors = { 
					cb.like(cb.lower(joinValueDefault.get(TranslationValueDefault_.value)), search.toLowerCase() + "%"),
					cb.like(cb.lower(joinValueCurrent.get(TranslationValueRevision_.value)), search.toLowerCase() + "%"),
					cb.like(cb.lower(root.get(TranslationKey_.keyCode)), search.toLowerCase() + "%"),
			};
			p = cb.and(p, cb.or(ors));
		}
		return p;
	}

	public static Specification<TranslationKey> byLanguage(final String locale2, final String search) {
		return new Specification<TranslationKey>() {
			@Override
			public Predicate toPredicate(Root<TranslationKey> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return byLanguage(locale2, root, cb, search);
			}
		};
	}
	
	public static Specification<TranslationKey> byLanguageAndRefLanguageMissing(final String locale2, final String refLocale2, final String search) {
		return new Specification<TranslationKey>() {
			@Override
			public Predicate toPredicate(Root<TranslationKey> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				Predicate p = byLanguage(refLocale2, root, cb, search);

				Subquery<TranslationKey> subQuery = query.subquery(TranslationKey.class);
				Root<TranslationKey> subQueryRoot = subQuery.from(TranslationKey.class);
				Join<TranslationKey, TranslationValue> subQueryJoinValues = subQueryRoot.join(TranslationKey_.values, JoinType.INNER);
				Join<TranslationValue, Language> subQueryJoinLanguage = subQueryJoinValues.join(TranslationValue_.language, JoinType.INNER);
				Predicate subQueryP = cb.equal(subQueryJoinLanguage.get(Language_.locale2), locale2);

				subQuery.select(subQueryRoot.get(TranslationKey_.id.getName()));
				subQuery.where(subQueryP);

				p = cb.and(p, cb.not(root.get(TranslationKey_.id).in(subQuery)));
				return p;
			}
		};
	}

}
