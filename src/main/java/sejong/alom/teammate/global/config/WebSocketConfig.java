package sejong.alom.teammate.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.domain.auth.provider.AuthTokenProvider;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	private final AuthTokenProvider authTokenProvider;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws/chat")
			.setAllowedOriginPatterns("*")
			.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic"); // 클라이언트가 메세지를 받을 때
		registry.setApplicationDestinationPrefixes("/app"); // 클라이언트가 서버로 메세지를 보낼 때
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

				// STOMP 연결(CONNECT) 시점에만 토큰 유효성 검사
				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					String authHeader = accessor.getFirstNativeHeader("Authorization");

					if (authHeader != null && authHeader.startsWith("Bearer ")) {
						String token = authHeader.substring(7);

						try {
							authTokenProvider.validateToken(token);

							Authentication authentication = authTokenProvider.getAuthentication(token);
							accessor.setUser(authentication);
						} catch (Exception e) {
							throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
						}
					} else {
						log.warn("웹소켓 연결 실패: 인증 헤더가 없습니다.");
						throw new IllegalArgumentException("인증 헤더가 없습니다.");
					}
				}
				return message;
			}
		});
	}
}
