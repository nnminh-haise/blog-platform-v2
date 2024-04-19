package com.example.projblogplatformv2.controller;

import com.example.projblogplatformv2.dto.CreateBlogDto;
import com.example.projblogplatformv2.dto.ErrorResponse;
import com.example.projblogplatformv2.dto.ResponseDto;
import com.example.projblogplatformv2.model.Blog;
import com.example.projblogplatformv2.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String findAllBlogs(ModelMap model) {
        ResponseDto<List<Blog>> response = blogService.findAll();

        if (!response.getStatus().equals(HttpStatus.OK.value())) {
            model.addAttribute("error", new ErrorResponse(
                    response.getStatus(),
                    "Resource not found!",
                    response.getMessage()));
        }

        model.addAttribute("data", response.getData());
        return "blog/index";
    }

    @RequestMapping(value = "/:id", method = RequestMethod.GET)
    public String findBlogById(
            @RequestParam("id") Long id,
            ModelMap model) {
        return "blog/index";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String routeToBlogEditor(
            ModelMap model) {
//        ResponseDto<Blog> response = blogService.findById(id);

        // TODO: upgrade this
//        if (response.hasStatus(HttpStatus.BAD_REQUEST)) {
//            model.addAttribute("errorMessage", new ErrorResponse(
//                HttpStatus.BAD_REQUEST.value(),
//                HttpStatus.BAD_REQUEST.toString(),
//                response.getMessage()
//            ));
//        }
//        else if (response.hasStatus(HttpStatus.INTERNAL_SERVER_ERROR)) {
//            model.addAttribute("errorMessage", new ErrorResponse(
//                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
//                    response.getMessage()
//            ));
//        }
//        else {
//            model.addAttribute("blog", response.getData());
//        }

        model.addAttribute("createBlogDto", new CreateBlogDto());
        return "blog/edit";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveBlog(
            @ModelAttribute("createBlogDto") CreateBlogDto createBlogDto,
            ModelMap model) {
        String resultView = "blog/edit";

        if (createBlogDto == null) {
            model.addAttribute("errorMessage", new ErrorResponse(
               HttpStatus.BAD_REQUEST.value(),
               HttpStatus.BAD_REQUEST.toString(),
               "Invalid blog"
            ));
            return resultView;
        }

        ResponseDto<Blog> response = this.blogService.create(createBlogDto);
        if (!response.hasStatus(HttpStatus.OK)) {
            model.addAttribute("errorMessage", new ErrorResponse(
                    response.getStatus(),
                    response.getStatus().toString(),
                    response.getMessage()
            ));
            return resultView;
        }

        model.addAttribute("data", "OK");
        return "blog/index";
    }
}
