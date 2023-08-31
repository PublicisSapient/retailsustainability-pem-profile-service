package com.publicis.sapient.p2p.repository.implementation;

import com.publicis.sapient.p2p.model.User;
import com.publicis.sapient.p2p.repository.ProfileRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileRepositoryImpl implements ProfileRepository {

    @Autowired
    public MongoTemplate mongoTemplate;


    @Override
    public void addUserProfile(User userProfile) {
        mongoTemplate.save(userProfile);
    }

    @Override
    public void updateUserProfile(User userProfile) {
        Query query = new Query(Criteria.where("email").is(userProfile.getEmail()));
        Document dbDoc = new Document();
        mongoTemplate.getConverter().write(userProfile, dbDoc);
        Update update = Update.fromDocument(dbDoc, "_id");
        mongoTemplate.upsert(query, update, "user");
    }

    @Override
    public User getUserProfile(String emailId) {
        Query query = new Query(Criteria.where("email").is(emailId));
        return mongoTemplate.findOne(query,User.class);
    }

    @Override
    public User getUserById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query,User.class);
    }

    @Override
    public void deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, User.class);
    }
}
