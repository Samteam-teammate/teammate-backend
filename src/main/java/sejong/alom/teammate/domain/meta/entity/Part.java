package sejong.alom.teammate.domain.meta.entity;

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
import sejong.alom.teammate.global.util.BaseTimeEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "part")
public class Part extends BaseTimeEntity {
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "part_seq_gen"
	)
	@SequenceGenerator(
		name = "part_seq_gen",
		sequenceName = "part_seq"
	)
	@Column(name = "part_id")
	private Long id;

	@Column(unique = true, nullable = false)
	private String name;
}
