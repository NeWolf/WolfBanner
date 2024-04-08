package com.newolf.widget.banner.demo.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.newolf.widget.banner.demo.data.ListItem

/**
 * ======================================================================
 *
 *
 * @author : NeWolf
 * @version : 1.0
 * @since :  2024-04-03
 *
 * =======================================================================
 */
class HomeViewModel : ViewModel() {
    val listData: MutableLiveData<MutableList<ListItem>> by lazy { MutableLiveData(mutableListOf()) }


    fun composeData() {
        val list: MutableList<ListItem> = mutableListOf()
        val bannerList = mutableListOf<String>(
            "https://img.zcool.cn/community/01bc8d5ef9b9b3a801215aa0d1101d.jpg?x-oss-process=image/format,webp",
            "https://img.zcool.cn/community/01fbbd5ef9b9b3a801206621311a8f.jpg?x-oss-process=image/format,webp",
            "https://img.zcool.cn/community/01a4b55ef9b9b3a80120662181a1e5.jpg?x-oss-process=image/format,webp",
            "https://img.zcool.cn/community/0181b85ef9b9b3a801215aa074a7bb.jpg?x-oss-process=image/format,webp",
            "https://img.zcool.cn/community/01d4365ef9b9b4a801215aa064abea.jpg?x-oss-process=image/format,webp",
            "https://img.zcool.cn/community/0183bc5ef9b9b4a801215aa0fe0fa1.jpg?x-oss-process=image/format,webp",
            "https://img.zcool.cn/community/01ab865ef9b9b4a80120662137a9c0.jpg?x-oss-process=image/format,webp",
        )
        list.add(ListItem(ListItem.ITEM_TYPE_BANNER, bannerList = bannerList))
        list.add(
            ListItem(
                ListItem.ITEM_TYPE_IMG,
                imgUrl = "https://img1.baidu.com/it/u=1170465163,1129050671&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=750"
            )
        )

        list.add(
            ListItem(
                ListItem.ITEM_TYPE_IMG,
                imgUrl = "https://wx4.sinaimg.cn/mw690/005Sy7DBgy1hnhgqgt7jcj31jk2bc1ky.jpg"
            )
        )
        list.add(
            ListItem(
                ListItem.ITEM_TYPE_IMG,
                imgUrl = "https://wx1.sinaimg.cn/mw690/006XioFOly1hnjf673y6nj31sc2dsb2a.jpg"
            )
        )
        list.add(
            ListItem(
                ListItem.ITEM_TYPE_IMG,
                imgUrl = "https://img.idol001.com/origin/2015/01/19/3a00b7acd87edbe65f3cd292997821201421652741.jpg"
            )
        )



        listData.postValue(list)
    }
}