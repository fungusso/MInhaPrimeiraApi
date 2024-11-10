package com.example.minhaprimeiraapi

import android.content.Context
import android.content.Intent
import android.os.Bundle

import android.view.View.VISIBLE
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.example.minhaprimeiraapi.R.id.map

import com.example.minhaprimeiraapi.databinding.ActivityItemDetailBinding
import com.example.minhaprimeiraapi.model.Item

import com.example.minhaprimeiraapi.service.RetrofitClient
import com.example.minhaprimeiraapi.service.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.minhaprimeiraapi.service.Result
import com.example.minhaprimeiraapi.ui.CircleTransform
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso



class ItemDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityItemDetailBinding

    private lateinit var item: Item

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        loadItem()
        setupGoogleMap()


    }

    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.deleteCTA.setOnClickListener {
            deleteItem()
        }
        binding.editCTA.setOnClickListener {
// manda pra tela do update
            val itemId = intent.getStringExtra(ARG_ID) ?: ""
            startActivity(ItemUpdateActivity.newIntent(
                this,
                itemId
            ))
        }

    }
    private fun setupGoogleMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun loadItem() {
        val itemId = intent.getStringExtra(ARG_ID) ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.apiService.getItem(itemId) }

            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {}
                    is Result.Success -> {
                        item = result.data
                        handleSuccess()
                    }

                }
            }
        }
    }

    private fun deleteItem() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.apiService.deleteItem(item.id) }

            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        Toast.makeText(
                            this@ItemDetailActivity,
                            R.string.error_delete,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Result.Success -> {
                        Toast.makeText(
                            this@ItemDetailActivity,
                            R.string.success_delete,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }
    }

    private fun handleSuccess() {
        binding.name.text = item.value.name
        binding.year.text =  item.value.year
        binding.licence.text = item.value.licence
     //   binding.image.loadUrl(item.value.imageUrl)


//        binding.image.loadUrl(item.value.imageUrl)
        Picasso.get()
            .load(item.value.imageUrl)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_error)
            .transform(CircleTransform())
     //       .into(holder.imageView)
            .into(binding.image)
        loadItemLocationInGoogleMap()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (::item.isInitialized) {
            // se o item já foi carregado por nossa chamada
            // carrega-lo no map
            loadItemLocationInGoogleMap()
        }
    }


    private fun loadItemLocationInGoogleMap() {
        item.value.place?.let{
            binding.googleMapContent.visibility = VISIBLE
            val latLng = LatLng(it.latitude, it.longitude)
            // Adiciona pin no Map
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(it.name)
            )
            // Move a camera do Map para a mesma localização do Pin
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    15f
                )
            )
        }
    }

    companion object {

        const val ARG_ID = "ARG_ID"

        fun newIntent(
            context: Context,
            itemId: String
        ) =
            Intent(context, ItemDetailActivity::class.java).apply {
                putExtra(ARG_ID, itemId)
            }
    }


}