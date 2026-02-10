package sejong.alom.teammate.global.config;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.global.exception.BusinessException;
import sejong.alom.teammate.global.exception.docs.ErrorCode;

@Slf4j
@Configuration
public class SejongPortalLoginConfig {
	@Bean
	public RestClient sejongPortalRestClient() {
		return RestClient.builder()
			.baseUrl("https://portal.sejong.ac.kr")
			.requestFactory(createSecureRequestFactory())
			.build();
	}

	private JdkClientHttpRequestFactory createSecureRequestFactory() {
		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[]{insecureTrustManager()}, new SecureRandom());

			CookieManager cookieManager = new CookieManager();
			cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

			HttpClient httpClient = HttpClient.newBuilder()
				.sslContext(sslContext)
				.version(HttpClient.Version.HTTP_1_1)
				.connectTimeout(Duration.ofSeconds(10))
				.cookieHandler(cookieManager)
				.followRedirects(HttpClient.Redirect.ALWAYS)
				.build();

			return new JdkClientHttpRequestFactory(httpClient);
		} catch (Exception e) {
			log.error("세종대 포털 통신을 위한 HTTP 클라이언트 초기화 실패", e);
			throw new BusinessException(ErrorCode.SJU_UPSTREAM_ERROR);
		}
	}

	private X509TrustManager insecureTrustManager() {
		return new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {}

			@Override
			public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};
	}
}
