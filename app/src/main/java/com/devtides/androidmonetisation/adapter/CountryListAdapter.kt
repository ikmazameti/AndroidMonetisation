package com.devtides.androidmonetisation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devtides.androidmonetisation.databinding.AdRowLayoutBinding
import com.devtides.androidmonetisation.databinding.RowLayoutBinding
import com.devtides.androidmonetisation.model.Country
import com.devtides.androidmonetisation.model.ListItem
import com.devtides.androidmonetisation.util.TYPE_COUNTRY
import com.devtides.androidmonetisation.util.getProgressDrawable
import com.devtides.androidmonetisation.util.loadImage
import com.google.android.gms.ads.AdRequest

class CountryListAdapter(
    private var countries: ArrayList<ListItem>,
    private val clickListener: CountryClickListener,
) :
    RecyclerView.Adapter<CountryListAdapter.CountryListViewHolder>() {

    fun updateCountries(newCountries: ArrayList<ListItem>) {
        countries.clear()
        countries.addAll(newCountries)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) = countries[position].type

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): CountryListViewHolder {

        return when (type) {
            TYPE_COUNTRY -> {
                val binding =
                    RowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CountryViewHolder(binding, clickListener)
            }

            else -> {
                val binding =
                    AdRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }
        }
    }

    override fun getItemCount() = countries.size

    override fun onBindViewHolder(holder: CountryListViewHolder, position: Int) {
        holder.bind(countries[position])
    }

    abstract class CountryListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: ListItem)
    }

    class CountryViewHolder(
        private val binding: RowLayoutBinding, private val clickListener: CountryClickListener
    ) : CountryListViewHolder(binding.root) {
        override fun bind(item: ListItem) {
            val country = item as Country
            binding.name.text = country.countryName
            binding.capital.text = country.capital
            binding.imageView.loadImage(
                country.flag, getProgressDrawable(binding.imageView.context)
            )
            binding.root.setOnClickListener { clickListener.onCountryClick(country) }
        }
    }

    class AdViewHolder(private val binding: AdRowLayoutBinding) :
        CountryListViewHolder(binding.root) {

        override fun bind(item: ListItem) {
            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
        }
    }
}