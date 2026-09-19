package com.satwik.example.mutt_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.satwik.example.mutt_app.ui.theme.Terracotta

@Composable
fun TermsAndConditionsPopup(
    onAgree: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    var isChecked by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { /* Cannot dismiss without agreeing */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Privacy Policy & Terms",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Terracotta,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = """
                            Welcome to the Sri Kanva Matha App. Your privacy is important to us.
                            
                            1. Information We Collect
                            We collect info necessary for religious services, including:
                            • Personal Info: Name, phone, email submitted via forms.
                            • Payment Verification: Transaction IDs for offerings.
                            • Technical Data: Device IDs via Google & Firebase.

                            2. How We Use Your Data
                            • To process Seva registrations and requests.
                            • To manage membership and administration.
                            • To operate app features like Panchanga.

                            3. Security
                            • We do NOT sell your personal info.
                            • Data is encrypted in transit.

                            4. Your Rights
                            • You may request data deletion at any time by emailing satwikgj.cs24@bmsce.ac.in.
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        colors = CheckboxDefaults.colors(checkedColor = Terracotta)
                    )
                    Text(
                        text = "I have read and agree to the Privacy Policy and Terms of Service.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { if (isChecked) onAgree() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isChecked,
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("I AGREE", color = Color.White, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = { uriHandler.openUri("https://sites.google.com/view/shrikanvamuthaprivacy/home") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Full Policy", color = Terracotta)
                }
            }
        }
    }
}
