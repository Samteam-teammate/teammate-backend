package sejong.alom.teammate.global.config;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SejongPortalLoginConfig {
	@Bean
	public RestClient sejongPortalRestClient() {
		return RestClient.builder()
			.baseUrl("https://portal.sejong.ac.kr")
			.requestFactory(createApacheRequestFactory())
			.build();
	}

	private HttpComponentsClientHttpRequestFactory createApacheRequestFactory() {
		try {
			// 1. SSL 설정: TLS 1.2 허용 및 모든 인증서 신뢰
			SSLContext sslContext = SSLContexts.custom()
				.loadTrustMaterial(null, (chain, authType) -> true)
				.build();

			SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
				.setSslContext(sslContext)
				// 중요: 구형 서버를 위해 호스트네임 검증기 완화
				.setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				// 중요: 서버가 요구할 수 있는 구형 프로토콜 명시
				.setTlsVersions(TLS.V_1_2)
				.build();

			// 2. 커넥션 매니저 및 쿠키 저장소 설정
			PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
				.setSSLSocketFactory(sslSocketFactory)
				.build();

			BasicCookieStore cookieStore = new BasicCookieStore();

			// 3. CloseableHttpClient 생성
			CloseableHttpClient httpClient = HttpClients.custom()
				.setConnectionManager(connectionManager)
				.setDefaultCookieStore(cookieStore)
				// 리다이렉트 자동 처리
				.setRedirectStrategy(DefaultRedirectStrategy.INSTANCE)
				// 기본 User-Agent 설정 (보안 장비 우회)
				.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/121.0.0.0")
				.build();

			return new HttpComponentsClientHttpRequestFactory(httpClient);
		} catch (Exception e) {
			throw new RuntimeException("Apache HttpClient 5 초기화 실패", e);
		}
	}
}
