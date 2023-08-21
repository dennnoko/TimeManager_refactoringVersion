package com.example.timemanager_refactoringversion.UIcomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timemanager_refactoringversion.RoomOfTimeData.TimeDataDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TotalTimeGraph(timeDB: TimeDataDatabase) {
    //work content のリストを取得
    var wcList: List<String> by remember { mutableStateOf(emptyList()) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            wcList = timeDB.TimeDataDao().getDistinctCWList()
        }
    }

    OutlinedCard(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
            .border(BorderStroke(5.dp, Color(0xff00f0f0)), RoundedCornerShape(10.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Total Time",
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(8.dp)
            )

            Column(modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
            ) {
                LazyRow() {
                    items(wcList) {wc ->
                        //work contentの合計時間を取得
                        var wcTime by remember { mutableStateOf(0) }
                        var wcH by remember { mutableStateOf(0) }
                        var wcM by remember { mutableStateOf(0) }

                        LaunchedEffect(Unit) {
                            withContext(Dispatchers.IO) {
                                wcTime = timeDB.TimeDataDao().getTotalTimeByWC(wc) / 60
                                wcH = wcTime / 60
                                wcM = wcTime % 60
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(55.dp, wcTime.dp)
                                    .background(Color.Green)
                            )
                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = wc,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(modifier = Modifier.height(3.dp))

                            Text(text = wcH.toString() + "h " + wcM.toString() + "m",)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewTotalGraph() {
    val timeDB = TimeDataDatabase.getdb(LocalContext.current.applicationContext)

    Surface() {
        TotalTimeGraph(timeDB)
    }
}