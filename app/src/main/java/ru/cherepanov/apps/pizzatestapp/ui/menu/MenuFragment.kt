package ru.cherepanov.apps.pizzatestapp.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.cherepanov.apps.pizzatestapp.R
import ru.cherepanov.apps.pizzatestapp.databinding.FragmentMenuBinding
import ru.cherepanov.apps.pizzatestapp.databinding.MenuCategoryChipBinding
import ru.cherepanov.apps.pizzatestapp.domain.model.Category
import ru.cherepanov.apps.pizzatestapp.domain.model.ProductDataResult
import ru.cherepanov.apps.pizzatestapp.domain.model.ProductDescription


@AndroidEntryPoint
class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding: FragmentMenuBinding
        get() = _binding!!
    private val viewModel by viewModels<MenuViewModel>()

    private var smoothScroller: SmoothScroller? = null

    private var enableChangeCategoryOnScrollTimestamp = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            productList.adapter = ProductListAdapter()
            bannerList.adapter = BannerListAdapter()
        }
        changeCategoryOnProductListScroll()
        changeBackgroundColorOnCollapsing()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { menuState ->
                updateBanners(menuState)
                updateProductList(menuState)
            }
        }
    }

    private fun changeCategoryOnProductListScroll() {
        binding.productList.setOnScrollChangeListener { _, _, _, _, _ ->
            val currentTimestamp = System.currentTimeMillis()
            val isSelectCategoryScrolling = (
                    smoothScroller?.isRunning == true ||
                            currentTimestamp < enableChangeCategoryOnScrollTimestamp
                    ) && binding.productList.scrollState == SCROLL_STATE_SETTLING
            if (isSelectCategoryScrolling) {
                enableChangeCategoryOnScrollTimestamp =
                    currentTimestamp + AFTER_SMOOTH_SCROLL_RUNNING_DELAY
                return@setOnScrollChangeListener
            }
            checkCategoryOfFirstVisibleProduct()
        }
    }

    private fun checkCategoryOfFirstVisibleProduct() {
        val layoutManager = binding.productList.layoutManager!! as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val categories = viewModel.uiState.value.productDataResult?.productData?.categories
        categories?.let {
            val categoryIndexOfFirstVisible = categories.indexOfLast { category ->
                category.productIndex <= firstVisibleItemPosition
            }
            checkCategoryChipAt(categoryIndexOfFirstVisible)
        }
    }

    private fun checkCategoryChipAt(index: Int) {
        if (index < 0) return
        val chip = binding.categories.getChildAt(index)
        if (binding.categories.checkedChipId == chip.id) return
        binding.categories.check(chip.id)
        bringCategoryChipIntoCenter(chip)
    }

    private fun bringCategoryChipIntoCenter(chip: View) {
        val scrollX = binding.categoriesScroll.scrollX
        val chipRelLeft = chip.left - scrollX
        val chipCenterPos = chipRelLeft + chip.width / 2
        val scrollWidth = binding.categoriesScroll.width
        val scrollDx = chipCenterPos - scrollWidth / 2
        binding.categoriesScroll.smoothScrollBy(scrollDx, 0)
    }

    private fun changeBackgroundColorOnCollapsing() {
        binding.appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            val color = if (verticalOffset == -binding.collapsingToolbarLayout.height) {
                R.color.collapsed_app_bar_bg
            } else {
                R.color.bg
            }
            setTopColor(color)
        }
    }

    private fun setTopColor(@ColorRes resId: Int) {
        val color = ContextCompat.getColor(requireContext(), resId)
        binding.appBarLayout.setBackgroundColor(color)
        binding.toolbarLayout.toolbar.setBackgroundColor(color)
    }

    private fun updateBanners(state: MenuState) {
        when (state.bannersStatus) {
            is Status.Success -> {
                val adapter = binding.bannerList.adapter as BannerListAdapter
                adapter.updateData(state.banners)
            }
            Status.Loading -> {

            }
            is Status.Error -> {

            }
        }
    }

    private fun updateProductList(state: MenuState) {
        when (state.productDataStatus) {
            is Status.Success -> {
                binding.apply {
                    productList.visibility = VISIBLE
                    categories.visibility = VISIBLE
                    progressIndicator.visibility = GONE
                }
                val productResult = state.productDataResult!!
                showOfflineModeSnackbarIfCachedData(productResult)
                val productData = productResult.productData
                updateProductDescriptions(productData.productDescriptions)
                updateCategories(productData.categories)
            }
            Status.Loading -> {
                binding.apply {
                    productList.visibility = GONE
                    categories.visibility = GONE
                    progressIndicator.visibility = VISIBLE
                }
            }
            is Status.Error -> {

            }
        }
    }

    private fun showOfflineModeSnackbarIfCachedData(productResult: ProductDataResult) {
        if (productResult.isCachedData) {
            Snackbar.make(
                binding.root,
                getString(R.string.snackbar_no_connection),
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(getString(R.string.snackbar_refresh)) {
                    viewModel.refresh()
                }.setAnchorView(binding.snackbarAnchorView)
                .show()
        }
    }

    private fun updateProductDescriptions(productDescriptions: List<ProductDescription>) {
        val adapter = binding.productList.adapter as ProductListAdapter
        adapter.updateData(productDescriptions)
    }

    private fun updateCategories(
        categories: List<Category>
    ) {
        binding.categories.removeAllViews()
        for (category in categories) {
            val chipBinding =
                MenuCategoryChipBinding.inflate(layoutInflater, binding.categories, true)
            val chip = chipBinding.root
            chip.text = category.name
            chip.setOnClickListener {
                bringCategoryChipIntoCenter(it)
                scrollToFirstProductOfCategory(category)
            }
        }
    }

    private fun scrollToFirstProductOfCategory(category: Category) {
        val layoutManager = binding.productList.layoutManager!!
        smoothScroller = createSmoothScroller()
        val smoothScroller = smoothScroller!!
        smoothScroller.targetPosition = category.productIndex
        enableChangeCategoryOnScrollTimestamp =
            System.currentTimeMillis() + AFTER_SMOOTH_SCROLL_RUNNING_DELAY
        layoutManager.startSmoothScroll(smoothScroller)
    }

    private fun createSmoothScroller() =
        object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }

        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val AFTER_SMOOTH_SCROLL_RUNNING_DELAY = 100
    }
}