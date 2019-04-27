package me.andrewda.payment

import com.paypal.api.payments.*
import com.paypal.base.rest.APIContext
import com.paypal.base.rest.PayPalRESTException
import com.paypal.api.payments.PaymentExecution

object PayPal {
    /**
     * PAYMENT MODEL/HOW TO
     *
     * 1. Create payment with `createPayment()` method, and accept the Payment object
     * 2. Get the link in `payment.links` with "rel=approval_url" (add &useraction=commit)
     * 3. Send the user to that link (they will be prompted to choose how to pay)
     * 4. User will be redirected to /api/payments/return?paymentId=<PAYMENT_ID>&token=<TOKEN>&PayerID=<PAYER_ID>
     * 5. Finish and execute the payment with `executePayment()` method with the payerId
     */

    private const val CLIENT_ID = "AZ-LgFG9PwN2kSc6hAVikpBgsOEJupfCcbXger31m5pmD9cNVAm53EJYnWp6t5Xqfv7WrIFNL4yJPsdn"
    private const val CLIENT_SECRET = "EFP_ESCRqvtAGwRbKLFR-Zw0xUEMQN4rU41K_KWtwTw5e3zR0NyUEWBwqad4qoRHgQ1hC5CCl3dL1vc5"
    private val apiContext = APIContext(CLIENT_ID, CLIENT_SECRET, "sandbox")

    private var webProfileId = ""

    init {
        createWebProfile()
    }

    private fun createWebProfile() {
        val profiles = WebProfile.getList(apiContext) ?: return

        if (profiles.size > 0) {
            webProfileId = profiles.first().id ?: ""
        } else {
            val profile = WebProfile().apply {
                presentation = Presentation().apply { brandName = "Carry it Forward" }
                inputFields = InputFields().apply {
                    noShipping = 1
                    addressOverride = 1
                }

                temporary = false
            }

            val response = profile.create(apiContext) ?: return

            webProfileId = response.id
        }
    }

    private fun generatePaymentDetails(cost: Double) = Payment().apply {
        intent = "order"
        experienceProfileId = webProfileId
        payer = Payer().apply {
            paymentMethod = "paypal"
        }
        transactions = listOf(
            Transaction().apply { amount = Amount("USD", cost.toString()) }
        )
        redirectUrls = RedirectUrls().apply {
            cancelUrl = "http://127.0.0.1:8080/api/payments/cancel"
            returnUrl = "http://127.0.0.1:8080/api/payments/return"
        }
    }

    fun createPayment(cost: Double) = try {
        val p = generatePaymentDetails(cost).create(apiContext)
        println(Payment.getLastResponse())
        p
    } catch (exception: PayPalRESTException) {
        println("Could not create payment...")
        println(exception)
        null
    } catch (exception: Exception) {
        println("Could not create payment...")
        println(exception)
        null
    }

    fun executePayment(paymentId: String, payerId: String): Payment {
        val payment = Payment().apply { id = paymentId }
        val paymentExecution = PaymentExecution().apply { this.payerId = payerId }
        return payment.execute(apiContext, paymentExecution)
    }
}
