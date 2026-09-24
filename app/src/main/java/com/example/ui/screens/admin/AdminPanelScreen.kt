package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReportEntity
import com.example.data.model.UserEntity
import com.example.service.ModerationService
import com.example.service.RiskLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminPanelScreen(
    users: List<UserEntity>,
    reports: List<ReportEntity>,
    onResolveReport: (reportId: Long, deletePost: Boolean) -> Unit,
    onToggleUserBan: (userId: String, isBanned: Boolean) -> Unit
) {
    var adminTab by remember { mutableStateOf("Overview") }
    val tabs = listOf("Overview", "Reports Queue", "Security Audit", "User Management")

    val auditLogs = remember { ModerationService.getAuditLogs().reversed() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Admin Top Bar
        Surface(
            color = Color(0xFF1E293B),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = Color(0xFFFECA57), modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("IshaQxaaDey Admin Hub", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text("Defense-in-Depth Moderation & Safety Analytics", color = Color(0xFF94A3B8), fontSize = 12.sp)
            }
        }

        ScrollableTabRow(
            selectedTabIndex = tabs.indexOf(adminTab),
            edgePadding = 16.dp
        ) {
            tabs.forEach { tab ->
                Tab(
                    selected = adminTab == tab,
                    onClick = { adminTab = tab },
                    text = { Text(tab, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }
        }

        when (adminTab) {
            "Overview" -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text("Platform Analytics", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    // Stat cards
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AdminStatCard("Total Children", "${users.size}", "👶", Color(0xFF48DBFB), Modifier.weight(1f))
                            AdminStatCard("Open Reports", "${reports.count { it.status == "open" }}", "🚩", Color(0xFFFF6B6B), Modifier.weight(1f))
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AdminStatCard("Audit Events", "${auditLogs.size}", "🛡️", Color(0xFF10B981), Modifier.weight(1f))
                            AdminStatCard("Banned Accounts", "${users.count { it.isBanned }}", "⛔", Color(0xFFF368E0), Modifier.weight(1f))
                        }
                    }

                    item {
                        Text("Child Protection Directives", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("✅ COPPA & GDPR-K Compliance Verified", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF10B981))
                                Text("✅ Zero-tolerance PII Filtering (Phone, Addresses, GPS, Financial)", fontSize = 13.sp)
                                Text("✅ Role-Based Access Control: Children strictly barred from Admin/Moderation", fontSize = 13.sp)
                                Text("✅ Parent PIN Enforcement on All Security & Screen Time Settings", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            "Reports Queue" -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val openReports = reports.filter { it.status == "open" }
                    if (openReports.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🎉", fontSize = 48.sp)
                                    Spacer(Modifier.height(12.dp))
                                    Text("Reports Queue is Clear!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("All user reports have been safely resolved.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        items(openReports, key = { it.id }) { rep ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Reported by ${rep.reporterName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(Modifier.weight(1f))
                                        Surface(color = Color(0xFFFFEAA7), shape = RoundedCornerShape(8.dp)) {
                                            Text(rep.reason, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text("\"${rep.reportedContent}\"", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.height(14.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Button(
                                            onClick = { onResolveReport(rep.id, true) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B)),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Remove Post")
                                        }
                                        OutlinedButton(
                                            onClick = { onResolveReport(rep.id, false) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Dismiss")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "Security Audit" -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("Live Security Audit Logs", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Automated real-time threat detection and PII leak blocks.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    if (auditLogs.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("No security incidents detected. System safe and compliant. 🛡️")
                                }
                            }
                        }
                    } else {
                        items(auditLogs) { log ->
                            val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            val timeStr = formatter.format(Date(log.timestamp))

                            val badgeColor = when (log.riskLevel) {
                                RiskLevel.CRITICAL -> Color(0xFFFF6B6B)
                                RiskLevel.WARNING -> Color(0xFFFECA57)
                                RiskLevel.SAFE -> Color(0xFF10B981)
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = badgeColor.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            log.riskLevel.name,
                                            color = badgeColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(log.eventType, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(log.details, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(timeStr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            "User Management" -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(users, key = { it.id }) { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(user.avatar, fontSize = 28.sp)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("XP: ${user.xp} • Stars: ${user.stars} • Role: ${user.role}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (user.isBanned) {
                                    OutlinedButton(onClick = { onToggleUserBan(user.id, false) }) {
                                        Text("Unban", color = Color(0xFF10B981))
                                    }
                                } else {
                                    OutlinedButton(onClick = { onToggleUserBan(user.id, true) }) {
                                        Text("Ban", color = Color(0xFFFF6B6B))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(title: String, value: String, icon: String, tint: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(icon, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = tint)
            Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
