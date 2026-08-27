package scyang.mutilboard.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scyang.mutilboard.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
