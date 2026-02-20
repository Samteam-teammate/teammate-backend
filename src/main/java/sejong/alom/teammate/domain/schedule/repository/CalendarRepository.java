package sejong.alom.teammate.domain.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sejong.alom.teammate.domain.schedule.entity.Calendar;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
}
