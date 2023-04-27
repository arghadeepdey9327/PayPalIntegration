package com.example.sqldemo
import android.content.Context
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SQLDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    main(vm = vm,
                    context = this
                    )
                }
            }
        }
    }
    @Composable
    fun main(vm: State,context:Context){
        val price = vm.amount.collectAsState().value
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            UserInput(vm = vm)
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick =
            //Below code is starting of callback lambda function
            //what's call when button pressed by user
            {
                Integration(
                    PAYPAL_CLIENT_ID =Config.PAYPAL_CLIENT_ID,
                    from =context,
                    to =PaymentDetails::class.java,
                    price=price,
                 toCompany = "Company Name here"
                    ).startPayment()
            }) {
                Text(
                    text = stringResource(id = R.string.btn)
                )
            }
        }
    }
    @Composable
    fun UserInput(vm: State) {
        TextField(
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            onValueChange = { vm.updateAmount(it) },
            value = vm.amount.collectAsState().value
        )
    }
}