package com.gitusers.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gitusers.R
import com.gitusers.adapters.UserAdapter
import com.gitusers.databinding.ActivityMainBinding
import com.gitusers.interfaces.ItemClickListener
import com.gitusers.model.UserModel
import com.gitusers.network.WebCallClass
import com.gitusers.util.Utility
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), WebCallClass.CommonResponseListener, WebCallClass.UsersResponseListener,
    ItemClickListener<UserModel>, View.OnClickListener {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mContext: Context
    private lateinit var mAdapter: UserAdapter
    private var nextId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mContext = this

        mAdapter = UserAdapter(this, true)

        mBinding.recyclerView.adapter = mAdapter

        getGitUsers()

        mBinding.fab.setOnClickListener(this)

        mBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    getGitUsers()
                }
            }
        })
    }

    private fun getGitUsers() {
        if (!Utility.isConnected(mContext)) {
            return
        }

        mBinding.flProgressBar.visibility = View.VISIBLE
        WebCallClass.getInstance().callGetGitUsers(
            mContext,
            nextId,
            this,
            this
        )
    }

    override fun volleyError() {
        mBinding.flProgressBar.visibility = View.GONE
        Snackbar.make(mBinding.root, getString(R.string.msg_error), Snackbar.LENGTH_LONG)
    }

    override fun usersSuccessResponse(json: String?) {
        mBinding.flProgressBar.visibility = View.GONE
        var list: ArrayList<UserModel> = Utility.getObjectListFromJsonString(json, Array<UserModel>::class.java)
        nextId = list.get(list.size - 1).id + 1
        mAdapter.addAll(list)
    }

    override fun onItemClick(position: Int, model: UserModel) {
        if (mAdapter.getSelected().isEmpty()) {
            mBinding.fab.animate().scaleY(0f)
            mBinding.fab.animate().scaleX(0f)
        } else {
            mBinding.fab.animate().scaleY(1f)
            mBinding.fab.animate().scaleX(1f)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> {
                if (mBinding.fab.scaleX != 0f)
                    SelectedUsersActivity.newInstance(mContext, mAdapter.getSelected())
            }
        }
    }
}
