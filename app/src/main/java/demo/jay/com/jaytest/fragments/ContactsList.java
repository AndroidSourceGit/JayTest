package demo.jay.com.jaytest.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import demo.jay.com.jaytest.R;
import demo.jay.com.jaytest.activities.ContactDetailsActivity;
import demo.jay.com.jaytest.activities.MainNavigationActivity;
import demo.jay.com.jaytest.adapters.ContactDetailsAdapter;
import demo.jay.com.jaytest.models.ContactDetailsModel;

import static demo.jay.com.jaytest.activities.MainNavigationActivity.addOrUpdatePersonDetailsDialog;
import static demo.jay.com.jaytest.activities.MainNavigationActivity.contactDetailsModelArrayList;
import static demo.jay.com.jaytest.activities.MainNavigationActivity.personDetailsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsList extends Fragment implements View.OnClickListener {

    private FloatingActionButton fabAddPerson;
    public static ListView lvPersonNameList;


    public ContactsList() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView =  inflater.inflate(R.layout.fragment_contacts_list, container, false);
        fabAddPerson = (FloatingActionButton)convertView. findViewById(R.id.fabAddPerson);
        lvPersonNameList = (ListView)convertView. findViewById(R.id.lvPersonNameList);
        lvPersonNameList.setAdapter(personDetailsAdapter);
        bindWidgetsWithEvents();
        MainNavigationActivity.getInstance().getContacts();

        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddPerson:
                addOrUpdatePersonDetailsDialog(null,-1);
                break;
        }
    }






    private void bindWidgetsWithEvents() {
        fabAddPerson.setOnClickListener(this);
        lvPersonNameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getContext(),ContactDetailsActivity.class);
                intent.putExtra("PersonID", contactDetailsModelArrayList.get(position).getId());
                startActivity(intent);
            }
        });
    }

}
