package ru.cherepanov.apps.pizzatestapp.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.cherepanov.apps.pizzatestapp.R
import ru.cherepanov.apps.pizzatestapp.databinding.MenuProductListItemBinding
import ru.cherepanov.apps.pizzatestapp.domain.model.ProductDescription

class ProductListAdapter : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {
    private var productList: List<ProductDescription> = listOf()

    fun updateData(list: List<ProductDescription>) {
        if (list == productList) return
        productList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class ProductViewHolder private constructor(private val binding: MenuProductListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(productShortDescription: ProductDescription) {
            binding.apply {
                title.text = productShortDescription.title
                itemDescription.text = productShortDescription.description
                selectButton.text = itemView.context.getString(
                    R.string.menu_list_item_select_button_text,
                    productShortDescription.price
                )
                Glide.with(itemView.context)
                    .load(productShortDescription.imageUrl)
                    .into(productImage)
            }
        }

        companion object {
            fun create(parent: ViewGroup): ProductViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MenuProductListItemBinding.inflate(layoutInflater, parent, false)
                return ProductViewHolder(binding)
            }
        }
    }
}