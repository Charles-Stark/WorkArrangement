package com.example.backend;

import com.example.backend.POJO.Flow;
import com.example.backend.POJO.Rule;
import com.example.backend.POJO.Schedule;
import com.example.backend.Service.*;
import com.example.backend.Service.Impl.RuleServiceImpl;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.FlowMapper;
import com.example.backend.mapper.ScheduleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class ArrangerTest {
    @Autowired
    Arranger arranger;
    @Autowired
    RuleServiceImpl ruleService;
    @Test
    void test(){
       // ruleService.addRule();
    }
}
