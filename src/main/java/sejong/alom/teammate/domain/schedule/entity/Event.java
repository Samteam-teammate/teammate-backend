package sejong.alom.teammate.domain.schedule.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.alom.teammate.global.util.BaseTimeEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "event")
public class Event extends BaseTimeEntity {
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "event_seq_gen"
	)
	@SequenceGenerator(
		name = "event_seq_gen",
		sequenceName = "event_seq"
	)
	@Column(name = "event_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id")
	private Calendar calendar;

	@Column(nullable = false)
	private String title;

	private String description;

	@Column(nullable = false)
	private LocalDateTime startTime;

	private LocalDateTime endTime;

	public void update(String title, String description, LocalDateTime startTime, LocalDateTime endTime) {
		if (title != null) this.title = title;
		if (description != null) this.description = description;
		if (startTime != null) this.startTime = startTime;
		if (endTime != null) this.endTime = endTime;
	}
}
