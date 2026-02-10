package sejong.alom.teammate.domain.auth.service;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.domain.auth.dto.SejongMemberDto;
import sejong.alom.teammate.global.exception.BusinessException;
import sejong.alom.teammate.global.exception.docs.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class SejongPortalLoginService {
	private final RestClient sejongPortalRestClient;

	public SejongMemberDto getMemberAuthInfos(String id, String pw) {
		try {
			// 로그인 수행
			authenticate(id, pw);

			// SSO 연동 및 최종 페이지 데이터 획득
			String html = fetchMemberStatusHtml();

			// 파싱 및 결과 반환
			return parseMemberInfo(html);
		} catch (HttpClientErrorException e) {
			throw new BusinessException(ErrorCode.SJU_AUTH_FAILED);
		}catch (Exception e) {
			log.error("세종대 통합 인증 프로세스 중 장애 발생: {}", e.getMessage());
			throw new BusinessException(ErrorCode.SJU_UPSTREAM_ERROR);
		}
	}

	private void authenticate(String id, String pw) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("mainLogin", "N");
		formData.add("rtUrl", "library.sejong.ac.kr");
		formData.add("id", id);
		formData.add("password", pw);

		sejongPortalRestClient.post()
			.uri("/jsp/login/login_action.jsp")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(formData)
			.header("Referer", "https://portal.sejong.ac.kr")
			.retrieve()
			.toBodilessEntity();
	}

	private String fetchMemberStatusHtml() {
		// SSO 전환 호출
		sejongPortalRestClient.get()
			.uri("http://classic.sejong.ac.kr/_custom/sejong/sso/sso-return.jsp?returnUrl=https://classic.sejong.ac.kr/classic/index.do")
			.retrieve()
			.toBodilessEntity();

		// 실제 정보 페이지 호출
		return sejongPortalRestClient.get()
			.uri("https://classic.sejong.ac.kr/classic/reading/status.do")
			.retrieve()
			.body(String.class);
	}

	private SejongMemberDto parseMemberInfo(String html) {
		Document doc = Jsoup.parse(html);
		List<String> extractedData = doc.select(".b-con-box table tbody tr td")
			.stream()
			.map(element -> element.text().trim())
			.toList();

		if (extractedData.isEmpty()) {
			throw new BusinessException(ErrorCode.SJU_AUTH_FAILED);
		}

		return SejongMemberDto.builder()
			.major(extractedData.get(0))
			.studentId(extractedData.get(1))
			.name(extractedData.get(2))
			.grade(extractedData.get(3))
			.status(extractedData.get(4))
			.completedSemester(extractedData.get(5))
			.build();
	}
}
