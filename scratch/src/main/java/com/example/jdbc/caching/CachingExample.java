package com.example.jdbc.caching;

import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@EnableCaching
@Configuration
class CachingExample {

    @Bean
    CacheManager cacheManager() {
        var cmch = new ConcurrentMapCacheManager();
        cmch.setStoreByValue(true);
        // forces Spring to serialize the objects and
        // thus recreate them from deserialization
        return cmch;
    }

    private Cat uuidFor(Optional<Cat> cat) {
        return cat.orElseThrow(() -> new IllegalArgumentException("got a bad cat!"));
    }

    @Bean
    ApplicationRunner caching(CatRepository catRepository) {
        return args -> Set.of("Felix", "Garfield")
                .stream()
                .map(name -> catRepository.save(new Cat(null, name)))
                .forEach(cat -> {
                    var id = cat.id;
                    var one = uuidFor(catRepository.findById(id));
                    var two = uuidFor(catRepository.findById(id));
                    Assert.state(one.uuid.equals(two.uuid), "the UUID matches, even though we didn't persist it");
                    Assert.state( one  != two  , "they shouldn't be the same references, though");
                });
    }
}


class Cat implements Serializable {

    @Id
    public Long id;

    String name;

    @Transient
    String uuid = UUID.randomUUID().toString();

    Cat(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Cat cat = (Cat) object;
        return Objects.equals(id, cat.id) && Objects.equals(name, cat.name) && Objects.equals(uuid, cat.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, uuid);
    }
}

interface CatRepository extends CrudRepository<Cat, Long> {


    String CAT_CACHE_KEY = "cats";

    @NonNull
    @Override
    @CacheEvict(value = CAT_CACHE_KEY, key = "#result.id")
    <S extends Cat> S save(@NonNull S s);

    @NonNull
    @Override
    @Cacheable(CAT_CACHE_KEY)
    Optional<Cat> findById(@NonNull Long aLong);
}