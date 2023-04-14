package lithium.service.accounting.provider.internal.data.repositories.specifications;

import lithium.service.accounting.provider.internal.data.entities.Label;
import lithium.service.accounting.provider.internal.data.entities.LabelValue;
import lithium.service.accounting.provider.internal.data.entities.LabelValue_;
import lithium.service.accounting.provider.internal.data.entities.Label_;
import lithium.service.accounting.provider.internal.data.entities.Transaction;
import lithium.service.accounting.provider.internal.data.entities.TransactionLabelValue;
import lithium.service.accounting.provider.internal.data.entities.TransactionLabelValue_;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.data.entities.TransactionType_;
import lithium.service.accounting.provider.internal.data.entities.Transaction_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class TransactionLabelValueSpecifications {
    public static Specification<TransactionLabelValue> find(
            final String transactionTypeCode,
            final String labelName,
            final String labelValue
    ) {
        return new Specification<TransactionLabelValue>() {
            @Override
            public Predicate toPredicate(Root<TransactionLabelValue> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join<TransactionLabelValue, LabelValue> joinLabelValue = root.join(TransactionLabelValue_.labelValue, JoinType.INNER);
                Join<LabelValue, Label> joinLabel = joinLabelValue.join(LabelValue_.label, JoinType.INNER);

                Root<Transaction> tranRoot = query.from(Transaction.class);
                Join<Transaction, TransactionType> joinTransactionType = tranRoot.join(Transaction_.transactionType, JoinType.INNER);

                Predicate p = cb.equal(root.get(TransactionLabelValue_.transactionId), tranRoot.get(Transaction_.id));
                p = cb.and(p, cb.equal(joinLabelValue.get(LabelValue_.value), labelValue));
                p = cb.and(p, cb.equal(joinLabel.get(Label_.name), labelName));
                p = cb.and(p, cb.equal(joinTransactionType.get(TransactionType_.code), transactionTypeCode));
                return p;
            }
        };
    }
}
