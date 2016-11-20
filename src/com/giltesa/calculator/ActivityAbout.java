package com.giltesa.calculator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class ActivityAbout extends Activity
{

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// Se obtiene el TextView, se le cambia el color y se le añade un Intent que abrirá el navegador cuando sea pulsado.
		TextView aboutUrl = (TextView)findViewById(R.id.about_url);
		aboutUrl.setTextColor(Color.rgb(169, 208, 245));
		aboutUrl.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://giltesa.com/sobre-mi/")));
			}
		});
	}
}