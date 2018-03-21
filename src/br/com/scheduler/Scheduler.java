package br.com.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.com.enumerator.Period;
import br.com.input.reader.TxtReader;

public class Scheduler {	
	
	List<Event> eventListWithHour = new ArrayList<>();	
	List<Event> eventList = new ArrayList<>();
	int allSessionsTime;	
	
	private void setIntialAndFinalTime(Iterator<Event> eventIterator, Calendar eventHour, Integer morningDuration, Integer afternoonDuration){				
		
		while(eventIterator.hasNext() && morningDuration > 0){
			
			Event event = eventIterator.next();
			
			morningDuration = this.organizeHour(morningDuration, Period.MORNING, event, eventHour, eventIterator);
										
		}				
		
		while(eventIterator.hasNext()){
			
			Event event = eventIterator.next();
			
			afternoonDuration = this.organizeHour(afternoonDuration, Period.AFETERNOON, event, eventHour, eventIterator);
										
		}
	
		if(!eventIterator.hasNext() && allSessionsTime > 0){
			
			List<Event> eventWithoutTimeList = new ArrayList<>();
			eventIterator = null;				
			
			for(Event evt:eventList){
				
				if(evt.getInitialTime() == null)
					eventWithoutTimeList.add(evt);
				
			}
			
			if(eventWithoutTimeList != null && eventWithoutTimeList.size() > 0){
				eventIterator = eventWithoutTimeList.iterator();
				this.setIntialAndFinalTime(eventIterator, eventHour, morningDuration, afternoonDuration);
			}			
		}		
	}
	
	private List<Event> buildAndOrdenateEventsList(String path){
		
		List<Event> eventList = new ArrayList<>();
					
		TxtReader txtReader = new TxtReader();
		List<String> txtEvents = null;
		
		try {
			txtEvents = txtReader.readTxtFile(path);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		if(txtEvents != null){
			for (String txtEventLine:txtEvents) {
				Event event = new Event();				
				event.setName(txtEventLine);
				event.setDurationMinute(getMinutesOfTracks(txtEventLine));
				
				eventList.add(event);
			}
		}			
		
		Collections.sort(eventList);				
		
		return eventList;
		
	}
		
	private int getMinutesOfTracks(String track){											
		
		if(track.contains("lightning"))
			return 5;
		else{
			int initMinutePosition = track.length() - 5;
			int endMinutePosition = initMinutePosition + 2;
			
			if(track != null && !track.isEmpty()){
				try{
					if(track.length() >= endMinutePosition && 
							track.substring(initMinutePosition, endMinutePosition).trim().length() > 0)
						return Integer.valueOf(track.substring(initMinutePosition, endMinutePosition).trim());
					return 0;
				}catch(Exception e){
					e.printStackTrace();
					return 0;
				}
			}else
				return 0;
			
		}
	}
	
	private int organizeHour(Integer sessionDuration, Period session, Event event, Calendar eventHour, Iterator<Event> eventIterator){				
		
		if(allSessionsTime > 0){
			if((sessionDuration > 0 && sessionDuration % event.getDurationMinute() ==0) || (sessionDuration > allSessionsTime)
					|| sessionDuration > event.getDurationMinute()){
				Calendar initialHour = Calendar.getInstance();
				Calendar finalHour = Calendar.getInstance();		
				
				initialHour.setTimeInMillis(eventHour.getTimeInMillis());							
				
				event.setInitialTime(initialHour);				
				sessionDuration -= event.getDurationMinute();
				eventHour.set(Calendar.MINUTE, eventHour.get(Calendar.MINUTE) + event.getDurationMinute());
				finalHour.setTimeInMillis(eventHour.getTimeInMillis());
				
				allSessionsTime -= event.getDurationMinute();
				
				event.setFinalTime(finalHour);						
				
				eventListWithHour.add(event);			
				
				if(finalHour.get(Calendar.HOUR_OF_DAY) == 12){
					
					Event lunch = new Event();
					lunch.setName("Lunch");
					lunch.setDurationMinute(60);
					lunch.setInitialTime(finalHour);								
					eventHour.set(Calendar.HOUR_OF_DAY, 13);		
					lunch.setFinalTime(eventHour);
					eventListWithHour.add(lunch);
					
				}else if( allSessionsTime == 0 && finalHour.get(Calendar.HOUR_OF_DAY) >= 15 && finalHour.get(Calendar.HOUR_OF_DAY) <= 16){															
					eventListWithHour.add(createNetworkingEvent(finalHour));																							
				}
				
			}else if((sessionDuration == 0 && session == Period.AFETERNOON) || (sessionDuration > 0 && sessionDuration >= allSessionsTime)
					|| (sessionDuration < event.getDurationMinute())){
				
				List<Event> eventWithoutTimeList = new ArrayList<>();
				eventIterator = null;
				
				Calendar netWorkingHour = Calendar.getInstance();
				netWorkingHour.setTimeInMillis(eventHour.getTimeInMillis());
				
				for(Event evt:eventList){
					
					if(evt.getInitialTime() == null)
						eventWithoutTimeList.add(evt);
					
				}								
								
				eventListWithHour.add(createNetworkingEvent(netWorkingHour));
							
				eventIterator = eventWithoutTimeList.iterator();
				
				eventHour.set(Calendar.DAY_OF_YEAR,eventHour.get(Calendar.DAY_OF_YEAR) + 1);			
				eventHour.set(Calendar.HOUR_OF_DAY,9);
				eventHour.set(Calendar.MINUTE,0);
				sessionDuration = 180;
				this.setIntialAndFinalTime(eventIterator, eventHour, sessionDuration, sessionDuration + 60);
			}
			
			return sessionDuration;
		}		
		return 0;
	}
	
	public String runSchedule(String path){					
		
		Calendar initialHour = Calendar.getInstance();
		initialHour.set(Calendar.HOUR_OF_DAY, 9);
		initialHour.set(Calendar.MINUTE, 0);
		int track = 1;
		int dayOfYear = 0;
		
		String result = "";
		
		eventList = this.buildAndOrdenateEventsList(path);
		
		Iterator<Event> eventIterator = eventList.iterator();
		
		for(Event evt:eventList){
			allSessionsTime += evt.getDurationMinute();
		}
		
		setIntialAndFinalTime(eventIterator,initialHour,180,240);
		
		for(Event event:eventListWithHour){	
			
			if(dayOfYear == 0){
				dayOfYear = event.getInitialTime().get(Calendar.DAY_OF_YEAR);
				result += "Track " + track + ":\n" ;
			}else if(dayOfYear != event.getInitialTime().get(Calendar.DAY_OF_YEAR)){
				result += "\n";
				dayOfYear = event.getInitialTime().get(Calendar.DAY_OF_YEAR);
				track ++;
				result +="Track " + track + ":\n";
			}
			
			String hour = String.format("%02d",event.getInitialTime().get(Calendar.HOUR) == 0 ? 12:event.getInitialTime().get(Calendar.HOUR));			
			String minute = String.format("%02d",event.getInitialTime().get(Calendar.MINUTE));			
			String period = event.getInitialTime().get(Calendar.HOUR_OF_DAY) < 12 ? "AM":"PM";
			
			String date = hour + ":" + minute + period + " ";
						
			result += date + event.getName() + " \n";								
		}
		
		return result;
	}
	
	private Event createNetworkingEvent(Calendar netWorkingHour){
		
		Event networking = new Event();
		
		if(netWorkingHour.get(Calendar.HOUR_OF_DAY) < 16){
			netWorkingHour.set(Calendar.HOUR_OF_DAY,16);
			netWorkingHour.set(Calendar.MINUTE,0);
		}
		
		networking.setName("Networking Event");
		networking.setDurationMinute(60);
		networking.setInitialTime(netWorkingHour);
		
		return networking;
	}
}
