package com.threecore.project.operator.score.post.base;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.threecore.project.model.EventQueryOne;
import com.threecore.project.model.PostScore;
import com.threecore.project.model.score.post.PostScoreRepo2;
import com.threecore.project.tool.JodaTimeTool;

public abstract class AbstractPostScoreUpdater2 implements FlatMapFunction<EventQueryOne, PostScore> {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPostScoreUpdater2.class.getSimpleName());
	
	protected PostScoreRepo2 scores;
	
	protected long ts; // timestamp flowing with events
	
	public AbstractPostScoreUpdater2() {
		this.ts = Long.MIN_VALUE;
	}

	@Override
	public void flatMap(EventQueryOne event, Collector<PostScore> out) throws Exception {
		LOGGER.debug("EVENT IN: " + event.asString());
		if (event.isPost()) {
			long postTimestamp = event.getTimestamp();
			long postId = event.getId();			
			if (postTimestamp > this.ts) {
				LOGGER.debug("UPDATE (by ev) [from TS=" + JodaTimeTool.getStringFromMillis(this.ts) + " to TS=" + JodaTimeTool.getStringFromMillis(postTimestamp) + "]");
				this.scores.update(postTimestamp, out);
				this.ts = postTimestamp;
			} else {
				LOGGER.debug("ALREADY UP-TO-DATE (by ev) [TS=" + JodaTimeTool.getStringFromMillis(this.ts) + "]");	
			}			
			PostScore score = this.scores.addPost(postTimestamp, postId, event.getUserId(), event.getUser());			
			LOGGER.debug("NEW POST ADDED: " + postId);			
			out.collect(score);						
		} else if (event.isComment()) {
			long commentTimestamp = event.getTimestamp();
			long commentId = event.getId();
			long commentUserId = event.getUserId();
			long postCommentedId = event.getPostCommentedId();
			if (commentTimestamp > this.ts) {
				LOGGER.debug("UPDATE (by ev) [from TS=" + JodaTimeTool.getStringFromMillis(this.ts) + " to TS=" + JodaTimeTool.getStringFromMillis(commentTimestamp) + "]");
				this.scores.update(commentTimestamp, out);
				this.ts = commentTimestamp;
			} else {
				LOGGER.debug("ALREADY UP-TO-DATE (by ev) [TS=" + JodaTimeTool.getStringFromMillis(this.ts) + "]");
			}			
			PostScore score = this.scores.addComment(commentTimestamp, commentUserId, postCommentedId);			
			if (score == null) {
				LOGGER.debug("COMMENT IGNORED [" + commentId + "]");
				return;	
			}
			LOGGER.debug("NEW COMMENT ADDED [" + commentId + "->" + score.getPostId() + "]");
			out.collect(score);
		} else if (event.isEOF()) {
			LOGGER.debug("EOF");
			this.scores.executeEOF(out);		
			return;
		}		
	}

}
