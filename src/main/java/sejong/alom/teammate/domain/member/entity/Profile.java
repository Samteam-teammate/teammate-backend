package sejong.alom.teammate.domain.member.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import sejong.alom.teammate.global.enums.Part;
import sejong.alom.teammate.global.enums.Skill;
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

	@ElementCollection(targetClass = Part.class)
	@CollectionTable(name = "profile_parts", joinColumns = @JoinColumn(name = "profile_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "part_name")
	private List<Part> profileParts = new ArrayList<>();

	@ElementCollection(targetClass = Skill.class)
	@CollectionTable(name = "profile_skills", joinColumns = @JoinColumn(name = "profile_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "skill_name")
	private List<Skill> profileSkills = new ArrayList<>();

	@Column(name = "scrap_count")
	private Integer scrapCount = 0;

	public void update(String nickname, String bio, String portfolioUrl, Boolean isOpenToWork,
		Boolean isVisible, List<Part> parts, List<Skill> skills) {
		if (nickname != null) this.nickname = nickname;
		if (bio != null) this.bio = bio;
		if (portfolioUrl != null) this.portfolioUrl = portfolioUrl;
		if (isOpenToWork != null) this.isOpenToWork = isOpenToWork;
		if (isVisible != null) this.isVisible = isVisible;
		if (parts != null) {
			this.profileParts.clear();
			this.profileParts.addAll(parts);
		}
		if (skills != null) {
			this.profileSkills.clear();
			this.profileSkills.addAll(skills);
		}
	}

	public void increaseScrapCount() {
		this.scrapCount++;
	}

	public void decreaseScrapCount() {
		if (this.scrapCount > 0) this.scrapCount--;
	}

	public void updateImageUrl(String imageUrl) {
		this.profileImage = imageUrl;
	}
}
