/**
 * 
 */
package com.rc_pro;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author csim
 * 
 */
public class pro_course extends Activity {
	private static final String NAMESPACE = "http://tempuri.org/";
	private static final String URL = "http://163.14.70.47:80/SNQuery.asmx";
	private static final String METHOD_NAME1 = "pro_course_data";
	private static final String METHOD_NAME2 = "ap_check";
	private ListView listView;
	private TextView apSSID;
	private RadioGroup rgroup;
	private RadioButton in_time_course;
	private RadioButton out_time_course;
	private String[] course_id;
	private String[] course_name;
	private int[] mPics;
	private int[] intime;
	ArrayList<HashMap<String, Object>> list;
	private SimpleAdapter adapter;
	private String gets = "";
	int use_check = 0;// �ΨӧP�_�O�_������
	int check_index = -1;// �O���Ҫ����p�U�ΨӰO����@�ӬO����
	int now_check = -1;// �ϥΪ��I��ҽҩθɽ�
	String now_ap = "";//�{�b�ϥΪ�ap��MAC_address
	String checks = "";//�s��^�ǭȡAY�N��ϥΪ��O�դ�AP�AN�N��ϥήե~AP
	String ap = "";//�{�b�ϥΪ�ap��SSID

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
		setContentView(R.layout.pro_course);
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

	}

	public void findview() {
		apSSID = (TextView) findViewById(R.id.apSSID);
		listView = (ListView) findViewById(R.id.listView);
		rgroup = (RadioGroup) findViewById(R.id.rgroup);
		in_time_course = (RadioButton) findViewById(R.id.in_course);
		out_time_course = (RadioButton) findViewById(R.id.re_course);
		get_mac();// ���ap�W��
		ap_address_check();// �T�{�O�_�ϥήդ�ap
		connect_sql_get_course();// ��o�Ѯv�W���ҵ{�s���H�νҵ{�W��
		if (gets.length() > 0) {
			process_data();// ��Ƥ��γB�z
			for (int i = 0; i < intime.length; i++) // �P�_�O�_������
			{
				if (intime[i] == 1) {
					rgroup.check(R.id.in_course);
					use_check = 1;// ������
				}
			}
			if (use_check == 0)// �S������
				rgroup.check(R.id.re_course);

			listView.setAdapter(null);// �M�Ÿ��
			// ���ƥ[�JArrayList��
			list = new ArrayList<HashMap<String, Object>>();// �M�Ÿ��
			for (int i = 0; i < course_name.length; i++) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("pic", mPics[i]);
				item.put("course", course_name[i]);
				item.put("no", null);
				list.add(item);
			}
			// �ۦ漶�gListview��xml�ӫD�ϥιw�]��
			adapter = new SimpleAdapter(this, list, R.layout.mylistview,
					new String[] { "pic", "course", "no" }, new int[] {
							R.id.myimageView1, R.id.mytextView1,
							R.id.mytextView2 });

			listView.setAdapter(adapter);

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) { // TODO Auto-generated method

					if (use_check == 0) // �O�ɽҪ��ɭ�
					{
						if (now_check == R.id.in_course) // �ϥΪ̫��U���ҫ��s��O�ثe�S������
						{
							Toast.makeText(getApplicationContext(),
									"�ثe�õL���ҡA�ЦܸɽҪ��A�U�}�l�ҵ{�I�W", Toast.LENGTH_SHORT)
									.show();
						} else // �ϥΪ̫��U�ɽҫ��s�A�ثe�O�ɽҩάO�Ĥ@����J(�w�])
						{
							if (checks.equals("Y"))// �P�_�O�_���դ�ip
							{
								Toast.makeText(
										getApplicationContext(),
										"�A��ܪ��O" + course_name[arg2] + ",id="
												+ arg2, Toast.LENGTH_SHORT)
										.show();
								pass_value.this_course = course_id[arg2];// �s��
								Intent intent = new Intent(pro_course.this,
										course_state.class);
								startActivity(intent);
							} else if (checks.equals("N")) {//���O�դ�ip
								opencheck_Dialog();
								Toast.makeText(
										getApplicationContext(),
										"�A��ܪ��O" + course_name[arg2] + ",id="
												+ arg2, Toast.LENGTH_SHORT)
										.show();
								pass_value.this_course = course_id[arg2];// �s��
							} else {
								Toast.makeText(getApplicationContext(),
										"��p�X�F�I�p��~", Toast.LENGTH_SHORT).show();
							}
						}

					} else // �O���Ҫ��ɭ�
					{
						if (now_check == R.id.re_course) // �ϥΪ̫��U���ҫ��s�A�ثe�O�ɽ�
						{
							if (checks.equals("Y"))// �P�_�O�_���դ�ip
							{
								Toast.makeText(
										getApplicationContext(),
										"�A��ܪ��O" + course_name[arg2] + ",id="
												+ arg2, Toast.LENGTH_SHORT)
										.show();
								pass_value.this_course = course_id[arg2];// �s��
								Intent intent = new Intent(pro_course.this,
										course_state.class);
								startActivity(intent);
							} else if (checks.equals("N")) {//���O�դ�ip
								opencheck_Dialog();
								Toast.makeText(
										getApplicationContext(),
										"�A��ܪ��O" + course_name[arg2] + ",id="
												+ arg2, Toast.LENGTH_SHORT)
										.show();
								pass_value.this_course = course_id[arg2];// �s��
							} else {
								Toast.makeText(getApplicationContext(),
										"��p�X�F�I�p��~", Toast.LENGTH_SHORT).show();
							}
						} else // �ϥΪ̫��U���ҫ��s�A�ثe�O���ҩάO�Ĥ@����J(�w�])
						{
							if (arg2 == check_index) {
								if (checks.equals("Y"))// �P�_�O�_���դ�ip
								{
									Toast.makeText(
											getApplicationContext(),
											"�A��ܪ��O" + course_name[arg2]
													+ ",id=" + arg2,
											Toast.LENGTH_SHORT).show();
									pass_value.this_course = course_id[arg2];// �s��
									Intent intent = new Intent(pro_course.this,
											course_state.class);
									startActivity(intent);
								} else if (checks.equals("N")) {//���O�դ�ip
									opencheck_Dialog();
									Toast.makeText(
											getApplicationContext(),
											"�A��ܪ��O" + course_name[arg2]
													+ ",id=" + arg2,
											Toast.LENGTH_SHORT).show();
									pass_value.this_course = course_id[arg2];// �s��
								} else {
									Toast.makeText(getApplicationContext(),
											"��p�X�F�I�p��~", Toast.LENGTH_SHORT)
											.show();
								}
							} else {
								Toast.makeText(getApplicationContext(),
										"�ثe���ҵ{�ëD���ҡA�ЦܸɽҪ��A�U�}�l���ҵ{�I�W",
										Toast.LENGTH_SHORT).show();
							}
						}

					}

				}
			});
		} else {
			Toast.makeText(this, "�z�õL�}��", Toast.LENGTH_LONG).show();
		}

		rgroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				now_check = checkedId;
				connect_sql_get_course();// ��o�Ѯv�W���ҵ{�s���H�νҵ{�W��
				process_data();// ��Ƥ��γB�z
				listView.setAdapter(null);// �M�Ÿ��
				list = new ArrayList<HashMap<String, Object>>();// �M�Ÿ��
				for (int i = 0; i < course_name.length; i++) {
					HashMap<String, Object> item = new HashMap<String, Object>();
					if (checkedId == R.id.in_course) {
						item.put("pic", mPics[i]);
					} else {
						item.put("pic", R.drawable.outtime);
					}
					item.put("course", course_name[i]);
					item.put("no", null);
					list.add(item);
				}
				// �ۦ漶�gListview��xml�ӫD�ϥιw�]��
				adapter = new SimpleAdapter(pro_course.this, list,
						R.layout.mylistview, new String[] { "pic", "course",
								"no" }, new int[] { R.id.myimageView1,
								R.id.mytextView1, R.id.mytextView2 });

				listView.setAdapter(adapter);
			}
		});
	}

	public void connect_sql_get_course() {
		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
			request.addProperty("id", pass_value.Uid.getText().toString());
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;// �YWS����J�Ѽƥ����n�[�o�@��_�hWS�S����
			envelope.setOutputSoapObject(request);
			HttpTransportSE ht = new HttpTransportSE(URL);
			ht.call((NAMESPACE + METHOD_NAME1), envelope);
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();

			gets = response.toString();

		} catch (Exception e) {
			e.printStackTrace();
			// check = "QQ";

		}
	}

	public void ap_address_check() {//�P�_�O�_���դ�AP
		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME2);
			request.addProperty("now_ap_address", now_ap);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;// �YWS����J�Ѽƥ����n�[�o�@��_�hWS�S����
			envelope.setOutputSoapObject(request);
			HttpTransportSE ht = new HttpTransportSE(URL);
			ht.call((NAMESPACE + METHOD_NAME2), envelope);
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();

			checks = response.toString();

		} catch (Exception e) {
			e.printStackTrace();
			// check = "QQ";

		}
	}

	public void process_data() // �N��Ƥ��Φ���Ӱ}�C�A�ҵ{�s���@�Ӱ}�C�A�ҵ{�W�٤@�Ӱ}�C
	{
		String[] use = gets.split("\\+");
		course_id = new String[use.length];
		course_name = new String[use.length];
		mPics = new int[use.length];
		intime = new int[use.length];
		for (int i = 0; i < use.length; i++) {
			if (use[i].split("_")[0].equals("1")) {
				intime[i] = 1;
				mPics[i] = R.drawable.intime;
				check_index = i;// ������@�ӬO����
			} else {
				intime[i] = 0;
				mPics[i] = R.drawable.outtime;
			}
			course_id[i] = use[i].split("_")[1];
			course_name[i] = use[i].split("_")[2];
		}
	}

	public void opencheck_Dialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(pro_course.this);
		dialog.setMessage("�{�b�ϥΪ��O�~����wifi : " + ap + "\n�аݭn�}�Ҷ�?");
		dialog.setTitle("����");
		dialog.setPositiveButton("�T�{", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(pro_course.this, course_state.class);
				startActivity(intent);
			}
		});
		dialog.setNegativeButton("���", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		dialog.show();
	}

	public void backDialog() {// ���Uback�A�߰ݭn���n����app
		AlertDialog.Builder dialog = new AlertDialog.Builder(pro_course.this);
		dialog.setMessage("�аݭn����App?");
		dialog.setTitle("����");
		dialog.setPositiveButton("�T�{", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				pro_course.this.finish();
				android.os.Process.killProcess(android.os.Process.myPid());
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

	public void get_mac() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (wifi.isWifiEnabled()) {
			ap = info.getSSID();
			apSSID.setText("AP : " + ap);
			now_ap = info.getBSSID();
		} else {
			apSSID.setText("�Х��}wifi");
		}
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
