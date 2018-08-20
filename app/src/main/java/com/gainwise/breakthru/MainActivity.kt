package com.gainwise.breakthru

import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.gainwise.seed.Vitals.AllPermissionsHelper
import com.gainwise.seed.Vitals.PermissionsDirective
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import osmandroid.project_basics.Task
import spencerstudios.com.fab_toast.FabToast


class MainActivity : AppCompatActivity() {

    private val RESULT_PICK_CONTACT = 28
    lateinit var helper: AllPermissionsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false);
        val title = toolbar.findViewById<CustomTextView>(R.id.title_toolbar)
        val font = Typeface.createFromAsset(getApplication().getAssets(), "FjallaOne-Regular.ttf");
        title.setTypeface(font);
        helper = AllPermissionsHelper(Perm())
        enableBreakThruInSettings()
        refreshRV()



        fab.setOnClickListener { view ->
            if(helper.needPermissions(Perm().permissionsToRequest())){
                val alertDialog =  AlertDialog.Builder(this).create();
                alertDialog.setTitle("Permission needed");
                alertDialog.setMessage("""This app needs permission to access your phone state to verify incoming caller info for waking up device, is this ok?""");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        helper.requestPermissions()
                    }

                });
                alertDialog.show();

            }else{
               addHit()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
       helper.handleResult(requestCode, permissions, grantResults)
    }


    private fun saveContact(name: String, number: String) {
        lateinit var list: MutableList<Contact>
        val prefs = getSharedPreferences("MASTER", Context.MODE_PRIVATE);
        val jsonList: String? = prefs.getString("list", null)
        val contact = Contact(name, number, false)
        if(jsonList != null){
            list = gson.fromJson(jsonList, typeOfSource)

            list.add(contact)
        }else{
            list = mutableListOf(contact)
        }

        val prefsEditor = getSharedPreferences("MASTER", Context.MODE_PRIVATE).edit();
        val newJSONstring = gson.toJson(list, typeOfSource)
        prefsEditor.putString("list",newJSONstring)
        prefsEditor.apply()
        refreshRV()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.share -> {Task.ShareApp(this,"com.gainwise.breakthru", "Simplified device wake up!", "Select or Input numbers, and they can wake device up from Do Not Disturb mode.")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

            }

    fun enableBreakThruInSettings(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
val alertDialog =  AlertDialog.Builder(this).create();
alertDialog.setTitle("Allow BreakThru Access");
alertDialog.setMessage("""You will need to allow this app the ability to override the "Do Not Disturb" feature in the settings  - shall I take you there?""");
alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", object : DialogInterface.OnClickListener{
    override fun onClick(p0: DialogInterface?, p1: Int) {

        val intent = Intent(
                Settings
                        .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        startActivity(intent)
    }

});
alertDialog.show();
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // check whether the result is ok
        if (resultCode == Activity.RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            when (requestCode) {
                RESULT_PICK_CONTACT -> contactPicked(data!!)
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact")
        }
    }

    private fun contactPicked(data: Intent) {
        var cursor: Cursor? = null
        try {
            var phoneNo: String? = null
            var name: String? = null
            // getData() method will have the Content Uri of the selected contact
            val uri = data.data
            //Query the content uri
            cursor = contentResolver.query(uri!!, null, null, null, null)
            cursor!!.moveToFirst()
            // column index of the phone number
            val phoneIndex = cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            // column index of the contact name
            val nameIndex = cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            phoneNo = cursor!!.getString(phoneIndex).removeAllButNumbers()
            name = cursor!!.getString(nameIndex)
            saveContact(name, phoneNo)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun refreshRV(){
        val prefs = getSharedPreferences("MASTER", Context.MODE_PRIVATE);
        val jsonList: String? = prefs.getString("list", null)
        if(jsonList != null){
            val list: MutableList<Contact> = gson.fromJson(jsonList, typeOfSource)
            val layoutManager =  LinearLayoutManager(this);
            listRV.layoutManager = layoutManager
            val dividerItemDecoration = DividerItemDecoration(listRV.getContext(),
                    layoutManager.getOrientation())
            listRV.addItemDecoration(dividerItemDecoration)

            val adapter = AdapterRV(list, MainActivity@this)
            listRV.adapter = adapter
        }

    }
    companion object Helper{
        val gson = Gson()
        val typeOfSource = object : TypeToken<List<Contact>>() {
        }.type
    }

    fun addHit(){
        val dialogAdd = Dialog(this@MainActivity)
        val view = layoutInflater.inflate(R.layout.dialog_add_fab, null)
        dialogAdd.setContentView(view)
        val custom = view.findViewById<CustomTextView>(R.id.add_custom)
        val contact = view.findViewById<CustomTextView>(R.id.add_from_contacts)
        contact.setOnClickListener {
            dialogAdd.dismiss()
            val contactPickerIntent = Intent(Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT)

        }
        custom.setOnClickListener{
            dialogAdd.dismiss()
            val dialogCustom = Dialog(this@MainActivity)
            val view2 = layoutInflater.inflate(R.layout.dialog_add_custom, null)
            dialogCustom.setContentView(view2)
            val nameET = view2.findViewById<EditText>(R.id.customEtName)
            val numberET = view2.findViewById<EditText>(R.id.customEtNumber)
            val saveButton = view2.findViewById<Button>(R.id.customSaveButton)
            val cancelButton = view2.findViewById<Button>(R.id.customCancelButton)

            cancelButton.setOnClickListener{dialogCustom.dismiss()}
            saveButton.setOnClickListener {
                if(nameET.text.toString().trim().isNotBlank() && nameET.text.toString().trim().isNotEmpty() &&
                        numberET.text.toString().trim().isNotBlank() && numberET.text.toString().trim().isNotEmpty()){
                    val name = nameET.text.toString().trim()
                    val number = numberET.text.toString().trim().removeAllButNumbers()
                    saveContact(name, number)
                    dialogCustom.dismiss()

                }else{
                    FabToast.makeText(this@MainActivity, "Please fill out required info!",
                            FabToast.LENGTH_LONG, FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show()
                }
            }
            dialogCustom.show()
        }
        dialogAdd.show()
    }

   open inner class Perm : PermissionsDirective{
       override val activity: Activity
           get() = Outer@ this@MainActivity //To change initializer of created properties use File | Settings | File Templates.
       override val requestCode: Int
           get() = 52//To change initializer of created properties use File | Settings | File Templates.

       override fun executeOnPermissionDenied() {
           FabToast.makeText(activity, "Permissions are needed.", FabToast.LENGTH_LONG,
                   FabToast.ERROR, FabToast.POSITION_DEFAULT).show() //To change body of created functions use File | Settings | File Templates.
       }

       override fun executeOnPermissionGranted() {
         addHit()
       }

       override fun permissionsToRequest(): Array<String?> {
        val p = arrayOfNulls<String>(1)
           p[0] = "android.permission.READ_PHONE_STATE"
           return p
       }

   }
    internal var double_backpressed = false
    override fun onBackPressed() {

        var prefs = getSharedPreferences("AUTO_PREF", Context.MODE_PRIVATE)
        var show = prefs.getBoolean("showRateDialog", true)

        if(show){

            var dialog = Dialog(Home@this)
            var view = layoutInflater.inflate(R.layout.rate_buy_exit, null)

            var llrate = view.findViewById<LinearLayout>(R.id.llrate);
            var llnever = view.findViewById<LinearLayout>(R.id.llnever);
            var lllater = view.findViewById<LinearLayout>(R.id.lllater);


            llrate.setOnClickListener({
                Task.RateApp(Home@this, "com.gainwise.breakthru")
                val editor = getSharedPreferences("AUTO_PREF", MODE_PRIVATE).edit()
                editor.putBoolean("showRateDialog", false)
                editor.apply()})
            llnever.setOnClickListener({
                val editor = getSharedPreferences("AUTO_PREF", MODE_PRIVATE).edit()
                editor.putBoolean("showRateDialog", false)
                editor.apply()
                dialog.dismiss()
                super.onBackPressed()
            })
            lllater.setOnClickListener({
                dialog.dismiss()
                super.onBackPressed()

            })
            dialog.setContentView(view)
            dialog.show()

        }else {


            if (double_backpressed) {
                super.onBackPressed()
                return
            }
            this.double_backpressed = true
            FabToast.makeText(Home@this,
                    "Click back again to exit.", Toast.LENGTH_SHORT, FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show()

            Handler().postDelayed({ double_backpressed = false }, 2000)
        }
    }

}


