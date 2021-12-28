package com.cursojean.libraryapi.service;


import com.cursojean.libraryapi.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface BookService {

    Book save(Book any);

    Optional<Book> getById(Long id);

    void delete(Book book)throws IllegalArgumentException;

    Book update(Book map);

    Page<Book> find(Book filter, Pageable pageRequest);

    Optional<Book> getBookByIsbn(String isbn);
}
