package scyang.mutilboard.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scyang.mutilboard.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
