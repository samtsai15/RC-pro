/**
 * 
 */
package com.rc_pro;

import java.util.List;

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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


// ����
public class course_state extends Activity {
	private static final String NAMESPACE = "http://tempuri.org/";
	private static final String URL = "http://163.14.70.47:80/SNQuery.asmx";
	private static final String METHOD_NAME1 = "pro_open_course";
	private static final String METHOD_NAME2 = "pro_close_course";
	private String ap_mac = "";// ��β{�bAP��MACADDRESS
	private String all_apmac = "";// ��ΩҦ��౵��AP��MACADDRESS
	List results;
	TextView mTextView;
	TextView search;
	Button close, query;
	private LinearLayout LL1;
	String check1 = "";
	String check2 = "";

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) // �������U�ϥΦ�method
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) {// ����U��^��
			ConfirmExit();// �����^�䲣�ͮĪG
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void ConfirmExit() {// �h�X�T�{
		AlertDialog.Builder ad = new AlertDialog.Builder(course_state.this);
		ad.setTitle("���}");
		ad.setMessage("�T�w�n���}?");
		ad.setPositiveButton("�O", new DialogInterface.OnClickListener() {// �h�X���s
					public void onClick(DialogInterface dialog, int i) {
						// TODO Auto-generated method stub
						connect_sql_close_course();// �����I�W
						course_state.this.finish();// ����activity
					}
				});
		ad.setNegativeButton("�_", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				// ���h�X���ΰ������ާ@
			}
		});
		ad.show();// �ܹ�ܮ�
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course_state);
		// ������
		// http://www.2cto.com/kf/201402/281526.html
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		findViews();
		connect_sql_open_course();// �}�ҽҵ{
		LL1.setBackgroundResource(R.drawable.teawake);
		if (check1.equals("y")) // �I�W�}�l��
		{
			LL1.setBackgroundResource(R.drawable.teawake);// �ϥΤp��C����
			time();// �˼ƭp�ɶ}�l
		} else {
			mTextView.setText("0:0");
		}

		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				connect_sql_close_course();// �����I�W
				LL1.setBackgroundResource(R.drawable.tsleep); // ���I���A�p����
				search.setTextColor(android.graphics.Color.WHITE);
			}
		});

	}

	private void findViews() {
		get_mac();// ��oAP_macAddress
		mTextView = (TextView) findViewById(R.id.show);
		search = (TextView) findViewById(R.id.reload);
		LL1 = (LinearLayout) findViewById(R.id.LL1);
		close = (Button) findViewById(R.id.close);
		query = (Button) findViewById(R.id.reload);
		// �Ѯv�i�H�ΨӬݤW�@���I�W�άO�?�I�W������
		query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(course_state.this, pro_query.class);
				startActivity(intent);
			}
		});
	}

	// �˼ƭp���`�@20����
	// �e�Q�����p��C���A���q�ɶ����I�W��Ǯ�
	// ��10�����A�p����O�O���I�W�A���q�ɶ����I�W����
	// �����I�W�ҵL��
	private void time() {
		new CountDownTimer(1200000, 1000) {
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				connect_sql_close_course();// �����I�W
			}

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				mTextView.setText(millisUntilFinished / 60000 - 10 + ":"
						+ millisUntilFinished % 60000 / 1000);
				if (millisUntilFinished <= 600000) {
					LL1.setBackgroundResource(R.drawable.tsleep); // �ɶ��촫�I��
					search.setTextColor(android.graphics.Color.WHITE);
				}
			}
		}.start();
	}

	// �}�ҽҵ{
	public void connect_sql_open_course() {
		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
			request.addProperty("couid", pass_value.this_course);
			request.addProperty("ap_mac", ap_mac);
			request.addProperty("all_apmac", ap_mac);//scan ��O���A�ϥγ�@mac�Y�i
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;// �YWS����J�Ѽƥ����n�[�o�@��_�hWS�S����
			envelope.setOutputSoapObject(request);
			HttpTransportSE ht = new HttpTransportSE(URL);
			ht.call((NAMESPACE + METHOD_NAME1), envelope);
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();

			check1 = response.toString();

		} catch (Exception e) {
			e.printStackTrace();
			check1 = "n";

		}
	}

	// �����ҵ{
	public void connect_sql_close_course() {
		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
			request.addProperty("couid", pass_value.this_course);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;// �YWS����J�Ѽƥ����n�[�o�@��_�hWS�S����
			envelope.setOutputSoapObject(request);
			HttpTransportSE ht = new HttpTransportSE(URL);
			ht.call((NAMESPACE + METHOD_NAME2), envelope);
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();

			check2 = response.toString();

		} catch (Exception e) {
			e.printStackTrace();
			check2 = "n";

		}
	}

	//scan ��o���mac_address&&AP��MACADDRESS
	//scan��O���A��w��@ap�Y�i
	public void get_mac() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifi.startScan();
		results = wifi.getScanResults();
		for (int i = 0; i < results.size(); i++) {
			all_apmac += results.get(i).toString().split(",")[1].split("SSID:")[1].trim();
			if (i == results.size()-1) {
				break;
			} else {
				all_apmac += ",";
			}
		}
		WifiInfo info = wifi.getConnectionInfo();
		ap_mac = info.getBSSID();
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
