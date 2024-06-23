package com.example.gatewayrestaurant.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Message
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TableLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gatewayrestaurant.Activity.AddEditAddressActivity
import com.example.gatewayrestaurant.Activity.LoginActivity
import com.example.gatewayrestaurant.Activity.PaymentModeActivity
import com.example.gatewayrestaurant.Activity.TabLayoutActivity
import com.example.gatewayrestaurant.Adapter.CartAdapter
import com.example.gatewayrestaurant.Class.BaseFragment
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.Session.SessionManager
import com.example.gatewayrestaurant.Utils.AppSettingsPref
import com.example.gatewayrestaurant.Utils.CommonSingleton
import com.example.gatewayrestaurant.Utils.LanguageInputFilter
import com.example.gatewayrestaurant.databinding.FragmentCartBinding
import com.example.gatewayrestaurant.model.Cartmodel
import com.example.gatewayrestaurant.model.model
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.stream.DoubleStream.builder
import java.util.stream.IntStream.builder


class CartFragment : BaseFragment() {

    private lateinit var mBinding: FragmentCartBinding
    private val user = FirebaseAuth.getInstance().currentUser
    private var sessionManager: SessionManager? = null
    var adapter1: CartAdapter? = null
    var amountPayable = 0.0

    private var itemCountCallBack: CartAdapter.ItemCountCallBack =
        object : CartAdapter.ItemCountCallBack {
            override fun itemCount(itemCount: Int) {
                if (itemCount == 0) {
                    mBinding.nsvParent.visibility = View.GONE
                    mBinding.btnPlaceOrder.visibility = View.GONE
                    mBinding.clEmptyCart.visibility = View.VISIBLE
                } else {
                    CommonSingleton.cartCount = itemCount
                    mBinding.nsvParent.visibility = View.VISIBLE
                    mBinding.btnPlaceOrder.visibility = View.VISIBLE
                    mBinding.clEmptyCart.visibility = View.GONE
                }
                mBinding.shimmerCart.stopShimmer()
                mBinding.shimmerCart.visibility = View.GONE
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        sessionManager = SessionManager(mActivity)

        if (user != null){
            mBinding.shimmerCart.startShimmer()
            mBinding.rvCartItems.layoutManager =
                LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
                val databaseRef = FirebaseDatabase.getInstance().reference.child("cartTotal")
                val query1 = databaseRef.child(user.uid)
                query1.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val totalAmount = snapshot.child("totalAmount").value.toString()
                        mBinding.tvItemTotal.text = "₹ $totalAmount /-"
                        mBinding.tvToPay.text = "₹ $totalAmount /-"
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            showAddress()

            val (flatNumber, location, landmark, label) = setAddress()
            if (!flatNumber.isNullOrEmpty() && !location.isNullOrEmpty()) {
                mBinding.parcelOption.check(R.id.rbHomedelivery)
                mBinding.clSelectAddress.visibility = View.VISIBLE
                mBinding.clSelectedAddress.visibility = View.VISIBLE
                mBinding.tvSelectAddress.visibility = View.GONE
            }else{
                mBinding.clSelectAddress.visibility = View.GONE
                mBinding.tvSelectAddress.visibility = View.GONE
                mBinding.clSelectedAddress.visibility = View.GONE
            }
            mBinding.parcelOption.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
                val id = mBinding.parcelOption.checkedRadioButtonId
                when (id) {
                    R.id.rbTakeaway -> mBinding.clSelectAddress.visibility = View.GONE
                    R.id.rbHomedelivery -> {
                        val (flatNumber, location, landmark, label) = setAddress()
                        if (!flatNumber.isNullOrEmpty() && !location.isNullOrEmpty()) {
                            mBinding.clSelectAddress.visibility = View.VISIBLE
                            mBinding.clSelectedAddress.visibility = View.VISIBLE
                            mBinding.tvSelectAddress.visibility = View.GONE

                        }else{
                            mBinding.clSelectAddress.visibility = View.VISIBLE
                            mBinding.tvSelectAddress.visibility = View.VISIBLE
                            mBinding.clSelectedAddress.visibility = View.GONE

                        }
                    }
                }
            })

            mBinding.ivSelectAddress.setOnClickListener{
                startActivity(Intent(mActivity, AddEditAddressActivity::class.java))

            }
            mBinding.tvSelectAddress.setOnClickListener{
                startActivity(Intent(mActivity, AddEditAddressActivity::class.java))
            }
            getDataFromFirebase()

        }else{
            mBinding.shimmerCart.stopShimmer()
            mBinding.shimmerCart.visibility = View.GONE
            mBinding.noLogIn.root.visibility = View.VISIBLE
        }



        setUpToolbar()

        mBinding.etCouponCode.filters = arrayOf(LanguageInputFilter(), InputFilter.LengthFilter(20))

        mBinding.btnAddToCart.setOnClickListener {
            startActivity(Intent(mActivity, TabLayoutActivity::class.java))
        }

        mBinding.noLogIn.btnLogIn.setOnClickListener {
            startActivity(Intent(mActivity, LoginActivity::class.java))
        }

        mBinding.btnPlaceOrder.setOnClickListener {
            val selectedOptionId = mBinding.parcelOption.checkedRadioButtonId
            if (selectedOptionId == -1) {
                showShortToast("Please select Delivery Type")
            } else {
                redirectToPayment()
            }

        }
        return mBinding.root
    }

    fun setUpToolbar() {
        mBinding.toolbar.tvHeader.text = getString(R.string.cart)
        mBinding.toolbar.ivBack.visibility = View.GONE
        mBinding.toolbar.shoppingCart.visibility = View.GONE
    }

    private fun getDataFromFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val options1 = FirebaseRecyclerOptions.Builder<Cartmodel>().setQuery(
            FirebaseDatabase.getInstance().reference.child("cart").child(uid), Cartmodel::class.java
        ).build()
        adapter1 = CartAdapter(options1, mActivity,itemCountCallBack)
        mBinding.rvCartItems.adapter = adapter1

    }

    private fun redirectToPayment() {
        val instruction = mBinding.etInstruction.text.toString()
        if (mBinding.rbTakeaway.isChecked) {
            val intent = Intent(mActivity, PaymentModeActivity::class.java)
            intent.putExtra("deliveryType", "takeaway")
            intent.putExtra("instruction", instruction)
            intent.putExtra("finalAmount", amountPayable.toString())
            startActivity(intent)
        } else {
            val (flatNumber, location, landmark, label) = setAddress()

            if (flatNumber.isNullOrEmpty() && landmark.isNullOrEmpty() && location.isNullOrEmpty()) {
                Toast.makeText(mActivity, "Please Select Delivery Address!", Toast.LENGTH_SHORT).show()
            } else if (flatNumber != ""  && location != "") {
                val intent = Intent(mActivity, PaymentModeActivity::class.java)
                intent.putExtra("deliveryType", "homeDelivery")
                intent.putExtra("instruction", instruction)
                intent.putExtra("finalAmount", amountPayable.toString())
                startActivity(intent)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun showAddress() {
        val (flatNumber, location, landmark, label) = setAddress()
        if (flatNumber.isNullOrEmpty()) {
            mBinding.clSelectAddress.visibility = View.VISIBLE
            mBinding.tvSelectAddress.visibility = View.VISIBLE
            mBinding.clSelectedAddress.visibility = View.GONE
        } else {
            mBinding.clSelectAddress.visibility = View.VISIBLE
            mBinding.clSelectedAddress.visibility = View.VISIBLE
            mBinding.tvSelectAddress.visibility = View.GONE
            val addressString = if (landmark?.isNotEmpty() == true) {
                "$flatNumber $location ($landmark)"
            } else {
                "$flatNumber $location"
            }
            mBinding.tvFullAddress.text = addressString
            val styledLabel = SpannableStringBuilder("Delivery at ")
            if (label != null) {
                styledLabel.append(label)
            } else {
                styledLabel.append("Other") // or handle the null case as per your requirement
            }
            val boldSpan = StyleSpan(Typeface.BOLD)
            styledLabel.setSpan(boldSpan, 12, styledLabel.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            mBinding.tvLabel.text = styledLabel
            when (label) {
                "Home" -> {
                    mBinding.ivLocation.setImageResource(R.drawable.nav_home)
                    mBinding.ivLocation.setColorFilter(ContextCompat.getColor(mContext, R.color.black))
                }
                "Office" -> mBinding.ivLocation.setImageResource(R.drawable.ic_vector_office_bag)
                else -> {
                    mBinding.ivLocation.setImageResource(R.drawable.map_location)
                }
            }

        }
    }

    override fun onStart() {
        adapter1?.startListening()
        super.onStart()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onPaymentSuccess(s: String) {
        val builder = AlertDialog.Builder(mActivity)
        builder.setTitle("Payment Id")
        builder.setMessage(s)
        builder.show()
    }

    fun onPaymentError(i: Int, s: String) {
        Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show()
    }

    private fun setAddress(): Array<String?> {
        val selectedAddress = sessionManager!!.getSelectedAddressData()
        val flatNumber = selectedAddress.flatNumber
        val location = selectedAddress.location
        val landmark = selectedAddress.landmark
        val label = selectedAddress.label

        return arrayOf(flatNumber, location, landmark, label)
    }


}

