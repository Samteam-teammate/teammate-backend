package sejong.alom.teammate.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "profile")
public class Profile extends BaseTimeEntity {
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "profile_seq_gen"
	)
	@SequenceGenerator(
		name = "profile_seq_gen",
		sequenceName = "profile_seq"
	)
	@Column(name = "profile_id")
	private Long id;

	@OneToOne
	@JoinColumn(name = "member_id", unique = true)
	private Member member;

	private String nickname;
	private String bio;
	private String portfolioUrl;
	private Boolean isOpenToWork;
	private Boolean isVisible;
	private String profileImage;
}
