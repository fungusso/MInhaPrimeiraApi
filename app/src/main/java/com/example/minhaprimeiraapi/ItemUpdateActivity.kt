package com.example.minhaprimeiraapi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.minhaprimeiraapi.databinding.ActivityItemUpdateBinding
import com.example.minhaprimeiraapi.model.Item
import com.example.minhaprimeiraapi.model.ItemValue
import com.example.minhaprimeiraapi.service.Result
import com.example.minhaprimeiraapi.service.RetrofitClient
import com.example.minhaprimeiraapi.service.safeApiCall
import com.example.minhaprimeiraapi.ui.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class ItemUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemUpdateBinding

    private lateinit var item: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        loadItem()

    }


    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.updateCTA.setOnClickListener {
            upadate()
            startActivity((Intent(this,MainActivity::class.java)))
        }

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

    private fun upadate() {
        if (!validateForm()) return
        CoroutineScope(Dispatchers.IO).launch {
 //           val id = intent.getStringExtra(ARG_ID) ?: ""
            val result = safeApiCall {
                RetrofitClient.apiService.updItem(
                    item.id,
                    item.value.copy(licence = binding.licence.text.toString(),
                                       name = binding.name.text.toString(),
                                       year = binding.year.text.toString(),
                                   imageUrl = binding.imageUrl.text.toString()
                    )
                )
            }

            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        Toast.makeText(
                            this@ItemUpdateActivity,
                            R.string.error_update,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Result.Success -> {
                        Toast.makeText(
                            this@ItemUpdateActivity,
                            getString(R.string.success_update),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        if (binding.name.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Modelo"), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.year.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Ano"), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.imageUrl.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Image Url"), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.licence.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.error_validate_form, "Placa"), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun handleSuccess() {

        binding.name.setText(item.value.name)
        binding.year.setText(item.value.year)
        binding.licence.setText(item.value.licence)
        binding.imageUrl.setText(item.value.imageUrl)
//        binding.image.loadUrl(item.value.imageUrl)
        Picasso.get()
            .load(item.value.imageUrl)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_error)
            .transform(CircleTransform())
            //       .into(holder.imageView)
            .into(binding.image)
    }

    companion object {

        private const val ARG_ID = "ARG_ID"

        fun newIntent(
            context: Context,
            itemId: String
        ) =
            Intent(context, ItemUpdateActivity::class.java).apply {
                putExtra(ARG_ID, itemId)
            }
    }

}