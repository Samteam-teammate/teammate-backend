package sejong.alom.teammate.domain.recruitment.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.domain.recruitment.dto.RecruitmentCreateRequest;
import sejong.alom.teammate.domain.recruitment.dto.RecruitmentUpdateRequest;
import sejong.alom.teammate.domain.recruitment.entity.Recruitment;
import sejong.alom.teammate.domain.recruitment.entity.RecruitmentPart;
import sejong.alom.teammate.domain.recruitment.repository.RecruitmentPartRepository;
import sejong.alom.teammate.domain.recruitment.repository.RecruitmentRepository;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.domain.team.repository.TeamRepository;
import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.exception.BusinessException;
import sejong.alom.teammate.global.exception.docs.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentService {
	private final RecruitmentRepository recruitmentRepository;
	private final TeamRepository teamRepository;
	private final RecruitmentPartRepository recruitmentPartRepository;

	public Map<String, Long> generateRecruitment(RecruitmentCreateRequest request) {
		Team team = teamRepository.findById(request.teamId())
			.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

		Recruitment recruitment = recruitmentRepository.save(request.to(team));
		for (Part p : request.recruitParts()) {
			recruitmentPartRepository.save(
				RecruitmentPart.builder()
					.recruitment(recruitment)
					.part(p)
					.build()
			);
		}

		Map<String, Long> data = new HashMap<>();
		data.put("recruitmentId", recruitment.getId());

		return data;
	}

	public void updateRecruitment(Long recruitmentId, RecruitmentUpdateRequest request) {
		Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.RECRUITMENT_NOT_FOUND));

		recruitment.update(request.deadline(), request.description());

		if (request.recruitmentParts() != null) {
			List<RecruitmentPart> newParts = request.recruitmentParts().stream()
				.map(p -> RecruitmentPart.builder()
					.recruitment(recruitment)
					.part(p)
					.build())
				.toList();
			recruitment.updateParts(newParts);
		}
	}
}
