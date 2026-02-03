package sejong.alom.teammate.domain.member.entity;

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
import sejong.alom.teammate.domain.meta.entity.Part;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "profile_part")
public class ProfilePart {
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "profile_part_seq_gen"
	)
	@SequenceGenerator(
		name = "profile_part_seq_gen",
		sequenceName = "profile_part_seq"
	)
	@Column(name = "profile_part_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id")
	private Profile profile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "part_id")
	private Part part;
}
