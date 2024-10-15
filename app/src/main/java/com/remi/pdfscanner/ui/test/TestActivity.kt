package com.remi.pdfscanner.ui.test

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.remi.pdfscanner.base.BaseActivity
import com.remi.pdfscanner.databinding.ActivityTestBinding
import com.remi.pdfscanner.util.Common
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class TestActivity : BaseActivity<ActivityTestBinding>(ActivityTestBinding::inflate) {
    val TAG = "~~~"
    override fun setSize() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.ratingbar.apply {
            numberOfStars = 5
            rating = 0f
            stepSize = 0.1f
            starSize = 11.667f * Common.screenWidth / 100f
            starCornerRadius = .5f * Common.screenWidth / 100f
            fillColor = Color.parseColor("#FFB90B")
            pressedStarBackgroundColor = Color.parseColor("#D7D7D7")
            pressedFillColor = Color.parseColor("#FFB90B")
            starBackgroundColor = Color.parseColor("#D7D7D7")

        }

    }

    class ListNode(var hehe: Int) {
        var next: ListNode? = null
    }
}

//fun main() {
//    printArray1(55)
//    printArray2(100)
//    printArray3(100)
//}

fun printArray1(input: Int) {
    var count = 0
    var start = 0
    for (i in 0..input / 2) {
        start += i
        if (start > input)
            break
        count++
    }
    var currentRow = 1
    var rowCount = 0
    for (i in 1..input) {
        print("$i ")
        rowCount++
        if (rowCount == currentRow) {
            println()
            rowCount = 0
            currentRow++
        }
    }
}

fun printArray2(input: Int) {
    var resultPair = Pair(1, 1)
    for (i in 3..input / 2 step 2) {
        var count = resultPair.second + resultPair.first * 2 + 2
        if (count > input)
            break
        resultPair = Pair(i, count)
    }
    var currentRow = 0
    var rowCount = 0
    val list: MutableList<Int> = ArrayList()
    list.add(resultPair.first)
    for (i in resultPair.first - 2 downTo 1 step 2) {
        list.add(i)
        list.add(0, i)
    }
    println(list)

    for (j in 1..(resultPair.first / 2))
        print("    ")
    for (i in 1..input) {
        print(" ${handleText(i)} ")
        rowCount++

        if (rowCount >= list[currentRow]) {
            println()
            rowCount = 0
            currentRow++
            if (currentRow >= list.size)
                break
            if (currentRow < resultPair.first / 2) {
                for (j in 1..(resultPair.first / 2 - currentRow))
                    print("    ")
            } else if (currentRow > resultPair.first / 2) {
                for (j in 1..(currentRow - resultPair.first / 2))
                    print("    ")
            }


        }
    }

}

fun handleText(inp: Int): String {
    if (inp < 10) return "$inp "
    return inp.toString()
}

fun printArray3(input: Int) {
    var resultPair = Pair(1, 1)
    for (i in 2..input / 2 step 2) {
        var count = resultPair.second + resultPair.first * 2 + 2
        if (count > input)
            break
        resultPair = Pair(i, count)
    }
    var currentRow = 0
    var rowCount = 0
    val list: MutableList<Int> = ArrayList()
    list.add(resultPair.first)
    for (i in resultPair.first - 2 downTo 1 step 2) {
        list.add(i)
        list.add(0, i)
    }
    println(list)

    print("row 0: ")
    for (i in 1..input) {
        print(" ${handleText(i)} ")
        rowCount++

        if (  rowCount==list[currentRow]/2) {
            for (j in 0..(resultPair.first  - rowCount*2))
                print("    ")
        }
//        if (currentRow > resultPair.first / 2  && rowCount==currentRow+1) {
//            for (j in 0..(currentRow - resultPair.first / 2))
//                print("    ")
//        }

        if (rowCount >= list[currentRow]) {
            println()
            print("row ${currentRow + 1}: ")
            rowCount = 0
            currentRow++
            if (currentRow >= list.size)
                break
        }
    }
}