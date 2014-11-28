package com.example.dailyselfie.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.dailyselfie.R;
import com.example.dailyselfie.alarms.DailySelfieAlarmManager;

public class AlarmDialog extends DialogFragment implements OnClickListener{

	DailySelfieAlarmManager mAlarmManager;
	SparseArray<NumberPicker> pickers;
	
	
	public AlarmDialog(DailySelfieAlarmManager mAlarmManager){
		super();
		this.mAlarmManager = mAlarmManager;
		pickers = new SparseArray<NumberPicker>();
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
		    getDialog().setTitle("Title!");
		    View v = inflater.inflate(R.layout.alarm_dialog, null);
		    v.findViewById(R.id.alarmSetUp).setOnClickListener(this);
		    v.findViewById(R.id.alarmRemove).setOnClickListener(this);
		    v.findViewById(R.id.alarmCancel).setOnClickListener(this);
		    setUpNumberPicker(v, R.id.alarmNumberPickerSec, 5, 60);
		    setUpNumberPicker(v, R.id.alarmNumberPickerMin, 0, 60);
		    setUpNumberPicker(v, R.id.alarmNumberPickerHour, 0, 24);
	    return v;
	}
	
	private void setUpNumberPicker(View parentView, int numberPickerId, int minValue, int maxValue){
		NumberPicker numberPicker = (NumberPicker)parentView.findViewById(numberPickerId);
		numberPicker.setMinValue(minValue);
		numberPicker.setMaxValue(maxValue);
		numberPicker.setWrapSelectorWheel(true);
		numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		pickers.put(numberPickerId, numberPicker);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.alarmSetUp:
			int seconds = pickers.get(R.id.alarmNumberPickerSec).getValue();
			int minutes = pickers.get(R.id.alarmNumberPickerMin).getValue();
			int hours = pickers.get(R.id.alarmNumberPickerHour).getValue();
			mAlarmManager.setUpAlarm(hours, minutes, seconds);
			Toast.makeText(getActivity(), formMessage(hours, minutes, seconds), Toast.LENGTH_SHORT).show();
			break;
		case R.id.alarmRemove:
			mAlarmManager.removeAlarm();
			Toast.makeText(getActivity(), "Alarm removed  ", Toast.LENGTH_SHORT).show();
			break;
		case R.id.alarmCancel:
			break;			
		}
		dismiss();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		this.dismiss();
	}
	
	private String formMessage(int hours, int minutes, int seconds){
		StringBuilder sb = new StringBuilder();
		sb.append("The alarm will occur every ");
		if(hours != 0){
			sb.append(hours);
			sb.append(" hours,");
		}if(minutes != 0){
			sb.append(minutes);
			sb.append(" minutes and");
		}
		sb.append(seconds);
		sb.append(" seconds.");
		return sb.toString();
	}

}
