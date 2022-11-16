package com.imgSys.service.impl;

import com.common.entity.PageData;

import com.imgSys.service.GraphingFormService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;

import java.util.List;

@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
@Slf4j
@Service
public class GraphingFormServiceImpl implements GraphingFormService {
    @Override
    public List<PageData> queryGraphingFrom(PageData pd) {
        return null;
    }
}
