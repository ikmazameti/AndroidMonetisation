package com.devtides.androidmonetisation.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
 import com.devtides.androidmonetisation.adapter.CountryClickListener
import com.devtides.androidmonetisation.adapter.CountryListAdapter
import com.devtides.androidmonetisation.databinding.ActivityMainBinding
import com.devtides.androidmonetisation.model.BannerAd
import com.devtides.androidmonetisation.model.Country
import com.devtides.androidmonetisation.model.ListItem
import com.devtides.androidmonetisation.presenter.CountriesPresenter
import com.devtides.androidmonetisation.util.BillingAgent
import com.devtides.androidmonetisation.util.BillingCallback
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), CountryClickListener, CountriesPresenter.View, BillingCallback {
    private lateinit var binding: ActivityMainBinding
    private val countriesList = arrayListOf<ListItem>()
    private val countriesAdapter = CountryListAdapter(arrayListOf(), this)
   // private lateinit var rewardedAd: RewardedVideoAd
    private var billingAgent: BillingAgent? = null
    private var clickedCountry: Country? = null
    private val presenter = CountriesPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.list.apply {
            adapter = countriesAdapter
        }

        CoroutineScope(Dispatchers.IO).launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }

        billingAgent = BillingAgent(this, this)
    }

    override fun onDestroy() {
        billingAgent?.onDestroy()
        billingAgent = null
        super.onDestroy()
    }

    override fun onCountryClick(country: Country) {
//        if(BuildConfig.FLAVOR == "free") {
//            progress.visibility = View.VISIBLE
//            retryButton.visibility = View.GONE
//            list.visibility = View.GONE
//            showRewardedAd(country)
//        } else {
//            startActivity(DetailActivity.getIntent(this, country))
//        }

        clickedCountry = country
//        billingAgent?.purchaseView()
        billingAgent?.purchaseSubscription()
    }

    override fun onTokenConsumed() {
        startActivity(DetailActivity.getIntent(this@MainActivity, clickedCountry))
    }

//    private fun showRewardedAd(country: Country) {
//        val listener = object : RewardedVideoAdListener {
//            override fun onRewardedVideoAdClosed() {
//                showList()
//            }
//
//            override fun onRewardedVideoAdLeftApplication() {
//                showList()
//            }
//
//            override fun onRewardedVideoAdLoaded() {
//                rewardedAd.show()
//            }
//
//            override fun onRewardedVideoAdOpened() {
//            }
//
//            override fun onRewardedVideoCompleted() {
//                showList()
//            }
//
//            override fun onRewarded(p0: RewardItem?) {
//                rewardedAd.destroy(this@MainActivity)
//                startActivity(DetailActivity.getIntent(this@MainActivity, country))
//            }
//
//            override fun onRewardedVideoStarted() {
//            }
//
//            override fun onRewardedVideoAdFailedToLoad(p0: Int) {
//                showList()
//                rewardedAd.destroy(this@MainActivity)
//                startActivity(DetailActivity.getIntent(this@MainActivity, country))
//            }
//        }
//
//        rewardedAd = MobileAds.getRewardedVideoAdInstance(this)
//        rewardedAd.rewardedVideoAdListener = listener
//        rewardedAd.loadAd(getString(R.string.rewarded_ad_id), AdRequest.Builder().build())
//    }

    fun showList() {
        binding.apply {
            progress.visibility = View.GONE
            list.visibility = View.VISIBLE
            retryButton.visibility = View.GONE
        }
    }

    fun onRetry(v: View) {
        presenter.onRetry()
        binding.apply {

        retryButton.visibility = View.GONE
        progress.visibility = View.VISIBLE
            list.visibility = View.GONE
        }
    }

    override fun setCountries(countries: List<Country>?) {
        countriesList.clear()

        var i = 0
        countries?.let {
            for (country in countries) {
                i++
                if (i % 5 == 0) {
                    countriesList.add(BannerAd())
                }
                countriesList.add(country)
            }
        }

        countriesAdapter.updateCountries(countriesList)
        binding.apply {

        retryButton.visibility = View.GONE
        progress.visibility = View.GONE
            list.visibility = View.VISIBLE
        }
    }

    override fun onError() {
        Toast.makeText(this, "Unable to get Countries list. Please try again later", Toast.LENGTH_SHORT).show()
        binding.apply {

        retryButton.visibility = View.VISIBLE
        progress.visibility = View.GONE
            list.visibility = View.GONE
        }
    }
}
