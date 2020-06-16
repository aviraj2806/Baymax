package com.mp2.baymax20.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mp2.baymax20.R
import com.mp2.baymax20.databse.HospitalEntity
import com.squareup.picasso.Picasso

class HospitalAdapter(val context: Context, val itemList: List<HospitalEntity>, val onHospitalInterface: OnHospitalInterface): RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder>() {

    class HospitalViewHolder(view: View): RecyclerView.ViewHolder(view){

        val txtName = view.findViewById<TextView>(R.id.txtHospitalName)
        val txtLoc = view.findViewById<TextView>(R.id.txtHospitalLoc)
        val txtRating = view.findViewById<TextView>(R.id.txtHospitalRating)
        val txtSelect = view.findViewById<TextView>(R.id.txtHospitalSelect)
        val imgHospital = view.findViewById<ImageView>(R.id.imgHospital)
        val imgContact = view.findViewById<ImageView>(R.id.txtHospitalContact)
        val imgDirections = view.findViewById<ImageView>(R.id.txtHospitalDirections)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_hospital,parent,false)
        return HospitalViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {

        Picasso.get().load(itemList[position].image).error(R.drawable.avatar).into(holder.imgHospital)
        holder.txtName.text = itemList[position].name
        holder.txtLoc.text = itemList[position].loc
        holder.txtRating.text = itemList[position].rating

        holder.imgContact.setOnClickListener {
            onHospitalInterface.onContactHospital(itemList[position])
        }

        holder.imgDirections.setOnClickListener {
            onHospitalInterface.onGetHospitalDirection(itemList[position])
        }

        holder.txtSelect.setOnClickListener {
            onHospitalInterface.onSelectHospital(itemList[position])
        }

    }

    interface OnHospitalInterface{
        fun onSelectHospital(hospitalEntity: HospitalEntity)
        fun onGetHospitalDirection(hospitalEntity: HospitalEntity)
        fun onContactHospital(hospitalEntity: HospitalEntity)
    }

}