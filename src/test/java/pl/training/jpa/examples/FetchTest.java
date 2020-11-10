package pl.training.jpa.examples;

import lombok.extern.java.Log;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.training.jpa.common.BaseTest;
import pl.training.jpa.entity.Post;
import pl.training.jpa.entity.PostComment;
import pl.training.jpa.entity.Tag;

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
        var firstComment = new PostComment("Komentarz pierwszy");
        var secondComment = new PostComment("Komentarz drugi");
        var firstPost = new Post("Programowannie w Javie", "Bardzo fajny post");
        firstPost.setComments(List.of(firstComment, secondComment));
        var thirdComment = new PostComment("Komentarz trzeci");
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
    void shouldNotReadFromDatabaseUntilTagNameIsAccessed() {
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
    void shouldLazyLoadPostCommentsUsingOneSelectWithJoin() {
        withTransaction(entityManager -> {
            var persistedPost = entityManager.find(Post.class, firstPostId);
            /*var persistedPost = entityManager.createQuery("select p from Post p where p.id = :id", Post.class)
                    .setParameter("id", firstPostId)
                    .getSingleResult();*/
            var comments = persistedPost.getComments();
            assertEquals(1, statistics.getEntityLoadCount());
            comments.forEach(comment -> log.info("Comment: " + comment));
            assertEquals(3, statistics.getEntityLoadCount());
        });
    }

}
