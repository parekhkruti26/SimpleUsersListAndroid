package com.gitusers.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gitusers.R
import com.gitusers.databinding.RowUserBinding
import com.gitusers.interfaces.ItemClickListener
import com.gitusers.model.UserModel
import com.gitusers.util.Utility

class UserAdapter(var mListener: ItemClickListener<UserModel>, val isClickable: Boolean) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var mList = mutableListOf<UserModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder = UserViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_user, parent, false
        )
    )


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val model = mList.get(holder.adapterPosition)
        val context: Context = holder.mBinding.root.context

        holder.mBinding.checkbox.visibility =
                if (isClickable) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

        holder.mBinding.checkbox.isChecked = model.isSeleted
        Utility.loadCircularImage(
            context,
            holder.mBinding.ivUser,
            model.avatarUrl,
            R.drawable.ic_placeholder_user
        )
        holder.mBinding.tvUserName.text = model.login
    }

    override fun getItemCount(): Int = mList.size


    inner class UserViewHolder(val mBinding: RowUserBinding) : RecyclerView.ViewHolder(mBinding.root) {
        private val mOnClickListener: View.OnClickListener

        init {
            mOnClickListener = object : View.OnClickListener {
                override fun onClick(view: View?) {
                    mList.get(adapterPosition).isSeleted = !mList.get(adapterPosition).isSeleted
                    mListener.onItemClick(adapterPosition, mList.get(adapterPosition))
                    notifyItemChanged(adapterPosition)
                }
            }

            if (isClickable) {
                mBinding.llMain.setOnClickListener(mOnClickListener)
                mBinding.checkbox.setOnClickListener(mOnClickListener)
            }
        }
    }

    fun addAll(list: ArrayList<UserModel>) {
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun getSelected(): ArrayList<UserModel> {
        val list: ArrayList<UserModel> = arrayListOf<UserModel>()

        mList.iterator().forEach { if (it.isSeleted) list.add(it) }

        return list
    }
}