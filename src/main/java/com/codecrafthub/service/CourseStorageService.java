package com.codecrafthub.service;

import com.codecrafthub.model.Course;
import com.codecrafthub.model.CourseStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseStorageService {
    private List<Course> courses = new ArrayList<>();
    private Path dataDir = Paths.get("data");
    private Path dataFile = dataDir.resolve("courses.json");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private synchronized void init() {
        try {
            if (Files.notExists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            if (Files.notExists(dataFile)) {
                Files.write(dataFile, "[]".getBytes());
            }
            load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize storage", e);
        }
    }

    private synchronized void load() {
        try {
            byte[] json = Files.readAllBytes(dataFile);
            if (json.length == 0) {
                courses = new ArrayList<>();
            } else {
                courses = objectMapper.readValue(json, new TypeReference<List<Course>>() {});
            }
        } catch (IOException e) {
            courses = new ArrayList<>();
        }
    }

    private synchronized void save() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(dataFile.toFile(), courses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<Course> findAll() {
        return new ArrayList<>(courses);
    }

    public synchronized Optional<Course> findById(String id) {
        return courses.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public synchronized Course create(Course c) {
        c.setId(UUID.randomUUID().toString());
        if (c.getStatus() == null) c.setStatus(CourseStatus.NOT_STARTED);
        courses.add(c);
        save();
        return c;
    }

    public synchronized Course update(String id, Course updated) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getId().equals(id)) {
                updated.setId(id);
                Course existing = courses.get(i);
                if (updated.getName() == null) updated.setName(existing.getName());
                if (updated.getDescription() == null) updated.setDescription(existing.getDescription());
                if (updated.getTargetDate() == null) updated.setTargetDate(existing.getTargetDate());
                if (updated.getStatus() == null) updated.setStatus(existing.getStatus());
                courses.set(i, updated);
                save();
                return updated;
            }
        }
        return null;
    }

    public synchronized boolean delete(String id) {
        boolean removed = courses.removeIf(c -> c.getId().equals(id));
        if (removed) save();
        return removed;
    }
}