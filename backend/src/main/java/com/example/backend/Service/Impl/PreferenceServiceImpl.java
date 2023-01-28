package com.example.backend.Service.Impl;

import com.example.backend.POJO.Preference;
import com.example.backend.Service.PreferenceService;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.PreferenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PreferenceServiceImpl implements PreferenceService {

    @Autowired
    private PreferenceMapper preferenceMapper;

    @Override
    public ResultVO<Object> updatePreference(long id, String workingDay, String workingHours, Integer durationOfShift, Integer durationOfWeek) {
        Preference preference = new Preference(id, workingDay, workingHours, durationOfShift, durationOfWeek);
        try {
            preferenceMapper.updateById(preference);
        } catch (Exception e) {
            return new ResultVO<>(-1, "更新偏好失败", null);
        }
        return new ResultVO<>(0, "更新偏好成功", preferenceMapper.selectById(id));
    }

    @Override
    public ResultVO<Object> getPreference(long id) {
        try {
            return new ResultVO<>(0, "获取偏好成功", preferenceMapper.selectById(id));
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取偏好失败", null);
        }
    }
}
