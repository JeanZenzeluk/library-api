package com.cursojean.libraryapi.service;

import com.cursojean.libraryapi.api.dto.LoanFilterDTO;
import com.cursojean.libraryapi.api.resource.BookController;
import com.cursojean.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filter, Pageable pageable);
}
