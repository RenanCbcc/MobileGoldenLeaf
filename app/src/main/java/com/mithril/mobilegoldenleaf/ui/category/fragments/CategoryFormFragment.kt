package com.mithril.mobilegoldenleaf.ui.category.fragments

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.mithril.mobilegoldenleaf.R
import com.mithril.mobilegoldenleaf.models.Category
import com.mithril.mobilegoldenleaf.persistence.MobileGoldenLeafDataBase
import com.mithril.mobilegoldenleaf.ui.category.interfaces.CategoryFormView
import com.mithril.mobilegoldenleaf.ui.category.interfaces.OnCategorySavedListener
import com.mithril.mobilegoldenleaf.ui.category.presenters.CategoryFormPresenter
import kotlinx.android.synthetic.main.dialogfragment_category_form.*
import kotlinx.android.synthetic.main.dialogfragment_category_form.view.*

class CategoryFormFragment : DialogFragment(), CategoryFormView {

    private val presenter by lazy {
        context.let {
            if (it != null) {
                val repository = MobileGoldenLeafDataBase.getInstance(it).categoryRepository
                CategoryFormPresenter(this, repository)
            } else {
                throw IllegalArgumentException("Contexto inválido")
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialogfragment_category_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryId = arguments?.getLong(EXTRA_CATEGORY_ID, 0L) ?: 0L
        if (categoryId != 0L) {
            presenter.loadBy(categoryId)
            dialog.setTitle(R.string.edit_category)
        }
        dialog.setTitle(R.string.add_category)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_action_concluded_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.fragment_form_action_concluded) {
            val category = saveCategory()
            if (category != null) {
                if (activity is OnCategorySavedListener) {
                    val listener = activity as OnCategorySavedListener
                    listener.onCategorySaved()
                }
            }
            dialog.dismiss()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun show(category: Category) {
        form_category_title.setText(category.title.toString())
    }

    override fun showInvalidTitleError() {
        Toast.makeText(context, R.string.invalid_title_error, Toast.LENGTH_SHORT)
                .show()
    }

    override fun showSavingCategoryError() {
        Toast.makeText(context, "Não foi possível salvar a categoria", Toast.LENGTH_SHORT)
                .show()
    }


    private fun saveCategory(): Category? {
        val category = Category()
        val categoryId = arguments?.getLong(EXTRA_CATEGORY_ID, 0L) ?: 0L
        if (categoryId != 0L) {
            category.id = categoryId
        }
        category.title = form_category_title.text.toString()
        return if (presenter.save(category)) {
            category
        } else {
            null
        }

    }

    fun open(fm: FragmentManager) {
        if (fm.findFragmentByTag(DIALOG_TAG) == null)
            show(fm, DIALOG_TAG)
    }


    companion object {
        private const val DIALOG_TAG = "categoryId"
        private const val EXTRA_CATEGORY_ID = "formDialog"

        fun newInstance(id: Long = 0): CategoryFormFragment {
            val fragment = CategoryFormFragment()
            val args = Bundle()
            args.putLong(EXTRA_CATEGORY_ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}