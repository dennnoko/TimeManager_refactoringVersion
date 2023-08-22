package com.example.timemanager_refactoringversion.Screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.timemanager_refactoringversion.RoomOfTimeData.TimeDataDatabase
import com.example.timemanager_refactoringversion.RoomOfTimeData.TimeDataEntity
import com.example.timemanager_refactoringversion.RoomOfWorkContent.WCDatabase
import com.example.timemanager_refactoringversion.RoomOfWorkContent.WCEntity
import com.example.timemanager_refactoringversion.UIcomponents.CountWindow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeMeasurementScreen(navController: NavController, timeDB: TimeDataDatabase, wcDB: WCDatabase) {
    //現在時刻を取得
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    //フォーマット
    val sdf = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault())
    var formattedDate = sdf.format(Date(currentTime))

    // ストップウォッチ用
    // 開始時間の記録
    var startTime: Long by remember { mutableStateOf(0) }
    // 経過時間
    var ansTime: Int by remember { mutableStateOf(0) }
    //ボタンに表示する文字
    var btnTxt by remember { mutableStateOf("Start") }
    //表示する結果
    var ans by remember { mutableStateOf("00:00:00") }
    //時間
    var timeS by remember { mutableStateOf(0) }
    var timeM by remember { mutableStateOf(0) }
    var timeH by remember { mutableStateOf(0) }

    //選択された作業名を保持
    var wc by remember { mutableStateOf("") }

    //データ追加の為のコルーチンとcontext
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    //1秒ごとに画面の現在時刻を更新する
    LaunchedEffect(currentTime) {
        while (true) {
            currentTime = System.currentTimeMillis()

            if(btnTxt == "Stop") {
                ansTime = ((currentTime - startTime)/1000).toInt()
                timeS = ansTime % 60
                timeM = (ansTime/60) % 60
                timeH = (ansTime/3600) % 24

                ans = String.format("%02d", timeH) + ":" + String.format("%02d", timeM) + ":" + String.format("%02d", timeS)
            }

            delay(1000)
        }
    }

    //work content のリストを取得
    var wcList by remember { mutableStateOf(emptyList<String>()) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            wcDB.WCDao().getAllName().collect() {
                wcList = it
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        OutlinedCard(border = BorderStroke(3.dp, Color(0xff00f0f0))) {
            Text(
                text = "Current time",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(10.dp)
            )

            Text(
                text = formattedDate,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(10.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Divider()
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if(btnTxt == "Start") {
                    startTime = currentTime
                    ans = "00:00:00"
                    btnTxt = "Stop"
                }else if(btnTxt == "Stop") {
                    //データを記録
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            timeDB.TimeDataDao().insertData(TimeDataEntity(timeData = ansTime, wc = wc))
                        }
                    }
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()

                    ans = "00:00:00"
                    btnTxt = "Start"
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xff00f0f0)),
            shape = RoundedCornerShape(5.dp)
        ) {
            DefaultTxt(txt = btnTxt)
        }
        Spacer(modifier = Modifier.height(2.dp))

        CountWindow(ans)
        Spacer(modifier = Modifier.height(20.dp))

        Divider()
        Spacer(modifier = Modifier.height(20.dp))

        //作業の追加
        OutlinedCard(
            border = BorderStroke(3.dp, Color(0xff00f0f0)),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .width(250.dp)
                .height(230.dp)
        ) {
            val radioOptions = wcList
            val (selectedOption, onOptionSelected) = remember { mutableStateOf<String?>(null) }

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                radioOptions.forEach {str ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .selectable(
                                selected = (str == selectedOption),
                                onClick = {
                                    onOptionSelected(str)
                                }
                            )
                            .padding(horizontal = 16.dp)
                    ) {
                        RadioButton(
                            selected = (str == selectedOption),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xff00f0f0),
                                unselectedColor = Color.Black
                            ),
                            onClick = {
                                onOptionSelected(str)
                                wc = str
                            },
                            modifier = Modifier
                                .padding(3.dp)
                        )

                        DefaultTxt(txt = str)
                    }
                }
            }
        }

        //work contentを追加するところ
        var txt by remember { mutableStateOf("") }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            if(txt != "") {
                                wcDB.WCDao().insertWC(WCEntity(wc = txt))

                                txt = ""
                            }
                        }
                    }
                }
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "add work content"
            )

            OutlinedTextField(
                value = txt,
                onValueChange = {newTxt ->
                    txt = newTxt
                },
                singleLine = true,
                label = {
                    Text(text = "Start something new")
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                if(txt != "") {
                                    wcDB.WCDao().insertWC(WCEntity(wc = txt))

                                    txt = ""
                                }
                            }
                        }
                    }
                ),
                modifier = Modifier
                    .padding(10.dp)
                    .width(250.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        NavigateButton(txt = "Back") {
            navController.navigate("HomeScreen")
        }
    }
}

@Preview
@Composable
fun PreviewTimeMeasuremetScreen() {
    val navController = rememberNavController()

    TimeMeasurementScreen(
        navController = navController,
        TimeDataDatabase.getdb(LocalContext.current.applicationContext),
        WCDatabase.getDB(LocalContext.current.applicationContext)
    )
}

@Preview
@Composable
fun PreviewRadioBtn() {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf<String?>(null) }
    var str = "test"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .selectable(
                selected = (str == selectedOption),
                onClick = {
                    onOptionSelected(str)
                }
            )
            .padding(horizontal = 16.dp)
    ) {
        RadioButton(
            selected = (str == selectedOption),
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xff00f0f0),
                unselectedColor = Color.Black
            ),
            onClick = {
                onOptionSelected(str)
            },
            modifier = Modifier
                .padding(3.dp)
        )

        DefaultTxt(txt = str)
    }
}

/*
時間計測画面
特に何も考えずに思いつくまま機能を追加していった結果とても見にくいコードになってしまった。
この画面に実装した機能は大きく2つで、時間の計測と作業内容のリストへの新規項目の追加となっている。
 */