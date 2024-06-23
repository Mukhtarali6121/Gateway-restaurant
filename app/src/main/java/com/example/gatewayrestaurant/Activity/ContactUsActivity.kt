package com.example.gatewayrestaurant.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.ActivityContactUsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ContactUsActivity : BaseActivity(), OnMapReadyCallback {
    private lateinit var mBinding: ActivityContactUsBinding
    var FHLoc = LatLng(19.1151216, 72.8613308)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.isTrafficEnabled = true
        googleMap.isIndoorEnabled = true
        googleMap.isBuildingsEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isRotateGesturesEnabled = true
        val marker = MarkerOptions().position(FHLoc).title("Fintoo")
        googleMap.addMarker(marker)
        val cameraPosition = CameraPosition.Builder().target(FHLoc).zoom(15f).build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onResume() {

        val googleMap =
            fragmentManager.findFragmentById(R.id.map) as MapFragment
        googleMap.getMapAsync(this@ContactUsActivity)

        super.onResume()
    }
}