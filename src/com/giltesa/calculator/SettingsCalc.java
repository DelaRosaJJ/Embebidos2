package com.giltesa.calculator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;


public class SettingsCalc
{

	private static boolean						showNotificationBar;
	private static boolean						rememberLastResult;
	private static int							vibrationTime;
	private Context								context;
	private static HashMap< String, Object >	data		= new HashMap< String, Object >();
	private final String						fileName	= "config.dat";



	/**
	 * Constructor
	 * Se carga la configuración del fichero si es posible, si no se llama al método que configurara las propiedades a los valores de por defecto y creara el fichero.
	 * 
	 * @param context
	 */
	public SettingsCalc(Context context)
	{
		this.context = context;

		if( existsConfigurationFile() )
		{
			load();
		}
		else
		{
			showNotificationBar = true;
			rememberLastResult = false;
			vibrationTime = 25;
			save();
		}
	}



	/**
	 * El siguiente método lee desde un fichero, almacenado en la memoria interna del dispositivo (directorio: /data/data/com.giltesa.calculator/files),
	 * un conjunto de objetos para copiarlos a sus propiedades.
	 */
	public void load()
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(context.openFileInput(fileName)));

			showNotificationBar = (Boolean)ois.readObject();
			rememberLastResult = (Boolean)ois.readObject();
			vibrationTime = (Integer)ois.readObject();
			data = (HashMap< String, Object >)ois.readObject();

			ois.close();
		}
		catch( Exception e )
		{
			Log.e("Ficheros", "Error load()", e);
		}
	}



	/**
	 * Este método guarda las propiedades del objeto en el fichero de configuración.
	 */
	public void save()
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE)));

			oos.writeObject(showNotificationBar);
			oos.writeObject(rememberLastResult);
			oos.writeObject(vibrationTime);
			oos.writeObject(data);

			oos.close();
		}
		catch( Exception e )
		{
			Log.e("Ficheros", "Error save()", e);
		}
	}



	/**
	 * Método para comprobar si el fichero existe ya o no.
	 * 
	 * @return
	 */
	private boolean existsConfigurationFile()
	{
		try
		{
			FileInputStream fis = context.openFileInput(fileName);
			fis.close();
			return true;
		}
		catch( Exception e )
		{
			return false;
		}
	}



	// A partir de aquí los getters y setters:


	public static boolean isShowNotificationBar()
	{
		return showNotificationBar;
	}



	public static void setShowNotificationBar(boolean showNotificationBar)
	{
		SettingsCalc.showNotificationBar = showNotificationBar;
	}



	public static boolean isRememberLastResult()
	{
		return rememberLastResult;
	}



	public static void setRememberLastResult(boolean rememberLastResult)
	{
		SettingsCalc.rememberLastResult = rememberLastResult;
	}



	public static int getVibrationTime()
	{
		return vibrationTime;
	}



	public static void setVibrationTime(int vibrationTime)
	{
		if( vibrationTime >= 0 )
			SettingsCalc.vibrationTime = vibrationTime;
	}



	public static HashMap< String, Object > getData()
	{
		return data;
	}



	public static void setData(HashMap< String, Object > data)
	{
		SettingsCalc.data = data;
	}


}
