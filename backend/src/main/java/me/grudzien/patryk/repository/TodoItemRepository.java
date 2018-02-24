package me.grudzien.patryk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.grudzien.patryk.domain.entities.TodoItem;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {}
