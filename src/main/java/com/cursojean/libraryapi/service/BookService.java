package com.cursojean.libraryapi.service;


import com.cursojean.libraryapi.model.entity.Book;
import org.springframework.stereotype.Service;

@Service
public interface BookService {

    Book save(Book any);
}
