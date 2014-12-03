package com.taskmanager.app.dao.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.taskmanager.app.bean.Task;
import com.taskmanager.app.dao.TaskDao;
import com.taskmanager.app.db.DBHelper;

public class TaskDaoImpl implements TaskDao {

	private Context context;

	public TaskDaoImpl(Context context) {
		this.context = context;
	}

	@Override
	public int insert(Task task) {
		DBHelper db = new DBHelper(context);
		ContentValues values = new ContentValues();
		values.put("name", task.getName());
		values.put("startTime", task.getStartTime());
		values.put("endTime", task.getEndTime());
		values.put("note", task.getNote());
		values.put("isNotification", task.isNotification());
		values.put("isFinish", task.isFinish());
		long res = db.insert("tsm_task", values);
		return (int) res;
	}

	@Override
	public boolean delete(int id) {
		DBHelper db = new DBHelper(context);
		String[] args = new String[] { id + "" };
		boolean res = db.delete("tsm_task", "id=?", args);
		return res;
	}

	@Override
	public boolean update(Task task) {
		DBHelper db = new DBHelper(context);
		ContentValues values = new ContentValues();
		values.put("name", task.getName());
		values.put("startTime", task.getStartTime());
		values.put("endTime", task.getEndTime());
		values.put("note", task.getNote());
		values.put("isNotification", task.isNotification());
		values.put("isFinish", task.isFinish());
		boolean res = db.update("tsm_task", values, "id=?",
				new String[] { task.getId() + "" });
		return res;
	}

	@Override
	public Task selectById(int id) {
		DBHelper db = new DBHelper(context);

		Cursor cursor = db.findOne(false, "tsm_task", null, "id=?",
				new String[] { id + "" }, null, null, null, null);
		if (cursor == null)
			return null;

		String name = cursor.getString(cursor.getColumnIndex("name"));
		String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
		String endTime = cursor.getString(cursor.getColumnIndex("endTime"));
		String note = cursor.getString(cursor.getColumnIndex("note"));
		boolean isNotification = Boolean.parseBoolean(cursor.getString(cursor
				.getColumnIndex("isNotification")));

		Task task = new Task();
		task.setName(name);
		task.setStartTime(startTime);
		task.setEndTime(endTime);
		task.setNote(note);
		task.setNotification(isNotification);
		return task;
	}

	@Override
	public List<Task> selectByPage(int pageIndex, int pageSize) {
		DBHelper db = new DBHelper(context);
		pageIndex=pageIndex<1?1:pageIndex;
		int startRow=(pageIndex-1)*pageSize;
		Cursor cursor = db.findList(false, "tsm_task", null, null,
				null, null, null, null, startRow+","+pageSize);
		return parseToList(cursor);
	}
	
	@Override
	public List<Task> selectFinishedByPage(int pageIndex,int pageSize) {
		DBHelper db = new DBHelper(context);
		pageIndex=pageIndex<1?1:pageIndex;
		int startRow=(pageIndex-1)*pageSize;
		Cursor cursor = db.findList(false, "tsm_task", null, "isFinish=1",
				null, null, null, null, startRow+","+pageSize);
		return parseToList(cursor);
	}

	private List<Task> parseToList(Cursor cursor) {
		List<Task> list=new ArrayList<Task>();
		Task task;
		while(cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
			String endTime = cursor.getString(cursor.getColumnIndex("endTime"));
			String note = cursor.getString(cursor.getColumnIndex("note"));
			boolean isNotification = Boolean.parseBoolean(cursor.getString(cursor
					.getColumnIndex("isNotification")));
			boolean isFinish=(cursor.getInt(cursor
					.getColumnIndex("isFinish"))==1);
			task = new Task();
			task.setId(id);
			task.setName(name);
			task.setStartTime(startTime);
			task.setEndTime(endTime);
			task.setNote(note);
			task.setNotification(isNotification);
			task.setFinish(isFinish);
			list.add(task);
		}
		return list;
	}

	

}
