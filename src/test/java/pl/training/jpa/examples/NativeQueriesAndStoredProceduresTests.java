package pl.training.jpa.examples;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.training.jpa.common.BaseTest;
import pl.training.jpa.entity.Comment;
import pl.training.jpa.entity.Post;
import pl.training.jpa.entity.PostLite;
import pl.training.jpa.entity.Tag;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NativeQueriesAndStoredProceduresTests extends BaseTest {

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

    @Test
    void shouldExecuteStoredProcedure() {
        withTransaction(entityManager -> {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("procedure name");
            query.registerStoredProcedureParameter("parameterName", Double.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("resultName", Double.class, ParameterMode.OUT);
            query.setParameter("parameterName", 2.2);
            query.execute();
            Double result = (Double) query.getOutputParameterValue("resultName");
        });
        withTransaction(entityManager -> {
            StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery("ourName");
            query.registerStoredProcedureParameter("parameterName", Double.class, ParameterMode.IN);
            query.execute();
            Double result = (Double) query.getOutputParameterValue("resultName");
        });
    }

}
