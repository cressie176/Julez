package uk.co.acuminous.julez;

public class Result {
	
	enum ResultStatus { PASS, FAIL }
	
	long timestamp;
	ResultStatus status;
	String description;
	
	public Result(long timestamp, ResultStatus status, String description) {
		this.timestamp = timestamp;
		this.status = status;
		this.description = description;		
	}
	
	public static Result fail() {
		return fail(null);
	}
	
	public static Result fail(String description) {
		return new Result(System.currentTimeMillis(), ResultStatus.FAIL, description);
	}
	
	public static Result pass() {
		return pass(null);
	}
	
	public static Result pass(String description) {
		return new Result(System.currentTimeMillis(), ResultStatus.PASS, description);
	}	
	
}
