package scyang.mutilboard.domain.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import scyang.mutilboard.domain.member.dto.MemberResponse;
import scyang.mutilboard.domain.member.dto.MemberSearchCondition;
import scyang.mutilboard.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);


}
