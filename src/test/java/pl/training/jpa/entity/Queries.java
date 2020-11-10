package pl.training.jpa.entity;

import javax.persistence.NamedQuery;

@NamedQuery(name = Queries.POSTS_REST_TEXT, query = "update Post p set p.text = 'test'")
public interface Queries {

    String POSTS_REST_TEXT = "postRestText";

}