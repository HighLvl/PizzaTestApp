package ru.cherepanov.apps.pizzatestapp.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.cherepanov.apps.pizzatestapp.databinding.MenuBannerItemBinding

class BannerListAdapter : RecyclerView.Adapter<BannerListAdapter.BannerViewHolder>() {
    private var bannerList: List<Unit> = listOf()
    fun updateData(bannerList: List<Unit>) {
        if (bannerList == this.bannerList) return
        this.bannerList = bannerList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    class BannerViewHolder private constructor(private val binding: MenuBannerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {}

        companion object {
            fun create(parent: ViewGroup): BannerViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MenuBannerItemBinding.inflate(layoutInflater, parent, false)
                return BannerViewHolder(binding)
            }
        }
    }
}