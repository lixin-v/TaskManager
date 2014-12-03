package com.taskmanager.app.bean;

public class Task {

	private Integer id;
	private String name;
	private String startTime;
	private String endTime;
	private String note;
	private boolean isNotification;
	private boolean isFinish;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isNotification() {
		return isNotification;
	}

	public void setNotification(boolean isNotification) {
		this.isNotification = isNotification;
	}

	public boolean isFinish() {
		return isFinish;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", name=" + name + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", note=" + note
				+ ", isNotification=" + isNotification + ", isFinish="
				+ isFinish + "]";
	}

}
