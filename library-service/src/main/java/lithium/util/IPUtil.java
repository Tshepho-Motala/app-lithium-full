package lithium.util;
import javax.servlet.http.HttpServletRequest;
public class IPUtil {
	public static String ipFromRequest(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		return ip != null ? ip : request.getRemoteAddr();
	}
}
