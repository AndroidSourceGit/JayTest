package demo.jay.com.jaytest.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import demo.jay.com.jaytest.R;
import demo.jay.com.jaytest.activities.MainNavigationActivity;
import demo.jay.com.jaytest.models.CategoryListModel;
import demo.jay.com.jaytest.models.CategoryListModel;
import io.realm.Realm;
import io.realm.RealmResults;

import static demo.jay.com.jaytest.activities.MainNavigationActivity.categoryAdapter;
/**
 * Created by jay on 03-Jan-18.
 */

public class CategoryAdapter extends BaseAdapter {

    public ArrayList<CategoryListModel> categoryModelArrayList;
    public Context context;
    public LayoutInflater inflater;

    public CategoryAdapter(Context context, ArrayList<CategoryListModel> categoryModelArrayList) {
        this.context = context;
        this.categoryModelArrayList = categoryModelArrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return categoryModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Holder holder;
        if (v == null) {
            v = inflater.inflate(R.layout.inflate_list_item, null);
            holder = new Holder();
            holder.tvPersonName = (TextView) v.findViewById(R.id.tvPersonName);
            holder.ivEditPesonDetail=(ImageView)v.findViewById(R.id.ivEditPesonDetail);
            holder.ivEditPesonDetail.setVisibility(View.GONE);
            holder.ivDeletePerson=(ImageView)v.findViewById(R.id.ivDeletePerson);
            v.setTag(holder);
        } else {
            holder = (Holder) v.getTag();
        }

        holder.tvPersonName.setText(categoryModelArrayList.get(position).getCategoryName());
       /* holder.ivEditPesonDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryListModel dataToEditModel= MainNavigationActivity.getInstance().searchPerson(categoryModelArrayList.get(position).getId());
                MainNavigationActivity.getInstance().addOrUpdatePersonDetailsDialog(dataToEditModel,position);
            }
        });*/
        holder.ivDeletePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowConfirmDialog(context,categoryModelArrayList.get(position).getId(), position);
            }
        });
        return v;
    }

    class Holder {
        TextView tvPersonName;
        ImageView ivDeletePerson, ivEditPesonDetail;
    }

    public void ShowConfirmDialog(Context context, final int categoryId, final int position)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder
                .setMessage("Are you sure you want to delete this record?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Realm myRealm = Realm.getDefaultInstance();
                        RealmResults<CategoryListModel> results = myRealm.where(CategoryListModel.class).equalTo("id", categoryId).findAll();

                        myRealm.beginTransaction();
                        results.deleteFromRealm(0);//remove(0);
                        myRealm.commitTransaction();

                        categoryModelArrayList.remove(position);
                        categoryAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
