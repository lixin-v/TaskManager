package com.taskmanager.app.dao;

import java.util.List;

import com.taskmanager.app.bean.Task;

public interface TaskDao {

	public int insert(Task task);
	public boolean delete(int id);
	public boolean update(Task task);
	public Task selectById(int id);
	public List<Task> selectByPage(int pageIndex,int pageSize);
	public List<Task> selectFinishedByPage(int pageIndex,int pageSize);
}
