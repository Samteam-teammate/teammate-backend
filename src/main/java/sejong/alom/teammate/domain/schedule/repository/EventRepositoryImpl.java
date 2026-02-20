package sejong.alom.teammate.domain.schedule.repository;

import static sejong.alom.teammate.domain.schedule.entity.QEvent.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.schedule.entity.Calendar;
import sejong.alom.teammate.domain.schedule.entity.Event;

@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Event> findEventsInRange(Calendar calendar, LocalDate startDate, LocalDate endDate) {
		// LocalDate(날짜)를 LocalDateTime(시간 포함)으로 변환 (00:00:00 ~ 23:59:59)
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

		return queryFactory
			.selectFrom(event)
			.where(
				event.calendar.eq(calendar),
				event.startTime.before(endDateTime), // 일정 시작이 조회 종료일 이전
				event.endTime.after(startDateTime)   // 일정 종료가 조회 시작일 이후 (겹침 조건)
			)
			.orderBy(event.startTime.asc())
			.fetch();
	}
}
