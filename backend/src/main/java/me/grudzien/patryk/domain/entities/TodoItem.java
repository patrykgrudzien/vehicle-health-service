package me.grudzien.patryk.domain.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "TODO_ITEM", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class TodoItem implements Serializable {

	private static final long serialVersionUID = -2068059977728167749L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "COMPLETED")
	private Boolean completed;
}
