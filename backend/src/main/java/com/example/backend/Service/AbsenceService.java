package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

import java.util.Date;

public interface AbsenceService {

    ResultVO<Object> createAbsence(long employee, String reason, Date absenceDate, byte[] attachmentPhoto, String photoType);

    ResultVO<Object> getAbsence(long id);

    ResultVO<Object> getAbsenceListByManager(long id);

    ResultVO<Object> getAbsenceListByShop(long id);

    ResultVO<Object> approveAbsenceOrNot(long id, boolean isApproved);

}
