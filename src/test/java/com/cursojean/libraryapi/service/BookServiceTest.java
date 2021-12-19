package com.cursojean.libraryapi.service;


import com.cursojean.libraryapi.exception.BusinessException;
import com.cursojean.libraryapi.model.entity.Book;
import com.cursojean.libraryapi.model.repository.BookRepository;
import com.cursojean.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        // cenario
        Book book = Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(book))
                .thenReturn((
                        Book.builder()
                                .id(1l)
                                .isbn("123")
                                .author("Fulano")
                                .title("As Aventuras")
                                .build()));

        //execucao
        Book saveBook = service.save(book);

        //verificacao
        assertThat(saveBook.getId()).isNotNull();
        assertThat(saveBook.getIsbn()).isEqualTo("123");
        assertThat(saveBook.getTitle()).isEqualTo("As Aventuras");
        assertThat(saveBook.getAuthor()).isEqualTo("Fulano");
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void sholdNotSaveBookWithDuplicatedISNB(){
        //cenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        //verificacao
        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("isbn já cadastrado");

        Mockito.verify(repository, Mockito.never()).save(book);

    }

    private Book createValidBook() {
        return Book.builder().author("Artur")
                .id(10l)
                .title("As aventuras")
                .isbn("001")
                .build();
    }

}
