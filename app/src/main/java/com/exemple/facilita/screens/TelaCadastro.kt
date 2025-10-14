package com.exemple.facilita.screens

import android.util.Patterns
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.exemple.facilita.R
import com.exemple.facilita.model.Register
import com.exemple.facilita.service.RetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.FlowRow
import retrofit2.await

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TelaCadastro(navController: NavController) {
    val facilitaApi = remember { RetrofitFactory().getUserService() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Estados dos campos
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var confirmarEmail by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

    // Estados de erro
    var isNomeError by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isConfirmarEmailError by remember { mutableStateOf(false) }
    var isTelefoneError by remember { mutableStateOf(false) }
    var isSenhaError by remember { mutableStateOf(false) }
    var isConfirmarSenhaError by remember { mutableStateOf(false) }

    // Regras de senha
    val hasUppercase = senha.any { it.isUpperCase() }
    val hasLowercase = senha.any { it.isLowerCase() }
    val hasDigit = senha.any { it.isDigit() }
    val hasSpecial = senha.any { !it.isLetterOrDigit() }
    val hasMinLength = senha.length >= 6

    fun validar(): Boolean {
        isNomeError = nome.isBlank()
        isEmailError = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        isConfirmarEmailError = email != confirmarEmail || confirmarEmail.isEmpty()
        isTelefoneError = telefone.length < 10 || !telefone.matches(Regex("^[0-9()\\-\\s+]+$"))
        isSenhaError = !(hasUppercase && hasLowercase && hasDigit && hasSpecial && hasMinLength)
        isConfirmarSenhaError = senha != confirmarSenha || confirmarSenha.isEmpty()

        return !isNomeError && !isEmailError && !isConfirmarEmailError &&
                !isTelefoneError && !isSenhaError && !isConfirmarSenhaError
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF444444))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Topo com logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
                    .background(Color(0xFF444444))
            ) {
                Image(
                    painter = painterResource(R.drawable.logotcc),
                    contentDescription = "Logo Facilita",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .height(140.dp)
                        .width(170.dp)
                        .padding(top = 20.dp, start = 30.dp)
                )

                Image(
                    painter = painterResource(R.drawable.texturalateral),
                    contentDescription = "Textura lateral",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .height(200.dp)
                        .width(180.dp)
                )
            }

            // Card branco
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(11f)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Cadastre-se",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Campo Nome
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it; isNomeError = false },
                        label = { Text("Nome Completo") },
                        placeholder = { Text("Seu Nome Completo") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = isNomeError,
                        supportingText = { if (isNomeError) Text("Nome é obrigatório") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; isEmailError = false },
                        label = { Text("E-mail") },
                        placeholder = { Text("seuemail@gmail.com") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = isEmailError,
                        supportingText = { if (isEmailError) Text("Email inválido") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = confirmarEmail,
                        onValueChange = { confirmarEmail = it; isConfirmarEmailError = false },
                        label = { Text("Confirmar e-mail") },
                        placeholder = { Text("seuemail@gmail.com") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = isConfirmarEmailError,
                        supportingText = {
                            if (isConfirmarEmailError) Text("Emails não coincidem")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = telefone,
                        onValueChange = { telefone = it; isTelefoneError = false },
                        label = { Text("Telefone") },
                        placeholder = { Text("(55)1191234-5678") },
                        leadingIcon = { Icon(Icons.Default.Phone, null) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = isTelefoneError,
                        supportingText = { if (isTelefoneError) Text("Telefone inválido") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = senha,
                        onValueChange = { senha = it; isSenhaError = false },
                        label = { Text("Senha") },
                        placeholder = { Text("Crie uma senha") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        isError = isSenhaError
                    )

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        maxItemsInEachRow = 4,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PasswordRequirement("Maiúscula", hasUppercase)
                        PasswordRequirement("Minúscula", hasLowercase)
                        PasswordRequirement("Número", hasDigit)
                        PasswordRequirement("Especial", hasSpecial)
                        PasswordRequirement("Mínimo 6 caracteres", hasMinLength)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = confirmarSenha,
                        onValueChange = { confirmarSenha = it; isConfirmarSenhaError = false },
                        label = { Text("Confirmar Senha") },
                        placeholder = { Text("Confirme sua senha") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        isError = isConfirmarSenhaError,
                        supportingText = {
                            if (isConfirmarSenhaError) Text("Senhas não coincidem")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (validar()) {
                                val cadastro = Register(
                                    nome = nome,
                                    email = email,
                                    telefone = telefone,
                                    senha_hash = senha
                                )
                                coroutineScope.launch(Dispatchers.IO) {
                                    try {
                                        facilitaApi.saveUser(cadastro).await()
                                        withContext(Dispatchers.Main) {
                                            navController.navigate("tela_termos")
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            println("Erro ao cadastrar: ${e.message}")
                                        }
                                    }
                                }
                            } else {
                                println("Os dados estão incorretos")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color(0xFF019D31), Color(0xFF06C755))
                                    ),
                                    shape = RoundedCornerShape(50)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Cadastrar",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Já possui uma conta? ",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "Fazer login",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF019D31),
                            modifier = Modifier.clickable {
                                navController.navigate("tela_login")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordRequirement(text: String, isMet: Boolean) {
    val color by animateColorAsState(
        targetValue = if (isMet) Color(0xFF06C755) else Color.Gray,
        label = "passwordColor"
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Check, null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, color = color, fontSize = 12.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaCadastroPreview() {
    val navController = rememberNavController()
    TelaCadastro(navController)
}
