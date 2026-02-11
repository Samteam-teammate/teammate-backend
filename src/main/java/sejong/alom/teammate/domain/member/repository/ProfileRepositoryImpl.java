package sejong.alom.teammate.domain.member.repository;

import static sejong.alom.teammate.domain.member.entity.QProfile.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.member.dto.ProfileListFetchRequest;
import sejong.alom.teammate.domain.member.entity.Profile;
import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.enums.Skill;
import sejong.alom.teammate.global.enums.SortingType;

@RequiredArgsConstructor
public class ProfileRepositoryImpl implements ProfileRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Profile> searchProfiles(ProfileListFetchRequest request, Pageable pageable) {
		List<Profile> content = queryFactory
			.selectFrom(profile)
			.where(
				partsAllMatch(request.part()),   // 중간 테이블을 거친 AND 로직
				skillsAllMatch(request.skill()), // 중간 테이블을 거친 AND 로직
				profile.isVisible.isTrue()
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(getOrderSpecifier(request.sort()))
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(profile.count())
			.from(profile)
			.where(
				partsAllMatch(request.part()),
				skillsAllMatch(request.skill()),
				profile.isVisible.isTrue()
			);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	// ProfilePart 리스트 내에 선택한 모든 Part가 존재해야 함 (AND)
	private BooleanBuilder partsAllMatch(List<Part> parts) {
		if (parts == null || parts.isEmpty()) return null;

		BooleanBuilder builder = new BooleanBuilder();
		for (Part p : parts) {
			builder.and(profile.profileParts.contains(p));
		}
		return builder;
	}

	// ProfileSkill 리스트 내에 선택한 모든 Skill이 존재해야 함 (AND)
	private BooleanBuilder skillsAllMatch(List<Skill> skills) {
		if (skills == null || skills.isEmpty()) return null;

		BooleanBuilder builder = new BooleanBuilder();
		for (Skill s : skills) {
			builder.and(profile.profileSkills.contains(s));
		}
		return builder;
	}

	private OrderSpecifier<?> getOrderSpecifier(SortingType sort) {
		return switch (sort) {
			case LATEST -> new OrderSpecifier<>(Order.DESC, profile.updatedAt);
			case RELEVANCE -> new OrderSpecifier<>(Order.ASC, profile.id); // TODO: 관련도
			default -> new OrderSpecifier<>(Order.DESC, profile.id);
		};
	}
}
