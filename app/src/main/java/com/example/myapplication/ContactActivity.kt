package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import com.example.myapplication.db.Contacts
import com.example.myapplication.ui.theme.MyApplicationTheme

class ContactActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Create viewModel with the factory
                    val viewModel: ContactViewModel = viewModel()

                    // Show the contact form
                    ContactsScreen(
                        viewModel = viewModel,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    viewModel: ContactViewModel = viewModel()
) {
    val contacts by viewModel.allContacts.collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val enterTransition = slideInVertically(initialOffsetY = { it }) + fadeIn()
    val exitTransition = slideOutVertically(targetOffsetY = { it }) + fadeOut()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contacts") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Contact")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (contacts.isEmpty()) {
            // Show empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("No contacts found")
                    Button(onClick = { showAddDialog = true }) {
                        Text("Add Contact")
                    }
                }
            }
        } else {
            // Show list of contacts with animation
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    count = contacts.size,
                    key = { index -> contacts[index].id }
                ) { index ->
                    val contact = contacts[index]
                    AnimatedVisibility(
                        visible = true,
                        enter = enterTransition,
                        exit = exitTransition
                    ) {
                        ContactItem(
                            contact = contact,
                            onDelete = { viewModel.deleteContact(contact) }
                        )
                    }
                }
            }
        }

        // Add Contact Dialog
        if (showAddDialog) {
            ContactFormDialog(
                onDismiss = { showAddDialog = false },
                onContactSaved = {
                    showAddDialog = false
                    Toast.makeText(context, "Contact saved successfully", Toast.LENGTH_SHORT).show()
                },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun ContactItem(
    contact: Contacts,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.mobileNumber}"))
    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${contact.emailAddress}"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = contact.mobileNumber,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable {
                            context.startActivity(phoneIntent)
                        }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = contact.emailAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable {
                            context.startActivity(emailIntent)
                        }
                    )
                }

                if (contact.description.isNotBlank()) {
                    Text(
                        text = contact.description,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Contact"
                )
            }
        }
    }
}

@Composable
fun ContactFormDialog(
    onDismiss: () -> Unit,
    onContactSaved: () -> Unit,
    viewModel: ContactViewModel
) {
    var name by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var mobileError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Add New Contact",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = if (it.isBlank()) "Name is required" else null
                    },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it) } }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mobile number field
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = {
                        mobileNumber = it
                        mobileError = if (it.isBlank()) {
                            "Mobile number is required"
                        } else if (!it.all { char -> char.isDigit() }) {
                            "Mobile number should contain only digits"
                        } else {
                            null
                        }
                    },
                    label = { Text("Mobile Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    isError = mobileError != null,
                    supportingText = { mobileError?.let { Text(it) } }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Email address field
                OutlinedTextField(
                    value = emailAddress,
                    onValueChange = {
                        emailAddress = it
                        emailError = if (it.isBlank()) {
                            "Email is required"
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                            "Enter a valid email address"
                        } else {
                            null
                        }
                    },
                    label = { Text("Email Address") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError != null,
                    supportingText = { emailError?.let { Text(it) } }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            // Validate all fields before saving
                            nameError = if (name.isBlank()) "Name is required" else null
                            mobileError = if (mobileNumber.isBlank()) {
                                "Mobile number is required"
                            } else if (!mobileNumber.all { it.isDigit() }) {
                                "Mobile number should contain only digits"
                            } else {
                                null
                            }
                            emailError = if (emailAddress.isBlank()) {
                                "Email is required"
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                                "Enter a valid email address"
                            } else {
                                null
                            }

                            // If no errors, save to database
                            if (nameError == null && mobileError == null && emailError == null) {
                                scope.launch {
                                    try {
                                        // Generate random ID or use your preferred ID generation method
                                        val newContact = Contacts(
                                            id = (0..1000000).random(), // Simple random ID generation
                                            name = name,
                                            mobileNumber = mobileNumber,
                                            emailAddress = emailAddress,
                                            description = description
                                        )

                                        viewModel.insertContact(newContact)
                                        onContactSaved()
                                    } catch (e: Exception) {
                                        // Handle error
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
