package me.grudzien.patryk.service;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import me.grudzien.patryk.domain.entities.TodoItem;
import me.grudzien.patryk.repository.TodoItemRepository;

@Service
@Log4j
public class TodoItemService {

	private final TodoItemRepository todoItemRepository;

	@Autowired
	public TodoItemService(final TodoItemRepository todoItemRepository) {
		this.todoItemRepository = todoItemRepository;
	}

	public List<TodoItem> getTodos() {
		log.info(">> getTodos()");
		return todoItemRepository.findAll();
	}

	public TodoItem getTodo(final long id) {
		log.info(">> getTodo()");
		return todoItemRepository.findOne(id);
	}

	public Long addTodo(final TodoItem todoItem) {
		log.info(">> addTodo()");
		todoItemRepository.save(todoItem);
		return todoItem.getId();
	}

	public Long updateTodo(final TodoItem todoItem) {
		log.info(">> updateTodo()");
		todoItemRepository.save(todoItem);
		return todoItem.getId();
	}

	public void deleteTodo(final Long id) {
		log.info(">> deleteTodo()");
		todoItemRepository.delete(id);
	}
}