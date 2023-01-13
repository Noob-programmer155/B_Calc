package com.amrtm.android.bcalc

import android.os.Bundle
import android.widget.ScrollView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.amrtm.android.bcalc.ui.theme.BCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BCalcTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.surface
                ) {
                    Greeting("B Calc")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box {
            Text(text = "Hello $name!",
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally))
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Hello $name!",
                    modifier = Modifier.padding(12.dp))
                Text(text = "- $name!",
                    modifier = Modifier.padding(12.dp))
//                Image(painter = painterResource(id = R.drawable.screenshot_from_2022_12_26_08_52_58),
//                    contentDescription = null,
//                    modifier= Modifier.clip(),
//                    contentScale= ContentScale.Crop)
                TextField(value = "", onValueChange = {},
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Go
                    )
                )
                LazyColumn() {
//                    items()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BCalcTheme {
        Greeting("B Calc")
    }
}