package demo.jay.com.jaytest.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import demo.jay.com.jaytest.R;
import demo.jay.com.jaytest.adapters.CategoryAdapter;
import demo.jay.com.jaytest.adapters.ContactDetailsAdapter;
import demo.jay.com.jaytest.fragments.AddContact;
import demo.jay.com.jaytest.fragments.Category;
import demo.jay.com.jaytest.fragments.ContactsList;
import demo.jay.com.jaytest.models.CategoryListModel;
import demo.jay.com.jaytest.models.ContactDetailsModel;
import demo.jay.com.jaytest.utility.Utility;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.Manifest.permission.READ_CONTACTS;
import static demo.jay.com.jaytest.fragments.ContactsList.lvPersonNameList;
/**
 * Created by jay on 03-Jan-18.
 */


public class MainNavigationActivity extends AppCompatActivity {

    public static SharedPreferences sharepref;
    String str_venue_name=null;

    DrawerLayout drawer;
    private MenuItem activeMenuItem;
    NavigationView navigationView;

    static Activity activity;

    private static int id ,id_category ;

    static Realm myRealm;

    private static MainNavigationActivity instance;
    private static android.app.AlertDialog.Builder subDialog;

    private static final int REQUEST_READ_CONTACTS = 444;

    //private ProgressDialog pDialog;
    //private Handler updateBarHandler;

    String ph_name = "";
    String ph_email=null;
    String ph_number=null;

    ArrayList<String> contactList;
    Cursor cursor;
    int counter;


    public static ArrayList<ContactDetailsModel> contactDetailsModelArrayList = new ArrayList<>();
    public static ContactDetailsAdapter personDetailsAdapter;

    public static ArrayList<CategoryListModel> categoryModelArrayList = new ArrayList<>();
    public static CategoryAdapter categoryAdapter;

    public static ArrayList<String> categoryModelSpinnerArrayList = new ArrayList<>();
    public static ArrayAdapter<String>dataAdapter;

    AlertDialog alertDialog1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        sharepref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        if (android.os.Build.VERSION.SDK_INT > 14) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        myRealm = Realm.getDefaultInstance();


        instance = this;

        setPersonDetailsAdapter();
        setCategoryAdapter();


        if(sharepref.getInt("id",0)==0){
            id=1;
            sharepref.edit().putInt("id",id).apply();
        }else{
            id=sharepref.getInt("id",0);
        }

        if(sharepref.getInt("id_category",0)==1){
            id_category=1;
            sharepref.edit().putInt("id_category",id_category).apply();

            myRealm.beginTransaction();

            CategoryListModel categoryListModel = myRealm.createObject(CategoryListModel.class,id_category);
            //categoryListModel.setId(id_category);
            categoryListModel.setCategoryName("General");
            categoryModelArrayList.add(categoryListModel);

            myRealm.commitTransaction();
            categoryAdapter.notifyDataSetChanged();
            id_category++;
            sharepref.edit().putInt("id_category",id_category).apply();
        }else{
            id_category = sharepref.getInt("id_category",0);
        }

        activity = this;

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle("Contacts");
        //getSupportActionBar().setSubtitle("Be a smart host");
//

        FragmentTransaction tx;
        tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.frame, new ContactsList());
        tx.commit();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle Toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(Toggle);
        //calling sync state is necessay or else your hamburger icon wont show up
        Toggle.syncState();


        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu m = navigationView.getMenu();


        // navigationView.setNavigationItemSelectedListener(this);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        assert navigationView != null;
        navigationView.setCheckedItem(R.id.nav_allcontacts);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setItemIconTintList(null);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                navigationView.getMenu().getItem(0).setChecked(false);

                //Checking if the item is in checked state or not, if not make it in checked state
                if (activeMenuItem != null) activeMenuItem.setChecked(false);
                activeMenuItem = menuItem;
                menuItem.setChecked(true);
                //else menuItem.setChecked(true);

                //Closing drawer on item click
                drawer.closeDrawers();
                Fragment fragment = null;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_allcontacts:
                        // Toast.makeText(getApplicationContext(),"Shop",Toast.LENGTH_SHORT).show();
                        fragment = new ContactsList();
                        getSupportActionBar().setTitle("Contacts");
                        break;
                    case R.id.nav_addcontact:
                        /*fragment = new AddContact();
                        getSupportActionBar().setTitle("Add New Contact");*/
                        addOrUpdatePersonDetailsDialog(null,-1);
                        break;
                    case R.id.nav_categories:
                        fragment = new Category();
                        getSupportActionBar().setTitle("Categories");
                        break;


                    default:
                        Toast.makeText(getApplicationContext(), "Coming Soon...", Toast.LENGTH_SHORT).show();

                        break;

                }

                if (fragment != null) {

                    fragmentTransaction.replace(R.id.frame, fragment);
                    //getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).addToBackStack(null).commit();
                    fragmentTransaction.addToBackStack(null).commit();

                } else {
                    menuItem.setChecked(false);
                }

                return true;
            }
        });


        //getContacts();

        getAllUsers();
        getAllCategories();

        //new SyncContact().execute();

    }



    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //getContacts();

                getContacts();
            }
        }
    }

    public void getContacts() {



        if (!mayRequestContacts()) {
            return;
        }

        contactList = new ArrayList<String>();

        String phoneNumber = null;
        String email = null;
        String image_uri = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        StringBuffer output;

        ContentResolver contentResolver = getContentResolver();

        cursor = contentResolver.query(CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");

        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {

            counter = 0;
            while (cursor.moveToNext()) { // HERE I MADE STATIC SYNC UNTILL 250 BCZ TOO MUCH TIME TAKING===
                output = new StringBuffer();

               /* // Update the progress message
                updateBarHandler.post(new Runnable() {
                    public void run() {
                        pDialog.setMessage("Reading contacts : " + counter++ + "/" + cursor.getCount());
                    }
                });
*/
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                if (name.length() > 0) {
                    ph_name = name;

                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                    if (hasPhoneNumber > 0) {

                        output.append("\n First Name:" + name);

                        //This is to read multiple phone numbers associated with the same contact
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                        while (phoneCursor.moveToNext()) {
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            output.append("\n Phone number:" + phoneNumber);
                            ph_number = phoneNumber;
                            ph_number = ph_number.replaceAll("[\\D]", "");
                        }

                        phoneCursor.close();

                        // Read every email id associated with the contact
                        Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);

                        while (emailCursor.moveToNext()) {

                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));

                            output.append("\n Email:" + email);
                        }

                        emailCursor.close();

                        String columns[] = {
                                ContactsContract.CommonDataKinds.Event.START_DATE,
                                ContactsContract.CommonDataKinds.Event.TYPE,
                                ContactsContract.CommonDataKinds.Event.MIMETYPE,
                        };

                        String where = ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY +
                                " and " + ContactsContract.CommonDataKinds.Event.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' and " + ContactsContract.Data.CONTACT_ID + " = " + contact_id;

                        String[] selectionArgs = null;
                        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME;

                        Cursor birthdayCur = contentResolver.query(ContactsContract.Data.CONTENT_URI, columns, where, selectionArgs, sortOrder);
                        //Log.d("BDAY", birthdayCur.getCount()+"");
                        if (birthdayCur.getCount() > 0) {
                            while (birthdayCur.moveToNext()) {
                                String birthday = birthdayCur.getString(birthdayCur.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
                                output.append("Birthday :" + birthday);
                                //Log.d("BDAY", birthday);
                            }
                        }
                        birthdayCur.close();

                        try {
                            Cursor curphoto = this.getBaseContext().getContentResolver().query(
                                    ContactsContract.Data.CONTENT_URI,
                                    null,
                                    ContactsContract.Data.CONTACT_ID + "=" + contact_id + " AND "
                                            + ContactsContract.Data.MIMETYPE + "='"
                                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                                    null);
                            if (curphoto.getCount() > 0) {
                                while (curphoto.moveToNext()) {
                                    image_uri = curphoto.getString(curphoto.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                                }
                            }
                            curphoto.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                                .parseLong(contact_id));


                    }

                    if (ph_name == null || ph_name.length() <= 0 || ph_number == null || ph_number.length() <= 0) {
                        Log.e("no number found", "" + cursor.getCount());
                    } else {

                        // Add the contact to the ArrayList
                        contactList.add(output.toString());
                        ContactDetailsModel contactDetailsModel = new ContactDetailsModel();
                        contactDetailsModel.setName(ph_name);
                        contactDetailsModel.setEmail(ph_email);
                        contactDetailsModel.setAddress(null);
                        contactDetailsModel.setPhonenumber(ph_number);
                        contactDetailsModel.setImageUri(image_uri);
                        //Log.d("contact details---" , ph_name+ ph_email+ ""+ ph_number+ image_uri);

                        if (myRealm.where(ContactDetailsModel.class).equalTo("phonenumber", ph_number).findFirst()==null) {
                            addDataToRealmcontactSYNC(contactDetailsModel);
                        }
                        /*else{
                            updatePersonDetails(contactDetailsModel, cursor.getPosition(), ph_number);
                        }*/
                    }
                }
            }


            // Dismiss the progressbar after 500 millisecondds
           /* updateBarHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    pDialog.cancel();
                }
            }, 500);*/
        }

    }



    public static MainNavigationActivity getInstance() {
        return instance;
    }



    private void setPersonDetailsAdapter() {
        personDetailsAdapter = new ContactDetailsAdapter(MainNavigationActivity.this, contactDetailsModelArrayList);

    }

    private void setCategoryAdapter() {
        categoryAdapter = new CategoryAdapter(MainNavigationActivity.this, categoryModelArrayList);

    }

    public static void addOrUpdatePersonDetailsDialog(final ContactDetailsModel model, final int position) {

        //subdialog
        subDialog =  new android.app.AlertDialog.Builder(activity)
                .setMessage("Please enter all the details!!!")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg2, int which) {
                        dlg2.cancel();
                    }
                });

        //maindialog
        LayoutInflater li = LayoutInflater.from(activity);
        View promptsView = li.inflate(R.layout.prompt_dialog, null);
        android.app.AlertDialog.Builder mainDialog = new android.app.AlertDialog.Builder(activity);
        mainDialog.setView(promptsView);

        final EditText etAddPersonName = (EditText) promptsView.findViewById(R.id.etAddPersonName);
        final EditText etAddPersonEmail = (EditText) promptsView.findViewById(R.id.etAddPersonEmail);
        final EditText etAddPersonAddress = (EditText) promptsView.findViewById(R.id.etAddPersonAddress);
        final EditText etAddPersonAge = (EditText) promptsView.findViewById(R.id.etAddPersonAge);
        final Spinner sp_category = (Spinner)promptsView.findViewById(R.id.sp_category);
        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(activity.getBaseContext(), android.R.layout.simple_spinner_item, categoryModelSpinnerArrayList);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner

        // attaching data adapter to spinner
        sp_category.setAdapter(dataAdapter);
        getAllCategoriesSpinner();


        if (model != null) {
            etAddPersonName.setText(model.getName());
            etAddPersonEmail.setText(model.getEmail());
            etAddPersonAddress.setText(model.getAddress());
            etAddPersonAge.setText(String.valueOf(model.getPhonenumber()));
        }

        mainDialog.setCancelable(false)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final android.app.AlertDialog dialog = mainDialog.create();
        dialog.show();

        Button b = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utility.isBlankField(etAddPersonName) && !Utility.isBlankField(etAddPersonEmail) && !Utility.isBlankField(etAddPersonAddress) && !Utility.isBlankField(etAddPersonAge)) {
                    ContactDetailsModel contactDetailsModel = new ContactDetailsModel();
                    contactDetailsModel.setName(etAddPersonName.getText().toString());
                    contactDetailsModel.setEmail(etAddPersonEmail.getText().toString());
                    contactDetailsModel.setAddress(etAddPersonAddress.getText().toString());
                    contactDetailsModel.setPhonenumber(etAddPersonAge.getText().toString());
                    contactDetailsModel.setCategory(sp_category.getSelectedItem().toString());

                    if (model == null)
                        addDataToRealm(contactDetailsModel);
                    else
                        updatePersonDetails(contactDetailsModel, position, model.getPhonenumber());

                    dialog.cancel();
                }/*else if(sp_category.getSelectedItemPosition()<=0){
                    ((TextView)sp_category.getSelectedView()).setError("Select category.");
                } */else {
                    subDialog.show();
                }
            }
        });
    }

    public static void addDataToRealmcontactSYNC(ContactDetailsModel model) {
        myRealm.beginTransaction();

       // Log.d("ID first",""+id);
        ContactDetailsModel contactDetailsModel = myRealm.createObject(ContactDetailsModel.class,model.getPhonenumber());
        contactDetailsModel.setId(id);
        contactDetailsModel.setName(model.getName());
        contactDetailsModel.setEmail(model.getEmail());
        contactDetailsModel.setAddress(model.getAddress());
        //. contactDetailsModel.setPhonenumber(model.getPhonenumber());
        contactDetailsModel.setCategory(model.getCategory());
        contactDetailsModelArrayList.add(contactDetailsModel);

        myRealm.commitTransaction();
        personDetailsAdapter.notifyDataSetChanged();
        Log.d("ID second",""+id);
        getAllUsers();
    }

    public static void addDataToRealm(ContactDetailsModel model) {
        myRealm.beginTransaction();

        //Log.d("ID first",""+id);
        ContactDetailsModel contactDetailsModel = myRealm.createObject(ContactDetailsModel.class,model.getPhonenumber());
        contactDetailsModel.setId(id);
        contactDetailsModel.setName(model.getName());
        contactDetailsModel.setEmail(model.getEmail());
        contactDetailsModel.setAddress(model.getAddress());
       //. contactDetailsModel.setPhonenumber(model.getPhonenumber());
        contactDetailsModel.setCategory(model.getCategory());
        contactDetailsModelArrayList.add(contactDetailsModel);

        myRealm.commitTransaction();
        personDetailsAdapter.notifyDataSetChanged();
        Log.d("ID second",""+id);
        getAllUsers();
    }

    public static void addCategoryDataToRealm(CategoryListModel model) {
        myRealm.beginTransaction();

        CategoryListModel categoryListModel = myRealm.createObject(CategoryListModel.class,id_category);
        //categoryListModel.setId(id_category);
        categoryListModel.setCategoryName(model.getCategoryName());
        categoryModelArrayList.add(categoryListModel);

        myRealm.commitTransaction();
        categoryAdapter.notifyDataSetChanged();
        id_category++;
        sharepref.edit().putInt("id_category",id_category).apply();

    }

    public static void deletePerson(int personId, int position) {
        RealmResults<ContactDetailsModel> results = myRealm.where(ContactDetailsModel.class).equalTo("id", personId).findAll();

        myRealm.beginTransaction();
        results.deleteFromRealm(0);//remove(0);
        myRealm.commitTransaction();

        contactDetailsModelArrayList.remove(position);
        personDetailsAdapter.notifyDataSetChanged();
    }

    public static void deleteCategory(int catId, int position) {
        RealmResults<CategoryListModel> results = myRealm.where(CategoryListModel.class).equalTo("id", catId).findAll();

        myRealm.beginTransaction();
        results.deleteFromRealm(0);//remove(0);
        myRealm.commitTransaction();

        categoryModelArrayList.remove(position);
        categoryAdapter.notifyDataSetChanged();
    }

    public ContactDetailsModel searchPerson(int personId) {
        RealmResults<ContactDetailsModel> results = myRealm.where(ContactDetailsModel.class).equalTo("id", personId).findAll();

        myRealm.beginTransaction();
        myRealm.commitTransaction();

        return results.get(0);
    }

    public static void updatePersonDetails(ContactDetailsModel model, int position, String phonenumber) {
        Log.d("update Model class----",model.getName()+"++"+position);
        ContactDetailsModel editPersonDetails = myRealm.where(ContactDetailsModel.class).equalTo("phonenumber", phonenumber).findFirst();
        myRealm.beginTransaction();
        editPersonDetails.setName(model.getName());
        editPersonDetails.setEmail(model.getEmail());
        editPersonDetails.setAddress(model.getAddress());
        editPersonDetails.setPhonenumber(model.getPhonenumber());
        myRealm.commitTransaction();

        contactDetailsModelArrayList.set(position, editPersonDetails);
        personDetailsAdapter.notifyDataSetChanged();
    }

    private static void getAllUsers() {

        contactDetailsModelArrayList.clear();
        personDetailsAdapter.notifyDataSetChanged();

        RealmResults<ContactDetailsModel> results = myRealm.where(ContactDetailsModel.class).findAll().sort("name", Sort.ASCENDING);

        myRealm.beginTransaction();

        for (int i = 0; i < results.size(); i++) {
            contactDetailsModelArrayList.add(results.get(i));
        }

        if(results.size()>0 /*&& sharepref.getInt("id",0)!=1*/) {
            id = (int) (myRealm.where(ContactDetailsModel.class).max("id")).intValue() + 1;
            sharepref.edit().putInt("id", id).apply();
        }

        myRealm.commitTransaction();
        personDetailsAdapter.notifyDataSetChanged();
        sharepref.edit().putInt("id",id).apply();
    }

    private static void getAllCategories() {
        categoryModelSpinnerArrayList.clear();
        categoryAdapter.notifyDataSetChanged();

        RealmResults<CategoryListModel> results = myRealm.where(CategoryListModel.class).findAll();
        myRealm.beginTransaction();

        for (int i = 0; i < results.size(); i++) {
            categoryModelArrayList.add(results.get(i));
        }

        if(results.size()>0) {
            id_category = (int)(myRealm.where(CategoryListModel.class).max("id")).intValue() + 1;
        }
        myRealm.commitTransaction();
        categoryAdapter.notifyDataSetChanged();
        sharepref.edit().putInt("id_category",id_category).apply();
    }

    private static void getAllCategoriesSpinner() {

        categoryModelSpinnerArrayList.clear();
        dataAdapter.notifyDataSetChanged();

        RealmResults<CategoryListModel> results = myRealm.where(CategoryListModel.class).findAll();
        myRealm.beginTransaction();

        for (int i = 0; i < results.size(); i++) {
            categoryModelSpinnerArrayList.add(results.get(i).getCategoryName());
        }

        myRealm.commitTransaction();
        dataAdapter.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenu_main, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_filter:

                final ArrayList<String> filterCategory = new ArrayList<String>();

                RealmResults<CategoryListModel> results = myRealm.where(CategoryListModel.class).findAll();
                myRealm.beginTransaction();

                for (int i = 0; i < results.size(); i++) {
                    filterCategory.add(results.get(i).getCategoryName());
                }
                myRealm.commitTransaction();

                String[] filter =  new String[filterCategory.size()];
                filter = filterCategory.toArray(filter);

                ArrayAdapter<String> adapterfilter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, filter);
                AlertDialog.Builder builderfilter = new AlertDialog.Builder(this);
                builderfilter.setTitle("Select Option");
                builderfilter.setSingleChoiceItems(filter, -1, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        String getCategory = filterCategory.get(item).toString();

                        contactDetailsModelArrayList.clear();
                        personDetailsAdapter.notifyDataSetChanged();

                        RealmResults<ContactDetailsModel> results = myRealm.where(ContactDetailsModel.class)
                                .equalTo("category", getCategory).findAll().sort("name", Sort.ASCENDING);

                        myRealm.beginTransaction();

                        for (int i = 0; i < results.size(); i++) {
                            contactDetailsModelArrayList.add(results.get(i));
                        }
                        myRealm.commitTransaction();
                        personDetailsAdapter.notifyDataSetChanged();

                        alertDialog1.dismiss();
                    }
                });
                alertDialog1 = builderfilter.create();
                alertDialog1.show();

                return true;

            case R.id.menu_sorting:

                final String[] option = new String[] { "AtoZ ", "ZtoA" };
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, option);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select Option");
                builder.setSingleChoiceItems(option, -1, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {

                        switch(item)
                        {
                            case 0:

                                contactDetailsModelArrayList.clear();
                                personDetailsAdapter.notifyDataSetChanged();

                                RealmResults<ContactDetailsModel> results = myRealm.where(ContactDetailsModel.class).findAll().sort("name", Sort.ASCENDING);

                                myRealm.beginTransaction();

                                for (int i = 0; i < results.size(); i++) {
                                    contactDetailsModelArrayList.add(results.get(i));
                                }
                                myRealm.commitTransaction();
                                personDetailsAdapter.notifyDataSetChanged();

                                dialog.dismiss();

                                break;
                            case 1:

                                contactDetailsModelArrayList.clear();
                                personDetailsAdapter.notifyDataSetChanged();

                                RealmResults<ContactDetailsModel> results2 = myRealm.where(ContactDetailsModel.class).findAll().sort("name", Sort.DESCENDING);

                                myRealm.beginTransaction();

                                for (int i = 0; i < results2.size(); i++) {
                                    contactDetailsModelArrayList.add(results2.get(i));
                                }
                                myRealm.commitTransaction();
                                personDetailsAdapter.notifyDataSetChanged();

                                dialog.dismiss();
                                break;
                        }
                        alertDialog1.dismiss();
                    }
                });
                alertDialog1 = builder.create();
                alertDialog1.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        contactDetailsModelArrayList.clear();
        myRealm.close();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {


            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return;
            } else if (getFragmentManager().getBackStackEntryCount() == 0) {
                this.doubleBackToExitPressedOnce = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ContactsList()).commit();
                //getSupportActionBar().setTitle("Ovenues");
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            }


            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }


}
