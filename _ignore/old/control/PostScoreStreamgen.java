package com.threecore.project.control.stream;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.threecore.project.model.ModelCommons;
import com.threecore.project.model.PostScore;
import com.threecore.project.model.conf.AppConfiguration;
import com.threecore.project.operator.time.AscendingTimestamper;

@Deprecated
public class PostScoreStreamgen {
	
	private static final LocalDateTime START = LocalDateTime.of(2016, 1, 1, 12, 0, 0);
	
	public static DataStream<PostScore> getPostScores(StreamExecutionEnvironment env, AppConfiguration config) {		
		List<PostScore> list = PostScoreStreamgen.getCreationAndDeletionInsideRank(10);	
		
		DataStream<PostScore> scores = env.fromCollection(list);		
		
		scores.assignTimestampsAndWatermarks(new AscendingTimestamper<PostScore>());
		
		return scores;
	}
	
	public static List<PostScore> getCreation(final long numposts) {
		List<PostScore> scores = new ArrayList<PostScore>();
		
		LocalDateTime time;
		LocalDateTime post_creation_ts;
		
		int eventno = 0;
		
		for (long post_id = 0; post_id < numposts; post_id++) {
			time = START.plusMinutes(5 * eventno);
			post_creation_ts = time;
			PostScore score = new PostScore(time, post_creation_ts, post_id, 1L, "user-1", 10L, 0L, ModelCommons.UNDEFINED_LDT);
			scores.add(score);
			eventno++;
		}		
		
		return scores;
	}
	
	public static List<PostScore> getCreationAndComment(final long numposts) {
		List<PostScore> scores = new ArrayList<PostScore>();
		Map<Long, LocalDateTime> creationTime = new HashMap<Long, LocalDateTime>();
		
		LocalDateTime time;
		LocalDateTime post_creation_ts;
		LocalDateTime last_comment_ts;
		
		int eventno = 0;
		
		for (long post_id = 0; post_id < numposts; post_id++) {
			time = START.plusMinutes(5 * eventno);
			post_creation_ts = time;
			creationTime.put(post_id, post_creation_ts);
			PostScore score = new PostScore(time, post_creation_ts, post_id, 1L, "user-1", 10L, 0L, ModelCommons.UNDEFINED_LDT);
			scores.add(score);
			eventno++;
		}		
		
		for (long post_id = 0; post_id < numposts; post_id++) {
			time = START.plusMinutes(5 * eventno);
			post_creation_ts = creationTime.get(post_id);
			last_comment_ts = time;
			long commenters = numposts - post_id;
			PostScore score = new PostScore(time, creationTime.get(post_id), post_id, 1L, "user-1", 10L + (10L * commenters), commenters, last_comment_ts);
			scores.add(score);
			eventno++;
		}
		
		return scores;
	}
	
	public static List<PostScore> getCreationAndDeletionOutsideRank(final long numposts) {
		List<PostScore> scores = new ArrayList<PostScore>();
		Map<Long, LocalDateTime> creationTime = new HashMap<Long, LocalDateTime>();
		
		LocalDateTime time;
		LocalDateTime post_creation_ts;
		LocalDateTime last_comment_ts;
		
		int eventno = 0;
		
		for (long post_id = 0; post_id < numposts; post_id++) {
			time = START.plusMinutes(5 * eventno);
			post_creation_ts = time;
			creationTime.put(post_id, post_creation_ts);
			PostScore score = new PostScore(time, post_creation_ts, post_id, 1L, "user-1", 10L, 0L, ModelCommons.UNDEFINED_LDT);
			scores.add(score);
			eventno++;
		}		
		
		for (long post_id = 0; post_id < numposts; post_id++) {
			time = START.plusMinutes(5 * eventno);
			post_creation_ts = creationTime.get(post_id);
			last_comment_ts = time;
			long commenters = numposts - post_id;
			PostScore score = new PostScore(time, creationTime.get(post_id), post_id, 1L, "user-1", 0L, commenters, last_comment_ts);
			scores.add(score);
			eventno++;
		}
		
		return scores;
	}
	
	public static List<PostScore> getCreationAndDeletionInsideRank(final long numposts) {
		List<PostScore> scores = new ArrayList<PostScore>();
		Map<Long, LocalDateTime> creationTime = new HashMap<Long, LocalDateTime>();
		
		LocalDateTime time = START;
		LocalDateTime post_creation_ts;
		LocalDateTime last_comment_ts;
		
		int eventno = 0;
		
		for (long post_id = 0; post_id < numposts; post_id++) {
			time = START.plusMinutes(5 * eventno);
			post_creation_ts = time;
			creationTime.put(post_id, post_creation_ts);
			PostScore score = new PostScore(time, post_creation_ts, post_id, 1L, "user-1", 10L, 0L, ModelCommons.UNDEFINED_LDT);
			scores.add(score);
			eventno++;
		}		
		
		for (long post_id = numposts - 1; post_id >= 0; post_id--) {
			time = START.plusMinutes(5 * eventno);
			post_creation_ts = creationTime.get(post_id);
			last_comment_ts = time;
			long commenters = numposts - post_id;
			PostScore score = new PostScore(time, creationTime.get(post_id), post_id, 1L, "user-1", 0L, commenters, last_comment_ts);
			scores.add(score);
			eventno++;
		}
		
		return scores;
	}

}