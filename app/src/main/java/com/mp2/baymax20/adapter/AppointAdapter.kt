package com.mp2.baymax20.adapter

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.mp2.baymax20.R
import com.mp2.baymax20.databse.AppointEntity
import com.mp2.baymax20.databse.HospitalDatabase
import com.mp2.baymax20.databse.HospitalEntity
import com.mp2.baymax20.databse.UserDatabase
import com.squareup.picasso.Picasso

class AppointAdapter(val context: Context, val itemList: List<AppointEntity>, val onAppointStatusControl: OnAppointStatusControl,val user: String) : RecyclerView.Adapter<AppointAdapter.AppointViewHolder>() {

    class AppointViewHolder(view:View) : RecyclerView.ViewHolder(view){
        val txtName = view.findViewById<TextView>(R.id.txtAppointName)
        val txtMobile = view.findViewById<TextView>(R.id.txtAppointMobile)
        val txtEmail = view.findViewById<TextView>(R.id.txtAppointEmail)
        val txtAccept = view.findViewById<TextView>(R.id.txtAppointAccept)
        val txtReject = view.findViewById<TextView>(R.id.txtAppointReject)
        val txtCancel = view.findViewById<TextView>(R.id.txtAppointCancel)
        val txtStatus = view.findViewById<TextView>(R.id.txtAppointStatus)
        val llStatus = view.findViewById<LinearLayout>(R.id.llAppointStatus)
        val imgUser = view.findViewById<ImageView>(R.id.imgAppointUser)
        val txtReqDate = view.findViewById<TextView>(R.id.txtAppointReqDate)
        val txtDate = view.findViewById<TextView>(R.id.txtAppointDate)
        val txtHName = view.findViewById<TextView>(R.id.txtAppointHospitalName)
        val imgContact = view.findViewById<ImageView>(R.id.txtAppointHospitalContact)
        val imgDirections = view.findViewById<ImageView>(R.id.txtAppointHospitalDirections)
        val llHospital = view.findViewById<LinearLayout>(R.id.llAppointHospitalDetails)
        val imgHospital = view.findViewById<ImageView>(R.id.imgAppointHospital)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_appoint,parent,false)
        return AppointViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: AppointViewHolder, position: Int) {
        val hospitalDetails = Room.databaseBuilder(context,HospitalDatabase::class.java,"hospital")
            .allowMainThreadQueries().build().hospitalDao().getHospitalByName(itemList[position].hospital_name)

        if(user == "1") {
            val userDetails = Room.databaseBuilder(context, UserDatabase::class.java, "user")
                .allowMainThreadQueries().build().userDao()
                .getUserByMobile(itemList[position].appoint_to)

            holder.llStatus.visibility = View.GONE

            if(itemList[position].appoint_status == "0" || itemList[position].appoint_status == "1"){
                    holder.txtName.text = userDetails.name
                    holder.txtEmail.text = userDetails.email
                    holder.txtMobile.text = userDetails.mobile
                    if(itemList[position].appoint_status == "0") {
                        holder.txtStatus.text = "Status : Pending"
                    }else{
                        holder.txtStatus.text = "Status : Accepted"
                    }
                    holder.txtReqDate.text = "Requested On : ${itemList[position].timeStamp} "
                    holder.txtDate.text = "Requested For : ${itemList[position].appoint_time} "
                    Picasso.get().load(userDetails.image).error(R.drawable.avatar_light).into(holder.imgUser)
             }else{
                holder.txtName.text = userDetails.name
                holder.txtEmail.text = userDetails.email
                holder.txtMobile.text = userDetails.mobile
                holder.txtStatus.text = "Status : Rejected"
                holder.txtReqDate.text = "Requested On : ${itemList[position].timeStamp} "
                holder.txtDate.text = "Requested For : ${itemList[position].appoint_time} "
                Picasso.get().load(userDetails.image).error(R.drawable.avatar_light).into(holder.imgUser)
                holder.txtCancel.visibility = View.GONE
            }

            holder.txtHName.text = hospitalDetails.name
            Picasso.get().load(hospitalDetails.image).error(R.drawable.avatar_light).into(holder.imgHospital)

        }else{
            holder.llHospital.visibility = View.GONE
            val userDetails = Room.databaseBuilder(context, UserDatabase::class.java, "user")
                .allowMainThreadQueries().build().userDao()
                .getUserByMobile(itemList[position].appoint_by)

            if(itemList[position].appoint_status == "0") {
                holder.txtName.text = userDetails.name
                holder.txtEmail.text = userDetails.email
                holder.txtMobile.text = userDetails.mobile
                holder.txtStatus.text = "Status : Pending"
                holder.txtReqDate.text = "Requested On : ${itemList[position].timeStamp} "
                holder.txtDate.text = "Requested For : ${itemList[position].appoint_time} "
                Picasso.get().load(userDetails.image).error(R.drawable.avatar_light).into(holder.imgUser)

                holder.txtCancel.visibility = View.GONE

            }else if(itemList[position].appoint_status == "1"){
                holder.txtName.text = userDetails.name
                holder.txtEmail.text = userDetails.email
                holder.txtMobile.text = userDetails.mobile
                holder.txtStatus.text = "Status : Accepted"
                holder.txtReqDate.text = "Requested On : ${itemList[position].timeStamp} "
                holder.txtDate.text = "Requested For : ${itemList[position].appoint_time} "
                Picasso.get().load(userDetails.image).error(R.drawable.avatar_light).into(holder.imgUser)

                holder.llStatus.visibility = View.GONE
            }

        }

        holder.txtAccept.setOnClickListener {
            onAppointStatusControl.onAccept(itemList[position])
        }

        holder.txtReject.setOnClickListener {
            onAppointStatusControl.onReject(itemList[position])
        }

        holder.txtCancel.setOnClickListener {
            onAppointStatusControl.onCancel(itemList[position])
        }

        holder.imgContact.setOnClickListener {
            onAppointStatusControl.onContact(hospitalDetails)
        }

        holder.imgDirections.setOnClickListener {
            onAppointStatusControl.onDirections(hospitalDetails)
        }

    }

    interface OnAppointStatusControl{
        fun onAccept(appointEntity: AppointEntity)
        fun onReject(appointEntity: AppointEntity)
        fun onCancel(appointEntity: AppointEntity)
        fun onDirections(hospitalEntity: HospitalEntity)
        fun onContact(hospitalEntity: HospitalEntity)
    }

}