package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.graphics.ColorFilter

class MainActivity : ComponentActivity() {

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the result data
            val data = result.data
            val returnedValue = data?.getStringExtra("returnValue") ?: "No data returned"
            // Update your UI with the returned value
            updateResultText(returnedValue)
        }
    }

    private var resultText by mutableStateOf("No result yet")
    private var profileImageUri by mutableStateOf<String?>(null) // Uri of the profile image

    private fun updateResultText(value: String) {
        resultText = value
        Toast.makeText(this, resultText, Toast.LENGTH_SHORT).show()
    }

    private fun launchSkillsActivity(inputText: String) {
        val intent = Intent(this, SkillActivity::class.java).apply {
            putExtra("textValue", inputText)
        }
        // Launch the activity expecting a result
        startForResult.launch(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ProfileScreenPreview({ inputText ->
                    launchSkillsActivity(inputText)
                })
            }
        }
    }
}

/**
 * ProfileScreen Composable
 *
 * A screen displaying user profile information including:
 * - Profile image
 * - Name
 * - Address
 */
@Composable
fun ProfileScreen(
    name: String = "Your Name",
    address: String = "Your Address",
    onLaunchSkillsClick: (String) -> Unit
) {
    var textValue by remember { mutableStateOf("") }
    var editableName by remember { mutableStateOf(false) }
    var editableAddress by remember { mutableStateOf(false) }
    var profileImageUri by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Name and address editing state
    var editedName by remember { mutableStateOf(name) }
    var editedAddress by remember { mutableStateOf(address) }

    // Handle image pick result
    val imagePickLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            profileImageUri = uri.toString()
        }

    // Surface
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Profile Image
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.Gray, shape = CircleShape)
                    .clickable {
                        imagePickLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    Image(
                        painter = rememberImagePainter(profileImageUri),
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Name",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        IconButton(onClick = { editableName = !editableName }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Name")
                        }
                    }

                    if (editableName) {
                        OutlinedTextField(
                            value = editedName,
                            onValueChange = { editedName = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )
                    } else {
                        Text(
                            text = editedName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Address
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Address",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        IconButton(onClick = { editableAddress = !editableAddress }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Address")
                        }
                    }

                    if (editableAddress) {
                        OutlinedTextField(
                            value = editedAddress,
                            onValueChange = { editedAddress = it },
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )
                    } else {
                        Text(
                            text = editedAddress,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Skills Button
                    Button(onClick = { onLaunchSkillsClick(editedName) }) {
                        Text(
                            text = "Show Skills",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Share Button
                    Button(onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, editedName)
                                setPackage("com.whatsapp")
                            }
                            context.startActivity(Intent.createChooser(intent, "Share to"))
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Unable to share. Please make sure WhatsApp is installed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        Text(
                            text = "Share",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Weather Button
                    Button(onClick = {
                        context.startActivity(Intent(context, WeatherActivity::class.java))
                    }) {
                        Text(
                            text = "Show Weather",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Contact Button
                    Button(onClick = {
                        context.startActivity(Intent(context, ContactActivity::class.java))
                    }) {
                        Text(
                            text = "Add Contact",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview(onLaunchSkillsClick: (String) -> Unit) {
    MyApplicationTheme {
        ProfileScreen(
            name = "John Doe",
            address = "123 Main Street, Madurai, Tamil Nadu, India",
            onLaunchSkillsClick = onLaunchSkillsClick
        )
    }
}
