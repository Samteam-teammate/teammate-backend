package sejong.alom.teammate.domain.recruitment.entity;

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
import sejong.alom.teammate.domain.meta.entity.Position;
import sejong.alom.teammate.global.util.BaseTimeEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "recruitment_position")
public class RecruitmentPosition extends BaseTimeEntity {
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "recruitment_position_seq_gen"
	)
	@SequenceGenerator(
		name = "recruitment_position_seq_gen",
		sequenceName = "recruitment_position_seq"
	)
	@Column(name = "profile_skill_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recruitment_id")
	private Recruitment recruitment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "position_id")
	private Position position;
}
