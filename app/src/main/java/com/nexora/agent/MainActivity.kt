package com.nexora.agent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

private val Navy = Color(0xFF05091C)
private val Panel = Color(0xFF10172D)
private val Mint = Color(0xFF00F5A0)
private val Cyan = Color(0xFF00D9F5)
private val MainText = Color(0xFFF4F7FF)
private val Muted = Color(0xFF9BA8C7)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NexoraChat() }
    }
}

private data class ChatMessage(val user: Boolean, val text: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NexoraChat() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("nexora", 0) }
    var endpoint by remember { mutableStateOf(prefs.getString("endpoint", "https://your-backend.example.com/v1/chat/completions") ?: "") }
    var key by remember { mutableStateOf(prefs.getString("key", "") ?: "") }
    var model by remember { mutableStateOf(prefs.getString("model", "your-model-name") ?: "") }
    var settings by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf(ChatMessage(false, "Hi! I'm Agent, your Nexora AI assistant. Configure your authorized backend in Settings to begin.")) }
    val scope = rememberCoroutineScope()

    MaterialTheme(colorScheme = darkColorScheme(primary = Mint, secondary = Cyan, background = Navy, surface = Panel)) {
        Scaffold(
            containerColor = Navy,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(Modifier.size(38.dp).background(Brush.linearGradient(listOf(Mint, Cyan)), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                                Text("N", color = Navy, fontWeight = FontWeight.Black, fontSize = 25.sp)
                            }
                            Column {
                                Text("NEXORA", color = MainText, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                                Text("AI AGENT", color = Mint, fontSize = 10.sp, letterSpacing = 2.sp)
                            }
                        }
                    },
                    actions = { IconButton(onClick = { settings = true }) { Icon(Icons.Default.Settings, "Settings", tint = Muted) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy)
                )
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 14.dp)) {
                Surface(color = Panel, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                    Text("  ●  Agent ready  ·  Configure your model in Settings", color = Mint, fontSize = 12.sp, modifier = Modifier.padding(10.dp))
                }
                LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 10.dp)) {
                    items(messages) { msg ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = if (msg.user) Arrangement.End else Arrangement.Start) {
                            Column(Modifier.widthIn(max = 320.dp).background(if (msg.user) Color(0xFF123A43) else Panel, RoundedCornerShape(18.dp)).padding(14.dp)) {
                                Text(if (msg.user) "YOU" else "AGENT", color = if (msg.user) Cyan else Mint, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                                Spacer(Modifier.height(7.dp))
                                Text(msg.text, color = MainText, fontSize = 15.sp, lineHeight = 22.sp)
                            }
                        }
                    }
                    if (loading) item { TypingIndicator() }
                    if (error.isNotBlank()) item { Text(error, color = Color(0xFFFF8585), fontSize = 12.sp) }
                }
                Row(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(24.dp)).padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f),
                        placeholder = { Text("Message Agent...", color = Muted) }, maxLines = 5,
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, cursorColor = Mint),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { submitMessage(input, messages, endpoint, key, model, scope, { loading = it }, { error = it }); input = "" })
                    )
                    IconButton(
                        onClick = { val p = input.trim(); if (p.isNotEmpty() && !loading) { input = ""; submitMessage(p, messages, endpoint, key, model, scope, { loading = it }, { error = it }) } },
                        enabled = input.isNotBlank() && !loading,
                        modifier = Modifier.size(42.dp).background(Brush.linearGradient(listOf(Mint, Cyan)), CircleShape)
                    ) { Icon(Icons.Default.ArrowUpward, "Send", tint = Navy) }
                }
                Text("AI responses can be inaccurate. Verify important information.", color = Muted, fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp))
            }
        }

        if (settings) {
            AlertDialog(
                onDismissRequest = { settings = false },
                title = { Text("Agent backend settings") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Use an endpoint you own or are authorized to access.", color = Muted, fontSize = 12.sp)
                        OutlinedTextField(endpoint, { endpoint = it }, label = { Text("Chat completions URL") }, singleLine = true)
                        OutlinedTextField(model, { model = it }, label = { Text("Model name") }, singleLine = true)
                        OutlinedTextField(key, { key = it }, label = { Text("API key (optional)") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                        Text("For production, keep provider secrets on your server.", color = Muted, fontSize = 11.sp)
                    }
                },
                confirmButton = { TextButton(onClick = { prefs.edit().putString("endpoint", endpoint.trim()).putString("model", model.trim()).putString("key", key).apply(); settings = false }) { Text("Save", color = Mint) } },
                dismissButton = { TextButton(onClick = { settings = false }) { Text("Cancel") } },
                containerColor = Panel
            )
        }
    }
}

@Composable
private fun TypingIndicator() {
    val transition = rememberInfiniteTransition(label = "typing")
    Row(Modifier.background(Panel, RoundedCornerShape(16.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        repeat(3) { i ->
            val alpha by transition.animateFloat(0.25f, 1f, infiniteRepeatable(tween(500, delayMillis = i * 150), RepeatMode.Reverse), label = "dot$i")
            Box(Modifier.size(7.dp).alpha(alpha).background(Mint, CircleShape))
        }
        Spacer(Modifier.width(4.dp))
        Text("Agent is responding", color = Muted, fontSize = 12.sp)
    }
}

private fun submitMessage(
    prompt: String, messages: MutableList<ChatMessage>, endpoint: String, key: String, model: String,
    scope: kotlinx.coroutines.CoroutineScope, setLoading: (Boolean) -> Unit, setError: (String) -> Unit
) {
    if (prompt.isBlank() || messages.isEmpty() && prompt.isBlank()) return
    messages.add(ChatMessage(true, prompt))
    setError("")
    setLoading(true)
    scope.launch {
        try {
            val answer = withContext(Dispatchers.IO) { requestAnswer(messages.toList(), endpoint, key, model) }
            messages.add(ChatMessage(false, answer))
        } catch (e: Exception) {
            setError("Agent connection failed: ${e.message ?: "unknown error"}. Check Settings and connection.")
        } finally { setLoading(false) }
    }
}

private fun requestAnswer(messages: List<ChatMessage>, endpoint: String, key: String, model: String): String {
    require(endpoint.startsWith("https://") && !endpoint.contains("your-backend.example.com")) { "Set a valid HTTPS backend URL in Settings." }
    require(model.isNotBlank()) { "Set a model name in Settings." }
    val history = JSONArray()
    history.put(JSONObject().put("role", "system").put("content", "You are Agent, the AI assistant inside Nexora. Be helpful, accurate, and clear."))
    messages.takeLast(20).forEach { history.put(JSONObject().put("role", if (it.user) "user" else "assistant").put("content", it.text)) }
    val payload = JSONObject().put("model", model).put("messages", history).toString().toRequestBody("application/json; charset=utf-8".toMediaType())
    val request = Request.Builder().url(endpoint).post(payload).header("Content-Type", "application/json").apply { if (key.isNotBlank()) header("Authorization", "Bearer $key") }.build()
    val client = OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).callTimeout(70, TimeUnit.SECONDS).build()
    client.newCall(request).execute().use { response ->
        val body = response.body?.string().orEmpty()
        if (!response.isSuccessful) error("HTTP ${response.code}: ${body.take(180)}")
        JSONObject(body).optJSONArray("choices")?.optJSONObject(0)?.optJSONObject("message")?.optString("content")?.takeIf { it.isNotBlank() }
            ?: error("Unexpected API response; expected choices[0].message.content.")
    }
}
