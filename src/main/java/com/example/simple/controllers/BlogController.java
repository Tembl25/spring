package com.example.simple.controllers;

import com.example.simple.models.Post;
import com.example.simple.models.User;
import com.example.simple.repository.PostRepository;
import com.example.simple.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;

@Controller
public class BlogController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/blog")
    public String blog(Model model, @AuthenticationPrincipal User user) {
        if (user != null) {
            model.addAttribute("user", user);
        }
        Iterable<Post> post = postRepository.findAll();
        model.addAttribute("post", post);
        return "blog";
    }

    @GetMapping("/blog/add")
    public String blogAdd(Model model, @AuthenticationPrincipal User user) {
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "blog-add";
    }

    @PostMapping("/blog/add")
    public String blogPostAdd(Principal principal, @RequestParam String title, @RequestParam String anons, @RequestParam String full_text, Model model){
        Post post = new Post(title, anons, full_text);
        User user = userRepository.findByUsername(principal.getName());
        post.setUser(user);
        postRepository.save(post);
        return "redirect:/blog";
    }

    @GetMapping("/blog/{id}")
    public String blogDetails(@PathVariable(value = "id") long id, Model model){
        if (!postRepository.existsById(id)) {
            return "redirect:/blog";
        }
        Optional<Post> post = postRepository.findById(id);
        ArrayList<Post> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("post", res);
        return "blog-details";
    }

    @GetMapping("/blog/{id}/edit")
    public String blogEdit(@PathVariable(value = "id") long id, Principal principal, Model model){
        if (!postRepository.existsById(id)) {
            return "redirect:/blog";
        }
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            Post postToEdit = post.get();
            if (!postToEdit.getUser().getUsername().equals(principal.getName())) {
                return "redirect:/blog";
            }
            ArrayList<Post> res = new ArrayList<>();
            res.add(postToEdit);
            model.addAttribute("post", res);
            return "blog-edit";
        }
        return "redirect:/blog";
    }

    @PostMapping("/blog/{id}/edit")
    public String blogPostEdit(Principal principal, @PathVariable(value = "id") long id, @RequestParam String title, @RequestParam String anons, @RequestParam String full_text, Model model){
        Post post = postRepository.findById(id).orElseThrow();
        if (!post.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/blog";
        }
        post.setTitle(title);
        post.setAnons(anons);
        post.setFull_text(full_text);
        postRepository.save(post);
        return "redirect:/blog";
    }

    @GetMapping("/blog/{id}/delete")
    public String confirmDelete(@PathVariable(value = "id") long id, Principal principal, Model model){
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            Post postToDelete = post.get();
            if (!postToDelete.getUser().getUsername().equals(principal.getName())) {
                return "redirect:/blog";
            }
            model.addAttribute("post", postToDelete);
            return "confirm-delete";
        }
        return "redirect:/blog";
    }


    @PostMapping("/blog/{id}/delete")
    public String blogPostDelete(@PathVariable(value = "id") long id, Principal principal, Model model){
        Post post = postRepository.findById(id).orElseThrow();
        if (!post.getUser().getUsername().equals(principal.getName())) {
            return "redirect:/blog";
        }
        postRepository.delete(post);
        return "redirect:/blog";
    }
}
