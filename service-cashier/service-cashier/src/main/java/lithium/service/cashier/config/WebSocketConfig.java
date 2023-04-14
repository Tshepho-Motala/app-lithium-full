//package lithium.service.cashier.config;
//
//import java.util.Map;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//import org.springframework.web.socket.server.HandshakeInterceptor;
//import org.springframework.web.socket.server.RequestUpgradeStrategy;
//import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
//import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
//
//import lithium.service.cashier.websockets.WebSocketHandler;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//	@Override
//	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//		registry
//		.addHandler(myHandler(), "/cashier/providers/")
//		.addInterceptors(handshakeInterceptor())
//		.setHandshakeHandler(handshakeHandler())
////		.addInterceptors(new HttpSessionHandshakeInterceptor())
//		.setAllowedOrigins("*")
//		.withSockJS();
//	}
//	
//	@Bean
//	public HandshakeInterceptor handshakeInterceptor() {
//		HandshakeInterceptor hi = new HandshakeInterceptor() {
//			@Override
//			public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, org.springframework.web.socket.WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//				log.info("Headers : "+request.getHeaders());
//				return true;
//			}
//			
//			@Override
//			public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, org.springframework.web.socket.WebSocketHandler wsHandler, Exception exception) {
//			}
//		};
//		return hi;
//	}
//	
//	@Bean
//	public RequestUpgradeStrategy requestUpgradeStrategy() {
//		return new TomcatRequestUpgradeStrategy();
//	}
//	
//	@Bean
//	public DefaultHandshakeHandler handshakeHandler() {
//		return new DefaultHandshakeHandler(requestUpgradeStrategy());
//	}
//	
////	@Bean
////	public HandshakeInterceptor interceptor() {
////		HandshakeInterceptor interceptor = new HandshakeInterceptor() {
////			
////			@Override
////			public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, org.springframework.web.socket.WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
////				log.info("beforeHandshake");
////				return false;
////			}
////			
////			@Override
////			public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, org.springframework.web.socket.WebSocketHandler wsHandler, Exception exception) {
////				log.info("afterHandshake");
////			}
////		};
////		return interceptor;
////	}
////	
////	@Bean
////	public HandshakeHandler handshakeHandler() {
////		HandshakeHandler hh = new HandshakeHandler() {
////			@Override
////			public boolean doHandshake(ServerHttpRequest request, ServerHttpResponse response, org.springframework.web.socket.WebSocketHandler wsHandler, Map<String, Object> attributes) throws HandshakeFailureException {
////				log.info("doHandshake(ServerHttpRequest : "+request+", ServerHttpResponse : "+response+", WebSocketHandler : "+wsHandler+", Map<String, Object> : "+attributes+")");
////				return true;
////			}
////		};
////		return hh;
////	}
//	
//	@Bean
//	public WebSocketHandler myHandler() {
//		log.info("Creating WebSocketHandler myHandler()");
//		return new WebSocketHandler();
//	}
//	
////	@Bean
////	public ServletServerContainerFactoryBean createWebSocketContainer() {
////		ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
////		container.setMaxTextMessageBufferSize(8192);
////		container.setMaxBinaryMessageBufferSize(8192);
////		return container;
////	}
//}