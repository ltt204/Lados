package org.nullgroup.lados

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import org.nullgroup.lados.data.remote.ApiService.VietnamProvinceApiInterface
import org.nullgroup.lados.navigations.RoleBasedNavigation
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.VIETNAM_PROVINCE_BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LadosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RoleBasedNavigation(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

fun main() {
    var retrofit =
        Retrofit.Builder().baseUrl("$VIETNAM_PROVINCE_BASE_URL")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    var service = retrofit.create(VietnamProvinceApiInterface::class.java)
    var response: String
    runBlocking {
        response = service.getProvinces().toString()
    }

    File("output.json").writeText(response)
}