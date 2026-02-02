package sejong.alom.teammate.domain.team.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.alom.teammate.global.enums.TeamCategory;
import sejong.alom.teammate.global.util.BaseTimeEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "team")
public class Team extends BaseTimeEntity {
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "team_seq_gen"
	)
	@SequenceGenerator(
		name = "team_seq_gen",
		sequenceName = "team_seq"
	)
	@Column(name = "team_id")
	private Long id;

	@Column(nullable = false)
	private String name;

	private String bio;

	@Column(nullable = false)
	private TeamCategory category;

	private Integer maxMemberCount;

	private Integer currentMemberCount;

	private String teamImage;
}
