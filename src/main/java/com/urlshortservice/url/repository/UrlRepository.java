package com.urlshortservice.url.repository;

import com.urlshortservice.url.model.Url;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface UrlRepository extends MongoRepository<Url,Long>
{
    Url findByShortLink(String shortLink);

}