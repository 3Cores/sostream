package com.threecore.project.operator.key;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

import com.threecore.project.SimpleTest;
import com.threecore.project.operator.key.CommentScoreKeyer;

public class TestCommentScoreKeyer extends SimpleTest {

	@Test
	public void serialization() {
		SerializationUtils.serialize(new CommentScoreKeyer());
	}

}
