package com.rc_pro;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button btn_next;
	private RelativeLayout RL1;
	int s = 1;
	private static final String NAMESPACE = "http://tempuri.org/";
	private static final String URL = "http://163.14.70.47:80/SNQuery.asmx";
	private static final String METHOD_NAME1 = "pro_info";
	private String check = "";
	private SharedPreferences sp;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backDialog();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// ������
		// http://www.2cto.com/kf/201402/281526.html
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		ActionBar actionBar = getActionBar();
		actionBar.hide();

		findview();
		read_userInfo(); //ŪuserInfo_pro.xml

		// RL1.setBackgroundResource(R.drawable.re);
		btn_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				connect_sql_get_check();
				if (check.equals("y")) {
					save_userInfo();
					Intent intent = new Intent(MainActivity.this,
							pro_course.class);
					startActivity(intent);
					MainActivity.this.finish();
				} else if (check.equals("n")) {
					Toast.makeText(v.getContext(), "�b���K�X��~", Toast.LENGTH_LONG)
							.show();
				} else {
					btn_next.setText(check);
				}
			}
		});

	}

	public void findview() {
		RL1 = (RelativeLayout) findViewById(R.id.RelativeLayout1);
		btn_next = (Button) findViewById(R.id.reload);
		pass_value.Uid = (EditText) findViewById(R.id.uid);
		pass_value.Upw = (EditText) findViewById(R.id.upw);
		sp = this.getSharedPreferences("userInfo_pro", Context.MODE_APPEND);
	}

	public void save_userInfo() {
		Editor editor = sp.edit();
		editor.putString("Uid", pass_value.Uid.getText().toString());
		editor.putString("Upw", pass_value.Upw.getText().toString());
		editor.commit();

	}

	public void read_userInfo() {
		sp = this.getSharedPreferences("userInfo_pro", Context.MODE_APPEND);
		// Editor editor = sp.edit();
		pass_value.Uid.setText(sp.getString("Uid", null));
		pass_value.Upw.setText(sp.getString("Upw", null));
		String tt1 = pass_value.Uid.getText().toString();// debug��
		String tt2 = pass_value.Upw.getText().toString();// debug��
		if (!pass_value.Uid.getText().toString().equals("")
				&& !pass_value.Upw.getText().toString().equals("")) {
			Intent intent = new Intent(MainActivity.this, pro_course.class);
			startActivity(intent);
			MainActivity.this.finish();
		}
	}

	public void connect_sql_get_check() {
		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
			request.addProperty("id", pass_value.Uid.getText().toString());
			request.addProperty("pw", pass_value.Upw.getText().toString());
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;// �YWS����J�Ѽƥ����n�[�o�@��_�hWS�S����
			envelope.setOutputSoapObject(request);
			HttpTransportSE ht = new HttpTransportSE(URL);
			ht.call((NAMESPACE + METHOD_NAME1), envelope);
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();

			check = response.toString();

		} catch (Exception e) {
			e.printStackTrace();
			check = "QQ";

		}
	}
	public void backDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
		dialog.setMessage("�аݭn����App?");
		dialog.setTitle("����");
		dialog.setPositiveButton("�T�{", new DialogInterface.OnClickListener() {  
		    public void onClick(DialogInterface dialog, int which) {  
		    	System.exit(0);
		    }  
		}); 
		dialog.setNegativeButton("���", new DialogInterface.OnClickListener() {  
		    public void onClick(DialogInterface dialog, int which) {  
		     
		    }  
		}); 
		dialog.show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
