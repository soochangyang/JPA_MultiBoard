package scyang.mutilboard.domain.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scyang.mutilboard.domain.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
