package com.antoninclouet.hanzilock.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Abonnement "HanziLock Pro" via Google Play Billing (StoreKit → Play).
 * IDs à créer à l'identique dans la Play Console (Produits → Abonnements).
 */
object ProductIds {
    const val MONTHLY = "pro_monthly"
    const val YEARLY = "pro_yearly"
    val all = listOf(MONTHLY, YEARLY)
}

class BillingManager private constructor(context: Context) : PurchasesUpdatedListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    val products: StateFlow<List<ProductDetails>> = _products.asStateFlow()

    private val client: BillingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build(),
        )
        .build()

    fun start() {
        if (client.isReady) {
            refresh()
            return
        }
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) refresh()
            }

            override fun onBillingServiceDisconnected() { /* réessai à la prochaine ouverture */ }
        })
    }

    private fun refresh() {
        scope.launch { loadProducts() }
        scope.launch { refreshEntitlements() }
    }

    private suspend fun loadProducts() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ProductIds.all.map {
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(it)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                },
            )
            .build()
        val result = client.queryProductDetails(params)
        if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            _products.value = result.productDetailsList
                ?.sortedBy { it.productId } // monthly avant yearly
                .orEmpty()
        }
    }

    private suspend fun refreshEntitlements() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        val purchases = client.queryPurchasesAsync(params).purchasesList
        _isPro.value = purchases.any { it.purchaseState == Purchase.PurchaseState.PURCHASED }
        purchases.forEach { acknowledgeIfNeeded(it) }
    }

    fun purchase(activity: Activity, product: ProductDetails) {
        val offerToken = product.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: return
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(product)
                        .setOfferToken(offerToken)
                        .build(),
                ),
            )
            .build()
        client.launchBillingFlow(activity, params)
    }

    fun restore() = refresh()

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            scope.launch {
                purchases.forEach { acknowledgeIfNeeded(it) }
                refreshEntitlements()
            }
        }
    }

    private suspend fun acknowledgeIfNeeded(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            client.acknowledgePurchase(
                com.android.billingclient.api.AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build(),
            )
        }
    }

    companion object {
        @Volatile
        private var instance: BillingManager? = null

        fun get(context: Context): BillingManager =
            instance ?: synchronized(this) {
                instance ?: BillingManager(context).also { instance = it }
            }
    }
}

/** Prix affiché pour un abonnement (première phase de la première offre). */
val ProductDetails.displayPrice: String
    get() = subscriptionOfferDetails
        ?.firstOrNull()
        ?.pricingPhases
        ?.pricingPhaseList
        ?.firstOrNull()
        ?.formattedPrice
        ?: ""

val ProductDetails.periodLabel: String
    get() = when (
        subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.billingPeriod
    ) {
        "P1M" -> "par mois"
        "P1Y" -> "par an"
        "P1W" -> "par semaine"
        else -> ""
    }
