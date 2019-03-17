package com.gitusers.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gitusers.R
import com.gitusers.adapters.UserAdapter
import com.gitusers.databinding.ActivitySelectedUsersBinding
import com.gitusers.interfaces.ItemClickListener
import com.gitusers.model.UserModel
import com.gitusers.util.Utility

class SelectedUsersActivity : AppCompatActivity(), ItemClickListener<UserModel> {

    private lateinit var mBinding: ActivitySelectedUsersBinding

    private lateinit var mContext: Context
    private lateinit var mAdapter: UserAdapter

    companion object {
        fun newInstance(context: Context, selectedList: ArrayList<UserModel>) {
            var intent = Intent(context, SelectedUsersActivity::class.java)
            intent.putExtra(Utility.EXTRA.SELECTED_USERS, selectedList)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_selected_users)

        mContext = this

        mAdapter = UserAdapter(this, false)

        mBinding.recyclerView.adapter = mAdapter
        mAdapter.addAll(intent.getSerializableExtra(Utility.EXTRA.SELECTED_USERS) as ArrayList<UserModel>)

    }

    override fun onItemClick(position: Int, model: UserModel) {

    }
}
