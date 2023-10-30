package com.example.jdbc.bidirectionalexternal;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Set;

@Configuration
class BidirectionalExternalExample {

    @Bean
    ApplicationRunner external(MinionRepository minions, PersonRepository persons) {
        return args -> {

            var scarletReference = AggregateReference.<Person, Long>to(persons.save(new Person(null, "Scarlet")).id());
            var gruReference = AggregateReference.<Person, Long>to(persons.save(new Person(null, "Gru")).id());

            Set.of("Bob", "Kevin", "Stuart").forEach(name -> minions.save(new Minion(null, name, scarletReference)));
            Set.of("Tim").forEach(name -> minions.save(new Minion(null, name, gruReference)));

            var minionsOfScarlet = minions.findByEvilMaster(scarletReference.getId());

            Assert.state(minionsOfScarlet.stream().anyMatch(m -> Set.of("Bob", "Kevin", "Stuart").contains(m.name())),
                    "there should be a match");
            minionsOfScarlet.forEach(System.out::println);
        };


    }

}

interface PersonRepository extends CrudRepository<Person, Long> {
}

record Minion(@Id Long id, String name, AggregateReference<Person, Long> evilMaster) {
}

interface MinionRepository extends CrudRepository<Minion, Long> {

    @Query("select * from minion where evil_master = :id")
    Collection<Minion> findByEvilMaster(Long id);
}

record Person(@Id Long id, String name) {
}
