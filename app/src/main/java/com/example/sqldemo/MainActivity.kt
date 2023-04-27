package com.example.sqldemo
import android.app.Activity
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
import com.example.sqldemo.store.State
import com.example.sqldemo.ui.theme.SQLDemoTheme
import org.json.JSONException

class MainActivity : ComponentActivity() {
    private val vm: State by viewModels()
    private var price: String = ""
    private val config = PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
        .clientId(Config.PAYPAL_CLIENT_ID)

//    private val PAYPAL_REQUEST_CODE = 200

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val confirmation: PaymentConfirmation? =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                    if (confirmation != null) {
                        val paymentDetails = confirmation.toJSONObject().toString(4)
                        try {
                            startActivity(
                                Intent(this, PaymentDetails::class.java)
                                    .putExtra("PaymentDetails", paymentDetails)
                                    .putExtra("PaymentAmount", price)
                            )
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
            else if (result.resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SQLDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val intent = Intent(this, PayPalService::class.java)
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
                    startService(intent)
                    price = main(vm = vm)
                }
            }
        }
    }

    private fun startPayment(price: String) {
        val payment = PayPalPayment(
            BigDecimal(java.lang.String.valueOf(price)), "USD", "Accenture Payment",
            PayPalPayment.PAYMENT_INTENT_SALE
        )
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
        startForResult.launch(intent)
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
                if(price=="") {
                    Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show()
                }else{
                startPayment(
                    price = price
                )}
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

}
