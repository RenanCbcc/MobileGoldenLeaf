package com.mithril.mobilegoldenleaf.ui.category.presenters

import android.util.Log
import com.mithril.mobilegoldenleaf.asynctask.category.GetCategoryByIdTask
import com.mithril.mobilegoldenleaf.asynctask.category.SaveCategoryTask
import com.mithril.mobilegoldenleaf.models.Category
import com.mithril.mobilegoldenleaf.persistence.repository.CategoryRepository
import com.mithril.mobilegoldenleaf.ui.category.interfaces.CategoryFormView
import com.mithril.mobilegoldenleaf.ui.category.validators.CategoryValidator
import kotlinx.android.synthetic.main.fragment_category_form.*

class CategoryFormPresenter(private val view: CategoryFormView,
                            private val repository: CategoryRepository) {

    fun loadBy(categoryId: Long) {
        val category = GetCategoryByIdTask(categoryId, repository).execute().get()
        view.show(category)
    }

    fun save(category: Category): Boolean {
        return if (CategoryValidator().validate(category)) {
            try {
                SaveCategoryTask(repository, category)
                Log.println(Log.DEBUG, "Debuging", "category id: "+category.id)
                true
            } catch (e: Exception) {
                view.showSavingCategoryError()
                false
            }
        } else {
            view.showInvalidTitleError()
            false
        }
    }
}