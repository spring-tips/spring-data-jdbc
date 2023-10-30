package com.example.jdbc.bidirectionalinternal;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;

import java.util.HashSet;
import java.util.Set;

/**
 * todo this is what i think most people will be looking for!
 */
@Configuration
class BidirectionalInternalExample {

    @Bean
    ApplicationRunner internal(CustomerRepository customerRepository, OrderRepository orderRepository) {
        return args -> {

            var customer = customerRepository.save(new Customer(null, "Josh", new HashSet<>()));
            customer.addOrder(new Order(null, "1"));
            customer.addOrder(new Order(null, "2"));
            customer.addOrder(new Order(null, "3"));
            customer = customerRepository.save(customer);
            customer.orders().forEach(System.out::println);

            System.out.println("----------");
            orderRepository.findAll().forEach(System.out::println);
        };
    }
}

interface CustomerRepository extends CrudRepository<Customer, Long> {
}

interface OrderRepository extends CrudRepository<Order, Long> {
}

record Customer(@Id Long id, String name, Set<Order> orders) {

    public void addOrder(Order order) {
        this.orders().add(order);
    }

}

@Table("customer_orders")
record Order(@Id Integer id, String name) {
}