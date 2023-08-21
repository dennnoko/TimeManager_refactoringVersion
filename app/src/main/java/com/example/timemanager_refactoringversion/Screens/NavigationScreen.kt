package com.example.timemanager_refactoringversion.Screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.timemanager_refactoringversion.RoomOfTimeData.TimeDataDatabase
import com.example.timemanager_refactoringversion.RoomOfWorkContent.WCDatabase

@Composable
fun NavigationScreen() {
    //navControllerの作成
    val navController = rememberNavController()
    //データベースの作成
    val timeDB = TimeDataDatabase.getdb(context = LocalContext.current.applicationContext)
    val wcDB = WCDatabase.getDB(context = LocalContext.current.applicationContext)

    //NavHostで画面遷移を管理
    NavHost(navController, "HomeScreen") {
        composable("HomeScreen") { HomeScreen(navController, timeDB) }
        composable("DataScreen") { DataScreen(navController, timeDB, wcDB) }
        composable("TimeMeasurementScreen") { TimeMeasurementScreen(navController, timeDB, wcDB) }
    }
}