package com.antoninclouet.hanzilock

import android.app.Application
import com.antoninclouet.hanzilock.billing.BillingManager
import com.antoninclouet.hanzilock.data.HskData

class HanziLockApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        HskData.ensureLoaded(this)
        BillingManager.get(this).start()
    }
}
