package sejong.alom.teammate.domain.recruitment.repository;

import static sejong.alom.teammate.domain.recruitment.entity.QRecruitment.*;
import static sejong.alom.teammate.domain.recruitment.entity.QRecruitmentPart.*;
import static sejong.alom.teammate.domain.team.entity.QTeam.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.recruitment.dto.RecruitmentListFetchRequest;
import sejong.alom.teammate.domain.recruitment.entity.Recruitment;
import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.enums.SortingType;
import sejong.alom.teammate.global.enums.TeamCategory;

@RequiredArgsConstructor
public class RecruitmentRepositoryImpl implements RecruitmentRepositoryCustom{
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Recruitment> searchRecruitments(RecruitmentListFetchRequest request, Pageable pageable) {
		List<Recruitment> content = queryFactory
			.selectFrom(recruitment)
			// [주의] 페이징 시 Collection Fetch Join은 메모리 이슈로 피하고, 대신 Batch Size 설정을 활용합니다.
			.join(recruitment.team, team).fetchJoin()
			.leftJoin(recruitment.recruitmentParts, recruitmentPart)
			.where(
				categoryIn(request.categories()),
				partIn(request.parts()),
				isActive(request.isActive())
			)
			.orderBy(getOrderBy(request.sort())) // 정렬 조건 적용
			.offset(pageable.getOffset())        // 시작 지점
			.limit(pageable.getPageSize())       // 조회 개수
			.fetch();

		// 2. 카운트 쿼리 (별도로 작성하여 최적화)
		JPAQuery<Long> countQuery = queryFactory
			.select(recruitment.countDistinct())
			.from(recruitment)
			.leftJoin(recruitment.recruitmentParts, recruitmentPart)
			.join(recruitment.team, team)
			.where(
				categoryIn(request.categories()),
				partIn(request.parts()),
				isActive(request.isActive())
			);

		// 3. Page 객체로 변환하여 반환
		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	private BooleanExpression categoryIn(List<TeamCategory> categories) {
		return (categories == null || categories.isEmpty()) ? null : team.category.in(categories);
	}

	private BooleanExpression partIn(List<Part> parts) {
		// OR 조건: 선택된 파트 중 하나라도 모집 중인 공고
		return (parts == null || parts.isEmpty()) ? null : recruitmentPart.part.in(parts);
	}

	private BooleanExpression isActive(Boolean isActive) {
		if (isActive == null || !isActive) return null;
		// 현재 시간이 마감기한(deadline) 이전인 공고만 필터링
		return recruitment.deadline.after(LocalDateTime.now());
	}

	// 정렬 조건
	private OrderSpecifier<?> getOrderBy(SortingType sort) {
		if (sort == null) return recruitment.createdAt.desc(); // 기본값: 최신순

		return switch (sort) {
			case LATEST -> recruitment.createdAt.desc();
			case IMMINENT -> recruitment.deadline.asc(); // 마감 임박순
			case POPULAR -> recruitment.scrapCount.desc();
			default -> recruitment.updatedAt.asc();
		};
	}
}
