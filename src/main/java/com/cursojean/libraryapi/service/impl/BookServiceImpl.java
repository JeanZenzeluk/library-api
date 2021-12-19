package com.cursojean.libraryapi.service.impl;

import com.cursojean.libraryapi.exception.BusinessException;
import com.cursojean.libraryapi.model.entity.Book;
import com.cursojean.libraryapi.model.repository.BookRepository;
import com.cursojean.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("isbn já cadastrado");
        }return repository.save(book);
    }
}
