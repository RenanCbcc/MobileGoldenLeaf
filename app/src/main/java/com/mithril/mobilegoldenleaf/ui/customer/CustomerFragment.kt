package com.mithril.mobilegoldenleaf.ui.customer

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mithril.mobilegoldenleaf.R
import com.mithril.mobilegoldenleaf.adapters.ClientAdapter
import com.mithril.mobilegoldenleaf.models.Customer
import com.mithril.mobilegoldenleaf.persistence.AppDataBase
import com.mithril.mobilegoldenleaf.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_clients_list.view.*

class CustomerFragment : Fragment() {

    private lateinit var activityContext: MainActivity

    private val adapter by lazy {
        ClientAdapter(activityContext)
    }

    private val presenter by lazy {
        val repository = AppDataBase.getInstance(activityContext).customerRepository
        CustomerPresenter(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activityContext = activity as MainActivity
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_clients_list, null)
        configureList(view)
        configFba(view)
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        searchCustomer()
    }

    private fun searchCustomer() {
        presenter.get(
                whenSucceeded = {
                    adapter.update(it)
                }, whenFailed = {
            val toast = Toast.makeText(context, R.string.getting_customer_error, Toast.LENGTH_SHORT)
            toast.show()
            //TODO Load categories offline here.
        }
        )
    }

    override fun onResume() {
        super.onResume()
        searchCustomer()
    }

    private fun configureList(view: View) {
        with(view) {
            clients_list.adapter = adapter
        }
    }

    private fun configFba(view: View) {
        view.fragment_clients_list_fab_new_client.setOnClickListener {
            CustomerFormDialog(activityContext, activityContext.window.decorView as ViewGroup)
                    .show(
                            whenSucceeded = { customerCreated: Customer ->
                                onDataBaseChanged(customerCreated)
                            },
                            whenFailed = { errorMessage ->
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT)
                                        .show()
                            }
                    )

        }
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        val customer = adapter.getItem(item.groupId)
        when (item.itemId) {
            R.id.client_list_menu_edit -> openEditCustomerDialog(customer)
        }
        return super.onContextItemSelected(item)

    }

    private fun openEditCustomerDialog(customer: Customer) {
        CustomerFormDialog(activityContext, activityContext.window.decorView as ViewGroup, customer.id)
                .show(
                        whenSucceeded = { customerEdited: Customer ->
                            onDataBaseChanged(customerEdited)
                        },
                        whenFailed = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT)
                                    .show()
                        }
                )
    }

    private fun onDataBaseChanged(customer: Customer) {
        searchCustomer()
        val text = "Banco de dados atualizado com " + customer.name
        val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast.show()
    }

    private fun initFooter(): TextView {
        val txtFooter = TextView(context)
        // txtFooter.text = resources.getQuantityString(R.plurals.footer_text_client, adapter.count, adapter.count)
        txtFooter.setBackgroundColor(Color.LTGRAY)
        txtFooter.gravity = Gravity.END
        txtFooter.setPadding(0, 8, 8, 8)
        return txtFooter
    }


    companion object {
        fun newInstance(): CustomerFragment {
            return CustomerFragment()

        }
    }
}