package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

public interface PreferenceService {

    ResultVO<Object> updatePreference(long id, String workingDay, String workingHours, Integer durationOfShift, Integer durationOfWeek);

    ResultVO<Object> getPreference(long id);

}
