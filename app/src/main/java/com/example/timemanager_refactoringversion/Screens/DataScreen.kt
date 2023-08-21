package com.example.timemanager_refactoringversion.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.timemanager_refactoringversion.RoomOfTimeData.TimeDataDatabase
import com.example.timemanager_refactoringversion.RoomOfTimeData.TimeDataEntity
import com.example.timemanager_refactoringversion.RoomOfWorkContent.WCDatabase
import com.example.timemanager_refactoringversion.RoomOfWorkContent.WCEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DataScreen(navController: NavController, timeDataDatabase: TimeDataDatabase, wcDatabase: WCDatabase) {
    var timeData by remember { mutableStateOf(emptyList<TimeDataEntity>()) }
    var wcList by remember { mutableStateOf(emptyList<WCEntity>()) }

    //ボタンを押したときにデータを操作する為のコルーチンスコープ
    val coroutineScope = rememberCoroutineScope()
    //データ削除で使う
    val context = LocalContext.current

    //作業時間の記録のListを取得する
    LaunchedEffect( Unit ) {
        withContext(Dispatchers.IO) {
            //以下のコードを timeData = timeDataDatabase.TimeDataDao().getAll() とするとType miss matchエラーとなる
            timeDataDatabase.TimeDataDao().getAll().collect() {
                timeData = it
            }
        }
    }

    //作業内容のリストを取得する
    LaunchedEffect( Unit ) {
        withContext(Dispatchers.IO) {
            wcDatabase.WCDao().getAll().collect() {
                wcList = it
            }
        }
    }


    //ここからUI
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        //記録データの一覧
        Text(
            text = "All Data",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedCard(
            border = BorderStroke(3.dp, Color.Gray),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .size(250.dp, 150.dp)
        ) {
            //取得した作業時間のデータリストをLazyColumnで一覧表示する
            LazyColumn(modifier = Modifier.padding(10.dp)) {
                items(timeData) {data ->
                    Row() {
                        DefaultTxt(
                            txt = data.wc,
                            modifier = Modifier
                                .size(120.dp, 20.dp)
                        )

                        //表示する時間のフォーマット
                        //保存されたデータは秒単位のため、時間、分、秒のそれぞれの数値を計算する
                        val timeS = data.timeData % 60
                        val timeM = (data.timeData/60) % 60
                        val timeH = data.timeData/3600

                        //String.formatを使用して時、分、秒を2桁で表示するように指定する
                        DefaultTxt(
                            txt = String.format("%02d", timeH) + ":" + String.format("%02d", timeM) + ":" + String.format("%02d", timeS),
                            modifier = Modifier
                                .size(70.dp, 20.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        timeDataDatabase.TimeDataDao().deleteAll(timeData)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(Color.Red),
            shape = RoundedCornerShape(5.dp)
        ) {
            DefaultTxt(txt = "Delete all data")
        }
        Spacer(modifier = Modifier.height(10.dp))

        Divider()
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Work contents listbb",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))

        //RadioButtonで選択されたもののIDを保持する変数 削除ボタンで利用するのでここに定義しておく
        var deleteId: Int? by remember { mutableStateOf(null) }

        //RadioButtonで作業内容の一覧を表示する
        OutlinedCard(
            border = BorderStroke(3.dp, Color.Gray),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .size(250.dp, 150.dp)
        ) {
            //RadioButtonで選択されたものを保持する変数
            var selectedOption by remember { mutableStateOf("") }
            //work contentのリストをラジオボタンで表示
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                wcList.forEach { wc ->
                    Row(verticalAlignment = CenterVertically) {
                        RadioButton(
                            selected = selectedOption == wc.wc,
                            onClick = {
                                selectedOption = wc.wc
                                deleteId = wc.id
                            }
                        )

                        DefaultTxt(txt = wc.wc)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        //選択したアイテムの削除ボタン
        Button(
            onClick = {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        //削除 deletedIdがnullではない場合にのみ動作
                        if (deleteId != null) {
                            // !!でdeleteIdがnullではないことを明示する
                            wcDatabase.WCDao().deleteWCById(deleteId!!)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(Color.Red)
        ) {
            DefaultTxt(txt = "Delete selected item")
        }
        Spacer(modifier = Modifier.height(10.dp))

        Divider()
        Spacer(modifier = Modifier.height(20.dp))

        //戻るボタン
        Button(
            onClick = { navController.navigate("HomeScreen") },
            colors = ButtonDefaults.buttonColors(Color(0xff00f0f0)),
            shape = RoundedCornerShape(5.dp),
        ) {
            DefaultTxt(txt = "Back")
        }
    }
}

@Composable
fun DefaultTxt(txt: String, modifier: Modifier = Modifier) {
    Text(
        text = txt,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Black,
        modifier = modifier
    )
}

@Preview
@Composable
fun PreviewDataScreen() {
    val navController = rememberNavController()
    val timeDataDatabase = TimeDataDatabase.getdb(LocalContext.current.applicationContext)
    val wcDatabase = WCDatabase.getDB(LocalContext.current.applicationContext)

    Surface() {
        DataScreen(navController = navController, timeDataDatabase = timeDataDatabase, wcDatabase = wcDatabase)
    }
}