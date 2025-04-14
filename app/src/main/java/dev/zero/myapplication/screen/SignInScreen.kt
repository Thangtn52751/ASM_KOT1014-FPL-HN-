package dev.zero.myapplication.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.zero.myapplication.api.RetrofitInstance
import dev.zero.myapplication.api.models.LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

var loggedInUserId: String? = null

@Composable
fun SignInScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign In",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitInstance.authService.loginUser(username,password)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                                val user = response.body()!!.first()
                                loggedInUserId = user.id
                                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                navController.navigate("home") {
                                    popUpTo("signin") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text(text = "SIGN IN", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Don't have an account? ", fontSize = 14.sp, color = Color.Gray)
            Text(
                text = "Sign up",
                fontSize = 14.sp,
                color = Color.Red,
                modifier = Modifier.clickable {
                    navController.navigate("signup")
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}