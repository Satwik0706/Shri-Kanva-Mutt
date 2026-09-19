package com.satwik.example.mutt_app.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.satwik.example.mutt_app.data.AppNotification
import com.satwik.example.mutt_app.ui.theme.Terracotta

@Preview(showBackground = true)
@Composable
fun NotificationPopupPreview() {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Chaturmasya Notification", fontWeight = FontWeight.Bold, color = Terracotta) },
        text = { Text("The Chaturmasya Vrata starts from tomorrow. Join us for the special evening Arati.") },
        confirmButton = {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Terracotta)
            ) { Text("View Details", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = { }) { Text("Close") }
        },
        containerColor = Color.White,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    )
}
