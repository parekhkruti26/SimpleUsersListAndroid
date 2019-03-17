package com.gitusers.interfaces

interface ItemClickListener<T> {
    fun onItemClick(position: Int, model: T)
}