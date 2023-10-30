package com.example.jdbc.repositories;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Collection;

@Configuration
class RepositoriesExample {

    @Bean
    ApplicationRunner bookRepositories(BookRepository bookRepository) {
        return args -> {
            bookRepository.save(new Book(null, "Cloud Native Java", "123"));
            bookRepository.save(new Book(null, "Fear & Loathing in PHP", "345"));
            debug("ISBN", bookRepository.findBookByIsbnContaining("345"));
            debug("NYT", bookRepository.findNytBestSellers());
        };
    }

    private void debug(String category, Collection<Book> books) {
        System.out.println(new StringBuilder().repeat("-", 100));
        System.out.println(category);
        books.forEach(System.out::println);
        System.out.println(new StringBuilder().repeat("-", 100));
    }

}

interface BookRepository extends ListCrudRepository<Book, Integer> {

    Collection<Book> findBookByIsbnContaining(String isbn);

    @Query(
            """
                select * from book b where b.title in 
                (
                  select nyt.title from nyt_best_seller nyt  
                )
            """)
    Collection<Book> findNytBestSellers();
}


record Book(@Id Integer id, String title, String isbn) {
}