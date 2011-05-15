UPDATE nk4um_forum_topic_post
SET    visible=(SELECT count(id)
                FROM nk4um_visible_forum_topic_post
                WHERE nk4um_visible_forum_topic_post.id=nk4um_forum_topic_post.id)=1
WHERE  nk4um_forum_topic_post.forum_topic_id=?;