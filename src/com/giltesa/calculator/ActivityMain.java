package com.giltesa.calculator;

import java.math.BigDecimal;
import java.util.HashMap;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ActivityMain extends Activity
{
	private CalculatorLCD	LCD;										
	private BigDecimal		memoryM				= new BigDecimal(0.0F); 
	private BigDecimal		memoryLCD			= new BigDecimal(0.0F);
	private Character		lastOperator		= ' ';					
	private String			lastKeyPressed		= " ";					
	private Integer			numberCharacterLCD	= 16;
	SettingsCalc			setting;									



	/**
	 * En el momento de crearse el activity se carga en pantalla el layaout, después las referencias y configuraciones.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// Se carga el layout del activity,adios barra de titulo :D
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_main);

		LCD = (CalculatorLCD)findViewById(R.id.main_CalculatorLCD);

		setting = new SettingsCalc(this);
		HashMap< String, Object > hm = SettingsCalc.getData();

		if( !SettingsCalc.isShowNotificationBar() )
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if( SettingsCalc.isRememberLastResult() && hm.size() > 0 )
		{
			LCD.setMemory((Boolean)hm.get("LCDgetMemory"));
			LCD.addHistory((String)hm.get("LCDgetHistory"));
			LCD.setOperation((String)hm.get("LCDgetOperationString"));
			memoryLCD = (BigDecimal)hm.get("memoryLCD");
			memoryM = (BigDecimal)hm.get("memoryM");
			lastOperator = (Character)hm.get("lastOperator");
			lastKeyPressed = (String)hm.get("lastKeyPressed");
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);

		if( hasFocus )
		{
			if( !SettingsCalc.isShowNotificationBar() )
				this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

			GridLayout layout = (GridLayout)findViewById(R.id.main_gridlayout);
			int height = layout.getHeight();
			int width = layout.getWidth();
			int row = layout.getRowCount();
			int column = layout.getColumnCount();

			int heightMemory = (int)( height - ( height / 1.10 ) );
			int heightHistory = (int)( height - ( height / 1.10 ) );
			int heightOperation = (int)( height - ( height / 1.15 ) );
			int heightBotones = (int)( height - heightOperation - heightHistory );
			int heightOneButton = (int)( heightBotones / ( row - 1 ) );
			int widthOneButton = (int)( width / column );

			TextView LCDMemory = (TextView)findViewById(R.id.tvc_label_memory);
			TextView LCDHistory = (TextView)findViewById(R.id.tvc_label_history);
			EditText LCDOperation = (EditText)findViewById(R.id.tvc_label_operation);
			LCDMemory.setHeight(heightMemory);
			LCDHistory.setHeight(heightHistory);
			LCDOperation.setHeight(heightOperation);

			LCDMemory.setTextSize(15);
			LCDHistory.setTextSize(15);
			LCDOperation.setTextSize(30);

			for( int count = 0 ; count < layout.getChildCount() ; count++ )
			{
				View v = layout.getChildAt(count);

				if( v instanceof Button )
				{
					Button vB = (Button)v;

					if( vB.getText().equals("0") )
					{
						vB.setWidth(widthOneButton * 2);
						vB.setHeight(heightOneButton);
					}
					else if( vB.getText().equals("=") )
					{
						vB.setWidth(widthOneButton);
						vB.setHeight(heightOneButton * 2);
					}
					else
					{
						vB.setWidth(widthOneButton);
						vB.setHeight(heightOneButton);
					}
				}
			}
		}
	}



	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		memoryM = new BigDecimal(savedInstanceState.getDouble("memoryM"));
		memoryLCD = new BigDecimal(savedInstanceState.getDouble("memoryLCD1"));
		LCD.setOperation(new BigDecimal(savedInstanceState.getDouble("memoryLCD2")));
		lastOperator = savedInstanceState.getChar("lastOperator");
		lastKeyPressed = savedInstanceState.getString("lastKeyPressed");
		LCD.addHistory(savedInstanceState.getString("History"));

		if( memoryM.compareTo(new BigDecimal(0.0F)) != 0 )
			LCD.setMemory(true);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putDouble("memoryM", memoryM.doubleValue());
		outState.putDouble("memoryLCD1", memoryLCD.doubleValue());
		outState.putDouble("memoryLCD2", LCD.getOperationBigDecimal().doubleValue());
		outState.putChar("lastOperator", lastOperator);
		outState.putString("lastKeyPressed", lastKeyPressed);
		outState.putString("History", LCD.getHistory());
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if( isFinishing() )
		{
			HashMap< String, Object > hm = new HashMap< String, Object >();
			hm.put("LCDgetMemory", LCD.getMemory());
			hm.put("LCDgetHistory", LCD.getHistory());
			hm.put("LCDgetOperationString", LCD.getOperationString());
			hm.put("memoryLCD", memoryLCD);
			hm.put("memoryM", memoryM);
			hm.put("lastOperator", lastOperator);
			hm.put("lastKeyPressed", lastKeyPressed);
			SettingsCalc.setData(hm);
			setting.save();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		this.getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip;

		switch( item.getItemId() )
		{
			case R.id.main_menu_copy:
				clip = ClipData.newPlainText("", LCD.getOperationString());
				clipboard.setPrimaryClip(clip);
				Toast.makeText(this, getResources().getString(R.string.main_menu_message_copied), Toast.LENGTH_SHORT).show();
				break;

			case R.id.main_menu_paste:
				clip = clipboard.getPrimaryClip();

				if( clip != null )
				{
					ClipData.Item item2 = clip.getItemAt(0);
					try
					{
						Double temp = Double.parseDouble(item2.getText().toString());
						LCD.setOperation(new BigDecimal(temp));
					}
					catch( Exception e )
					{
						Toast.makeText(this, getResources().getString(R.string.main_menu_message_no_copied), Toast.LENGTH_SHORT).show();
					}
				}
				break;

			case R.id.main_menu_settings:
				startActivity(new Intent(this, ActivityMenu.class));
				break;

			case R.id.main_menu_about:
				startActivity(new Intent(this, ActivityAbout.class));
				break;
		}
		return true;
	}

	public void eventNumericButton(View view)
	{
		final String textBTN = ( (Button)findViewById(view.getId()) ).getText().toString();
		String textLCD = LCD.getOperationString();
		( (Vibrator)getSystemService(VIBRATOR_SERVICE) ).vibrate(SettingsCalc.getVibrationTime());


		if( "+-x/%".indexOf(lastKeyPressed) != -1 )
		{
			memoryLCD = LCD.getOperationBigDecimal();
			LCD.clearOperation();
			textLCD = "";
		}
		else if( lastKeyPressed.equals("=") )
		{
			memoryLCD = new BigDecimal(0.0F);
			LCD.clearOperation();
			textLCD = "";
		}


		lastKeyPressed = textBTN;


		if( textLCD.length() < numberCharacterLCD )
		{
			if( textLCD.equals("0") && !textBTN.equals(".") )
				LCD.setOperation(textBTN);
			else if( !textLCD.isEmpty() && textLCD.indexOf(".") == -1 && textBTN.equals(".") )
				LCD.setOperation(textLCD + textBTN);
			else if( !textBTN.equals(".") )
				LCD.setOperation(textLCD + textBTN);
		}
	}

	public void eventMemoryButton(View view)
	{
		final String textBTN = ( (Button)findViewById(view.getId()) ).getText().toString();
		String textLCD = LCD.getOperationString();
		( (Vibrator)getSystemService(VIBRATOR_SERVICE) ).vibrate(SettingsCalc.getVibrationTime());


		if( textBTN.equals("MC") )
		{
			memoryM = new BigDecimal(0.0F);
			LCD.setMemory(false);
		}
		else if( textBTN.equals("MR") )
			LCD.setOperation(memoryM);
		else if( textBTN.equals("MS") )
		{
			memoryM = LCD.getOperationBigDecimal();
			LCD.setMemory(true);
		}
		else if( textBTN.equals("M+") )
			memoryM = memoryM.add(LCD.getOperationBigDecimal());
		else if( textBTN.equals("M-") )
			memoryM = memoryM.subtract(LCD.getOperationBigDecimal());
		else if( textBTN.equals("←") )
		{
			if( !textLCD.isEmpty() && "0123456789.←".indexOf(lastKeyPressed) != -1 )
			{
				String cadTemp = textLCD.substring(0, textLCD.length() - 1);
				if( !cadTemp.equals("") && !cadTemp.equals("-") )
					LCD.setOperation(cadTemp);
				else
					LCD.setOperation(new BigDecimal(0.0F));
				lastKeyPressed = textBTN;
			}
		}
		else if( textBTN.equals("CE") )
			LCD.setOperation(new BigDecimal(0.0F));
		else if( textBTN.equals("C") )
		{
			LCD.setOperation(new BigDecimal(0.0F));
			LCD.clearHistory();
			memoryLCD = new BigDecimal(0.0F);
		}

	}

	public void eventOperatorButton(View view)
	{
		final String textBTN = ( (Button)findViewById(view.getId()) ).getText().toString();
		( (Vibrator)getSystemService(VIBRATOR_SERVICE) ).vibrate(SettingsCalc.getVibrationTime());

		BigDecimal numLCD = LCD.getOperationBigDecimal();


		if( textBTN.equals("±") )
			LCD.setOperation(numLCD.multiply(new BigDecimal(-1.0F)));
		else if( textBTN.equals("√") )
		{
			if( LCD.getOperationBigDecimal().compareTo(new BigDecimal(0.0F)) == 1 )
			{
				LCD.setOperation(new BigDecimal(Math.sqrt(numLCD.doubleValue())));
				LCD.addHistory("sqrt(" + CalculatorLCD.removeDecimalEmpty(numLCD.doubleValue()) + ")");
			}
		}
		else if( textBTN.equals("1/x") )
			LCD.setOperation(new BigDecimal(1.0F).divide(numLCD, 30, BigDecimal.ROUND_FLOOR));
		else if( "+-x/%".indexOf(textBTN) != -1 )
		{
			if( "0123456789.←=".indexOf(lastKeyPressed) != -1 )
				LCD.addHistory(numLCD);
			if( memoryLCD.compareTo(new BigDecimal(0.0F)) != 0 && "+-x/%".indexOf(lastKeyPressed) == -1 )
				LCD.setOperation(LCD.makeOperation(memoryLCD, lastOperator, numLCD));
			else if( lastKeyPressed.equals("=") )
				memoryLCD = numLCD;
			lastOperator = textBTN.charAt(0);
			LCD.addHistory(lastOperator + "");
		}
		else if( textBTN.equals("=") )
		{
			if( memoryLCD.compareTo(new BigDecimal(0.0F)) != 0 && numLCD.compareTo(new BigDecimal(0.0F)) != 0 && "+-x/%".indexOf(lastOperator) != -1 )
			{
				LCD.setOperation(LCD.makeOperation(memoryLCD, lastOperator, numLCD));
				memoryLCD = new BigDecimal(0.0F);
				LCD.clearHistory();
			}
		}
		else if( textBTN.equals("x²") )
		{
			if( LCD.getOperationBigDecimal().compareTo(new BigDecimal(0.0F)) == 1 )
			{
				LCD.setOperation(LCD.makeOperation(numLCD, 'x', numLCD));
				LCD.addHistory(CalculatorLCD.removeDecimalEmpty(numLCD.doubleValue()) + "²");
			}
		}

		lastKeyPressed = textBTN;
	}

	public void eventMemuButton(View view)
	{
		( (Vibrator)getSystemService(VIBRATOR_SERVICE) ).vibrate(SettingsCalc.getVibrationTime());

		registerForContextMenu(view);
		openContextMenu(view); 
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, view, menuInfo);
		if( view.getId() == R.id.main_btn_menu )
		{
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.mathematical_menu, menu);
		}

	}


	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		( (Vibrator)getSystemService(VIBRATOR_SERVICE) ).vibrate(SettingsCalc.getVibrationTime());


		if( item.getTitle().equals(getResources().getString(R.string.mathematical_menu_sin)) )
		{
			LCD.setOperation(new BigDecimal(Math.sin(LCD.getOperationBigDecimal().doubleValue() * 2.0 * Math.PI / 360.0)));
			return true;
		}
		if( item.getTitle().equals(getResources().getString(R.string.mathematical_menu_cos)) )
		{
			LCD.setOperation(new BigDecimal(Math.cos(LCD.getOperationBigDecimal().doubleValue() * 2.0 * Math.PI / 360.0)));
			return true;
		}
		if( item.getTitle().equals(getResources().getString(R.string.mathematical_menu_tan)) )
		{
			LCD.setOperation(new BigDecimal(Math.tan(LCD.getOperationBigDecimal().floatValue() * 2.0 * Math.PI / 360.0)));
			return true;
		}
		if( item.getTitle().equals(getResources().getString(R.string.mathematical_menu_pi)) )
		{
			LCD.setOperation(new BigDecimal(Math.PI));
			return true;
		}
		else
		{
			return false;
		}
	}

}