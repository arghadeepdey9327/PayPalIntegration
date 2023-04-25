package com.example.sqldemo
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import com.paypal.android.sdk.payments.*
import java.math.BigDecimal
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.paypalintegration.Integration
import com.example.sqldemo.store.State
import com.example.sqldemo.ui.theme.SQLDemoTheme
import org.json.JSONException

class MainActivity : ComponentActivity() {
    private val vm: State by viewModels()
    private var price: String = ""
    private lateinit var integration: Integration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SQLDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    integration=Integration(
                        PAYPAL_CLIENT_ID =Config.PAYPAL_CLIENT_ID,
                        from =this,
                        to =PaymentDetails::class.java,
                        price=price)
                    integration.startService()
                    price = main(vm = vm)
                }
            }
        }
    }
    @Composable
    fun main(vm: State): String {
        val price = vm.amount.collectAsState().value
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            UserInput(vm = vm)
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                integration.startPayment(
                    price=price,
                    toCompany = "Accenture PLC"
                )
            }) {
                Text(
                    text = stringResource(id = R.string.btn)
                )
            }
        }
        return price
    }
    @Composable
    fun UserInput(vm: State) {
        TextField(
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            onValueChange = { vm.updateAmount(it) },
            value = vm.amount.collectAsState().value
        )
    }
    override fun onDestroy() {
        integration.onDestroy(from = this,to=PaymentDetails::class.java)
        super.onDestroy()
    }
}