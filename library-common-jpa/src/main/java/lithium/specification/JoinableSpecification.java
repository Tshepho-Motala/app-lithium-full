package lithium.specification;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.data.jpa.domain.Specification;

public interface JoinableSpecification<T> extends Specification<T> {
	
	/**
	 * Allow reuse of join when possible
	 * 
	 * @param <K>
	 * @param <Z>
	 * @return joinType
	 */
	
	@SuppressWarnings("unchecked")
	public default <K, Z> ListJoin<K, Z> joinList(From<?, K> from, ListAttribute<K, Z> attribute, JoinType joinType) {
		
		for (Join<K, ?> join : from.getJoins()) {
			
			boolean sameName = join.getAttribute().getName().equals(attribute.getName());
			
			if (sameName && join.getJoinType().equals(joinType)) {
				
				return (ListJoin<K, Z>) join; // TODO verify Z type it should be
												// of Z after all its
												// ListAttribute<K,Z>
			}
		}
		return from.join(attribute, joinType);
	}
	
	/**
	 * Allow reuse of join when possible
	 * 
	 * @param <K>
	 * @param <Z>
	 * @param joinType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public default <K, Z> SetJoin<K, Z> joinList(From<?, K> from, SetAttribute<K, Z> attribute, JoinType joinType) {
		
		for (Join<K, ?> join : from.getJoins()) {
			
			boolean sameName = join.getAttribute().getName().equals(attribute.getName());
			
			if (sameName && join.getJoinType().equals(joinType)) {
				return (SetJoin<K, Z>) join; // TODO verify Z type it should be
												// of Z after all its
												// ListAttribute<K,Z>
			}
		}
		return from.join(attribute, joinType);
	}
	
	/**
	 * Allow reuse of join when possible
	 * 
	 * @param <K>
	 * @param <Z>
	 * @param joinType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public default <K, Z> Join<K, Z> joinList(From<?, K> from, SingularAttribute<K, Z> attribute, JoinType joinType) {
		
		for (Join<K, ?> join : from.getJoins()) {
			
			boolean sameName = join.getAttribute().getName().equals(attribute.getName());
			
			if (sameName && join.getJoinType().equals(joinType)) {
				return (Join<K, Z>) join; // TODO verify Z type it should be of
											// Z after all its
											// ListAttribute<K,Z>
			}
		}
		return from.join(attribute, joinType);
	}
}