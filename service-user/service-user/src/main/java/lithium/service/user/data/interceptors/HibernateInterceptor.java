package lithium.service.user.data.interceptors;

import lithium.service.user.data.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.EmptyInterceptor;

import java.util.Iterator;

@Slf4j
public class HibernateInterceptor extends EmptyInterceptor {
	@Override
	public void preFlush(Iterator entities) {
		entities.forEachRemaining(e -> {
			if (e instanceof User) saveUser((User)e);
		});
		super.preFlush(entities);
	}

	private void saveUser(User user) {
		String logMsg = "PreFlush User guid: "+user.guid();
		user.setGuid(user.guid());
		log.debug(logMsg);
	}
}
