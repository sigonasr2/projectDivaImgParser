package com.example.demo;

public class TestResult {
	public boolean passed;
	public String message;
	public int count;
	public TestResult(boolean passed,int count,String message) {
		this.passed=passed;
		this.count=count;
		this.message=message;
	}
}
