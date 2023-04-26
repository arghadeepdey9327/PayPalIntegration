package com.example.sqldemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sqldemo.ui.theme.SQLDemoTheme
import org.json.JSONException
import org.json.JSONObject

class PaymentDetails: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContent{
            SQLDemoTheme{
                androidx.compose.material.Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ){

                    @Composable
                    fun Result(id:String,status:String,amount:String) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Text(text = id)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text=status)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text=amount)
                        }
                    }
                    @Throws(JSONException::class)
                    @Composable
                    fun showDetails(jsonDetails: JSONObject,
                                    paymentAmount: String){
                        Result(id=jsonDetails.getString("id"),
                            status = jsonDetails.getString("state"),
                            amount = "$paymentAmount INR"
                        )
                    }
                    val intent = intent
                        val jsonDetails = JSONObject(intent.getStringExtra("PaymentDetails"))
                        showDetails(
                            jsonDetails.getJSONObject("response"),
                            intent.getStringExtra("PaymentAmount")!!
                        )
                }

                }
            }
        }
    }
