package br.com.scheduler;

import java.util.Calendar;

public class Event implements Comparable<Event>{
	
	private String name;
	
	private int durationMinute;
	
	private Calendar initialTime;
	
	private Calendar finalTime;		

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDurationMinute() {
		return durationMinute;
	}

	public void setDurationMinute(int minute) {
		this.durationMinute = minute;
	}

	public Calendar getInitialTime() {
		return initialTime;
	}

	public void setInitialTime(Calendar initialTime) {
		this.initialTime = initialTime;
	}

	public Calendar getFinalTime() {
		return finalTime;
	}

	public void setFinalTime(Calendar finalTime) {
		this.finalTime = finalTime;
	}

	@Override
	public int compareTo(Event o) {		
		return Integer.compare(o.getDurationMinute(), this.getDurationMinute());
	}	

}
