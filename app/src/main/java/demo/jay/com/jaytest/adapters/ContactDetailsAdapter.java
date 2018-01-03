package demo.jay.com.jaytest.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import demo.jay.com.jaytest.R;
import demo.jay.com.jaytest.activities.MainNavigationActivity;
import demo.jay.com.jaytest.models.ContactDetailsModel;
/**
 * Created by jay on 03-Jan-18.
 */

public class ContactDetailsAdapter extends BaseAdapter {

    private ArrayList<ContactDetailsModel> personDetailsArrayList;
    private Context context;
    private LayoutInflater inflater;

    public ContactDetailsAdapter(Context context, ArrayList<ContactDetailsModel> personDetailsArrayList) {
        this.context = context;
        this.personDetailsArrayList = personDetailsArrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return personDetailsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return personDetailsArrayList.get(position);
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
            holder.ivDeletePerson=(ImageView)v.findViewById(R.id.ivDeletePerson);
            holder.profile_image = (CircleImageView)v.findViewById(R.id.profile_image);

            v.setTag(holder);
        } else {
            holder = (Holder) v.getTag();
        }

        holder.tvPersonName.setText(personDetailsArrayList.get(position).getName()
                +"\n"+ Html.fromHtml("<font color=\'#0000FF\'>"+personDetailsArrayList.get(position).getPhonenumber()+"</font>"));
        holder.ivEditPesonDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactDetailsModel dataToEditModel= MainNavigationActivity.getInstance().searchPerson(personDetailsArrayList.get(position).getId());
                MainNavigationActivity.getInstance().addOrUpdatePersonDetailsDialog(dataToEditModel,position);
            }
        });
        holder.ivDeletePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowConfirmDialog(context,personDetailsArrayList.get(position).getId(), position);
            }
        });

        if (personDetailsArrayList.get(position).getImageUri() != null) {
            //holder.profile_image.setImageURI(Uri.parse(personDetailsArrayList.get(position).getImageUri()));
            Glide.with(context).load(personDetailsArrayList.get(position).getImageUri()).into(holder.profile_image);
        }else{
            holder.profile_image.setImageResource(R.drawable.ic_user_photo_avatart);
        }
        return v;
    }

    class Holder {
        TextView tvPersonName;
        ImageView ivDeletePerson, ivEditPesonDetail;
        CircleImageView profile_image;
    }

    public static void ShowConfirmDialog(Context context, final int personId, final int position)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder
                .setMessage("Are you sure you want to delete this record?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainNavigationActivity.getInstance().deletePerson(personId,position);
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
