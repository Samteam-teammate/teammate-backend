package sejong.alom.teammate.domain.team.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sejong.alom.teammate.domain.member.entity.Member;
import sejong.alom.teammate.domain.member.repository.MemberRepository;
import sejong.alom.teammate.domain.recruitment.entity.Recruitment;
import sejong.alom.teammate.domain.recruitment.repository.RecruitmentRepository;
import sejong.alom.teammate.domain.schedule.entity.Calendar;
import sejong.alom.teammate.domain.team.dto.TeamCreateRequest;
import sejong.alom.teammate.domain.team.dto.TeamDetailResponse;
import sejong.alom.teammate.domain.team.dto.TeamListResponse;
import sejong.alom.teammate.domain.team.dto.TeamMemberUpdateRequest;
import sejong.alom.teammate.domain.team.dto.TeamMemberResponse;
import sejong.alom.teammate.domain.team.dto.TeamUpdateRequest;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.domain.team.entity.TeamMember;
import sejong.alom.teammate.domain.team.repository.TeamMemberRepository;
import sejong.alom.teammate.domain.team.repository.TeamRepository;
import sejong.alom.teammate.global.enums.TeamMemberRole;
import sejong.alom.teammate.global.exception.BusinessException;
import sejong.alom.teammate.global.exception.docs.ErrorCode;
import sejong.alom.teammate.global.util.S3Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
	private final MemberRepository memberRepository;
	private final TeamRepository teamRepository;
	private final TeamMemberRepository teamMemberRepository;
	private final TeamMemberService teamMemberService;
	private final RecruitmentRepository recruitmentRepository;
	private final S3Service s3Service;

	@Transactional
	public void generateTeam(Long memberId, TeamCreateRequest request, MultipartFile teamImage) {
		// 멤버 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

		String imageUrl = null;
		if (teamImage != null && !teamImage.isEmpty()) {
			imageUrl = s3Service.upload(teamImage, "teams");
		}

		// 팀 생성 및 저장
		Team team = teamRepository.save(request.to(imageUrl));
		teamMemberRepository.save(
			TeamMember.builder()
				.team(team)
				.member(member)
				.role(TeamMemberRole.LEADER)
				.build()
		);
		Calendar calendar = Calendar.builder().build();
		team.setCalendar(calendar);

	}

	@Transactional(readOnly = true)
	public List<TeamListResponse> getMyTeamList(Long memberId) {
		// 멤버 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

		// 팀원으로 속해있는 데이터 찾아서 팀 추출
		List<TeamMember> teamMembers = teamMemberRepository.findAllByMember(member);
		return teamMembers.stream()
			.map(TeamMember::getTeam)
			.map(TeamListResponse::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public TeamDetailResponse getTeamDetail(Long teamId) {
		// 팀 조회
		Team team = teamRepository.findById(teamId)
			.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

		// 모집 공고 조회
		Recruitment recruitment = recruitmentRepository.findByTeam(team);
		Long recruitmentId = recruitment == null ? null : recruitment.getId();

		List<TeamMemberResponse> teamMembers = teamMemberService.getTeamMemberList(team);

		return TeamDetailResponse.of(team, teamMembers, recruitmentId);
	}

	@Transactional
	public void updateTeamInfo(Long teamId, TeamUpdateRequest request) {
		// 팀 조회
		Team team = teamRepository.findById(teamId)
			.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

		// 팀 정보 업데이트
		team.update(
			request.name(),
			request.bio(),
			request.category(),
			request.maxMemberCount(),
			request.isPublic()
		);
	}

	@Transactional
	public void updateTeamImage(Long teamId, MultipartFile file) {
		Team team = teamRepository.findById(teamId)
			.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

		// 기존 이미지가 있다면 S3에서 삭제
		if (team.getTeamImage() != null) {
			s3Service.delete(team.getTeamImage());
		}

		// 새 이미지 업로드 후 URL 업데이트
		String imageUrl = s3Service.upload(file, "teams");
		team.updateImageUrl(imageUrl);
	}

	@Transactional
	public void addTeamMember(Long teamId, Long memberId, TeamMemberUpdateRequest request) {
		// 팀 조회
		Team team = teamRepository.findById(teamId)
			.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

		// 멤버 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

		// 최대 팀원 수와 현재 팀원 수 + 1 비교
		if (team.getCurrentMemberCount() + 1 > team.getMaxMemberCount()) {
			throw new BusinessException(ErrorCode.INVALID_MEMBER_COUNT);
		}

		// 팀원 추가
		teamMemberRepository.save(
			TeamMember.builder()
				.team(team)
				.member(member)
				.role(TeamMemberRole.MEMBER)
				.part(request.part())
				.build()
		);
	}

	@Transactional
	public void updateTeamMemberRole(Long teamId, Long memberId, TeamMemberUpdateRequest request) {
		// 팀 조회
		Team team = teamRepository.findById(teamId)
			.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

		// 멤버 조회
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

		// 팀원 조회
		TeamMember teamMember = teamMemberRepository.findByTeamAndMember(team, member)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		// 팀원 역할 업데이트
		teamMember.updatePart(request.part());
	}
}
