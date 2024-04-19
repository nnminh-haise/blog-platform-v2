package com.example.projblogplatformv2.service;

import com.example.projblogplatformv2.dto.CreateBlogDto;
import com.example.projblogplatformv2.dto.ErrorResponse;
import com.example.projblogplatformv2.dto.ResponseDto;
import com.example.projblogplatformv2.dto.UpdateBlogDto;
import com.example.projblogplatformv2.exceptions.ResourceNotFoundException;
import com.example.projblogplatformv2.model.Blog;
import com.example.projblogplatformv2.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;

    private String getSlug(String title) {
        return title
                .toLowerCase()
                .trim()
                .replace(" ", "-");
    }

    // TODO: update this method logic to remove the bypass constrains code
    public ResponseDto<Blog> create(CreateBlogDto dto) {
        Date currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
        Blog newBlog = new Blog();
        newBlog.setTitle(dto.getTitle());
        newBlog.setDescription(dto.getDescription());
        // ! This is a temporary dummy code to bypass the not null constrains
        dto.setAttachment(dto.getTitle() + "'s attachments");
        newBlog.setCreateAt(currentTimestamp);
        newBlog.setUpdateAt(currentTimestamp);
        newBlog.setSlug(getSlug(dto.getTitle()));

        ErrorResponse errorResponse = this.blogRepository.create(newBlog);

        return errorResponse.ifHasError(
                () -> new ResponseDto<>(
                        errorResponse.getStatus(),
                        errorResponse.getMessage()),
                // * otherwise
                () -> new ResponseDto<>(
                        HttpStatus.OK.value(),
                        "Success", newBlog));
    }

    public ResponseDto<List<Blog>> findAll() {
        try {
            List<Blog> blogs = this.blogRepository.findAll();
            return new ResponseDto<>(
                    HttpStatus.OK.value(),
                    "Success",
                    blogs);
        }
        catch (ResourceNotFoundException exception) {
            return new ResponseDto<>(
                    HttpStatus.NOT_FOUND.value(),
                    exception.getMessage());
        }
    }

    public ResponseDto<Blog> findById(UUID id) {
        if (id == null) {
            return new ResponseDto<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid ID");
        }

        Optional<Blog> blog = this.blogRepository.findById(id);
        if (blog.isPresent()) {
            return new ResponseDto<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Cannot find any blog with " + id);
        }
        return new ResponseDto<>(
                HttpStatus.OK.value(),
                "Success",
                blog.get());
    }

    public ResponseDto<Blog> update(UUID id, UpdateBlogDto dto) {
        Optional<Blog> targetingBlog = this.blogRepository.findById(id);
        if (!targetingBlog.isPresent()) {
            return new ResponseDto<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Cannot find any blog with " + id);
        }

        Blog newBlog = targetingBlog.get();
        newBlog.setTitle(dto.getTitle());
        newBlog.setDescription(dto.getDescription());
        newBlog.setAttachment(dto.getAttachment());
        newBlog.setSlug(getSlug(newBlog.getTitle()));
        newBlog.setPublishAt(dto.getPublishAt());
        newBlog.setHiddenStatus(dto.getHiddenStatus());

        ErrorResponse errorResponse = this.blogRepository.update(newBlog);
        return errorResponse.ifHasError(
                () -> new ResponseDto<>(
                        errorResponse.getStatus(),
                        errorResponse.getMessage()),
                // * otherwise
                () -> new ResponseDto<>(
                        HttpStatus.OK.value(),
                        "Success"));
    }

    public ResponseDto<Blog> remove(UUID id) {
        Optional<Blog> targetingBlog = this.blogRepository.findById(id);
        if (!targetingBlog.isPresent()) {
            return new ResponseDto<>(
                    HttpStatus.NOT_FOUND.value(),
                    "Cannot find any blog with " + id);
        }

        Blog removingBlog = targetingBlog.get();
        removingBlog.setDeleteAt(Timestamp.valueOf(LocalDateTime.now()));

        ErrorResponse errorResponse = this.blogRepository.update(removingBlog);
        return errorResponse.ifHasError(
                () -> new ResponseDto<>(
                        errorResponse.getStatus(),
                        errorResponse.getMessage()),
                // * otherwise
                () -> new ResponseDto<>(
                        HttpStatus.OK.value(),
                        "Success"));
    }
}
