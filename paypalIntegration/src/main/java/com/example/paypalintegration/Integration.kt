package com.example.paypalintegration

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.paypal.android.sdk.payments.*
import org.json.JSONException
import java.math.BigDecimal

class Integration(
    val PAYPAL_CLIENT_ID: String,
    val from: Context,
    val to: Class<out ComponentActivity>,
    val price:String,
    val toCompany:String
): ComponentActivity() {

    val config = PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
        .clientId(PAYPAL_CLIENT_ID)
    var startForResult: ActivityResultLauncher<Intent> =
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
                                Intent(from, to)
                                    .putExtra("PaymentDetails", paymentDetails)
                                    .putExtra("PaymentAmount", price)
                            )
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(from, "Cancel", Toast.LENGTH_SHORT).show()
            else if (result.resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(from, "Invalid", Toast.LENGTH_SHORT).show()
        }

//    override fun onCreate(si: Bundle?){
//        super.onCreate(si)
//    }

    fun startPayment() {
        Log.i("start","")
        val payment = PayPalPayment(
            BigDecimal(java.lang.String.valueOf(price)), "USD", toCompany,
            PayPalPayment.PAYMENT_INTENT_SALE
        )
        val intent = Intent(from, PaymentActivity::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
        startForResult.launch(intent)
    }
}
