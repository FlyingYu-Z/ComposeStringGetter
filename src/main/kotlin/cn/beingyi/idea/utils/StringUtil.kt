package cn.beingyi.idea.utils

/**
 * author: zhengyu
 * date: 2021/8/22 12:22
 *
 */

fun String.upperCaseFirst(): String {
    val arr = this.toCharArray()
    arr[0] = Character.toUpperCase(arr[0])
    return String(arr)
}