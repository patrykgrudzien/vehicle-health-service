package me.grudzien.patryk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.grudzien.patryk.domain.entities.TodoItem;
import me.grudzien.patryk.service.TodoItemService;

@RestController
@RequestMapping("/todo-item")
public class TodoItemController {

	private final TodoItemService todoItemService;

	@Autowired
	public TodoItemController(final TodoItemService todoItemService) {
		this.todoItemService = todoItemService;
	}

	@GetMapping
	public TodoItem getTodo(final Long id) {
		return todoItemService.getTodo(id);
	}

	@PostMapping("/add")
	public Long addTodo(@RequestBody final TodoItem todoItem) {
		return todoItemService.addTodo(todoItem);
	}
}
