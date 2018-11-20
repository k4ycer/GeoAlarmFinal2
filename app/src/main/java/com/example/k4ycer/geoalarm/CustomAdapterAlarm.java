package com.example.k4ycer.geoalarm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.k4ycer.geoalarm.model.Alarm;

import java.util.List;

public class CustomAdapterAlarm extends ArrayAdapter<Alarm> {
    Context context;
    CustomAdapterAlarmCallback callback;

    public CustomAdapterAlarm(@NonNull Context context, int resource, @NonNull List<Alarm> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View vista = convertView;
        if(vista == null){
            vista = LayoutInflater.from(context).inflate(R.layout.custom_layout_alarm,null);
        }

        TextView name = vista.findViewById(R.id.txtName);
        TextView description = vista.findViewById(R.id.txtDescription);
        final CheckBox status = vista.findViewById(R.id.checkBoxAlarm);

        final Alarm elemento = getItem(position);

        name.setText(elemento.getName());
        description.setText(elemento.getDescription());
        status.setChecked(elemento.getStatus());

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null){
                    callback.togglePressed(elemento, status.isChecked());
                }
            }
        });

        return vista;
    }

    public void setCallback(CustomAdapterAlarmCallback callback){
        this.callback = callback;
    }

    public interface CustomAdapterAlarmCallback{
        public void togglePressed(Alarm alarm, boolean status);
    }
}
