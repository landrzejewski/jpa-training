package pl.training.jpa.examples;

import lombok.extern.java.Log;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
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

@Log
public class FetchTest extends BaseTest {

    private final Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();

    private Long firstPostId;
    private Long tagId;

    @BeforeEach
    void setup() {
        statistics.setStatisticsEnabled(true);
        var tag = new Tag("Java");
        var firstComment = new Comment("Komentarz pierwszy");
        var secondComment = new Comment("Komentarz drugi");
        var firstPost = new Post("Programowannie w Javie", "Bardzo fajny post");
        firstPost.setComments(List.of(firstComment, secondComment));
        var thirdComment = new Comment("Komentarz trzeci");
        var secondPost = new Post("Programowannie w Kotlin", "Bardzo fajny post");
        secondPost.setComments(List.of(thirdComment));
        withTransaction(entityManager -> {
            entityManager.persist(firstComment);
            entityManager.persist(secondComment);
            entityManager.persist(thirdComment);
            entityManager.persist(firstPost);
            entityManager.persist(secondPost);
            entityManager.persist(tag);
        });
        tagId = tag.getId();
        firstPostId = firstPost.getId();
    }

    @Test
    void shouldNotLoadTagUntilTagNameIsAccessed() {
        withTransaction(entityManager -> {
            var persistedTag = entityManager.getReference(Tag.class, tagId);
            persistedTag.getId();
            assertEquals(0, statistics.getEntityLoadCount());
            persistedTag.getName();
            assertEquals(1, statistics.getEntityLoadCount());
        });
    }

    @Test
    void shouldLoadPostWithoutComments() {
        withTransaction(entityManager -> {
            var persistedPost = entityManager.find(Post.class, firstPostId);
            assertEquals(1, statistics.getEntityLoadCount());
        });
    }

    @Test
    void shouldLazyLoadCommentsUsingOneSelectWithJoin() {
        withTransaction(entityManager -> {
            var persistedPost = entityManager.find(Post.class, firstPostId);
            var comments = persistedPost.getComments();
            assertEquals(1, statistics.getEntityLoadCount());
            comments.forEach(comment -> log.info("Comment: " + comment));
            assertEquals(3, statistics.getEntityLoadCount());
        });
    }

    @Test
    void shouldLazyLoadCommentsUsingOneSelectWithJoinPerPost() {
        withTransaction(entityManager -> entityManager.createQuery("select p from Post p", Post.class)
                .getResultList()
                .forEach(post -> post.getComments().forEach(comment -> log.info("Comment: " + comment)))
        );
    }

    @Test
    void shouldEagerlyLoadPostWithComments() {
        withTransaction(entityManager -> entityManager.createQuery("select p from Post p join fetch p.comments pc", Post.class)
                .getResultList()
                .forEach(post -> post.getComments().forEach(comment -> log.info("Comment: " + comment)))
        );
    }

    @Test
    void shouldCreatePostProjection() {
        withTransaction(entityManager -> {
            var result = entityManager.createQuery("select new pl.training.jpa.entity.PostLite(p.title) from Post p", PostLite.class)
                    .getResultList();
            assertEquals(2, result.size());
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
