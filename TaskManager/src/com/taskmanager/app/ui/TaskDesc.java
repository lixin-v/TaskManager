package com.taskmanager.app.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.taskmanager.app.bean.Task;
import com.taskmanager.app.dao.TaskDao;
import com.taskmanager.app.dao.impl.TaskDaoImpl;
import com.vegeta.taskmanager.R;
import com.widget.time.ScreenInfo;
import com.widget.time.WheelMain;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 任务明细类
 * 
 * @author lixin-v
 * 
 */
public class TaskDesc extends Activity {

	private final static String TAG = "TaskDesc";
	private ImageView imgBack;
	private Button btnSave;
	private TextView tvTaskStartTime;
	private TextView tvTaskEndTime;
	private TextView tvNote;
	private TextView tvTaskName;
	private TextView tvNotification;
	private TextView tvState;
	private Task task;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private TextView tvTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_desc);
		task =new Task();
		int id=getIntent().getExtras().getInt("taskId");
		task.setId(id);
		Log.d(TAG, "-->>taskid:"+task.getId());
		initHeadBar();
		
	}

	private void initHeadBar() {
		
		imgBack = (ImageView) findViewById(R.id.sub_head_back);
		tvTitle=(TextView) findViewById(R.id.sub_head_title);
		btnSave = (Button) findViewById(R.id.sub_head_save);
		tvTaskStartTime = (TextView) findViewById(R.id.tv_task_start_time);
		tvTaskEndTime = (TextView) findViewById(R.id.tv_task_end_time);
		tvNote = (TextView) findViewById(R.id.tv_task_note);
		tvTaskName = (TextView) findViewById(R.id.tv_task_name);
		tvNotification = (TextView) findViewById(R.id.tv_notification);
		tvState = (TextView) findViewById(R.id.tv_state);
		btnSave.setVisibility(View.GONE);
		imgBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		TaskDao dao=new TaskDaoImpl(this);
		task=dao.selectById(task.getId());
		
		tvTitle.setText(R.string.title_task_desc);
		tvNote.setText(task.getNote());
		tvState.setText(task.isFinish()?"已完成":"未完成");
		tvNotification.setText(task.isNotification()?"显示":"不显示");
		tvTaskStartTime.setText(task.getStartTime());
		tvTaskEndTime.setText(task.getEndTime());
		tvTaskName.setText(task.getName());
		

	}

	
}
