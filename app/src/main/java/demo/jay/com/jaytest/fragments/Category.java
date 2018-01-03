package demo.jay.com.jaytest.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import demo.jay.com.jaytest.R;
import demo.jay.com.jaytest.activities.MainNavigationActivity;
import demo.jay.com.jaytest.adapters.ContactDetailsAdapter;
import demo.jay.com.jaytest.models.CategoryListModel;
import demo.jay.com.jaytest.models.ContactDetailsModel;
import demo.jay.com.jaytest.utility.Utility;
import io.realm.Realm;

import static demo.jay.com.jaytest.activities.MainNavigationActivity.addCategoryDataToRealm;
import static demo.jay.com.jaytest.activities.MainNavigationActivity.categoryAdapter;
import static demo.jay.com.jaytest.activities.MainNavigationActivity.personDetailsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class Category extends Fragment {

    public static ListView lvCategoryList;
    EditText et_Category;
    Button btn_submit;

    public Category() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View convertView =  inflater.inflate(R.layout.fragment_category, container, false);
        et_Category = (EditText) convertView. findViewById(R.id.et_Category);
        btn_submit=(Button)convertView.findViewById(R.id.btn_submit);
        lvCategoryList = (ListView)convertView. findViewById(R.id.lvCategoryList);
        lvCategoryList.setAdapter(categoryAdapter);


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utility.isBlankField(et_Category)) {
                    CategoryListModel contactDetailsModel = new CategoryListModel();
                    contactDetailsModel.setCategoryName(et_Category.getText().toString());
                    addCategoryDataToRealm(contactDetailsModel);
                }else{
                    Toast.makeText(getContext(),"Enter Category Title",Toast.LENGTH_LONG).show();
                }
            }
        });
        return convertView;
    }

}
