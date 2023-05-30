package com.example.collegecommapp.interfaces

import com.example.collegecommapp.models.Group

interface Generalinterface {

    fun logOut()
    fun addChatRoom(group: Group)
    fun goToChatPage(groupId: String)
    fun getPhoneDetails(userPhone: String): String?
    fun goToNewChatRooms()
    fun goToSearch()
    fun goToMainPage()

}