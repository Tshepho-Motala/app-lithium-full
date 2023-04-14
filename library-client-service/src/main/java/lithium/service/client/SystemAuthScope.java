package lithium.service.client;

public class SystemAuthScope<T> {

	private SystemAuthScopeCallback<T> callback;
	
	public SystemAuthScope(SystemAuthScopeCallback<T> callback) {
		this.callback = callback;
	}
	
	public T run() throws Exception {
	
		LithiumServiceClientConfiguration.useSystemAuthForThisThread(true);
		try {
			return callback.run();
		} finally {
			LithiumServiceClientConfiguration.useSystemAuthForThisThread(false);
		}
	}

}
