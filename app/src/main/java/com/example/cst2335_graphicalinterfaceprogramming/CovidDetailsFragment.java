package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class CovidDetailsFragment  extends Fragment {
    private Bundle dataFromActivity;
    private AppCompatActivity parentActivity;

    public CovidDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.activity_covid_details, container, false);

        TextView msg= result.findViewById(R.id.message);
        msg.setText(dataFromActivity.getString(ChatRoomActivity.ITEM_SELECTED));
        TextView id= result.findViewById(R.id.id);
        id.setText("ID: "+dataFromActivity.getLong(ChatRoomActivity.ITEM_ID));
        CheckBox check=result.findViewById(R.id.check);
        check.setChecked(dataFromActivity.getString(ChatRoomActivity.ITEM_TYPE).equals("isSend"));

        Button hideButton = (Button)result.findViewById(R.id.hide);
        hideButton.setOnClickListener( clk -> parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit());
        return result;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }
}