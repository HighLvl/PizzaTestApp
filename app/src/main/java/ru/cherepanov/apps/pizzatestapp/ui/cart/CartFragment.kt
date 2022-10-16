package ru.cherepanov.apps.pizzatestapp.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.cherepanov.apps.pizzatestapp.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCartBinding.inflate(inflater, container, false).root
    }
}