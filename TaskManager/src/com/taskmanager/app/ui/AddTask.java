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
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 添加任务类
 * 
 * @author lixin-v
 * 
 */
public class AddTask extends Activity {

	private final static String TAG = "AddTask";
	private ImageView imgBack;
	private Button btnSave;
	private EditText txtTaskStartTime;
	private EditText txtTaskEndTime;
	private EditText txtNote;
	private EditText txtTaskName;
	private CheckBox cbNotification;
	WheelMain wheelMain;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_task);

		initHeadBar();
		// Task task = new Task();
		// task.setStartTime("2014-10-07 19:57");
		// notifyTask(task);
	}

	private void initHeadBar() {
		imgBack = (ImageView) findViewById(R.id.sub_head_back);
		btnSave = (Button) findViewById(R.id.sub_head_save);
		txtTaskStartTime = (EditText) findViewById(R.id.txt_task_start_time);
		txtTaskEndTime = (EditText) findViewById(R.id.txt_task_end_time);
		txtNote = (EditText) findViewById(R.id.txt_task_note);
		txtTaskName = (EditText) findViewById(R.id.txt_task_name);
		cbNotification = (CheckBox) findViewById(R.id.cb_notification);

		Calendar calendar = Calendar.getInstance();
		txtTaskStartTime.setText(calendar.get(Calendar.YEAR) + "-"
				+ (calendar.get(Calendar.MONTH) + 1) + "-"
				+ calendar.get(Calendar.DAY_OF_MONTH) + " "
				+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ calendar.get(Calendar.MINUTE));

		imgBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		txtTaskStartTime.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					showTimeDialog(txtTaskStartTime);
			}
		});
		txtTaskStartTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showTimeDialog(txtTaskStartTime);
			}
		});
		txtTaskEndTime.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					showTimeDialog(txtTaskEndTime);
			}
		});
		txtTaskEndTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showTimeDialog(txtTaskEndTime);
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Task task = new Task();
				task.setName(txtTaskName.getText().toString());
				task.setStartTime(txtTaskStartTime.getText().toString());
				task.setEndTime(txtTaskEndTime.getText().toString());
				task.setNotification(cbNotification.isChecked());
				task.setNote(txtNote.getText().toString());
				task.setFinish(false);
				if (!saveTask(task)) {
					Toast.makeText(AddTask.this,
							R.string.message_task_add_error, Toast.LENGTH_SHORT)
							.show();
				} else {
					notifyTask(task);
					Toast.makeText(AddTask.this,
							R.string.message_task_add_success,
							Toast.LENGTH_SHORT).show();
					AddTask.this.finish();
				}
			}
		});

	}

	protected void notifyTask(Task task) {
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		// create a PendingIntent that will perform a broadcast
		Intent intent = new Intent(this, MyReceiver.class);
		intent.putExtra("title", task.getName());
		intent.putExtra("note", task.getNote());
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

		Calendar c = Calendar.getInstance();
		String time = task.getStartTime();
		try {
			Date date = dateFormat.parse(time);
			c.setTime(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("-->>" + c.getTime().toString());
		// schedule an alarm
		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
	}

	protected boolean saveTask(Task task) {
		TaskDao dao = new TaskDaoImpl(this);

		try {

			if (dao.insert(task) == -1) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void showTimeDialog(final EditText txtTaskTime) {
		boolean hasTime = true;
		View timepickerview = LayoutInflater.from(AddTask.this).inflate(
				R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo(AddTask.this);
		wheelMain = new WheelMain(timepickerview, hasTime);
		wheelMain.screenheight = screenInfo.getHeight();
		String time = txtTaskTime.getText().toString();
		Calendar calendar = Calendar.getInstance();
		// if(JudgeDate.isDate(time, formatStr)){
		try {
			System.out.println("dialog date--》》" + dateFormat.parse(time));
			calendar.setTime(dateFormat.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// }

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		if (hasTime)
			wheelMain.initDateTimePicker(year, month, day, hour, min);
		else
			wheelMain.initDateTimePicker(year, month, day);

		new AlertDialog.Builder(AddTask.this).setTitle("选择日期")
				.setView(timepickerview)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						txtTaskTime.setText(wheelMain.getTime());
					}
				}).setNegativeButton("取消", null).show();
	}

	public static class MyReceiver extends BroadcastReceiver {
		public static final int NOTIFICATION_ID = 1;

		/**
		 * called when the BroadcastReceiver is receiving an Intent broadcast.
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v(TAG, "onreceive");
			String taskName = intent.getStringExtra("title");
			String taskNote = intent.getStringExtra("note");

			final NotificationManager nm = (NotificationManager) context
					.getSystemService(NOTIFICATION_SERVICE);
			Notification n = new Notification(R.drawable.logo, taskName,
					System.currentTimeMillis());
			//设置点击自动消失
			n.flags |= Notification.FLAG_AUTO_CANCEL ;
			//设置灯光
			n.ledARGB = 0xff00ff00;  
			n.ledOnMS = 300;  
			n.ledOffMS = 1000;  
			n.flags |= Notification.FLAG_SHOW_LIGHTS;
			
			//设置震动
			long[] vibrate = {0,100,200,300};  
			n.vibrate = vibrate; 
			/*
			 * set the sound of the alarm. There are two way of setting the
			 * sound
			 */
			// n.sound=Uri.parse("file:///sdcard/alarm.mp3");
			// n.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI,
			// "20");
			n.sound = Uri.parse("android.resource://"
					+ context.getPackageName() + "/" + R.raw.ring);
			CharSequence contentTitle = taskName;

			CharSequence contentText = taskNote;

			Intent notificationIntent = new Intent(context, Main.class);

			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);

			n.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);
			// Post a notification to be shown in the status bar
			nm.notify(NOTIFICATION_ID, n);

		}

	}
}
