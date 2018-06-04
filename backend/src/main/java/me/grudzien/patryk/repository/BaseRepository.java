package me.grudzien.patryk.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/*
 * @NoRepositoryBean - this ensures that Spring Data JPA won't try to create an implementation for our
 * base repository interface.
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends Repository<T, ID> {

	void delete(T deleted);

	List<T> findAll();

	Optional<T> findOne(ID id);

	T save(T objectToBeSaved);
}
