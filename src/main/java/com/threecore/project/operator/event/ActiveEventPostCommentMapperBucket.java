package com.threecore.project.operator.event;

import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.threecore.project.model.EventPostComment;
import com.threecore.project.model.event.BucketId;
import com.threecore.project.model.event.CommentMap;
import com.threecore.project.model.event.StandardCommentMap;

public class ActiveEventPostCommentMapperBucket implements CoFlatMapFunction<EventPostComment, BucketId, EventPostComment> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveEventPostCommentMapperBucket.class.getSimpleName());
	
	private static final long serialVersionUID = 1L;
	
	private CommentMap comment2Post;
	
	public ActiveEventPostCommentMapperBucket(){
		this.comment2Post = new StandardCommentMap();
	}

	@Override
	public void flatMap1(EventPostComment event, Collector<EventPostComment> out) throws Exception {
		LOGGER.debug("EVENT IN: " + event.asString());
		if (event.isPost()) {
			this.comment2Post.addPost(event.getPost());
			LOGGER.debug("POST ADDED: " + event.getPost().getPostId());
			out.collect(event);
		} else if (event.isComment()){
			long postCommentedId = this.comment2Post.addComment(event.getComment());
			if (postCommentedId != -1) {
				event.getComment().setPostCommentedId(postCommentedId);
				LOGGER.debug("COMMENT ADDED: " + event.getComment().getCommentId() + " to post " + event.getComment().getPostCommentedId());
				out.collect(event);
			} else {
				LOGGER.debug("COMMENT IGNORED: " + event.getComment().getCommentId());
			}
		} else if (event.isEOF()) {
			out.collect(event);
		}
	}

	@Override
	public void flatMap2(BucketId postids, Collector<EventPostComment> out) throws Exception {
		for (long postid : postids) {
			this.comment2Post.removePost(postid);
			LOGGER.debug("POST REMOVED: " + postid);
		}		
	}	
	
}