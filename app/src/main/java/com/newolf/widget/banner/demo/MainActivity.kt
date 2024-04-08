package com.newolf.widget.banner.demo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.newolf.widget.banner.demo.adapter.ListAdapter
import com.newolf.widget.banner.demo.databinding.ActivityMainBinding
import com.newolf.widget.banner.demo.vm.HomeViewModel

class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy {ActivityMainBinding.inflate(layoutInflater)  }

    private val listAdapter: ListAdapter by lazy { ListAdapter() }

    private val viewModel:HomeViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory(application).create(HomeViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        initView()
        initData()
        initListener()
    }

    private fun initView() {
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = listAdapter
    }

    private fun initData() {
        viewModel.listData.observe(this, Observer {
            listAdapter.setNewInstance(it)
        })

        viewModel.composeData()
    }

    private fun initListener() {


    }


}