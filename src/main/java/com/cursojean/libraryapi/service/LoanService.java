package com.cursojean.libraryapi.service;

import com.cursojean.libraryapi.api.dto.LoanFilterDTO;
import com.cursojean.libraryapi.api.resource.BookController;
import com.cursojean.libraryapi.model.entity.Book;
import com.cursojean.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filter, Pageable pageable);

    Page<Loan> getLoansByBook(Book book, Pageable pageable);

    List<Loan> getAllLateLoans();
}
