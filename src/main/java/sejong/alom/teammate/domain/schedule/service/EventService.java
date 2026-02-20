package sejong.alom.teammate.domain.schedule.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sejong.alom.teammate.domain.schedule.dto.EventCreateRequest;
import sejong.alom.teammate.domain.schedule.dto.EventResponse;
import sejong.alom.teammate.domain.schedule.entity.Event;
import sejong.alom.teammate.domain.schedule.repository.EventRepository;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.domain.team.repository.TeamRepository;
import sejong.alom.teammate.global.exception.BusinessException;
import sejong.alom.teammate.global.exception.docs.ErrorCode;

@Service
@RequiredArgsConstructor
public class EventService {
	private final TeamRepository teamRepository;
	private final EventRepository eventRepository;

	@Transactional(readOnly = true)
	public List<EventResponse> getEvents(Long teamId, LocalDate startDate, LocalDate endDate) {
		Team team = teamRepository.findById(teamId)
			.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

		List<Event> events = eventRepository.findEventsInRange(team.getCalendar(), startDate, endDate);

		return events.stream()
			.map(EventResponse::from)
			.toList();
	}

	@Transactional
	public Long createEvent(Long teamId, EventCreateRequest request) {
		Team team = teamRepository.findById(teamId)
			.orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));

		Event event = request.to(team.getCalendar());

		return eventRepository.save(event).getId();
	}
}
