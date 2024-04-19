package com.example.projblogplatformv2.repository;

import com.example.projblogplatformv2.dto.ErrorResponse;
import com.example.projblogplatformv2.exceptions.ResourceNotFoundException;
import com.example.projblogplatformv2.model.Blog;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BlogRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public List<Blog> findAll() {
        Session session = sessionFactory.getCurrentSession();
        String findAllBlogQuery = "SELECT b FROM Blog AS b WHERE b.deleteAt IS NULL";
        Query query = session.createQuery(findAllBlogQuery);
        List<Blog> blogs = query.list();

        if (blogs.isEmpty()) {
            throw new ResourceNotFoundException("Cannot find any blog!");
        }
        return blogs;
    }

    @Transactional
    public Optional<Blog> findById(UUID id) {
        Session session = sessionFactory.getCurrentSession();
        String findByIdQuery = "SELECT b FROM Blog AS b WHERE b.deleteAt IS NULL AND b.id = :id";
        Query query = session.createQuery(findByIdQuery);
        query.setParameter("id", id);
        Blog blog = (Blog) query.uniqueResult();
        return Optional.ofNullable(blog);
    }

    @Transactional
    public ErrorResponse create(Blog blog) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(blog);
            transaction.commit();
            return new ErrorResponse(HttpStatus.OK.value());
        }
        catch (Exception exception) {
            transaction.rollback();
            session.close();
            return new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "SQL Runtime error",
                    exception.getMessage());
        }
    }

    @Transactional
    public ErrorResponse update(Blog blog) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(blog);
            transaction.commit();
            session.close();
            return new ErrorResponse(HttpStatus.OK.value());
        }
        catch (Exception exception) {
            transaction.rollback();
            session.close();
            return new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "SQL Runtime error",
                    exception.getMessage());
        }
    }

    @Transactional
    public ErrorResponse remove(UUID id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            Optional<Blog> targetingBlog = this.findById(id);
            targetingBlog.ifPresent(blog -> {
                blog.setDeleteAt(Timestamp.valueOf(LocalDateTime.now()));
                session.update(blog);
            });
            transaction.commit();
            session.close();
            return new ErrorResponse(HttpStatus.OK.value());
        }
        catch (Exception exception) {
            transaction.rollback();
            session.close();
            return new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "SQL Runtime error",
                    exception.getMessage()
            );
        }
    }
}
