package com.gainwise.breakthru

import android.content.Context
import android.content.DialogInterface
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Switch

class AdapterRV(var list: MutableList<Contact>?, val context: Context) : RecyclerView.Adapter<AdapterRV.MyViewHolder>(){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(p0.getContext()).inflate(R.layout.rv_item, p0, false)
        return MyViewHolder(view)

    }

    override fun getItemCount(): Int {
       return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, pos: Int) {
        holder.tvNum.setText(list?.get(pos)?.number)
        holder.tvName.setText(list?.get(pos)?.name)
        if (list?.get(pos)!!.on) {
            holder.switch.isChecked = true
        } else {
            holder.switch.isChecked = false
        }
        holder.switch.setOnCheckedChangeListener { compoundButton, b ->
            list?.get(pos)!!.on = b
            saveList()
            if(b){
                Snackbar.make(compoundButton, "${list?.get(pos)?.name} (${list?.get(pos)?.number}) is enabled to BreakThru.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }else {
                Snackbar.make(compoundButton, "${list?.get(pos)?.name} (${list?.get(pos)?.number}) is disabled from BreakThru.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }
        }
      holder.itemView.setOnCreateContextMenuListener(object : View.OnCreateContextMenuListener {
          override fun onCreateContextMenu(menu: ContextMenu?, p1: View?, p2: ContextMenu.ContextMenuInfo?) {
              menu!!.add("Delete").setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener {

                  val alertDialog =  AlertDialog.Builder(context).create();
                  alertDialog.setTitle("Confirm Delete");
                  alertDialog.setMessage("Remove From List?");
                  alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES   ", object : DialogInterface.OnClickListener{
                      override fun onClick(p0: DialogInterface?, p1: Int) {

                          list?.removeAt(pos)
                          notifyDataSetChanged()
                          saveList()
                      }

                  });
                  alertDialog.show();

                  true
              })
          }

      })

    }
    private fun saveList() {
        val prefsEditor = context.getSharedPreferences("MASTER", Context.MODE_PRIVATE).edit();
        val newJSONstring = MainActivity.gson.toJson(list, MainActivity.typeOfSource)
        prefsEditor.putString("list",newJSONstring)
        prefsEditor.apply()
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: CustomTextView
        var tvNum: CustomTextView
        var switch: Switch

        init {
            tvName = itemView.findViewById(R.id.rvitem_tvname)
            tvNum = itemView.findViewById(R.id.rvitem_tvnumber)
            switch = itemView.findViewById(R.id.rvswitch)
        }
    }
}
