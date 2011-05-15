UPDATE nk4um_forum_topic
SET    visible=(SELECT count(id)
                FROM nk4um_visible_forum_topic
                WHERE nk4um_visible_forum_topic.id=nk4um_forum_topic.id)=1
WHERE  nk4um_forum_topic.id=?;