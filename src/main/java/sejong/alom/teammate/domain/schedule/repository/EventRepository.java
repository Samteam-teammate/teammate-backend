package sejong.alom.teammate.domain.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sejong.alom.teammate.domain.schedule.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {
}
