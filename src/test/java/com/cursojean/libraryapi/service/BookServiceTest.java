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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest(){
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //execucao
        Optional<Book> foundBook = service.getById(id);

        //verificacao
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());

    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id quando ele não existe na base.")
    public void getBookNotFoundByIdTest(){
        Long id = 1l;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<Book> foundBook = service.getById(id);

        //verificacao
        assertThat(foundBook.isPresent()).isFalse();
    }


    @Test
    @DisplayName("Deve deletar um livro por id")
    public void deleteTest(){
        //cenario
        Book book = Book.builder().id(1l).build();

        //execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.delete(book));

        //verificacao
        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve retornar vazio ao deletar um livro por Id quando ele não existe na base.")
    public void deleteInvalidBookTest(){
        //cenario
        Book book = new Book();

        //execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        //verificacao
        Mockito.verify(repository, Mockito.never()).delete(book);
    }



    @Test
    @DisplayName("Deve alterar um livro por id")
    public void updateBookTest(){
        //cenário
        long id = 1l;

        //livro a atualizar
        Book updatingBook = Book.builder().id(1l).build();

        //simulacao
        Book updatedBook = createValidBook();
        updatedBook.setId(id);

        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

        //execucao
        Book book = service.update(updatingBook);

        //verificações
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
    }

    @Test
    @DisplayName("Deve retornar vazio ao tentar atualizar um livro por Id quando ele não existe na base.")
    public void updateInvalidBookTest(){
        //cenario
        Book book = new Book();

        //execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));

        //verificacao
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest(){

        //cenario
        Book book = createValidBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> lista = Arrays.asList(book);

        Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execucao
        Page<Book> result = service.find(book, pageRequest);

        //verificacao
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve obter um livro pelo isbn")
    public void getBookIsbnTest(){
        String isbn = "1230";

        Book book = createValidBook();
//        book.setId(id);
        Mockito.when(repository.findByIsbn(isbn)).thenReturn(Optional.of(book));

        //execucao
        Optional<Book> foundBook = service.getBookByIsbn(isbn);

        //verificacao
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(book.getId());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        Mockito.verify(repository, Mockito.times(1)).findByIsbn(isbn);
    }

    private Book createValidBook() {
        return Book.builder().author("Artur")
                .id(10l)
                .title("As aventuras")
                .isbn("001")
                .build();
    }

}
