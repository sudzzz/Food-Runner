package com.internshala.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.internshala.foodrunner.R
import com.internshala.foodrunner.activity.HomeActivity.Companion.sharedPreferences
import com.internshala.foodrunner.util.ConnectionManager


class MyProfileFragment : Fragment() {

    lateinit var rlMyProfile: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var imgProfile: ImageView
    lateinit var txtName: TextView
    lateinit var txtNumber: TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress: TextView
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_my_profile, container, false)

        sharedPreferences = (activity as FragmentActivity).getSharedPreferences(getString(R.string.preference_login),Context.MODE_PRIVATE)

        rlMyProfile = view.findViewById(R.id.progressLayoutMyProfile)
        progressBar = view.findViewById(R.id.progressBar)
        imgProfile = view.findViewById(R.id.imgProfile)
        txtName = view.findViewById(R.id.txtName)
        txtNumber =  view.findViewById(R.id.txtNumber)
        txtEmail =  view.findViewById(R.id.txtEmail)
        txtAddress =  view.findViewById(R.id.txtAddress)



        rlMyProfile.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE


        if (ConnectionManager().checkConnectivity(activity as Context)){
            rlMyProfile.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE

            txtName.text = sharedPreferences.getString("user_name","John")
            txtEmail.text = sharedPreferences.getString("user_email","johnathan@doe.gmail")
            txtNumber.text = "+91-${sharedPreferences.getString("user_mobile","+91-1115555555")}"
            txtAddress.text = sharedPreferences.getString("user_address","Gurugram")

        }
        else
        {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit") {text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view


    }


}