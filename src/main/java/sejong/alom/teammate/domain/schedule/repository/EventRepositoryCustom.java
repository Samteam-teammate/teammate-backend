package sejong.alom.teammate.domain.schedule.repository;

import java.time.LocalDate;
import java.util.List;

import sejong.alom.teammate.domain.schedule.entity.Calendar;
import sejong.alom.teammate.domain.schedule.entity.Event;

public interface EventRepositoryCustom {
	List<Event> findEventsInRange(Calendar calendar, LocalDate startDate, LocalDate endDate);
}
