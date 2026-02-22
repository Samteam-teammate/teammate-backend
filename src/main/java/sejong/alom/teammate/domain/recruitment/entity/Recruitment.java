package sejong.alom.teammate.domain.recruitment.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejong.alom.teammate.domain.team.entity.Team;
import sejong.alom.teammate.global.util.BaseTimeEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "recruitment")
public class Recruitment extends BaseTimeEntity {
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "recruitment_seq_gen"
	)
	@SequenceGenerator(
		name = "recruitment_seq_gen",
		sequenceName = "recruitment_seq"
	)
	@Column(name = "recruitment_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	private LocalDateTime deadline;

	private String description;

	@OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RecruitmentPart> recruitmentParts = new ArrayList<>();

	@Column(name = "scrap_count")
	private Integer scrapCount = 0;

	public void update(LocalDateTime deadline, String description) {
		if (deadline != null) this.deadline = deadline;
		if (description != null) this.description = description;
	}

	public void updateParts(List<RecruitmentPart> newParts) {
		this.recruitmentParts.clear(); // 기존 파트 삭제 (orphanRemoval = true 설정 필요)
		this.recruitmentParts.addAll(newParts);
		newParts.forEach(part -> part.setRecruitment(this));
	}

	public void increaseScrapCount() {
		this.scrapCount++;
	}

	public void decreaseScrapCount() {
		if (this.scrapCount>0) this.scrapCount--;
	}
}
