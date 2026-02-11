package sejong.alom.teammate.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sejong.alom.teammate.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	boolean existsByStudentId(Long studentId);
	Optional<Member> findByStudentId(Long studentId);
}
