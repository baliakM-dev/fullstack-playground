package com.todo.todo_app.service;

import com.todo.todo_app.dto.tag.TagCreateRequest;
import com.todo.todo_app.dto.tag.TagUpdateRequest;
import com.todo.todo_app.entity.Tag;
import com.todo.todo_app.exception.ConflictException;
import com.todo.todo_app.exception.NotFoundException;
import com.todo.todo_app.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> list() {
        return tagRepository.findAll();
    }

    public Tag get(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag not found: " + id));
    }

    @Transactional
    public Tag create(TagCreateRequest req) {
        if (tagRepository.existsByNameIgnoreCase(req.name())) {
            throw new ConflictException("Tag name already exists: " + req.name());
        }
        return tagRepository.save(Tag.builder().name(req.name()).build());
    }

    @Transactional
    public Tag update(UUID id, TagUpdateRequest req) {
        Tag t = get(id);
        if (!t.getName().equalsIgnoreCase(req.name()) && tagRepository.existsByNameIgnoreCase(req.name())) {
            throw new ConflictException("Tag name already exists: " + req.name());
        }
        t.setName(req.name());
        return t;
    }

    @Transactional
    public void delete(UUID id) {
        tagRepository.delete(get(id));
    }
}