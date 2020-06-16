package com.mp2.baymax20.adapter

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mp2.baymax20.R
import com.mp2.baymax20.databse.UserEntity
import com.squareup.picasso.Picasso

class DoctorAdapter(val context: Context, val itemList: List<UserEntity>, val onDoctorSelectInterface: OnDoctorSelectInterface): RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtName = view.findViewById<TextView>(R.id.txtSDoctorName)
        val txtSpe = view.findViewById<TextView>(R.id.txtSDoctorSpe)
        val txtHospital = view.findViewById<TextView>(R.id.txtSDoctorHospital)
        val txtSelect = view.findViewById<TextView>(R.id.txtSDoctorSelect)
        val imgDoctor = view.findViewById<ImageView>(R.id.imgSDoctor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_doctor,parent,false)
        return DoctorViewHolder(view)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {

        holder.txtName.text = "Dr.${itemList[position].name}"
        holder.txtHospital.text = "Hospital : ${itemList[position].hospital}"
        holder.txtSpe.text = "Specialization : ${itemList[position].spe}"
        Picasso.get().load(itemList[position].image).error(R.drawable.avatar).into(holder.imgDoctor)

        holder.txtSelect.setOnClickListener {
            onDoctorSelectInterface.onDoctorSelected(itemList[position])
        }
    }

    interface OnDoctorSelectInterface{
        fun onDoctorSelected(userEntity: UserEntity)
    }

}