
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.exemple.facilita.R
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.getValue
import com.exemple.facilita.screens.PasswordRequirement

@Composable
fun TelaAlterarSenha(navController: NavController) {
    var senhaAtual by remember { mutableStateOf("") }
    var novaSenha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }

    // ✅ Requisitos da senha
    val hasUppercase = novaSenha.any { it.isUpperCase() }
    val hasLowercase = novaSenha.any { it.isLowerCase() }
    val hasDigit = novaSenha.any { it.isDigit() }
    val hasSpecial = novaSenha.any { !it.isLetterOrDigit() }
    val hasMinLength = novaSenha.length >= 6

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 Cabeçalho
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Voltar",
                tint = Color.Black,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { navController.popBackStack() }
            )

            Text(
                text = "Perfil",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 108.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Foto de perfil
        Box(contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = painterResource(id = R.drawable.foto_perfil),
                contentDescription = "Foto do usuário",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.LightGray, CircleShape),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = Color(0xFF019D31), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Alterar senha",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Campos de senha
        OutlinedTextField(
            value = senhaAtual,
            onValueChange = { senhaAtual = it },
            placeholder = { Text("Digite a senha atual") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF019D31),
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = novaSenha,
            onValueChange = { novaSenha = it },
            placeholder = { Text("Nova Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF019D31),
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = confirmarSenha,
            onValueChange = { confirmarSenha = it },
            placeholder = { Text("Confirmar nova senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF019D31),
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ✅ Título das regras
        Text(
            text = "Sua senha deve conter:",
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )

        // ✅ Regras lado a lado (compactas)
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
            PasswordRequirement("Minimo de 6 caracteres", hasMinLength)
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 🔹 Mensagem de erro ou sucesso
        if (mensagem.isNotEmpty()) {
            Text(
                text = mensagem,
                color = if (erro) Color.Red else Color(0xFF019D31),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

        }

        Spacer(modifier = Modifier.height(70.dp))
        // 🔹 Botão salvar
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF019D31), Color(0xFF007A26))
                    )
                )
                .clickable {
                    if (novaSenha != confirmarSenha) {
                        erro = true
                        mensagem = "As senhas não coincidem."
                    } else if (!hasUppercase || !hasLowercase || !hasDigit || !hasMinLength) {
                        erro = true
                        mensagem = "A senha não atende aos requisitos."
                    } else {
                        erro = false
                        mensagem = "Senha alterada com sucesso!"
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Salvar",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PasswordRequirement(text: String, isMet: Boolean) {
    val color by animateColorAsState(
        targetValue = if (isMet) Color(0xFF06C755) else Color.Gray,
        label = "passwordColor"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            color = color,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TelaAlterarSenhaPreview() {
    val navController = rememberNavController()
    TelaAlterarSenha(navController)
}
