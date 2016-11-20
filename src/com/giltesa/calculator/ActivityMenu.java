package com.giltesa.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Switch;


public class ActivityMenu extends Activity
{
	private Switch			showNotificationBar;
	private Switch			rememberLastResult;
	private SeekBar			vibrationTime;
	private SettingsCalc	settings;



	/**
	 * Se crea el activity y se establece los valores de los elementos segun la configuracion guardada.
	 */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_menu);

		// Se instancia la clase de configuraciones:
		settings = new SettingsCalc(this);

		// Se obtienen los elementos del xml:
		showNotificationBar = (Switch)findViewById(R.id.menu_showNotificationBar);
		rememberLastResult = (Switch)findViewById(R.id.menu_rememberLastResult);
		vibrationTime = (SeekBar)findViewById(R.id.menu_vibrationTime);

		// Se modifica el estado de los elementos segun las configuraciones del fichero:
		showNotificationBar.setChecked(SettingsCalc.isShowNotificationBar());
		rememberLastResult.setChecked(SettingsCalc.isRememberLastResult());
		vibrationTime.setProgress(SettingsCalc.getVibrationTime());
	}



	/**
	 * Revisar 
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);

		if( hasFocus )
		{
			if( !SettingsCalc.isShowNotificationBar() )
				this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}



	/**
	 * Antes de eliminar el activity se guardan las configuraciones en el fichero.
	 */
	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if( isFinishing() )
		{
			SettingsCalc.setShowNotificationBar(showNotificationBar.isChecked());
			SettingsCalc.setRememberLastResult(rememberLastResult.isChecked());
			SettingsCalc.setVibrationTime(vibrationTime.getProgress());
			settings.save();
		}
	}
}
