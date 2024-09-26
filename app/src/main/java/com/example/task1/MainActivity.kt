package com.example.task1

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.task1.databinding.ActivityMainBinding
import com.example.task1.model.viewmodel.DataViewModel
import com.example.task1.network.BaseResponse
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val viewModel by viewModels<DataViewModel>()
    private lateinit var binding: ActivityMainBinding
    var titlesString = ""
    var description: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.show()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.titleCopyButton.setOnClickListener(this)
        binding.titleShareButton.setOnClickListener(this)
        binding.descriptionCopyButton.setOnClickListener(this)
        binding.descriptionShareButton.setOnClickListener(this)
        if (isInternetAvailable()) {
            dataMessageObserver()
            viewModel.getMessageData()
        } else {
            binding.titleTXT.text = "Wait for ...."
            binding.descriptionTXT.text = "Wait...."
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show()
        }

    }
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
    private fun dataMessageObserver() {
        viewModel._dataResponse.observe(this) {
            when (it) {
                is BaseResponse.Loading -> {
                    binding.titleTXT.text = "Wait...."
                    binding.descriptionTXT.text = "Wait...."
                    Toast.makeText(this, "Loading......", Toast.LENGTH_SHORT).show()
                }

                is BaseResponse.Error -> {
                    binding.titleTXT.text = "Sorry There is not Text Message !"
                    binding.descriptionTXT.text = "Sorry There is not Text Message !"
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }

                is BaseResponse.Success -> {
                    lifecycleScope.launch {
                        getTitleAndDescription(it)
                    }

                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    suspend fun getTitleAndDescription(it: BaseResponse.Success<JsonObject>) {
        var choicesArray: JsonArray? = null
        try {

            choicesArray = it.data?.getAsJsonArray("choices")
        } catch (e: Exception) {
            Log.e("Aaradhya", "Error" + e)
        }
        var mess: JsonObject?
        var content: String = ""
        if (choicesArray != null) {
            for (i in 0 until choicesArray.size()) {
                try {
                    mess = choicesArray.get(i).asJsonObject.getAsJsonObject("message")
                    content = mess.get("content").asString

                } catch (e: Exception) {
                    Log.e("Aaradhya", "Error" + e)
                }
                try {
                    val contentJsonObject: JsonObject = JsonParser.parseString(content).asJsonObject
                    val titles: JsonArray = contentJsonObject.getAsJsonArray("titles")
                    titlesString = titles.joinToString(separator = "\n ") { it.asString }
                    description = contentJsonObject.get("description").asString
                } catch (e: Exception) {
                    Log.e("Aaradhya", "Error" + e)
                }

            }
            binding.titleTXT.text = titlesString
            binding.descriptionTXT.text = description

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.titleCopyButton -> {
                if (!titlesString.equals("")) {
                    copyToClipboard(titlesString)
                } else {
                    Toast.makeText(this, "You cannot copy Empty Title text", Toast.LENGTH_SHORT)
                        .show()

                }
            }

            R.id.titleShareButton -> {
                if (!titlesString.equals("")) {
                    shareText(titlesString)
                } else {
                    Toast.makeText(
                        this,
                        "You cannot Share Empty Description text",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            R.id.descriptionCopyButton -> {
                if (!description.equals("")) {
                    copyToClipboard(description)
                } else {
                    Toast.makeText(
                        this,
                        "You cannot copy Empty Description text",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            R.id.descriptionShareButton -> {
                if (!description.equals("")) {
                    shareText(description)
                } else {
                    Toast.makeText(
                        this,
                        "You cannot Share Empty Description text",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun shareText(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}