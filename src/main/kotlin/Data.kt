package cn.mpsmc

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object Data : AutoSavePluginData("data") {
    val historicalMessages by value<MutableMap<String, MutableList<String>>>()
    val lastChatTime by value<MutableMap<String, Long>>()
}