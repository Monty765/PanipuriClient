package com.hmkcode.android.adapters.lists;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hmkcode.android.gcm.R;

import java.util.ArrayList;

public class AdapterMain extends ArrayAdapter<PPlist> {

    Context context;
    int layoutResourceId;
    ArrayList<PPlist> students = new ArrayList<PPlist>();

    public AdapterMain(Context context, int layoutResourceId,
                          ArrayList<PPlist> studs) {
        super(context, layoutResourceId, studs);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.students = studs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        StudentWrapper StudentWrapper = null;

        if (item == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            item = inflater.inflate(layoutResourceId, parent, false);
            StudentWrapper = new StudentWrapper();
            StudentWrapper.pubname = (TextView) item.findViewById(R.id.pubname);
            StudentWrapper.add = (Button) item.findViewById(R.id.add);
            item.setTag(StudentWrapper);
        } else {
            StudentWrapper = (StudentWrapper) item.getTag();
        }

        PPlist student = students.get(position);
        final String fo=student.getPubname();
        StudentWrapper.pubname.setText(student.getPubname());

        StudentWrapper.add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "added"+fo, Toast.LENGTH_LONG).show();
            }
        });

        return item;

    }

    static class StudentWrapper {
        TextView pubname;
        Button add;

    }

}