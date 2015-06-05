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
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class pro_query extends Activity {
	private static final String NAMESPACE = "http://tempuri.org/";
	private static final String URL = "http://163.14.70.47:80/SNQuery.asmx";
	private static final String METHOD_NAME1 = "pro_the_last_record";
	private ListView listView;
	private Button reload;
	private int[] mPics;
	private String[] state;
	private String[] course_name;
	ArrayList<HashMap<String, Object>> list;
	private SimpleAdapter adapter;
	private String gets = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pro_query);
		// ������
		// http://www.2cto.com/kf/201402/281526.html
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		findview();// �s����ݦ�A���A��o��ơA���θ�ơA���
		reload = (Button) findViewById(R.id.reload);// ���s��z�����s
		reload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				findview();
			}
		});
	}

	public void findview() {
		connect_sql_get_the_last_record();// �s����ݦ�A���A��o���
		if (gets.length() > 0) {
			process_state_data();// ��Ƥ��γB�z
			listView = (ListView) findViewById(R.id.stu_list);
			listView.setAdapter(null);// �M�Ÿ��
			// ���ƥ[�JArrayList��
			list = new ArrayList<HashMap<String, Object>>();// �M�Ÿ��
			for (int i = 0; i < course_name.length; i++) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("pic", mPics[i]);
				item.put("course", course_name[i]);
				item.put("state", state[i]);
				list.add(item);
			}
			// �ۦ漶�gListview��xml�ӫD�ϥιw�]��
			adapter = new SimpleAdapter(this, list, R.layout.mylistview,
					new String[] { "pic", "course", "state" }, new int[] {
							R.id.myimageView1, R.id.mytextView1,
							R.id.mytextView2 })
			/*
			 * {
			 * 
			 * @Override public View getView(int position, View convertView,
			 * ViewGroup parent) { // TODO Auto-generated method stub
			 * LayoutInflater inflater = getLayoutInflater(); View view =
			 * inflater.inflate(android.R.layout.simple_list_item_2, parent,
			 * false); TextView textView =
			 * (TextView)findViewById(android.R.id.text1);
			 * textView.setTextColor(Color.BLUE); return view; } }
			 */
			;

			listView.setAdapter(adapter);

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) { // TODO Auto-generated method
												// stub
					Toast.makeText(getApplicationContext(),
							"�A��ܪ��O" + state[arg2] + ",id=" + arg2,
							Toast.LENGTH_SHORT).show();
					pass_value.this_course = course_name[arg2];// �s��

				}
			});
		} else {
			Toast.makeText(this, "�z�õL�}��", Toast.LENGTH_LONG).show();
		}
	}

	public void connect_sql_get_the_last_record() {
		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);
			request.addProperty("couid", pass_value.this_course.toString());
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

	public void process_state_data() // �N��Ƥ��Φ���Ӱ}�C�A�ҵ{�s���@�Ӱ}�C�A�ҵ{�W�٤@�Ӱ}�C
	{
		String[] use = gets.split("_");
		course_name = new String[use.length];
		state = new String[use.length];
		mPics = new int[use.length];
		for (int i = 0; i < use.length; i++) {
			course_name[i] = use[i].split("\\+")[0];
			if (use[i].split("\\+")[1].equals("0")) {
				state[i] = "�|���I�W";
				mPics[i] = R.drawable.red1;
			} else if (use[i].split("\\+")[1].equals("1")) {
				state[i] = "���";
				mPics[i] = R.drawable.yellow1;
			} else {
				state[i] = "�w��";
				mPics[i] = R.drawable.green1;
			}
		}
	}
}
