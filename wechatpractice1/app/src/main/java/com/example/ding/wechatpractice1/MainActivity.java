package com.example.ding.wechatpractice1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private LinkedList<PeopleStudentBean> sList = null;
    private LinkedList<PeopleTeacherBean> tList = null;
    private LinkedList<Bean> beans = null;


    /** 聊天message 格式 */
    private ListView listView;
    /** 信息编辑框 */
    private EditText edt;
    /** 信息发送按钮 */
    private Button btnEnter;

    private CustomAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sList = new LinkedList<PeopleStudentBean>();
        tList = new LinkedList<PeopleTeacherBean>();
        beans = new LinkedList<Bean>();
        String[] msg = new String[]{"asd","ergerterter","qwe","wrweqwreqw","asdasfafdafd","sdaasdasd","asdasdasdas","asfwewer"};

        // 0 是教师； 1 是学生
		for (int i = 0; i < 4; i++) {
			sList.add(new PeopleStudentBean(msg[i], R.drawable.you,"", 1));
			tList.add(new PeopleTeacherBean(msg[i + 4], R.drawable.me,"", 0));
		}

        // 归放到 同一个 类集合Bean中
        for (int j = 0; j < sList.size(); j++) {

            beans.add(sList.get(j));
            beans.add(tList.get(j));
        }
        setContentView(R.layout.activity_main);
        initViewsMethod();
        onHandleMethod();
    }
    /** 处理listView 的 item方法  */
    private void initViewsMethod(){
        listView = (ListView) findViewById(R.id.lvMessages);
        edt = (EditText) findViewById(R.id.edt);
        btnEnter = (Button) findViewById(R.id.enter);

        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                // TODO Auto-generated method stub

                menu.setHeaderTitle("提示：");
                menu.setHeaderIcon(android.R.drawable.stat_notify_error);
                menu.add(0, 0, 1, "删除");
                menu.add(1, 1, 0, "取消");

            }
        });

    }


    /** 处理发送信息的方法  */
    public void onHandleMethod(){
        adapter = new CustomAdapter(this, beans);
        listView.setAdapter(adapter);
        btnEnter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String txt = edt.getText().toString();
                if(txt==null) {
                    Toast.makeText(getApplicationContext(), "发送内容不能为空 !", Toast.LENGTH_SHORT).show();
                }else {
                    adapter.addItemNotifiChange(new Bean(txt, R.drawable.me, new Date() + "", 0));
                    edt.setText("");
                    listView.setSelection(beans.size());
                }
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case 0:
                Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Bean bean = (Bean) adapter.getItem(info.position);
                beans.remove(bean);
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }
}
