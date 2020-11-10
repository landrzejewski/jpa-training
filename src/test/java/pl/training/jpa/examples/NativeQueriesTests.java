package pl.training.jpa.examples;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.training.jpa.common.BaseTest;
import pl.training.jpa.entity.Comment;
import pl.training.jpa.entity.Post;
import pl.training.jpa.entity.PostLite;
import pl.training.jpa.entity.Tag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NativeQueriesTests extends BaseTest {

    private final Post firstPost = new Post("Programowannie w Javie", "Bardzo fajny post");

    @BeforeEach
    void setup() {
        var firstComment = new Comment("Komentarz pierwszy");
        var secondComment = new Comment("Komentarz drugi");
        firstPost.setComments(List.of(firstComment, secondComment));
        withTransaction(entityManager -> {
            entityManager.persist(firstComment);
            entityManager.persist(secondComment);
            entityManager.persist(firstPost);
        });
    }

    @Test
    void shouldExecuteNativeQuery() {
        withTransaction(entityManager -> {
            var post = (PostLite) entityManager.createNativeQuery("select * from posts p where id = ?", "postMapping")
                    //(Post) entityManager.createNativeQuery("select * from posts p where id = ?", Post.class)
                    .setParameter(1, firstPost.getId())
                    .getSingleResult();
            assertEquals(firstPost.getTitle(), post.getShortName());
        });
    }

}
