package com.giltesa.calculator;

import java.io.Serializable;
import java.math.BigDecimal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;


public class CalculatorLCD extends GridLayout implements Serializable
{
	private TextView			memory;
	private TextView			history;
	private EditText			operation;
	private static final long	serialVersionUID	= 1798102008860609015L;



	/**
	 * Constructor 1 de la clase.
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CalculatorLCD(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context);
	}



	/**
	 * Constructor 2 de la clase.
	 * 
	 * @param context
	 * @param attrs
	 */
	public CalculatorLCD(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}



	/**
	 * Constructor 3 de la clase.
	 * 
	 * @param context
	 */
	public CalculatorLCD(Context context)
	{
		super(context);
		initView(context);
	}



	/**
	 * Metodo que "infla" el XML con la interfaz de esta clase para mostrarla en el Activity principal cuando es "instanciada" con findViewById().
	 * 
	 * @param context
	 */
	private void initView(Context context)
	{
		final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.textview_calculator, this, true);

		memory = (TextView)findViewById(R.id.tvc_label_memory);
		history = (TextView)findViewById(R.id.tvc_label_history);
		operation = (EditText)findViewById(R.id.tvc_label_operation);
	}



	/**
	 * Metodo que escribe en la pantalla de memoria una M o no dependiendo del parametro recibido.
	 * 
	 * @param bol
	 */
	public void setMemory(boolean bol)
	{
		if( bol )
			memory.setText("M");
		else
			memory.setText("");
	}



	/**
	 * Devuelve true si en el TextView de Memory hay una M o no.
	 * 
	 * @return
	 */
	public boolean getMemory()
	{
		if( memory.getText().toString().equals("M") )
			return true;
		else
			return false;
	}



	/**
	 * Añade al historial el numero decimal sin decimales vacios.
	 * 
	 * @param num
	 */
	public void addHistory(BigDecimal num)
	{
		history.setText(getHistory() + String.valueOf(removeDecimalEmpty(num.doubleValue())).toLowerCase() + " ");
	}



	/**
	 * Añade o actualiza el historial con el operador recibido por parametro.
	 * 
	 * @param str
	 */
	public void addHistory(String operator)
	{
		String text = getHistory();
		int lengthText = text.length();
		int index = 0;

		// Si ya hay texto el indice sera de la antepenultima posicion de la cadena de modo que solo se trbajaran con los dos ultimos caracteres.
		if( !text.isEmpty() )
			index = lengthText - 2;


		// Si al final del historial ya hay un operador (+ - x / %) se sustituye por el recibido como parametro. Si no lo hay o es distinto se imprime sin mas.
		if( "+ - x / % ".indexOf(text.substring(index, lengthText)) != -1 )

			history.setText(text.substring(0, index) + operator + " ");
		else
			history.setText(getHistory() + operator + " ");
	}



	/**
	 * Devuelve el contenido del historial como un String.
	 * 
	 * @return
	 */
	public String getHistory()
	{
		return history.getText().toString();
	}



	/**
	 * Borra el historial.
	 */
	public void clearHistory()
	{
		history.setText("");
	}



	/**
	 * Recibe un Float por parametro, se le eliminan los decimales vacios y se escribe en la pantalla.
	 * 
	 * @param dato
	 */
	public void setOperation(BigDecimal dato)
	{
		operation.setText("" + String.valueOf(removeDecimalEmpty(dato.doubleValue())).toLowerCase());
	}



	/**
	 * Se escribe en pantalla el String recibido por parametro.
	 * 
	 * @param dato
	 */
	public void setOperation(String dato)
	{
		operation.setText(dato);
	}



	/**
	 * Se devuelve el contenido de la pantalla como un Float. Se elimina la coma si no hay decimales.
	 * 
	 * @return
	 */
	public BigDecimal getOperationBigDecimal()
	{
		String text = operation.getText().toString();

		// Si el ultimo caracter es una coma la elimina:
		if( text.charAt(text.length() - 1) == '.' )
			text = text.substring(0, text.length() - 1);

		return new BigDecimal(text);
	}



	/**
	 * Se devuelve el contenido de la pantalla como un String.
	 * 
	 * @return
	 */
	public String getOperationString()
	{
		return operation.getText().toString();
	}



	/**
	 * Se borra la pantalla.
	 */
	public void clearOperation()
	{
		operation.setText("");
	}



	/**
	 * Funcion que elimina los decimales vacios del Float recibido como parametro, devuelve un String con el numero.
	 * 
	 * @param num
	 * @return
	 */
	public static String removeDecimalEmpty(Double num)
	{
		if( num != num.intValue() )
			return String.valueOf(num);
		else
			return String.valueOf(num.intValue());
	}



	/**
	 * Funcion que realiza la operacion de dos numeros Float segun el caracter de Operador recibido.
	 * 
	 * @param num1
	 * @param operator
	 * @param num2
	 * @return
	 */
	public BigDecimal makeOperation(BigDecimal num1, Character operator, BigDecimal num2)
	{
		BigDecimal result;

		switch( operator )
		{
			case '+':
				result = num1.add(num2);
				break;
			case '-':
				result = num1.subtract(num2);
				break;
			case 'x':
				result = num1.multiply(num2);
				break;
			case '/':
				result = num1.divide(num2, 10, BigDecimal.ROUND_FLOOR);
				break;
			case '%':
				result = num1.multiply(num2);
				result = result.divide(new BigDecimal(100), 10, BigDecimal.ROUND_FLOOR);
				break;

			default:
				result = new BigDecimal(-1.0);
				break;
		}
		return result;
	}

}
