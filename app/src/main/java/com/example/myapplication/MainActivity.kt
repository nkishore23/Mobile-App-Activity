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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

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
         fun launchSkillsActivity(inputText: String) {
            val intent = Intent(this, SkillActivity::class.java).apply {
                putExtra("textValue", inputText)
            }
            // Launch the activity expecting a result
            startForResult.launch(intent)
        }



    private var resultText by mutableStateOf("No result yet")

    private fun updateResultText(value: String) {
        resultText = value
        Toast.makeText(this, resultText, Toast.LENGTH_SHORT).show()
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
    var localContext = LocalContext.current
    var textValue by remember { mutableStateOf("") }

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
//            Image(
//                painter = painterResource(id = profileImageId),
//                contentDescription = "Profile Image",
//                modifier = Modifier
//                    .size(120.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Name
                    Text(
                        text = "Name",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Address
                    Text(
                        text = "Address",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = address,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = textValue,
                        onValueChange = { textValue = it },
                        label = {  },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {  }
                    )

                }

                Button(onClick = {
                    onLaunchSkillsClick(textValue)
                }
                ) {
                    Text(
                        text = "Show Skills",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }


                Button(onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, textValue)
                            // Try to set WhatsApp as the package
                            //val pm = localContext.packageManager
                            setPackage("com.whatsapp")
                        }

                        localContext.startActivity(Intent.createChooser(intent, "Share to"))
                    } catch (e: Exception) {
                        Toast.makeText(
                            localContext,
                            "Unable to share. Please make sure WhatsApp is installed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                ) {
                    Text(
                        text = "Share ",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }


            }
        }
    }
}

/**
 * How to use this Composable:
 *
 * In your Activity or Fragment:
 *
 * class MainActivity : ComponentActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         setContent {
 *             YourAppTheme {
 *                 ProfileScreen(
 *                     name = "John Doe",
 *                     address = "123 Main Street, Madurai, Tamil Nadu, India",
 *                     profileImageId = R.drawable.your_profile_image
 *                 )
 *             }
 *         }
 *     }
 * }
 */

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview(onLaunchSkillsClick:(String) -> Unit) {
    MaterialTheme {
        ProfileScreen(
            name = "John Doe",
            address = "123 Main Street, Madurai, Tamil Nadu, India",
            onLaunchSkillsClick = {inputText -> onLaunchSkillsClick(inputText)}
        )
    }
}