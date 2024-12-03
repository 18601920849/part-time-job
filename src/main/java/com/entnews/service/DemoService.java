package com.entnews.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entnews.dao.DemoDao;
import com.entnews.entity.Demo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoService extends ServiceImpl<DemoDao, Demo> {

    public List<Demo> getDemo(){

        return this.list();
    }
}
