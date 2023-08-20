package com.example.timemanager_refactoringversion.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.timemanager_refactoringversion.R

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Image(
            painter = painterResource(id = R.drawable.clock_icon),
            contentDescription = "app icon",
            modifier = Modifier
                .size(250.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))

        NavigateButton(txt = "Time measurement") {
            navController.navigate("TimeMeasurementScreen")
        }
        Spacer(modifier = Modifier.height(15.dp))

        NavigateButton(txt = "Data management") {
            navController.navigate("DataScreen")
        }
    }
}


//ボタンを切り分けた。
// ラムダ式でナビゲーションの処理を呼び出したところで定義する形にしたが、ナビゲーションのrouteのStringを引数で取る形の方が使いやすい。
// 今回はラムダ式の練習としてこの形にした。
@Composable
fun NavigateButton(txt: String, btnTodo: () -> Unit) {
    Button(
        onClick = btnTodo,
        colors = ButtonDefaults.buttonColors(Color(0xff00f0f0)),
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 80.dp, end = 80.dp)
    ) {
        Text(
            text = txt,
            color = Color.Black
        )
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    val navController = rememberNavController()

    Surface() {
        HomeScreen(navController = navController)
    }
}