package com.mysql.sbb.Comment;

import com.mysql.sbb.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    Page<Comment> findByAuthor (SiteUser user,Pageable pageable);
    Page<Comment> findAll (Pageable pageable);
}
