package uk.co.acuminous.julez;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.acuminous.julez.Result.ResultStatus;

public class ResultRecorder {

	List<Result> results = Collections.synchronizedList(new ArrayList<Result>());
		
	public void fail(String description) {
		results.add(Result.fail(description));
		System.err.println(description);		
	}
	
	public void fail() {
		results.add(Result.fail());
	}
	
	public void pass(String description) {
		results.add(Result.pass(description));
	}
	
	public void pass() {
		results.add(Result.pass());
	}

	public int failureCount() {
		int failures = 0;
		for (Result result : results) {
			failures += result.status == ResultStatus.FAIL ? 1 : 0;
		}
		return failures;
	}

	public int successCount() {
		int successes = 0;
		for (Result result : results) {
			successes += result.status == ResultStatus.PASS ? 1 : 0;
		}
		return successes;
	}
}
