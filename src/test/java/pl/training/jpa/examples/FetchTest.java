package pl.training.jpa.examples;

import lombok.extern.java.Log;
import org.hibernate.LazyInitializationException;
import org.hibernate.SessionFactory;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.training.jpa.common.BaseTest;
import pl.training.jpa.entity.Comment;
import pl.training.jpa.entity.Post;
import pl.training.jpa.entity.PostLite;
import pl.training.jpa.entity.Tag;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Log
public class FetchTest extends BaseTest {

    private final Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();

    private Post result;
    private Long firstPostId;
    private Long firstTagId;

    @BeforeEach
    void setup() {
        statistics.setStatisticsEnabled(true);
        var firstTag = new Tag("Java");
        var secondTag = new Tag("Kotlin");
        var firstComment = new Comment("Komentarz pierwszy");
        var secondComment = new Comment("Komentarz drugi");
        var firstPost = new Post("Programowannie w Javie", "Bardzo fajny post");
        firstPost.setComments(List.of(firstComment, secondComment));
        firstPost.setTags(Set.of(firstTag, secondTag));
        var thirdComment = new Comment("Komentarz trzeci");
        var secondPost = new Post("Programowannie w Kotlin", "Bardzo fajny post");
        secondPost.setComments(List.of(thirdComment));
        withTransaction(entityManager -> {
            entityManager.persist(firstComment);
            entityManager.persist(secondComment);
            entityManager.persist(thirdComment);
            entityManager.persist(firstPost);
            entityManager.persist(secondPost);
            entityManager.persist(firstTag);
            entityManager.persist(secondTag);
        });
        firstTagId = firstTag.getId();
        firstPostId = firstPost.getId();
    }

    @Test
    void shouldNotLoadTagUntilTagNameIsAccessed() {
        withTransaction(entityManager -> {
            var persistedTag = entityManager.getReference(Tag.class, firstTagId);
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
    void shouldThrowExceptionWhenLoadingPostCommentsAfterTransactionIsClosed() {
        withTransaction(entityManager -> result = entityManager.find(Post.class, firstPostId));
        var comments = result.getComments();
        assertThrows(LazyInitializationException.class, () -> comments.forEach(comment -> log.info("Comment: " + comment)));
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
    void shouldEagerlyLoadPostWithCommentsBasedOnEntityGraph() {
        withTransaction(entityManager -> {
            Map<String, Object> properties = Map.of("javax.persistence.loadgraph", entityManager.getEntityGraph(Post.EAGER_COMMENTS));
            var persistedPost = entityManager.find(Post.class, firstPostId, properties);
            assertEquals(3, statistics.getEntityLoadCount());
        });
    }

    @Test
    void shouldEagerlyLoadPostWithCommentsBasedOnGeneratedEntityGraph() {
        withTransaction(entityManager -> {
            var postEntityGraph = entityManager.createEntityGraph(Post.class);
            postEntityGraph.addAttributeNodes("comments");
            Map<String, Object> properties = Map.of("javax.persistence.loadgraph", postEntityGraph);
            var persistedPost = entityManager.find(Post.class, firstPostId, properties);
            assertEquals(3, statistics.getEntityLoadCount());
        });
    }

    @Test
    void shouldLoadPostWithoutText() { // depends on provider
        withTransaction(entityManager -> {
            var persistedPost = entityManager.find(Post.class, firstPostId);
            var data = persistedPost.getData();
        });
    }

    @Test
    void envers() {
        withTransaction(entityManager -> {
            var persistedPost = entityManager.find(Post.class, firstPostId);
            persistedPost.setText("Jpa w praktyce");
            persistedPost.setTitle("Jpa");
        });
        withTransaction(entityManager -> {
            var persistedPost = entityManager.find(Post.class, firstPostId);
            entityManager.remove(persistedPost);
        });
        withTransaction(entityManager -> {
            var auditReader = AuditReaderFactory.get(entityManager);
            List<Object[]> result = auditReader
                    .createQuery()
                    .forRevisionsOfEntity(Post.class, false, false)
                    .getResultList();
            for (Object[] tuple : result) {
                var post = (Post) tuple[0];
                var revisionEntity = (DefaultRevisionEntity) tuple[1];
                var revisionType = (RevisionType) tuple[2];
                switch (revisionType) {
                    case ADD:
                        log.info("ADD: title - " + post.getTitle() + " timestamp: " + revisionEntity.getTimestamp());
                        break;
                    case MOD:
                        log.info("MOD: title - " + post.getTitle() + " timestamp: " + revisionEntity.getTimestamp());
                        break;
                    case DEL:
                        log.info("DEL: title - " + post.getTitle() + " timestamp: " + revisionEntity.getTimestamp());
                        break;
                }
            }
        });
        withTransaction(entityManager -> {
            var auditReader = AuditReaderFactory.get(entityManager);
            var revisionNumber = auditReader.getRevisionNumberForDate(new Date());
            var revisionNumbers = auditReader.getRevisions(Post.class, firstPostId);
            var post = auditReader.find(Post.class, firstPostId, revisionNumbers.get(0));
            log.info(post.toString());
        });
    }

    @Test
    void queryHints() {
        withTransaction(entityManager -> {
            var result = entityManager.createQuery("select p from Post p", Post.class)
                    .setHint("javax.persistence.query.timeout", 10000)
                    .setHint("org.hibernate.flushMode", "")
                    .getResultList();
        });
    }

}
