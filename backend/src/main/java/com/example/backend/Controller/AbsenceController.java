package com.example.backend.Controller;

import com.example.backend.POJO.Absence;
import com.example.backend.Service.AbsenceService;
import com.example.backend.Utils.MagicNumberUtil;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.AbsenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/absence")
public class AbsenceController {

    private static final long MAX_PHOTO_SIZE = 12 * 1024 * 1024;  // 图片附件大小限制 12MB

    private static final Map<String, String> ALLOWED_PHOTO_TYPES = new HashMap<>();
    static {
        ALLOWED_PHOTO_TYPES.put("FFD8FF", "image/jpeg");
        ALLOWED_PHOTO_TYPES.put("89504E", "image/png");
    }

    @Autowired
    private AbsenceService absenceService;

    @Autowired
    private AbsenceMapper absenceMapper;

    @PostMapping("/create")
    public ResultVO<Object> createAbsence(@RequestParam("employee") long employee,
                                          @RequestParam("reason") String reason,
                                          @RequestParam("absenceDate") Date absenceDate,
                                          @RequestParam(value = "attachmentPhoto", required = false) MultipartFile attachmentPhoto) throws IOException {
        if (attachmentPhoto != null) {
            if (attachmentPhoto.getSize() > MAX_PHOTO_SIZE) {
                return new ResultVO<>(-1, "图片过大", null);
            }
            String magicNumber = MagicNumberUtil.getMagicNumber(attachmentPhoto.getBytes(), 6);
            if (!ALLOWED_PHOTO_TYPES.containsKey(magicNumber)) {
                return new ResultVO<>(-1, "图片类型不符", null);
            }
            return absenceService.createAbsence(employee, reason, absenceDate, attachmentPhoto.getBytes(), ALLOWED_PHOTO_TYPES.get(magicNumber));
        }
        return absenceService.createAbsence(employee, reason, absenceDate, null, null);
    }

    @GetMapping("/get/{id}")
    public ResultVO<Object> getAbsence(@PathVariable long id) {
        return absenceService.getAbsence(id);
    }

    @GetMapping("/get/list/{id}")
    public ResultVO<Object> getAbsenceListByManager(@PathVariable long id) {
        return absenceService.getAbsenceListByManager(id);
    }

    @GetMapping("/get/photo/{id}")
    public void getAbsencePhoto(@PathVariable long id, HttpServletResponse response) {
        try {
            Absence absence = absenceMapper.selectById(id);
            InputStream inputStream = new ByteArrayInputStream(absence.getAttachmentPhoto());
            response.setContentType(absence.getPhotoType());

            ServletOutputStream outputStream = response.getOutputStream();
            int length;
            byte[] buffer = new byte[1024];
            while ((length = inputStream.read(buffer, 0, 1024)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
    }

    @PostMapping("/approve")
    public ResultVO<Object> approveAbsenceOrNot(@RequestParam("id") long id,
                                                @RequestParam("approve") boolean approve) {
        return absenceService.approveAbsenceOrNot(id, approve);
    }

}
