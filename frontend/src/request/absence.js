//导入request
import request from './request'
import store from '../store/index'
//存放api


//员工申请请假
// employee: long 员工id
// reason: String 请假原因（不超过250个字符）
// absenceDate: Date 请假日期，格式：yyyy-MM-dd HH:mm:ss 例：2023-03-17 00:00:00
// attachmentPhoto: 图片 图片附件，限jpg和png，不超过12MB（可选）
export const applyAbsence = data => {
    return request({
        method: 'post',
        url: `api/absence/create`,
        data,
        headers: { 'Content-Type': 'multipart/form-data' },
    })
}

//按管理员获取请假列表
export const getAbsenceList = id => {
    return request({
        method: 'get',
        url: `api/absence/get/list/${id || store.state.userId}`,
    })
}
