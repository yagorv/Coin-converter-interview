package com.yago.coin.ui.views.tradedetail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yago.coin.R
import com.yago.coin.databinding.ActivityTradeDetailBinding
import com.yago.coin.ui.views.shared.base.BindingActivity
import com.yago.coin.ui.views.tradedetail.adapter.TradesAdapter
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class TradeDetailActivity : BindingActivity<ActivityTradeDetailBinding>(), HasSupportFragmentInjector {

    companion object {
        private const val SKU_ID_PARAMETER = "skuId"
    }

    fun start(activity: AppCompatActivity?, skuSelected: String) {
        val intent = Intent(activity, TradeDetailActivity::class.java)
        intent.putExtra(SKU_ID_PARAMETER, skuSelected)
        activity?.startActivity(intent)
    }

    private val skuSelected: String? by lazy { intent.getStringExtra(SKU_ID_PARAMETER) }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var tradesAdapter: TradesAdapter

    private lateinit var tradeDetailViewModel: TradeDetailViewModel

    override fun createDataBinding(): ActivityTradeDetailBinding =
        DataBindingUtil.setContentView(this, R.layout.activity_trade_detail)

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportPostponeEnterTransition()

        tradeDetailViewModel = ViewModelProvider(this, viewModelFactory)[TradeDetailViewModel::class.java]

        tradesAdapter = TradesAdapter()
        binding.tradesRecycler.layoutManager = LinearLayoutManager(this)
        binding.tradesRecycler.adapter = tradesAdapter

        initializeViewObservers()

        tradeDetailViewModel.onCreateTradeScreen(skuSelected)

        supportFragmentManager.executePendingTransactions()
    }

    private fun initializeViewObservers() {

        tradeDetailViewModel.transactions.observe(this, { list ->
            tradesAdapter.submitList(list)
        })

        tradeDetailViewModel.rates.observe(this, {
            Log.d("Coin_APP", "Rates received")
        })

    }

}