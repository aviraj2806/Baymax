package com.mp2.baymax20.adapter

import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.mp2.baymax20.R
import com.mp2.baymax20.databse.ReportEntity
import com.mp2.baymax20.databse.UserDatabase
import com.mp2.baymax20.databse.UserEntity
import com.squareup.picasso.Picasso

class ReportAdapter(val context: Context, val itemList: List<ReportEntity>, val onReportInterface: OnReportInterface, val user: String): RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtName = view.findViewById<TextView>(R.id.txtReportName)
        val txtEmail = view.findViewById<TextView>(R.id.txtReportEmail)
        val txtMobile = view.findViewById<TextView>(R.id.txtReportMobile)
        val txtHistory = view.findViewById<TextView>(R.id.txtReportHistory)
        val imgUser = view.findViewById<ImageView>(R.id.imgReportUser)
        val imgReport = view.findViewById<ImageView>(R.id.imgReport)
        val imgPres = view.findViewById<ImageView>(R.id.imgPrescription)
        val llUser = view.findViewById<LinearLayout>(R.id.llReportUserDetails)
        val llReport = view.findViewById<LinearLayout>(R.id.llReportDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_report,parent,false)
        return ReportViewHolder(view)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {

        if(user == "1"){
            holder.llUser.visibility = View.GONE
            holder.txtHistory.text = itemList[position].history

            Picasso.get().load(itemList[position].report_link).error(R.drawable.nav_reports_light).into(holder.imgReport)
            Picasso.get().load(itemList[position].pres_link).error(R.drawable.nav_reports_light).into(holder.imgPres)
        }else{
            val user = Room.databaseBuilder(context,UserDatabase::class.java,"user")
                .allowMainThreadQueries().build().userDao().getUserByMobile(itemList[position].report_by)

            holder.txtName.text = user.name
            holder.txtEmail.text = user.email
            holder.txtMobile.text = user.mobile

            Picasso.get().load(user.image).error(R.drawable.avatar_light).into(holder.imgUser)

            holder.txtHistory.text = itemList[position].history

            Picasso.get().load(itemList[position].report_link).error(R.drawable.nav_reports_light).into(holder.imgReport)
            Picasso.get().load(itemList[position].pres_link).error(R.drawable.nav_reports_light).into(holder.imgPres)
        }

        holder.imgReport.setOnClickListener {
            onReportInterface.onReportClicked(itemList[position])
        }

        holder.imgPres.setOnClickListener {
            onReportInterface.onPresClicked(itemList[position])
        }

    }

    interface OnReportInterface{
        fun onReportClicked(reportEntity: ReportEntity)
        fun onPresClicked(reportEntity: ReportEntity)
    }

}