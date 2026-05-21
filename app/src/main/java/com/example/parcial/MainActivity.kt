package com.example.parcial

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.parcial.ui.MainViewModel
import com.example.parcial.ui.screens.CharacterDetailScreen
import com.example.parcial.ui.screens.CharacterListScreen
import com.example.parcial.ui.theme.ParcialTheme
import com.example.parcial.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        NotificationHelper.createNotificationChannel(this)
        
        enableEdgeToEdge()
        setContent {
            ParcialTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = hiltViewModel()
                val context = LocalContext.current

                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        NotificationHelper.showNotification(context, "Permisos concedidos", "¡Ahora recibirás actualizaciones!")
                    } else {
                        Toast.makeText(context, "Las notificaciones están deshabilitadas. No recibirás avisos de carga.", Toast.LENGTH_LONG).show()
                    }
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        val permission = Manifest.permission.POST_NOTIFICATIONS
                        val hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                        
                        if (!hasPermission) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, permission)) {
                                // Tarea 4.3: Rationale (Explicación simplificada vía Toast antes de pedirlo de nuevo o informar)
                                Toast.makeText(context, "Necesitamos el permiso para avisarte cuando los datos estén listos.", Toast.LENGTH_SHORT).show()
                            }
                            permissionLauncher.launch(permission)
                        }
                    }
                }

                NavHost(navController = navController, startDestination = "list") {
                    composable("list") {
                        CharacterListScreen(
                            viewModel = viewModel,
                            onCharacterClick = { id ->
                                navController.navigate("detail/$id")
                            }
                        )
                    }
                    composable(
                        route = "detail/{characterId}",
                        arguments = listOf(navArgument("characterId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val characterId = backStackEntry.arguments?.getInt("characterId") ?: return@composable
                        CharacterDetailScreen(
                            characterId = characterId,
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}