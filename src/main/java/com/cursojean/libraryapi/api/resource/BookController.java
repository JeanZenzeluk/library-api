package com.cursojean.libraryapi.api.resource;

import com.cursojean.libraryapi.api.dto.LoanDTO;
import com.cursojean.libraryapi.api.exception.ApiErros;
import com.cursojean.libraryapi.dto.BookDTO;
import com.cursojean.libraryapi.exception.BusinessException;
import com.cursojean.libraryapi.model.entity.Book;
import com.cursojean.libraryapi.model.entity.Loan;
import com.cursojean.libraryapi.service.BookService;
import com.cursojean.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("Book API")
@Slf4j
public class BookController {

    private final BookService service;
    private final LoanService loanService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creates a book")
    public BookDTO create(@RequestBody @Valid BookDTO dto){
        log.info(" creating a book for isbn: {} ", dto.getIsbn());
        Book entity = modelMapper.map(dto, Book.class);
        entity = service.save(entity);
        return modelMapper.map(entity, BookDTO.class);
    }

    @PutMapping("{id}")
    @ApiOperation("Updates a book")
    public BookDTO update(@PathVariable Long id, @RequestBody BookDTO dto){
        log.info(" updating a book of id: {} ", id);
        return service.getById(id).map( book -> {
            book.setAuthor(dto.getAuthor());
            book.setTitle(dto.getTitle());
            book = service.update(book);
            return modelMapper.map(book, BookDTO.class);

        }).orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND)));


//        Book entity = modelMapper.map(dto, Book.class);
//        return service
//                .getById(id)
//                .map(
//                        book ->
//                                modelMapper.map(service.update(Book.builder().build()), Book.class))
//                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
//        entity = service.save(entity);
//        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping("{id}")
    @ApiOperation("Obtains a book by id")
    public BookDTO get(@PathVariable Long id){
        log.info(" obtaining details for book id: {} ", id);
        return service
                .getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
    }

    @GetMapping
    @ApiOperation("Find books by params")
    public Page<BookDTO> find(BookDTO dto, Pageable pageRequest){
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);
        List<BookDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements() );
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Deletes a book by id")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Book sucesfully deleted")
    })
    public void delete(@PathVariable Long id){
        log.info(" deleting a book of id: {} ", id);
        Book book = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);
    }

    @GetMapping("{id}/loans")
    public Page<LoanDTO> loanByBook(@PathVariable Long id, Pageable pageable){
        Book book = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = loanService.getLoansByBook(book, pageable);

        List<LoanDTO> list = result.getContent()
                .stream()
                .map(loan -> {
                        Book loanBook = loan.getBook();
                        BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);
                        LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
                        loanDTO.setBook(bookDTO);
                        return loanDTO;
                }).collect(Collectors.toList());

        return new PageImpl<LoanDTO>(list, pageable, result.getTotalElements());
    }

}
