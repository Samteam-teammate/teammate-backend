package sejong.alom.teammate.domain.recruitment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import sejong.alom.teammate.domain.recruitment.dto.RecruitmentListFetchRequest;
import sejong.alom.teammate.domain.recruitment.entity.Recruitment;

public interface RecruitmentRepositoryCustom {
	Page<Recruitment> searchRecruitments(RecruitmentListFetchRequest request, Pageable pageable);
}
