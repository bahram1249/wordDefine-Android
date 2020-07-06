package com.example.worddefine

import android.content.Context
import android.content.SharedPreferences

class Token{
    companion object{
        private fun getSharedPreferences(context: Context): SharedPreferences{
            return context.getSharedPreferences(
                context.getString(R.string.auth_pref), Context.MODE_PRIVATE
            )
        }

        fun set(context: Context, token: String){
            with(getSharedPreferences(context).edit()){
                putString(context.getString(R.string.token), token)
                commit()
            }
        }

        fun setUserId(context: Context, userId: String){
            with(getSharedPreferences(context).edit()){
                putString("userId", userId)
                commit()
            }
        }

        fun get(context: Context): String?{
            return getSharedPreferences(context)
                .getString(context.getString(R.string.token), null)
        }

        fun getUserId(context: Context): String?{
            return getSharedPreferences(context)
                .getString("userId", null)
        }
    }
}